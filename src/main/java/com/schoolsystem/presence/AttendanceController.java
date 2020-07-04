package com.schoolsystem.presence;

import com.schoolsystem.common.Pair;
import com.schoolsystem.common.SchoolTimeUtil;
import com.schoolsystem.course.ServiceTeacherCourse;
import com.schoolsystem.lesson.EntityLesson;
import com.schoolsystem.lesson.EnumDayOfWeek;
import com.schoolsystem.lesson.EnumLessonNumber;
import com.schoolsystem.lesson.ServiceLesson;
import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.student.ServiceStudent;
import com.schoolsystem.user.EntityUser;
import com.schoolsystem.user.EnumUserType;
import com.schoolsystem.user.ServiceUser;
import io.swagger.annotations.ApiOperation;
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
public class AttendanceController {
    //TODO::everything xd
    // Show weekly records to work with less data at once.
    // 1 - show all presence of current user (only when current user is STUDENT, otherwise 401 error)
    // 2 - show all presence of given user of given class (only for TEACHER supervisor of given class!),
    // basically same method as the one above

    private SchoolTimeUtil schoolTimeUtil;
    private ServiceUser serviceUser;
    private ServiceLesson serviceLesson;
    private ServiceTeacherCourse serviceTeacherCourse;
    private ServiceStudent serviceStudent;
    private ServicePresence servicePresence;

    public AttendanceController(SchoolTimeUtil schoolTimeUtil, ServiceUser serviceUser, ServiceLesson serviceLesson, ServiceTeacherCourse serviceTeacherCourse, ServiceStudent serviceStudent, ServicePresence servicePresence) {
        this.schoolTimeUtil = schoolTimeUtil;
        this.serviceUser = serviceUser;
        this.serviceLesson = serviceLesson;
        this.serviceTeacherCourse = serviceTeacherCourse;
        this.serviceStudent = serviceStudent;
        this.servicePresence = servicePresence;
    }

    @GetMapping("/dateTest")
    public void test() {
        System.out.println(schoolTimeUtil.getCurrentDayOfWeek());
        System.out.println(schoolTimeUtil.getLessonNumberByLocalDateTime());
        schoolTimeUtil.getLessonNumberByLocalDateTime().ifPresent(System.out::println);
    }


    //TODO::check if there is already record for presence in that day/lesson/class so that we won't repeat it
    @ApiOperation(value = "By current system time check if user(teacher) has any lessons, if so returns list of students of that lesson and lessonId",
            notes = "Teacher only operation, works on system time. Helper endpoint used before assigning attendance for given lesson.",
            response = LessonAttendanceDTO.class)
    @GetMapping("/attendanceInit")
    public ResponseEntity<?> getCurrentLessonStudents() {
        Optional<EnumDayOfWeek> dayOfWeek = schoolTimeUtil.getCurrentDayOfWeek();
        Optional<EnumLessonNumber> lessonNumber = schoolTimeUtil.getLessonNumberByLocalDateTime();
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        //1 check if current user is TEACHER type.
        if (!currentUser.getUserType().equals(EnumUserType.TEACHER)) {
            return ResponseEntity.badRequest().body("Current user is not a teacher. It is teacher only operation!");
        }
        return dayOfWeek.flatMap(enumDayOfWeek -> lessonNumber.map(enumLessonNumber -> {
            //2 check if teacher has any lesson for given day and hour
            //TODO::FOR TEST HARDCODED DAY AND LESSON NUMBER !!!!!!!! CHANGEW LATER !!!!!!!!!!!!!!!!
            Optional<EntityLesson> lessonOptional = serviceLesson.find(serviceTeacherCourse.findByTeacher(currentUser.getEntityTeacher()),
                    EnumDayOfWeek.MONDAY, EnumLessonNumber.FIRST);
            if (lessonOptional.isPresent()) {
                //check if presence was already saved (if so then convert already saved one to LessonPresenceDTO)
                List<EntityPresence> presences = servicePresence.find(lessonOptional.get(), schoolTimeUtil.getCurrentSqlDate());
                //if there are any record that means teacher did already presence, so return values that he passed
                if (presences.isEmpty()) {
                    List<EntityStudent> students = serviceStudent.findAllByStudentClass(lessonOptional.get().getEntityClass());
                    return ResponseEntity.ok().body(new LessonAttendanceDTO(students, lessonOptional.get().getId()));
                }
                return ResponseEntity.ok(new LessonAttendanceDTO(presences));
            } else {
                return ResponseEntity.badRequest().body("No Lessons for given teacher in current time");
            }
        })).orElse(ResponseEntity.badRequest().body("It is free time, no lessons occur in that time."));
    }

    @ApiOperation(value = "Add attendance for given lesson in given time, before that use /api/presenceInit endpoint to get current lesson")
    @PostMapping("/attendance")
    public ResponseEntity<?> addAttendance(@Valid @RequestBody LessonAttendanceDTO lessonAttendanceDTO) {
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get(); //kozak nie? taki warning spoko
        Optional<EntityLesson> lessonOptional = serviceLesson.get(lessonAttendanceDTO.getLessonId());
        return lessonOptional.map(entityLesson -> {
            //TODO::delete maybe
            if (!entityLesson.getTeacherCourse().getTeacher().getId().equals(currentUser.getEntityTeacher().getId())) {
                return ResponseEntity.badRequest().body("Teacher is not responsible for given lesson");
            }
            List<Pair<Boolean, EntityStudent>> studentsPresence = new ArrayList<>();
            lessonAttendanceDTO.getStudents().forEach(studentPresenceDTO -> {
                Optional<EntityStudent> entityStudent = serviceStudent.get(studentPresenceDTO.getStudentId());
                entityStudent.ifPresent(student -> {
                    studentsPresence.add(Pair.of(studentPresenceDTO.getIsPresent(), student));
                });
            });
            //TODO::delete maybe
            if (lessonAttendanceDTO.getStudents().size() != studentsPresence.size()) {
                return ResponseEntity.badRequest().body("Some students ID were not existent");
            }
            servicePresence.saveOrUpdateAll(studentsPresence, entityLesson);
            return ResponseEntity.ok().body("Saved successfully");
        }).orElse(ResponseEntity.badRequest().body("Wrong arguments, couldn't find lesson"));
    }

    private List<UserAttendanceDTO> mapPresenceToUserPresenceDTO(List<EntityPresence> presences) {
        List<UserAttendanceDTO> userAttendanceDTOS = new ArrayList<>();
        presences.forEach(presence -> {
            userAttendanceDTOS.add(new UserAttendanceDTO(presence));
        });
        return userAttendanceDTOS;
    }

    @ApiOperation(value = "Show current user(student) attendance.",
            notes = "Student only operation",
            response = UserAttendanceDTO.class,
            responseContainer = "List")
    @GetMapping("/myAttendance")
    public ResponseEntity<?> getMyAttendance() {
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        if (currentUser.getUserType().equals(EnumUserType.STUDENT)) {
            return ResponseEntity.ok().body(mapPresenceToUserPresenceDTO(servicePresence.find(currentUser.getEntityStudent())));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current user is not of STUDENT type, this endpoint is for students only.");
        }
    }

    @ApiOperation(value = "Get attendance of all students of current user(teacher, supervisor only) class.",
            notes = "Teacher(supervisor of class) only operation")
    // response = UserPresenceDTO.class,
    // responseContainer = "List")
    @GetMapping("/classAttendance")
    public ResponseEntity<?> getClassAttendance() {
        throw new UnsupportedOperationException();
    }
}
