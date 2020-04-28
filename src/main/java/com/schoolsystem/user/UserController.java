package com.schoolsystem.user;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.classes.ServiceClass;
import com.schoolsystem.parent.EntityParent;
import com.schoolsystem.parent.ServiceParent;
import com.schoolsystem.student.ServiceStudent;
import com.schoolsystem.teacher.ServiceTeacher;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final ServiceStudent serviceStudent;
    private final ServiceParent serviceParent;
    private final ServiceClass serviceClass;

    private final ModelMapper modelMapper;

    public UserController(ServiceUser serviceUser, ServiceTeacher serviceTeacher, ServiceStudent serviceStudent, ServiceParent serviceParent, ServiceClass serviceClass, ModelMapper modelMapper) {
        this.serviceUser = serviceUser;
        this.serviceTeacher = serviceTeacher;
        this.serviceStudent = serviceStudent;
        this.serviceParent = serviceParent;
        this.serviceClass = serviceClass;
        this.modelMapper = modelMapper;
    }

    private Optional<EntityUser> getCurrentUserFromToken() {
        return serviceUser.findByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PostMapping("/addTeacher")
    public ResponseEntity<UserGetDTO> addTeacher(@Valid @RequestBody UserPostDTO user) {
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        serviceTeacher.save(user);
        UserGetDTO savedUser = modelMapper.map(user, UserGetDTO.class);
        savedUser.setUserType(EnumUserType.TEACHER);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping("/getCurrentUser")
    public ResponseEntity<UserGetDTO> getCurrentUser() {
        Optional<EntityUser> currentUser = getCurrentUserFromToken();
        Optional<UserGetDTO> currentUserDTO = Optional.empty();
        if (currentUser.isPresent()) {
            currentUserDTO = Optional.of(modelMapper.map(currentUser.get(), UserGetDTO.class));
            currentUserDTO.get().setUserType(currentUser.get().getUserType());
        }
        return currentUserDTO.map(response -> ResponseEntity.ok().body(response))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/validateUsername")
    public ResponseEntity<Boolean> validateUsername(@Valid @RequestBody NameDTO name) {
        return serviceUser.findByLogin(name.getName()).map(response -> ResponseEntity.ok().body(false))
                .orElse(ResponseEntity.ok(true));
    }


    @GetMapping("/getTeachers")
    public ResponseEntity<List<UserGetDTO>> getTeachers() {
        List<UserGetDTO> lst = new ArrayList<>();
        serviceTeacher.getAll().forEach(e -> {
            lst.add(modelMapper.map(e.getUsers(), UserGetDTO.class));
        });
        return ResponseEntity.ok(lst);
    }

    @PostMapping("/addParentStudent")
    public ResponseEntity<?> addParentStudent(@Valid @RequestBody ParentStudentPostDTO toSave) {
        if (toSave.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Optional<EntityClass> entityClass = serviceClass.get(toSave.getStudent().getClassId());
        if (entityClass.isPresent()) {
            EntityParent entityParent = serviceParent.save(toSave.getParent());
            serviceStudent.save(toSave.getStudent(), entityParent, entityClass.get());
            return ResponseEntity.ok("XD");
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


}
