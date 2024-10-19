package tn.wecraft.artisans.api.products.v1.errors;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import tn.wecraft.socle.api.common.errors.IErrorCode;
import tn.wecraft.socle.api.common.exceptions.BadRequestException;

@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode implements IErrorCode {

    PRODUCT_NOT_FOUND("The product %s does not exist"),
    PRODUCT_ALREADY_EXISTS("The product with rib %s already exists"),
    ORDERBY_FIELD_NOT_FOUND("The Field %s is not known"),
    AVATAR_DOES_NOT_EXIST("Logo does not exist"),
    UNSUPPORTED_FILE_TYPE("The file type %s is not supported."),
    MERGE_FAILED ("Error Merging duplicates ");
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
