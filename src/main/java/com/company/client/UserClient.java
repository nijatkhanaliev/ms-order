package com.company.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${application.client.user.url}")
public interface UserClient {

    @GetMapping("{id}/exists")
    boolean userExists(@PathVariable Long id);

}
