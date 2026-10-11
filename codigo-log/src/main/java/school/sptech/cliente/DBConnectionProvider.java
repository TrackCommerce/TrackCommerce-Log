package school.sptech.cliente;

// Apache Commons DBCP2: pool de conexões (reaproveita conexões abertas com o banco em vez de abrir uma a cada consulta).
import org.apache.commons.dbcp2.BasicDataSource;

// Spring JDBC: JdbcTemplate executa SQL de forma simples, cuidando de abrir/fechar conexão e statements.
import org.springframework.jdbc.core.JdbcTemplate;

// java.sql / java.time: exceção de SQL e duração (tempo máximo de espera por uma conexão).
import java.sql.SQLException;
import java.time.Duration;

// Configuração do projeto (variáveis de ambiente).
import school.sptech.config.Config;

/**
 * Cria a conexão com o MySQL (driver mysql-connector-j + pool DBCP2 + JdbcTemplate).
 *
 * O pool só abre a conexão de verdade na primeira consulta; se o banco estiver fora do ar,
 * quem consulta recebe uma exceção e o programa segue no modo sem banco.
 */
public class DBConnectionProvider {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Pool de conexões configurado com a URL, usuário e senha do banco.
    private final BasicDataSource dataSource;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Configura o pool de conexões a partir do objeto Config. */
    public DBConnectionProvider(Config config) {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(config.getDbUrl());
        basicDataSource.setUsername(config.getDbUser());
        basicDataSource.setPassword(config.getDbPass());
        // Não espera mais que 5 segundos por uma conexão (evita travar quando o banco está fora do ar).
        basicDataSource.setMaxWait(Duration.ofSeconds(5));

        this.dataSource = basicDataSource;
    }

    // ==================================================================
    // MÉTODOS
    // ==================================================================

    /** Devolve o JdbcTemplate pronto para executar consultas. */
    public JdbcTemplate getConnection() {
        return new JdbcTemplate(dataSource);
    }

    /** Fecha o pool de conexões ao terminar o ciclo. */
    public void fechar() {
        try {
            dataSource.close();
        } catch (SQLException e) {
            // SQLException: lançada se o pool não conseguir fechar alguma conexão. Como já
            // terminamos de usar o banco, apenas avisamos e seguimos.
            System.err.println("[AVISO BD] Falha ao fechar o pool de conexões: " + e.getMessage());
        }
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public BasicDataSource getDataSource() {
        return dataSource;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "DBConnectionProvider{dataSource=" + dataSource + "}";
    }

}
