package sit.int204.itbmsbackend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.configs.FileStorageProperties;
import sit.int204.itbmsbackend.dtos.PageDTO;
import sit.int204.itbmsbackend.dtos.saleItem.*;
import sit.int204.itbmsbackend.entities.Brand;
import sit.int204.itbmsbackend.entities.SaleItem;
import sit.int204.itbmsbackend.entities.SaleItemImage;
import sit.int204.itbmsbackend.repositories.SaleItemImageRepository;
import sit.int204.itbmsbackend.repositories.SaleItemRepository;
import sit.int204.itbmsbackend.utils.ListMapper;

import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.List;

@Service
public class SaleItemService {
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private SaleItemImageRepository saleItemImageRepository;
    @Autowired
    private BrandService brandService;
    @Autowired
    private SaleItemImageService imageService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private SaleItemImageService saleItemImageService;

    public List<SaleItemListDto> findAll() {
        List <SaleItem> saleItems = saleItemRepository.findAll();
        return listMapper.mapList(saleItems, SaleItemListDto.class, modelMapper);
    }
    public PageDTO<SaleItemListDto> findAll(
            List<String> brands,
            Integer page,
            Integer size,
            String sortField,
            String sortDirection,
            BigDecimal priceLower,
            BigDecimal priceUpper,
            List<Integer> storageSizes
    ) {
        Sort sort = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.by(sortField).descending().and(Sort.by("id").ascending())
                : Sort.by(sortField).ascending().and(Sort.by("id").ascending());
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<SaleItem> spec = Specification.where(null);

        if (brands != null && !brands.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("brand").get("name").in(brands));
        }

        // แก้ไข price filter logic
        if (priceLower != null && priceUpper != null) {
            // ถ้ามีทั้ง lower และ upper
            spec = spec.and((root, query, cb) ->
                    cb.between(root.get("price"), priceLower, priceUpper));
        } else if (priceLower != null) {
            // ถ้ามีแค่ lower bound
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("price"), priceLower));
        } else if (priceUpper != null) {
            // ถ้ามีแค่ upper bound
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("price"), priceUpper));
        }

        // แก้ไข storage filter logic
        if (storageSizes != null && !storageSizes.isEmpty()) {
            // ตรวจสอบว่ามี -1 ใน list หรือไม่
            if (storageSizes.contains(-1)) {
                // ถ้ามี -1 ให้แสดงเฉพาะ storage ที่เป็น null
                spec = spec.and((root, query, cb) -> cb.isNull(root.get("storageGb")));
            } else {
                // ถ้าไม่มี -1 ให้ filter ตามค่าที่ส่งมา
                spec = spec.and((root, query, cb) -> root.get("storageGb").in(storageSizes));
            }
        }

        return listMapper.toPageDTO(
                saleItemRepository.findAll(spec, pageable),
                SaleItemListDto.class,
                modelMapper
        );
    }

