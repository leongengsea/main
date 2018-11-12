package seedu.thanepark.storage.html;

import static j2html.TagCreator.div;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.img;
import static j2html.TagCreator.p;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import j2html.tags.ContainerTag;
import seedu.thanepark.commons.util.FilePathToUrl;
import seedu.thanepark.commons.util.FileUtil;
import seedu.thanepark.model.ride.Ride;

/**
 * Singleton Class for generating a Html page used to display ride details.
 */
public class RidePageGenerator extends HtmlGenerator<Ride> {
    private static RidePageGenerator instance;

    /**
     * Required for singleton.
     */
    private RidePageGenerator() {
        ;
    }

    public static RidePageGenerator getInstance() {
        if (instance == null) {
            instance = new RidePageGenerator();
        }
        return instance;
    }

    /**
     * Generates a Html page to display ride information.
     */
    public void generateHtml(String title, Ride data, FilePathToUrl filePathToUrl)
        throws IOException {
        List<Ride> dataAsList = new LinkedList<>();
        dataAsList.add(data);
        generateHtml(title, dataAsList, filePathToUrl);
        FileUtil.saveResource(getClass().getResourceAsStream("/docs/images/ride.png"),
                "ride.png", false);
        FileUtil.saveResource(getClass().getResourceAsStream("/docs/stylesheets/asciidoctor.css"),
                "asciidoctor.css", false);
    }

    @Override
    public ContainerTag generateHeader(String title) {
        return div(
            div(
                p(h2().withId("name"))
            ).withStyle("height: auto; min-width: 200px; width: 50%; display: inline-block; left: 0px;"
                + "word-wrap: break-word"),
            div(
                img().attr("src=\"ride.png\" alt=\"ride\"").withStyle("height: auto; width: 100%")
            ).withStyle("min-width: 200px; height: auto; width: 40%; display: inline-block;"
                   + "right: 0px; vertical-align:top")
        );
    }

    @Override
    public ContainerTag generateBody(List<Ride> data) {
        return generateVerticalTable(data.get(0));
    }

}
