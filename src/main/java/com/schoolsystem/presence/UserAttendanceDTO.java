package com.schoolsystem.presence;

import com.schoolsystem.lesson.EnumDayOfWeek;
import com.schoolsystem.lesson.EnumLessonNumber;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAttendanceDTO {
    private EnumDayOfWeek dayOfWeek;
    private EnumLessonNumber lessonNumber;
    private Boolean wasPresent;
    private String courseName;

    UserAttendanceDTO(EntityPresence presence) {
        this.dayOfWeek = presence.getLesson().getDayOfWeek();
        this.lessonNumber = presence.getLesson().getLessonNumber();
        this.wasPresent = presence.getWasPresent();
        this.courseName = presence.getLesson().getTeacherCourse().getCourse().getName();
    }
}
