package com.backendsyndicate.smashclub.external.service.storage;

import com.backendsyndicate.smashclub.common.util.FileManipulator;
import com.backendsyndicate.smashclub.common.util.Logging;
import com.backendsyndicate.smashclub.external.config.CloudinaryConfig;
import com.backendsyndicate.smashclub.external.dto.CloudinaryResponseDTO;
import com.cloudinary.Cloudinary;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {
    private Cloudinary cloudinaryClient;
    private Map<String, Object> baseOptions;
    private ModelMapper modelMapper;

    public CloudinaryService() {
        initClient();

        this.baseOptions = new HashMap<>();
        this.baseOptions.put("use_filename", true);
        this.baseOptions.put("unique_filename", false);
        this.baseOptions.put("overwrite", true);

        this.modelMapper = new ModelMapper();
    }

    private void initClient() {
        try {
            this.cloudinaryClient = new Cloudinary(CloudinaryConfig.getCloudinaryUrl());
        } catch(Exception e) {
            Logging.handleException("CloudinaryService", "initClient()", 34, generateErrorCode("00", "001"), e.getMessage());
        }
    }

    private String generateErrorCode(String methodNo, String errorNo) {
        return "CLD-" + methodNo + "E" + errorNo;
    }

    public CloudinaryResponseDTO uploadImage(String folder, MultipartFile image) {
        CloudinaryResponseDTO result = null;

        try {
            Logging.printConsole(CloudinaryConfig.getCloudinaryUrl());
            Map<String, Object> options = this.baseOptions;
            options.put("asset_folder", folder);
            options.put("resource_type", "image");
            Logging.printConsole(options.toString());

            File imageFile = FileManipulator.convertMultipartToFile(image);

            Map uploadMap = cloudinaryClient.uploader().upload(imageFile, this.baseOptions);
            result = modelMapper.map(uploadMap, CloudinaryResponseDTO.class);
        } catch(IOException e) {
            Logging.handleException("CloudinaryService", "uploadImage(String folder, MultipartFile image)", 48, generateErrorCode("01", "009"), e.getMessage());
        } catch(Exception e) {
            Logging.handleException("CloudinaryService", "uploadImage(String folder, MultipartFile image)", 48, generateErrorCode("01", "010"), e.getMessage());
        }

        return result;
    }
}
