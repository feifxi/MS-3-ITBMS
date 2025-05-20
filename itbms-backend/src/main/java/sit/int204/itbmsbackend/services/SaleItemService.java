package sit.int204.itbmsbackend.services;

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

    public List<ListSaleItemResponseDto> findAll(String brand, String sort) {
        List <SaleItem> saleItems = saleItemRepository.findAll();
        return listMapper.mapList(saleItems, ListSaleItemResponseDto.class, modelMapper);
    }

    public PageDTO<ListSaleItemResponseDto> findAll(Integer page, Integer size) {
        Page<SaleItem> saleItemPages = saleItemRepository.findAll(PageRequest.of(page, size));
        return listMapper.toPageDTO(saleItemPages, ListSaleItemResponseDto.class, modelMapper);
    }

    public DetailSaleItemResponseDto findById(Integer id) {
        SaleItem saleItem =  saleItemRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"SaleItem not found for this id :: " + id)
        );
        return modelMapper.map(saleItem, DetailSaleItemResponseDto.class);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CreateUpdateSaleItemResponseDto addSaleItem(CreateSaleItemRequestDto saleItemDto) {
        Brand brand = brandService.getBrandById(saleItemDto.getBrand().getId());
        saleItemDto.setBrand(brand);
        SaleItem newSaleItem = saleItemRepository.save(
                modelMapper.map(saleItemDto, SaleItem.class)
        );
        return modelMapper.map(newSaleItem, CreateUpdateSaleItemResponseDto.class);
    }

    public CreateUpdateSaleItemResponseDto updateSaleItem(UpdateSaleItemRequestDto saleItemDto) {
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
        return modelMapper.map(updatedSaleItem, CreateUpdateSaleItemResponseDto.class);
    }

    public void removeSaleItem(Integer id) {
        if (!saleItemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SaleItem not found for this id :: " + id);
        }
        saleItemRepository.deleteById(id);
    }
}