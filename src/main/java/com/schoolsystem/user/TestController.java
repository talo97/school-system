package com.schoolsystem.user;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.classes.EnumEducationStage;
import com.schoolsystem.classes.ServiceClass;
import com.schoolsystem.competition.EntityCompetition;
import com.schoolsystem.competition.EntityCompetitionParticipation;
import com.schoolsystem.competition.ServiceCompetition;
import com.schoolsystem.competition.ServiceCompetitionParticipation;
import com.schoolsystem.course.EntityCourse;
import com.schoolsystem.course.EntityTeacherCourse;
import com.schoolsystem.course.ServiceCourse;
import com.schoolsystem.course.ServiceTeacherCourse;
import com.schoolsystem.lesson.EntityLesson;
import com.schoolsystem.lesson.EnumDayOfWeek;
import com.schoolsystem.lesson.EnumLessonNumber;
import com.schoolsystem.lesson.ServiceLesson;
import com.schoolsystem.mark.EntityMark;
import com.schoolsystem.mark.EnumGrade;
import com.schoolsystem.mark.ServiceMark;
import com.schoolsystem.parent.EntityParent;
import com.schoolsystem.parent.ServiceParent;
import com.schoolsystem.presence.EntityPresence;
import com.schoolsystem.presence.ServicePresence;
import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.student.ServiceStudent;
import com.schoolsystem.teacher.EntityTeacher;
import com.schoolsystem.teacher.ServiceTeacher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class TestController {

    private ServiceUser serviceUser;
    private ServiceTeacher serviceTeacher;
    private ServiceClass serviceClass;
    private ServiceCourse serviceCourse;
    private ServiceTeacherCourse serviceTeacherCourse;
    private ServiceStudent serviceStudent;
    private ServiceParent serviceParent;
    private ServiceLesson serviceLesson;
    private ServicePresence servicePresence;
    private ServiceMark serviceMark;
    private ServiceCompetition serviceCompetition;
    private ServiceCompetitionParticipation serviceCompetitionParticipation;

    public TestController(ServiceUser serviceUser, ServiceTeacher serviceTeacher, ServiceClass serviceClass, ServiceCourse serviceCourse, ServiceTeacherCourse serviceTeacherCourse, ServiceStudent serviceStudent, ServiceParent serviceParent, ServiceLesson serviceLesson, ServicePresence servicePresence, ServiceMark serviceMark, ServiceCompetition serviceCompetition, ServiceCompetitionParticipation serviceCompetitionParticipation) {
        this.serviceUser = serviceUser;
        this.serviceTeacher = serviceTeacher;
        this.serviceClass = serviceClass;
        this.serviceCourse = serviceCourse;
        this.serviceTeacherCourse = serviceTeacherCourse;
        this.serviceStudent = serviceStudent;
        this.serviceParent = serviceParent;
        this.serviceLesson = serviceLesson;
        this.servicePresence = servicePresence;
        this.serviceMark = serviceMark;
        this.serviceCompetition = serviceCompetition;
        this.serviceCompetitionParticipation = serviceCompetitionParticipation;
    }

    @PostMapping("/hax")
    public ResponseEntity<?> addTestDatabase() {
        saveAdmin();
        saveTeachers();
        saveStudentsAndClasses();
        saveCourses();
        saveLessons();
        saveAttendances();
        saveGrades();
        saveCompetition();
        return ResponseEntity.ok().build();
    }

    private void saveCompetition() {
        EntityCompetition entityCompetition = new EntityCompetition();
        entityCompetition.setDescription("Konkurs matematyczny");
        entityCompetition.setName("Kangur");
        serviceCompetition.save(entityCompetition);
        EntityCompetitionParticipation entityCompetitionParticipation = new EntityCompetitionParticipation();
        entityCompetitionParticipation.setDescription("Wynik: 85%");
        entityCompetitionParticipation.setTeacher(serviceTeacher.get(1L).get());
        entityCompetitionParticipation.setCompetition(entityCompetition);
        entityCompetitionParticipation.setStudent(serviceStudent.get(1L).get());
        serviceCompetitionParticipation.save(entityCompetitionParticipation);
    }

    private void saveGrade(EntityStudent student, EntityTeacherCourse teacherCourse) {
        EntityMark entityMark = new EntityMark();
        entityMark.setDescription("Sprawdzian");
        entityMark.setLastChange(Date.valueOf(LocalDate.now()));
        Random random = new Random();
        double randomNumber = random.nextDouble();
        if (randomNumber > 0.9) {
            entityMark.setEnumGrade(EnumGrade.SIX);
        } else if (randomNumber > 0.8) {
            entityMark.setEnumGrade(EnumGrade.FIVE);
        } else if (randomNumber > 0.6) {
            entityMark.setEnumGrade(EnumGrade.FOUR);
        } else if (randomNumber > 0.1) {
            entityMark.setEnumGrade(EnumGrade.THREE);
        } else {
            entityMark.setEnumGrade(EnumGrade.TWO);
        }
        entityMark.setStudent(student);
        entityMark.setTeacherCourse(teacherCourse);
        serviceMark.save(entityMark);
    }

    private void saveGrades() {
        EntityClass classA = serviceClass.get(1L).get();
        EntityClass classB = serviceClass.get(2L).get();
        List<EntityStudent> studentsA = serviceStudent.findAllByStudentClass(classA);
        List<EntityStudent> studentsB = serviceStudent.findAllByStudentClass(classB);
        List<EntityTeacherCourse> teacherCoursesA = serviceLesson.findDistinctTeacherCoursesOfGivenClass(classA);
        List<EntityTeacherCourse> teacherCoursesB = serviceLesson.findDistinctTeacherCoursesOfGivenClass(classB);
        saveGrades(studentsA, teacherCoursesA);
        saveGrades(studentsA, teacherCoursesA);
        saveGrades(studentsA, teacherCoursesA);
        saveGrades(studentsB, teacherCoursesB);
        saveGrades(studentsB, teacherCoursesB);
        saveGrades(studentsB, teacherCoursesB);
        saveGrades(studentsB, teacherCoursesB);
    }

    private void saveGrades(List<EntityStudent> students, List<EntityTeacherCourse> teacherCourses) {
        students.forEach(student -> {
            teacherCourses.forEach(teacherCourse -> {
                saveGrade(student, teacherCourse);
            });
        });
    }

    private void saveAttendanceSingle(Date date, boolean wasPresent, EntityLesson lesson, EntityStudent student) {
        EntityPresence presence = new EntityPresence();
        presence.setWasPresent(wasPresent);
        presence.setStudent(student);
        presence.setLesson(lesson);
        presence.setDescription("");
        presence.setDate(date);
        servicePresence.save(presence);
    }


    private Map<EnumDayOfWeek, Date> initWeekDateMap(String startDate) {
        Map<EnumDayOfWeek, Date> weekDateMap = new HashMap<>();
        LocalDate localDate = LocalDate.parse(startDate);
        weekDateMap.put(EnumDayOfWeek.MONDAY, Date.valueOf(localDate));
        weekDateMap.put(EnumDayOfWeek.TUESDAY, Date.valueOf(localDate.plusDays(1)));
        weekDateMap.put(EnumDayOfWeek.WEDNESDAY, Date.valueOf(localDate.plusDays(2)));
        weekDateMap.put(EnumDayOfWeek.THURSDAY, Date.valueOf(localDate.plusDays(3)));
        weekDateMap.put(EnumDayOfWeek.FRIDAY, Date.valueOf(localDate.plusDays(4)));
        return weekDateMap;
    }

    private void saveAttendanceClass(EntityClass entityClass, String startDate) {
        Map<EnumDayOfWeek, Date> weekDateMap = initWeekDateMap(startDate);
        List<EntityStudent> students = serviceStudent.findAllByStudentClass(entityClass);
        List<EntityLesson> lessons = serviceLesson.findAllByClass(entityClass);
        for (EntityLesson lesson : lessons) {
            for (EntityStudent student : students) {
                Random random = new Random();
                double randomNumber = random.nextDouble();
                boolean wasPresent = true;
                if (randomNumber > 0.95) {
                    wasPresent = false;
                }
                saveAttendanceSingle(weekDateMap.get(lesson.getDayOfWeek()), wasPresent, lesson, student);
            }
        }
    }

    private void saveAttendanceClassOnlyTwoDays(EntityClass entityClass, String startDate) {
        Map<EnumDayOfWeek, Date> weekDateMap = initWeekDateMap(startDate);
        List<EntityStudent> students = serviceStudent.findAllByStudentClass(entityClass);
        List<EntityLesson> lessons = serviceLesson.findAllByClass(entityClass);
        for (EntityLesson lesson : lessons) {
            if (lesson.getDayOfWeek() == EnumDayOfWeek.MONDAY || lesson.getDayOfWeek() == EnumDayOfWeek.TUESDAY) {
                for (EntityStudent student : students) {
                    Random random = new Random();
                    double randomNumber = random.nextDouble();
                    boolean wasPresent = true;
                    if (randomNumber > 0.95) {
                        wasPresent = false;
                    }
                    saveAttendanceSingle(weekDateMap.get(lesson.getDayOfWeek()), wasPresent, lesson, student);
                }
            }
        }
    }

    private void saveAttendances() {
        EntityClass classA = serviceClass.get(1L).get();
        EntityClass classB = serviceClass.get(2L).get();
        String stringDate = "2020-08-03";
        saveAttendanceClass(classA, stringDate);
        saveAttendanceClass(classB, stringDate);
        stringDate = "2020-09-07";
        saveAttendanceClass(classA, stringDate);
        saveAttendanceClass(classB, stringDate);
        stringDate = "2020-09-14";
        saveAttendanceClass(classA, stringDate);
        saveAttendanceClass(classB, stringDate);
        //fast cheat to save first two days of next week
        stringDate = "2020-09-21";
        saveAttendanceClassOnlyTwoDays(classA, stringDate);
        saveAttendanceClassOnlyTwoDays(classB, stringDate);
    }

    private void saveLesson(EntityClass entityClass, EnumDayOfWeek enumDayOfWeek, EnumLessonNumber lessonNumber, EntityTeacherCourse teacherCourse) {
        EntityLesson lesson = new EntityLesson();
        lesson.setActive(true);
        lesson.setEntityClass(entityClass);
        lesson.setDayOfWeek(enumDayOfWeek);
        lesson.setLessonNumber(lessonNumber);
        lesson.setTeacherCourse(teacherCourse);
        serviceLesson.save(lesson);
    }

    private void saveLessons() {
        EntityClass classA = serviceClass.get(1L).get();
        EntityClass classB = serviceClass.get(2L).get();
        List<EntityTeacherCourse> teacherCourses = serviceTeacherCourse.getAll();
        //MONDAY
        saveLesson(classA, EnumDayOfWeek.MONDAY, EnumLessonNumber.FIRST, teacherCourses.get(0));
        saveLesson(classA, EnumDayOfWeek.MONDAY, EnumLessonNumber.SECOND, teacherCourses.get(0));
        saveLesson(classA, EnumDayOfWeek.MONDAY, EnumLessonNumber.THIRD, teacherCourses.get(1));
        saveLesson(classA, EnumDayOfWeek.MONDAY, EnumLessonNumber.FOURTH, teacherCourses.get(1));
        saveLesson(classA, EnumDayOfWeek.MONDAY, EnumLessonNumber.FIFTH, teacherCourses.get(3));
        saveLesson(classA, EnumDayOfWeek.MONDAY, EnumLessonNumber.SIXTH, teacherCourses.get(3));

        saveLesson(classB, EnumDayOfWeek.MONDAY, EnumLessonNumber.FIRST, teacherCourses.get(4));
        saveLesson(classB, EnumDayOfWeek.MONDAY, EnumLessonNumber.SECOND, teacherCourses.get(4));
        saveLesson(classB, EnumDayOfWeek.MONDAY, EnumLessonNumber.THIRD, teacherCourses.get(2));
        saveLesson(classB, EnumDayOfWeek.MONDAY, EnumLessonNumber.FOURTH, teacherCourses.get(2));
        saveLesson(classB, EnumDayOfWeek.MONDAY, EnumLessonNumber.FIFTH, teacherCourses.get(5));
        saveLesson(classB, EnumDayOfWeek.MONDAY, EnumLessonNumber.SIXTH, teacherCourses.get(5));
        //TUESDAY
        saveLesson(classA, EnumDayOfWeek.TUESDAY, EnumLessonNumber.FIRST, teacherCourses.get(3));
        saveLesson(classA, EnumDayOfWeek.TUESDAY, EnumLessonNumber.SECOND, teacherCourses.get(3));
        saveLesson(classA, EnumDayOfWeek.TUESDAY, EnumLessonNumber.THIRD, teacherCourses.get(3));
        saveLesson(classA, EnumDayOfWeek.TUESDAY, EnumLessonNumber.FOURTH, teacherCourses.get(0));

        saveLesson(classB, EnumDayOfWeek.TUESDAY, EnumLessonNumber.FIRST, teacherCourses.get(5));
        saveLesson(classB, EnumDayOfWeek.TUESDAY, EnumLessonNumber.SECOND, teacherCourses.get(4));
        saveLesson(classB, EnumDayOfWeek.TUESDAY, EnumLessonNumber.THIRD, teacherCourses.get(4));
        saveLesson(classB, EnumDayOfWeek.TUESDAY, EnumLessonNumber.FOURTH, teacherCourses.get(4));

        //WEDNESDAY
        saveLesson(classA, EnumDayOfWeek.WEDNESDAY, EnumLessonNumber.FIRST, teacherCourses.get(0));
        saveLesson(classA, EnumDayOfWeek.WEDNESDAY, EnumLessonNumber.SECOND, teacherCourses.get(0));
        saveLesson(classA, EnumDayOfWeek.WEDNESDAY, EnumLessonNumber.THIRD, teacherCourses.get(1));
        saveLesson(classA, EnumDayOfWeek.WEDNESDAY, EnumLessonNumber.FOURTH, teacherCourses.get(1));
        saveLesson(classA, EnumDayOfWeek.WEDNESDAY, EnumLessonNumber.FIFTH, teacherCourses.get(3));
        saveLesson(classA, EnumDayOfWeek.WEDNESDAY, EnumLessonNumber.SIXTH, teacherCourses.get(3));

        saveLesson(classB, EnumDayOfWeek.WEDNESDAY, EnumLessonNumber.FIRST, teacherCourses.get(4));
        saveLesson(classB, EnumDayOfWeek.WEDNESDAY, EnumLessonNumber.SECOND, teacherCourses.get(4));
        saveLesson(classB, EnumDayOfWeek.WEDNESDAY, EnumLessonNumber.THIRD, teacherCourses.get(2));
        saveLesson(classB, EnumDayOfWeek.WEDNESDAY, EnumLessonNumber.FOURTH, teacherCourses.get(2));
        saveLesson(classB, EnumDayOfWeek.WEDNESDAY, EnumLessonNumber.FIFTH, teacherCourses.get(5));
        saveLesson(classB, EnumDayOfWeek.WEDNESDAY, EnumLessonNumber.SIXTH, teacherCourses.get(5));

        //THURSDAY
        saveLesson(classA, EnumDayOfWeek.THURSDAY, EnumLessonNumber.FIRST, teacherCourses.get(0));
        saveLesson(classA, EnumDayOfWeek.THURSDAY, EnumLessonNumber.SECOND, teacherCourses.get(3));
        saveLesson(classA, EnumDayOfWeek.THURSDAY, EnumLessonNumber.THIRD, teacherCourses.get(3));
        saveLesson(classA, EnumDayOfWeek.THURSDAY, EnumLessonNumber.FOURTH, teacherCourses.get(3));

        saveLesson(classB, EnumDayOfWeek.THURSDAY, EnumLessonNumber.FIRST, teacherCourses.get(4));
        saveLesson(classB, EnumDayOfWeek.THURSDAY, EnumLessonNumber.SECOND, teacherCourses.get(5));
        saveLesson(classB, EnumDayOfWeek.THURSDAY, EnumLessonNumber.THIRD, teacherCourses.get(5));
        saveLesson(classB, EnumDayOfWeek.THURSDAY, EnumLessonNumber.FOURTH, teacherCourses.get(5));

        //FRIDAY
        saveLesson(classA, EnumDayOfWeek.FRIDAY, EnumLessonNumber.FIRST, teacherCourses.get(1));
        saveLesson(classA, EnumDayOfWeek.FRIDAY, EnumLessonNumber.SECOND, teacherCourses.get(1));
        saveLesson(classA, EnumDayOfWeek.FRIDAY, EnumLessonNumber.THIRD, teacherCourses.get(1));
        saveLesson(classA, EnumDayOfWeek.FRIDAY, EnumLessonNumber.FOURTH, teacherCourses.get(0));
        saveLesson(classA, EnumDayOfWeek.FRIDAY, EnumLessonNumber.FIFTH, teacherCourses.get(0));
        saveLesson(classA, EnumDayOfWeek.FRIDAY, EnumLessonNumber.SIXTH, teacherCourses.get(3));

        saveLesson(classB, EnumDayOfWeek.FRIDAY, EnumLessonNumber.FIRST, teacherCourses.get(2));
        saveLesson(classB, EnumDayOfWeek.FRIDAY, EnumLessonNumber.SECOND, teacherCourses.get(2));
        saveLesson(classB, EnumDayOfWeek.FRIDAY, EnumLessonNumber.THIRD, teacherCourses.get(2));
        saveLesson(classB, EnumDayOfWeek.FRIDAY, EnumLessonNumber.FOURTH, teacherCourses.get(4));
        saveLesson(classB, EnumDayOfWeek.FRIDAY, EnumLessonNumber.FIFTH, teacherCourses.get(4));
        saveLesson(classB, EnumDayOfWeek.FRIDAY, EnumLessonNumber.SIXTH, teacherCourses.get(5));
    }

    private void saveTeachers() {
        for (int i = 1; i < 5; i++) {
            EntityUser userTeacher = new EntityUser();
            userTeacher.setLogin("teacher" + i);
            userTeacher.setPassword("teacher" + i);
            userTeacher.setLastName("teacher" + i);
            userTeacher.setFirstName("teacher" + i);
            userTeacher.setEmail("teacher" + i + "@mail.com");
            Random random = new Random();
            double randomNumber = random.nextDouble();
            long randomPhone = (int) (randomNumber * 1000000000);
            userTeacher.setPhoneNumber(String.valueOf(randomPhone));
            userTeacher.setUserType(EnumUserType.TEACHER);
            userTeacher.setBirthDate(new Date(315532800000L + i * 25532800000L));
            EntityTeacher teacher = new EntityTeacher();
            teacher.setUsers(userTeacher);
            serviceUser.save(userTeacher);
            serviceTeacher.save(teacher);
        }
    }

    private void saveStudentsAndClasses() {
        EntityClass entityClassA = new EntityClass();
        entityClassA.setSupervisor(serviceTeacher.get(1L).get());
        entityClassA.setEnumEducationStage(EnumEducationStage.FIRST_YEAR);
        entityClassA.setName("Pierwsza A");
        serviceClass.save(entityClassA);
        EntityClass entityClassB = new EntityClass();
        entityClassB.setSupervisor(serviceTeacher.get(2L).get());
        entityClassB.setEnumEducationStage(EnumEducationStage.FIRST_YEAR);
        entityClassB.setName("Pierwsza B");
        serviceClass.save(entityClassB);
        saveStudentsAndParents(entityClassA, 1);
        saveStudentsAndParents(entityClassB, 11);
    }

    private void saveStudentsAndParents(EntityClass entityClass, int number) {
        for (int i = number; i < number + 10; i++) {
            EntityParent parent = new EntityParent();
            EntityUser userParent = new EntityUser();
            userParent.setLastName("parent" + i);
            userParent.setFirstName("parent" + i);
            userParent.setLogin("parent" + i);
            userParent.setPassword("parent" + i);
            Random random = new Random();
            double randomNumber = random.nextDouble();
            long randomPhone = (int) (randomNumber * 1000000000);
            userParent.setPhoneNumber(String.valueOf(randomPhone));
            userParent.setEmail("parent" + i + "@mail.com");
            userParent.setUserType(EnumUserType.PARENT);
            userParent.setBirthDate(new Date(415532800000L + i * 5532800000L));
            parent.setUsers(userParent);
            serviceUser.save(userParent);
            serviceParent.save(parent);
            EntityStudent student = new EntityStudent();
            EntityUser userStudent = new EntityUser();
            userStudent.setBirthDate(new Date(946684800000L + (i * 930009600L)));
            userStudent.setUserType(EnumUserType.STUDENT);
            userStudent.setLogin("student" + i);
            userStudent.setPassword("student" + i);
            userStudent.setFirstName("student" + i);
            userStudent.setLastName("student" + i);
            userStudent.setEmail("student" + i + "@mail.com");
            randomNumber = random.nextDouble();
            randomPhone = (int) (randomNumber * 1000000000);
            userStudent.setPhoneNumber(String.valueOf(randomPhone));
            student.setStudentClass(entityClass);
            student.setParent(parent);
            student.setUsers(userStudent);
            serviceUser.save(userStudent);
            serviceStudent.save(student);
        }
    }

    private void saveCourses() {
        //courses
        EntityCourse courseMath = new EntityCourse();
        courseMath.setName("Matematyka");
        EntityCourse coursePhysic = new EntityCourse();
        coursePhysic.setName("Fizyka");
        EntityCourse courseEnglish = new EntityCourse();
        courseEnglish.setName("Jezyk Angielski");
        serviceCourse.save(courseMath);
        serviceCourse.save(coursePhysic);
        serviceCourse.save(courseEnglish);
        //teacherCourses
        EntityTeacherCourse teacherCourse = new EntityTeacherCourse();
        List<EntityTeacher> teachers = serviceTeacher.getAll();
        teacherCourse.setCourse(courseMath);
        teacherCourse.setTeacher(teachers.get(0));
        serviceTeacherCourse.save(teacherCourse);
        teacherCourse = new EntityTeacherCourse();
        teacherCourse.setCourse(coursePhysic);
        teacherCourse.setTeacher(teachers.get(0));
        serviceTeacherCourse.save(teacherCourse);
        teacherCourse = new EntityTeacherCourse();
        teacherCourse.setTeacher(teachers.get(1));
        teacherCourse.setCourse(coursePhysic);
        serviceTeacherCourse.save(teacherCourse);
        teacherCourse = new EntityTeacherCourse();
        teacherCourse.setCourse(courseEnglish);
        teacherCourse.setTeacher(teachers.get(2));
        serviceTeacherCourse.save(teacherCourse);
        teacherCourse = new EntityTeacherCourse();
        teacherCourse.setTeacher(teachers.get(3));
        teacherCourse.setCourse(courseMath);
        serviceTeacherCourse.save(teacherCourse);
        teacherCourse = new EntityTeacherCourse();
        teacherCourse.setTeacher(teachers.get(3));
        teacherCourse.setCourse(courseEnglish);
        serviceTeacherCourse.save(teacherCourse);
    }

    private void saveAdmin() {
        EntityUser admin = new EntityUser();
        admin.setLogin("admin");
        admin.setPassword("admin");
        admin.setUserType(EnumUserType.ADMIN);
        Date date = new Date(861926400000L);
        admin.setBirthDate(date);
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setPhoneNumber("717 131 333");
        admin.setEmail("admin@gmail.com");
        serviceUser.save(admin);
    }

}
