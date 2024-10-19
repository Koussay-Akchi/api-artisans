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
    @CircuitBreaker
    public List<Product> findAllProducts(String tenantId) {
        return list("tenantId = ?1 and archived = ?2", tenantId, false);
    }

    @Bulkhead
    @Timeout
    @CircuitBreaker
    public List<Product> findProductsByNameContaining(String search, String tenantId, String profession) {
        String regex = "(?i)" + search;
        List<Product> results = new ArrayList<>();
        Set<String> uniqueKeys = new HashSet<>();

        List<Product> lastNameResults;
        List<Product> firstNameResults;

        if (profession != null && !profession.isBlank()) {
            lastNameResults = list("tenantId = ?1 and lastName like ?2 and profession = ?3 and archived = ?4", tenantId, regex, profession, false);
            firstNameResults = list("tenantId = ?1 and firstName like ?2 and profession = ?3 and archived = ?4", tenantId, regex, profession, false);
        } else {
            lastNameResults = list("tenantId = ?1 and lastName like ?2 and archived = ?3", tenantId, regex, false);
            firstNameResults = list("tenantId = ?1 and firstName like ?2 and archived = ?3", tenantId, regex, false);
        }

        for (Product product : firstNameResults) {
            String key = String.valueOf(product.getId());
            if (!uniqueKeys.contains(key)) {
                uniqueKeys.add(key);
                results.add(product);
            }
        }

        for (Product product : lastNameResults) {
            String key = String.valueOf(product.getId());
            if (!uniqueKeys.contains(key)) {
                uniqueKeys.add(key);
                results.add(product);
            }
        }

        return results;
    }

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

    // maybe not needed since we archive?
    @Bulkhead
    @Timeout
    @CircuitBreaker
    public void deleteProductById(String id) {
        deleteById(new ObjectId(id));
    }

    public void archiveProductById(Product product, String tenantId) {
        product.setArchived(true);
        update(product);
    }


    private Sort getSort(String orderBy, Boolean ascending) {
        Sort sort = null;
        Sort.Direction direction = Sort.Direction.Ascending;

        if (ascending != null && !ascending) {
            direction = Sort.Direction.Descending;
        }

        if (orderBy != null && !orderBy.isEmpty()) {
            sort = Sort.by(orderBy, direction);
        }

        return sort;
    }

    @Bulkhead
    @Timeout
    @CircuitBreaker
    public PagedEntity<Product> search(
            String tenantId,
            String profession,
            String name,
            String establishmentId,
            Integer pageIndex,
            Integer pageSize,
            String orderBy,
            Boolean ascending
    ) {
        Map<String, Object> params = new HashMap<>();
        List<String> requestPortions = new ArrayList<>();

        params.put("tenant_id", tenantId);
        requestPortions.add("tenant_id = :tenant_id");

        if (profession != null && !profession.isEmpty()) {
            params.put("profession", profession);
            requestPortions.add("profession = :profession");
        }

        if (establishmentId != null && !establishmentId.isEmpty()) {
            params.put("establishmentId", establishmentId);
            requestPortions.add("establishmentId = :establishmentId");
        }

        params.put("archived", false);
        requestPortions.add("archived = :archived");

        List<Product> allProducts = find(String.join(" and ", requestPortions), getSort(orderBy, ascending), params).list();

        List<Product> filteredProducts = allProducts.stream()
                .filter(p -> {
                    String fullName = (p.getFirstName() + " " + p.getLastName()).toLowerCase();
                    return fullName.contains(name.toLowerCase());
                })
                .toList();

        int totalNumberOfPages = (int) Math.ceil((double) filteredProducts.size() / pageSize);

        List<Product> pagedResults = filteredProducts.stream()
                .skip((long) (pageIndex) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        return new PagedEntity<>(pagedResults, pageSize, pageIndex + 1, totalNumberOfPages);
    }

    public List<Product> findByEstablishmentId(String establishmentId) {
        return list("establishmentId", establishmentId);
    }

    @Bulkhead
    @Timeout
    @CircuitBreaker
    public long countByEstablishmentId(String establishmentId) {
        return count("establishmentId = ?1 and archived = ?2", establishmentId, false);
    }

}
