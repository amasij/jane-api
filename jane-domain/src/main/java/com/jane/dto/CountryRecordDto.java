package com.jane.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CountryRecordDto {
    private String name;
    private String alpha2;
    private String alpha3;
    private String internationalDialingCode;
    private boolean supported;
}
