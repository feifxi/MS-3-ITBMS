package sit.int204.itbmsbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;
import sit.int204.itbmsbackend.configs.FileStorageProperties;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ImageService {
    @Autowired
    private FileStorageProperties properties;

    public boolean isValidFileSize(MultipartFile file) {
        DataSize maxSize = DataSize.parse(properties.getMaxSize());
        return file.getSize() < maxSize.toBytes();
    }

    public boolean isValidFileType(MultipartFile file) {
        List<String> allowedTypes = Arrays.asList(properties.getAllowedTypes().split(","));
        String contentType = file.getContentType();
        return allowedTypes.contains(contentType);
    }

    public String saveImage(MultipartFile file) throws IOException {
        if (!isValidFileType(file)) {
            throw new IOException("Invalid file type");
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
        file.transferTo(filePath.toFile());
        return newFilename;
    }

    public List<String> getImageNames() {
        File folder = new File(properties.getUploadDir());
        if (!folder.exists() || !folder.isDirectory()) {
            return Collections.emptyList();
        }

        String[] fileList = folder.list((dir, name) ->
                name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")
        );
        if (fileList == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(fileList);
    }

    public Resource getImage(String filename) throws IOException {
        Path filePath = Paths.get(properties.getUploadDir()).resolve(filename).normalize();
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists()) throw new FileNotFoundException(filename);
        return resource;
    }

    public File getAllImage() throws IOException {
        File folder = new File(properties.getUploadDir());
        if (!folder.exists() || folder.listFiles() == null || folder.listFiles().length == 0) {
            throw new FileNotFoundException(properties.getUploadDir());
        }

        // Temp ZIP file
        File zipFile = File.createTempFile("images-", ".zip");

        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (File file : folder.listFiles()) {
                if (file.isFile()) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        ZipEntry zipEntry = new ZipEntry(file.getName());
                        zipOut.putNextEntry(zipEntry);

                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            zipOut.write(buffer, 0, length);
                        }
                        zipOut.closeEntry();
                    }
                }
            }
        }
        return zipFile;
    }

    public boolean deleteImage(String filename) throws IOException {
        Path path = Paths.get(properties.getUploadDir(), filename);
        return Files.deleteIfExists(path);
    }
}
