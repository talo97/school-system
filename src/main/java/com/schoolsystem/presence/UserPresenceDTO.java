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
public class UserPresenceDTO {
    private EnumDayOfWeek dayOfWeek;
    private EnumLessonNumber lessonNumber;
    private Boolean wasPresent;
    private String courseName;
}
