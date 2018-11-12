package seedu.thanepark.model.ride;

import static java.util.Objects.requireNonNull;
import static seedu.thanepark.commons.util.AppUtil.checkArgument;

/**
 * Represents a Ride's current waiting time, in minutes, in the park management app.
 */
public class WaitTime implements NumericAttribute {


    public static final String MESSAGE_WAIT_TIME_CONSTRAINTS =
            "Waiting time should only contain positive integers with at least 1 digit long, "
            + "and should be less than 1 billion";
    public static final String WAIT_TIME_VALIDATION_REGEX = "\\d+";
    private final int value;

    /**
     * Constructs a {@code WaitTime}.
     *
     * @param waitingTimeString Waiting time in minutes.
     */
    public WaitTime(String waitingTimeString) {
        requireNonNull(waitingTimeString);
        checkArgument(isValidWaitTime(waitingTimeString), MESSAGE_WAIT_TIME_CONSTRAINTS);
        value = Integer.parseInt(waitingTimeString);
    }

    /**
     * Constructs a {@code WaitTime}.
     * @param waitingTime Waiting time in minutes.
     */
    public WaitTime(int waitingTime) {
        value = waitingTime;
    }

    public int getValue() {
        return value;
    }

    /**
     * Returns true if a given string is a valid days since last maintenance.
     */
    public static boolean isValidWaitTime(String test) {
        return test.matches(WAIT_TIME_VALIDATION_REGEX) && test.length() < 10;
    }

    @Override
    public String toString() {
        return String.valueOf(value) + " min";
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
            || (other instanceof WaitTime // instanceof handles nulls
            && value == ((WaitTime) other).value); // state check
    }

    @Override
    public int hashCode() {
        return value;
    }

}
