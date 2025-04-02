package com.esthetic.usermicroservices.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncrypDecrypCode {
    private static final String SECRET_KEY = "AbasayoITech2025";
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";

    public static String passwordDecrypt(String encryptedText) throws Exception {
        // Convertir la clave a un array de 16 bytes (AES usa claves de 16, 24 o 32 bytes)
        byte[] keyBytes = SECRET_KEY.getBytes("UTF-8");
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        // Inicializar el cifrador en modo DESCIFRADO
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        // Decodificar el texto cifrado desde Base64
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);

        // Desencriptar y devolver la cadena resultante
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, "UTF-8");
    }
}
