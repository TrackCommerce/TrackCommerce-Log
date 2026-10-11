package school.sptech.service;

// Jackson (jackson-databind): JsonNode é a árvore do JSON; ObjectMapper lê o arquivo Gold.
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// AWS SDK v2: SdkException é a exceção-base de qualquer falha do SDK (sem credenciais, sem rede, objeto inexistente...); S3Client lê o objeto; GetObjectRequest descreve qual objeto baixar.
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

// java.io / java.nio.file: File lista pastas/arquivos locais; Files/Path leem o conteúdo; IOException é a exceção de I/O.
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// java.time: LocalDateTime = data e hora; DateTimeFormatter define o formato do texto; DateTimeParseException é lançada quando o texto não está nesse formato.
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

// java.util: ArrayList/List guardam coleções de objetos em memória.
import java.util.ArrayList;
import java.util.List;

// Projeto: cliente S3, configuração e modelos.
import school.sptech.cliente.S3Provider;
import school.sptech.config.Config;
import school.sptech.model.Fonte;
import school.sptech.model.MaquinaGold;
import school.sptech.model.Ponto;

/**
 * Lê o JSON Gold gerado pelo ETL (S3 ou pasta local) e extrai a série histórica de uma métrica.
 *
 * Ordem de leitura: se o S3 estiver ativo, tenta o bucket primeiro; se falhar (ou se estiver
 * desativado), lê da pasta local (GOLD_DIR_LOCAL). Assim o programa roda sem o S3.
 *
 * O Gold é lido como árvore JSON (JsonNode) e não como DTO porque o formato é grande e dinâmico:
 * só precisamos de alguns campos de "historico" de cada seção (cpu, ram, disco, rede).
 */
public class GoldReader {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Formato do campo "timestamp" do Gold (o mesmo FORMATO_TIMESTAMP do ETL).
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Configuração (bucket, prefixo e pasta local do Gold).
    private final Config config;

    // Cliente S3; fica null quando o S3 está desativado.
    private final S3Client s3Client;

    // Converte os bytes do arquivo em JsonNode.
    private final ObjectMapper objectMapper;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Só cria o cliente S3 quando o S3 está ativo (bucket informado e modo offline desligado). */
    public GoldReader(Config config) {
        this.config = config;
        this.objectMapper = new ObjectMapper();

        if (config.s3Ativo()) {
            this.s3Client = new S3Provider(config).getS3Client();
        } else {
            this.s3Client = null;
            System.out.println("[INFO] S3 desativado: o Gold será lido da pasta local " + config.getGoldDirLocal());
        }
    }

    // ==================================================================
    // MÉTODOS
    // ==================================================================

    /** Mesma normalização do ETL: minúsculas e sem "-" nem "_" (feita caractere a caractere). */
    public static String normalizar(String id) {
        if (id == null) {
            return "";
        }
        StringBuilder resultado = new StringBuilder();
        for (Integer i = 0; i < id.length(); i++) {
            Character c = id.charAt(i);
            if (c == '-' || c == '_') {
                continue; // descarta hífen e sublinhado
            }
            if (c >= 'A' && c <= 'Z') {
                c = (char) (c + 32); // letra maiúscula ASCII -> minúscula (diferença de 32 na tabela ASCII)
            } else if (c > 127) {
                c = Character.toLowerCase(c); // letras fora do ASCII (acentos): a conta de +32 não vale
            }
            resultado.append(c);
        }
        return resultado.toString();
    }

    /** Mesma regra do nome_seguro() do ETL: só letras, números, "-" e "_"; o resto vira "_" (máx. 120 caracteres). */
    public static String nomeSeguro(String texto) {
        StringBuilder resultado = new StringBuilder();
        for (Integer i = 0; i < texto.length(); i++) {
            if (resultado.length() >= 120) {
                break; // o ETL corta o nome em 120 caracteres
            }
            Character c = texto.charAt(i);
            // Character.isLetterOrDigit é usado porque o ETL (isalnum do Python) também aceita letras acentuadas.
            if (Character.isLetterOrDigit(c) || c == '-' || c == '_') {
                resultado.append(c);
            } else {
                resultado.append('_');
            }
        }
        return resultado.toString();
    }

    /** Lê o Gold de uma máquina: tenta o S3 (se ativo) e, se não der, a pasta local. */
    public JsonNode ler(String empresaId, String identificador) throws IOException {
        String pasta = nomeSeguro(empresaId);
        String arquivo = nomeSeguro(normalizar(identificador)) + "_sistema.json";

        if (s3Client != null) {
            String chave = config.getGoldPrefix() + "/" + pasta + "/" + arquivo;
            try {
                GetObjectRequest requisicao = GetObjectRequest.builder()
                        .bucket(config.getAwsBucket())
                        .key(chave)
                        .build();
                byte[] bytes = s3Client.getObjectAsBytes(requisicao).asByteArray();
                return objectMapper.readTree(bytes);
            } catch (SdkException e) {
                // SdkException: erro do SDK da AWS - credenciais ausentes, bucket sem permissão, sem
                // internet ou objeto inexistente (NoSuchKeyException). Não é fatal: tentamos a pasta local.
                System.err.println("[AVISO S3] Falha ao ler " + chave + ": " + e.getMessage()
                        + " -> tentando a pasta local.");
            }
        }
        return lerLocal(pasta, arquivo);
    }

