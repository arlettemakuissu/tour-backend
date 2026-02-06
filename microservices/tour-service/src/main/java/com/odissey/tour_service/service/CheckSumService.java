package com.odissey.tour_service.service;

import com.odissey.tour_service.exception.ErrMsg;
import com.odissey.tour_service.exception.TourException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;

@Service
public class CheckSumService {

    private final static String CHECKSUM_SHA_256_ALGORITHM = "SHA-256";

    public CheckSumFile generateChecksum(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance(CHECKSUM_SHA_256_ALGORITHM);

            byte[] data;
            try (InputStream is = file.getInputStream();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[8192];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    digest.update(buffer, 0, read);   // aggiorna checksum
                    baos.write(buffer, 0, read);      // accumula bytes per il LOB
                }
                data = baos.toByteArray();
            }
            String checksum = toHex(digest.digest());
            CheckSumFile checkSumFile = new CheckSumFile(CHECKSUM_SHA_256_ALGORITHM, checksum);
            return checkSumFile;

        } catch (Exception e) {
            throw new TourException(ErrMsg.CHECKSUM_ERROR);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
