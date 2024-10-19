package tn.wecraft.artisans.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PagedEntity<T> {
    private List<T> entities;
    private int pageSize;
    private int pageNumber;
    private int totalNumberOfPages;
}

