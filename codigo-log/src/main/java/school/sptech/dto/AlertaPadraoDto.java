package school.sptech.dto;

// Jackson (jackson-annotations): anotações que dizem como cada atributo vira uma chave no JSON.
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO: limites padrão de UM componente (usados quando ele não tem parâmetro vinculado no banco).
 * É também o formato de cada item do arquivo alertas_padrao.json.
 */
public class AlertaPadraoDto {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Nome do componente, igual ao da tabela componentes (ex.: CPU).
    @JsonProperty("componente")
    private String componente;

    // Unidade dos limites (%, MB/s, Mbps).
    @JsonProperty("unidade")
    private String unidade;

    // Limite a partir do qual o alerta é MODERADO.
    @JsonProperty("moderado")
    private Double moderado;

    // Limite a partir do qual o alerta é CRITICO.
    @JsonProperty("critico")
    private Double critico;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public AlertaPadraoDto() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public AlertaPadraoDto(String componente, String unidade, Double moderado, Double critico) {
        this.componente = componente;
        this.unidade = unidade;
        this.moderado = moderado;
        this.critico = critico;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public String getComponente() {
        return componente;
    }

    public void setComponente(String componente) {
        this.componente = componente;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public Double getModerado() {
        return moderado;
    }

    public void setModerado(Double moderado) {
        this.moderado = moderado;
    }

    public Double getCritico() {
        return critico;
    }

    public void setCritico(Double critico) {
        this.critico = critico;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "AlertaPadraoDto{componente=" + componente
                + ", unidade=" + unidade
                + ", moderado=" + moderado
                + ", critico=" + critico + "}";
    }

}
