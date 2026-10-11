package school.sptech.dto;

/**
 * DTO: representa como objeto Java o JSON { "name": "..." } do tipo da issue (Task, Bug, Story...).
 */
public class IssueTypeDto {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Nome do tipo da issue.
    private String name;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public IssueTypeDto() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public IssueTypeDto(String name) {
        this.name = name;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "IssueTypeDto{name=" + name + "}";
    }

}
