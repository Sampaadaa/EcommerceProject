package com.example.Ecommerce.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        // Generate a random unique identifier for the file
        String randomId = UUID.randomUUID().toString();

        // Get the original file name from the MultipartFile
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new IOException("File name is null");
        }

        // Concatenate the random ID with the file extension
        String fileName = randomId.concat(originalFileName.substring(originalFileName.lastIndexOf('.')));

        // Create the file path
        String filePath = path + File.separator + fileName;

        // Create the folder if it does not exist
        File folder = new File(path);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new IOException("Failed to create directory: " + path);
            }
        }

        // Copy the file to the specified path
        Files.copy(file.getInputStream(), Paths.get(filePath));

        // Return the generated file name
        return fileName;
    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {
        // Create the file path
        String filePath = path + File.separator + fileName;

        // Return an InputStream for the file
        return new FileInputStream(filePath);
    }
}






