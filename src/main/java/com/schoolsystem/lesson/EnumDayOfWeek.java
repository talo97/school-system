package com.schoolsystem.lesson;

import lombok.Getter;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public enum EnumDayOfWeek {
    MONDAY(DayOfWeek.MONDAY),
    TUESDAY(DayOfWeek.TUESDAY),
    WEDNESDAY(DayOfWeek.WEDNESDAY),
    THURSDAY(DayOfWeek.THURSDAY),
    FRIDAY(DayOfWeek.FRIDAY);

    private DayOfWeek uselessThingToDoButWhatever;

    EnumDayOfWeek(DayOfWeek dayOfWeek) {
        uselessThingToDoButWhatever = dayOfWeek;
    }

    private static final Map<DayOfWeek, EnumDayOfWeek> LOOKUP = Arrays.stream(values()).collect(Collectors.toMap(EnumDayOfWeek::getUselessThingToDoButWhatever, x -> x));

    public static Optional<EnumDayOfWeek> get(DayOfWeek dayOfWeek) {
        //TODO::CHANGE LATER!!! FOR testing rn
        return Optional.of(MONDAY);
//        if (LOOKUP.get(dayOfWeek) != null) {
//            return Optional.of(LOOKUP.get(dayOfWeek));
//        } else {
//            return Optional.empty();
//        }
    }
}
