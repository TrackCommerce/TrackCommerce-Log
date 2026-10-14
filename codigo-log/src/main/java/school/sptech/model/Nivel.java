package school.sptech.model;

/**
 * Estado de um componente em relação aos limites configurados.
 *
 * NORMAL   -> abaixo do limite moderado (não gera alerta)
 * MODERADO -> igual ou acima do limite moderado e abaixo do crítico
 * CRITICO  -> igual ou acima do limite crítico
 */
public enum Nivel {
    NORMAL, MODERADO, CRITICO
}
