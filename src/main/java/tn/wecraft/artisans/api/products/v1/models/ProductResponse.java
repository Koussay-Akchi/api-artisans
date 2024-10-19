package tn.wecraft.artisans.api.products.v1.models;

import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "The response of get Products")
@Getter
@Builder
public class ProductResponse {

    @Schema(example = "1", description = "The product id")
    private ObjectId id;

    @Schema(example = "Beautiful Handmade Vase", description = "The name of the product")
    private String nom;

    @Schema(example = "A beautiful handcrafted vase made by local artisans.", description = "Description of the product")
    private String description;

    @Schema(example = "99.99", description = "The price of the product")
    private Float prix;

    @Schema(example = "true", description = "Is the product in stock?")
    private Boolean stock;

    @Schema(example = "true", description = "Is the product handmade?")
    private Boolean faitmain;

    @Schema(example = "Ariana", description = "Governorate where the product is located")
    private String gouvernorat;

    @Schema(example = "Soukra", description = "City where the product is located")
    private String ville;

    @Schema(example = "1 Rue X", description = "Address where the product is located")
    private String address;

    @Schema(example = "John Doe", description = "Name of the artisan/owner")
    private String artisan;

    @Schema(example = "john.doe@example.com", description = "Email of the artisan/owner")
    private String email;

    @Schema(example = "facebook.com/johndoe", description = "Facebook profile of the artisan")
    private String facebook;

    @Schema(example = "instagram.com/johndoe", description = "Instagram profile of the artisan")
    private String instagram;

    @Schema(example = "bijoux", description = "Category of the product")
    private String categorie;

    @Schema(example = "2023-10-19", description = "Creation date of the product")
    private String createdAt;

}