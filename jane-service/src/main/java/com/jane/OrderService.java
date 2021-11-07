package com.jane;

import com.jane.dto.DeliveryCostEstimateDto;
import com.jane.dto.OrderCreationDto;
import com.jane.pojo.DeliveryCostEstimatePojo;
import com.jane.pojo.OrderCreationPojo;


public interface OrderService {
    DeliveryCostEstimatePojo getDeliveryCost(Long customerId, DeliveryCostEstimateDto dto);
    OrderCreationPojo createOrder(Long customerId, OrderCreationDto dto);
}
