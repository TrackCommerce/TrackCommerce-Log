package school.sptech.dto;

// Jackson (jackson-annotations): anotações que dizem como cada atributo vira uma chave no JSON.
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO: um alerta dentro do alertas.json.
 * Os nomes das chaves do JSON seguem o formato combinado com o time (Tipo_alerta, UnidadeGravada...).
 */
public class AlertaItemDto {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // MODERADO ou CRITICO.
    @JsonProperty("Tipo_alerta")
    private String tipoAlerta;

    // Unidade dos limites usados no alerta.
    @JsonProperty("UnidadeGravada")
    private String unidadeGravada;

    // Nome do componente que gerou o alerta.
    @JsonProperty("ComponenteEmAlerta")
    private String componenteEmAlerta;

    // Início do alerta (yyyy-MM-dd HH:mm:ss).
    @JsonProperty("DataInicioAlerta")
    private String dataInicioAlerta;

    // Fim do alerta; fica nulo enquanto o alerta continua aberto.
    @JsonProperty("DataFimAlerta")
    private String dataFimAlerta;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public AlertaItemDto() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public AlertaItemDto(String tipoAlerta, String unidadeGravada, String componenteEmAlerta, String dataInicioAlerta, String dataFimAlerta) {
        this.tipoAlerta = tipoAlerta;
        this.unidadeGravada = unidadeGravada;
        this.componenteEmAlerta = componenteEmAlerta;
        this.dataInicioAlerta = dataInicioAlerta;
        this.dataFimAlerta = dataFimAlerta;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public String getTipoAlerta() {
        return tipoAlerta;
    }

    public void setTipoAlerta(String tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }

    public String getUnidadeGravada() {
        return unidadeGravada;
    }

    public void setUnidadeGravada(String unidadeGravada) {
        this.unidadeGravada = unidadeGravada;
    }

    public String getComponenteEmAlerta() {
        return componenteEmAlerta;
    }

    public void setComponenteEmAlerta(String componenteEmAlerta) {
        this.componenteEmAlerta = componenteEmAlerta;
    }

    public String getDataInicioAlerta() {
        return dataInicioAlerta;
    }

    public void setDataInicioAlerta(String dataInicioAlerta) {
        this.dataInicioAlerta = dataInicioAlerta;
    }

    public String getDataFimAlerta() {
        return dataFimAlerta;
    }

    public void setDataFimAlerta(String dataFimAlerta) {
        this.dataFimAlerta = dataFimAlerta;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "AlertaItemDto{tipoAlerta=" + tipoAlerta
                + ", unidadeGravada=" + unidadeGravada
                + ", componenteEmAlerta=" + componenteEmAlerta
                + ", dataInicioAlerta=" + dataInicioAlerta
                + ", dataFimAlerta=" + dataFimAlerta + "}";
    }

}
