package school.sptech;

// Spring JDBC: DataAccessException é a exceção lançada em qualquer falha de acesso ao banco de dados.
import org.springframework.dao.DataAccessException;

// java.io / java.util: exceção de I/O e listas em memória.
import java.io.IOException;
import java.util.List;
import java.nio.file.Path;
import school.sptech.cliente.S3Provider;

// Importando classes do próprio projeto (cada uma tem a sua responsabilidade explicada no topo do arquivo).
import school.sptech.cliente.DBConnectionProvider;
import school.sptech.cliente.Jira;
import school.sptech.config.Config;
import school.sptech.model.Evento;
import school.sptech.model.Nivel;
import school.sptech.model.Regra;
import school.sptech.model.Resultado;
import school.sptech.service.AlertaPadraoRepository;
import school.sptech.service.AlertaStore;
import school.sptech.service.GoldReader;
import school.sptech.service.RegraLocalService;
import school.sptech.service.RegraRepository;
import school.sptech.util.Formatador;

/**
 * Ponto de entrada do Track Commerce - Alertas.
 *
 * Lê o JSON Gold gerado pelo ETL, compara cada métrica com os limites de alerta e gera/atualiza o
 * arquivo alertas.json. Opcionalmente abre chamados no Jira.
 *
 * Como executar:
 *   java -jar track-alertas.jar            -> executa UM ciclo e termina (use com cron/systemd)
 *   java -jar track-alertas.jar --loop     -> executa a cada INTERVALO_MINUTOS
 *   java -jar track-alertas.jar --offline  -> não usa banco, S3 nem Jira (só a pasta Gold local)
 *
 * Todas as integrações são opcionais: se o MySQL, o S3, o Jira estiverem desligados ou
 * inacessíveis, o programa avisa no console e continua com o que tiver disponível.
 *
 * Classe sem atributos: só tem métodos estáticos.
 */
public class Main {

    public static void main(String[] args) {
        Config config = Config.carregar();
        Boolean modoLoop = false;

        // Lê os argumentos da linha de comando para execução do arquivo.
        for (Integer i = 0; i < args.length; i++) { /** Args: lista nativa utilizada no Method Man para verificar comandos de terminais*/
            if (args[i].equals("--loop")) {
                modoLoop = true;
            } else if (args[i].equals("--offline")) {
                config.setModoOffline(true);
            } else {
                System.err.println("[AVISO] Argumento desconhecido ignorado: " + args[i]);
            }
        }

        if (config.getModoOffline()) {
            System.out.println("[MODO OFFLINE] Banco, S3, Jira desativados.");
        }

        if (!modoLoop) {
            executarCiclo(config);
            return;
        }

        // Modo --loop: repete o ciclo e espera o intervalo configurado entre uma execução e outra.
        while (true) {
            executarCiclo(config);
            try {
                Long esperaMilissegundos = config.getIntervaloMinutos() * 60L * 1000L;
                Thread.sleep(esperaMilissegundos);
            } catch (InterruptedException e) {
                // InterruptedException: lançada quando alguém pede para a thread parar durante a espera
                // (ex.: Ctrl+C ou encerramento do serviço). Restauramos o sinal e saímos do laço.
                Thread.currentThread().interrupt();
                System.out.println("Agendamento interrompido. Encerrando.");
                break;
            }
        }
    }

