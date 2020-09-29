package com.schoolsystem.report;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.classes.ServiceClass;
import com.schoolsystem.course.EntityTeacherCourse;
import com.schoolsystem.course.ServiceTeacherCourse;
import com.schoolsystem.lesson.EntityLesson;
import com.schoolsystem.lesson.ServiceLesson;
import com.schoolsystem.mark.EntityMark;
import com.schoolsystem.mark.EnumGrade;
import com.schoolsystem.mark.ServiceMark;
import com.schoolsystem.presence.EntityPresence;
import com.schoolsystem.presence.ServicePresence;
import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.student.ServiceStudent;
import com.schoolsystem.teacher.EntityTeacher;
import com.schoolsystem.teacher.ServiceTeacher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@RestController
@RequestMapping("/api/report")
@CrossOrigin()
public class ReportController {


    //teacher detailed hours by each teacher course they have.
    // average grades for each class + average for each student

    private ServiceClass serviceClass;
    private ServiceTeacher serviceTeacher;
    private ServiceTeacherCourse serviceTeacherCourse;
    private ServiceStudent serviceStudent;
    private ServiceLesson serviceLesson;
    private ServicePresence servicePresence;
    private ServiceMark serviceMark;


    public ReportController(ServiceClass serviceClass, ServiceTeacher serviceTeacher, ServiceTeacherCourse serviceTeacherCourse, ServiceStudent serviceStudent, ServiceLesson serviceLesson, ServicePresence servicePresence, ServiceMark serviceMark) {
        this.serviceClass = serviceClass;
        this.serviceTeacher = serviceTeacher;
        this.serviceTeacherCourse = serviceTeacherCourse;
        this.serviceStudent = serviceStudent;
        this.serviceLesson = serviceLesson;
        this.servicePresence = servicePresence;
        this.serviceMark = serviceMark;
    }

    @GetMapping("/attendanceClasses")
    public ResponseEntity<List<ClassReportAttendanceDTO>> reportClasses() {
        List<EntityClass> classes = serviceClass.getAll();
        List<ClassReportAttendanceDTO> classAttendances = new ArrayList<>();
        for (EntityClass entityClass : classes) {
            ClassReportAttendanceDTO classReportAttendanceDTO = new ClassReportAttendanceDTO();
            classReportAttendanceDTO.setClassName(entityClass.getName());
            classReportAttendanceDTO.setEducationStage(entityClass.getEnumEducationStage());
            List<EntityLesson> classLessons = serviceLesson.findAllByClass(entityClass);
            classReportAttendanceDTO.setHoursPerWeek(classLessons.size());
            List<EntityStudent> students = serviceStudent.findAllByStudentClass(entityClass);
            List<StudentTotalAttendanceDTO> studentTotalAttendanceDTOS = new ArrayList<>();
            int totalHours = 0;
            for (EntityStudent student : students) {
                StudentTotalAttendanceDTO studentTotalAttendance = new StudentTotalAttendanceDTO();
                studentTotalAttendance.setFirstName(student.getUsers().getFirstName());
                studentTotalAttendance.setLastName(student.getUsers().getLastName());
                List<EntityPresence> presences = servicePresence.find(student);
                studentTotalAttendance.setAttendedHours(presences.stream().filter(EntityPresence::getWasPresent).count());
                studentTotalAttendanceDTOS.add(studentTotalAttendance);
                if (totalHours < presences.size()) {
                    totalHours = presences.size();
                }
            }
            classReportAttendanceDTO.setTotalHours(totalHours);
            classReportAttendanceDTO.setStudents(studentTotalAttendanceDTOS);
            classAttendances.add(classReportAttendanceDTO);
        }
        return ResponseEntity.ok(classAttendances);
    }

