package com.company.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE", url = "http://localhost:8083/api/v1/users")
public interface UserClient {

    @GetMapping("{id}/exists")
    boolean userExists(@PathVariable Long id);

}
