package com.schoolsystem.classes;

import com.schoolsystem.user.UserPostDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ClassController {

    @PostMapping("/addClass")
    public ResponseEntity<?> addClass(@Valid @RequestBody ClassPostDTO classPostDTO) {

        return ResponseEntity.badRequest().body("xd");
    }


}
