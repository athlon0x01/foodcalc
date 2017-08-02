package com.outdoor.foodcalc.endpoint;

import com.outdoor.foodcalc.model.CategoryWithProducts;
import com.outdoor.foodcalc.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * REST Endpoint for Product related operations
 *
 * @author Anton Borovyk
 */
@Path("/products")
public class ProductEndpoint {

    private ProductService productService;

    @Autowired
    public ProductEndpoint(ProductService productService) {
        this.productService = productService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<CategoryWithProducts> allProducts() {
        return productService.getAllProducts();
    }
}
