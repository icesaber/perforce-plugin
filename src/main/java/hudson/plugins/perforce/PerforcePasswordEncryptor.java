package hudson.plugins.perforce;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * @author stimmslocal
 */
public class PerforcePasswordEncryptor {

    private static final String keyString = "405kqo0gc20f9985142rj17779v4922568on29pwj92toqt884";
    private static final String ENCRYPTION_PREFFACE = "0f0kqlwa";

    public PerforcePasswordEncryptor() {
    }

    public boolean appearsToBeAnEncryptedPassword(String toCheck) {
        return toCheck.startsWith(ENCRYPTION_PREFFACE);
    }

    public String encryptString(String toEncrypt) {
        if (toEncrypt == null || toEncrypt.trim().length() == 0)
            return "";

        byte[] keyBytes = null;
        try {
            keyBytes = keyString.getBytes("UTF8");
        } catch (UnsupportedEncodingException uee) {
            System.err.println(uee);
        }
        KeySpec keySpec = null;
        try {
            keySpec = new DESKeySpec(keyBytes);
        } catch (InvalidKeyException ike) {
            System.err.println("Unable to create DES keyspec " + ike);
        }

        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("DES");
        } catch (NoSuchAlgorithmException nsal) {
            System.err.println(nsal);
        }
        SecretKey key = null;
        try {
            key = factory.generateSecret(keySpec);
        } catch (InvalidKeySpecException ikse) {
            System.err.print("Unable to generate secret key: " + ikse);
        }

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DES");
        } catch (NoSuchAlgorithmException nsal) {
            System.err.println(nsal);
        } catch (NoSuchPaddingException nspe) {
            System.err.println(nspe);
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (InvalidKeyException ike) {
            System.err.print("Unable to init cipher: " + ike);
        }
        byte[] cleartext = toEncrypt.getBytes();
        byte[] encryptedtext = null;
        try {
            encryptedtext = cipher.doFinal(cleartext);
        } catch (IllegalBlockSizeException ibse) {
            System.err.println(ibse);
        } catch (BadPaddingException bpe) {
            System.err.println(bpe);
        }

        BASE64Encoder encoder = new BASE64Encoder();
        return ENCRYPTION_PREFFACE + encoder.encode(encryptedtext);
    }

    public String decryptString(String toDecrypt) {
        if (toDecrypt == null || toDecrypt.length() == 0)
            return "";

        byte[] keyBytes = null;
        try {
           keyBytes = keyString.getBytes("UTF8");
        } catch (UnsupportedEncodingException uee) {
            System.err.println(uee);
        }
        KeySpec keySpec = null;
        try {
            keySpec = new DESKeySpec(keyBytes);
        } catch (InvalidKeyException ike) {
            System.err.println("Unable to create DES keyspec " + ike);
        }

        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("DES");
        } catch (NoSuchAlgorithmException nsal) {
            System.err.println(nsal);
        }
        SecretKey key = null;
        try {
            key = factory.generateSecret(keySpec);
        } catch (InvalidKeySpecException ikse) {
            System.err.print("Unable to generate secret key: " + ikse);
        }

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DES");
        } catch (NoSuchAlgorithmException nsal) {
            System.err.println(nsal);
        } catch (NoSuchPaddingException nspe) {
            System.err.println(nspe);
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }
        catch (InvalidKeyException ike) {
            System.err.print("Unable to init cipher: " + ike);
        }
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] encryptedtext = null;
        try {
            String processedToDecrypt = toDecrypt.replaceFirst(ENCRYPTION_PREFFACE, "");
            encryptedtext = decoder.decodeBuffer(processedToDecrypt);
        } catch (IOException ioe) {
            System.err.println("Unable to decode buffer " + toDecrypt);
        }
        byte[] cleartext = null;
        try {
            cleartext = cipher.doFinal(encryptedtext);
        } catch (IllegalBlockSizeException ibse) {
            System.err.println(ibse);
        } catch (BadPaddingException bpe) {
            System.err.println(bpe);
        }

        return convertBytesToString(cleartext);
    }

    private static String convertBytesToString(byte[] bytes) {
        if (bytes == null)
            return "";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            stringBuffer.append((char) bytes[i]);
        }
        return stringBuffer.toString();
    }

}
