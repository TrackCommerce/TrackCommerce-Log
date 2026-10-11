package school.sptech.cliente;

// Jackson (jackson-databind): ObjectMapper converte objeto Java <-> JSON.
import com.fasterxml.jackson.databind.ObjectMapper;

// java.net.http: HttpClient/HttpRequest/HttpResponse fazem requisições HTTP (nativo do Java 11+).
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// java.io / java.time: exceção de I/O e tempo limite das requisições.
import java.io.IOException;
import java.time.Duration;

// DTO do projeto e utilitário de validação de texto.
import school.sptech.dto.SlackMessageDto;
import school.sptech.util.Formatador;

/**
 * Envia mensagens para um canal do Slack usando Incoming Webhook (uma URL secreta por empresa).
 */
public class Slack {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Cliente HTTP reutilizado em todas as chamadas.
    private final HttpClient httpClient;

    // Converte o DTO da mensagem em JSON.
    private final ObjectMapper objectMapper;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Cria o cliente HTTP (30 s de tempo limite para conectar) e o conversor de JSON. */
    public Slack() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // ==================================================================
    // MÉTODOS
    // ==================================================================

    /** Envia o texto para o webhook. Se o webhook estiver vazio, não faz nada. */
    public void enviar(String webhook, String texto) throws IOException, InterruptedException {
        if (Formatador.textoVazio(webhook)) {
            return;
        }

        String json = objectMapper.writeValueAsString(new SlackMessageDto(texto));
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create(webhook))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> resposta = httpClient.send(requisicao, HttpResponse.BodyHandlers.ofString());
        if (resposta.statusCode() != 200) {
            throw new IOException("Slack: " + resposta.statusCode() + " " + resposta.body());
        }
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "Slack{httpClient=" + httpClient
                + ", objectMapper=" + objectMapper + "}";
    }

}
