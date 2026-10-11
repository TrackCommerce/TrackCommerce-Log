package school.sptech.service;

// Jackson (jackson-databind): ObjectMapper lê/grava o alertas.json; DeserializationFeature permite ignorar chaves desconhecidas; JsonNode é a árvore do JSON Gold.
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// java.io / java.nio.file: IOException (erro de leitura/gravação); Files/Path/StandardCopyOption manipulam o arquivo em disco.
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// java.time: LocalDateTime = data e hora; DateTimeFormatter define o formato do texto; DateTimeParseException é lançada quando o texto não está nesse formato.
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// java.util: coleções em memória (LinkedHashMap mantém a ordem de inserção).
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Projeto: DTOs do alertas.json e modelos de domínio.
import school.sptech.dto.AlertaItemDto;
import school.sptech.dto.MaquinaDto;
import school.sptech.dto.RaizDto;
import school.sptech.model.Evento;
import school.sptech.model.Fonte;
import school.sptech.model.Intervalo;
import school.sptech.model.Nivel;
import school.sptech.model.Ponto;
import school.sptech.model.Regra;
import school.sptech.model.Resultado;

/**
 * Gera e mantém o alertas.json a partir do Gold do ETL e dos limites das regras.
 *
 * Fluxo:
 *  - alertas.json NÃO existe: lê TODO o histórico de cada componente, aplica os limites e grava
 *    os alertas (com DataFimAlerta quando o componente já voltou ao normal).
 *  - alertas.json existe: para cada máquina com alertasSemFim > 0, procura o ponto em que o
 *    alerta terminou e preenche DataFimAlerta. Máquinas sem alerta aberto pulam essa etapa e só
 *    têm os pontos novos (posteriores à última verificação) examinados em busca de alertas novos.
 *
 * A lógica é a mesma da versão anterior; mudou apenas a forma de escrever (laços for/if em vez de streams).
 */
public class AlertaStore {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Formato das datas de início/fim dos alertas.
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Formato da data de última atualização do arquivo.
    private static final DateTimeFormatter DATA_ARQUIVO = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");

    // Caminho absoluto do alertas.json.
    private final Path arquivo;

    // Lê e grava o alertas.json (ignora chaves desconhecidas).
    private final ObjectMapper mapper;

    // Verdadeiro quando o alertas.json já existia (e não estava vazio) ao iniciar.
    private final Boolean jsonExistia;

    // Conteúdo do alertas.json em memória.
    private final RaizDto raiz;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Abre o alertas.json se ele existir; caso contrário começa com uma estrutura vazia. */
    public AlertaStore(String caminho) throws IOException {
        this.arquivo = Path.of(caminho).toAbsolutePath();
        this.mapper = new ObjectMapper();
        // Ignora chaves que não conhecemos: o arquivo pode ter sido editado por outra ferramenta.
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.jsonExistia = Files.exists(arquivo) && Files.size(arquivo) > 0;
        if (jsonExistia) {
            this.raiz = mapper.readValue(arquivo.toFile(), RaizDto.class);
        } else {
            this.raiz = new RaizDto();
        }
    }

    // ==================================================================
    // MÉTODOS
    // ==================================================================

    // ------------------------------------------------------------------------------------
    // Fluxo principal
    // ------------------------------------------------------------------------------------

    /** Atualiza o alertas.json com base nas regras e no Gold; devolve o que abriu e o que encerrou. */
    public Resultado atualizar(List<Regra> regras, GoldReader goldReader) throws IOException {
        Resultado resultado = new Resultado();
        LocalDateTime ultimaData = lerData(raiz.getAlertasDias().getData()); // null na primeira execução

        // Agrupa as regras por máquina (empresa + identificador), mantendo a ordem em que chegaram.
        Map<String, List<Regra>> porMaquina = new LinkedHashMap<>();
        for (Regra regra : regras) {
            String chave = regra.getEmpresaId() + "|" + regra.getIdentificador();
            List<Regra> grupo = porMaquina.get(chave);
            if (grupo == null) {
                grupo = new ArrayList<>();
                porMaquina.put(chave, grupo);
            }
            grupo.add(regra);
        }

        for (List<Regra> grupo : porMaquina.values()) {
            Regra base = grupo.get(0);

            JsonNode gold;
            try {
                gold = goldReader.ler(base.getEmpresaId(), base.getIdentificador());
            } catch (IOException e) {
                // IOException: o Gold da máquina não existe (ETL ainda não rodou para ela) ou está ilegível.
                // Esta máquina é pulada neste ciclo, as demais continuam normalmente.
                System.err.println("[AVISO] Sem gold para " + base.getInstancia() + " (" + base.getIdentificador()
                        + "): " + e.getMessage());
                continue;
            }

            MaquinaDto maquina = buscarMaquina(base.getIdentificador());
            Boolean nova = (maquina == null);
            if (nova) {
                maquina = new MaquinaDto();
                maquina.setUuid(base.getIdentificador());
                raiz.getAlertasDias().getMaquinas().add(maquina);
            }
            maquina.setEmpresa(base.getEmpresaNome());

            // Máquina nova: lê todo o histórico (corte nulo). Máquina antiga: só o que veio depois da última data.
            LocalDateTime corteBase = ultimaData;
            if (nova) {
                corteBase = null;
            }

            for (Regra regra : grupo) {
                Fonte fonte = FonteResolver.para(regra.getComponente());
                if (fonte == null) {
                    continue;
                }
                processarComponente(maquina, regra, goldReader.serie(gold, fonte), corteBase, resultado);
            }
        }

        atualizarContadores();
        salvar();
        return resultado;
    }

