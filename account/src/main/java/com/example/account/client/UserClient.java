package com.example.account.client;

import com.example.account.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.base-url}")
public interface UserClient {

    @GetMapping("/users/{id}/profile")
    UserResponse getUserProfile(@PathVariable("id") String userId);
}
