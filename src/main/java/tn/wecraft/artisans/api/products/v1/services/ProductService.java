package tn.wecraft.artisans.api.products.v1.services;

import jakarta.activation.UnsupportedDataTypeException;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import tn.wecraft.artisans.api.products.v1.errors.ErrorCode;
import tn.wecraft.artisans.api.products.v1.mappers.ProductMapper;
import tn.wecraft.artisans.api.products.v1.models.ProductOrderBy;
import tn.wecraft.artisans.api.products.v1.models.ProductRequest;
import tn.wecraft.artisans.api.products.v1.models.ProductResponse;
import tn.wecraft.artisans.artisans.activities.repositories.ActivitiesRestClient;
import tn.wecraft.artisans.artisans.files.models.LogoResponse;
import tn.wecraft.artisans.artisans.files.repository.ApiFilesRestClient;
import tn.wecraft.artisans.artisans.products.repositories.ProductRestClient;
import tn.wecraft.artisans.common.models.DownloadLogoResponse;
import tn.wecraft.artisans.common.utils.FilesApiUtils;
import tn.wecraft.artisans.domains.Product;
import tn.wecraft.artisans.domains.repositories.ProductRepository;
import tn.wecraft.socle.api.common.exceptions.BadRequestException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import tn.wecraft.artisans.common.models.PagedEntity;

import static tn.wecraft.artisans.api.products.v1.errors.ErrorCode.AVATAR_DOES_NOT_EXIST;
import static tn.wecraft.artisans.api.products.v1.errors.ErrorCode.PRODUCT_NOT_FOUND;

import org.apache.commons.text.similarity.LevenshteinDistance;

@ApplicationScoped
public class ProductService {
    @Inject
    ProductRepository productRepository;
    @Inject
    ProductMapper productMapper;
    @Inject
    FilesApiUtils filesApiUtils;

    @RestClient
    ApiFilesRestClient apiFilesRestClient;
    @RestClient
    ActivitiesRestClient activitiesRestClient;
    @RestClient
    ProductRestClient productRestClient;
    @ConfigProperty(name = "base-url-files")
    String baseUrl;
    public List<Product> findAll(String search, String tenantId, String profession) {
        if (search != null && !search.isEmpty()) {
            return productRepository.findProductsByNameContaining(search, tenantId, profession);
        } else {
            return productRepository.findProductsByNameContaining("", tenantId, profession);
        }
    }

    public Product findById(String tenantId, String id) {
        Product product = productRepository.getById(id).orElseThrow(() -> new BadRequestException(PRODUCT_NOT_FOUND, id));
            if (product.getTenantId().equals(tenantId)){
                return product;
            }
            else{
                throw new BadRequestException(PRODUCT_NOT_FOUND, id);
            }
    }

    public List<Product> findByLaboratoryId(String tenantId) {
        return productRepository.findAllProducts(tenantId);
    }

    public Product create(ProductRequest productRequest, String tenantId) {
        Product product = productMapper.productRequestToProduct(productRequest);
        updateProductImage(productRequest,product);
        product.setTenantId(tenantId);
        return productRepository.create(product);
    }

    
    public void deleteById(String id) {
        productRepository.deleteProductById(id);
    }

    
    public void archiveProductById(String productId, String tenantId) {

        Optional<Product> productOptional = productRepository.getById(productId);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (product.getTenantId().equals(tenantId)){
                productRepository.archiveProductById(product,tenantId );
            } else {
                throw new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND, productId);
            }

        } else {
            throw new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND, productId);
        }
    }

    
    public ProductResponse update(String productId, ProductRequest productRequest) {


        Optional<Product> productOptional = productRepository.getById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            productMapper.updateProductFromRequest(productRequest, product);
            updateProductImage(productRequest,product);
            productRepository.persistOrUpdate(product);
            return productMapper.productToProductResponse(product, baseUrl);

        }
        throw new BadRequestException(PRODUCT_NOT_FOUND, productId);
    }

