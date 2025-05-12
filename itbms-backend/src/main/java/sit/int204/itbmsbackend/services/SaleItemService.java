package sit.int204.itbmsbackend.services;

import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.dtos.PageDTO;
import sit.int204.itbmsbackend.dtos.saleItem.*;
import sit.int204.itbmsbackend.entities.Brand;
import sit.int204.itbmsbackend.entities.SaleItem;
import sit.int204.itbmsbackend.repositories.SaleItemRepository;
import sit.int204.itbmsbackend.utils.ListMapper;

import java.time.Instant;
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

    public List<ListSaleItemRes> findAll(String brand, String sort) {
        // Implement Sort and Filtering later

        List <SaleItem> saleItems = saleItemRepository.findAll();
        return listMapper.mapList(saleItems, ListSaleItemRes.class, modelMapper);
    }

    public PageDTO<ListSaleItemRes> findAll(Integer page, Integer size) {
        Page<SaleItem> saleItemPages = saleItemRepository.findAll(PageRequest.of(page, size));
        return listMapper.toPageDTO(saleItemPages, ListSaleItemRes.class, modelMapper);
    }

    public DetailSaleItemRes findById(Integer id) {
        SaleItem saleItem =  saleItemRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"SaleItem not found for this id :: " + id)
        );
        return modelMapper.map(saleItem, DetailSaleItemRes.class);
    }

    public CreateUpdateSaleItemRes addSaleItem(CreateSaleItemReq saleItemDto) {
        Brand brand = brandService.getBrandById(saleItemDto.getBrand().getId());
        saleItemDto.setBrand(brand);
        SaleItem newSaleItem = saleItemRepository.save(
                modelMapper.map(saleItemDto, SaleItem.class)
        );
        return modelMapper.map(newSaleItem, CreateUpdateSaleItemRes.class);
    }

    public CreateUpdateSaleItemRes updateSaleItem(UpdateSaleItemReq saleItemDto) {
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
        return modelMapper.map(updatedSaleItem, CreateUpdateSaleItemRes.class);
    }

    public void removeSaleItem(Integer id) {
        if (!saleItemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SaleItem not found for this id :: " + id);
        }
        saleItemRepository.deleteById(id);
    }


}