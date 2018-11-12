package seedu.thanepark.storage.html;

import java.util.List;

import j2html.tags.ContainerTag;
import seedu.thanepark.model.logging.CommandEntry;

/**
 * Singleton Class for generating command history report formatted as a Html page.
 */
public class CommandReportGenerator extends HtmlGenerator<CommandEntry> {

    private static CommandReportGenerator instance;

    /**
     * Required for singleton.
     */
    private CommandReportGenerator() {
        ;
    }

    public static CommandReportGenerator getInstance() {
        if (instance == null) {
            instance = new CommandReportGenerator();
        }
        return instance;
    }

    /**
     * Returns a ContainerTag representation of the generated table with commandEntryList as entries.
     */
    @Override
    public ContainerTag generateBody(List<CommandEntry> data) {
        return generateHorizontalTable(data);
    }

}
