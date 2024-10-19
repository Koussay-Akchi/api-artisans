package tn.wecraft.artisans.common.utils;

import jakarta.activation.UnsupportedDataTypeException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import tn.wecraft.artisans.artisans.files.repository.ApiFilesRestClient;

import java.io.File;

@ApplicationScoped
public class FilesApiUtils {
    @Inject
    @RestClient
    ApiFilesRestClient apiFilesRestClient;

    public String uploadFile(FileUpload logo) throws UnsupportedDataTypeException {
        File file = logo.uploadedFile().toFile();
        String fileType = logo.contentType();
        String fileId = "";
        if (fileType != null) {
            if (fileType.equals("image/png")) {
                fileId = apiFilesRestClient.uploadPngImageFiles(file).get(0);
            } else if (fileType.equals("image/jpeg")) {
                fileId = apiFilesRestClient.uploadJpegImageFiles(file).get(0);
            } else {
                throw new UnsupportedDataTypeException("Unsupported file type");
            }
        }
        return fileId;
    }
}
