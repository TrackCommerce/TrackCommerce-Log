package school.sptech.model;

/**
 * Máquina descoberta ao varrer a pasta Gold local (usada no modo sem banco de dados).
 * Os dados vêm do próprio JSON gerado pelo ETL (seções "empresa" e "maquina").
 */
public class MaquinaGold {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Nome da pasta da empresa dentro do Gold (= empresa_id já "seguro" para nome de pasta).
    private String empresaId;

    // Razão social registrada no Gold (pode ser null).
    private String empresaNome;

    // Identificador original da máquina (campo maquina.identifier do Gold).
    private String identificador;

    // Nome amigável da VM (campo maquina.nome do Gold, pode ser null).
    private String nome;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Construtor vazio (necessário para o Jackson e para montar o objeto passo a passo). */
    public MaquinaGold() {
    }

    /** Construtor completo: recebe todos os atributos. */
    public MaquinaGold(String empresaId, String empresaNome, String identificador, String nome) {
        this.empresaId = empresaId;
        this.empresaNome = empresaNome;
        this.identificador = identificador;
        this.nome = nome;
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public String getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(String empresaId) {
        this.empresaId = empresaId;
    }

    public String getEmpresaNome() {
        return empresaNome;
    }

    public void setEmpresaNome(String empresaNome) {
        this.empresaNome = empresaNome;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "MaquinaGold{empresaId=" + empresaId
                + ", empresaNome=" + empresaNome
                + ", identificador=" + identificador
                + ", nome=" + nome + "}";
    }

}
