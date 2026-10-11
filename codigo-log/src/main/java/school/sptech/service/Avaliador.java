package school.sptech.service;

import school.sptech.model.Nivel;

/**
 * Regra pura de negócio: recebe um valor e os limites e devolve o nível do alerta.
 *
 * Classe sem atributos: só tem método estático, por isso não há getters/setters nem toString.
 */
public final class Avaliador {

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor privado: a classe não deve ser instanciada. */
    private Avaliador() {
    }

    // ==================================================================
    // MÉTODOS
    // ==================================================================

    /**
     * Compara o valor com os limites. A ordem do if importa: o crítico é verificado primeiro,
     * porque todo valor crítico também é maior que o moderado.
     */
    public static Nivel avaliar(Double valor, Double moderado, Double critico) {
        if (valor >= critico) {
            return Nivel.CRITICO;
        }
        if (valor >= moderado) {
            return Nivel.MODERADO;
        }
        return Nivel.NORMAL;
    }
}
