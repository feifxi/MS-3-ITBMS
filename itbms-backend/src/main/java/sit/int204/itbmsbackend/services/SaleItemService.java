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
import sit.int204.itbmsbackend.dtos.common.PageDTO;
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
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;
    @Autowired
    private ImageStorageService imageStorageService;

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
            List<Integer> storageSizes,
            String searchKeyword
    ) {
        // ======= validate price range =======
        // ส่ง priceLower หรือ priceUpper มา "อย่างใดอย่างหนึ่ง" → ไม่อนุญาต
        if ((priceLower != null && priceUpper == null) ||
                (priceLower == null && priceUpper != null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ต้องส่ง priceLower และ priceUpper มาพร้อมกัน หรือไม่ส่งเลย");
        }

        Sort sort = "desc".equalsIgnoreCase(sortDirection)
                ? Sort.by(sortField).descending().and(Sort.by("id").ascending())
                : Sort.by(sortField).ascending().and(Sort.by("id").ascending());
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<SaleItem> spec = Specification.where(null);

        // Filter by brand
        if (brands != null && !brands.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("brand").get("name").in(brands));
        }

        // Filter by price
        if (priceLower != null && priceUpper != null) {
            if (priceLower.compareTo(priceUpper) == 0) {
                // ถ้าเท่ากัน → เท่ากับค่านั้น
                spec = spec.and((root, query, cb) ->
                        cb.equal(root.get("price"), priceLower));
            } else {
                // ไม่เท่ากัน → between
                spec = spec.and((root, query, cb) ->
                        cb.between(root.get("price"), priceLower, priceUpper));
            }
        }

        // Filter by storage size
        if (storageSizes != null && !storageSizes.isEmpty()) {
            if (storageSizes.contains(-1)) {
                List<Integer> validSizes = storageSizes.stream()
                        .filter(s -> s != -1)
                        .toList();
                spec = spec.and((root, query, cb) -> {
                    if (validSizes.isEmpty()) {
                        return cb.isNull(root.get("storageGb"));
                    } else {
                        return cb.or(
                                cb.isNull(root.get("storageGb")),
                                root.get("storageGb").in(validSizes)
                        );
                    }
                });
            } else {
                spec = spec.and((root, query, cb) ->
                        root.get("storageGb").in(storageSizes));
            }
        }

        // แทนที่ search logic เดิมด้วยโค้ดนี้ใน SaleItemService

//        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
//            System.out.println("Search keyword received: " + searchKeyword);
//
//        }
        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            String keyword = searchKeyword.trim();

            spec = spec.and((root, query, cb) -> {
                return cb.or(
                        cb.like(cb.lower(cb.concat(root.get("description"), "")),
                                "%" + keyword.toLowerCase() + "%"),
                        cb.like(cb.lower(cb.concat(root.get("model"), "")),
                                "%" + keyword.toLowerCase() + "%"),
                        cb.like(cb.lower(cb.concat(root.get("color"), "")),
                                "%" + keyword.toLowerCase() + "%")
                );
            });
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
    public List<Integer> getDistinctStorageSizes() {
        return saleItemRepository.findDistinctStorageGb();
    }

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
            String newFilename = imageStorageService.saveImage(image);
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
            saleItemImage.setImageViewOrder(i);
            saleItemImageRepository.save(saleItemImage);
        }
        List<SaleItemImage> saleItemImages  = saleItemImageRepository.findAllBySaleItemOrderByImageViewOrder(newSaleItem);
        newSaleItem.setSaleItemImages(saleItemImages);  // fetch new image data because of lazy loading
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
        List<SaleItemImage> existingImages = saleItemImageRepository.findAllBySaleItemOrderByImageViewOrder(existingSaleItem);

        // --- Remove deleted images ---
        for (SaleItemImage img : existingImages) {
            if (!keptImageNames.contains(img.getImageName())) {
                imageStorageService.deleteImage(img.getImageName());
                saleItemImageRepository.delete(img);
            }
        }

        // --- Handle new or updated images ---
        for (int i = 0; i < images.size(); i++) {
            MultipartFile uploadedImage = images.get(i);
            Boolean isNewImage = isNewImageList.get(i);

            if (isNewImage) {
                // Save as a completely new image
                String newFilename = imageStorageService.saveImage(uploadedImage);
                SaleItemImage newImgEntity = new SaleItemImage();
                newImgEntity.setImageName(newFilename);
                newImgEntity.setOriginalImageName(uploadedImage.getOriginalFilename());
                newImgEntity.setImageViewOrder(i);
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
                existingImg.setImageViewOrder(i);
                saleItemImageRepository.save(existingImg);
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
        List<SaleItemImage> saleItemImages = saleItemImageRepository.findAllBySaleItemOrderByImageViewOrder(existingSaleItem);
        for (SaleItemImage img : saleItemImages) {
            imageStorageService.deleteImage(img.getImageName());   // remove image from storage
        }
        saleItemImageRepository.deleteAll(saleItemImages);  // remove image data from db
        saleItemRepository.deleteById(id);  // remove sale item data
    }
}