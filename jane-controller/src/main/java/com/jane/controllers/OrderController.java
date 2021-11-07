package com.jane.controllers;

import com.jane.OrderService;
import com.jane.dto.DeliveryCostEstimateDto;
import com.jane.dto.OrderCreationDto;
import com.jane.pojo.DeliveryCostEstimatePojo;
import com.jane.pojo.OrderCreationPojo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("order")
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/delivery-cost/{customerId}")
    public ResponseEntity<DeliveryCostEstimatePojo> getDeliveryCost(@PathVariable Long customerId, @Valid @RequestBody DeliveryCostEstimateDto dto){
        return ResponseEntity.ok(orderService.getDeliveryCost(customerId,dto));
    }

    @PostMapping("/create/{customerId}")
    public ResponseEntity<OrderCreationPojo> placeOrder(@PathVariable Long customerId, @Valid @RequestBody OrderCreationDto dto){
        return ResponseEntity.ok(orderService.createOrder(customerId,dto));
    }
}
