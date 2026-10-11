package school.sptech.config;

// Utilitário próprio do projeto (validação de texto sem métodos prontos).
import school.sptech.util.Formatador;

/**
 * Toda a configuração do programa vem de VARIÁVEIS DE AMBIENTE (nada fixo no código).
 *
 * Modos de execução (todos opcionais - o programa roda mesmo sem nenhuma integração):
 *   MODO_OFFLINE=true -> não usa banco, nem S3, nem Jira, nem Slack (só a pasta Gold local).
 *   USAR_BANCO=false  -> não conecta ao MySQL (as regras vêm dos alertas padrão).
 *   USAR_S3=false     -> não lê do S3, mesmo com AWS_BUCKET preenchido.
 *   USAR_JIRA=false   -> não cria issues no Jira (apenas mostra o que seria criado).
 *   USAR_SLACK=false  -> não envia mensagens ao Slack (apenas mostra o que seria enviado).
 */
public class Config {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // URL JDBC do MySQL (montada com DB_HOST, DB_PORT e DB_NAME, ou informada direto em DB_URL).
    private String dbUrl;

    // Usuário do banco (DB_USER).
    private String dbUser;

    // Senha do banco (DB_PASS).
    private String dbPass;

    // Nome do bucket S3 (AWS_BUCKET). Vazio = não usa S3.
    private String awsBucket;

    // Região do bucket (AWS_REGION).
    private String awsRegion;

    // Prefixo ("pasta") do Gold dentro do bucket (GOLD_PREFIX).
    private String goldPrefix;

    // Pasta local com o Gold gerado pelo ETL (GOLD_DIR_LOCAL).
    private String goldDirLocal;

    // Caminho do arquivo alertas.json gerado por este programa (ALERTAS_JSON).
    private String alertasJson;

    // Caminho do arquivo opcional com alertas padrão (ALERTAS_PADRAO_JSON).
    private String alertasPadraoJson;

    // Reservado para uso futuro (JANELA_MINUTOS): a lógica atual ainda não utiliza.
    private Integer janelaMinutos;

    // Reservado para uso futuro (TELEMETRIA_MAX_MINUTOS): a lógica atual ainda não utiliza.
    private Integer telemetriaMaxMinutos;

    // Intervalo entre ciclos no modo --loop (INTERVALO_MINUTOS).
    private Integer intervaloMinutos;

    // Liga o modo 100% offline (MODO_OFFLINE ou argumento --offline).
    private Boolean modoOffline = false;

    // Permite tentar conectar ao MySQL (USAR_BANCO).
    private Boolean usarBanco = true;

    // Permite tentar ler do S3 (USAR_S3).
    private Boolean usarS3 = true;

    // Permite criar issues no Jira (USAR_JIRA).
    private Boolean usarJira = true;

