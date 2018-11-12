package seedu.thanepark.storage.html;

import static j2html.TagCreator.attrs;
import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.h1;
import static j2html.TagCreator.h3;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.link;
import static j2html.TagCreator.table;
import static j2html.TagCreator.tbody;
import static j2html.TagCreator.td;
import static j2html.TagCreator.th;
import static j2html.TagCreator.thead;
import static j2html.TagCreator.title;
import static j2html.TagCreator.tr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

import j2html.TagCreator;
import j2html.tags.ContainerTag;
import seedu.thanepark.commons.util.FilePathToUrl;
import seedu.thanepark.commons.util.FileUtil;
import seedu.thanepark.model.logging.HtmlFormattable;

/**
 * Abstract class that generates a Html page with a Time Generated timestamp.
 * Accepts a HtmlFormattable type argument.
 */
public abstract class HtmlGenerator<HtmlData extends HtmlFormattable> {
    private static final String MESSAGE_TIME_GENERATED = "report generated at: %1$s";
    private static final DateTimeFormatter formatter =
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .withLocale(Locale.UK)
            .withZone(ZoneId.systemDefault());

    /**
     * Generates a Html report with commandEntryList as entries as fileName.
     */
    public void generateHtml(String title, List<HtmlData> data, FilePathToUrl filePathToUrl)
        throws IOException {
        String content = generateHtmlLayout(title, data);
        createFile(filePathToUrl.getFilePath(), content);
        FileUtil.saveResource(getClass().getResourceAsStream("/docs/stylesheets/asciidoctor.css"),
                "asciidoctor.css", false);
    }

    /**
     * Returns a String representation of the generated Html report with commandEntryList as entries.
     */
    private String generateHtmlLayout(String title, List<HtmlData> data) {
        return html(
            head(
                title(title),
                link().withRel("stylesheet").withHref("asciidoctor.css")
            ),
            body(
                generateHeader(title),
                generateBody(data)
            )
        ).withStyle("padding: 10px 30px").renderFormatted();
    }

    /**
     * Return ContainerTag representation of page header.
     */
    public ContainerTag generateHeader(String title) {
        return TagCreator.main(attrs("#main.header"), h1(title),
            h3(String.format(MESSAGE_TIME_GENERATED, formatter.format(Instant.now()))));
    }

    /**
     * Return ContainerTag representation of page body.
     */
    public ContainerTag generateBody(List<HtmlData> data) {
        return div();
    }

    /**
     * Generates a horizontal table with HtmlFormattable data.
     */
    protected ContainerTag generateHorizontalTable(List<HtmlData> data) {
        if (data.isEmpty()) {
            return div();
        }
        ContainerTag headers = tr();
        for (String header : data.get(0).getFieldHeaders()) {
            headers.with(th(header));
        }
        ContainerTag rows = tbody();
        for (HtmlData rowData : data) {
            rows.with(generateTableRow(rowData));
        }
        return table(
            thead(headers),
            rows
        ).withStyle("width: 100%");
    }

    /**
     * Generates a table row with HtmlFormattable data.
     */
    private ContainerTag generateTableRow(HtmlData rowData) {
        ContainerTag row = tr();
        for (String entry : rowData.getFields()) {
            row.with(td(entry));
        }
        return row;
    }

    /**
     * Generates a vertical table with 2 columns using HtmlFormattable data.
     */
    protected ContainerTag generateVerticalTable(HtmlData data) {
        ContainerTag table = table().withStyle("width: 100%");
        List<String> template = data.getFieldHeaders();
        int totalFields = template.size();
        for (int row = 0; row < totalFields; row++) {
            ContainerTag htmlRow = tr();
            htmlRow.with(th(template.get(row)));
            htmlRow.with(td(data.getFields().get(row)).withId(template.get(row)));
            table.with(htmlRow);
        }
        return table;
    }

    /**
     * Creates the html file and writes the html content into it. Overwrites existing files.
     * Throws IOException if file cannot be written to.
     */
    private void createFile(String fileName, String content) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(fileName, false);
        fileWriter.append(content);
        fileWriter.close();
    }
}
