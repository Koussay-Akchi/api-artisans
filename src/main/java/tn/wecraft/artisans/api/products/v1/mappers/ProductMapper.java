package tn.wecraft.artisans.api.products.v1.mappers;

import org.mapstruct.*;
import tn.wecraft.artisans.api.products.v1.models.ProductRequest;
import tn.wecraft.artisans.api.products.v1.models.ProductResponse;
import tn.wecraft.artisans.domains.Product;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.JAKARTA)

public interface ProductMapper {
    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java( java.time.OffsetDateTime.now().format(ProductMapper.FORMATTER).toString() )")
    Product productRequestToProduct(ProductRequest productRequest);

    ProductResponse productToProductResponse(Product product);
    List<ProductResponse> productsToProductResponses(List<Product> products);

    @Mapping(target = "id", ignore = true)
    void updateProductFromRequest(ProductRequest productRequest, @MappingTarget Product product);
}