    @GetMapping("/attendanceClasses/{dateYear}/{dateMonth}")
    public ResponseEntity<List<ClassReportAttendanceDTO>> reportClasses(
            @PathVariable @Valid Integer dateYear,
            @PathVariable @Valid Integer dateMonth) {
        List<EntityClass> classes = serviceClass.getAll();
        Date dateFrom = Date.valueOf(dateYear + "-" + String.valueOf(dateMonth) + "-01");
        Date dateTo = Date.valueOf(dateYear + "-" + String.valueOf(dateMonth + 1) + "-01");
        List<ClassReportAttendanceDTO> classAttendances = new ArrayList<>();
        for (EntityClass entityClass : classes) {
            ClassReportAttendanceDTO classReportAttendanceDTO = new ClassReportAttendanceDTO();
            classReportAttendanceDTO.setClassName(entityClass.getName());
            classReportAttendanceDTO.setEducationStage(entityClass.getEnumEducationStage());
            List<EntityLesson> classLessons = serviceLesson.findAllByClass(entityClass);
            classReportAttendanceDTO.setHoursPerWeek(classLessons.size());
            List<EntityStudent> students = serviceStudent.findAllByStudentClass(entityClass);
            List<StudentTotalAttendanceDTO> studentTotalAttendanceDTOS = new ArrayList<>();
            int totalHours = 0;
            for (EntityStudent student : students) {
                StudentTotalAttendanceDTO studentTotalAttendance = new StudentTotalAttendanceDTO();
                studentTotalAttendance.setFirstName(student.getUsers().getFirstName());
                studentTotalAttendance.setLastName(student.getUsers().getLastName());
                List<EntityPresence> presences = servicePresence.find(student, dateFrom, dateTo);
                studentTotalAttendance.setAttendedHours(presences.stream().filter(EntityPresence::getWasPresent).count());
                studentTotalAttendanceDTOS.add(studentTotalAttendance);
                totalHours = presences.size();
            }
            classReportAttendanceDTO.setTotalHours(totalHours);
            classReportAttendanceDTO.setStudents(studentTotalAttendanceDTOS);
            classAttendances.add(classReportAttendanceDTO);
        }
        System.out.println(dateFrom);
        System.out.println(dateTo);
        return ResponseEntity.ok(classAttendances);
    }


    @GetMapping("/attendanceTeachers")
    public ResponseEntity<List<TeacherAttendanceDTO>> reportAttendanceTeacher() {
        List<EntityTeacher> teachers = serviceTeacher.getAll();
        List<TeacherAttendanceDTO> teacherAttendanceDTOS = new ArrayList<>();
        teachers.forEach(entityTeacher -> {
            TeacherAttendanceDTO teacherAttendanceDTO = new TeacherAttendanceDTO();
            teacherAttendanceDTO.setFirstName(entityTeacher.getUsers().getFirstName());
            teacherAttendanceDTO.setLastName(entityTeacher.getUsers().getLastName());
            List<EntityTeacherCourse> teacherCourses = serviceTeacherCourse.findByTeacher(entityTeacher);
            List<EntityLesson> teacherLessons = serviceLesson.findAllByTeacherCoursesInWithInactive(teacherCourses);
            teacherAttendanceDTO.setHoursPerWeek(teacherLessons.size());
            long attendedHours = (long) servicePresence.getPresenceFromLessons(teacherLessons)
                    .stream()
                    .filter(distinctByKeys(EntityPresence::getLesson, EntityPresence::getDate))
                    .count();
            teacherAttendanceDTO.setTotalHours(attendedHours);
            teacherAttendanceDTO.setAttendedHours(attendedHours);
            teacherAttendanceDTOS.add(teacherAttendanceDTO);
        });
        return ResponseEntity.ok(teacherAttendanceDTOS);
    }

    @GetMapping("/attendanceTeachers/{dateYear}/{dateMonth}")
    public ResponseEntity<List<TeacherAttendanceDTO>> reportAttendanceTeacher(
            @PathVariable @Valid Integer dateYear,
            @PathVariable @Valid Integer dateMonth) {
        List<EntityTeacher> teachers = serviceTeacher.getAll();
        if (dateMonth > 12 || dateMonth < 0) {
            return ResponseEntity.badRequest().build();
        }
        if (dateMonth == 12) {
            dateMonth = 1;
            dateYear++;
        }
        Date dateFrom = Date.valueOf(LocalDate.of(dateYear, dateMonth, 1));
        Date dateTo = Date.valueOf(LocalDate.of(dateYear, dateMonth + 1, 1));
        List<TeacherAttendanceDTO> teacherAttendanceDTOS = new ArrayList<>();
        teachers.forEach(entityTeacher -> {
            TeacherAttendanceDTO teacherAttendanceDTO = new TeacherAttendanceDTO();
            teacherAttendanceDTO.setFirstName(entityTeacher.getUsers().getFirstName());
            teacherAttendanceDTO.setLastName(entityTeacher.getUsers().getLastName());
            List<EntityTeacherCourse> teacherCourses = serviceTeacherCourse.findByTeacher(entityTeacher);
            List<EntityLesson> teacherLessons = serviceLesson.findAllByTeacherCoursesInWithInactive(teacherCourses);
            teacherAttendanceDTO.setHoursPerWeek(teacherLessons.size());
            long attendedHours = (long) servicePresence.getPresenceFromLessons(teacherLessons, dateFrom, dateTo)
                    .stream()
                    .filter(distinctByKeys(EntityPresence::getLesson, EntityPresence::getDate))
                    .count();
            teacherAttendanceDTO.setTotalHours(attendedHours);
            teacherAttendanceDTO.setAttendedHours(attendedHours);
            teacherAttendanceDTOS.add(teacherAttendanceDTO);
        });
        return ResponseEntity.ok(teacherAttendanceDTOS);
    }

