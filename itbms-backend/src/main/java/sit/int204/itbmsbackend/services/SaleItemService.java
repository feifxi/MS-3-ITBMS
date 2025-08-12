package sit.int204.itbmsbackend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.dtos.PageDTO;
import sit.int204.itbmsbackend.dtos.saleItem.*;
import sit.int204.itbmsbackend.entities.Brand;
import sit.int204.itbmsbackend.entities.SaleItem;
import sit.int204.itbmsbackend.repositories.SaleItemRepository;
import sit.int204.itbmsbackend.utils.ListMapper;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SaleItemService {
    private final SaleItemRepository saleItemRepository;
    private final BrandService brandService;
    private final ModelMapper modelMapper;
    private final ListMapper listMapper;

    @Autowired
    public SaleItemService(SaleItemRepository saleItemRepository, ModelMapper modelMapper, ListMapper listMapper, BrandService brandService) {
        this.saleItemRepository = saleItemRepository;
        this.brandService = brandService;
        this.modelMapper = modelMapper;
        this.listMapper = listMapper;
    }

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
        Brand brand = brandService.getBrandById(saleItemDto.getBrand().getId());
        saleItemDto.setBrand(brand);
        SaleItem newSaleItem = saleItemRepository.save(
                modelMapper.map(saleItemDto, SaleItem.class)
        );
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