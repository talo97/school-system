package com.schoolsystem.classes;

import com.schoolsystem.teacher.EntityTeacher;
import com.schoolsystem.teacher.ServiceTeacher;
import com.schoolsystem.user.*;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ClassController {

    private final ServiceClass serviceClass;
    private final ModelMapper modelMapper;
    private final ServiceUser serviceUser;
    private final ServiceTeacher serviceTeacher;

    public ClassController(ServiceClass serviceClass, ModelMapper modelMapper, ServiceUser serviceUser, ServiceTeacher serviceTeacher) {
        this.serviceClass = serviceClass;
        this.modelMapper = modelMapper;
        this.serviceUser = serviceUser;
        this.serviceTeacher = serviceTeacher;
    }

    public List<ClassGetDTO> mapEntityListToDTO(List<EntityClass> entityClasses) {
        List<ClassGetDTO> classPostDTOList = new ArrayList<>();
        entityClasses.forEach(e -> {
            ClassGetDTO temp = modelMapper.map(e, ClassGetDTO.class);
            temp.setSupervisor(modelMapper.map(e.getSupervisor().getUsers(), UserGetDTO.class));
            temp.getSupervisor().setId(e.getSupervisor().getId());
            classPostDTOList.add(temp);
        });
        return classPostDTOList;
    }

    //shit coding but too lazy to fix xdd
    @PostMapping("/classes")
    public ResponseEntity<?> addClass(@Valid @RequestBody ClassPostDTO classPostDTO) {
        if (classPostDTO.containsEmptyValues()) {
            return ResponseEntity.badRequest().body("contains empty values");
        } else {
            Optional<EntityTeacher> teacher = serviceTeacher.get(classPostDTO.getSupervisorId());
            if (teacher.isPresent()) {
                if (!serviceClass.findBySupervisor(teacher.get()).isPresent()){
                return serviceClass.save(classPostDTO).map(response -> ResponseEntity.ok().body(classPostDTO))
                        .orElse(ResponseEntity.badRequest().build());
                }else{
                    return ResponseEntity.badRequest().body("Teacher has already class");
                }
            } else {
                return ResponseEntity.badRequest().body("Teacher with given Id doesn't exist");
            }
        }
    }

    @GetMapping("/classes")
    public ResponseEntity<?> getAllClasses() {
        return ResponseEntity.ok().body(mapEntityListToDTO(serviceClass.getAll()));
    }

    @GetMapping("/teacherClasses")
    @ApiOperation(value = "Returns list of distinct classes that current teacher teaches.",
            notes = "Teacher only operation.",
            response = ClassGetDTO.class)
    public ResponseEntity<?> getTeacherClasses() {
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        if (currentUser.getUserType().equals(EnumUserType.TEACHER)) {
            return ResponseEntity.ok(mapEntityListToDTO(serviceClass.findDistinctByTeacher(currentUser.getEntityTeacher())));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/isSupervisor")
    @ApiOperation(value = "Returns true if current user is supervisor of class or false otherwise",
            notes = "Teacher only operation")
    public ResponseEntity<?> isSupervisor() {
        return serviceUser.getCurrentUserFromToken().map(entityUser -> {
            if (entityUser.getUserType().equals(EnumUserType.TEACHER)) {
                return ResponseEntity.ok(serviceClass.findBySupervisor(entityUser.getEntityTeacher()).isPresent());
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current user is not a teacher");
            }
        }).orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/supervisorClass")
    @ApiOperation(value = "Returns class that current user is supervisor of.",
            notes = "Teacher only operation")
    public ResponseEntity<?> getSupervisorClass() {
        return serviceUser.getCurrentUserFromToken().map(entityUser -> {
            if (entityUser.getUserType().equals(EnumUserType.TEACHER)) {
                Optional<EntityClass> optionalEntityClass = serviceClass.findBySupervisor(entityUser.getEntityTeacher());
                if (optionalEntityClass.isPresent()) {
                    ClassGetDTO temp = modelMapper.map(optionalEntityClass.get(), ClassGetDTO.class);
                    temp.setSupervisor(modelMapper.map(optionalEntityClass.get().getSupervisor().getUsers(), UserGetDTO.class));
                    temp.getSupervisor().setId(optionalEntityClass.get().getSupervisor().getId());
                    return ResponseEntity.ok(temp);
                } else {
                    return ResponseEntity.badRequest().body("Current teacher is not class supervisor");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current user is not a teacher");
            }
        }).orElse(ResponseEntity.badRequest().build());
    }

}