    private <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();
        return t -> {
            final List<?> keys = Arrays.stream(keyExtractors)
                    .map(ke -> ke.apply(t))
                    .collect(Collectors.toList());
            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }

    private StudentAverageGradeDTO getStudentAverageGradeDTO(EntityStudent student) {
        StudentAverageGradeDTO studentAverageGrade = new StudentAverageGradeDTO();
        studentAverageGrade.setFirstName(student.getUsers().getFirstName());
        studentAverageGrade.setLastName(student.getUsers().getLastName());
        List<CourseGradeDTO> courseGrades = new ArrayList<>();
        double averageGradeTotal = 0d;
        int totalTeacherCoursesWithMarks = 0;
        for (EntityTeacherCourse teacherCourse : serviceLesson.findDistinctTeacherCoursesOfGivenClass(student.getStudentClass())) {
            totalTeacherCoursesWithMarks++;
            CourseGradeDTO courseGradeDTO = new CourseGradeDTO();
            courseGradeDTO.setCourseName(teacherCourse.getCourse().getName());
            double averageGrade = 0d;
            int amountOfNoneZeroGrades = 0;
            for (EntityMark entityMark : serviceMark.findAllByStudentAndTeacherCourse(student, teacherCourse)) {
                if (entityMark.getEnumGrade() != EnumGrade.NONE) {
                    averageGrade += entityMark.getEnumGrade().getValue();
                    amountOfNoneZeroGrades++;
                }
            }
            if (amountOfNoneZeroGrades != 0) {
                averageGrade = averageGrade / (double) amountOfNoneZeroGrades;
                averageGradeTotal += averageGrade;
            } else {
                totalTeacherCoursesWithMarks--;
                averageGrade = -1d;
            }
            averageGrade = BigDecimal.valueOf(averageGrade)
                    .setScale(1, RoundingMode.HALF_UP)
                    .doubleValue();
            if (averageGrade < 0) {
                courseGradeDTO.setAverageGrade("Brak ocen");
            } else {
                courseGradeDTO.setAverageGrade(String.valueOf(averageGrade));
            }
            courseGrades.add(courseGradeDTO);
        }
        if (totalTeacherCoursesWithMarks != 0) {
            averageGradeTotal = averageGradeTotal / (double) totalTeacherCoursesWithMarks;
        } else {
            averageGradeTotal = -1d;
        }
        studentAverageGrade.setCourseGrades(courseGrades);
        averageGradeTotal = BigDecimal.valueOf(averageGradeTotal)
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();
        if (averageGradeTotal < 0) {
            studentAverageGrade.setAverageGradeTotal("Brak ocen");
        } else {
            studentAverageGrade.setAverageGradeTotal(String.valueOf(averageGradeTotal));
        }
        return studentAverageGrade;
    }

    @GetMapping("/studentsAverageGrades/{classId}")
    public ResponseEntity<List<StudentAverageGradeDTO>> studentsAverageGrades(@Valid @PathVariable Long classId) {
        return serviceClass.get(classId).map(entityClass -> {
            List<EntityStudent> students = serviceStudent.findAllByStudentClass(entityClass);
            List<StudentAverageGradeDTO> studentAverageGrades = new ArrayList<>();
            students.forEach(student -> {
                studentAverageGrades.add(getStudentAverageGradeDTO(student));
            });
            return ResponseEntity.ok(studentAverageGrades);
        }).orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/classStudentsAverageGrades")
    public ResponseEntity<List<ClassStudentsAverageGradeDTO>> classStudentsAverageGrades() {
        List<EntityClass> classes = serviceClass.getAll();
        List<ClassStudentsAverageGradeDTO> classStudentsAverageGrades = new ArrayList<>();
        for (EntityClass entityClass : classes) {
            ClassStudentsAverageGradeDTO classStudentsAverageGrade = new ClassStudentsAverageGradeDTO();
            List<EntityStudent> students = serviceStudent.findAllByStudentClass(entityClass);
            List<StudentAverageGradeDTO> studentAverageGrades = new ArrayList<>();
            students.forEach(student -> {
                studentAverageGrades.add(getStudentAverageGradeDTO(student));
            });
            classStudentsAverageGrade.setStudentAverageGrades(studentAverageGrades);
            classStudentsAverageGrade.setClassName(entityClass.getName());
            classStudentsAverageGrade.setEducationStage(entityClass.getEnumEducationStage());
            classStudentsAverageGrades.add(classStudentsAverageGrade);
        }
        return ResponseEntity.ok(classStudentsAverageGrades);
    }

}
