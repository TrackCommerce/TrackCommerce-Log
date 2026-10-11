package school.sptech.service;

import school.sptech.model.Fonte;

/**
 * Traduz o nome do componente cadastrado no banco (ex.: "CPU") para o lugar onde a métrica
 * está no JSON Gold (seção, campo e fator de conversão de unidade).
 *
 * Classe sem atributos de instância: só tem constante e método estático.
 * Foi escrita com if/else (em vez de switch/Map) para ficar simples de ler e de dar manutenção.
 */
public final class FonteResolver {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    /** Componentes que o ETL consegue entregar no Gold (os demais são ignorados com um aviso). */
    public static final String[] COMPONENTES_SUPORTADOS = {
            "CPU", "Memoria RAM", "Armazenamento",
            "Leitura de Disco", "Escrita de Disco",
            "Rede - Download", "Rede - Upload"
    };

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor privado: a classe não deve ser instanciada. */
    private FonteResolver() {
    }

    // ==================================================================
    // MÉTODOS
    // ==================================================================

    /** Devolve a Fonte do componente, ou null (com aviso) se o nome não for conhecido. */
    public static Fonte para(String componente) {
        if (componente == null) {
            return null;
        }

        if (componente.equals("CPU")) {
            return new Fonte("cpu", "cpu_percent", 1.0);
        }
        if (componente.equals("Memoria RAM")) {
            return new Fonte("ram", "ram_percent", 1.0);
        }
        if (componente.equals("Armazenamento")) {
            return new Fonte("disco", "disco_percent", 1.0);
        }

        // O ETL grava bytes/s; o parâmetro é cadastrado em MB/s -> divide por 1.000.000.
        if (componente.equals("Leitura de Disco")) {
            return new Fonte("disco", "disco_leitura_bytes_s", 1.0 / 1000000.0);
        }
        if (componente.equals("Escrita de Disco")) {
            return new Fonte("disco", "disco_escrita_bytes_s", 1.0 / 1000000.0);
        }

        // O ETL grava bytes/s; o parâmetro é cadastrado em Mbps -> bytes x 8 bits / 1.000.000.
        if (componente.equals("Rede - Download")) {
            return new Fonte("rede", "rede_download_bytes_s", 8.0 / 1000000.0);
        }
        if (componente.equals("Rede - Upload")) {
            return new Fonte("rede", "rede_upload_bytes_s", 8.0 / 1000000.0);
        }

        System.err.println("[AVISO] Componente sem mapeamento: " + componente);
        return null;
    }
}
