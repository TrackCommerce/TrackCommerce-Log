package school.sptech.dto;

// Jackson (jackson-annotations): anotações que dizem como cada atributo vira uma chave no JSON.
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO: raiz do alertas.json. O arquivo inteiro é { "AlertasDias": { ... } }.
 */
public class RaizDto {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Conteúdo do arquivo.
    @JsonProperty("AlertasDias")
    private DiasDto alertasDias = new DiasDto();

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public RaizDto() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public RaizDto(DiasDto alertasDias) {
        this.alertasDias = alertasDias;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public DiasDto getAlertasDias() {
        return alertasDias;
    }

    public void setAlertasDias(DiasDto alertasDias) {
        this.alertasDias = alertasDias;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "RaizDto{alertasDias=" + alertasDias + "}";
    }

}
