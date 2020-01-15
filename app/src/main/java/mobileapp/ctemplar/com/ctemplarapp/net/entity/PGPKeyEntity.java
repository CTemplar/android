package mobileapp.ctemplar.com.ctemplarapp.net.entity;

public class PGPKeyEntity {

    private String publicKey;
    private String privateKey;
    private String fingerprint;

    public PGPKeyEntity() {

    }

    public PGPKeyEntity(String publicKey, String privateKey, String fingerprint) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.fingerprint = fingerprint;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
