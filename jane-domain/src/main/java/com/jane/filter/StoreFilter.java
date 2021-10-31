package com.jane.filter;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class StoreFilter {
    private Optional<Long> limit;
    private Optional<Long> offset;
    private Optional<String> name;
    private Optional<String> latitude;
    private Optional<String> longitude;
    private Optional<String> stateCode;
}
