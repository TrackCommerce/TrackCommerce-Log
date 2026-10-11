package trackcommerce.com;

public class Bucket {

    /**
     *    ▄▖▗ ▗
     *    ▌▌▜▘▜▘▛▘
     *    ▛▌▐▖▐▖▌
     *
     */

    private String nomeBucket;
    private String keyId;
    private String securityKey;
    private String token;

    /**
     *    ▄▖      ▗       ▗
     *    ▌ ▛▌▛▌▛▘▜▘▛▘▌▌▛▘▜▘▛▌▛▘▛▘
     *    ▙▖▙▌▌▌▄▌▐▖▌ ▙▌▙▖▐▖▙▌▌ ▄▌
     *
     */

    public Bucket() {
    }

    public Bucket(String nomeBucket, String keyId, String securityKey, String token) {
        this.nomeBucket = nomeBucket;
        this.keyId = keyId;
        this.securityKey = securityKey;
        this.token = token;
    }

    /**
     *    ▖  ▖  ▗ ▌    ▌
     *    ▛▖▞▌█▌▜▘▛▌▛▌▛▌▛▘
     *    ▌▝ ▌▙▖▐▖▌▌▙▌▙▌▄▌
     *
     */


    /**
     *    ▄▖  ▗ ▗         ▗ ▗   ▄▖  ▗ ▗
     *    ▌ █▌▜▘▜▘█▌▛▘▛▘  ▚▘▚▘  ▚ █▌▜▘▜▘█▌▛▘▛▘
     *    ▙▌▙▖▐▖▐▖▙▖▌ ▄▌  ▚▌▚▌  ▄▌▙▖▐▖▐▖▙▖▌ ▄▌
     *
     */

    public String getNomeBucket() {
        return nomeBucket;
    }


    public void setNomeBucket(String nomeBucket) {
        this.nomeBucket = nomeBucket;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     *    ▄▖    ▄▖▗   ▘
     *    ▐ ▛▌  ▚ ▜▘▛▘▌▛▌▛▌
     *    ▐ ▙▌  ▄▌▐▖▌ ▌▌▌▙▌
     *                   ▄▌
     */

    @Override
    public String toString() {
        return "Bucket{" +
                "nomeBucket='" + nomeBucket + '\'' +
                ", keyId='" + keyId + '\'' +
                ", securityKey='" + securityKey + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
