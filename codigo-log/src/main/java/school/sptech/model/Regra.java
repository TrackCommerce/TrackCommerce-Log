package school.sptech.model;

// Utilitário próprio do projeto para validar textos vazios sem métodos prontos.
import school.sptech.util.Formatador;

/**
 * Uma regra de monitoramento: componente de uma instância + limites + destinos de notificação.
 *
 * Quando o componente está ativo em Instancias_Componentes mas NÃO tem parâmetro vinculado,
 * os limites vêm da lista de alertas padrão e o atributo "padrao" fica verdadeiro.
 */
public class Regra {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Chave da tabela instancias_componentes (null no modo sem banco).
    private Integer idInstanciaComponente;

    // Identificador da empresa (nome da pasta no Gold).
    private String empresaId;

    // Razão social da empresa.
    private String empresaNome;

    // Nome da instância (VM) exibido nas mensagens.
    private String instancia;

    // Identificador da máquina, igual ao "identifier" do ETL.
    private String identificador;

    // Nome do componente (CPU, Memoria RAM, Armazenamento...).
    private String componente;

    // Unidade de medida dos limites (%, MB/s, Mbps).
    private String unidade;

    // Valor a partir do qual o alerta é MODERADO.
    private Double moderado;

    // Valor a partir do qual o alerta é CRITICO.
    private Double critico;

    // URL do Jira Cloud da empresa (pode ser null).
    private String jiraUrl;

    // E-mail do usuário do Jira (pode ser null).
    private String jiraEmail;

    // Token de API do Jira (pode ser null).
    private String jiraToken;

    // Chave do projeto no Jira, ex.: TRK (pode ser null).
    private String jiraProjectKey;

    // Webhook do Slack da empresa (pode ser null).
    private String slackWebhook;

    // Verdadeiro quando os limites vieram dos alertas padrão (componente sem parâmetro vinculado).
    private Boolean padrao = false;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public Regra() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public Regra(Integer idInstanciaComponente, String empresaId, String empresaNome, String instancia, String identificador, String componente, String unidade, Double moderado, Double critico, String jiraUrl, String jiraEmail, String jiraToken, String jiraProjectKey, String slackWebhook, Boolean padrao) {
        this.idInstanciaComponente = idInstanciaComponente;
        this.empresaId = empresaId;
        this.empresaNome = empresaNome;
        this.instancia = instancia;
        this.identificador = identificador;
        this.componente = componente;
        this.unidade = unidade;
        this.moderado = moderado;
        this.critico = critico;
        this.jiraUrl = jiraUrl;
        this.jiraEmail = jiraEmail;
        this.jiraToken = jiraToken;
        this.jiraProjectKey = jiraProjectKey;
        this.slackWebhook = slackWebhook;
        this.padrao = padrao;
    }

    // ==================================================================
    // MÉTODOS
    // ==================================================================

    /** Diz se a regra tem todos os dados necessários para abrir um chamado no Jira. */
    public Boolean temJira() {
        // Validação com if simples: qualquer dado ausente impede a criação da issue.
        if (Formatador.textoVazio(jiraUrl)) {
            return false;
        }
        if (Formatador.textoVazio(jiraEmail)) {
            return false;
        }
        if (Formatador.textoVazio(jiraToken)) {
            return false;
        }
        if (Formatador.textoVazio(jiraProjectKey)) {
            return false;
        }
        return true;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public Integer getIdInstanciaComponente() {
        return idInstanciaComponente;
    }

    public void setIdInstanciaComponente(Integer idInstanciaComponente) {
        this.idInstanciaComponente = idInstanciaComponente;
    }

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getEmpresaNome() {
        return empresaNome;
    }

    public void setEmpresaNome(String empresaNome) {
        this.empresaNome = empresaNome;
    }

    public String getInstancia() {
        return instancia;
    }

    public void setInstancia(String instancia) {
        this.instancia = instancia;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getComponente() {
        return componente;
    }

    public void setComponente(String componente) {
        this.componente = componente;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public Double getModerado() {
        return moderado;
    }

    public void setModerado(Double moderado) {
        this.moderado = moderado;
    }

    public Double getCritico() {
        return critico;
    }

    public void setCritico(Double critico) {
        this.critico = critico;
    }

    public String getJiraUrl() {
        return jiraUrl;
    }

    public void setJiraUrl(String jiraUrl) {
        this.jiraUrl = jiraUrl;
    }

    public String getJiraEmail() {
        return jiraEmail;
    }

    public void setJiraEmail(String jiraEmail) {
        this.jiraEmail = jiraEmail;
    }

    public String getJiraToken() {
        return jiraToken;
    }

    public void setJiraToken(String jiraToken) {
        this.jiraToken = jiraToken;
    }

    public String getJiraProjectKey() {
        return jiraProjectKey;
    }

    public void setJiraProjectKey(String jiraProjectKey) {
        this.jiraProjectKey = jiraProjectKey;
    }

    public String getSlackWebhook() {
        return slackWebhook;
    }

    public void setSlackWebhook(String slackWebhook) {
        this.slackWebhook = slackWebhook;
    }

    public Boolean getPadrao() {
        return padrao;
    }

    public void setPadrao(Boolean padrao) {
        this.padrao = padrao;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "Regra{idInstanciaComponente=" + idInstanciaComponente
                + ", empresaId=" + empresaId
                + ", empresaNome=" + empresaNome
                + ", instancia=" + instancia
                + ", identificador=" + identificador
                + ", componente=" + componente
                + ", unidade=" + unidade
                + ", moderado=" + moderado
                + ", critico=" + critico
                + ", jiraUrl=" + jiraUrl
                + ", jiraEmail=" + jiraEmail
                + ", jiraToken=" + "****"
                + ", jiraProjectKey=" + jiraProjectKey
                + ", slackWebhook=" + "****"
                + ", padrao=" + padrao + "}";
    }

}
