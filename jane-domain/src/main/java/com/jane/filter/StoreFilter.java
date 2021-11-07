package com.jane.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class StoreFilter {
    private Long limit;
    private Long offset;
    private String name;
    private String latitude;
    private String longitude;
    private String stateCode;
}
