package com.outdoor.foodcalc.endpoint;

import com.outdoor.foodcalc.model.SimpleProductCategory;
import com.outdoor.foodcalc.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

/**
 * REST Endpoint for Product Category related operations
 *
 * @author Anton Borovyk.
 */
@Path("/product-categories")
public class ProductCategoryEndpoint {

    private ProductCategoryService categoryService;

    @Autowired
    public ProductCategoryEndpoint(ProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SimpleProductCategory> getCategories() {
        return categoryService.getCategories();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategory(@PathParam("id") long id) {
        Optional<SimpleProductCategory> category = categoryService.getCategory(id);
        if (!category.isPresent()) {
            return Response.status(NOT_FOUND).build();
        }
        return Response.ok(category.get()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCategory(SimpleProductCategory category, @Context UriInfo uriInfo) {
        long newId = categoryService.addCategory(category.name);
        return Response.created(URI.create(uriInfo.getAbsolutePath().toString() + "/" + newId)).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCategory(SimpleProductCategory category) {
        if (!categoryService.updateCategory(category)){
            return Response.status(NOT_FOUND).build();
        }
        return Response.ok(category).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") long id) {
        if(!categoryService.deleteCategory(id)) {
            return Response.status(NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}
