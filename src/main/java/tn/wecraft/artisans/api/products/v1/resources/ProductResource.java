package tn.wecraft.artisans.api.products.v1.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import tn.wecraft.artisans.api.products.v1.mappers.ProductMapper;
import tn.wecraft.artisans.api.products.v1.models.ProductRequest;
import tn.wecraft.artisans.api.products.v1.models.ProductResponse;
import tn.wecraft.artisans.api.products.v1.services.ProductService;
import tn.wecraft.artisans.domains.Product;
import tn.wecraft.socle.api.common.errors.IErrorCode;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Slf4j
@PermitAll
@RequestScoped
@Path(ProductResource.CONTEXT_PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tags(value = @Tag(name = "products", description = "All the product methods"))
public class ProductResource {
    public static final String CONTEXT_PATH = "/v1/products";

    @Inject
    ProductService productService;
    
    @Inject
    ProductMapper productMapper;

    @GET
    @APIResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IErrorCode.class)))
    @APIResponse(responseCode = "200", description = "got all products successfully", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = ProductResponse.class)))
    @APIResponse(responseCode = "204", description = "There is no product")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response list() {
        List<Product> products = productService.findAll();
        List<ProductResponse> productResponses = productMapper.productsToProductResponses(products);
        return products.isEmpty() ? Response.noContent().build() : Response.ok(productResponses).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    @APIResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IErrorCode.class), examples = {
            @ExampleObject(ref = "PRODUCT_NOT_FOUND")}
    ))
    @APIResponse(responseCode = "200", description = "got a specific product successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class)))
    @APIResponse(responseCode = "204", description = "There is no product")
    public Response getById(@PathParam("id") String id) {
        Product product = productService.findById(id);
        if (product != null) {
            ProductResponse productResponse = productMapper.productToProductResponse(product);
            return Response.ok(productResponse).status(200).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    
    @PermitAll
    @APIResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IErrorCode.class), examples = {
            @ExampleObject(ref = "INVALID_CREATE_PRODUCT_REQUEST"),
            @ExampleObject(ref = "PRODUCT_ALREADY_EXISTS")
    }
    ))
    @APIResponse(responseCode = "201", description = "added a product successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class)))
    public Response create(@Valid ProductRequest productRequest) {
        Product product = productService.create(productRequest);
        ProductResponse productResponse = productMapper.productToProductResponse(product);
        return Response.ok(productResponse).status(201).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PermitAll
    @Path("{id}")
    @APIResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IErrorCode.class), examples = {
            @ExampleObject(ref = "PRODUCT_NOT_FOUND")}
    ))
    @APIResponse(responseCode = "200", description = "deleted a product successfully", content = @Content(mediaType = "application/json"))
    public Response deleteById(@PathParam("id") String id) {
        productService.deleteById(id);
        return Response.ok().build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/{id}")
    
    @PermitAll
    @APIResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IErrorCode.class), examples = {
            @ExampleObject(ref = "PRODUCT_NOT_FOUND")}
    ))
    @APIResponse(responseCode = "200", description = "Product updated successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class)))
    public ProductResponse update(@PathParam("id") String id, @Valid ProductRequest productRequest) {
        return productService.update(id, productRequest);
    }

    @GET
    @Path("/artisan/{artisan}")
    @PermitAll
    @APIResponse(responseCode = "200", description = "Products found successfully", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = Product.class)))
    @APIResponse(responseCode = "204", description = "There is no product")
    public Response getProductsByArtisan(@PathParam("artisan") String artisan) {
        List<ProductResponse> products = productService.findByArtisan(artisan);
        return Response.ok(products).build();
    }

    @GET
    @Path("/artisan/{artisan}/count")
    @PermitAll
    @APIResponse(responseCode = "200", description = "Counted products by artisan ID successfully", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.INTEGER)))
    @APIResponse(responseCode = "204", description = "There is no product")
    public Response countProductsByArtisan(@PathParam("artisan") String artisan) {
        long productCount = productService.countByArtisan(artisan);
        return Response.ok(productCount).build();
    }

}