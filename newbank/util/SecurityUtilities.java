package newbank.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtilities {

  // This is a static utility library for encoding and decoding information
  // passed over the socket interface.
  // The general pattern is for the client and server to exchange RSA public
  // keys. The data is encrypted using a symmetric AES key, and the AES key is
  // encrypted using the public RSA key. Then, the data can be decrypted using
  // the AES key, after that has been decrypted with the RSA private key.

  private static final String RSA = "RSA";
  private static final byte[] SALT = "SaLtYBoI69".getBytes();

  public static String getSecurePassword(String password) {
    String generatedPassword = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(SALT);
      byte[] bytes = md.digest(password.getBytes());
      generatedPassword = encode(bytes);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return generatedPassword;
  }

  // Generating public and private RSA keys
  public static KeyPair generateRSAKeyPair() {
    KeyPair keyPair = null;
    try {
      SecureRandom sr = new SecureRandom();
      KeyPairGenerator kpGen = KeyPairGenerator.getInstance(RSA);

      kpGen.initialize(2048, sr);
      keyPair = kpGen.generateKeyPair();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return keyPair;
  }

  // encrypt the AES key with RSA
  public static byte[] encryptRSA(SecretKey aesKey, PublicKey publicKey) {
    byte[] encryptedString = null;
    try {
      Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      encryptedString = cipher.doFinal(aesKey.getEncoded());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return encryptedString;
  }

  // decrypt the AES key with RSA
  public static SecretKey decryptRSA(byte[] data, PrivateKey privateKey) {
    SecretKey aesKey = null;
    try {
      Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
      cipher.init(Cipher.DECRYPT_MODE, privateKey);
      byte[] decryptedKey = cipher.doFinal(data);
      aesKey = new SecretKeySpec(decryptedKey, 0, decryptedKey.length, "AES");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return aesKey;
  }
  
  // Generate an AES key
  public static SecretKey generateAESSecretKey() {
    KeyGenerator keyGenerator;
    SecretKey key = null;
    try {
      keyGenerator = KeyGenerator.getInstance("AES");
      keyGenerator.init(128);
      key = keyGenerator.generateKey();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return key;
  }

  // Encrypt plain text with AES
  public static byte[] encryptAES(String data, SecretKey key) {
    byte[] encryptedBytes = null;
    try {
      byte[] dataInBytes = data.getBytes();
      Cipher encryptionCipher = Cipher.getInstance("AES");
      encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
      encryptedBytes = encryptionCipher.doFinal(dataInBytes);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return encryptedBytes;
  }

  // Decrypt AES encoded bytes to plain text
  public static String decryptAES(byte[] encryptedData, SecretKey key) {
    String decryptedString = null;
    try {
      byte[] dataInBytes = encryptedData;
      Cipher decryptionCipher = Cipher.getInstance("AES");
      decryptionCipher.init(Cipher.DECRYPT_MODE, key);
      byte[] decryptedBytes = decryptionCipher.doFinal(dataInBytes);
      decryptedString = new String(decryptedBytes);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return decryptedString;
  }

  private static String encode(byte[] data) {
    return Base64.getEncoder().encodeToString(data);
  }

  private static byte[] readByteArray(DataInputStream in) throws IOException {
    int length = in.readInt();

    if (length > 0) {
      byte[] message = new byte[length];
      in.readFully(message, 0, message.length);
      return message;
    }
    return null;
  }

  private static void sendByteArray(DataOutputStream out, byte[] message) throws IOException {
    out.writeInt(message.length);
    out.write(message);
  }

  public static String read(DataInputStream in, PrivateKey privateKey) throws IOException {
    byte[] encryptedKey = readByteArray(in);
    byte[] input = readByteArray(in);
    SecretKey aesKey = SecurityUtilities.decryptRSA(encryptedKey, privateKey);

    return SecurityUtilities.decryptAES(input, aesKey);
  }
  
  public static void send(DataOutputStream out, String s, PublicKey publicKey) throws IOException {
    SecretKey aesKey = SecurityUtilities.generateAESSecretKey();
    sendByteArray(
      out,
      SecurityUtilities.encryptRSA(aesKey, publicKey)
    );

    sendByteArray(out, SecurityUtilities.encryptAES(s, aesKey));

  }
}
