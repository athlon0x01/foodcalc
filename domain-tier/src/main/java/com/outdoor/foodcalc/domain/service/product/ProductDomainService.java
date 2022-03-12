package com.outdoor.foodcalc.domain.service.product;

import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.repository.product.IProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Domain service for all operations with {@link Product} objects.
 *
 * @author Anton Borovyk
 */
@Service
public class ProductDomainService {

    private final IProductRepo productRepo;

    @Autowired
    public ProductDomainService(IProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public List<Product> getAllProducts() {
        return productRepo.getAllProducts();
    }
}
