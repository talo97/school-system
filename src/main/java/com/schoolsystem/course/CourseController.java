package com.schoolsystem.course;

import com.schoolsystem.teacher.EntityTeacher;
import com.schoolsystem.teacher.ServiceTeacher;
import com.schoolsystem.user.UserGetDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class CourseController {

    private final ServiceCourse serviceCourse;
    private final ServiceTeacher serviceTeacher;
    private final ServiceTeacherCourse serviceTeacherCourse;
    private final ModelMapper modelMapper;

    public CourseController(ServiceCourse serviceCourse, ServiceTeacher serviceTeacher, ServiceTeacherCourse serviceTeacherCourse, ModelMapper modelMapper) {
        this.serviceCourse = serviceCourse;
        this.serviceTeacher = serviceTeacher;
        this.serviceTeacherCourse = serviceTeacherCourse;
        this.modelMapper = modelMapper;
    }

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

    @GetMapping("/courses")
    public ResponseEntity<List<CourseGetDTO>> getAllCourses() {
        return ResponseEntity.ok(modelMapper.map(serviceCourse.getAll(), new TypeToken<List<CourseGetDTO>>() {
        }.getType()));
    }

    @DeleteMapping("courses/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id){
        Optional<EntityCourse> course = serviceCourse.get(id);
        if(course.isPresent()){
            if(!serviceTeacherCourse.findByCourse(course.get()).isEmpty()){
                return ResponseEntity.badRequest().body("Course is used in TeacherCourse table. Can't delete it until all associations are removed");
            }else{
                serviceCourse.delete(course.get());
                return ResponseEntity.ok("Course was successfully removed");
            }
        }else{
            return ResponseEntity.badRequest().body("Course with given ID doesn't exist");
        }
    }

    @PutMapping("courses/{id}")
    public ResponseEntity<?> editCourse(@Valid @RequestBody CoursePostDTO courseNew,@Valid @PathVariable Long id){
        Optional<EntityCourse> course = serviceCourse.get(id);
        if(course.isPresent()){
            course.get().setName(courseNew.getName());
            serviceCourse.save(course.get());
            return ResponseEntity.ok(modelMapper.map(course.get(), CourseGetDTO.class));
        }else{
            return ResponseEntity.badRequest().body("Course with given ID doesn't exist");
        }
    }

    @PostMapping("/teacherCourses")
    public ResponseEntity<?> addTeacherCourse(@Valid @RequestBody TeacherCoursePostDTO teacherCoursePostDTO) {
        Optional<EntityTeacher> teacher = serviceTeacher.get(teacherCoursePostDTO.getTeacherId());
        Optional<EntityCourse> course = serviceCourse.get(teacherCoursePostDTO.getCourseId());
        if (!teacher.isPresent()) {
            return ResponseEntity.badRequest().body("Teacher with given ID doesn't exist");
        }else if(!course.isPresent() ){
            return ResponseEntity.badRequest().body("Course with given ID doesn't exist");
        }else if( serviceTeacherCourse.findByCourseIdAndTeacherId(teacherCoursePostDTO.getCourseId(),teacherCoursePostDTO.getTeacherId()).isPresent()){
            return ResponseEntity.badRequest().body("There is already exactly same record saved");
        }
        TeacherCourseGetDTO savedTeacherCourse = new TeacherCourseGetDTO();
        teacher.ifPresent(entityTeacher -> course.ifPresent(entityCourse -> {
            EntityTeacherCourse entityTeacherCourse = new EntityTeacherCourse();
            entityTeacherCourse.setCourse(entityCourse);
            entityTeacherCourse.setTeacher(entityTeacher);
            serviceTeacherCourse.save(entityTeacherCourse);
            savedTeacherCourse.setCourse(modelMapper.map(entityCourse, CourseGetDTO.class));
            savedTeacherCourse.setTeacher(modelMapper.map(entityTeacher.getUsers(), UserGetDTO.class));
        }));
        return ResponseEntity.ok(savedTeacherCourse);
    }

    @GetMapping("/teacherCourses")
    public ResponseEntity<?> getAllTeacherCourses(){
        List<TeacherCourseGetDTO> dtoList = new ArrayList<>();
        serviceTeacherCourse.getAll().forEach(e-> dtoList.add(new TeacherCourseGetDTO(modelMapper.map(e.getCourse(), CourseGetDTO.class), modelMapper.map(e.getTeacher().getUsers(),UserGetDTO.class))));
        return ResponseEntity.ok(dtoList);
    }
}
