package school.sptech.model;

// java.time: LocalDateTime representa uma data e hora (sem fuso).
import java.time.LocalDateTime;

/**
 * Trecho contínuo de pontos que ficaram no mesmo nível de alerta (usado internamente pelo AlertaStore).
 */
public class Intervalo {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Nível do trecho (MODERADO ou CRITICO).
    private Nivel nivel;

    // Instante do primeiro ponto do trecho.
    private LocalDateTime inicio;

    // Instante em que o nível mudou (null enquanto o alerta continua aberto).
    private LocalDateTime fim;

    // Maior valor medido dentro do trecho.
    private Double pico;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio. */
    public Intervalo() {
    }

    /** Abre um trecho novo: o pico começa com o primeiro valor e o fim fica nulo (alerta aberto). */
    public Intervalo(Nivel nivel, LocalDateTime inicio, Double valor) {
        this.nivel = nivel;
        this.inicio = inicio;
        this.pico = valor;
        this.fim = null;
    }

    /** Construtor completo: recebe todos os atributos. */
    public Intervalo(Nivel nivel, LocalDateTime inicio, LocalDateTime fim, Double pico) {
        this.nivel = nivel;
        this.inicio = inicio;
        this.fim = fim;
        this.pico = pico;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public Nivel getNivel() {
        return nivel;
    }

    public void setNivel(Nivel nivel) {
        this.nivel = nivel;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public void setFim(LocalDateTime fim) {
        this.fim = fim;
    }

    public Double getPico() {
        return pico;
    }

    public void setPico(Double pico) {
        this.pico = pico;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "Intervalo{nivel=" + nivel
                + ", inicio=" + inicio
                + ", fim=" + fim
                + ", pico=" + pico + "}";
    }

}
