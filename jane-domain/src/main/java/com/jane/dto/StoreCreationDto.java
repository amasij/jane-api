package com.jane.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class StoreCreationDto {
    @NotBlank(message = "Store name cannot be blank")
    private String name;

    @NotBlank(message = "State code cannot be blank")
    private String stateCode;

    @NotBlank(message = "Latitude cannot be blank")
    private String latitude;

    @NotBlank(message = "Longitude cannot be blank")
    private String longitude;

    @NotNull(message = "Image ID cannot be blank")
    private Long logoId;

    @NotNull(message = "Store schedule cannot be blank")
    @Size(min = 1, max = 7)
    List<@Valid StoreScheduleDto> schedules;
}
