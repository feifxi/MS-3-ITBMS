package sit.int204.itbmsbackend.controllers.v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import sit.int204.itbmsbackend.configs.FileStorageProperties;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemCreateDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemDetailDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemListDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemResponseDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemUpdateDto;
import sit.int204.itbmsbackend.entities.SaleItem;
import sit.int204.itbmsbackend.entities.SaleItemImage;
import sit.int204.itbmsbackend.repositories.SaleItemImageRepository;
import sit.int204.itbmsbackend.services.ImageService;
import sit.int204.itbmsbackend.services.SaleItemService;


@RestController
@RequestMapping("/v1/sale-items")
public class SaleItemControllerV1 {
    @Autowired
    private SaleItemService saleItemService;
    @Autowired
    private FileStorageProperties properties;
    @Autowired
    private ImageService imageService;
    @Autowired
    private SaleItemImageRepository saleItemImageRepository;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<SaleItemListDto>> getAllSaleItems() {
        return ResponseEntity.ok(saleItemService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleItemDetailDto> getSaleItemById(@PathVariable Integer id) {
        return ResponseEntity.ok(saleItemService.findById(id));
    }

    @PostMapping
    public ResponseEntity<SaleItemResponseDto> addSaleItem(@Valid @RequestBody SaleItemCreateDto saleItem) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleItemService.addSaleItem(saleItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SaleItemResponseDto> updateProduct(@Valid @PathVariable Integer id, @RequestBody SaleItemUpdateDto saleItem) {
        saleItem.setId(id);
        return ResponseEntity.ok(saleItemService.updateSaleItem(saleItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        saleItemService.removeSaleItem(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Test Upload Image File

    @PostMapping("/test/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        // Validate file size
        if (!imageService.isValidFileSize(file)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size exceeds limit (" + properties.getMaxSize() + ")");
        }
        // Validate file type
        if (!imageService.isValidFileType(file)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported file type: " + file.getContentType());
        }

        try {
            // Save image
            String newFilename = imageService.saveImage(file);
            return ResponseEntity.ok("File uploaded: " + newFilename);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed.");
        }
    }

    @PostMapping("/test/multiple-upload")
    public ResponseEntity<?> uploadMultipleFiles(@RequestParam("files") List<MultipartFile> files) {
        List<String> fileNames = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                if (!imageService.isValidFileSize(file)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size exceeds limit (" + properties.getMaxSize() + ")");
                }
                if (!imageService.isValidFileType(file)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unsupported file type: " + file.getContentType());
                }
                // Save image
                String filename = imageService.saveImage(file);
                fileNames.add(filename);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed.");
            }
        }

        return ResponseEntity.ok(fileNames);
    }


    @GetMapping("/test/image-name")
    public ResponseEntity<List<String>> listUploadedImages() {
        return ResponseEntity.ok(imageService.getImageNames());
    }

    @GetMapping("/{id}/image-names")
    public ResponseEntity<List<SaleItemImage>> listSaleItemImages(@PathVariable Integer id) {
        SaleItemDetailDto saleItem = saleItemService.findById(id);
        SaleItem saleitem = modelMapper.map(saleItem, SaleItem.class);
        return ResponseEntity.status(HttpStatus.OK).body(
                saleItemImageRepository.findAllBySaleItem(saleitem)
        );
    }


    @GetMapping("/test/download/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        Resource resourceFile = imageService.getImage(filename);

        Path filePath = Paths.get(properties.getUploadDir()).resolve(filename).normalize();
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"") // <-- force download
                .body(resourceFile);
    }


    @GetMapping("/test/download-all")
    public ResponseEntity<Resource> downloadAllImages() {
        try {
            File zipFile = imageService.getAllImage();
            InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=all-images.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(zipFile.length())
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/test/delete-image/{fileName}")
    public ResponseEntity<String> deleteImage(@PathVariable String fileName) {
        try {
            imageService.deleteImage(fileName);
            return ResponseEntity.ok("Deleted: " + fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting file: " + fileName);
        }
    }
}
