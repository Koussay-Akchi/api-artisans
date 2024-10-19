package tn.wecraft.artisans.api.products.v1.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.MediaType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MergeRequest {
    @RestForm
    @NotNull
    @PartType(MediaType.APPLICATION_JSON)
    private List<String> idsToDelete;

    @RestForm
    @Valid
    @NotNull
    @PartType(MediaType.APPLICATION_JSON)
    private ProductRequest newProductRequest;

}
