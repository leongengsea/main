package seedu.thanepark.model.ride;

import static seedu.thanepark.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import seedu.thanepark.model.logging.HtmlFormattable;
import seedu.thanepark.model.ride.exceptions.InvalidNumericAttributeException;
import seedu.thanepark.model.tag.Tag;

/**
 * Represents a Ride in the thanepark book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Ride implements HtmlFormattable {
    public static final Ride RIDE_TEMPLATE =
        new Ride(new Name("Name"), new Maintenance(0), new WaitTime(0),
            new Zone("Zone"), new HashSet<>(), Status.OPEN);
    private static final Status DEFAULT_STATUS = Status.OPEN;

    // Identity fields
    private final Name name;
    private final Maintenance daysSinceMaintenance;
    private final WaitTime waitingTime;

    // Data fields
    private final Zone zone;
    private final Set<Tag> tags = new HashSet<>();

    //Current state
    private final Status status;

    /**
     * Every field except status must be present and not null. Default value of status is OPEN.
     */
    public Ride(Name name, Maintenance daysSinceMaintenance, WaitTime waitingTime, Zone zone, Set<Tag> tags) {
        this(name, daysSinceMaintenance, waitingTime, zone, tags, DEFAULT_STATUS);
    }

    /**
     * Every field must be present and not null.
     */
    public Ride(Name name, Maintenance daysSinceMaintenance, WaitTime waitingTime, Zone zone, Set<Tag> tags,
                Status status) {
        requireAllNonNull(name, daysSinceMaintenance, waitingTime, zone, tags, status);
        this.name = name;
        this.daysSinceMaintenance = daysSinceMaintenance;
        this.waitingTime = waitingTime;
        this.zone = zone;
        this.tags.addAll(tags);
        this.status = status;
    }

    public Name getName() {
        return name;
    }

    public WaitTime getWaitingTime() {
        return waitingTime;
    }

    public Maintenance getDaysSinceMaintenance() {
        return daysSinceMaintenance;
    }

    public Status getStatus() {
        return status;
    }

    public Zone getZone() {
        return zone;
    }

    /**
     * Returns an immutable information set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<String> getInformation() {
        List<String> information = new LinkedList<>();
        information.add(zone.value);
        information.add(daysSinceMaintenance.toString());
        information.add(waitingTime.toString());
        Set<String> informationSet = new HashSet<>();
        informationSet.addAll(information);
        return Collections.unmodifiableSet(informationSet);
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns the attribute that is required.
     * Throws an exception if the attribute has an invalid type.
     */
    public NumericAttribute getAttribute(NumericAttribute attribute) throws InvalidNumericAttributeException {
        if (attribute instanceof WaitTime) {
            return this.getWaitingTime();
        } else if (attribute instanceof Maintenance) {
            return this.getDaysSinceMaintenance();
        } else {
            throw new InvalidNumericAttributeException();
        }
    }

    /**
     * Returns true if both rides have the same name.
     * This defines a weaker notion of equality between two rides.
     */
    public boolean isSameRide(Ride otherRide) {
        if (otherRide == this) {
            return true;
        }

        return otherRide != null
                && otherRide.getName().equals(getName());
    }

    @Override
    public List<String> getFieldHeaders() {
        List<String> headers = new LinkedList<>();
        headers.add("Status");
        headers.add("Days since last maintenance");
        headers.add("Waiting Time");
        return headers;
    }

    @Override
    public List<String> getFields() {
        List<String> fields = new LinkedList<>();
        fields.add(getStatus().name());
        fields.add(getDaysSinceMaintenance().toString());
        fields.add(getWaitingTime().toString());
        return fields;
    }

    /**
     * Returns true if both rides have the same identity and data fields.
     * This defines a stronger notion of equality between two rides.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Ride)) {
            return false;
        }

        Ride otherRide = (Ride) other;
        return otherRide.getName().equals(getName())
                && otherRide.getDaysSinceMaintenance().equals(getDaysSinceMaintenance())
                && otherRide.getWaitingTime().equals(getWaitingTime())
                && otherRide.getZone().equals(getZone())
                && otherRide.getStatus() == getStatus()
                && otherRide.getTags().equals(getTags());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, daysSinceMaintenance, waitingTime, zone, status, tags);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append(" Maintenance: ")
                .append(getDaysSinceMaintenance())
                .append(" WaitTime: ")
                .append(getWaitingTime())
                .append(" Zone: ")
                .append(getZone())
                .append(" Tags: ");
        getTags().forEach(builder::append);
        return builder.toString();
    }
}
