package sit.int204.itbmsbackend.controllers.v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.configs.FileStorageProperties;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemCreateDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemDetailDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemListDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemResponseDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemUpdateDto;
import sit.int204.itbmsbackend.entities.SaleItem;
import sit.int204.itbmsbackend.entities.SaleItemImage;
import sit.int204.itbmsbackend.repositories.SaleItemImageRepository;
import sit.int204.itbmsbackend.services.SaleItemImageService;
import sit.int204.itbmsbackend.services.SaleItemService;


@RestController
@RequestMapping("/v1/sale-items")
public class SaleItemControllerV1 {
    @Autowired
    private SaleItemService saleItemService;
    @Autowired
    private FileStorageProperties properties;
    @Autowired
    private SaleItemImageService imageService;
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

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<SaleItemResponseDto> addSaleItem(@Valid @ModelAttribute SaleItemCreateDto saleItem) {
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

    @GetMapping("/{id}/pictures")
    public ResponseEntity<List<SaleItemImage>> listSaleItemImages(@PathVariable Integer id) {
        SaleItemDetailDto saleItem = saleItemService.findById(id);
        SaleItem saleitem = modelMapper.map(saleItem, SaleItem.class);
        return ResponseEntity.status(HttpStatus.OK).body(
                saleItemImageRepository.findAllBySaleItemOrderByOrderIndex(saleitem)
        );
    }

    @GetMapping("/pictures/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        Resource resourceFile = imageService.getImage(filename);
        SaleItemImage saleItemImageInfo = saleItemImageRepository.findByImageName(filename).orElseThrow(
                 () -> new ResponseStatusException(HttpStatus.NOT_FOUND, resourceFile.getFilename() +  "not found")
        );
        String originalImageName = saleItemImageInfo.getOriginalImageName();

        Path filePath = Paths.get(properties.getUploadDir()).resolve(filename).normalize();
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalImageName + "\"") // <-- force download
                .body(resourceFile);
    }

    @DeleteMapping("/pictures/{filename:.+}")
    public ResponseEntity<String> deleteImage(@PathVariable String filename) {
        imageService.deleteImage(filename);
        return ResponseEntity.ok("Deleted: " + filename);
    }
}