    /** Lê o Gold da pasta local: GOLD_DIR_LOCAL/<empresa>/<identificador>_sistema.json. */
    private JsonNode lerLocal(String pasta, String arquivo) throws IOException {
        Path caminho = Path.of(config.getGoldDirLocal(), pasta, arquivo);
        // Files.readAllBytes lança NoSuchFileException (subclasse de IOException) se o arquivo não existir;
        // quem chama trata e apenas pula a máquina sem Gold.
        return objectMapper.readTree(Files.readAllBytes(caminho));
    }

    /** Todos os registros do histórico da métrica, em ordem de tempo (pontos nulos ou inválidos são ignorados). */
    public List<Ponto> serie(JsonNode gold, Fonte fonte) {
        List<Ponto> pontos = new ArrayList<>();

        for (JsonNode registro : gold.path(fonte.getSecao()).path("historico")) {
            JsonNode valor = registro.path(fonte.getCampo());
            if (!valor.isNumber()) {
                continue; // campo ausente ou null: nada a avaliar neste ponto
            }
            try {
                LocalDateTime ts = LocalDateTime.parse(registro.path("timestamp").asText(), FORMATO);
                pontos.add(new Ponto(ts, valor.asDouble() * fonte.getFator()));
            } catch (DateTimeParseException e) {
                // DateTimeParseException: o "timestamp" está ausente ou fora do formato yyyy-MM-dd HH:mm:ss.
                // Sem horário não dá para posicionar o ponto na linha do tempo, então ele é descartado.
            }
        }

        ordenar(pontos);
        return pontos;
    }

    /** Ordena os pontos do mais antigo para o mais novo (insertion sort, estável, escrito com for/while). */
    private void ordenar(List<Ponto> pontos) {
        for (Integer i = 1; i < pontos.size(); i++) {
            Ponto atual = pontos.get(i);
            Integer j = i - 1;
            while (j >= 0 && pontos.get(j).getTs().isAfter(atual.getTs())) {
                pontos.set(j + 1, pontos.get(j));
                j = j - 1;
            }
            pontos.set(j + 1, atual);
        }
    }

    /**
     * Varre a pasta Gold local (GOLD_DIR_LOCAL/<empresa>/<id>_sistema.json) e lista as máquinas encontradas.
     * Usado no modo sem banco de dados, onde não há tabela de instâncias para consultar.
     */
    public List<MaquinaGold> listarMaquinasLocais() {
        List<MaquinaGold> maquinas = new ArrayList<>();

        File raiz = new File(config.getGoldDirLocal());
        File[] pastas = raiz.listFiles(); // null quando a pasta não existe
        if (pastas == null) {
            System.err.println("[AVISO] Pasta Gold local não encontrada: " + config.getGoldDirLocal());
            return maquinas;
        }
        ordenarPorNome(pastas);

        for (File pasta : pastas) {
            if (!pasta.isDirectory()) {
                continue; // ignora o indice_maquinas.json da raiz
            }
            File[] arquivos = pasta.listFiles();
            if (arquivos == null) {
                continue;
            }
            ordenarPorNome(arquivos);

            for (File arquivo : arquivos) {
                String nomeArquivo = arquivo.getName();
                if (!nomeArquivo.endsWith("_sistema.json")) {
                    continue; // ignora o indice_maquinas.json da empresa
                }
                try {
                    JsonNode gold = objectMapper.readTree(arquivo);
                    maquinas.add(montarMaquina(pasta.getName(), nomeArquivo, gold));
                } catch (IOException e) {
                    // IOException: arquivo Gold ilegível ou com JSON inválido (ex.: ETL ainda gravando). Pula só ele.
                    System.err.println("[AVISO] Gold ilegível (" + arquivo.getPath() + "): " + e.getMessage());
                }
            }
        }
        return maquinas;
    }

    /** Monta a MaquinaGold com os dados do próprio JSON (usa o nome do arquivo se o JSON não trouxer o identificador). */
    private MaquinaGold montarMaquina(String pastaEmpresa, String nomeArquivo, JsonNode gold) {
        String identificador = textoOuNulo(gold.path("maquina").path("identifier"));
        if (identificador == null) {
            // "<identificador>_sistema.json" -> remove os 13 caracteres de "_sistema.json"
            identificador = nomeArquivo.substring(0, nomeArquivo.length() - 13);
        }
        return new MaquinaGold(
                pastaEmpresa,
                textoOuNulo(gold.path("empresa").path("nome")),
                identificador,
                textoOuNulo(gold.path("maquina").path("nome")));
    }

    /** Devolve o texto do nó JSON, ou null se ele não existir, for null ou não for texto. */
    private String textoOuNulo(JsonNode no) {
        if (no.isTextual()) {
            return no.asText();
        }
        return null;
    }

    /** Ordena um vetor de arquivos pelo nome (insertion sort) para o resultado ser sempre na mesma ordem. */
    private void ordenarPorNome(File[] arquivos) {
        for (Integer i = 1; i < arquivos.length; i++) {
            File atual = arquivos[i];
            Integer j = i - 1;
            while (j >= 0 && arquivos[j].getName().compareTo(atual.getName()) > 0) {
                arquivos[j + 1] = arquivos[j];
                j = j - 1;
            }
            arquivos[j + 1] = atual;
        }
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public Config getConfig() {
        return config;
    }

    public S3Client getS3Client() {
        return s3Client;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "GoldReader{config=" + config
                + ", s3Client=" + s3Client
                + ", objectMapper=" + objectMapper + "}";
    }

}
