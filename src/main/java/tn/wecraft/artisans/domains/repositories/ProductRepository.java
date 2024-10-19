package tn.wecraft.artisans.domains.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Timeout;
import tn.wecraft.artisans.common.models.PagedEntity;
import tn.wecraft.artisans.domains.Product;

import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProductRepository implements PanacheMongoRepository<Product> {

    @Bulkhead
    @Timeout
    public Optional<Product> getById(String id) {
        return findByIdOptional(new ObjectId(id));
    }


    @Bulkhead
    @CircuitBreaker
    @Timeout
    public Product create(Product product) {
        persist(product);
        return product;
    }

    @Bulkhead
    @Timeout
    @CircuitBreaker
    public void deleteProductById(String id) {
        deleteById(new ObjectId(id));
    }


    public List<Product> findByArtisan(String artisan) {
        return list("artisan", artisan);
    }

    @Bulkhead
    @Timeout
    @CircuitBreaker
    public long countByArtisan(String artisan) {
        return count("artisan = ?1", artisan);
    }

}
