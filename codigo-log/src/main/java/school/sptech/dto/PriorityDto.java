package school.sptech.dto;

/**
 * DTO: representa como objeto Java o JSON { "name": "..." } da prioridade da issue (Highest, Medium...).
 */
public class PriorityDto {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Nome da prioridade.
    private String name;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public PriorityDto() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public PriorityDto(String name) {
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
        return "PriorityDto{name=" + name + "}";
    }

}
