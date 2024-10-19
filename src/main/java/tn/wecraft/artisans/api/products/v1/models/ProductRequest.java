package tn.wecraft.artisans.api.products.v1.models;

import jakarta.validation.constraints.Email;
import lombok.*;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @RestForm
    @NotBlank
    @Schema(required = true, example = "Artisan Name", description = "The name of the product")
    private String nom;

    @RestForm
    @NotBlank
    @Schema(required = true, example = "Beautiful handcrafted vase", description = "Description of the product")
    private String description;

    @RestForm
    @Schema(required = true, example = "99.99", description = "The price of the product")
    private Float prix;

    @RestForm
    @Schema(required = true, example = "true", description = "Is the product in stock?")
    private Boolean stock;

    @RestForm
    @Schema(required = true, example = "true", description = "Is the product handmade?")
    private Boolean faitmain;

    @RestForm
    @NotBlank
    @Schema(required = false, example = "Ariana", description = "Governorate where the product is located")
    private String gouvernorat;

    @RestForm
    @NotBlank
    @Schema(required = false, example = "Soukra", description = "City where the product is located")
    private String ville;

    @RestForm
    @Schema(required = false, example = "1 Rue X", description = "Address where the product is located")
    private String address;

    @RestForm
    @Schema(required = true, example = "John Doe", description = "Name of the artisan/owner")
    private String artisan;

    @RestForm
    @Email
    @Schema(required = true, example = "john.doe@example.com", description = "Email of the artisan/owner")
    private String email;

    @RestForm
    @Schema(required = false, example = "facebook.com/johndoe", description = "Facebook profile of the artisan")
    private String facebook;

    @RestForm
    @Schema(required = false, example = "instagram.com/johndoe", description = "Instagram profile of the artisan")
    private String instagram;

    @RestForm
    @NotBlank
    @Schema(required = true, example = "Bijoux", description = "Category of the product")
    private String categorie;

}
