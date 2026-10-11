package school.sptech.dto;

// Jackson (jackson-annotations): ignora campos da resposta do Jira que não declaramos aqui.
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO: resposta do Jira ao criar uma issue. Só nos interessa a chave (ex.: TRK-123);
 * os demais campos da resposta são ignorados.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueResponseDto {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Chave da issue criada.
    private String key;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public IssueResponseDto() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public IssueResponseDto(String key) {
        this.key = key;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "IssueResponseDto{key=" + key + "}";
    }

}
