package tn.wecraft.artisans.api.products.v1.resources;

import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import tn.wecraft.socle.api.common.models.UserProfile;
import tn.wecraft.artisans.api.products.v1.mappers.ProductMapper;
import tn.wecraft.artisans.api.products.v1.models.MergeRequest;
import tn.wecraft.artisans.api.products.v1.models.PagedProductResponse;
import tn.wecraft.artisans.api.products.v1.models.ProductRequest;
import tn.wecraft.artisans.api.products.v1.models.ProductResponse;
import tn.wecraft.artisans.api.products.v1.services.ProductService;
import tn.wecraft.artisans.common.models.DownloadLogoResponse;
import tn.wecraft.artisans.common.models.PagedEntity;
import tn.wecraft.artisans.domains.Product;
import tn.wecraft.socle.api.common.errors.IErrorCode;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Slf4j
@PermitAll
@SecurityRequirement(name = "jwt")

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
    UserProfile userProfile;

    @Inject
    ProductMapper productMapper;
    @ConfigProperty(name = "base-url-files")
    String baseUrl;

    @GET
    @APIResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IErrorCode.class)))
    @APIResponse(responseCode = "200", description = "got all products successfully", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = ProductResponse.class)))
    @APIResponse(responseCode = "204", description = "There is no product")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("**")
    @SecurityRequirement(name = "jwt")
    public Response list(@QueryParam("search") String search,@QueryParam("category") String category) {
        List<Product> products = productService.findAll(search, userProfile.getTenantId(),category);
        List<ProductResponse> productResponses = productMapper.productsToProductResponses(products);
        return products.isEmpty() ? Response.noContent().build() : Response.ok(productResponses).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("**")
    @SecurityRequirement(name = "jwt")
    @APIResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IErrorCode.class), examples = {
            @ExampleObject(ref = "PRODUCT_NOT_FOUND")}
    ))
    @APIResponse(responseCode = "200", description = "got a specific product successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class)))
    @APIResponse(responseCode = "204", description = "There is no product")
    public Response getById(@PathParam("id") String id) {
        Product product = productService.findById(userProfile.getTenantId(), id);
        if (product != null) {
            ProductResponse productResponse = productMapper.productToProductResponse(product, baseUrl);
            return Response.ok(productResponse).status(200).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PATCH
    @Path("/{id}/archive")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("**")
    @SecurityRequirement(name = "jwt")
    @APIResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IErrorCode.class), examples = {
            @ExampleObject(ref = "ID_NOT_FOUND"), }))
    @APIResponse(responseCode = "200", description = "Product updated successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class)))
    public Response archive(@PathParam("id") String id) {
        productService.archiveProductById(id, userProfile.getTenantId());
        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed("**")
    @SecurityRequirement(name = "jwt")
    @APIResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IErrorCode.class), examples = {
            @ExampleObject(ref = "INVALID_CREATE_PRODUCT_REQUEST"),
            @ExampleObject(ref = "PRODUCT_ALREADY_EXISTS")
    }
    ))
    @APIResponse(responseCode = "201", description = "added a product successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class)))
    public Response create(@Valid ProductRequest productRequest) {
        Product product = productService.create(productRequest, userProfile.getTenantId());
        ProductResponse productResponse = productMapper.productToProductResponse(product, baseUrl);
        return Response.ok(productResponse).status(201).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed("**")
    @SecurityRequirement(name = "jwt")
    @Path("{id}")
    @APIResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IErrorCode.class), examples = {
            @ExampleObject(ref = "PRODUCT_NOT_FOUND")}
    ))
    @APIResponse(responseCode = "200", description = "deleted a product successfully", content = @Content(mediaType = "application/json"))
    public Response deleteById(@PathParam("id") String id) {
        productService.deleteById(id);
        return Response.ok().build();
    }

