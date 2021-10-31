package com.jane.controllers;

import com.jane.StoreService;
import com.jane.dto.StoreCreationDto;
import com.jane.entity.Store;
import com.jane.filter.StoreFilter;
import com.jane.pojo.QueryResultsPojo;
import com.jane.pojo.StoreCreationPojo;
import com.jane.pojo.StorePojo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
@RequestMapping("store")
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/create")
    public ResponseEntity<StoreCreationPojo> createStore(@RequestBody @Valid StoreCreationDto dto) {
        return ResponseEntity.ok(storeService.createStore(dto));
    }

    @GetMapping("/search")
    public ResponseEntity<QueryResultsPojo<StorePojo>> searchStores(StoreFilter filter) {
        return ResponseEntity.ok(storeService.getStores(filter));
    }


}
