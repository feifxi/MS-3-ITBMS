package sit.int204.itbmsbackend.services;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.dtos.PageDTO;
import sit.int204.itbmsbackend.dtos.saleItem.CreateSaleItemDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemDetailDto;
import sit.int204.itbmsbackend.dtos.saleItem.SaleItemListDto;
import sit.int204.itbmsbackend.dtos.saleItem.UpdateSaleItemDto;
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

    public List<SaleItemListDto> findAll(String brand, String sort) {
        // Implement Sort and Filtering later

        List <SaleItem> saleItems = saleItemRepository.findAll();
        return listMapper.mapList(saleItems, SaleItemListDto.class, modelMapper);
    }

    public PageDTO<SaleItemListDto> findAll(Integer page, Integer size) {
        Page<SaleItem> saleItemPages = saleItemRepository.findAll(PageRequest.of(page, size));
        return listMapper.toPageDTO(saleItemPages, SaleItemListDto.class, modelMapper);
    }

    public SaleItemDetailDto findById(Integer id) {
        SaleItem saleItem =  saleItemRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"SaleItem not found for this id :: " + id)
        );
        return modelMapper.map(saleItem, SaleItemDetailDto.class);
    }

    public SaleItemDetailDto addSaleItem(CreateSaleItemDto saleItemDto) {
        // Mock Brand - waiting for brand service >:CCC
        Brand mockBrand = new Brand();
        mockBrand.setId(saleItemDto.getBrandId());
        saleItemDto.setBrand(mockBrand);
        SaleItem newSaleItem = saleItemRepository.save(
                modelMapper.map(saleItemDto, SaleItem.class)
        );
        return modelMapper.map(newSaleItem, SaleItemDetailDto.class);
    }

    public SaleItemDetailDto updateSaleItem(UpdateSaleItemDto saleItemDto) {
        if (!saleItemRepository.existsById(saleItemDto.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SaleItem not found for this id :: " + saleItemDto.getId());
        }
        SaleItem updatedSaleItem = saleItemRepository.save(
                modelMapper.map(saleItemDto, SaleItem.class)
        );
        return modelMapper.map(updatedSaleItem, SaleItemDetailDto.class);
    }

    public void removeSaleItem(Integer id) {
        if (!saleItemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SaleItem not found for this id :: " + id);
        }
        saleItemRepository.deleteById(id);
    }
}
