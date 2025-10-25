package com.company.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "INVENTORY-SERVICE", url = "http://localhost:8085/api/v1/products")
public interface InventoryClient {

    @GetMapping("/{id}/price")
    BigDecimal getProductPriceById(@PathVariable Long id);

}
