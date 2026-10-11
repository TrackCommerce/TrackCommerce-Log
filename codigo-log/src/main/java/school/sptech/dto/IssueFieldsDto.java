package school.sptech.dto;

/**
 * DTO: representa como objeto Java o JSON { "project": {...}, "issuetype": {...}, "priority": {...},
 * "summary": "...", "description": "..." } enviado ao criar uma issue.
 */
public class IssueFieldsDto {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Projeto onde a issue será criada.
    private ProjectDto project;

    // Tipo da issue.
    private IssueTypeDto issuetype;

    // Prioridade da issue.
    private PriorityDto priority;

    // Título da issue.
    private String summary;

    // Descrição em texto simples (API v2).
    private String description;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public IssueFieldsDto() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public IssueFieldsDto(ProjectDto project, IssueTypeDto issuetype, PriorityDto priority, String summary, String description) {
        this.project = project;
        this.issuetype = issuetype;
        this.priority = priority;
        this.summary = summary;
        this.description = description;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

    public IssueTypeDto getIssuetype() {
        return issuetype;
    }

    public void setIssuetype(IssueTypeDto issuetype) {
        this.issuetype = issuetype;
    }

    public PriorityDto getPriority() {
        return priority;
    }

    public void setPriority(PriorityDto priority) {
        this.priority = priority;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "IssueFieldsDto{project=" + project
                + ", issuetype=" + issuetype
                + ", priority=" + priority
                + ", summary=" + summary
                + ", description=" + description + "}";
    }

}
