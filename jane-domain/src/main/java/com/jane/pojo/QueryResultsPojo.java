package com.jane.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QueryResultsPojo<T> {
    private Long limit;
    private Long offset;
    private List<T> results;
    private boolean empty;
    private Long total;

    public QueryResultsPojo(Long limit, Long offset, Long total, List<T> result){
        this.limit = limit;
        this.offset = offset;
        this.results = result;
        this.total = total;
        this.empty = result.isEmpty();
    }
}