/*
    public PageDTO<SaleItemListDto> findAll(List<String> brands, Integer page, Integer size, String sortField ,String sortDirection) {
        // Sorting
        Sort sort = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.by(sortField).descending().and(Sort.by("id").ascending())
                : Sort.by(sortField).ascending().and(Sort.by("id").ascending());
        Pageable pageable = PageRequest.of(page, size, sort);
        if (brands == null || brands.isEmpty()) {
            return listMapper.toPageDTO(saleItemRepository.findAll(pageable), SaleItemListDto.class, modelMapper);
        }
        return listMapper.toPageDTO(
                saleItemRepository.findByBrandNameIn(brands, pageable),
                SaleItemListDto.class, modelMapper
        );
    }*/

    public SaleItemDetailDto findById(Integer id) {
        SaleItem saleItem =  saleItemRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"SaleItem not found for this id :: " + id)
        );
        return modelMapper.map(saleItem, SaleItemDetailDto.class);
    }

    public SaleItemResponseDto addSaleItem(SaleItemCreateDto saleItemDto) {
        // Save sale item image
        List<String> originalFilenames = new ArrayList<>();
        List<String> newFilenames = new ArrayList<>();
        for (MultipartFile image : saleItemDto.getImages()) {
            String newFilename = imageService.saveImage(image);
            originalFilenames.add(image.getOriginalFilename());
            newFilenames.add(newFilename);
        }
        // Save sale item
        Brand brand = brandService.getBrandById(saleItemDto.getBrandId());
        saleItemDto.setBrand(brand);
        saleItemDto.setQuantity(saleItemDto.getQuantity() != null ? saleItemDto.getQuantity() : 1);
        SaleItem newSaleItem = saleItemRepository.save(
                modelMapper.map(saleItemDto, SaleItem.class)
        );
        // Save sale item image info to database
        for (int i = 0; i < originalFilenames.size(); i++) {
            SaleItemImage saleItemImage = new SaleItemImage();
            saleItemImage.setImageName(newFilenames.get(i));
            saleItemImage.setOriginalImageName(originalFilenames.get(i));
            saleItemImage.setSaleItem(newSaleItem);
            saleItemImage.setOrderIndex(i);
            saleItemImageRepository.save(saleItemImage);
        }
        return modelMapper.map(newSaleItem, SaleItemResponseDto.class);
    }

    public SaleItemResponseDto updateSaleItem(SaleItemUpdateDto saleItemDto) {
        SaleItem existingSaleItem = saleItemRepository.findById(saleItemDto.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"SaleItem not found for this id :: " + saleItemDto.getId())
        );

        // Update sale item image
        List<MultipartFile> images = saleItemDto.getImages();
        List<Boolean> isNewImageList = saleItemDto.getIsNewImageList();
        List<String> keptImageNames = saleItemDto.getKeptImageNames();

        if (images.size() != isNewImageList.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Images size does not match isNewImageList size");
        }

        // Fetch existing images from DB
        List<SaleItemImage> existingImages = saleItemImageRepository.findAllBySaleItemOrderByOrderIndex(existingSaleItem);

        // --- Remove deleted images ---
        for (SaleItemImage img : existingImages) {
            if (!keptImageNames.contains(img.getImageName())) {
                imageService.deleteImage(img.getImageName());
                saleItemImageRepository.delete(img);
            }
        }

        // --- Handle new or updated images ---
        for (int i = 0; i < images.size(); i++) {
            MultipartFile uploadedImage = images.get(i);
            Boolean isNewImage = isNewImageList.get(i);

            if (isNewImage) {
                // Save as a completely new image
                String newFilename = imageService.saveImage(uploadedImage);
                SaleItemImage newImgEntity = new SaleItemImage();
                newImgEntity.setImageName(newFilename);
                newImgEntity.setOriginalImageName(uploadedImage.getOriginalFilename());
                newImgEntity.setOrderIndex(i);
                newImgEntity.setSaleItem(existingSaleItem);
                saleItemImageRepository.save(newImgEntity);
            } else {
                // This image already exists, just update order
                String imgName = uploadedImage.getOriginalFilename();
                SaleItemImage existingImg = existingImages.stream()
                        .filter(e -> e.getImageName().equals(imgName))
                        .findFirst()
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST, "Existing image not found for image name: " + imgName
                        ));
                existingImg.setOrderIndex(i);

                SaleItemImage s = saleItemImageRepository.save(existingImg);
            }
        }

        // --- Update Sale Item core fields ---
        Brand brand = brandService.getBrandById(saleItemDto.getBrandId());
        existingSaleItem.setModel(saleItemDto.getModel());
        existingSaleItem.setPrice(saleItemDto.getPrice());
        existingSaleItem.setColor(saleItemDto.getColor());
        existingSaleItem.setDescription(saleItemDto.getDescription());
        existingSaleItem.setQuantity(saleItemDto.getQuantity());
        existingSaleItem.setRamGb(saleItemDto.getRamGb());
        existingSaleItem.setStorageGb(saleItemDto.getStorageGb());
        existingSaleItem.setScreenSizeInch(saleItemDto.getScreenSizeInch());
        existingSaleItem.setBrand(brand);
        SaleItem updatedSaleItem = saleItemRepository.save(existingSaleItem);
        return modelMapper.map(updatedSaleItem, SaleItemResponseDto.class);
    }

    public void removeSaleItem(Integer id) {
        SaleItem existingSaleItem = saleItemRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"SaleItem not found for this id :: " + id)
        );
        List<SaleItemImage> saleItemImages = saleItemImageRepository.findAllBySaleItemOrderByOrderIndex(existingSaleItem);
        for (SaleItemImage img : saleItemImages) {
            saleItemImageService.deleteImage(img.getImageName());   // remove image from storage
        }
        saleItemImageRepository.deleteAll(saleItemImages);  // remove image data from db
        saleItemRepository.deleteById(id);  // remove sale item data
    }
}