package me.donttalkto.padblog.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Class used to check and authenticate users
 */
public class PasswordAuthentication {

    private PasswordAuthentication() throws InstantiationException {
        throw new InstantiationException("Can't instantiate this class");
    }

    // The higher this number, the more heavy it is on the system
    private static final int ITERATIONS = 10000;
    private static MessageDigest digest;
    private static final SecureRandom DICE = new SecureRandom();

    static {
        try {
            digest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            digest = null;
            e.printStackTrace();
        }
    }

    /**
     * Returns a random salt to be used to hash a password.
     *
     * @param length
     *            The length of the salt
     * @return a random salt of the specified length
     */
    private static byte[] getSalt(int length) {
        byte[] salt = new byte[length];
        DICE.nextBytes(salt);
        return salt;
    }

    /**
     * This methods creates a hash (that includes the salt AND hashed password) of
     * the specified password
     *
     * @param password
     *            The password to hash
     * @param salt
     *            The salt to use when hashing the password
     * @return The hashed password and the salt string (respectively)
     */
    private static byte[][] hash(char[] password, byte[] salt) {
        if (digest == null)
            return null;

        try {
            byte[] bytePassword = toString(password).getBytes("UTF-8");
            byte[] hashedPassword = new byte[bytePassword.length + salt.length];
            System.arraycopy(bytePassword, 0, hashedPassword, 0, bytePassword.length);
            System.arraycopy(salt, 0, hashedPassword, bytePassword.length, salt.length);

            for (int i = 0; i < ITERATIONS; i++)
                hashedPassword = digest.digest(hashedPassword);

            return new byte[][] { hashedPassword, salt };

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * This methods creates a hash (that includes the salt AND hashed password) of
     * the specified password
     *
     * @param password
     *            The password to hash
     * @return The hashed password and the salt string (respectively)
     */
    public static byte[][] hash(char[] password) {
        byte[] salt = getSalt(password.length);
        return hash(password, salt);
    }

    /**
     * Compare 2 passwords using hash
     * @param enteredPassword The password entered
     * @param salt The salt of the stored password
     * @param hashedPassword The stored password
     * @return Whether the passwords match
     */
    public static boolean compare(char[] enteredPassword, byte[] salt, byte[] hashedPassword) {
        byte[][] hashResult = hash(enteredPassword, salt);
        if (hashResult == null)
            return false;
        byte[] hashedEnteredPassword = hashResult[0];
        return new String(hashedPassword).equals(new String(hashedEnteredPassword));
    }

    private static String toString(char[] chars) {
        StringBuilder builder = new StringBuilder();
        for (char c : chars)
            builder.append(c);
        return builder.toString();
    }

}