    /** Fecha o alerta aberto do componente (se houver) e procura alertas novos na série. */
    private void processarComponente(MaquinaDto maquina, Regra regra, List<Ponto> serie,
                                     LocalDateTime corteBase, Resultado resultado) {
        AlertaItemDto aberto = alertaAbertoDe(maquina, regra.getComponente());
        List<Ponto> pontos = new ArrayList<>();

        if (aberto != null) {
            // Etapa de fechamento: procura o primeiro ponto, depois do início, em que o nível mudou.
            LocalDateTime inicio = lerTimestamp(aberto.getDataInicioAlerta());
            Nivel nivelAberto = nivelDe(aberto.getTipoAlerta());
            if (inicio == null || nivelAberto == null) {
                System.err.println("[AVISO] Alerta aberto com dados inválidos em " + regra.getComponente()
                        + " (" + regra.getInstancia() + "): ignorado neste ciclo.");
                return;
            }

            Ponto fechamento = null;
            Nivel nivelNoFechamento = null;
            for (Ponto ponto : serie) {
                if (!ponto.getTs().isAfter(inicio)) {
                    continue; // só interessam os pontos posteriores ao início do alerta
                }
                Nivel nivel = Avaliador.avaliar(ponto.getValor(), regra.getModerado(), regra.getCritico());
                if (nivel != nivelAberto) {
                    fechamento = ponto;
                    nivelNoFechamento = nivel;
                    break;
                }
            }

            if (fechamento == null) {
                return; // continua aberto: nada a fazer
            }
            aberto.setDataFimAlerta(fechamento.getTs().format(TS));
            if (nivelNoFechamento == Nivel.NORMAL) {
                resultado.getEncerrados().add(new Evento(regra, nivelAberto, 0.0));
            }

            // Depois do fim ainda pode começar um alerta novo (ex.: moderado -> crítico).
            LocalDateTime aPartirDe = fechamento.getTs();
            for (Ponto ponto : serie) {
                if (!ponto.getTs().isBefore(aPartirDe)) {
                    pontos.add(ponto);
                }
            }

        } else if (corteBase != null) {
            // Sem alerta aberto: não há o que fechar, só examina o que é novo.
            LocalDateTime corte = maiorFim(maquina, corteBase);
            for (Ponto ponto : serie) {
                if (ponto.getTs().isAfter(corte)) {
                    pontos.add(ponto);
                }
            }

        } else {
            pontos = serie; // primeira leitura da máquina: histórico completo
        }

        List<Intervalo> intervalos = segmentar(pontos, regra);
        for (Intervalo intervalo : intervalos) {
            String inicio = intervalo.getInicio().format(TS);
            if (buscarAlerta(maquina, regra.getComponente(), intervalo.getNivel(), inicio) != null) {
                continue; // já registrado
            }

            AlertaItemDto item = new AlertaItemDto();
            item.setTipoAlerta(intervalo.getNivel().name());
            item.setUnidadeGravada(regra.getUnidade());
            item.setComponenteEmAlerta(regra.getComponente());
            item.setDataInicioAlerta(inicio);
            if (intervalo.getFim() == null) {
                item.setDataFimAlerta(null);
            } else {
                item.setDataFimAlerta(intervalo.getFim().format(TS));
            }
            maquina.getAlertas().add(item);

            if (intervalo.getFim() == null) {
                resultado.getAbertos().add(new Evento(regra, intervalo.getNivel(), intervalo.getPico()));
            }
        }
    }

    /** Divide a série em trechos contínuos no mesmo nível (moderado ou crítico). */
    private List<Intervalo> segmentar(List<Ponto> pontos, Regra regra) {
        List<Intervalo> resultado = new ArrayList<>();
        Intervalo atual = null;

        for (Ponto ponto : pontos) {
            Nivel nivel = Avaliador.avaliar(ponto.getValor(), regra.getModerado(), regra.getCritico());

            if (atual != null && atual.getNivel() != nivel) { // o nível mudou: o trecho atual termina aqui
                atual.setFim(ponto.getTs());
                resultado.add(atual);
                atual = null;
            }
            if (nivel != Nivel.NORMAL) {
                if (atual == null) {
                    atual = new Intervalo(nivel, ponto.getTs(), ponto.getValor());
                } else if (ponto.getValor() > atual.getPico()) {
                    atual.setPico(ponto.getValor()); // guarda o maior valor visto (pico)
                }
            }
        }
        if (atual != null) {
            resultado.add(atual); // ainda aberto
        }
        return resultado;
    }

