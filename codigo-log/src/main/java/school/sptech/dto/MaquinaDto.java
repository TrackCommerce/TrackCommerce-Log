package school.sptech.dto;

// Jackson (jackson-annotations): anotações que dizem como cada atributo vira uma chave no JSON.
import com.fasterxml.jackson.annotation.JsonProperty;

// java.util: ArrayList/List guardam coleções de objetos em memória.
import java.util.ArrayList;
import java.util.List;

/**
 * DTO: uma máquina dentro do alertas.json, com todos os alertas dela.
 */
public class MaquinaDto {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Identificador da máquina.
    @JsonProperty("uuid")
    private String uuid;

    // Razão social da empresa dona da máquina.
    @JsonProperty("empresa")
    private String empresa;

    // Quantidade de alertas com DataFimAlerta nula.
    @JsonProperty("alertasSemFim")
    private Integer alertasSemFim = 0;

    // Todos os alertas já registrados para a máquina.
    @JsonProperty("alertas")
    private List<AlertaItemDto> alertas = new ArrayList<>();

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public MaquinaDto() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public MaquinaDto(String uuid, String empresa, Integer alertasSemFim, List<AlertaItemDto> alertas) {
        this.uuid = uuid;
        this.empresa = empresa;
        this.alertasSemFim = alertasSemFim;
        this.alertas = alertas;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Integer getAlertasSemFim() {
        return alertasSemFim;
    }

    public void setAlertasSemFim(Integer alertasSemFim) {
        this.alertasSemFim = alertasSemFim;
    }

    public List<AlertaItemDto> getAlertas() {
        return alertas;
    }

    public void setAlertas(List<AlertaItemDto> alertas) {
        this.alertas = alertas;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "MaquinaDto{uuid=" + uuid
                + ", empresa=" + empresa
                + ", alertasSemFim=" + alertasSemFim
                + ", alertas=" + alertas + "}";
    }

}
