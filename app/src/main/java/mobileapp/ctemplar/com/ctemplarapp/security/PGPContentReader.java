package mobileapp.ctemplar.com.ctemplarapp.security;

import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class PGPContentReader {
    private final JcaPGPObjectFactory jcaPGPObjectFactory;

    private PGPContentReader(JcaPGPObjectFactory jcaPGPObjectFactory) {
        this.jcaPGPObjectFactory = jcaPGPObjectFactory;
    }

    public static PGPContentReader read(byte[] content) throws IOException {
        InputStream in = new ByteArrayInputStream(content);
        in = PGPUtil.getDecoderStream(in);
        return new PGPContentReader(new JcaPGPObjectFactory(in));
    }

    public static PGPContentReader read(PGPPublicKeyEncryptedData publicKeyEncryptedData, PGPPrivateKey privateKey) throws PGPException {
        InputStream inputStream;
        inputStream = publicKeyEncryptedData.getDataStream(new BcPublicKeyDataDecryptorFactory(privateKey));
        return new PGPContentReader(new JcaPGPObjectFactory(inputStream));
    }

    public PGPEncryptedDataList getEncryptedDataList() {
        for (Object object : jcaPGPObjectFactory) {
            if (object instanceof PGPEncryptedDataList) {
                return (PGPEncryptedDataList) object;
            }
        }
        return null;
    }

    public List<PGPPublicKeyEncryptedData> getPublicKeyEncryptedDatum() {
        PGPEncryptedDataList list = getEncryptedDataList();
        if (list == null) {
            return null;
        }
        List<PGPPublicKeyEncryptedData> result = new ArrayList<>();
        for (PGPEncryptedData encryptedData : list) {
            if (encryptedData instanceof PGPPublicKeyEncryptedData) {
                result.add((PGPPublicKeyEncryptedData) encryptedData);
            }
        }
        return result;
    }

    public PGPLiteralData getUncompressedLiteralData() {
        for (Object object : jcaPGPObjectFactory) {
            if (object instanceof PGPCompressedData) {
                PGPCompressedData compressedData = (PGPCompressedData) object;
                InputStream compressedDataInputStream;
                try {
                    compressedDataInputStream = compressedData.getDataStream();
                } catch (PGPException e) {
                    Timber.e(e, "get dataStream from compressedData failed");
                    continue;
                }
                try {
                    object = new JcaPGPObjectFactory(compressedDataInputStream).nextObject();
                } catch (IOException e) {
                    Timber.e(e, "Failed to get next object from uncompressed object");
                    continue;
                }
            }
            if (object instanceof PGPLiteralData) {
                return ((PGPLiteralData) object);
            }
        }
        return null;
    }
}
