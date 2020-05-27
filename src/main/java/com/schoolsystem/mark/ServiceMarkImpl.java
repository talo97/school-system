package com.schoolsystem.mark;

import com.schoolsystem.classes.EntityClass;
import com.schoolsystem.common.CommonServiceImpl;
import com.schoolsystem.course.EntityTeacherCourse;
import com.schoolsystem.lesson.ServiceLesson;
import com.schoolsystem.student.EntityStudent;
import com.schoolsystem.student.ServiceStudent;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceMarkImpl extends CommonServiceImpl<EntityMark, DaoMark> implements ServiceMark {

    private final ServiceStudent serviceStudent;
    private final ServiceLesson serviceLesson;
    private final ModelMapper modelMapper;

    public ServiceMarkImpl(DaoMark repository, ServiceStudent serviceStudent, ServiceLesson serviceLesson, ModelMapper modelMapper) {
        super(repository);
        this.serviceStudent = serviceStudent;
        this.serviceLesson = serviceLesson;
        this.modelMapper = modelMapper;
    }

    @Override
    public EntityMark save(MarkPostDTO markPostDTO, EntityStudent student, EntityTeacherCourse teacherCourse) {
        EntityMark mark = modelMapper.map(markPostDTO, EntityMark.class);
        mark.setTeacherCourse(teacherCourse);
        mark.setStudent(student);
        LocalDateTime dateTime = LocalDateTime.now(ZoneId.systemDefault());
        ZoneId newZone = ZoneId.of("Europe/Warsaw");
        LocalDateTime newTime = dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(newZone).toLocalDateTime();
        mark.setLastChange(Date.valueOf(newTime.toLocalDate()));
        return save(mark);
    }

    @Override
    public List<EntityMark> findAllByStudent(EntityStudent student) {
        return repository.findAllByStudent(student);
    }

    @Override
    public List<EntityMark> findAllByClass(EntityClass entityClass) {
        return repository.findAllByStudentIn(serviceStudent.findAllByStudentClass(entityClass));
    }

    @Override
    public List<StudentMarksGetDTO> getStudentsMarksByClassAndCourse(EntityClass entityClass, EntityTeacherCourse teacherCourse) {
        List<StudentMarksGetDTO> studentsMarks = new ArrayList<>();
        serviceStudent.findAllByStudentClass(entityClass).forEach(e -> {
            StudentMarksGetDTO studentMarks = modelMapper.map(e.getUsers(), StudentMarksGetDTO.class);
            studentMarks.setId(e.getId());
            studentMarks.setGrades(modelMapper.map(repository.findAllByStudentAndTeacherCourse(e, teacherCourse), new TypeToken<List<MarkShortGetDTO>>() {
            }.getType()));
            studentsMarks.add(studentMarks);
        });
        return studentsMarks;
    }

    @Override
    public List<TeacherCourseMarksGetDTO> getStudentMarks(EntityStudent student) {
        return serviceLesson.findDistinctTeacherCoursesOfGivenClass(student.getStudentClass()).stream().map(teacherCourse -> {
            return new TeacherCourseMarksGetDTO(teacherCourse.getCourse().getName(), modelMapper.map(repository.findAllByStudentAndTeacherCourse(student, teacherCourse),
                    new TypeToken<List<MarkShortGetDTO>>() {
                    }.getType()));
        }).collect(Collectors.toList());
    }

}
