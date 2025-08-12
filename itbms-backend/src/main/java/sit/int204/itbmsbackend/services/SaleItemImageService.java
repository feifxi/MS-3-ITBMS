package sit.int204.itbmsbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.configs.FileStorageProperties;
import sit.int204.itbmsbackend.entities.SaleItemImage;
import sit.int204.itbmsbackend.repositories.SaleItemImageRepository;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class SaleItemImageService {
    @Autowired
    private FileStorageProperties properties;
    @Autowired
    private SaleItemImageRepository saleItemImageRepository;

    public boolean isValidFileSize(MultipartFile file) {
        DataSize maxSize = DataSize.parse(properties.getMaxSize());
        return file.getSize() < maxSize.toBytes();
    }

    public boolean isValidFileType(MultipartFile file) {
        List<String> allowedTypes = Arrays.asList(properties.getAllowedTypes().split(","));
        String contentType = file.getContentType();
        return allowedTypes.contains(contentType);
    }

    public String saveImage(MultipartFile file) {
        if (!isValidFileType(file)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid file type");
        }
        // Ensure upload dir exists
        File uploadDir = new File(properties.getUploadDir());
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Generate UUID filename
        String originalFilename = file.getOriginalFilename();
        String extension = Optional.ofNullable(originalFilename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(originalFilename.lastIndexOf(".")))
                .orElse("");

        String newFilename = UUID.randomUUID() + extension;
        Path filePath = Paths.get(uploadDir.getAbsolutePath(), newFilename);
        try {
            file.transferTo(filePath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Upload failed.");
        }
        return newFilename;
    }

    public Resource getImage(String filename)  {
        Path filePath = Paths.get(properties.getUploadDir()).resolve(filename).normalize();
        Resource resource = null;
        try {
            resource = new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (!resource.exists()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found.");
        };
        return resource;
    }

    public boolean deleteImage(String filename) {
        try {
            Path path = Paths.get(properties.getUploadDir(), filename);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting file: " + filename);
        }
    }
}
