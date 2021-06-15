package mobileapp.ctemplar.com.ctemplarapp.settings.keys;

import mobileapp.ctemplar.com.ctemplarapp.repository.enums.KeyType;

public class GeneralizedMailboxKey {
    private final long id;
    private final String privateKey;
    private final String publicKey;
    private final String fingerprint;
    private final KeyType keyType;

    public GeneralizedMailboxKey(long id, String privateKey, String publicKey, String fingerprint, KeyType keyType) {
        this.id = id;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.fingerprint = fingerprint;
        this.keyType = keyType;
    }

    public long getId() {
        return id;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    public boolean isPrimary() {
        return id < 0;
    }
}
