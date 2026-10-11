package school.sptech.dto;

/**
 * DTO: representa como objeto Java o JSON { "fields": { ... } } enviado ao criar uma issue.
 */
public class IssueRequestDto {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Campos da issue.
    private IssueFieldsDto fields;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public IssueRequestDto() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public IssueRequestDto(IssueFieldsDto fields) {
        this.fields = fields;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public IssueFieldsDto getFields() {
        return fields;
    }

    public void setFields(IssueFieldsDto fields) {
        this.fields = fields;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "IssueRequestDto{fields=" + fields + "}";
    }

}
