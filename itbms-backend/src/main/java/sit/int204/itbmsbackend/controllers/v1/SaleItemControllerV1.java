package sit.int204.itbmsbackend.controllers.v1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import sit.int204.itbmsbackend.configs.FileStorageProperties;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemCreateDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemDetailDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemListDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemResponseDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemUpdateDto;
import sit.int204.itbmsbackend.entities.SaleItemImage;
import sit.int204.itbmsbackend.repositories.SaleItemImageRepository;
import sit.int204.itbmsbackend.security.UserPrincipal;
import sit.int204.itbmsbackend.services.ImageStorageService;
import sit.int204.itbmsbackend.services.SaleItemService;


@RestController
@RequestMapping("/v1/sale-items")
@RequiredArgsConstructor
public class SaleItemControllerV1 {
    private final SaleItemService saleItemService;
    private final FileStorageProperties properties;
    private final ImageStorageService imageService;
    private final SaleItemImageRepository saleItemImageRepository;

    @GetMapping
    public ResponseEntity<List<SaleItemListDto>> getAllSaleItems() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(saleItemService.findAll(userPrincipal.getUser()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleItemDetailDto> getSaleItemById(@PathVariable Integer id) {
        SaleItemDetailDto saleItemDetailDto = saleItemService.findById(id);
        return ResponseEntity.ok(saleItemDetailDto);
    }

    @PostMapping(
            consumes = {"multipart/form-data"},
            produces = {"application/json"}
    )
    public ResponseEntity<SaleItemResponseDto> addSaleItem(
            @Valid @ModelAttribute SaleItemCreateDto saleItem,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleItemService.addSaleItem(saleItem, userPrincipal.getUser()));
    }

    @PutMapping(
            value = "/{id}",
            consumes = {"multipart/form-data", "application/json"},
            produces = {"application/json"}
    )
    public ResponseEntity<SaleItemResponseDto> updateProduct(
            @Valid
            @PathVariable Integer id,
            @ModelAttribute SaleItemUpdateDto saleItem,
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
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"") // <-- force download
                .body(resourceFile);
    }
}
