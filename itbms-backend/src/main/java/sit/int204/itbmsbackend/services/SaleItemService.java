package sit.int204.itbmsbackend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import java.io.IOException;
import java.util.ArrayList;
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
    private FileStorageProperties fileProperties;
    @Autowired
    private SaleItemImageService imageService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    public List<SaleItemListDto> findAll() {
        List <SaleItem> saleItems = saleItemRepository.findAll();
        return listMapper.mapList(saleItems, SaleItemListDto.class, modelMapper);
    }

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
    }

    public SaleItemDetailDto findById(Integer id) {
        SaleItem saleItem =  saleItemRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"SaleItem not found for this id :: " + id)
        );
        return modelMapper.map(saleItem, SaleItemDetailDto.class);
    }

    public SaleItemResponseDto addSaleItem(SaleItemCreateDto saleItemDto) {
        // Save sale item image
        List<MultipartFile> images = saleItemDto.getImages();
        List<String> originalFilenames = new ArrayList<>();
        List<String> newFilenames = new ArrayList<>();
        for (MultipartFile image : images) {
            if (!imageService.isValidFileSize(image)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"File size exceeds limit (" + fileProperties.getMaxSize() + ")");
            }
            if (!imageService.isValidFileType(image)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unsupported file type: " + image.getContentType() );
            }
            String newFilename = imageService.saveImage(image);
            originalFilenames.add(image.getOriginalFilename());
            newFilenames.add(newFilename);
        }
        // Save sale item
        Brand brand = brandService.getBrandById(saleItemDto.getBrandId());
        saleItemDto.setBrand(brand);
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
        Brand brand = brandService.getBrandById(saleItemDto.getBrand().getId());
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
        if (!saleItemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SaleItem not found for this id :: " + id);
        }
        saleItemRepository.deleteById(id);
    }
}