package school.sptech.service;

// Jackson (jackson-databind): ObjectMapper lê o arquivo alertas_padrao.json e converte em objetos Java.
import com.fasterxml.jackson.databind.ObjectMapper;

// java.io: File representa o arquivo no disco; IOException é a exceção de leitura/escrita.
import java.io.File;
import java.io.IOException;

// java.util: ArrayList/List guardam coleções de objetos em memória.
import java.util.ArrayList;
import java.util.List;

// Configuração do projeto (variáveis de ambiente).
import school.sptech.config.Config;

// DTOs do projeto e utilitário de validação de texto.
import school.sptech.dto.AlertaPadraoDto;
import school.sptech.dto.AlertasPadraoArquivoDto;
import school.sptech.util.Formatador;

/**
 * Guarda os limites de alerta PADRÃO de cada componente.
 *
 * Eles são usados quando o componente está ATIVO (Status = 1) em Instancias_Componentes, porém
 * não tem nenhum parâmetro de alerta vinculado - assim o alerta é gerado mesmo assim.
 *
 * Origem dos valores (nesta ordem):
 *   1) valores embutidos neste código (carregarPadroesEmbutidos);
 *   2) arquivo opcional alertas_padrao.json - adiciona componentes novos ou substitui os embutidos;
 *   3) chamadas ao método adicionar(), caso queira cadastrar valores por código.
 */
public class AlertaPadraoRepository {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Limites padrão conhecidos, um por componente.
    private List<AlertaPadraoDto> alertasPadrao = new ArrayList<>();

    // Caminho do alertas_padrao.json (ALERTAS_PADRAO_JSON).
    private final String caminhoArquivo;

    // Converte o JSON do arquivo em objetos Java.
    private final ObjectMapper objectMapper;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Carrega os valores embutidos e, se o arquivo existir, aplica os valores dele por cima. */
    public AlertaPadraoRepository(Config config) {
        this.caminhoArquivo = config.getAlertasPadraoJson();
        this.objectMapper = new ObjectMapper();

        carregarPadroesEmbutidos();
        carregarArquivo();
    }

    // ==================================================================
    // MÉTODOS
    // ==================================================================

    /**
     * Valores que acompanham o programa. Ajuste aqui (ou no alertas_padrao.json) conforme a regra
     * de negócio da empresa. Unidades: % para uso, MB/s para disco e Mbps para rede.
     */
    private void carregarPadroesEmbutidos() {
        adicionar(new AlertaPadraoDto("CPU", "%", 70.0, 90.0));
        adicionar(new AlertaPadraoDto("Memoria RAM", "%", 75.0, 90.0));
        adicionar(new AlertaPadraoDto("Armazenamento", "%", 80.0, 90.0));
        adicionar(new AlertaPadraoDto("Leitura de Disco", "MB/s", 100.0, 200.0));
        adicionar(new AlertaPadraoDto("Escrita de Disco", "MB/s", 100.0, 200.0));
        adicionar(new AlertaPadraoDto("Rede - Download", "Mbps", 500.0, 800.0));
        adicionar(new AlertaPadraoDto("Rede - Upload", "Mbps", 500.0, 800.0));
    }

    /** Lê o alertas_padrao.json (se existir) e adiciona/substitui os valores válidos. */
    private void carregarArquivo() {
        File arquivo = new File(caminhoArquivo);
        if (!arquivo.isFile()) {
            System.out.println("[INFO] Arquivo de alertas padrão não encontrado (" + caminhoArquivo
                    + "). Usando apenas os valores embutidos.");
            return;
        }

        try {
            AlertasPadraoArquivoDto conteudo = objectMapper.readValue(arquivo, AlertasPadraoArquivoDto.class);
            Integer aplicados = 0;
            for (AlertaPadraoDto item : conteudo.getAlertasPadrao()) {
                if (adicionar(item)) {
                    aplicados++;
                }
            }
            System.out.println("[INFO] " + aplicados + " alerta(s) padrão carregado(s) de " + caminhoArquivo);
        } catch (IOException e) {
            // IOException: lançada quando o arquivo não pode ser lido ou quando o conteúdo não é um
            // JSON válido no formato esperado (o Jackson usa subclasses de IOException para isso).
            // Não derrubamos o programa: seguimos só com os valores embutidos.
            System.err.println("[AVISO] Não foi possível ler " + caminhoArquivo + ": " + e.getMessage());
        }
    }

    /**
     * Cadastra (ou substitui) o limite padrão de um componente.
     * Devolve false, com aviso, se o item for inválido - a validação é feita com if simples.
     */
    public Boolean adicionar(AlertaPadraoDto novo) {
        if (novo == null || Formatador.textoVazio(novo.getComponente())) {
            System.err.println("[AVISO] Alerta padrão ignorado: componente não informado.");
            return false;
        }
        if (novo.getModerado() == null || novo.getCritico() == null) {
            System.err.println("[AVISO] Alerta padrão ignorado (" + novo.getComponente() + "): informe moderado e crítico.");
            return false;
        }
        if (novo.getModerado() < 0 || novo.getCritico() < 0 || novo.getModerado() > novo.getCritico()) {
            System.err.println("[AVISO] Alerta padrão ignorado (" + novo.getComponente()
                    + "): é preciso 0 <= moderado <= crítico.");
            return false;
        }
        if (novo.getUnidade() == null) {
            novo.setUnidade("");
        }

        // Se já existe um padrão para o componente, substitui; senão, adiciona no fim da lista.
        for (Integer i = 0; i < alertasPadrao.size(); i++) {
            if (alertasPadrao.get(i).getComponente().equals(novo.getComponente())) {
                alertasPadrao.set(i, novo);
                return true;
            }
        }
        alertasPadrao.add(novo);
        return true;
    }

    /** Procura o padrão do componente. Devolve null se não houver. */
    public AlertaPadraoDto buscar(String componente) {
        for (AlertaPadraoDto padrao : alertasPadrao) {
            if (padrao.getComponente().equals(componente)) {
                return padrao;
            }
        }
        return null;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public List<AlertaPadraoDto> getAlertasPadrao() {
        return alertasPadrao;
    }

    public void setAlertasPadrao(List<AlertaPadraoDto> alertasPadrao) {
        this.alertasPadrao = alertasPadrao;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "AlertaPadraoRepository{alertasPadrao=" + alertasPadrao
                + ", caminhoArquivo=" + caminhoArquivo
                + ", objectMapper=" + objectMapper + "}";
    }

}
