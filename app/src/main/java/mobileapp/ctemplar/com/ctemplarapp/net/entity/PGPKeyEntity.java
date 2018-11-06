package mobileapp.ctemplar.com.ctemplarapp.net.entity;

public class PGPKeyEntity {

    private String publicKey;
    private String privateKey;

    public PGPKeyEntity(String publicKey, String privateKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }
}
