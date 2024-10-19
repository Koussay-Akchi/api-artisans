package tn.wecraft.artisans.api.products.v1.mappers;

import jakarta.ws.rs.core.UriBuilder;
import org.mapstruct.*;
import tn.wecraft.artisans.api.products.v1.models.ProductRequest;
import tn.wecraft.artisans.api.products.v1.models.ProductResponse;
import tn.wecraft.artisans.api.products.v1.resources.ProductResource;
import tn.wecraft.artisans.common.models.PagedEntity;
import tn.wecraft.artisans.domains.Product;
import java.time.format.DateTimeFormatter;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)

public interface ProductMapper {
    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "createdAt", expression = "java( java.time.OffsetDateTime.now().format(ProductMapper.FORMATTER).toString() )")
    Product productRequestToProduct(ProductRequest productRequest);
    @Mapping(target = "image", expression = "java(buildFileUrl(baseUrl, product.getImage(), product.getId().toString()))")
    ProductResponse productToProductResponse(Product product, @Context String baseUrl);
    List<ProductResponse> productsToProductResponses(List<Product> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    void updateProductFromRequest(ProductRequest productRequest, @MappingTarget Product product);
    default String buildFileUrl(String baseUrl, String logo, String productId) {
        if ((logo == null) || logo.isEmpty())
            return null;

        return UriBuilder.fromUri(baseUrl)
                .path(ProductResource.class)
                .path(ProductResource.class, "downloadProductImage")
                .build(productId)
                .toString();
    }
}
