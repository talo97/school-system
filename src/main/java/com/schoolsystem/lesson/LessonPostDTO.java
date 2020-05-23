package com.schoolsystem.lesson;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LessonPostDTO {

    private EnumDayOfWeek dayOfWeek;

    private EnumLessonNumber lessonNumber;

    private Long teacherCourseId = 0L;

    private Long entityClassId = 0L;

    public boolean containsEmptyValue() {
        return dayOfWeek == null || lessonNumber == null ||
                teacherCourseId == null || entityClassId == null;
    }

}
