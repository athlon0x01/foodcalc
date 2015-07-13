package com.outdoor.foodcalc.domain.service.product;

import com.outdoor.foodcalc.domain.model.product.ProductCategory;
import com.outdoor.foodcalc.domain.model.product.Product;
import com.outdoor.foodcalc.domain.repository.product.IProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <description>
 *
 * @author Anton Borovyk
 */
@Service
public class ProductDomainService {

    private IProductRepo productRepo;

    @Autowired
    public ProductDomainService(IProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    public List<ProductCategory> getCategories() {
        return productRepo.getCategories();
    }

    public List<Product> getAllProducts() {
        return productRepo.getAllProducts();
    }
}
