package school.sptech.cliente;

// AWS SDK v2 (auth): descobre as credenciais da AWS automaticamente (variáveis de ambiente, arquivo ~/.aws, perfil da EC2...).
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;

// AWS SDK v2 (regions): representa a região da AWS onde o bucket está (ex.: us-east-1).
import software.amazon.awssdk.regions.Region;

// AWS SDK v2 (s3): cliente que conversa com o Amazon S3 (listar, baixar, enviar arquivos).
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.core.sync.RequestBody;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

// Configuração do projeto (variáveis de ambiente).
import school.sptech.config.Config;

/**
 * Cria o cliente do Amazon S3 usado para LER o Gold gerado pelo ETL.
 *
 * Criar o S3Client não abre conexão: o contato com a AWS só acontece quando um arquivo é pedido.
 * Por isso, mesmo sem credenciais, o programa continua funcionando lendo da pasta local.
 */
public class S3Provider {

    // ==================================================================
    // ATRIBUTOS
    // ==================================================================

    // Provedor de credenciais usado pelo SDK.
    private final AwsCredentialsProvider credentials;

    // Região da AWS (AWS_REGION).
    private final Region region;

    // ==================================================================
    // CONSTRUTORES
    // ==================================================================

    /** Inicializa as credenciais com a cadeia padrão da AWS e a região vinda da configuração. */
    public S3Provider(Config config) {
        this.credentials = DefaultCredentialsProvider.create();
        this.region = Region.of(config.getAwsRegion());
    }

    // ==================================================================
    // MÉTODOS
    // ==================================================================

    /** Constrói e devolve o cliente S3 (builder -> região -> credenciais -> build). */
    public S3Client getS3Client() {
        return S3Client.builder()
                .region(region)
                .credentialsProvider(credentials)
                .build();
    }

    /** Recupera o histórico; se não existe no bucket, remove uma cópia local antiga. */
    public boolean baixarSeExistir(String bucket, String chave, Path destino) throws IOException {
        try (S3Client s3 = getS3Client()) {
            try {
                byte[] dados = s3.getObjectAsBytes(GetObjectRequest.builder()
                        .bucket(bucket).key(chave).build()).asByteArray();
                if (destino.getParent() != null) Files.createDirectories(destino.getParent());
                Files.write(destino, dados);
                return true;
            } catch (NoSuchKeyException e) {
                Files.deleteIfExists(destino);
                return false;
            }
        }
    }

    /** Publica o JSON na Gold. Requer s3:PutObject. */
    public void enviarJson(String bucket, String chave, Path origem) {
        try (S3Client s3 = getS3Client()) {
            s3.putObject(PutObjectRequest.builder().bucket(bucket).key(chave)
                    .contentType("application/json").build(), RequestBody.fromFile(origem));
        }
    }

    // ==================================================================
    // GETTERS & SETTERS
    // ==================================================================

    public AwsCredentialsProvider getCredentials() {
        return credentials;
    }

    public Region getRegion() {
        return region;
    }

    // ==================================================================
    // TOSTRING
    // ==================================================================

    @Override
    public String toString() {
        return "S3Provider{region=" + region + "}";
    }

}
