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

    @Schema(example = "Doe", description = "The last name")
    private String lastName;

    @Schema(example = "John", description = "The first name")
    private String firstName;

    @Schema(example = "1234567890", description = "The phone number")
    private String phone;

    @Schema(example = "john.doe@example.com", description = "The email address")
    private String email;

    @Schema(example = "7 rue X", description = "The address")
    private String address;

    @Schema(example = "Private", description = "The activity domain")
    private String activity;

    @Schema(example = "Ariana")
    private String governorate;

    @Schema(example = "Ariana")
    private String city;

    @Schema(example = "1", description = "The establishment id")
    private String establishmentId;

    @Schema(example = "B2", description = "The importance of the product")
    private String potential;

    @Schema(example = "Doctor", description = "The profession category")
    private String profession;

    @Schema(example = "Pediatrics", description = "The branch of medical practice")
    private String specialty;

    @Schema(example = "Doctor", description = "The level of training, expertise, and qualification")
    private String grade;

    @Schema(example = "Department Head", description = "The role or responsibilities")
    private String function;
    @Schema(example = "product_image.jpg", description = "URL or filename of the product image")
    private String image;
    @Schema(example = "2", description = "The number of children the product has")
    private String childrenNumber;

    @Schema(example = "XYZ Association", description = "The association the product is affiliated with")
    private String association;

    @Schema(example = "123 Street, City", description = "The home address of the product")
    private String homeAddress;

    @Schema(example = "987654321", description = "The personal phone number of the product")
    private String personalPhone;

    @Schema(example = "1990-05-25", description = "The birthday of the product")
    private String birthday;

    @Schema(example = "profile", description = "The profile of the product")
    private String profile;

    @Schema(example = "Additional comments about the product")
    private String comment;

    @Schema(example = "facebook.com/johndoe", description = "The Facebook profile of the product")
    private String facebook;

    @Schema(example = "linkedin.com/in/johndoe", description = "The LinkedIn profile of the product")
    private String linkedin;

    @Schema(example = "instagram.com/johndoe", description = "The Instagram profile of the product")
    private String instagram;

    @Schema(example = "VIP", description = "The classification of the product")
    private String classification;

    private String partnershipType;

    private String partnershipAgreement;

}