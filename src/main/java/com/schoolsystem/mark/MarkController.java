package com.schoolsystem.mark;

import com.schoolsystem.classes.ServiceClass;
import com.schoolsystem.course.EntityTeacherCourse;
import com.schoolsystem.course.ServiceTeacherCourse;
import com.schoolsystem.lesson.ServiceLesson;
import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.student.ServiceStudent;
import com.schoolsystem.user.EntityUser;
import com.schoolsystem.user.EnumUserType;
import com.schoolsystem.user.ServiceUser;
import io.swagger.annotations.ApiOperation;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class MarkController {

    private final ServiceMark serviceMark;
    private final ServiceUser serviceUser;
    private final ServiceStudent serviceStudent;
    private final ServiceTeacherCourse serviceTeacherCourse;
    private final ServiceLesson serviceLesson;
    private final ModelMapper modelMapper;
    private final ServiceClass serviceClass;

    public MarkController(ServiceMark serviceMark, ServiceUser serviceUser, ServiceStudent serviceStudent, ServiceTeacherCourse serviceTeacherCourse, ServiceLesson serviceLesson, ModelMapper modelMapper, ServiceClass serviceClass) {
        this.serviceMark = serviceMark;
        this.serviceUser = serviceUser;
        this.serviceStudent = serviceStudent;
        this.serviceTeacherCourse = serviceTeacherCourse;
        this.serviceLesson = serviceLesson;
        this.modelMapper = modelMapper;
        this.serviceClass = serviceClass;
    }

    @GetMapping("/myMarks")
    @ApiOperation(value = "Show current student marks (by JWT token)",
            notes = "Student only operation",
            response = TeacherCourseMarksGetDTO.class,
            responseContainer = "List")
    public ResponseEntity<?> getMarks() {
        EntityUser user = serviceUser.getCurrentUserFromToken().get();
        if (user.getUserType().equals(EnumUserType.STUDENT)) {
            return ResponseEntity.ok(serviceMark.getStudentMarks(user.getEntityStudent()));
        } else {
            return ResponseEntity.badRequest().body("Current user is not a student, therefor cannot access this endpoint");
        }
    }

    //TODO:: change for more specific with /classId/courseId, or prepare DTO object with Course -> studentlist/markslist
//    @GetMapping("/classMarks/{id}")
//    @ApiOperation(value = "Show all marks from all subjects of given by ID class",
//            notes = "Supervisor only operation. For supervisor of given class",
//            response = MarkGetDTO.class)
//    public ResponseEntity<?> getClassMarks(@Valid @PathVariable Long id) {
//        EntityUser user = serviceUser.getCurrentUserFromToken().get();
//        return serviceClass.get(id).map(entityClass -> {
//            if (user.getUserType().equals(EnumUserType.TEACHER) && user.getEntityTeacher().getId().equals(entityClass.getSupervisor().getId())) {
//                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
//                List<MarkGetDTO> lst = modelMapper.map(serviceMark.findAllByClass(entityClass), new TypeToken<List<MarkGetDTO>>() {
//                }.getType());
//                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
//                return ResponseEntity.ok(lst);
//            } else {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current user is not supervisor of given class");
//            }
//        }).orElse(ResponseEntity.badRequest().body("There is no class with given ID"));
//    }

    @GetMapping("/marks/{classId}/{teacherCourseId}")
    @ApiOperation(value = "Show all students marks from given class and given course",
            notes = "Endpoint available only for teacher of given class and subject",
            response = StudentMarksGetDTO.class,
            responseContainer = "List")
    public ResponseEntity<?> getMarksByClassAndCourse(@Valid @PathVariable Long classId, @Valid @PathVariable Long teacherCourseId) {
        return serviceClass.get(classId).flatMap(entityClass -> serviceTeacherCourse.get(teacherCourseId).map(teacherCourse -> {
            EntityUser currentUser = serviceUser.getCurrentUserFromToken().get(); //user exist coz otherwise this endpoint would be unavailable, no check required.
            if (currentUser.getUserType().equals(EnumUserType.TEACHER)
                    && teacherCourse.getTeacher().getId().equals(currentUser.getEntityTeacher().getId())
                    && serviceLesson.isThereLessonForTeacherCourseInClass(teacherCourse, entityClass)) {
                return ResponseEntity.ok(serviceMark.getStudentsMarksByClassAndCourse(entityClass, teacherCourse));
            } else {
                return ResponseEntity.badRequest().body("Current user is either not a teacher or doesn't teach given class");
            }
        })).orElse(ResponseEntity.badRequest().body("Wrong class or teacherCourse Id"));
    }

    @PostMapping("/marks")
    @ApiOperation(value = "Add mark to given student",
            notes = "Teacher only operation. Works only for assigned teachers")
    public ResponseEntity<?> addMark(@Valid @RequestBody MarkPostDTO mark) {
        if (mark.containsEmptyValue()) {
            return ResponseEntity.badRequest().body("Request body contains empty values");
        }
        Optional<EntityTeacherCourse> optionalTeacherCourse = serviceTeacherCourse.get(mark.getTeacherCourseId());
        Optional<EntityStudent> optionalStudent = serviceStudent.get(mark.getStudentId());
        //check if teacher is assigned
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        return optionalTeacherCourse.flatMap(teacherCourse -> optionalStudent.map(student -> {
            if (currentUser.getUserType().equals(EnumUserType.TEACHER)
                    && teacherCourse.getTeacher().getId().equals(currentUser.getEntityTeacher().getId())
                    && serviceLesson.isThereLessonForTeacherCourseInClass(teacherCourse, student.getStudentClass())) {
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
                MarkGetDTO toReturn = modelMapper.map(serviceMark.save(mark, student, teacherCourse), MarkGetDTO.class);
                modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);
                return ResponseEntity.ok(toReturn);
            } else {
                return ResponseEntity.badRequest().body("Current user is either not a teacher or doesn't teach given class");
            }
        })).orElse(ResponseEntity.badRequest().build());
    }

}