//    @POST
//    @Path("/similar")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @RolesAllowed("**")
//    @SecurityRequirement(name = "jwt")
//    @APIResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IErrorCode.class), examples = {
//            @ExampleObject(ref = "INVALID_CREATE_PRODUCT_REQUEST"),
//            @ExampleObject(ref = "PRODUCT_ALREADY_EXISTS")
//    }
//    ))
//    @APIResponse(responseCode = "200", description = "Similar product found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class)))
//    public Response findSimilarProducts(@Valid ProductRequest productRequest) {
//        Product similarProduct = productService.findSimilar(productRequest, userProfile.getTenantId());
//        if (similarProduct != null) {
//            ProductResponse productResponse = productMapper.productToProductResponse(similarProduct, baseUrl);
//            return Response.ok(productResponse).build();
//        } else {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/{id}")
    @RolesAllowed("**")
    @SecurityRequirement(name = "jwt")
    @APIResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IErrorCode.class), examples = {
            @ExampleObject(ref = "PRODUCT_NOT_FOUND")}
    ))
    @APIResponse(responseCode = "200", description = "Product updated successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class)))
    public ProductResponse update(@PathParam("id") String id, @Valid ProductRequest productRequest) {
        return productService.update(id, productRequest);
    }

    @GET
    @Path("/search")
    @RolesAllowed("**")
    @SecurityRequirement(name = "jwt")
    @APIResponse(responseCode = "200", description = "Get all products", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = ProductResponse.class)))
    @APIResponse(responseCode = "204", description = "There is no product")
    @APIResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IErrorCode.class), examples = {
            @ExampleObject(ref = "ORDERBY_FIELD_NOT_FOUND")}
    ))
    public Response list(
            @QueryParam("profession") @DefaultValue("") String profession,
            @QueryParam("name") @DefaultValue("") String name,
            @QueryParam("establishmentId") @DefaultValue("") String establishmentId,
            @QueryParam("page") @DefaultValue("1") @Min(1) Integer page,
            @QueryParam("page_size")  @Min(1) Integer pageSize,
            @QueryParam("order_by") @DefaultValue("firstName") @Parameter(in = ParameterIn.QUERY, example = "id") String orderBy,
            @QueryParam("asc") @Parameter(in = ParameterIn.QUERY, example = "true") Boolean asc
    ){
        PagedEntity<Product> productPagedEntity = productService.search(
                userProfile.getTenantId(),
                profession,
                name,
                establishmentId,
                page,
                pageSize,
                orderBy,
                asc
        );

        PagedProductResponse products = productMapper.pagedProductsToPagedProductResponse(productPagedEntity);

        return products.getProducts().isEmpty()? Response.noContent().build() :  Response.ok(products).build();
    }

    @GET
    @Path("/establishment/{establishmentId}")
    @RolesAllowed("**")
    @SecurityRequirement(name = "jwt")
    @APIResponse(responseCode = "200", description = "Products found successfully", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = Product.class)))
    @APIResponse(responseCode = "204", description = "There is no product")
    public Response getProductsByEstablishmentId(@PathParam("establishmentId") String establishmentId) {
        List<ProductResponse> products = productService.findByEstablishmentId(establishmentId);
        return Response.ok(products).build();
    }

    @GET
    @Path("/establishment/{establishmentId}/count")
    @RolesAllowed("**")
    @SecurityRequirement(name = "jwt")
    @APIResponse(responseCode = "200", description = "Counted products by establishment ID successfully", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.INTEGER)))
    @APIResponse(responseCode = "204", description = "There is no product")
    public Response countProductsByEstablishmentId(@PathParam("establishmentId") String establishmentId) {
        long productCount = productService.countByEstablishmentId(establishmentId);
        return Response.ok(productCount).build();
    }

    @GET
    @Path("/all")
    @RolesAllowed("**")
    @SecurityRequirement(name = "jwt")
    @APIResponse(responseCode = "200", description = "Counted products by establishment ID successfully", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.INTEGER)))
    @APIResponse(responseCode = "204", description = "There is no product")
    public Response getProductsByLaboratoryId() {
        List<Product> products = productService.findByLaboratoryId(userProfile.getTenantId());
        return Response.ok(productMapper.productsToProductResponses(products)).build();
    }
    @GET
    @Path("/duplicates")
    @RolesAllowed("**")
    @Operation(summary = "Get all potential duplicate products", description = "Returns a list of potential duplicates based on phone number, email, and Levenshtein distance of name, surname, and specialty.")
    @APIResponses(value = {
            @APIResponse(responseCode = "200", description = "Successfully retrieved duplicate suspicions",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = List.class))),
            @APIResponse(responseCode = "400", description = "Invalid request parameters"),
            @APIResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "jwt")

    public List<List<Product>> getAllDuplicateSuspicions(@QueryParam("threshold") @DefaultValue("3") int threshold) {
        return productService.getAllDuplicateSuspicions(userProfile.getTenantId(), threshold);
    }

    @POST
    @Path("/merge")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed("**")
    @SecurityRequirement(name = "jwt")
    @APIResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IErrorCode.class)))
    @APIResponse(responseCode = "200", description = "Duplicates merged successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductResponse.class)))
    public ProductResponse mergeDuplicates( @Valid MergeRequest mergeRequest) {
            List<String> idsToDelete = mergeRequest.getIdsToDelete();
            ProductRequest newProductRequest = mergeRequest.getNewProductRequest();

           return  productService.mergeDuplicates(idsToDelete, newProductRequest, userProfile.getTenantId());


    }
    @RolesAllowed("**")
    @GET
    @Path("{id}/image")
    @APIResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = IErrorCode.class), examples = {
            @ExampleObject(ref = "PRODUCT_NOT_FOUND"),
            @ExampleObject(ref = "LOGO_DOES_NOT_EXIST"),
    }))
    @APIResponse(responseCode = "200", description = "Company logo downloaded successfully")
    public Response downloadProductImage(@NotBlank @PathParam("id") String productId) {
        DownloadLogoResponse response = productService.download(productId);
        return Response.ok(response.getFileContent(), response.getFileType()).status(200).build();
    }
}

