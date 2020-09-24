package com.schoolsystem.lesson;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public enum EnumLessonNumber {
    //TODO::add conversion from any time not in range to first lesson to make testing easier
    FIRST(8),
    SECOND(9),
    THIRD(10),
    FOURTH(11),
    FIFTH(12),
    SIXTH(13),
    SEVENTH(14),
    EIGHTH(15),
    NINTH(16);

    int hour;

    EnumLessonNumber(int hour) {
        this.hour = hour;
    }

    private static final Map<Integer, EnumLessonNumber> LOOKUP = Arrays.stream(values()).collect(Collectors.toMap(EnumLessonNumber::getHour, x -> x));

    public static Optional<EnumLessonNumber> get(Integer lessonNumber) {
        if (LOOKUP.get(lessonNumber) != null) {
            return Optional.of(LOOKUP.get(lessonNumber));
        } else {
            return Optional.empty();
        }
    }
}