    // ------------------------------------------------------------------------------------
    // Busca e contadores
    // ------------------------------------------------------------------------------------

    private MaquinaDto buscarMaquina(String uuid) {
        for (MaquinaDto maquina : raiz.getAlertasDias().getMaquinas()) {
            if (uuid.equals(maquina.getUuid())) {
                return maquina;
            }
        }
        return null;
    }

    /** Alerta do componente que ainda não tem data de fim. */
    private AlertaItemDto alertaAbertoDe(MaquinaDto maquina, String componente) {
        for (AlertaItemDto alerta : maquina.getAlertas()) {
            if (alerta.getDataFimAlerta() == null && componente.equals(alerta.getComponenteEmAlerta())) {
                return alerta;
            }
        }
        return null;
    }

    /** Procura um alerta já registrado com o mesmo componente, nível e início (evita duplicar). */
    private AlertaItemDto buscarAlerta(MaquinaDto maquina, String componente, Nivel nivel, String inicio) {
        for (AlertaItemDto alerta : maquina.getAlertas()) {
            if (componente.equals(alerta.getComponenteEmAlerta())
                    && nivel.name().equals(alerta.getTipoAlerta())
                    && inicio.equals(alerta.getDataInicioAlerta())) {
                return alerta;
            }
        }
        return null;
    }

    /** Maior entre a base e o fim dos alertas já encerrados da máquina (tudo até aí já foi lido). */
    private LocalDateTime maiorFim(MaquinaDto maquina, LocalDateTime base) {
        LocalDateTime maior = base;
        for (AlertaItemDto alerta : maquina.getAlertas()) {
            if (alerta.getDataFimAlerta() == null) {
                continue;
            }
            LocalDateTime fim = lerTimestamp(alerta.getDataFimAlerta());
            if (fim != null && fim.isAfter(maior)) {
                maior = fim;
            }
        }
        return maior;
    }

    /** Recalcula quantos alertas estão sem fim por máquina e quantas máquinas têm alerta aberto. */
    private void atualizarContadores() {
        Integer comAbertos = 0;
        for (MaquinaDto maquina : raiz.getAlertasDias().getMaquinas()) {
            Integer semFim = 0;
            for (AlertaItemDto alerta : maquina.getAlertas()) {
                if (alerta.getDataFimAlerta() == null) {
                    semFim++;
                }
            }
            maquina.setAlertasSemFim(semFim);
            if (semFim > 0) {
                comAbertos++;
            }
        }
        raiz.getAlertasDias().setMaquinasComAlertasSemFim(comAbertos);
        raiz.getAlertasDias().setData(LocalDateTime.now().format(DATA_ARQUIVO));
    }

    // ------------------------------------------------------------------------------------
    // Conversões e gravação
    // ------------------------------------------------------------------------------------

    /** Converte o texto "yyyy-MM-dd_HH-mm" em data; devolve null se for vazio ou inválido. */
    private LocalDateTime lerData(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(texto, DATA_ARQUIVO);
        } catch (DateTimeParseException e) {
            // DateTimeParseException: o texto não está no formato yyyy-MM-dd_HH-mm (arquivo editado à mão,
            // por exemplo). Tratamos como "sem data" e o histórico será lido por completo.
            return null;
        }
    }

    /** Converte o texto "yyyy-MM-dd HH:mm:ss" em data; devolve null se for vazio ou inválido. */
    private LocalDateTime lerTimestamp(String texto) {
        if (texto == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(texto, TS);
        } catch (DateTimeParseException e) {
            // DateTimeParseException: o texto não está no formato yyyy-MM-dd HH:mm:ss.
            return null;
        }
    }

    /** Converte o texto do alertas.json em Nivel com if/else (devolve null se não reconhecer o valor). */
    private Nivel nivelDe(String texto) {
        if ("CRITICO".equals(texto)) {
            return Nivel.CRITICO;
        }
        if ("MODERADO".equals(texto)) {
            return Nivel.MODERADO;
        }
        if ("NORMAL".equals(texto)) {
            return Nivel.NORMAL;
        }
        return null;
    }

    /** Grava em arquivo temporário e troca, para não corromper o JSON se o processo cair no meio. */
    private void salvar() throws IOException {
        if (arquivo.getParent() != null) {
            Files.createDirectories(arquivo.getParent());
        }
        Path temporario = arquivo.resolveSibling(arquivo.getFileName() + ".tmp");
        mapper.writerWithDefaultPrettyPrinter().writeValue(temporario.toFile(), raiz);
        Files.move(temporario, arquivo, StandardCopyOption.REPLACE_EXISTING);
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public Path getArquivo() {
        return arquivo;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public Boolean getJsonExistia() {
        return jsonExistia;
    }

    public RaizDto getRaiz() {
        return raiz;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "AlertaStore{arquivo=" + arquivo
                + ", mapper=" + mapper
                + ", jsonExistia=" + jsonExistia + "}";
    }

}
