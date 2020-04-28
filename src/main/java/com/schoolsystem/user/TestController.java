package com.schoolsystem.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
//TODO:: delete this later
@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class TestController {

    @GetMapping("/")
    public ResponseEntity<?> hello(){
        return ResponseEntity.ok("hello there");
    }
}
