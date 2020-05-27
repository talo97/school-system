package com.schoolsystem.lesson;

import com.schoolsystem.classes.ClassGetDTO;
import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.classes.ServiceClass;
import com.schoolsystem.course.CourseGetDTO;
import com.schoolsystem.course.EntityTeacherCourse;
import com.schoolsystem.course.ServiceTeacherCourse;
import com.schoolsystem.course.TeacherCourseGetDTO;
import com.schoolsystem.teacher.ServiceTeacher;
import com.schoolsystem.user.UserGetDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class LessonController {

    private final ServiceLesson serviceLesson;
    private final ServiceTeacherCourse serviceTeacherCourse;
    private final ServiceClass serviceClass;
    private final ServiceTeacher serviceTeacher;
    private final ModelMapper modelMapper;

    public LessonController(ServiceLesson serviceLesson, ServiceTeacherCourse serviceTeacherCourse, ServiceClass serviceClass, ServiceTeacher serviceTeacher, ModelMapper modelMapper) {
        this.serviceLesson = serviceLesson;
        this.serviceTeacherCourse = serviceTeacherCourse;
        this.serviceClass = serviceClass;
        this.serviceTeacher = serviceTeacher;
        this.modelMapper = modelMapper;
    }

    private List<LessonGetDTO> mapEntityToGetDTO(List<EntityLesson> lstToMap) {
        List<LessonGetDTO> dtoList = new ArrayList<>();
        lstToMap.forEach(e -> {
            LessonGetDTO temp = modelMapper.map(e, LessonGetDTO.class);

            ClassGetDTO classTemp = modelMapper.map(e.getEntityClass(), ClassGetDTO.class);
            classTemp.setSupervisor(modelMapper.map(e.getEntityClass().getSupervisor().getUsers(), UserGetDTO.class));
            temp.setEntityClass(classTemp);

            TeacherCourseGetDTO teacherCourseTemp = new TeacherCourseGetDTO();
            teacherCourseTemp.setId(e.getTeacherCourse().getId());
            teacherCourseTemp.setTeacher(modelMapper.map(e.getTeacherCourse().getTeacher().getUsers(), UserGetDTO.class));
            teacherCourseTemp.setCourse(modelMapper.map(e.getTeacherCourse().getCourse(), CourseGetDTO.class));
            temp.setTeacherCourse(teacherCourseTemp);
            dtoList.add(temp);
        });
        return dtoList;
    }

    @GetMapping("/lessons")
    public ResponseEntity<List<LessonGetDTO>> getLessons() {
        return ResponseEntity.ok(mapEntityToGetDTO(serviceLesson.findAllActive()));
    }

    @GetMapping("/lessonsClass/{id}")
    public ResponseEntity<List<LessonGetDTO>> getLessons(@Valid @PathVariable Long id) {
        return serviceClass.get(id).map(entityClass -> {
            return ResponseEntity.ok(mapEntityToGetDTO(serviceLesson.findAllByClass(entityClass)));
        }).orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/lessonsTeacher/{id}")
    public ResponseEntity<?> getLessonsTeacher(@Valid @PathVariable Long id) {
        return serviceTeacher.get(id).map(entityTeacher -> {
            return ResponseEntity.ok(mapEntityToGetDTO(serviceLesson.findAllByTeacher(entityTeacher)));
        }).orElse(ResponseEntity.badRequest().build());
//
//
//        List<LessonGetDTO> dtoList = new ArrayList<>();
//        serviceTeacher.get(id).ifPresent(e -> dtoList.addAll(mapEntityToGetDTO(serviceLesson.findAllByTeacher(e))));
//        return ResponseEntity.ok().body(dtoList);
    }


    @PostMapping("/lessons")
    public ResponseEntity<?> addLesson(@Valid @RequestBody LessonPostDTO lessonPostDTO) {
        Optional<EntityClass> entityClass = serviceClass.get(lessonPostDTO.getEntityClassId());
        Optional<EntityTeacherCourse> entityTeacherCourse = serviceTeacherCourse.get(lessonPostDTO.getTeacherCourseId());
        if (lessonPostDTO.containsEmptyValue() || !entityClass.isPresent() || !entityTeacherCourse.isPresent()) {
            return ResponseEntity.badRequest().body("Not all parameters were given correctly (either no ID was passed or the ID was wrong)");
        }
        if (serviceLesson.doesLessonAlreadyExist(lessonPostDTO.getDayOfWeek(), lessonPostDTO.getLessonNumber(), lessonPostDTO.getEntityClassId())) {
            return ResponseEntity.badRequest().body("There is already lesson at this time for given class.");
        }
        if (!serviceLesson.isTeacherAvailable(entityTeacherCourse.get().getTeacher(), lessonPostDTO.getDayOfWeek(), lessonPostDTO.getLessonNumber())) {
            return ResponseEntity.badRequest().body("Teacher is not available in given day and lesson number");
        }
        LessonGetDTO savedLesson = new LessonGetDTO();
        savedLesson.setDayOfWeek(lessonPostDTO.getDayOfWeek());
        savedLesson.setLessonNumber(lessonPostDTO.getLessonNumber());
        entityClass.ifPresent(entClass -> entityTeacherCourse.ifPresent(teacherCourse -> {
            EntityLesson lesson = modelMapper.map(lessonPostDTO, EntityLesson.class);
            lesson.setEntityClass(entClass);
            lesson.setTeacherCourse(teacherCourse);
            savedLesson.setId(serviceLesson.save(lesson).getId());
            //teacher course
            TeacherCourseGetDTO teacherCourseTemp = new TeacherCourseGetDTO();
            teacherCourseTemp.setTeacher(modelMapper.map(teacherCourse.getTeacher().getUsers(), UserGetDTO.class));
            teacherCourseTemp.setCourse(modelMapper.map(teacherCourse.getCourse(), CourseGetDTO.class));
            teacherCourseTemp.setId(teacherCourse.getId());
            teacherCourseTemp.getTeacher().setId(teacherCourse.getTeacher().getId());
            savedLesson.setTeacherCourse(teacherCourseTemp);
            //class
            ClassGetDTO classTemp = modelMapper.map(entClass, ClassGetDTO.class);
            classTemp.setSupervisor(modelMapper.map(entClass.getSupervisor().getUsers(), UserGetDTO.class));
            classTemp.getSupervisor().setId(entClass.getSupervisor().getId());
            savedLesson.setEntityClass(classTemp);

        }));
        return ResponseEntity.ok(savedLesson);
    }

    @DeleteMapping("/lessons/{lessonId}")
    public ResponseEntity<?> deleteLesson(@Valid @PathVariable Long lessonId) {
        Optional<EntityLesson> lesson = serviceLesson.get(lessonId);
        if (lesson.isPresent()) {
            lesson.get().setActive(false);
            serviceLesson.update(lesson.get());
            return ResponseEntity.ok("Lesson was successfully removed from active lessons");
        }
        return ResponseEntity.badRequest().body("Lesson with given ID doesn't exist");
    }

    @PutMapping("/lessons/{id}")
    public ResponseEntity<?> editLesson(@Valid @RequestBody Long teacherCourseId, @Valid @PathVariable Long id) {
        Optional<EntityTeacherCourse> teacherCourse = serviceTeacherCourse.get(teacherCourseId);
        if (teacherCourse.isPresent()) {
            return serviceLesson.get(id).map(lesson -> {
                if (!serviceLesson.isTeacherAvailable(teacherCourse.get().getTeacher(), lesson.getDayOfWeek(), lesson.getLessonNumber())) {
                    return ResponseEntity.badRequest().body("Teacher is not available in given day and lesson number");
                }
                if (lesson.isActive()) {
                    EntityLesson newLesson = new EntityLesson();
                    newLesson.setEntityClass(lesson.getEntityClass());
                    newLesson.setDayOfWeek(lesson.getDayOfWeek());
                    newLesson.setLessonNumber(lesson.getLessonNumber());
                    newLesson.setTeacherCourse(teacherCourse.get());
                    lesson.setActive(false);
                    serviceLesson.update(lesson);
                    serviceLesson.save(newLesson);
                    return ResponseEntity.ok().body("updated successfully");
                } else {
                    return ResponseEntity.badRequest().build();
                }
            }).orElse(ResponseEntity.badRequest().build());
        }else{
            return ResponseEntity.badRequest().build();
        }

    }

}
