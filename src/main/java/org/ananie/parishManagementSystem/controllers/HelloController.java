package org.ananie.parishManagementSystem.controllers;

import org.ananie.parishManagementSystem.dto.response.GreetingResponse;
import org.ananie.parishManagementSystem.dto.response.ApiResponse;
import org.ananie.parishManagementSystem.dto.response.WelcomeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class HelloController {

    // 1. Serves the main landing page (HTML)
   /* @GetMapping
    public String showLandingPage() {
        return "index"; // Returns index.html template
    } */

    // 2. Serves the faithful management page (HTML)
    @GetMapping("/faithful")
    public String showFaithfulPage() {
        return "faithful"; // Returns faithful.html template
    }

    // 3. REST endpoint for welcome message (JSON)
    @GetMapping("/api/hello")
    @ResponseBody
    public ResponseEntity<ApiResponse<WelcomeResponse>> sayHello() {
        WelcomeResponse welcomeResponse = new WelcomeResponse(
                "Hello, Most welcomed User of Parish Management System application!"
        );
        return ResponseEntity.ok(ApiResponse.success(welcomeResponse));
    }

    // 4. REST endpoint for personalized greeting (JSON)
    @GetMapping("/api/hello/greet")
    @ResponseBody
    public ResponseEntity<ApiResponse<GreetingResponse>> greetUser(
            @RequestParam(value = "name", defaultValue = "Christ's Faithful") String name) {
        String greetingMessage = String.format("Greetings, %s! Yezu Akuzwe iteka ryose.", name);
        GreetingResponse greetingResponse = new GreetingResponse(greetingMessage, name);
        return ResponseEntity.ok(ApiResponse.success(greetingResponse));
    }
}