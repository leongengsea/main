package seedu.thanepark.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.thanepark.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.thanepark.logic.commands.HelpCommand.SHOWING_HELP_MESSAGE;
import static seedu.thanepark.logic.commands.HelpCommand.SHOWING_SHORT_HELP_MESSAGE;

import org.junit.Rule;
import org.junit.Test;

import seedu.thanepark.commons.events.BaseEvent;
import seedu.thanepark.commons.events.ui.ShowHelpRequestEvent;
import seedu.thanepark.logic.CommandHistory;
import seedu.thanepark.logic.parser.HelpCommandParser;
import seedu.thanepark.model.Model;
import seedu.thanepark.model.ModelManager;
import seedu.thanepark.ui.testutil.EventsCollectorRule;

public class HelpCommandTest {
    @Rule
    public final EventsCollectorRule eventsCollectorRule = new EventsCollectorRule();

    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();
    private CommandHistory commandHistory = new CommandHistory();

    @Test
    public void execute_help_success() {
        assertCommandSuccess(new HelpCommand(true, ""),
                model, commandHistory, SHOWING_SHORT_HELP_MESSAGE, expectedModel);

        BaseEvent recentEvent = eventsCollectorRule.eventsCollector.getMostRecent();
        assertTrue(recentEvent instanceof ShowHelpRequestEvent);
        assertTrue(((ShowHelpRequestEvent) recentEvent).isRequestSummarized());
        assertEquals(1, eventsCollectorRule.eventsCollector.getSize());
    }

    @Test
    public void execute_helpMore_success() {
        final String args = "more";
        assertCommandSuccess(new HelpCommandParser().parse(args),
                model, commandHistory, SHOWING_HELP_MESSAGE, expectedModel);

        BaseEvent recentEvent = eventsCollectorRule.eventsCollector.getMostRecent();
        assertTrue(recentEvent instanceof ShowHelpRequestEvent);
        assertFalse(((ShowHelpRequestEvent) recentEvent).isRequestSummarized());
        assertEquals(1, eventsCollectorRule.eventsCollector.getSize());
    }

    @Test
    public void execute_helpSpecificCommand_success() {
        int numEvents = 0;
        for (String commandWord : AllCommandWords.COMMAND_WORDS) {
            numEvents++;
            final String args = commandWord;
            assertCommandSuccess(new HelpCommand(false, args),
                    model, commandHistory, SHOWING_HELP_MESSAGE, expectedModel);

            BaseEvent recentEvent = eventsCollectorRule.eventsCollector.getMostRecent();
            assertTrue(recentEvent instanceof ShowHelpRequestEvent);
            assertFalse(((ShowHelpRequestEvent) recentEvent).isRequestSummarized());
            assertEquals(commandWord, ((ShowHelpRequestEvent) recentEvent).getCommandWord());
            assertEquals(numEvents, eventsCollectorRule.eventsCollector.getSize());
        }
    }
}
