package school.sptech.service;

// Spring JDBC: JdbcTemplate executa a consulta SQL; DataAccessException é a exceção lançada em qualquer falha de banco.
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

// java.util: List/ArrayList/Map guardam as linhas do resultado (cada linha = Map coluna -> valor).
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Projeto: provedor de conexão, DTO de alerta padrão e modelo da regra.
import school.sptech.cliente.DBConnectionProvider;
import school.sptech.dto.AlertaPadraoDto;
import school.sptech.model.Regra;

/**
 * Acesso ao MySQL: leitura das regras de monitoramento (o estado dos alertas fica no alertas.json).
 *
 * Diferença em relação à versão anterior: a consulta usa LEFT JOIN nos parâmetros de alerta.
 * Assim, um componente ATIVO (ic.Status = 1) que ainda não tem parâmetro vinculado NÃO é descartado:
 * ele recebe os limites do AlertaPadraoRepository e os alertas são gerados normalmente.
 */
public class RegraRepository {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Consulta das regras. LEFT JOIN em parametros_alertas/parametros/apijira = esses dados são opcionais.
    private static final String SQL_REGRAS = """
            SELECT ic.id_instancia_componente, e.id_empresa, e.razao_social AS empresa_nome, i.nome AS instancia,
                   i.identificador, c.nome AS componente, p.unidade_medida,
                   pa.alerta_moderado, pa.alerta_critico,
                   aj.jira_url, aj.jira_email, aj.codigo_api, aj.jira_project_key, aj.slack_webhook
            FROM instancias_componentes ic
            JOIN instancias i  ON i.id_instancia = ic.fk_instancia
            JOIN empresa e     ON e.id_empresa = i.fk_empresa
            JOIN componentes c ON c.id_componente = ic.fk_componentes
            LEFT JOIN parametros_alertas pa ON pa.id_parametro_alerta = ic.fk_parametros_alertas
            LEFT JOIN parametros p          ON p.id_parametro = pa.fk_parametro
            LEFT JOIN apijira aj            ON aj.fk_empresa = e.id_empresa
            WHERE ic.Status = 1 AND i.ativo = 1
              AND c.nome IN ('CPU','Memoria RAM','Armazenamento',
                             'Leitura de Disco','Escrita de Disco',
                             'Rede - Download','Rede - Upload')
            """;

    // Executor de SQL.
    private final JdbcTemplate jdbcTemplate;

    // Fonte dos limites padrão.
    private final AlertaPadraoRepository alertaPadraoRepository;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Recebe o provedor de conexão (já configurado) e a fonte de alertas padrão. */
    public RegraRepository(DBConnectionProvider dbConnectionProvider, AlertaPadraoRepository alertaPadraoRepository) {
        this.jdbcTemplate = dbConnectionProvider.getConnection();
        this.alertaPadraoRepository = alertaPadraoRepository;
    }

    // ==================================================================
    // MÉTODOS
    // ==================================================================

    /**
     * Executa a consulta e transforma cada linha em uma Regra.
     *
     * @throws DataAccessException (não verificada) se o banco estiver inacessível ou a consulta falhar;
     *                             quem chama decide seguir sem banco.
     */
    public List<Regra> carregarRegras() throws DataAccessException {
        List<Regra> regras = new ArrayList<>();
        List<Map<String, Object>> linhas = jdbcTemplate.queryForList(SQL_REGRAS);

        for (Map<String, Object> linha : linhas) {
            String componente = paraTexto(linha.get("componente"));
            String unidade = paraTexto(linha.get("unidade_medida"));
            Double moderado = paraDecimal(linha.get("alerta_moderado"));
            Double critico = paraDecimal(linha.get("alerta_critico"));
            Boolean usouPadrao = false;

            AlertaPadraoDto padrao = alertaPadraoRepository.buscar(componente);

            // Componente ativo, mas sem parâmetro vinculado (ou com limites em branco): usa o padrão.
            if (moderado == null || critico == null) {
                if (padrao == null) {
                    System.err.println("[AVISO] " + componente + " sem parâmetro vinculado e sem alerta padrão cadastrado: ignorado.");
                    continue;
                }
                moderado = padrao.getModerado();
                critico = padrao.getCritico();
                unidade = padrao.getUnidade();
                usouPadrao = true;
            } else if (unidade == null) {
                // Tem limites, mas o parâmetro não informa a unidade: completa com a do padrão, se houver.
                if (padrao != null) {
                    unidade = padrao.getUnidade();
                } else {
                    unidade = "";
                }
            }

            regras.add(new Regra(
                    paraInteiro(linha.get("id_instancia_componente")),
                    paraTexto(linha.get("id_empresa")),
                    paraTexto(linha.get("empresa_nome")),
                    paraTexto(linha.get("instancia")),
                    paraTexto(linha.get("identificador")),
                    componente,
                    unidade,
                    moderado,
                    critico,
                    paraTexto(linha.get("jira_url")),
                    paraTexto(linha.get("jira_email")),
                    paraTexto(linha.get("codigo_api")),
                    paraTexto(linha.get("jira_project_key")),
                    paraTexto(linha.get("slack_webhook")),
                    usouPadrao));
        }
        return regras;
    }

    /** Converte o valor de uma coluna em texto (null continua null). */
    private String paraTexto(Object valor) {
        if (valor == null) {
            return null;
        }
        return valor.toString();
    }

    /** Converte o valor de uma coluna em Integer (null se a coluna estiver vazia ou não for numérica). */
    private Integer paraInteiro(Object valor) {
        if (valor instanceof Number) {
            return ((Number) valor).intValue();
        }
        return null;
    }

    /** Converte o valor de uma coluna em Double (null se a coluna estiver vazia ou não for numérica). */
    private Double paraDecimal(Object valor) {
        if (valor instanceof Number) {
            return ((Number) valor).doubleValue();
        }
        return null;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public AlertaPadraoRepository getAlertaPadraoRepository() {
        return alertaPadraoRepository;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "RegraRepository{jdbcTemplate=" + jdbcTemplate
                + ", alertaPadraoRepository=" + alertaPadraoRepository + "}";
    }

}
