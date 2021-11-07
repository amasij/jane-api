package com.jane.pojo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreCreationPojo {
    private Long id;

    public StoreCreationPojo(Long id){
        this.id = id;
    }
}
