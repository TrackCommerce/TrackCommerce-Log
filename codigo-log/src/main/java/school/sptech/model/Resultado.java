package school.sptech.model;

// java.util: ArrayList/List guardam coleções de objetos em memória.
import java.util.ArrayList;
import java.util.List;

/**
 * Resultado de um ciclo do AlertaStore: quais alertas abriram e quais foram encerrados.
 */
public class Resultado {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Alertas novos que ainda estão sem data de fim.
    private List<Evento> abertos = new ArrayList<>();

    // Alertas que voltaram ao normal neste ciclo.
    private List<Evento> encerrados = new ArrayList<>();

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public Resultado() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public Resultado(List<Evento> abertos, List<Evento> encerrados) {
        this.abertos = abertos;
        this.encerrados = encerrados;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public List<Evento> getAbertos() {
        return abertos;
    }

    public void setAbertos(List<Evento> abertos) {
        this.abertos = abertos;
    }

    public List<Evento> getEncerrados() {
        return encerrados;
    }

    public void setEncerrados(List<Evento> encerrados) {
        this.encerrados = encerrados;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "Resultado{abertos=" + abertos
                + ", encerrados=" + encerrados + "}";
    }

}
