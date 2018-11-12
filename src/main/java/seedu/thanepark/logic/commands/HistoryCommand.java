package seedu.thanepark.logic.commands;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import seedu.thanepark.commons.core.EventsCenter;
import seedu.thanepark.commons.events.ui.ShowHistoryRequestEvent;
import seedu.thanepark.commons.util.FilePathToUrl;
import seedu.thanepark.logic.CommandHistory;
import seedu.thanepark.model.Model;
import seedu.thanepark.storage.html.CommandReportGenerator;

/**
 * Lists all the commands entered by user from the start of app launch.
 */
public class HistoryCommand extends Command {

    public static final String COMMAND_WORD = "history";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows history of executed commands.\n"
            + "Example: " + COMMAND_WORD;
    public static final String MORE_INFO_FLAG = "more";

    public static final String MESSAGE_SUCCESS = "Entered commands (from most recent to earliest):\n%1$s";
    public static final String MESSAGE_HISTORY_WINDOW = "Showing requested history of commands.";
    public static final String MESSAGE_HISTORY_WINDOW_FAILURE = "Failed to generate Html report.";
    public static final String MESSAGE_NO_HISTORY = "You have not yet entered any commands.";

    private static final FilePathToUrl REPORT_PAGE_PATH =
            new FilePathToUrl("report.html", true);
    private static final String COMMAND_ENTRY_REPORT_TITLE = "Command History Report";

    private static final String[] ARGUMENTS_BLANK = {""};
    private final boolean isSummarized;

    /**
     * Creates a HistoryCommand.
     */
    public HistoryCommand() {
        this(ARGUMENTS_BLANK);
    }

    /**
     * Creates a HistoryCommand that requests for help based on {@param args}.
     */
    public HistoryCommand(String[] args) {
        //summarized history
        if (args.length == 1 && args[0].isEmpty()) {
            isSummarized = true;
        //full history
        } else if (args.length == 1 && args[0].equals(MORE_INFO_FLAG)) {
            isSummarized = false;
        //error
        } else {
            isSummarized = false;
        }
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) {
        requireNonNull(history);
        //request for CommandEntry list
        if (!isSummarized) {
            try {
                CommandReportGenerator.getInstance()
                    .generateHtml(COMMAND_ENTRY_REPORT_TITLE, history.getCommandEntryList(), REPORT_PAGE_PATH);
                EventsCenter.getInstance().post(new ShowHistoryRequestEvent(REPORT_PAGE_PATH));
                return new CommandResult(MESSAGE_HISTORY_WINDOW);
            } catch (IOException e) {
                e.printStackTrace();
                return new CommandResult(MESSAGE_HISTORY_WINDOW_FAILURE);
            }
        }

        assert(isSummarized);

        //request for command history in the result window
        List<String> previousCommands = history.getHistory();
        if (previousCommands.isEmpty()) {
            return new CommandResult(MESSAGE_NO_HISTORY);
        } else {
            Collections.reverse(previousCommands);
            return new CommandResult(String.format(MESSAGE_SUCCESS, String.join("\n", previousCommands)));
        }

    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof HistoryCommand // instanceof handles nulls
                && isSummarized == ((HistoryCommand) other).isSummarized); // state check
    }
}
