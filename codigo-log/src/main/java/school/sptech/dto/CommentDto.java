package school.sptech.dto;

/**
 * DTO: representa como objeto Java o JSON { "body": "..." } de um comentário em uma issue.
 */
public class CommentDto {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Texto do comentário.
    private String body;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public CommentDto() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public CommentDto(String body) {
        this.body = body;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "CommentDto{body=" + body + "}";
    }

}
