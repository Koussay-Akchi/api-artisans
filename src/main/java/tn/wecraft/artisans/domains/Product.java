package tn.wecraft.artisans.domains;
import io.quarkus.mongodb.panache.PanacheMongoEntityBase;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

@MongoEntity(collection = "products")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Product extends PanacheMongoEntityBase {

    @BsonId
    private ObjectId id;

    @BsonProperty("tenant_id")
    private String tenantId;

    @BsonProperty("last_name")
    private String lastName;

    @BsonProperty("first_name")
    private String firstName;

    private String phone;

    private String email;

    private String governorate;

    private String city;

    private String address;

    private String activity;

    private String image;

    private boolean archived;

    private String potential;

    private String profession;

    private String specialty;

    private String grade;

    private String function;

    @BsonProperty("establishment_id")
    private String establishmentId;

    @BsonProperty("children_number")
    private String childrenNumber;

    private String association;

    @BsonProperty("home_address")
    private String homeAddress;

    @BsonProperty("personal_phone")
    private String personalPhone;

    private String birthday;

    //no idea what this is, will ask on figma
    private String profile;

    private String comment;

    private String facebook;

    private String linkedin;

    private String instagram;

    private String classification;

    @BsonProperty("created_at")
    private String createdAt;

    private String partnershipType;

    private String partnershipAgreement;

}