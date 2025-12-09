package com.ticketblock.utils;

import com.ticketblock.exception.InvalidDateAndTimeException;
import lombok.Getter;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

/**
 * Enum representing all 15-minute time slots in a day (00:00 - 23:45).
 * Each slot has a numeric index and an associated LocalTime.
 */
@Getter
public enum TimeSlot {
    // Slot 00:00 - 01:00
    SLOT_00_00(0, LocalTime.of(0, 0)),
    SLOT_00_15(1, LocalTime.of(0, 15)),
    SLOT_00_30(2, LocalTime.of(0, 30)),
    SLOT_00_45(3, LocalTime.of(0, 45)),

    SLOT_01_00(4, LocalTime.of(1, 0)),
    SLOT_01_15(5, LocalTime.of(1, 15)),
    SLOT_01_30(6, LocalTime.of(1, 30)),
    SLOT_01_45(7, LocalTime.of(1, 45)),

    SLOT_02_00(8, LocalTime.of(2, 0)),
    SLOT_02_15(9, LocalTime.of(2, 15)),
    SLOT_02_30(10, LocalTime.of(2, 30)),
    SLOT_02_45(11, LocalTime.of(2, 45)),

    SLOT_03_00(12, LocalTime.of(3, 0)),
    SLOT_03_15(13, LocalTime.of(3, 15)),
    SLOT_03_30(14, LocalTime.of(3, 30)),
    SLOT_03_45(15, LocalTime.of(3, 45)),

    SLOT_04_00(16, LocalTime.of(4, 0)),
    SLOT_04_15(17, LocalTime.of(4, 15)),
    SLOT_04_30(18, LocalTime.of(4, 30)),
    SLOT_04_45(19, LocalTime.of(4, 45)),

    SLOT_05_00(20, LocalTime.of(5, 0)),
    SLOT_05_15(21, LocalTime.of(5, 15)),
    SLOT_05_30(22, LocalTime.of(5, 30)),
    SLOT_05_45(23, LocalTime.of(5, 45)),

    SLOT_06_00(24, LocalTime.of(6, 0)),
    SLOT_06_15(25, LocalTime.of(6, 15)),
    SLOT_06_30(26, LocalTime.of(6, 30)),
    SLOT_06_45(27, LocalTime.of(6, 45)),

    SLOT_07_00(28, LocalTime.of(7, 0)),
    SLOT_07_15(29, LocalTime.of(7, 15)),
    SLOT_07_30(30, LocalTime.of(7, 30)),
    SLOT_07_45(31, LocalTime.of(7, 45)),

    SLOT_08_00(32, LocalTime.of(8, 0)),
    SLOT_08_15(33, LocalTime.of(8, 15)),
    SLOT_08_30(34, LocalTime.of(8, 30)),
    SLOT_08_45(35, LocalTime.of(8, 45)),

    SLOT_09_00(36, LocalTime.of(9, 0)),
    SLOT_09_15(37, LocalTime.of(9, 15)),
    SLOT_09_30(38, LocalTime.of(9, 30)),
    SLOT_09_45(39, LocalTime.of(9, 45)),

    SLOT_10_00(40, LocalTime.of(10, 0)),
    SLOT_10_15(41, LocalTime.of(10, 15)),
    SLOT_10_30(42, LocalTime.of(10, 30)),
    SLOT_10_45(43, LocalTime.of(10, 45)),

    SLOT_11_00(44, LocalTime.of(11, 0)),
    SLOT_11_15(45, LocalTime.of(11, 15)),
    SLOT_11_30(46, LocalTime.of(11, 30)),
    SLOT_11_45(47, LocalTime.of(11, 45)),

    SLOT_12_00(48, LocalTime.of(12, 0)),
    SLOT_12_15(49, LocalTime.of(12, 15)),
    SLOT_12_30(50, LocalTime.of(12, 30)),
    SLOT_12_45(51, LocalTime.of(12, 45)),

    SLOT_13_00(52, LocalTime.of(13, 0)),
    SLOT_13_15(53, LocalTime.of(13, 15)),
    SLOT_13_30(54, LocalTime.of(13, 30)),
    SLOT_13_45(55, LocalTime.of(13, 45)),

    SLOT_14_00(56, LocalTime.of(14, 0)),
    SLOT_14_15(57, LocalTime.of(14, 15)),
    SLOT_14_30(58, LocalTime.of(14, 30)),
    SLOT_14_45(59, LocalTime.of(14, 45)),

    SLOT_15_00(60, LocalTime.of(15, 0)),
    SLOT_15_15(61, LocalTime.of(15, 15)),
    SLOT_15_30(62, LocalTime.of(15, 30)),
    SLOT_15_45(63, LocalTime.of(15, 45)),

    SLOT_16_00(64, LocalTime.of(16, 0)),
    SLOT_16_15(65, LocalTime.of(16, 15)),
    SLOT_16_30(66, LocalTime.of(16, 30)),
    SLOT_16_45(67, LocalTime.of(16, 45)),

    SLOT_17_00(68, LocalTime.of(17, 0)),
    SLOT_17_15(69, LocalTime.of(17, 15)),
    SLOT_17_30(70, LocalTime.of(17, 30)),
    SLOT_17_45(71, LocalTime.of(17, 45)),

