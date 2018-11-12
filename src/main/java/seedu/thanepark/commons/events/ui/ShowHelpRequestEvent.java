package seedu.thanepark.commons.events.ui;

import seedu.thanepark.commons.events.BaseEvent;

/**
 * An event requesting to view the help page.
 */
public class ShowHelpRequestEvent extends BaseEvent {

    private final boolean isSummarized;
    private final String commandWord;

    /**
     * Creates an event requesting for help info, and the level of detail is specified by {@param summarized}.
     */
    public ShowHelpRequestEvent(boolean isSummarized, String commandWord) {
        this.isSummarized = isSummarized;
        this.commandWord = commandWord;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public boolean isRequestSummarized() {
        return isSummarized;
    }

    public String getCommandWord() {
        return commandWord;
    }
}
