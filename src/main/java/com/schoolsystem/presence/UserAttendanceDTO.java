package com.schoolsystem.presence;

import com.schoolsystem.lesson.EnumDayOfWeek;
import com.schoolsystem.lesson.EnumLessonNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAttendanceDTO {
    private Long id;
    private EnumDayOfWeek dayOfWeek;
    private EnumLessonNumber lessonNumber;
    private Boolean wasPresent;
    private String courseName;
    private Date date;

    UserAttendanceDTO(EntityPresence presence) {
        this.id = presence.getId();
        this.dayOfWeek = presence.getLesson().getDayOfWeek();
        this.lessonNumber = presence.getLesson().getLessonNumber();
        this.wasPresent = presence.getWasPresent();
        this.courseName = presence.getLesson().getTeacherCourse().getCourse().getName();
        this.date = presence.getDate();
    }
}
