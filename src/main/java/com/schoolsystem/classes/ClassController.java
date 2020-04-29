package com.schoolsystem.classes;

import com.schoolsystem.user.UserGetDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class ClassController {

    private final ServiceClass serviceClass;
    private final ModelMapper modelMapper;

    public ClassController(ServiceClass serviceClass, ModelMapper modelMapper) {
        this.serviceClass = serviceClass;
        this.modelMapper = modelMapper;
    }

    public List<ClassGetDTO> mapEntityListToDTO(List<EntityClass> entityClasses) {
        List<ClassGetDTO> classPostDTOList = new ArrayList<>();
        entityClasses.forEach(e -> {
            ClassGetDTO temp = modelMapper.map(e, ClassGetDTO.class);
            temp.setSupervisor(modelMapper.map(e.getSupervisor().getUsers(), UserGetDTO.class));
            classPostDTOList.add(temp);
        });
        return classPostDTOList;
    }

    @PostMapping("/classes")
    public ResponseEntity<ClassPostDTO> addClass(@Valid @RequestBody ClassPostDTO classPostDTO) {
        return serviceClass.save(classPostDTO).map(response -> ResponseEntity.ok().body(classPostDTO))
                .orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/classes")
    public ResponseEntity<?> getAllClasses() {
        return ResponseEntity.ok().body(mapEntityListToDTO(serviceClass.getAll()));
    }


}
