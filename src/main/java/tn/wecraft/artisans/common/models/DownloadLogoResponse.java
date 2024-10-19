package tn.wecraft.artisans.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public class DownloadLogoResponse {
    private String fileType;
    private byte[] fileContent;
}
