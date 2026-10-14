package school.sptech.model;

// java.time: LocalDateTime representa uma data e hora (sem fuso), usada para comparar instantes da série.
import java.time.LocalDateTime;

/**
 * Um ponto da série histórica: instante + valor já convertido para a unidade do parâmetro.
 */
public class Ponto {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Instante da medição (coluna "timestamp" do Gold).
    private LocalDateTime ts;

    // Valor medido, já multiplicado pelo fator da Fonte.
    private Double valor;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public Ponto() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public Ponto(LocalDateTime ts, Double valor) {
        this.ts = ts;
        this.valor = valor;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public LocalDateTime getTs() {
        return ts;
    }

    public void setTs(LocalDateTime ts) {
        this.ts = ts;
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
        return "Ponto{ts=" + ts
                + ", valor=" + valor + "}";
    }

}