    SLOT_18_00(72, LocalTime.of(18, 0)),
    SLOT_18_15(73, LocalTime.of(18, 15)),
    SLOT_18_30(74, LocalTime.of(18, 30)),
    SLOT_18_45(75, LocalTime.of(18, 45)),

    SLOT_19_00(76, LocalTime.of(19, 0)),
    SLOT_19_15(77, LocalTime.of(19, 15)),
    SLOT_19_30(78, LocalTime.of(19, 30)),
    SLOT_19_45(79, LocalTime.of(19, 45)),

    SLOT_20_00(80, LocalTime.of(20, 0)),
    SLOT_20_15(81, LocalTime.of(20, 15)),
    SLOT_20_30(82, LocalTime.of(20, 30)),
    SLOT_20_45(83, LocalTime.of(20, 45)),

    SLOT_21_00(84, LocalTime.of(21, 0)),
    SLOT_21_15(85, LocalTime.of(21, 15)),
    SLOT_21_30(86, LocalTime.of(21, 30)),
    SLOT_21_45(87, LocalTime.of(21, 45)),

    SLOT_22_00(88, LocalTime.of(22, 0)),
    SLOT_22_15(89, LocalTime.of(22, 15)),
    SLOT_22_30(90, LocalTime.of(22, 30)),
    SLOT_22_45(91, LocalTime.of(22, 45)),

    SLOT_23_00(92, LocalTime.of(23, 0)),
    SLOT_23_15(93, LocalTime.of(23, 15)),
    SLOT_23_30(94, LocalTime.of(23, 30)),
    SLOT_23_45(95, LocalTime.of(23, 45));

    private final int index;
    private final LocalTime time;

    TimeSlot(int index, LocalTime time) {
        this.index = index;
        this.time = time;
    }


    // ============================================
    // LOOKUP METHODS
    // ============================================

    /**
     * Find a slot from its index (received from the frontend).
     *
     * @param index Index of the slot
     * @return Optional TimeSlot if found
     */
    public static Optional<TimeSlot> fromIndex(int index) {
        return Arrays.stream(TimeSlot.values())
                .filter(slot -> slot.index == index)
                .findFirst();
    }

    /**
     * Find a slot from its index (received from the frontend).
     *
     * @param index Index of the slot
     * @return TimeSlot
     * @throws InvalidDateAndTimeException if the index is invalid
     */
    public static TimeSlot fromIndexOrThrow(int index) {
        return fromIndex(index)
                .orElseThrow(() -> new InvalidDateAndTimeException(
                        "Invalid slot index: " + index + ". Must be between 0 and 95."
                ));
    }

    /**
     * Find the nearest time slot from a given time.
     * Rounds to the closest 15-minute interval.
     *
     * @param time The LocalTime to convert
     * @return The closest TimeSlot
     *
     * Example:
     *      TimeSlot.fromTime(LocalTime.of(9, 7))  -> SLOT_09_00
     *      TimeSlot.fromTime(LocalTime.of(9, 12)) -> SLOT_09_15
     */
    public static TimeSlot fromTime(LocalTime time) {
        int totalMinutes = time.getHour() * 60 + time.getMinute();

        int index = (int) Math.round(totalMinutes / 15.0);

        return fromIndexOrThrow(index);
    }

    // ============================================
    // OPERATIONS WITH DURATION (from Frontend)
    // ============================================

    /**
     * Adds a duration (number of slots) to this time slot.
     * This is the key method for handling data coming from the frontend!
     *
     * @param duration Number of slots to add (received from the frontend)
     * @return The resulting TimeSlot
     * @throws IllegalArgumentException if the result exceeds the limit
     *
     * Example:
     *      SLOT_09_00.plus(3) → SLOT_09_45
     *      SLOT_09_00.plus(8) → SLOT_11_00
     */
    public TimeSlot plus(int duration) {
        if (duration < 0) {
            throw new InvalidDateAndTimeException("Duration must be >= 0");
        }
        int newIndex = this.index + duration;

        return fromIndexOrThrow(newIndex);
    }

    /**
     * Calculates the end slot given a start slot and a duration from the frontend.
     *
     * @param startSlot Index of the starting slot (from the frontend)
     * @param duration Duration in slots (from the frontend)
     * @return The ending TimeSlot
     *
     * Example:
     *      TimeSlot.calculateEndSlot(36, 3) → SLOT_09_45
     */
    public static TimeSlot calculateEndSlot(int startSlot, int duration) {
        TimeSlot start = fromIndexOrThrow(startSlot);
        return start.plus(duration);
    }

    /**
     * Gets the next time slot.
     *
     * @return The next TimeSlot
     */
    public TimeSlot next() {
        return plus(1);
    }

    /**
     * Gets the previous time slot.
     *
     * @return The previous TimeSlot
     * @throws IllegalArgumentException if already at the first slot
     */
    public TimeSlot previous() {
        if (this.index == 0) {
            throw new IllegalArgumentException("Cannot go before first slot");
        }
        return fromIndexOrThrow(this.index - 1);
    }

    /**
     * Checks if this slot + duration stays within the day.
     *
     * @param duration Number of slots
     * @return true if valid, false otherwise
     */
    public boolean canAddDuration(int duration) {
        return (this.index + duration) <= 95;
    }



}
