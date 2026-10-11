package school.sptech.util;

/**
 * Funções utilitárias de texto e número escritas "na mão" (somente for, if/else e contas matemáticas),
 * para não depender de métodos prontos como String.format, String.isBlank, Math.round, etc.
 *
 * Classe sem atributos: todos os métodos são estáticos, por isso não há construtores,
 * getters/setters nem toString.
 */
public final class Formatador {

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor privado: a classe é apenas um conjunto de métodos estáticos e não deve ser instanciada. */
    private Formatador() {
    }

    // ==================================================================
    // MÉTODOS
    // ==================================================================

    /**
     * Diz se um texto é nulo, vazio ou só tem espaços (equivale ao isBlank, mas com for + if).
     * Todo caractere com código menor ou igual a ' ' (espaço) é tratado como "espaço em branco".
     */
    public static Boolean textoVazio(String texto) {
        if (texto == null) {
            return true;
        }
        for (Integer i = 0; i < texto.length(); i++) {
            if (texto.charAt(i) > ' ') {
                return false; // achou um caractere visível: o texto não é vazio
            }
        }
        return true;
    }

    /**
     * Formata um número com a quantidade de casas decimais pedida, usando ponto como separador
     * (substitui o String.format("%.1f", valor)). Arredonda "meio para cima".
     *
     * Exemplo: decimal(72.456, 1) -> "72.5"
     */
    public static String decimal(Double valor, Integer casas) {
        if (valor == null) {
            return "0";
        }

        // Guarda o sinal à parte para arredondar sempre o valor positivo.
        Boolean negativo = valor < 0;
        Double absoluto = valor;
        if (negativo) {
            absoluto = -valor;
        }

        // fator = 10^casas (feito com for em vez de Math.pow).
        Long fator = 1L;
        for (Integer i = 0; i < casas; i++) {
            fator = fator * 10L;
        }

        // Multiplica, soma 0.5 e descarta a parte fracionária (cast para long) = arredondamento.
        Long escalado = (long) (absoluto * fator + 0.5); // o cast (long) é o que descarta a parte fracionária
        Long parteInteira = escalado / fator;
        Long parteDecimal = escalado % fator;

        StringBuilder texto = new StringBuilder();
        if (negativo && escalado > 0) {
            texto.append("-");
        }
        texto.append(parteInteira);

        if (casas > 0) {
            texto.append(".");
            // Completa com zeros à esquerda (ex.: 5 com 2 casas -> "05").
            String digitos = String.valueOf(parteDecimal);
            for (Integer i = digitos.length(); i < casas; i++) {
                texto.append("0");
            }
            texto.append(digitos);
        }
        return texto.toString();
    }
}
