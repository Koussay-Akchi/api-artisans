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
    @Schema(required = true, example = "Doe", description = "The last name")
    private String lastName;

    @RestForm
    @NotBlank
    @Schema(required = true, example = "John", description = "The first name")
    private String firstName;

    @RestForm
    @NotBlank
    @Schema(required = true, example = "98123456", description = "The phone number")
    private String phone;

    @RestForm
    @Email
    @Schema(required = true, example = "john.doe@example.com", description = "The email address")
    private String email;

    @RestForm
    @NotBlank
    @Schema(required = false, example = "1", description = "The establishment ID")
    private String establishmentId;

    @RestForm
    @NotBlank
    @Schema(required = false, example = "Ariana", description = "governorate where the product operates")
    private String governorate;

    @RestForm
    @NotBlank
    @Schema(required = false, example = "Soukra", description = "city where the product operates")
    private String city;

    @RestForm
    @NotBlank
    @Schema(required = true, example = "Private", description = "product's activity domain")
    private String activity;

    @RestForm
    @Schema(required = false, example = "1 rue X", description = "address of the product")
    private String address;

    @RestForm
    @Schema(required = false, example = "B2", description = "The importance of the product")
    private String potential;

    @RestForm
    @Schema(required = true, example = "Doctor", description = "The profession category")
    private String profession;

    @RestForm
    @Schema(required = false, example = "Pediatrics", description = "The branch of medical practice")
    private String specialty;

    @RestForm
    @Schema(required = false, example = "Doctor", description = "The level of training, expertise, and qualification")
    private String grade;

    @RestForm
    @Schema(required = false, example = "Department Head", description = "The role or responsibilities")
    private String function;

    @RestForm
    @Schema(required = false, example = "1", description = "address of the product's profile picture")
    private FileUpload image;

    @RestForm
    @Schema(required = false, example = "3", description = "The number of children the product has")
    private String childrenNumber;

    @RestForm
    @Schema(required = false, example = "XYZ Association", description = "The association the product is affiliated with")
    private String association;

    @RestForm
    @Schema(required = false, example = "123 Street, City", description = "The home address of the product")
    private String homeAddress;

    @RestForm
    @Schema(required = false, example = "987654321", description = "The personal phone number of the product")
    private String personalPhone;

    @RestForm
    @Schema(required = false, example = "1990-05-25", description = "The birthday of the product")
    private String birthday;

    @RestForm
    @Schema(required = false, example = "profile", description = "The profile of the product")
    private String profile;

    @RestForm
    @Schema(required = false, example = "Additional comments about the product")
    private String comment;

    @RestForm
    @Schema(required = false, example = "facebook.com/johndoe", description = "The Facebook profile of the product")
    private String facebook;

    @RestForm
    @Schema(required = false, example = "linkedin.com/in/johndoe", description = "The LinkedIn profile of the product")
    private String linkedin;

    @RestForm
    @Schema(required = false, example = "instagram.com/johndoe", description = "The Instagram profile of the product")
    private String instagram;

    @RestForm
    @Schema(required = false, example = "VIP", description = "The classification of the product")
    private String classification;

    @RestForm
    private String partnershipType;

    @RestForm
    private String partnershipAgreement;

}
