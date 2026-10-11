package school.sptech.dto;

/**
 * DTO: representa como objeto Java o JSON { "key": "..." } do projeto do Jira.
 */
public class ProjectDto {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Chave do projeto (ex.: TRK).
    private String key;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public ProjectDto() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public ProjectDto(String key) {
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
        return "ProjectDto{key=" + key + "}";
    }

}
