package school.sptech.dto;

// Jackson (jackson-annotations): anotações que dizem como cada atributo vira uma chave no JSON.
import com.fasterxml.jackson.annotation.JsonProperty;

// java.util: ArrayList/List guardam coleções de objetos em memória.
import java.util.ArrayList;
import java.util.List;

/**
 * DTO: raiz do arquivo alertas_padrao.json -> { "AlertasPadrao": [ {...}, {...} ] }.
 */
public class AlertasPadraoArquivoDto {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Lista de limites padrão por componente.
    @JsonProperty("AlertasPadrao")
    private List<AlertaPadraoDto> alertasPadrao = new ArrayList<>();

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public AlertasPadraoArquivoDto() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public AlertasPadraoArquivoDto(List<AlertaPadraoDto> alertasPadrao) {
        this.alertasPadrao = alertasPadrao;
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

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "AlertasPadraoArquivoDto{alertasPadrao=" + alertasPadrao + "}";
    }

}
