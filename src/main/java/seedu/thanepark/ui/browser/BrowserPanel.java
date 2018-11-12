package seedu.thanepark.ui.browser;

import java.io.IOException;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import seedu.thanepark.commons.core.LogsCenter;
import seedu.thanepark.commons.events.ui.RidePanelSelectionChangedEvent;
import seedu.thanepark.commons.util.FilePathToUrl;
import seedu.thanepark.model.ride.Ride;
import seedu.thanepark.storage.html.RidePageGenerator;

/**
 * The Browser Panel of the App.
 */
public class BrowserPanel extends BrowserRelatedUiPart<Region> {

    public static final FilePathToUrl RIDE_PAGE_PATH =
        new FilePathToUrl("ride.html", true);
    private static final String RIDE_PAGE_TITLE = "Ride information";
    private static final String TEXT_REPLACEMENT_JAVASCRIPT =
        "function updateRide(listOfFields) {"
        + "   for (index in listOfFields) {"
        + "       document.getElementById(listOfFields[index][0]).innerHTML = listOfFields[index][1];"
        + "   }"
        + "}";

    private static final String FXML = "BrowserPanel.fxml";

    private final Logger logger = LogsCenter.getLogger(getClass());

    @FXML
    private WebView browser;

    public BrowserPanel() {
        super(FXML);

        // To prevent triggering events for typing inside the loaded Web page.
        getRoot().setOnKeyPressed(Event::consume);
        registerAsAnEventHandler(this);
        try {
            RidePageGenerator.getInstance().generateHtml(RIDE_PAGE_TITLE, Ride.RIDE_TEMPLATE, RIDE_PAGE_PATH);
            loadDefaultPage();
        } catch (IOException ie) {
            ie.printStackTrace();
            logger.warning("Unable to create ride page.");
        }
    }

    @Override
    protected WebView getWebView() {
        return browser;
    }

    /**
     * Loads the ride information page using Javascript and BrowserRelatedUiPart.loadPage
     */
    private void loadRidePage(Ride ride) {
        int totalFields = ride.getFieldHeaders().size();
        StringBuilder parameters = new StringBuilder();
        parameters.append(String.format("[\"%1s\", \"%2s\"], ", "name", ride.getName().fullName));
        for (int i = 0; i < totalFields; i++) {
            parameters.append(String.format("[\"%1s\", \"%2s\"], ",
                ride.getFieldHeaders().get(i), ride.getFields().get(i)));
        }
        queuedJavascriptCommands.add(TEXT_REPLACEMENT_JAVASCRIPT);
        queuedJavascriptCommands.add(String.format("updateRide([%1s])", parameters.toString()));

        loadPage(RIDE_PAGE_PATH);
    }

    /**
     * Loads the short Help file as the default HTML file.
     */
    public void loadDefaultPage() {
        loadPage(HelpWindow.SHORT_HELP_FILE_PATH);
    }

    @Subscribe
    private void handleRidePanelSelectionChangedEvent(RidePanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadRidePage(event.getNewSelection());
    }

    /**
     * Frees resources allocated to the browser.
     */
    public void freeResources() {
        browser = null;
    }
}
