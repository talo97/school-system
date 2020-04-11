package com.schoolsystem.user;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final ServiceUser serviceUser;

    private final ModelMapper modelMapper;

    public UserController(ServiceUser serviceUser, ModelMapper modelMapper) {
        this.serviceUser = serviceUser;
        this.modelMapper = modelMapper;
    }

    //TODO::test, change later
    @PostMapping("/addUser")
    public ResponseEntity<UserPostDTO> createUser(@Valid @RequestBody UserPostDTO user) {
        Optional<EntityUser> result;
        result = serviceUser.save(user);
        return result.map(response -> ResponseEntity.ok().body(modelMapper.map(response, UserPostDTO.class)))
                .orElse(ResponseEntity.badRequest().build());
    }

}
