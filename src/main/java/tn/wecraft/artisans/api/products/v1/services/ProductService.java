package tn.wecraft.artisans.api.products.v1.services;

import tn.wecraft.artisans.api.products.v1.mappers.ProductMapper;
import tn.wecraft.artisans.api.products.v1.models.ProductRequest;
import tn.wecraft.artisans.api.products.v1.models.ProductResponse;
import tn.wecraft.artisans.domains.Product;
import tn.wecraft.artisans.domains.repositories.ProductRepository;
import tn.wecraft.socle.api.common.exceptions.BadRequestException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.*;
import static tn.wecraft.artisans.api.products.v1.errors.ErrorCode.PRODUCT_NOT_FOUND;

@ApplicationScoped
public class ProductService {
    @Inject
    ProductRepository productRepository;
    @Inject
    ProductMapper productMapper;


    public List<Product> findAll() {
        return productRepository.findAll().list();
    }

    public Product findById(String id) {
        return productRepository.getById(id).orElseThrow(() -> new BadRequestException(PRODUCT_NOT_FOUND, id));
    }

    public Product create(ProductRequest productRequest) {
        Product product = productMapper.productRequestToProduct(productRequest);
        return productRepository.create(product);
    }

    public void deleteById(String id) {
        productRepository.deleteProductById(id);
    }

    public ProductResponse update(String productId, ProductRequest productRequest) {


        Optional<Product> productOptional = productRepository.getById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            productMapper.updateProductFromRequest(productRequest, product);
            productRepository.persistOrUpdate(product);
            return productMapper.productToProductResponse(product);

        }
        throw new BadRequestException(PRODUCT_NOT_FOUND, productId);
    }

    public List<ProductResponse> findByArtisan(String artisan) {
        return productMapper.productsToProductResponses(productRepository.findByArtisan(artisan));
    }

    public Long countByArtisan(String artisan) {
        return productRepository.countByArtisan(artisan);
    }


}
