package com.backendsyndicate.smashclub.external.service.storage;

import com.backendsyndicate.smashclub.common.util.FileManipulator;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.common.util.Util;
import com.backendsyndicate.smashclub.external.config.CloudinaryConfig;
import com.backendsyndicate.smashclub.external.dto.CloudinaryResponseDTO;
import com.backendsyndicate.smashclub.external.util.CloudinaryUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinaryClient;
    private Map<String, Object> baseOptions;
    private ModelMapper modelMapper;

    public CloudinaryService() {
        this.baseOptions = new HashMap<>();

//        Use this if you want to use filename instead of letting Cloudinary generate public ID for you
//        this.baseOptions.put("use_filename", true);
//        this.baseOptions.put("unique_filename", false);
//        this.baseOptions.put("overwrite", true);

        this.modelMapper = new ModelMapper();
    }

    private String generateErrorCode(String methodNo, String errorNo) {
        return "CLD-" + methodNo + "E" + errorNo;
    }

    public CloudinaryResponseDTO uploadImage(String folder, MultipartFile image) {
        CloudinaryResponseDTO result = null;

        try {
            Map<String, Object> options = this.baseOptions;
            options.put("asset_folder", folder);
            options.put("resource_type", "image");
            options.put("public_id", Util.generateRandomString(10, false));

            File imageFile = FileManipulator.convertMultipartToFile(image);

            Map uploadMap = cloudinaryClient.uploader().upload(imageFile, options);
            result = new CloudinaryResponseDTO();
            result.mapToDTO(uploadMap);
        } catch(IOException e) {
            Logging.handleException("CloudinaryService", "uploadImage(String folder, MultipartFile image)", 48, generateErrorCode("01", "009"), e.getMessage());
        } catch(Exception e) {
            Logging.handleException("CloudinaryService", "uploadImage(String folder, MultipartFile image)", 48, generateErrorCode("01", "010"), e.getMessage());
        }

        return result;
    }

    public CloudinaryResponseDTO uploadImage(String folder, String image) {
        CloudinaryResponseDTO result = null;

        try {
            Map<String, Object> options = this.baseOptions;
            options.put("asset_folder", folder);
            options.put("resource_type", "image");
            options.put("public_id", Util.generateRandomString(10, false));

            Map uploadMap = cloudinaryClient.uploader().upload(image, options);
            result = new CloudinaryResponseDTO();
            result.mapToDTO(uploadMap);
        } catch(IOException e) {
            Logging.handleException("CloudinaryService", "uploadImage(String folder, String image)", 48, generateErrorCode("02", "009"), e.getMessage());
        } catch(Exception e) {
            Logging.handleException("CloudinaryService", "uploadImage(String folder, String image)", 48, generateErrorCode("02", "010"), e.getMessage());
        }

        return result;
    }

    public CloudinaryResponseDTO deleteImage(String imageUrl) {
        CloudinaryResponseDTO result = null;

        try {
            Map<String, Object> options = this.baseOptions;
            String publicId = CloudinaryUtil.extractPublicIdFromUrl(imageUrl);
            if( publicId == null ) {
                Logging.handleException("CloudinaryService", "deleteImage(String publicId)", 89, generateErrorCode("03", "010"), "Failed to extract public ID!");
                return null;
            }

            Map uploadMap = cloudinaryClient.uploader().destroy(publicId, options);
            result = new CloudinaryResponseDTO();
            result.mapToDTO(uploadMap);
        } catch(IOException e) {
            Logging.handleException("CloudinaryService", "deleteImage(String publicId)", 86, generateErrorCode("03", "009"), e.getMessage());
        } catch(Exception e) {
            Logging.handleException("CloudinaryService", "deleteImage(String publicId)", 86, generateErrorCode("03", "010"), e.getMessage());
        }

        return result;
    }

    public String uploadImageGetUrl(String folder, MultipartFile image) {
        CloudinaryResponseDTO result = uploadImage(folder, image);
        return result.getSecureUrl();
    }
}
