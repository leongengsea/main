package seedu.thanepark.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.thanepark.logic.parser.CliSyntax.PREFIX_MAINTENANCE;
import static seedu.thanepark.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.thanepark.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.thanepark.logic.parser.CliSyntax.PREFIX_WAITING_TIME;
import static seedu.thanepark.logic.parser.CliSyntax.PREFIX_ZONE;
import static seedu.thanepark.model.Model.PREDICATE_SHOW_ALL_RIDES;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import seedu.thanepark.commons.core.Messages;
import seedu.thanepark.commons.core.index.Index;
import seedu.thanepark.commons.util.CollectionUtil;
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
 * Updates the details of an existing ride in Thanepark.
 */
public class UpdateCommand extends Command {

    public static final String COMMAND_WORD = "update";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Updates the details of the ride identified "
            + "by the index number used in the displayed ride list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + PREFIX_NAME + "NAME] "
            + "[" + PREFIX_MAINTENANCE + "MAINTENANCE] "
            + "[" + PREFIX_WAITING_TIME + "WAITING_TIME] "
            + "[" + PREFIX_ZONE + "ZONE] "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + PREFIX_MAINTENANCE + "90 "
            + PREFIX_WAITING_TIME + "60";

    public static final String MESSAGE_UPDATE_RIDE_SUCCESS = "Updated Ride: %1$s";
    public static final String MESSAGE_NOT_UPDATED = "At least one field to update must be provided.";
    public static final String MESSAGE_DUPLICATE_RIDE = "This ride already exists in the thanepark book.";
    public static final String MESSAGE_CLOSED_RIDE = "Maintenance and Waiting Time can only be updated "
            + "if the ride is opened.\n";

    private final Index index;
    private final UpdateRideDescriptor updateRideDescriptor;

    /**
     * @param index of the ride in the filtered ride list to edit
     * @param updateRideDescriptor details to edit the ride with
     */
    public UpdateCommand(Index index, UpdateRideDescriptor updateRideDescriptor) {
        requireNonNull(index);
        requireNonNull(updateRideDescriptor);

        this.index = index;
        this.updateRideDescriptor = new UpdateRideDescriptor(updateRideDescriptor);
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);
        List<Ride> lastShownList = model.getFilteredRideList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_RIDE_DISPLAYED_INDEX);
        }

        Ride rideToEdit = lastShownList.get(index.getZeroBased());
        Ride editedRide = createUpdatedRide(rideToEdit, updateRideDescriptor);

        if (!rideToEdit.isSameRide(editedRide) && model.hasRide(editedRide)) {
            throw new CommandException(MESSAGE_DUPLICATE_RIDE);
        }

        model.updateRide(rideToEdit, editedRide);
        model.updateFilteredRideList(PREDICATE_SHOW_ALL_RIDES);
        model.commitThanePark();
        return new CommandResult(String.format(MESSAGE_UPDATE_RIDE_SUCCESS, editedRide));
    }

    /**
     * Creates and returns a {@code Ride} with the details of {@code rideToEdit}
     * edited with {@code updateRideDescriptor}.
     */
    private static Ride createUpdatedRide(Ride rideToEdit,
                                          UpdateRideDescriptor updateRideDescriptor) throws CommandException {
        assert rideToEdit != null;

        if (rideToEdit.getStatus() != Status.OPEN) {
            if (updateRideDescriptor.getMaintenance().isPresent() || updateRideDescriptor.getWaitTime().isPresent()) {
                throw new CommandException(MESSAGE_CLOSED_RIDE);
            }
        }
        Name updatedName = updateRideDescriptor.getName().orElse(rideToEdit.getName());
        Maintenance updatedMaintenance =
                updateRideDescriptor.getMaintenance().orElse(rideToEdit.getDaysSinceMaintenance());
        WaitTime updatedWaitTime = updateRideDescriptor.getWaitTime().orElse(rideToEdit.getWaitingTime());
        Zone updatedZone = updateRideDescriptor.getZone().orElse(rideToEdit.getZone());
        Set<Tag> updatedTags = updateRideDescriptor.getTags().orElse(rideToEdit.getTags());
        Status updatedStatus = rideToEdit.getStatus();

        return new Ride(updatedName, updatedMaintenance, updatedWaitTime, updatedZone, updatedTags, updatedStatus);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UpdateCommand)) {
            return false;
        }

        // state check
        UpdateCommand e = (UpdateCommand) other;
        return index.equals(e.index)
                && updateRideDescriptor.equals(e.updateRideDescriptor);
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
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, maintenance, waitTime, zone, tags);
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
                    && getTags().equals(e.getTags());
        }
    }
}
