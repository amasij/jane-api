package com.jane.pojo;

import com.jane.entity.Gps;
import com.jane.entity.State;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StorePojo {
    private String name;
    private String code;
    private State state;
    private Gps gps;
    private Long logoId;
}
