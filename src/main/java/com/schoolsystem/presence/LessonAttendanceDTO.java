package com.schoolsystem.presence;

import com.schoolsystem.student.EntityStudent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class LessonAttendanceDTO {
    private Long lessonId;
    private String courseName;
    private List<StudentAttendanceDTO> students;

    LessonAttendanceDTO(List<EntityStudent> students, Long lessonId, String courseName) {
        this.lessonId = lessonId;
        this.courseName = courseName;
        this.students = new ArrayList<>();
        students.forEach(e -> {
            this.students.add(new StudentAttendanceDTO(e.getId(), e.getUsers().getFirstName(), e.getUsers().getLastName(), false));
        });
    }

    public LessonAttendanceDTO(List<EntityPresence> presences) {
        this.students = new ArrayList<>();
        if (presences.size() > 0) {
            this.lessonId = presences.get(0).getLesson().getId();
            this.courseName = presences.get(0).getLesson().getTeacherCourse().getCourse().getName();
        }
        presences.forEach(entityPresence -> {
            EntityStudent student = entityPresence.getStudent(); //temp value for cleaner code
            StudentAttendanceDTO studentAttendanceDTO = new StudentAttendanceDTO(student.getId(), student.getUsers().getFirstName(),
                    student.getUsers().getLastName(), entityPresence.getWasPresent());
            students.add(studentAttendanceDTO);
        });
    }
}
