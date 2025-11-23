package net.spudacious5705.crackdown.db_operations;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    static byte[] write(CompoundTag data){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            NbtIo.writeCompressed(data, stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stream.toByteArray();
    }

    static CompoundTag read(InputStream data){
        try {
            return NbtIo.readCompressed(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