    // Permite enviar mensagens ao Slack (USAR_SLACK).
    private Boolean usarSlack = true;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public Config() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public Config(String dbUrl, String dbUser, String dbPass, String awsBucket, String awsRegion, String goldPrefix, String goldDirLocal, String alertasJson, String alertasPadraoJson, Integer janelaMinutos, Integer telemetriaMaxMinutos, Integer intervaloMinutos, Boolean modoOffline, Boolean usarBanco, Boolean usarS3, Boolean usarJira, Boolean usarSlack) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPass = dbPass;
        this.awsBucket = awsBucket;
        this.awsRegion = awsRegion;
        this.goldPrefix = goldPrefix;
        this.goldDirLocal = goldDirLocal;
        this.alertasJson = alertasJson;
        this.alertasPadraoJson = alertasPadraoJson;
        this.janelaMinutos = janelaMinutos;
        this.telemetriaMaxMinutos = telemetriaMaxMinutos;
        this.intervaloMinutos = intervaloMinutos;
        this.modoOffline = modoOffline;
        this.usarBanco = usarBanco;
        this.usarS3 = usarS3;
        this.usarJira = usarJira;
        this.usarSlack = usarSlack;
    }

    // ==================================================================
    // MÉTODOS
    // ==================================================================

    /** Lê as variáveis de ambiente e monta o objeto Config já com os valores padrão aplicados. */
    public static Config carregar() {
        String host = env("DB_HOST", "localhost");
        String porta = env("DB_PORT", "3306");
        String nome = env("DB_NAME", "TrackCommerce");

        // connectTimeout evita que o programa fique "preso" muito tempo quando o banco está inacessível.
        String urlPadrao = "jdbc:mysql://" + host + ":" + porta + "/" + nome
                + "?useSSL=false&allowPublicKeyRetrieval=true&connectTimeout=5000";

        Config config = new Config();
        config.setDbUrl(env("DB_URL", urlPadrao));
        config.setDbUser(env("DB_USER", "root"));
        config.setDbPass(env("DB_PASS", ""));
        config.setAwsBucket(env("AWS_BUCKET", ""));
        config.setAwsRegion(env("AWS_REGION", "us-east-1"));
        config.setGoldPrefix(env("GOLD_PREFIX", "track-commerce/gold"));
        config.setGoldDirLocal(env("GOLD_DIR_LOCAL", "/.track-commerce/gold"));
        config.setAlertasJson(env("ALERTAS_JSON", "/.track-commerce/gold/alertas.json"));
        config.setAlertasPadraoJson(env("ALERTAS_PADRAO_JSON", "./alertas_padrao.json"));
        config.setJanelaMinutos(envInteiro("JANELA_MINUTOS", 15));
        config.setTelemetriaMaxMinutos(envInteiro("TELEMETRIA_MAX_MINUTOS", 30));
        config.setIntervaloMinutos(envInteiro("INTERVALO_MINUTOS", 60));
        config.setModoOffline(envBooleano("MODO_OFFLINE", false));
        config.setUsarBanco(envBooleano("USAR_BANCO", true));
        config.setUsarS3(envBooleano("USAR_S3", true));
        config.setUsarJira(envBooleano("USAR_JIRA", true));
        config.setUsarSlack(envBooleano("USAR_SLACK", true));
        return config;
    }

    /** O banco só é usado fora do modo offline e quando o USAR_BANCO está ligado. */
    public Boolean bancoAtivo() {
        return !modoOffline && usarBanco;
    }

    /** O S3 só é usado fora do modo offline, com USAR_S3 ligado e com um bucket informado. */
    public Boolean s3Ativo() {
        return !modoOffline && usarS3 && !Formatador.textoVazio(awsBucket);
    }

    /** O Jira só é acionado fora do modo offline e com USAR_JIRA ligado. */
    public Boolean jiraAtivo() {
        return !modoOffline && usarJira;
    }

    /** O Slack só é acionado fora do modo offline e com USAR_SLACK ligado. */
    public Boolean slackAtivo() {
        return !modoOffline && usarSlack;
    }

    /** Lê uma variável de ambiente de texto; se não existir (ou estiver vazia), devolve o padrão. */
    private static String env(String chave, String padrao) {
        String valor = System.getenv(chave);
        if (valor == null || Formatador.textoVazio(valor)) {
            return padrao;
        }
        return valor.trim();
    }

    /** Lê uma variável de ambiente numérica, validando o conteúdo. */
    private static Integer envInteiro(String chave, Integer padrao) {
        String texto = env(chave, "");
        if (texto.equals("")) {
            return padrao;
        }
        try {
            Integer numero = Integer.parseInt(texto);
            // Validação: intervalos e janelas precisam ser positivos.
            if (numero <= 0) {
                System.err.println("[AVISO] " + chave + " deve ser maior que zero. Usando o padrão: " + padrao);
                return padrao;
            }
            return numero;
        } catch (NumberFormatException e) {
            // NumberFormatException: lançada pelo Integer.parseInt quando o texto não é um número
            // inteiro (ex.: "abc" ou "10min"). Em vez de derrubar o programa, usamos o valor padrão.
            System.err.println("[AVISO] " + chave + " não é um número válido (\"" + texto + "\"). Usando o padrão: " + padrao);
            return padrao;
        }
    }

    /** Lê uma variável de ambiente verdadeiro/falso (aceita true/false, 1/0, sim/nao, yes/no, t/f). */
    private static Boolean envBooleano(String chave, Boolean padrao) {
        String texto = env(chave, "");
        if (texto.equals("")) {
            return padrao;
        }
        if (texto.equalsIgnoreCase("true") || texto.equals("1") || texto.equalsIgnoreCase("sim")
                || texto.equalsIgnoreCase("yes") || texto.equalsIgnoreCase("t")) {
            return true;
        }
        if (texto.equalsIgnoreCase("false") || texto.equals("0") || texto.equalsIgnoreCase("nao")
                || texto.equalsIgnoreCase("não") || texto.equalsIgnoreCase("no") || texto.equalsIgnoreCase("f")) {
            return false;
        }
        System.err.println("[AVISO] " + chave + " deve ser true ou false. Usando o padrão: " + padrao);
        return padrao;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }

    public String getAwsBucket() {
        return awsBucket;
    }

    public void setAwsBucket(String awsBucket) {
        this.awsBucket = awsBucket;
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }

    public String getGoldPrefix() {
        return goldPrefix;
    }

    public void setGoldPrefix(String goldPrefix) {
        this.goldPrefix = goldPrefix;
    }

    public String getGoldDirLocal() {
        return goldDirLocal;
    }

    public void setGoldDirLocal(String goldDirLocal) {
        this.goldDirLocal = goldDirLocal;
    }

    public String getAlertasJson() {
        return alertasJson;
    }

    public void setAlertasJson(String alertasJson) {
        this.alertasJson = alertasJson;
    }

    public String getAlertasPadraoJson() {
        return alertasPadraoJson;
    }

    public void setAlertasPadraoJson(String alertasPadraoJson) {
        this.alertasPadraoJson = alertasPadraoJson;
    }

    public Integer getJanelaMinutos() {
        return janelaMinutos;
    }

    public void setJanelaMinutos(Integer janelaMinutos) {
        this.janelaMinutos = janelaMinutos;
    }

    public Integer getTelemetriaMaxMinutos() {
        return telemetriaMaxMinutos;
    }

    public void setTelemetriaMaxMinutos(Integer telemetriaMaxMinutos) {
        this.telemetriaMaxMinutos = telemetriaMaxMinutos;
    }

    public Integer getIntervaloMinutos() {
        return intervaloMinutos;
    }

    public void setIntervaloMinutos(Integer intervaloMinutos) {
        this.intervaloMinutos = intervaloMinutos;
    }

    public Boolean getModoOffline() {
        return modoOffline;
    }

    public void setModoOffline(Boolean modoOffline) {
        this.modoOffline = modoOffline;
    }

    public Boolean getUsarBanco() {
        return usarBanco;
    }

    public void setUsarBanco(Boolean usarBanco) {
        this.usarBanco = usarBanco;
    }

    public Boolean getUsarS3() {
        return usarS3;
    }

    public void setUsarS3(Boolean usarS3) {
        this.usarS3 = usarS3;
    }

    public Boolean getUsarJira() {
        return usarJira;
    }

    public void setUsarJira(Boolean usarJira) {
        this.usarJira = usarJira;
    }

    public Boolean getUsarSlack() {
        return usarSlack;
    }

    public void setUsarSlack(Boolean usarSlack) {
        this.usarSlack = usarSlack;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "Config{dbUrl=" + dbUrl
                + ", dbUser=" + dbUser
                + ", dbPass=" + "****"
                + ", awsBucket=" + awsBucket
                + ", awsRegion=" + awsRegion
                + ", goldPrefix=" + goldPrefix
                + ", goldDirLocal=" + goldDirLocal
                + ", alertasJson=" + alertasJson
                + ", alertasPadraoJson=" + alertasPadraoJson
                + ", janelaMinutos=" + janelaMinutos
                + ", telemetriaMaxMinutos=" + telemetriaMaxMinutos
                + ", intervaloMinutos=" + intervaloMinutos
                + ", modoOffline=" + modoOffline
                + ", usarBanco=" + usarBanco
                + ", usarS3=" + usarS3
                + ", usarJira=" + usarJira
                + ", usarSlack=" + usarSlack + "}";
    }

}
