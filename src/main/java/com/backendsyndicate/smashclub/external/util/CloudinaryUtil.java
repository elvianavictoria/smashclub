package com.backendsyndicate.smashclub.external.util;

import org.springframework.stereotype.Component;

@Component
public class CloudinaryUtil {
    public static String extractPublicIdFromUrl(String url) {
        // Contoh URL: https://res.cloudinary.com/demo/image/upload/v12345/profile-pictures/user123/abc.jpg
        try {
            String[] parts = url.split("/");
            // Cari bagian setelah 'upload' dan sebelum ekstensi
            boolean uploadFound = false;
            StringBuilder publicId = new StringBuilder();

            for (String part : parts) {
                if (uploadFound) {
                    if (part.contains("v") && part.matches("v\\d+")) {
                        // Skip version number
                        continue;
                    }
                    // Hapus ekstensi
                    publicId.append(part.split("\\.")[0]).append("/");
                }
                if (part.equals("upload")) {
                    uploadFound = true;
                }
            }

            String result = publicId.toString().replaceAll("/$", "");
            return result.isEmpty() ? null : result;
        } catch (Exception e) {
            return null;
        }
    }
}
