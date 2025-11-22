package net.spudacious5705.crackdown.db_operations;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public interface BackupUtil {

    static byte[] checksum(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    static boolean compareChecksums(byte[] checksum1, byte[] checksum2) {
        if (checksum1 == null || checksum2 == null) return false;
        return Arrays.equals(checksum1, checksum2);
    }

}
