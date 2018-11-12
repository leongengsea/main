package seedu.thanepark.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.thanepark.model.Model.PREDICATE_SHOW_ALL_RIDES;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.thanepark.commons.core.Messages;
import seedu.thanepark.commons.core.index.Index;
import seedu.thanepark.logic.CommandHistory;
import seedu.thanepark.logic.commands.exceptions.CommandException;
import seedu.thanepark.model.Model;
import seedu.thanepark.model.ride.Maintenance;
import seedu.thanepark.model.ride.Name;
import seedu.thanepark.model.ride.Ride;
import seedu.thanepark.model.ride.Status;
import seedu.thanepark.model.ride.WaitTime;
import seedu.thanepark.model.ride.Zone;
import seedu.thanepark.model.tag.Tag;

/**
 * Shut down an existing ride in the thane park.
 */
public class ShutDownCommand extends Command {

    public static final String COMMAND_WORD = "shutdown";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shutdown the ride identified by index.\n "
            + "Parameters: INDEX (Must be a positive integer that is not larger than the size of the ride list)\n"
            + "Example: " + COMMAND_WORD + " 3";

    public static final String MESSAGE_SHUTDOWN_RIDE_SUCCESS = "Ride is shut down: %1$s";
    public static final String MESSAGE_DUPLICATE_RIDE = "This ride is already shut down.";

    private final Index index;
    private final UpdateRideDescriptor shutdownRideDescriptor;

    /**
     * @param index of the ride in the filtered ride list to open
     */
    public ShutDownCommand(Index index) {
        requireNonNull(index);

        this.index = index;
        this.shutdownRideDescriptor = new UpdateRideDescriptor();
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);
        List<Ride> lastShownList = model.getFilteredRideList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_RIDE_DISPLAYED_INDEX);
        }

        Ride rideToShutDown = lastShownList.get(index.getZeroBased());
        Ride editedRide = createUpdatedRide(rideToShutDown, shutdownRideDescriptor);

        if (rideToShutDown.isSameRide(editedRide) && rideToShutDown.equals(editedRide)) {
            throw new CommandException(MESSAGE_DUPLICATE_RIDE);
        }

        model.updateRide(rideToShutDown, editedRide);
        model.updateFilteredRideList(PREDICATE_SHOW_ALL_RIDES);
        model.commitThanePark();
        return new CommandResult(String.format(MESSAGE_SHUTDOWN_RIDE_SUCCESS, editedRide));
    }

    /**
     * Creates and returns a {@code Ride} with the details of {@code rideToOpen}
     * edited with {@code shutdownRideDescriptor}.
     */
    private static Ride createUpdatedRide(Ride rideToOpen, UpdateRideDescriptor openRideDescriptor) {
        assert rideToOpen != null;

        Name updatedName = openRideDescriptor.getName().orElse(rideToOpen.getName());
        Maintenance updatedMaintenance =
                openRideDescriptor.getMaintenance().orElse(rideToOpen.getDaysSinceMaintenance());
        WaitTime updatedWaitTime = new WaitTime(0);
        Zone updatedZone = openRideDescriptor.getZone().orElse(rideToOpen.getZone());
        Set<Tag> updatedTags = openRideDescriptor.getTags().orElse(rideToOpen.getTags());
        Status updatedStatus = Status.SHUTDOWN;

        return new Ride(updatedName, updatedMaintenance, updatedWaitTime, updatedZone, updatedTags, updatedStatus);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ShutDownCommand)) {
            return false;
        }

        // state check
        ShutDownCommand e = (ShutDownCommand) other;
        return index.equals(e.index)
                && shutdownRideDescriptor.equals(e.shutdownRideDescriptor);
    }

    /**
     * Stores the details to edit the ride with. Each non-empty field value will replace the
     * corresponding field value of the ride.
     */
    public static class UpdateRideDescriptor {
        private Name name;
        private Maintenance maintenance;
        private WaitTime waitTime;
        private Zone zone;
        private Set<Tag> tags;
        private Status status;

        public UpdateRideDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public UpdateRideDescriptor(UpdateRideDescriptor toCopy) {
            setName(toCopy.name);
            setMaintenance(toCopy.maintenance);
            setWaitTime(toCopy.waitTime);
            setZone(toCopy.zone);
            setTags(toCopy.tags);
            setStatus(toCopy.status);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setMaintenance(Maintenance maintenance) {
            this.maintenance = maintenance;
        }

        public Optional<Maintenance> getMaintenance() {
            return Optional.ofNullable(maintenance);
        }

        public void setWaitTime(WaitTime waitTime) {
            this.waitTime = waitTime;
        }

        public Optional<WaitTime> getWaitTime() {
            return Optional.ofNullable(waitTime);
        }

        public void setZone(Zone zone) {
            this.zone = zone;
        }

        public Optional<Zone> getZone() {
            return Optional.ofNullable(zone);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */

        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public Optional<Status> getStatus() {
            return Optional.ofNullable(status);
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof UpdateRideDescriptor)) {
                return false;
            }

            // state check
            UpdateRideDescriptor e = (UpdateRideDescriptor) other;

            return getName().equals(e.getName())
                    && getMaintenance().equals(e.getMaintenance())
                    && getWaitTime().equals(e.getWaitTime())
                    && getZone().equals(e.getZone())
                    && getTags().equals(e.getTags())
                    && getStatus().equals(e.getStatus());
        }
    }
}

