package school.sptech.model;

/**
 * Alerta que acabou de ABRIR (ainda sem fim) ou que acabou de NORMALIZAR neste ciclo.
 * É o que a classe Main usa para decidir quando avisar o Slack e o Jira.
 */
public class Evento {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Regra (componente + limites + destinos) que originou o evento.
    private Regra regra;

    // Nível do alerta (MODERADO ou CRITICO).
    private Nivel nivel;

    // Pico medido durante o alerta (0 quando o evento é de normalização).
    private Double valor;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public Evento() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public Evento(Regra regra, Nivel nivel, Double valor) {
        this.regra = regra;
        this.nivel = nivel;
        this.valor = valor;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public Regra getRegra() {
        return regra;
    }

    public void setRegra(Regra regra) {
        this.regra = regra;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "Evento{regra=" + regra
                + ", nivel=" + nivel
                + ", valor=" + valor + "}";
    }

}
