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

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private boolean validateLogin(String login) {
        Matcher matcher = VALID_USERNAME_REGEX.matcher(login);
        return matcher.matches();
    }

    private boolean validatePassword(String password) {
        Matcher matcher = VALID_PASSWORD_REGEX.matcher(password);
        return matcher.matches();
    }

    private boolean validateEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.matches();
    }

    public List<StudentGetDTO> mapStudentListToGetDTO(List<EntityStudent> lst) {
        List<StudentGetDTO> studentGetDTOS = new ArrayList<>();
        lst.forEach(e -> {
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
        temp.setUserId(student.getUsers().getId());
        return temp;
    }

    @PostMapping("/teachers")
    @ApiOperation(value = "Add teacher by given JSON",
            notes = "Admin only operation",
            response = UserGetDTO.class)
    public ResponseEntity<UserGetDTO> addTeacher(@Valid @RequestBody UserPostDTO user) {
        if (user.isEmpty() || !checkIfLoginIsAvailable(user.getLogin())
                || !validateLogin(user.getLogin()) || !validateEmail(user.getEmail()) || !validatePassword(user.getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        EntityTeacher savedTeacher = serviceTeacher.save(user);
        UserGetDTO dtoToReturn = modelMapper.map(user, UserGetDTO.class);
        dtoToReturn.setUserType(EnumUserType.TEACHER);
        dtoToReturn.setId(savedTeacher.getId());
        dtoToReturn.setUserId(savedTeacher.getUsers().getId());
        return ResponseEntity.ok(dtoToReturn);
    }


    @GetMapping("/currentUser")
    public ResponseEntity<UserGetDTO> getCurrentUser() {
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        Optional<UserGetDTO> currentUserDTO = Optional.empty();
        currentUserDTO = Optional.of(modelMapper.map(currentUser, UserGetDTO.class));
        currentUserDTO.get().setUserType(currentUser.getUserType());
        currentUserDTO.get().setUserId(currentUser.getId());
        switch (currentUser.getUserType()) {
            case TEACHER:
                currentUserDTO.get().setId(currentUser.getEntityTeacher().getId());
                Optional<EntityClass> optionalEntityClass = serviceClass.findBySupervisor(currentUser.getEntityTeacher());
                if (optionalEntityClass.isPresent()) {
                    currentUserDTO.get().setClassId(optionalEntityClass.get().getId());
                }
                break;
            case STUDENT:
                currentUserDTO.get().setId(currentUser.getEntityStudent().getId());
                currentUserDTO.get().setClassId(currentUser.getEntityStudent().getStudentClass().getId());
                break;
            case PARENT:
                currentUserDTO.get().setId(currentUser.getEntityParent().getId());
                currentUserDTO.get().setClassId(currentUser.getEntityParent().getEntityStudent().getStudentClass().getId());
                break;
            default:
                break;
        }
        return currentUserDTO.map(response -> ResponseEntity.ok().body(response))
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeDTO passwordChangeDTO) {
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        if (passwordChangeDTO.getOldPassword().equals(currentUser.getPassword()) && validatePassword(passwordChangeDTO.getNewPassword())) {
            currentUser.setPassword(passwordChangeDTO.getNewPassword());
            serviceUser.update(currentUser);
            return ResponseEntity.ok("password changed");
        } else {
            return ResponseEntity.badRequest().build();
        }
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
            temp.setUserId(e.getUsers().getId());
            Optional<EntityClass> optionalEntityClass = serviceClass.findBySupervisor(e);
            optionalEntityClass.ifPresent(entityClass -> {
                temp.setClassName(entityClass.getName());
            });
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
            temp.setClassId(e.getEntityStudent().getStudentClass().getId());
            temp.setUserId(e.getUsers().getId());
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

    @GetMapping("/studentsWithParents")
    public ResponseEntity<List<StudentWithParentDTO>> getStudentsWithParents() {
        List<StudentWithParentDTO> lst = new ArrayList<>();
        serviceStudent.getAll().forEach(e -> {
            StudentWithParentDTO temp = modelMapper.map(e.getUsers(), StudentWithParentDTO.class);
            temp.setUserId(e.getUsers().getId());
            temp.setParentFirstName(e.getParent().getUsers().getFirstName());
            temp.setParentLastName(e.getParent().getUsers().getLastName());
            lst.add(temp);
        });
        return ResponseEntity.ok(lst);
    }

    @GetMapping("/parentsWithStudents")
    public ResponseEntity<List<ParentWithStudentDTO>> getParentsWithStudents() {
        List<ParentWithStudentDTO> lst = new ArrayList<>();
        serviceParent.getAll().forEach(e -> {
            ParentWithStudentDTO temp = modelMapper.map(e.getUsers(), ParentWithStudentDTO.class);
            temp.setUserId(e.getUsers().getId());
            temp.setStudentFirstName(e.getEntityStudent().getUsers().getFirstName());
            temp.setStudentLastName(e.getEntityStudent().getUsers().getLastName());
            lst.add(temp);
        });
        return ResponseEntity.ok(lst);
    }

    @GetMapping("/students/{id}")
    @ApiOperation(value = "Returns all students of given class",
            notes = "Teacher and Admin only operation",
            response = UserGetDTO.class)
    public ResponseEntity<?> getStudentsByClass(@Valid @PathVariable Long id) {
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        if (currentUser.getUserType().equals(EnumUserType.TEACHER) || currentUser.getUserType().equals(EnumUserType.ADMIN)) {
            return serviceClass.get(id).map(e -> ResponseEntity.ok(mapStudentListToGetDTO(serviceStudent.findAllByStudentClass(e))))
                    .orElse(ResponseEntity.badRequest().build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @ApiOperation(value = "Admin only, change user information")
    @PutMapping("/users/{userId}")
    public ResponseEntity<?> editUser(@Valid @PathVariable Long userId, @Valid @RequestBody UserPutDTO userPutDTO) {
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        if (currentUser.getUserType() == EnumUserType.ADMIN) {
            Optional<EntityUser> userOptional = serviceUser.get(userId);
            if (userOptional.isPresent()) {
                EntityUser user = userOptional.get();
                user.setPhoneNumber(userPutDTO.getPhoneNumber());
                user.setEmail(userPutDTO.getEmail());
                user.setBirthDate(userPutDTO.getBirthDate());
                user.setFirstName(userPutDTO.getFirstName());
                user.setLastName(userPutDTO.getLastName());
                serviceUser.update(user);
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }


    //XDDDDDDDD plz don't look
    @PostMapping("/parentsStudents")
    public ResponseEntity<ParentStudentGetDTO> addParentStudent(@Valid @RequestBody ParentStudentPostDTO toSave) {
        if (toSave.isEmpty() || !checkIfLoginIsAvailable(toSave.getStudent().getLogin())
                || !checkIfLoginIsAvailable(toSave.getParent().getLogin())
                || !validateLogin(toSave.getStudent().getLogin()) || !validateEmail(toSave.getStudent().getEmail()) || !validatePassword(toSave.getStudent().getPassword())
                || !validateLogin(toSave.getParent().getLogin()) || !validateEmail(toSave.getParent().getEmail()) || !validatePassword(toSave.getParent().getPassword())
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
            dtoToReturn.getParent().setUserId(savedParent.getUsers().getId());
            dtoToReturn.setStudent(modelMapper.map(savedStudent.getUsers(), StudentGetDTO.class));
            dtoToReturn.getStudent().setStudentClass(modelMapper.map(savedStudent.getStudentClass(), ClassGetDTO.class));
            dtoToReturn.getStudent().getStudentClass().setSupervisor(modelMapper.map(savedStudent.getStudentClass().getSupervisor().getUsers(), UserGetDTO.class));
            dtoToReturn.getStudent().setId(savedStudent.getId());
            dtoToReturn.getStudent().setUserId(savedStudent.getUsers().getId());
            return ResponseEntity.ok(dtoToReturn);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
