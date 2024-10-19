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

    private String nom;

    private String description;

    private Float prix;

    private Boolean stock;

    private Boolean faitmain;

    private String gouvernorat;

    private String ville;

    private String address;

    private String artisan;

    private String email;

    private String facebook;

    private String instagram;

    @BsonProperty("created_at")
    private String createdAt;

    private String categorie;

}