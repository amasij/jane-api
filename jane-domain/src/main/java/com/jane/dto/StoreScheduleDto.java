package com.jane.dto;

import com.jane.enumeration.DayConstant;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class StoreScheduleDto {
    @NotNull(message = "Cannot be blank")
    private DayConstant day;

    @NotNull(message = "Cannot be blank")
    @Min(value = 0)
    @Max(value = 23)
    private int open;

    @NotNull(message = "Cannot be blank")
    @Min(value = 0)
    @Max(value = 23)
    private Integer close;
}
