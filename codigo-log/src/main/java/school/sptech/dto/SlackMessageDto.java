package school.sptech.dto;

/**
 * DTO: representa como objeto Java o JSON { "text": "..." } aceito pelo Incoming Webhook do Slack.
 */
public class SlackMessageDto {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Texto da mensagem.
    private String text;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public SlackMessageDto() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public SlackMessageDto(String text) {
        this.text = text;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "SlackMessageDto{text=" + text + "}";
    }

}
