package school.sptech.dto;

// Jackson (jackson-annotations): anotações que dizem como cada atributo vira uma chave no JSON.
import com.fasterxml.jackson.annotation.JsonProperty;

// java.util: ArrayList/List guardam coleções de objetos em memória.
import java.util.ArrayList;
import java.util.List;

/**
 * DTO: bloco "AlertasDias" do alertas.json (data da última atualização + todas as máquinas).
 */
public class DiasDto {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Data da última atualização do arquivo: yyyy-MM-dd_HH-mm.
    @JsonProperty("data")
    private String data;

    // Quantas máquinas têm pelo menos um alerta aberto.
    @JsonProperty("maquinasComAlertasSemFim")
    private Integer maquinasComAlertasSemFim = 0;

    // Máquinas monitoradas.
    @JsonProperty("maquinas")
    private List<MaquinaDto> maquinas = new ArrayList<>();

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public DiasDto() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public DiasDto(String data, Integer maquinasComAlertasSemFim, List<MaquinaDto> maquinas) {
        this.data = data;
        this.maquinasComAlertasSemFim = maquinasComAlertasSemFim;
        this.maquinas = maquinas;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Integer getMaquinasComAlertasSemFim() {
        return maquinasComAlertasSemFim;
    }

    public void setMaquinasComAlertasSemFim(Integer maquinasComAlertasSemFim) {
        this.maquinasComAlertasSemFim = maquinasComAlertasSemFim;
    }

    public List<MaquinaDto> getMaquinas() {
        return maquinas;
    }

    public void setMaquinas(List<MaquinaDto> maquinas) {
        this.maquinas = maquinas;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "DiasDto{data=" + data
                + ", maquinasComAlertasSemFim=" + maquinasComAlertasSemFim
                + ", maquinas=" + maquinas + "}";
    }

}
