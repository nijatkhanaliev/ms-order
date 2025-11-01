package com.company.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient(name = "inventory-service", url = "${application.client.inventory.url}")
public interface InventoryClient {

    @GetMapping("/{id}/price")
    BigDecimal getProductPriceById(@PathVariable Long id);

}
