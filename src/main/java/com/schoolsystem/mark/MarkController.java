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
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
            notes = "Student/Parent only operation",
            response = TeacherCourseMarksGetDTO.class,
            responseContainer = "List")
    public ResponseEntity<?> getMarks() {
        EntityUser user = serviceUser.getCurrentUserFromToken().get();
        if (user.getUserType().equals(EnumUserType.STUDENT)) {
            return ResponseEntity.ok(serviceMark.getStudentMarks(user.getEntityStudent()));
        } else if (user.getUserType().equals(EnumUserType.PARENT)) {
            return ResponseEntity.ok(serviceMark.getStudentMarks(user.getEntityParent().getEntityStudent()));
        } else {
            return ResponseEntity.badRequest().body("Current user is not a student, therefor cannot access this endpoint");
        }
    }

    @GetMapping("/marks/{classId}/{teacherCourseId}")
    @ApiOperation(value = "Show all students marks from given class and given course",
            notes = "Endpoint available only for teacher of given class and subject",
            response = StudentMarksGetDTO.class,
            responseContainer = "List")
    public ResponseEntity<?> getMarksByClassAndCourse(@Valid @PathVariable Long classId, @Valid @PathVariable Long teacherCourseId) {
        return serviceClass.get(classId).flatMap(entityClass -> serviceTeacherCourse.get(teacherCourseId).map(teacherCourse -> {
            EntityUser currentUser = serviceUser.getCurrentUserFromToken().get(); //user exist coz otherwise this endpoint would be unavailable, no check required.
            if (currentUser.getUserType().equals(EnumUserType.TEACHER)) {
                return ResponseEntity.ok(serviceMark.getStudentsMarksByClassAndCourse(entityClass, teacherCourse));
            } else {
                return ResponseEntity.badRequest().body("Current user is either not a teacher or doesn't teach given class");
            }
        })).orElse(ResponseEntity.badRequest().body("Wrong class or teacherCourse Id"));
    }

    @PutMapping("/marks/{markId}")
    @ApiOperation(value = "Edit mark given by ID",
            notes = "Teacher only operation. Works only for assigned teachers.")
    public ResponseEntity<?> editMark(@Valid @RequestBody MarkPutDTO markEdit, @Valid @PathVariable Long markId) {
        if (markEdit.containsEmptyValue()) {
            return ResponseEntity.badRequest().body("Request body contains empty values");
        }
        return serviceMark.get(markId).map(entityMark -> {
            EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
            if (currentUser.getUserType().equals(EnumUserType.TEACHER)
                    && entityMark.getTeacherCourse().getTeacher().getId().equals(currentUser.getEntityTeacher().getId())) {
                entityMark.setEnumGrade(markEdit.getEnumGrade());
                LocalDateTime dateTime = LocalDateTime.now(ZoneId.systemDefault());
                ZoneId newZone = ZoneId.of("Europe/Warsaw");
                LocalDateTime newTime = dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(newZone).toLocalDateTime();
                entityMark.setLastChange(Date.valueOf(newTime.toLocalDate()));
                entityMark.setDescription(markEdit.getDescription());
                MarkShortGetDTO toReturn = new MarkShortGetDTO(serviceMark.save(entityMark).getId(), entityMark.getEnumGrade(), entityMark.getDescription(), entityMark.getLastChange());
                return ResponseEntity.ok(toReturn);
            } else {
                return ResponseEntity.badRequest().body("Current user is not responsible for this course.");
            }
        }).orElse(ResponseEntity.badRequest().body("Mark of given ID doesn't exist"));

    }

    @DeleteMapping("/marks/{markId}")
    @ApiOperation(value = "Delete mark by ID",
            notes = "Teacher only operation. Works only for assigned teachers.")
    public ResponseEntity<?> deleteMark(@Valid @PathVariable Long markId) {
        return serviceMark.get(markId).map(entityMark -> {
            EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
            if (currentUser.getUserType().equals(EnumUserType.TEACHER)
                    && entityMark.getTeacherCourse().getTeacher().getId().equals(currentUser.getEntityTeacher().getId())) {
                //teacher and owner of the mark
                serviceMark.delete(entityMark);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }).orElse(ResponseEntity.badRequest().build());
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
