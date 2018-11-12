package seedu.thanepark.ui;

import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.thanepark.commons.core.LogsCenter;
import seedu.thanepark.commons.events.ui.NewResultAvailableEvent;
import seedu.thanepark.commons.events.ui.SuggestCommandEvent;
import seedu.thanepark.logic.ListElementPointer;
import seedu.thanepark.logic.Logic;
import seedu.thanepark.logic.commands.CommandResult;
import seedu.thanepark.logic.commands.SuggestCommand;
import seedu.thanepark.logic.commands.exceptions.CommandException;
import seedu.thanepark.logic.matchers.PrefixMatcher;
import seedu.thanepark.logic.parser.SuggestCommandParser;
import seedu.thanepark.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    private static final int CHARACTER_LIMIT = 250;

    private final Logger logger = LogsCenter.getLogger(CommandBox.class);
    private final Logic logic;
    private ListElementPointer historySnapshot;
    private String pendingText = "";
    private int caretPosition = 0;

    @FXML
    private TextField commandTextField;

    public CommandBox(Logic logic) {
        super(FXML);
        this.logic = logic;
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
        commandTextField.caretPositionProperty().addListener((unused1) -> updateCaretPosition());

        //Directly taken from
        //https://stackoverflow.com/questions/22714268/how-to-limit-the-amount-of-characters-a-javafx-textfield/31161519
        commandTextField.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                                Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    // Check if the new character is greater than CHARACTER_LIMIT
                    if (commandTextField.getText().length() >= CHARACTER_LIMIT) {

                        // if it's 11th character then just setText to previous
                        // one
                        commandTextField.setText(commandTextField.getText().substring(0, CHARACTER_LIMIT));
                    }
                }
            }
        });

        historySnapshot = logic.getHistorySnapshot();

        registerAsAnEventHandler(this);
    }

    /**
     * Handles the key press event, {@code keyEvent}.
     */
    @FXML
    private void handleKeyPress(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
        case UP:
            // As up and down buttons will alter the position of the caret,
            // consuming it causes the caret's position to remain unchanged
            keyEvent.consume();

            navigateToPreviousInput();
            break;
        case DOWN:
            keyEvent.consume();
            navigateToNextInput();
            break;
        case TAB:
            keyEvent.consume();
            preventTabNavigation();
            suggestCommand();
            break;
        default:
            // let JavaFx handle the keypress
        }
    }

    /**
     * Updates the text field with the previous input in {@code historySnapshot},
     * if there exists a previous input in {@code historySnapshot}
     */
    private void navigateToPreviousInput() {
        assert historySnapshot != null;
        if (!historySnapshot.hasPrevious()) {
            return;
        }

        replaceText(historySnapshot.previous());
    }

    /**
     * Updates the text field with the next input in {@code historySnapshot},
     * if there exists a next input in {@code historySnapshot}
     */
    private void navigateToNextInput() {
        assert historySnapshot != null;
        if (!historySnapshot.hasNext()) {
            return;
        }

        replaceText(historySnapshot.next());
    }

    /**
     * Keeps track of any change in caret position while the commandTextField is focused.
     */
    private void updateCaretPosition() {
        if (!commandTextField.isFocused()) {
            return;
        }
        caretPosition = commandTextField.getCaretPosition();
    }

    /**
     * Negates the effect of tab navigation
     */
    private void preventTabNavigation() {
        commandTextField.requestFocus();
        commandTextField.positionCaret(caretPosition);
    }

    /**
     * Suggests commands with the text in the box as arguments. Does not behave like an actual command.
     */
    private void suggestCommand() {
        try {
            SuggestCommandParser parser = new SuggestCommandParser(new PrefixMatcher());
            SuggestCommand suggestCommand = parser.parse(commandTextField.getText().split(" ")[0]);
            CommandResult commandResult = suggestCommand.execute(null, null);
            if (!pendingText.isEmpty()) {
                replaceText(pendingText);
                pendingText = "";
            }
            raise(new NewResultAvailableEvent(commandResult.feedbackToUser));
        } catch (ParseException e) {
            logger.warning(e.getMessage());
        }
    }

    /**
     * Sets {@code CommandBox}'s text field with {@code text} and
     * positions the caret to the end of the {@code text}.
     */
    private void replaceText(String text) {
        commandTextField.setText(text);
        commandTextField.positionCaret(commandTextField.getText().length());
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        try {
            CommandResult commandResult = logic.execute(commandTextField.getText());
            initHistory();
            historySnapshot.next();
            // process result of the command
            replaceText(pendingText);
            pendingText = "";
            logger.info("Result: " + commandResult.feedbackToUser);
            raise(new NewResultAvailableEvent(commandResult.feedbackToUser));

        } catch (CommandException | ParseException e) {
            initHistory();
            // handle command failure
            setStyleToIndicateCommandFailure();
            logger.info("Invalid command: " + commandTextField.getText());
            raise(new NewResultAvailableEvent(e.getMessage()));
        }
    }

    /**
     * Initializes the history snapshot.
     */
    private void initHistory() {
        historySnapshot = logic.getHistorySnapshot();
        // add an empty string to represent the most-recent end of historySnapshot, to be shown to
        // the user if she tries to navigate past the most-recent end of the historySnapshot.
        historySnapshot.add("");
    }

    @Subscribe
    private void handleSuggestCommandEvent(SuggestCommandEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        if (event.commandWords.length == 1) {
            pendingText = event.commandWords[0];
        }
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

}
