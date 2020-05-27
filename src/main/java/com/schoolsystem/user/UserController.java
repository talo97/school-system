package com.schoolsystem.user;

import com.schoolsystem.classes.ClassGetDTO;
import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.classes.ServiceClass;
import com.schoolsystem.mark.MarkGetDTO;
import com.schoolsystem.parent.EntityParent;
import com.schoolsystem.parent.ServiceParent;
import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.student.ServiceStudent;
import com.schoolsystem.student.StudentGetDTO;
import com.schoolsystem.teacher.EntityTeacher;
import com.schoolsystem.teacher.ServiceTeacher;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//TODO:: edition of all kind of users

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

    private boolean checkIfLoginIsAvailable(String login) {
        return !serviceUser.findByLogin(login).isPresent();
    }

    private final Pattern VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*[a-zA-Z\\d].*)[a-zA-Z\\d!@#$%&*]{3,}$", Pattern.CASE_INSENSITIVE);

    private final Pattern VALID_USERNAME_REGEX = Pattern.compile("^[a-z0-9_-]{3,25}$", Pattern.CASE_INSENSITIVE);

    private boolean validateLogin(String login) {
        Matcher matcher = VALID_USERNAME_REGEX.matcher(login);
        return matcher.matches();
    }

    private boolean validatePassword(String password) {
        Matcher matcher = VALID_PASSWORD_REGEX.matcher(password);
        return matcher.matches();
    }

    public List<StudentGetDTO> mapStudentListToGetDTO(List<EntityStudent> lst){
        List<StudentGetDTO> studentGetDTOS = new ArrayList<>();
        lst.forEach(e->{
            studentGetDTOS.add(mapStudentToGetDTO(e));
        });
        return studentGetDTOS;
    }

    private StudentGetDTO mapStudentToGetDTO(EntityStudent student) {
        StudentGetDTO temp = modelMapper.map(student.getUsers(), StudentGetDTO.class);
        temp.setStudentClass(modelMapper.map(student.getStudentClass(), ClassGetDTO.class));
        temp.getStudentClass().setSupervisor(modelMapper.map(student.getStudentClass().getSupervisor().getUsers(), UserGetDTO.class));
        temp.getStudentClass().getSupervisor().setId(student.getStudentClass().getSupervisor().getId());
        temp.setId(student.getId());
        return temp;
    }

    @PostMapping("/teachers")
    @ApiOperation(value = "Add teacher by given JSON",
            notes = "Admin only operation",
            response = UserGetDTO.class)
    public ResponseEntity<UserGetDTO> addTeacher(@Valid @RequestBody UserPostDTO user) {
        if (user.isEmpty() || !checkIfLoginIsAvailable(user.getLogin())
                || !validateLogin(user.getLogin()) || !validatePassword(user.getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        EntityTeacher savedTeacher = serviceTeacher.save(user);
        UserGetDTO dtoToReturn = modelMapper.map(user, UserGetDTO.class);
        dtoToReturn.setUserType(EnumUserType.TEACHER);
        dtoToReturn.setId(savedTeacher.getId());
        return ResponseEntity.ok(dtoToReturn);
    }


    @GetMapping("/currentUser")
    public ResponseEntity<UserGetDTO> getCurrentUser() {
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        Optional<UserGetDTO> currentUserDTO = Optional.empty();
        currentUserDTO = Optional.of(modelMapper.map(currentUser, UserGetDTO.class));
        currentUserDTO.get().setUserType(currentUser.getUserType());
        return currentUserDTO.map(response -> ResponseEntity.ok().body(response))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping(value = "/loginAvailable")
    public ResponseEntity<Boolean> checkIfLoginIsAvailable(@Valid @RequestBody NameDTO name) {
        return serviceUser.findByLogin(name.getName()).map(response -> ResponseEntity.ok().body(false))
                .orElse(ResponseEntity.ok(true));
    }


    @GetMapping("/teachers")
    public ResponseEntity<List<UserGetDTO>> getTeachers() {
        List<UserGetDTO> lst = new ArrayList<>();
        serviceTeacher.getAll().forEach(e -> {
            UserGetDTO temp = modelMapper.map(e.getUsers(), UserGetDTO.class);
            temp.setId(e.getId());
            lst.add(temp);
        });
        return ResponseEntity.ok(lst);
    }

    @GetMapping("/parents")
    public ResponseEntity<List<UserGetDTO>> getParents() {
        List<UserGetDTO> lst = new ArrayList<>();
        serviceParent.getAll().forEach(e -> {
            UserGetDTO temp = modelMapper.map(e.getUsers(), UserGetDTO.class);
            temp.setId(e.getId());
            lst.add(temp);
        });
        return ResponseEntity.ok(lst);
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentGetDTO>> getStudents() {
        List<StudentGetDTO> lst = new ArrayList<>();
        serviceStudent.getAll().forEach(e -> {
            lst.add(mapStudentToGetDTO(e));
        });
        return ResponseEntity.ok(lst);
    }

    @GetMapping("/students/{id}")
    @ApiOperation(value = "Returns all students of given class",
            notes = "Teacher and Admin only operation",
            response = UserGetDTO.class)
    public ResponseEntity<?> getStudentsByClass(@Valid @PathVariable Long id) {
        EntityUser currentUser =  serviceUser.getCurrentUserFromToken().get();
        if (currentUser.getUserType().equals(EnumUserType.TEACHER) || currentUser.getUserType().equals(EnumUserType.ADMIN)) {
            return serviceClass.get(id).map(e -> ResponseEntity.ok(mapStudentListToGetDTO(serviceStudent.findAllByStudentClass(e))))
                    .orElse(ResponseEntity.badRequest().build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    //XDDDDDDDD plz don't look
    @PostMapping("/parentsStudents")
    public ResponseEntity<ParentStudentGetDTO> addParentStudent(@Valid @RequestBody ParentStudentPostDTO toSave) {
        if (toSave.isEmpty() || !checkIfLoginIsAvailable(toSave.getStudent().getLogin())
                || !checkIfLoginIsAvailable(toSave.getParent().getLogin())
                || !validateLogin(toSave.getStudent().getLogin()) || !validatePassword(toSave.getStudent().getPassword())
                || !validateLogin(toSave.getParent().getLogin()) || !validatePassword(toSave.getParent().getPassword())
                || toSave.getStudent().getLogin().equals(toSave.getParent().getLogin())) {
            return ResponseEntity.badRequest().build();
        }
        Optional<EntityClass> entityClass = serviceClass.get(toSave.getStudent().getClassId());
        if (entityClass.isPresent()) {
            EntityParent savedParent = serviceParent.save(toSave.getParent());
            EntityStudent savedStudent = serviceStudent.save(toSave.getStudent(), savedParent, entityClass.get());
            ParentStudentGetDTO dtoToReturn = new ParentStudentGetDTO();
            dtoToReturn.setParent(modelMapper.map(savedParent.getUsers(), UserGetDTO.class));
            dtoToReturn.getParent().setUserType(EnumUserType.TEACHER);
            dtoToReturn.getParent().setId(savedParent.getId());
            dtoToReturn.setStudent(modelMapper.map(savedStudent.getUsers(), StudentGetDTO.class));
            dtoToReturn.getStudent().setStudentClass(modelMapper.map(savedStudent.getStudentClass(), ClassGetDTO.class));
            dtoToReturn.getStudent().getStudentClass().setSupervisor(modelMapper.map(savedStudent.getStudentClass().getSupervisor().getUsers(), UserGetDTO.class));
            dtoToReturn.getStudent().setId(savedStudent.getId());
            return ResponseEntity.ok(dtoToReturn);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
