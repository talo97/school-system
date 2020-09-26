package com.schoolsystem.presence;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.classes.ServiceClass;
import com.schoolsystem.common.Pair;
import com.schoolsystem.common.SchoolTimeUtil;
import com.schoolsystem.course.ServiceTeacherCourse;
import com.schoolsystem.lesson.EntityLesson;
import com.schoolsystem.lesson.EnumDayOfWeek;
import com.schoolsystem.lesson.EnumLessonNumber;
import com.schoolsystem.lesson.ServiceLesson;
import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.student.ServiceStudent;
import com.schoolsystem.teacher.EntityTeacher;
import com.schoolsystem.user.EntityUser;
import com.schoolsystem.user.EnumUserType;
import com.schoolsystem.user.ServiceUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class AttendanceController {
    //TODO:: Add teacher substitution.

    private SchoolTimeUtil schoolTimeUtil;
    private ServiceUser serviceUser;
    private ServiceLesson serviceLesson;
    private ServiceTeacherCourse serviceTeacherCourse;
    private ServiceStudent serviceStudent;
    private ServicePresence servicePresence;
    private ServiceClass serviceClass;

    public AttendanceController(SchoolTimeUtil schoolTimeUtil, ServiceUser serviceUser, ServiceLesson serviceLesson, ServiceTeacherCourse serviceTeacherCourse, ServiceStudent serviceStudent, ServicePresence servicePresence, ServiceClass serviceClass) {
        this.schoolTimeUtil = schoolTimeUtil;
        this.serviceUser = serviceUser;
        this.serviceLesson = serviceLesson;
        this.serviceTeacherCourse = serviceTeacherCourse;
        this.serviceStudent = serviceStudent;
        this.servicePresence = servicePresence;
        this.serviceClass = serviceClass;
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
                    enumDayOfWeek, enumLessonNumber);
            if (lessonOptional.isPresent()) {
                //check if presence was already saved (if so then convert already saved one to LessonPresenceDTO)
                List<EntityPresence> presences = servicePresence.find(lessonOptional.get(), schoolTimeUtil.getCurrentSqlDate());
                //if there are any record that means teacher did already presence, so return values that he passed
                if (presences.isEmpty()) {
                    List<EntityStudent> students = serviceStudent.findAllByStudentClass(lessonOptional.get().getEntityClass());
                    return ResponseEntity.ok().body(new LessonAttendanceDTO(students, lessonOptional.get().getId(),
                            lessonOptional.get().getTeacherCourse().getCourse().getName()));
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

    @ApiOperation(value = "Change attendance for given presence by ID")
    @PutMapping("/attendance/{attendanceID}")
    public ResponseEntity<?> changeAttendance(@Valid @PathVariable Long attendanceID) {
        Optional<EntityPresence> presence = servicePresence.get(attendanceID);
        if (presence.isPresent()) {
            presence.get().setWasPresent(!presence.get().getWasPresent());
            servicePresence.update(presence.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @ApiOperation(value = "Show current user(student) attendance.",
            notes = "Student/Parent only operation",
            response = UserAttendanceDTO.class,
            responseContainer = "List")
    @GetMapping("/myAttendance")
    public ResponseEntity<?> getMyAttendance() {
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        if (currentUser.getUserType().equals(EnumUserType.STUDENT)) {
            return ResponseEntity.ok().body(mapEntityPresenceToUserAttendanceDTO(servicePresence.find(currentUser.getEntityStudent())));
        } else if (currentUser.getUserType().equals(EnumUserType.PARENT)) {
            return ResponseEntity.ok().body(mapEntityPresenceToUserAttendanceDTO(servicePresence.find(currentUser.getEntityParent().getEntityStudent())));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Current user is not of STUDENT type, this endpoint is for students only.");
        }
    }

    @ApiOperation(value = "Get attendance of all students of current user(teacher, supervisor only) class.",
            notes = "Teacher(supervisor of class) only operation")
    @GetMapping("/classAttendance")
    public ResponseEntity<List<ClassAttendanceDTO>> getClassAttendance() {
        //check if current user is teacher
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        if (currentUser.getUserType().equals(EnumUserType.TEACHER)) {
            Optional<EntityClass> entityClass = serviceClass.findBySupervisor(currentUser.getEntityTeacher());
            if (entityClass.isPresent()) {
                Map<EntityStudent, List<UserAttendanceDTO>> mapStudentAttendance = prepareMapStudentAttendance(entityClass.get());
                return ResponseEntity.ok(mapEntityPresenceToClassAttendanceDTO(mapStudentAttendance));
            } else {
                return ResponseEntity.badRequest().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @ApiOperation(value = "Get attendance of given student by id.",
            notes = "Teacher only operation")
    @GetMapping("/classAttendance/{studentId}")
    public ResponseEntity<List<UserAttendanceDTO>> getClassAttendance(@Valid @PathVariable Long studentId) {
        //check if current user is teacher
        EntityUser currentUser = serviceUser.getCurrentUserFromToken().get();
        if (currentUser.getUserType().equals(EnumUserType.TEACHER)) {
            Optional<EntityStudent> student = serviceStudent.get(studentId);
            return student.map(entityStudent -> {
                List<EntityPresence> presences = servicePresence.find(entityStudent);
                return ResponseEntity.ok(mapEntityPresenceToUserAttendanceDTO(presences));
            }).orElse(ResponseEntity.badRequest().build());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Method prepares map of students and their attendance. Used for simplifying DTO object creation.
     *
     * @param entityClass - supervisor of class
     * @return Map
     */
    private Map<EntityStudent, List<UserAttendanceDTO>> prepareMapStudentAttendance(EntityClass entityClass) {
        Map<EntityStudent, List<UserAttendanceDTO>> mapStudentAttendance = new HashMap<>();
        List<EntityStudent> students = serviceStudent.findAllByStudentClass(entityClass);
        students.forEach(student -> {
            List<EntityPresence> presences = servicePresence.find(student);
            mapStudentAttendance.put(student, mapEntityPresenceToUserAttendanceDTO(presences));
        });
        return mapStudentAttendance;
    }

    private List<UserAttendanceDTO> mapEntityPresenceToUserAttendanceDTO(List<EntityPresence> presences) {
        List<UserAttendanceDTO> userAttendanceDTOS = new ArrayList<>();
        presences.forEach(presence -> {
            userAttendanceDTOS.add(new UserAttendanceDTO(presence));
        });
        return userAttendanceDTOS;
    }

    private List<ClassAttendanceDTO> mapEntityPresenceToClassAttendanceDTO(Map<EntityStudent, List<UserAttendanceDTO>> mapStudentAttendance) {
        List<ClassAttendanceDTO> userAttendanceDTOS = new ArrayList<>();
        mapStudentAttendance.forEach((student, attendanceList) -> {
            userAttendanceDTOS.add(new ClassAttendanceDTO(student, attendanceList));
        });
        return userAttendanceDTOS;
    }
}
