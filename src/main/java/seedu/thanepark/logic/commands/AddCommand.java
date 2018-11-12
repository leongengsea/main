package seedu.thanepark.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.thanepark.logic.parser.CliSyntax.PREFIX_MAINTENANCE;
import static seedu.thanepark.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.thanepark.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.thanepark.logic.parser.CliSyntax.PREFIX_WAITING_TIME;
import static seedu.thanepark.logic.parser.CliSyntax.PREFIX_ZONE;

import seedu.thanepark.logic.CommandHistory;
import seedu.thanepark.logic.commands.exceptions.CommandException;
import seedu.thanepark.model.Model;
import seedu.thanepark.model.ride.Ride;

/**
 * Adds a ride to the thanepark book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a ride to the thanepark book. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_MAINTENANCE + "MAINTENANCE "
            + PREFIX_WAITING_TIME + "WAITING TIME "
            + PREFIX_ZONE + "ZONE "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "Transformers "
            + PREFIX_MAINTENANCE + "3 "
            + PREFIX_WAITING_TIME + "45 "
            + PREFIX_ZONE + "311, Clementi Ave 2, #02-25 "
            + PREFIX_TAG + "4D "
            + PREFIX_TAG + "indoor";

    public static final String MESSAGE_SUCCESS = "New ride added: %1$s";
    public static final String MESSAGE_DUPLICATE_RIDE = "This ride already exists in the thanepark book";

    private final Ride toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Ride}
     */
    public AddCommand(Ride ride) {
        requireNonNull(ride);
        toAdd = ride;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        if (model.hasRide(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_RIDE);
        }

        model.addRide(toAdd);
        model.commitThanePark();
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddCommand // instanceof handles nulls
                && toAdd.equals(((AddCommand) other).toAdd));
    }
}
