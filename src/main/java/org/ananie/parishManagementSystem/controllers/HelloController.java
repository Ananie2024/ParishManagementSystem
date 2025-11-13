package org.ananie.parishManagementSystem.controllers;

import org.ananie.parishManagementSystem.dto.GreetingResponse;
import org.ananie.parishManagementSystem.dto.ApiResponse;
import org.ananie.parishManagementSystem.dto.WelcomeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
// 1. Marks the class as a Controller where return values are written directly to the response body (REST).
@RequestMapping("/hello") // 2. Maps all methods in this class to the base path /hello.
public class HelloController {

    // 3. Defines a handler method for GET requests to the path /hello
    @GetMapping
    public ResponseEntity<ApiResponse<WelcomeResponse>> sayHello() {
        WelcomeResponse welcomeResponse = new WelcomeResponse("Hello, Most welcomed User of Parish Management System application !");

        return ResponseEntity.ok(ApiResponse.success(welcomeResponse));
    }

    // 4. Defines a handler method for GET requests to the path /hello/greet
    // This example includes a Request Parameter for a dynamic response.
    @GetMapping("/greet")
    public ResponseEntity<ApiResponse<GreetingResponse>> greetUser(
            @RequestParam(value = "name", defaultValue = "Christ's Faithful") String name) {
       String greetingMessage = String.format("Greetings, %s! Yezu Akuzwe iteka ryose.", name);
       GreetingResponse greetingResponse = new GreetingResponse(greetingMessage, name);

        return ResponseEntity.ok(ApiResponse.success(greetingResponse));
    }
}