//    private boolean areAttributesSimilar(Product product, ProductRequest productRequest) {
//        //(WIP) maybe with search?
//        return product.getFirstName().equalsIgnoreCase(productRequest.getFirstName()) &
//                product.getLastName().equalsIgnoreCase(productRequest.getLastName()) ||
//                product.getEmail().equalsIgnoreCase(productRequest.getEmail()) ||
//                product.getPhone().equals(productRequest.getPhone());
//    }
//
//    //WIP
//    public Product findSimilar(ProductRequest productRequest, String tenantId) {
//        List<Product> allProducts = this.productRepository.findAllProducts(tenantId);
//
//        for (Product product : allProducts) {
//            if (areAttributesSimilar(product, productRequest)) {
//                return product; // maybe it returns a list?
//            }
//        }
//
//        return null;
//    }

    public PagedEntity<Product> search(
            String tenantId,
            String profession,
            String name,
            String establishmentId,
            Integer page,
            Integer pageSize,
            String orderBy,
            Boolean ascending
    ) {
        ProductOrderBy orderByEnum = ProductOrderBy.fromValue(orderBy);

        if (orderByEnum == null)
            throw new BadRequestException(ErrorCode.ORDERBY_FIELD_NOT_FOUND, orderBy);

        return productRepository.search(tenantId, profession, name, establishmentId, page - 1, pageSize, orderBy, ascending);

    }

    public List<ProductResponse> findByEstablishmentId(String establishmentId) {
        return productMapper.productsToProductResponses(productRepository.findByEstablishmentId(establishmentId));
    }

    public Long countByEstablishmentId(String establishmentId) {
        return productRepository.countByEstablishmentId(establishmentId);
    }

    public List<List<Product>> getAllDuplicateSuspicions(String tenantId, int nameThreshold) {
        List<Product> allProducts = productRepository.findAllProducts(tenantId); // Fetch products once
        Set<Product> detectedProducts = new HashSet<>();  // Store already processed products
        // Detect duplicates by name similarity
        List<List<Product>> nameDuplicates = detectDuplicatesByNameSurnameSpecialty(allProducts, nameThreshold, detectedProducts);
        List<List<Product>> allDuplicates = new ArrayList<>(nameDuplicates);
        // Detect duplicates by phone
        List<List<Product>> phoneDuplicates = detectDuplicatesByField(allProducts, Product::getPhone, detectedProducts);
        allDuplicates.addAll(phoneDuplicates);

        // Detect duplicates by email
        List<List<Product>> emailDuplicates = detectDuplicatesByField(allProducts, Product::getEmail, detectedProducts);
        allDuplicates.addAll(emailDuplicates);



        return allDuplicates;
    }

    private List<List<Product>> detectDuplicatesByField(List<Product> products, Function<Product, String> fieldExtractor, Set<Product> detectedProducts) {
        List<List<Product>> duplicates = products.stream()
                .filter(product -> !detectedProducts.contains(product))
                .collect(Collectors.groupingBy(fieldExtractor))
                .values().stream()
                .filter(group -> group.size() > 1)
                .collect(Collectors.toList());

        duplicates.forEach(detectedProducts::addAll);

        return duplicates;
    }

    private List<List<Product>> detectDuplicatesByNameSurnameSpecialty(List<Product> allProducts, int threshold, Set<Product> detectedProducts) {
        List<List<Product>> potentialDuplicates = new ArrayList<>();
        Set<Integer> checkedProducts = new HashSet<>();

        for (int i = 0; i < allProducts.size(); i++) {
            if (checkedProducts.contains(i) || detectedProducts.contains(allProducts.get(i))) {
                continue;
            }

            Product p1 = allProducts.get(i);
            List<Product> duplicates = new ArrayList<>();
            duplicates.add(p1);

            for (int j = i + 1; j < allProducts.size(); j++) {
                if (checkedProducts.contains(j) || detectedProducts.contains(allProducts.get(j))) {
                    continue;
                }

                Product p2 = allProducts.get(j);

                int distance = calculateLevenshteinDistance(
                        p1.getFirstName().toUpperCase() + p1.getLastName().toUpperCase() + p1.getSpecialty().toUpperCase(),
                        p2.getFirstName().toUpperCase() + p2.getLastName().toUpperCase() + p2.getSpecialty().toUpperCase()
                );

                if (distance <= threshold) {
                    duplicates.add(p2);
                    checkedProducts.add(j);
                }
            }

            if (duplicates.size() > 1) {
                potentialDuplicates.add(duplicates);
                detectedProducts.addAll(duplicates);
            }
        }

        return potentialDuplicates;
    }

    private int calculateLevenshteinDistance(String str1, String str2) {
        return LevenshteinDistance.getDefaultInstance().apply(str1, str2);
    }

    public ProductResponse mergeDuplicates(List<String> idsToDelete, ProductRequest newProductRequest, String tenantId) {
        Product mergedProduct = create(newProductRequest, tenantId);
        try {
           for (String id : idsToDelete) {
                activitiesRestClient.updateClient(id, mergedProduct.getId().toString());
                productRestClient.updateClient(id, mergedProduct.getId().toString());
               archiveProductById(id, tenantId);
           }

           return productMapper.productToProductResponse(mergedProduct, baseUrl);
        }
       catch (Exception e) {
           throw new BadRequestException(ErrorCode.MERGE_FAILED);
       }

    }
    private void updateProductImage(ProductRequest productRequest, Product existingProduct) {
        if (productRequest.getImage() != null) {
            try {
                String pictureId = filesApiUtils.uploadFile(productRequest.getImage());
                existingProduct.setImage(pictureId);
            } catch (UnsupportedDataTypeException e) {
                throw new BadRequestException(ErrorCode.UNSUPPORTED_FILE_TYPE, productRequest.getImage().contentType());
            }
        }
    }
    public DownloadLogoResponse download(String productId) {
        Optional<Product> productOptional = productRepository.getById(productId);
        if (productOptional.isPresent()){
            Product product = productOptional.get();
            String logoId = product.getImage();

            if (logoId == null)
                throw new BadRequestException(AVATAR_DOES_NOT_EXIST);

            LogoResponse logoDetails = apiFilesRestClient.getFileDetails(logoId);

            if (logoDetails == null)
                throw new BadRequestException(AVATAR_DOES_NOT_EXIST);

            String logoType = logoDetails.getContentType();
            byte[] logoContent = apiFilesRestClient.downloadFile(logoDetails.getId());

            return new DownloadLogoResponse(logoType, logoContent);

        }else {
            throw new BadRequestException(PRODUCT_NOT_FOUND, productId);
        }


    }

}
