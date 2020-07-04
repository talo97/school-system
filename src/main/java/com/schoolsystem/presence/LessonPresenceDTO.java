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
public class LessonPresenceDTO {
    private List<StudentPresenceDTO> students;
    private Long lessonId;

    LessonPresenceDTO(List<EntityStudent> students, Long lessonId) {
        this.lessonId = lessonId;
        this.students = new ArrayList<>();
        students.forEach(e -> {
            this.students.add(new StudentPresenceDTO(e.getId(), e.getUsers().getFirstName(), e.getUsers().getLastName(), false));
        });
    }

    public LessonPresenceDTO(List<EntityPresence> presences) {
        this.students = new ArrayList<>();
        if (presences.size() > 0) {
            this.lessonId = presences.get(0).getLesson().getId();
        }
        presences.forEach(entityPresence -> {
            EntityStudent student = entityPresence.getStudent(); //temp value for cleaner code
            StudentPresenceDTO studentPresenceDTO = new StudentPresenceDTO(student.getId(),student.getUsers().getFirstName(),
                    student.getUsers().getLastName(),entityPresence.getWasPresent());
            students.add(studentPresenceDTO);
        });
    }
}
