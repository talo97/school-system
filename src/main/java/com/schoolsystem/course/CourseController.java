package com.schoolsystem.course;

import com.schoolsystem.classes.ServiceClass;
import com.schoolsystem.lesson.ServiceLesson;
import com.schoolsystem.mark.TeacherCourseMarksGetDTO;
import com.schoolsystem.teacher.EntityTeacher;
import com.schoolsystem.teacher.ServiceTeacher;
import com.schoolsystem.user.EntityUser;
import com.schoolsystem.user.EnumUserType;
import com.schoolsystem.user.ServiceUser;
import com.schoolsystem.user.UserGetDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class CourseController {

    private final ServiceCourse serviceCourse;
    private final ServiceTeacher serviceTeacher;
    private final ServiceUser serviceUser;
    private final ServiceClass serviceClass;
    private final ServiceTeacherCourse serviceTeacherCourse;
    private final ServiceLesson serviceLesson;
    private final ModelMapper modelMapper;

    public CourseController(ServiceCourse serviceCourse, ServiceTeacher serviceTeacher, ServiceUser serviceUser, ServiceClass serviceClass, ServiceTeacherCourse serviceTeacherCourse, ServiceLesson serviceLesson, ModelMapper modelMapper) {
        this.serviceCourse = serviceCourse;
        this.serviceTeacher = serviceTeacher;
        this.serviceUser = serviceUser;
        this.serviceClass = serviceClass;
        this.serviceTeacherCourse = serviceTeacherCourse;
        this.serviceLesson = serviceLesson;
        this.modelMapper = modelMapper;
    }

    @ApiOperation(value = "add new Course")
    @PostMapping("/courses")
    public ResponseEntity<CourseGetDTO> addCourse(@Valid @RequestBody CoursePostDTO course) {
        if (course.getName() == null || course.getName().isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            EntityCourse toSave = new EntityCourse();
            toSave.setName(course.getName());
            return ResponseEntity.ok(modelMapper.map(serviceCourse.save(toSave), CourseGetDTO.class));
        }
    }

    @ApiOperation(value = "Get all available courses")
    @GetMapping("/courses")
    public ResponseEntity<List<CourseGetDTO>> getAllCourses() {
        return ResponseEntity.ok(modelMapper.map(serviceCourse.getAll(), new TypeToken<List<CourseGetDTO>>() {
        }.getType()));
    }

    @ApiOperation(value = "Delete course")
    @DeleteMapping("courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        Optional<EntityCourse> course = serviceCourse.get(id);
        if (course.isPresent()) {
            if (!serviceTeacherCourse.findByCourse(course.get()).isEmpty()) {
                return ResponseEntity.badRequest().body("Course is used in TeacherCourse table. Can't delete it until all associations are removed");
            } else {
                serviceCourse.delete(course.get());
                return ResponseEntity.ok("Course was successfully removed");
            }
        } else {
            return ResponseEntity.badRequest().body("Course with given ID doesn't exist");
        }
    }

    @ApiOperation(value = "Edit course by ID")
    @PutMapping("courses/{id}")
    public ResponseEntity<?> editCourse(@Valid @RequestBody CoursePostDTO courseNew, @Valid @PathVariable Long id) {
        Optional<EntityCourse> course = serviceCourse.get(id);
        if (course.isPresent()) {
            if (courseNew.getName() == null || courseNew.getName().isEmpty()) {
                return ResponseEntity.badRequest().build();
            } else {
                course.get().setName(courseNew.getName());
                serviceCourse.save(course.get());
                return ResponseEntity.ok(modelMapper.map(course.get(), CourseGetDTO.class));
            }
        } else {
            return ResponseEntity.badRequest().body("Course with given ID doesn't exist");
        }
    }

    @ApiOperation(value = "Allocate course to teacher")
    @PostMapping("/teacherCourses")
    public ResponseEntity<?> addTeacherCourse(@Valid @RequestBody TeacherCoursePostDTO teacherCoursePostDTO) {
        Optional<EntityTeacher> teacher = serviceTeacher.get(teacherCoursePostDTO.getTeacherId());
        Optional<EntityCourse> course = serviceCourse.get(teacherCoursePostDTO.getCourseId());
        if (!teacher.isPresent()) {
            return ResponseEntity.badRequest().body("Teacher with given ID doesn't exist");
        } else if (!course.isPresent()) {
            return ResponseEntity.badRequest().body("Course with given ID doesn't exist");
        } else if (serviceTeacherCourse.findByCourseIdAndTeacherId(teacherCoursePostDTO.getCourseId(), teacherCoursePostDTO.getTeacherId()).isPresent()) {
            return ResponseEntity.badRequest().body("There is already exactly same record saved");
        }
        TeacherCourseGetDTO savedTeacherCourse = new TeacherCourseGetDTO();
        teacher.ifPresent(entityTeacher -> course.ifPresent(entityCourse -> {
            EntityTeacherCourse entityTeacherCourse = new EntityTeacherCourse();
            entityTeacherCourse.setCourse(entityCourse);
            entityTeacherCourse.setTeacher(entityTeacher);
            savedTeacherCourse.setId(serviceTeacherCourse.save(entityTeacherCourse).getId());
            savedTeacherCourse.setCourse(modelMapper.map(entityCourse, CourseGetDTO.class));
            savedTeacherCourse.setTeacher(modelMapper.map(entityTeacher.getUsers(), UserGetDTO.class));
        }));
        return ResponseEntity.ok(savedTeacherCourse);
    }

    @ApiOperation(value = "Get all teacher-courses connections")
    @GetMapping("/teacherCourses")
    public ResponseEntity<?> getAllTeacherCourses() {
        List<TeacherCourseGetDTO> dtoList = new ArrayList<>();
        serviceTeacherCourse.getAll().forEach(e -> {
            TeacherCourseGetDTO temp = new TeacherCourseGetDTO(e.getId(), modelMapper.map(e.getCourse(), CourseGetDTO.class), modelMapper.map(e.getTeacher().getUsers(), UserGetDTO.class));
            temp.getTeacher().setId(e.getTeacher().getId());
            dtoList.add(temp);
        });
        return ResponseEntity.ok(dtoList);
    }

    @ApiOperation(value = "Get all teacher-courses of given class by Id")
    @GetMapping("/teacherCourses/{classId}")
    public ResponseEntity<?> getAllTeacherCourses(@Valid @PathVariable Long classId) {
        return serviceClass.get(classId).map(entityClass -> {
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
            List<TeacherCourseGetDTO> lst = modelMapper.map(serviceLesson.findDistinctTeacherCoursesOfGivenClass(entityClass), new TypeToken<List<TeacherCourseGetDTO>>() {
            }.getType());
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
            return ResponseEntity.ok(lst);
        }).orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/myCourses")
    @ApiOperation(value = "Returns list of courses of current user(teacher).",
            notes = "Teacher only operation.",
            response = CourseGetDTO.class)
    public ResponseEntity<?> getCurrentTeacherCourses() {
        EntityUser entityUser = serviceUser.getCurrentUserFromToken().get();
        if (entityUser.getUserType().equals(EnumUserType.TEACHER)) {
            List<TeacherCourseGetDTO> courseGetDTOS = new ArrayList<>();
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
            serviceTeacherCourse.findByTeacher(entityUser.getEntityTeacher()).forEach(e -> courseGetDTOS.add(modelMapper.map(e, TeacherCourseGetDTO.class)));
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
            return ResponseEntity.ok(courseGetDTOS);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    //TODO:: maybe will return duplicates values dunno, have to test
    @GetMapping("/myCourses/{classId}")
    @ApiOperation(value = "Returns list of courses of current user(teacher) for given class",
            notes = "Teacher only operation.")
    public ResponseEntity<?> getCurrentTeacherCourses(@Valid @PathVariable Long classId) {
        EntityUser entityUser = serviceUser.getCurrentUserFromToken().get();
        if (entityUser.getUserType().equals(EnumUserType.TEACHER)) {
            return serviceClass.get(classId).map(entityClass -> {
                List<TeacherCourseGetDTO> toReturn;
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
                List<EntityTeacherCourse> entityTeacherCourses = new ArrayList<>();
                serviceLesson.findAllByTeacherAndClass(entityUser.getEntityTeacher(), entityClass).forEach(e -> entityTeacherCourses.add(e.getTeacherCourse()));
                toReturn = modelMapper.map(entityTeacherCourses.stream().distinct().collect(Collectors.toList()), new TypeToken<List<TeacherCourseGetDTO>>() {
                }.getType());
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
                return ResponseEntity.ok(toReturn);
            }).orElse(ResponseEntity.badRequest().build());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
