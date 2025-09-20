package sit.int204.itbmsbackend.controller.v1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.config.FileStorageProperties;
import sit.int204.itbmsbackend.dto.saleItem.SaleItemCreateRequestDTO;
import sit.int204.itbmsbackend.dto.saleItem.SaleItemListResponseDTO;
import sit.int204.itbmsbackend.dto.saleItem.SaleItemResponseDTO;
import sit.int204.itbmsbackend.dto.saleItem.SaleItemUpdateRequestDTO;
import sit.int204.itbmsbackend.entity.SaleItemImage;
import sit.int204.itbmsbackend.repository.SaleItemImageRepository;
import sit.int204.itbmsbackend.security.UserPrincipal;
import sit.int204.itbmsbackend.service.ImageStorageService;
import sit.int204.itbmsbackend.service.SaleItemService;


@RestController
@RequestMapping("/v1/sale-items")
@RequiredArgsConstructor
public class SaleItemControllerV1 {
    private final SaleItemService saleItemService;
    private final FileStorageProperties properties;
    private final ImageStorageService imageService;
    private final SaleItemImageRepository saleItemImageRepository;

    @GetMapping
    public ResponseEntity<List<SaleItemListResponseDTO>> getAllSaleItems() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(saleItemService.findAll(userPrincipal.getUser()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleItemResponseDTO> getSaleItemById(@PathVariable Integer id) {
        SaleItemResponseDTO saleItemDetailDto = saleItemService.findById(id);
        return ResponseEntity.ok(saleItemDetailDto);
    }

    @PostMapping(
            consumes = {"multipart/form-data"},
            produces = {"application/json"}
    )
    public ResponseEntity<SaleItemResponseDTO> addSaleItem(
            @Valid @ModelAttribute SaleItemCreateRequestDTO saleItem,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleItemService.addSaleItem(saleItem, userPrincipal.getUser()));
    }

    @PutMapping(
            value = "/{id}",
            consumes = {"multipart/form-data", "application/json"},
            produces = {"application/json"}
    )
    public ResponseEntity<SaleItemResponseDTO> updateProduct(
            @Valid
            @PathVariable Integer id,
            @ModelAttribute SaleItemUpdateRequestDTO saleItem,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        saleItem.setId(id);
        return ResponseEntity.ok(saleItemService.updateSaleItem(saleItem,  userPrincipal.getUser()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        saleItemService.removeSaleItem(id, userPrincipal.getUser());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        Resource resourceFile = imageService.getImage(filename);
        SaleItemImage saleItemImageInfo = saleItemImageRepository.findOneByImageName(filename).orElseThrow(
                 () -> new ResponseStatusException(HttpStatus.NOT_FOUND, resourceFile.getFilename() +  " not found")
        );
        String originalImageName = saleItemImageInfo.getOriginalImageName();

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
}
