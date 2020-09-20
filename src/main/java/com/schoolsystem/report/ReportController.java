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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "http://localhost:4200")
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
                totalHours = presences.size();
            }
            classReportAttendanceDTO.setTotalHours(totalHours);
            classReportAttendanceDTO.setStudents(studentTotalAttendanceDTOS);
            classAttendances.add(classReportAttendanceDTO);
        }
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
            List<EntityLesson> teacherLessons = serviceLesson.findAllByTeacherCoursesIn(teacherCourses);
            teacherAttendanceDTO.setHoursPerWeek(teacherLessons.size());
            long attendedHours = servicePresence.getTotalAmountOfPresence(teacherLessons);
            teacherAttendanceDTO.setTotalHours(attendedHours);
            teacherAttendanceDTO.setAttendedHours(attendedHours);
            teacherAttendanceDTOS.add(teacherAttendanceDTO);
        });
        return ResponseEntity.ok(teacherAttendanceDTOS);
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
            courseGradeDTO.setAverageGrade(averageGrade);
            courseGrades.add(courseGradeDTO);
        }
        if (totalTeacherCoursesWithMarks != 0) {
            averageGradeTotal = averageGradeTotal / (double) totalTeacherCoursesWithMarks;
        } else {
            averageGradeTotal = -1d;
        }
        studentAverageGrade.setCourseGrades(courseGrades);
        studentAverageGrade.setAverageGradeTotal(averageGradeTotal);
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
