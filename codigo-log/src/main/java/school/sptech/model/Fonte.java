package school.sptech.model;

/**
 * Indica ONDE encontrar a métrica dentro do JSON Gold gerado pelo ETL.
 *
 * Exemplo: o componente "CPU" está na seção "cpu", campo "cpu_percent", fator 1.
 */
public class Fonte {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Seção do JSON Gold (cpu, ram, disco, rede).
    private String secao;

    // Nome do campo dentro de "historico" daquela seção.
    private String campo;

    // Multiplicador que converte a unidade do ETL (ex.: bytes/s) para a unidade do parâmetro (ex.: MB/s).
    private Double fator;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public Fonte() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public Fonte(String secao, String campo, Double fator) {
        this.secao = secao;
        this.campo = campo;
        this.fator = fator;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public String getSecao() {
        return secao;
    }

    public void setSecao(String secao) {
        this.secao = secao;
    }

    public String getCampo() {
        return campo;
    }

    public void setCampo(String campo) {
        this.campo = campo;
    }

    public Double getFator() {
        return fator;
    }

    public void setFator(Double fator) {
        this.fator = fator;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "Fonte{secao=" + secao
                + ", campo=" + campo
                + ", fator=" + fator + "}";
    }

}
