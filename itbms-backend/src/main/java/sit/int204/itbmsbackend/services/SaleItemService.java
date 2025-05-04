package sit.int204.itbmsbackend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int204.itbmsbackend.entities.SaleItem;
import sit.int204.itbmsbackend.repositories.SaleItemRepository;

import java.util.List;

@Service
public class SaleItemService {
    final SaleItemRepository saleItemRepository;

    @Autowired
    public SaleItemService(SaleItemRepository saleItemRepository) {
        this.saleItemRepository = saleItemRepository;
    }

    public List<SaleItem> findAll() {
        return saleItemRepository.findAll();
    }

    public SaleItem findById(Integer id) {
        return saleItemRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,"SaleItem not found for this id :: " + id)
        );
    }

    public SaleItem addSaleItem(SaleItem saleItem) {
        return saleItemRepository.save(saleItem);
    }

    public SaleItem updateSaleItem(SaleItem saleItem) {
        if (!saleItemRepository.existsById(saleItem.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SaleItem not found for this id :: " + saleItem.getId());
        }
        return saleItemRepository.save(saleItem);
    }

    public String removeSaleItem(Integer id) {
        if (!saleItemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "SaleItem not found for this id :: " + id);
        }
        saleItemRepository.deleteById(id);
        return "SaleItem remove Successfully";
    }
}
