package com.maddy.recipes.dto;

import lombok.Data;
import java.util.List;

@Data
public class PageResponse<T> {
    private int offSet;
    private int pageSize;
    private long total;
    private List<T> data;
}
