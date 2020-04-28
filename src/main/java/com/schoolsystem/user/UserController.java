package com.schoolsystem.user;

import com.schoolsystem.teacher.EntityTeacher;
import com.schoolsystem.teacher.ServiceTeacher;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final ServiceUser serviceUser;
    private final ServiceTeacher serviceTeacher;

    private final ModelMapper modelMapper;

    public UserController(ServiceUser serviceUser, ServiceTeacher serviceTeacher, ModelMapper modelMapper) {
        this.serviceUser = serviceUser;
        this.serviceTeacher = serviceTeacher;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/addTeacher")
    public ResponseEntity<?> addTeacher(@Valid @RequestBody UserPostDTO user) {
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body("Contains empty field!");
        }
        serviceTeacher.save(user);
        return ResponseEntity.ok("Teacher added successfully");
    }

    //TODO::for testing purposes
    @GetMapping("/getTeachers")
    public ResponseEntity<List<UserGetDTO>> getTeachers() {
        List<UserGetDTO> lst = new ArrayList<>();
        serviceTeacher.getAll().forEach(e -> {
            lst.add(modelMapper.map(e.getUsers(), UserGetDTO.class));
        });
        return ResponseEntity.ok(lst);
    }


    //TODO::change return value
    @PostMapping("/addParentStudent")
    public ResponseEntity<?> addParentStudent(@Valid @RequestBody ParentStudentPostDTO parentStudentPostDTO) {
        if (parentStudentPostDTO.isEmpty()) {
            return ResponseEntity.badRequest().body("Contains empty field!");
        }
        serviceUser.save(parentStudentPostDTO.getParent(), EnumUserType.PARENT);
        return ResponseEntity.ok("TODO!!!!");
    }


    //TODO::test, change later
//    @PostMapping("/addUser")
//    public ResponseEntity<UserPostDTO> createUser(@Valid @RequestBody UserPostDTO user) {
//        Optional<EntityUser> result;
//        result = serviceUser.save(user);
//        return result.map(response -> ResponseEntity.ok().body(modelMapper.map(response, UserPostDTO.class)))
//                .orElse(ResponseEntity.badRequest().build());
//    }

}
