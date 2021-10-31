package com.jane;

import com.jane.dto.StoreCreationDto;
import com.jane.filter.StoreFilter;
import com.jane.pojo.QueryResultsPojo;
import com.jane.pojo.StoreCreationPojo;
import com.jane.pojo.StorePojo;

import java.util.List;

public interface StoreService {
    StoreCreationPojo createStore(StoreCreationDto dto);
    QueryResultsPojo<StorePojo> getStores(StoreFilter filter);
}
