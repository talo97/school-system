package com.schoolsystem.common;

import com.schoolsystem.lesson.EnumDayOfWeek;
import com.schoolsystem.lesson.EnumLessonNumber;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Component
public class SchoolTimeUtil {
    /**
     * Returns enum value representing current day of week.
     *
     * @return EnumDayOfWeek
     */
    public final Optional<EnumDayOfWeek> getCurrentDayOfWeek(){
        ZonedDateTime currentPolandTime = ZonedDateTime.now(ZoneId.of("Europe/Warsaw"));
        return EnumDayOfWeek.get(currentPolandTime.getDayOfWeek());
    }
    /**
     * Returns enum value representing current lesson.
     *
     * @return EnumLessonNumber
     */
    public final Optional<EnumLessonNumber> getLessonNumberByLocalDateTime(){
        ZonedDateTime currentPolandTime = ZonedDateTime.now(ZoneId.of("Europe/Warsaw"));
        return EnumLessonNumber.get(currentPolandTime.getHour());
    }

    public final Date getCurrentSqlDate(){
        return Date.valueOf(LocalDate.now(ZoneId.of("Europe/Warsaw")));
    }
    public final Timestamp getCurrentSqlTimestamp(){
        return Timestamp.valueOf(LocalDateTime.now(ZoneId.of("Europe/Warsaw")));
    }
}
