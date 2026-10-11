package school.sptech.cliente;

// Jackson (jackson-databind): ObjectMapper converte objeto Java <-> JSON.
import com.fasterxml.jackson.databind.ObjectMapper;

// java.net.http: HttpClient/HttpRequest/HttpResponse fazem requisições HTTP (nativo do Java 11+).
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// java.io / java.nio / java.time / java.util: exceção de I/O, charset UTF-8, tempo limite e Base64 (autenticação Basic).
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

// DTOs do projeto: moldes Java do JSON enviado/recebido pelo Jira.
import school.sptech.dto.CommentDto;
import school.sptech.dto.IssueFieldsDto;
import school.sptech.dto.IssueRequestDto;
import school.sptech.dto.IssueResponseDto;
import school.sptech.dto.IssueTypeDto;
import school.sptech.dto.PriorityDto;
import school.sptech.dto.ProjectDto;

// Modelos do projeto e utilitário de formatação de números.
import school.sptech.model.Nivel;
import school.sptech.model.Regra;
import school.sptech.util.Formatador;

/**
 * Cria issues e comentários no Jira Cloud (REST API v2, autenticação Basic = e-mail:token).
 *
 * As credenciais (URL, e-mail, token e chave do projeto) vêm de cada Regra, pois cada empresa
 * pode ter o seu próprio Jira (tabela apijira).
 */
public class Jira {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Cliente HTTP reutilizado em todas as chamadas.
    private final HttpClient httpClient;

    // Converte os DTOs em JSON e a resposta do Jira em DTO.
    private final ObjectMapper objectMapper;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Cria o cliente HTTP (30 s de tempo limite para conectar) e o conversor de JSON. */
    public Jira() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    // ==================================================================
    // MÉTODOS
    // ==================================================================

    /** Cria o chamado e devolve a chave dele (ex.: TRK-123). */
    public String criarIssue(Regra regra, Nivel nivel, Double valor) throws IOException, InterruptedException {
        // Prioridade: crítico vira "Highest"; moderado vira "Medium".
        String nomePrioridade = "Medium";
        if (nivel == Nivel.CRITICO) {
            nomePrioridade = "Highest";
        }

        String titulo = "[" + nivel.name() + "] " + regra.getComponente() + " em " + regra.getInstancia();
        String descricao = "Instância: " + regra.getInstancia() + "\n"
                + "Componente: " + regra.getComponente() + "\n"
                + "Valor medido: " + Formatador.decimal(valor, 2) + " " + regra.getUnidade() + "\n"
                + "Limite moderado: " + Formatador.decimal(regra.getModerado(), 1)
                + " | crítico: " + Formatador.decimal(regra.getCritico(), 1);

        // Monta o corpo da requisição usando os DTOs (o Jackson transforma em JSON ao enviar).
        IssueFieldsDto campos = new IssueFieldsDto(
                new ProjectDto(regra.getJiraProjectKey()),
                new IssueTypeDto("Task"),
                new PriorityDto(nomePrioridade),
                titulo,
                descricao);
        IssueRequestDto requisicao = new IssueRequestDto(campos);

        HttpResponse<String> resposta = enviar(regra, "/rest/api/2/issue", requisicao);
        if (resposta.statusCode() != 201) {
            throw new IOException("Jira: " + resposta.statusCode() + " " + resposta.body());
        }

        IssueResponseDto criada = objectMapper.readValue(resposta.body(), IssueResponseDto.class);
        return criada.getKey();
    }

    /** Comenta no chamado que o componente voltou ao normal. */
    public void comentar(Regra regra, String chaveIssue, String texto) throws IOException, InterruptedException {
        if (Formatador.textoVazio(chaveIssue)) {
            return;
        }
        HttpResponse<String> resposta = enviar(regra, "/rest/api/2/issue/" + chaveIssue + "/comment", new CommentDto(texto));
        if (resposta.statusCode() != 201) {
            throw new IOException("Jira comentário: " + resposta.statusCode() + " " + resposta.body());
        }
    }

    /** Monta o cabeçalho "Basic base64(email:token)" exigido pelo Jira Cloud. */
    private String autenticacao(Regra regra) {
        String credencial = regra.getJiraEmail() + ":" + regra.getJiraToken();
        return "Basic " + Base64.getEncoder().encodeToString(credencial.getBytes(StandardCharsets.UTF_8));
    }

    /** Envia um POST com o corpo em JSON para o endpoint informado e devolve a resposta. */
    private HttpResponse<String> enviar(Regra regra, String caminho, Object corpo) throws IOException, InterruptedException {
        String json = objectMapper.writeValueAsString(corpo);

        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create(regra.getJiraUrl() + caminho))
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", autenticacao(regra))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return httpClient.send(requisicao, HttpResponse.BodyHandlers.ofString());
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
        return "Jira{httpClient=" + httpClient
                + ", objectMapper=" + objectMapper + "}";
    }

}
