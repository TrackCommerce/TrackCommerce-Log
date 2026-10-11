package school.sptech.service;

// java.util: ArrayList/List guardam coleções de objetos em memória.
import java.util.ArrayList;
import java.util.List;

// Projeto: modelos e DTO de alerta padrão.
import school.sptech.dto.AlertaPadraoDto;
import school.sptech.model.MaquinaGold;
import school.sptech.model.Regra;

/**
 * Gera as regras SEM usar o banco de dados.
 *
 * Descobre as máquinas olhando os arquivos do Gold local e aplica os alertas padrão a todos os
 * componentes suportados. É o plano B quando o MySQL está desativado ou inacessível.
 * Como não há tabela apijira, essas regras não têm Jira nem Slack.
 */
public class RegraLocalService {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Leitor do Gold (usado para listar as máquinas locais).
    private final GoldReader goldReader;

    // Fonte dos limites padrão.
    private final AlertaPadraoRepository alertaPadraoRepository;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Recebe as duas fontes de dados necessárias. */
    public RegraLocalService(GoldReader goldReader, AlertaPadraoRepository alertaPadraoRepository) {
        this.goldReader = goldReader;
        this.alertaPadraoRepository = alertaPadraoRepository;
    }

    // ==================================================================
    // MÉTODOS
    // ==================================================================

    /** Uma regra por (máquina do Gold x componente suportado), todas com limites padrão. */
    public List<Regra> gerarRegras() {
        List<Regra> regras = new ArrayList<>();
        List<MaquinaGold> maquinas = goldReader.listarMaquinasLocais();

        for (MaquinaGold maquina : maquinas) {
            // Sem a tabela de instâncias, o "nome da instância" é o nome da VM no Gold ou, na falta dele, o identificador.
            String nomeInstancia = maquina.getNome();
            if (nomeInstancia == null) {
                nomeInstancia = maquina.getIdentificador();
            }
            String nomeEmpresa = maquina.getEmpresaNome();
            if (nomeEmpresa == null) {
                nomeEmpresa = "Empresa Desconhecida";
            }

            for (String componente : FonteResolver.COMPONENTES_SUPORTADOS) {
                AlertaPadraoDto padrao = alertaPadraoRepository.buscar(componente);
                if (padrao == null) {
                    continue; // alguém removeu o padrão deste componente: não há limites para avaliar
                }
                regras.add(new Regra(
                        null,                        // sem banco não existe id_instancia_componente
                        maquina.getEmpresaId(),
                        nomeEmpresa,
                        nomeInstancia,
                        maquina.getIdentificador(),
                        componente,
                        padrao.getUnidade(),
                        padrao.getModerado(),
                        padrao.getCritico(),
                        null, null, null, null,      // sem Jira
                        null,                        // sem Slack
                        true));
            }
        }
        return regras;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public GoldReader getGoldReader() {
        return goldReader;
    }

    public AlertaPadraoRepository getAlertaPadraoRepository() {
        return alertaPadraoRepository;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "RegraLocalService{goldReader=" + goldReader
                + ", alertaPadraoRepository=" + alertaPadraoRepository + "}";
    }

}
