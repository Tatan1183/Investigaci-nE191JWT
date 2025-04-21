package com.app.login.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greeting")
public class GreetingController {

    @GetMapping("/sayHelloPublic")
    public String sayHello() {
        return "Hola desde Api JAAX";
    }

    @GetMapping("/sayHelloProtected")
    public String sayHelloProtected() {
        return "Hello desde Api JAAX Protected";
    }
}
