package seaung.ratelimiter.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {
    @GetMapping("/hello/{id}")
    public String hello(@PathVariable String id) {
        return "hello world!";
    }
}