    /** Ciclos: carregam as regras, atualizam o arquivo alertas.json e notifica. */
    static void executarCiclo(Config config) {
        System.out.println("Iniciando ciclo de alertas");

        try {
            GoldReader goldReader = new GoldReader(config);
            AlertaPadraoRepository alertaPadraoRepository = new AlertaPadraoRepository(config);
            Jira jira = new Jira();

            // 1) Regras: do banco (quando disponível) ou dos alertas padrão aplicados às máquinas do Gold.
            List<Regra> regras = obterRegras(config, goldReader, alertaPadraoRepository);
            Integer comPadrao = 0;
            for (Regra regra : regras) {
                if (regra.getPadrao()) {
                    comPadrao++;
                }
            }
            System.out.println(regras.size() + " regra(s) ativa(s), " + comPadrao + " com limites padrão");

            // Recupera o histórico da Gold antes de processar novos alertas.
            // Erros de credencial/permissão interrompem o ciclo para evitar perda de histórico.
            S3Provider s3Historico = config.s3Ativo() ? new S3Provider(config) : null;
            if (s3Historico != null) {
                Path local = Path.of(config.getAlertasJson());
                boolean encontrado = s3Historico.baixarSeExistir(
                        config.getAwsBucket(), config.getAlertasS3Key(), local);
                System.out.println(encontrado ? "[S3] Histórico recuperado" : "[S3] Histórico ainda não existe");
            }

            // 2) Atualiza o alertas.json (cria do zero ou fecha/abre alertas conforme o Gold).
            AlertaStore store = new AlertaStore(config.getAlertasJson());
            if (store.getJsonExistia()) {
                System.out.println("alertas.json encontrado: verificando alertas sem fim e dados novos");
            } else {
                System.out.println("alertas.json não existe: lendo todo o histórico do gold");
            }

            Resultado resultado = store.atualizar(regras, goldReader);
            System.out.println(resultado.getAbertos().size() + " alerta(s) aberto(s) e "
                    + resultado.getEncerrados().size() + " encerrado(s) neste ciclo");

            // 3) Notificações (Jira) - só para alertas que abriram e para os que normalizaram.
            for (Evento evento : resultado.getAbertos()) {
                notificarAbertura(evento, config, jira);
            }
            for (Evento evento : resultado.getEncerrados()) {
                System.out.println("[NORMALIZADO] " + evento.getRegra().getComponente() + " em " + evento.getRegra().getInstancia());
            }
            if (s3Historico != null) {
                s3Historico.enviarJson(config.getAwsBucket(), config.getAlertasS3Key(), store.getArquivo());
                System.out.println("[S3] Histórico publicado: " + config.getAlertasS3Key());
            }
        } catch (IOException e) {
            // IOException: falha ao ler/gravar o alertas.json (arquivo corrompido, sem permissão, disco cheio).
            System.err.println("[ERRO] Falha ao acessar o alertas.json: " + e.getMessage());
        } catch (RuntimeException e) {
            // RuntimeException: qualquer erro inesperado de programação/dados. É capturada para que, no
            // modo --loop, um ciclo com problema não derrube o agendamento dos próximos ciclos.
            System.err.println("[ERRO] Falha geral no ciclo: " + e.getMessage());
        }
        System.out.println("Ciclo finalizado");
    }

    /**
     * Escolhe a origem das regras:
     *  - banco ativo e acessível -> regras do MySQL (componentes sem parâmetro recebem o alerta padrão);
     *  - banco desligado ou fora do ar -> alertas padrão aplicados a cada máquina encontrada no Gold local.
     */
    private static List<Regra> obterRegras(Config config, GoldReader goldReader,
                                           AlertaPadraoRepository alertaPadraoRepository) {
        if (config.bancoAtivo()) {
            DBConnectionProvider dbConnectionProvider = new DBConnectionProvider(config);
            try {
                return new RegraRepository(dbConnectionProvider, alertaPadraoRepository).carregarRegras();
            } catch (DataAccessException e) {
                // DataAccessException: o Spring converte qualquer falha do banco (servidor fora do ar,
                // senha errada, tabela inexistente...) nesta exceção. Seguimos sem banco, em vez de parar.
                System.err.println("[AVISO BD] Banco indisponível, usando alertas padrão com o Gold local: "
                        + e.getMessage());
            } finally {
                dbConnectionProvider.fechar(); // sempre libera o pool, deu certo ou não
            }
        } else {
            System.out.println("[INFO] Banco desativado: usando alertas padrão com o Gold local.");
        }

        return new RegraLocalService(goldReader, alertaPadraoRepository).gerarRegras();
    }

    /** Alerta novo: abre issue no Jira quando configurado. */
    private static void notificarAbertura(Evento evento, Config config, Jira jira) {
        Regra regra = evento.getRegra();
        String chaveJira = null;

        if (config.jiraAtivo() && regra.temJira()) {
            try {
                chaveJira = jira.criarIssue(regra, evento.getNivel(), evento.getValor());
            } catch (IOException | InterruptedException | IllegalArgumentException e) {
                // IOException: Jira fora do ar, sem internet ou resposta diferente de "criado" (401, 400...).
                // InterruptedException: a thread foi interrompida durante a requisição.
                // IllegalArgumentException: a URL do Jira cadastrada no banco é inválida.
                // Em todos os casos o alerta continua válido; só não haverá chamado no Jira.
                System.err.println("[AVISO] Jira indisponível (" + regra.getInstancia() + "): "
                        + e.getClass().getSimpleName() + " - " + e.getMessage());
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        } else {
            System.out.println("[JIRA DESATIVADO] Chamado que seria criado: [" + evento.getNivel().name() + "] "
                    + regra.getComponente() + " em " + regra.getInstancia());
        }

        if (chaveJira != null) {
            System.out.println("[JIRA] Chamado criado: " + chaveJira);
        }
    }
}
