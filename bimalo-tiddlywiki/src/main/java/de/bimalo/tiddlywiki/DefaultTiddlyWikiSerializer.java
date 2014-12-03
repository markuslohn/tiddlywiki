package de.bimalo.tiddlywiki;

import de.bimalo.common.Assert;
import de.bimalo.common.IOUtilities;
import de.bimalo.common.Localizer;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Serializes a <code>TiddlyWiki</code> based on a template to a file.</p>
 * <p>
 * It uses the standard layout and wiki syntax needed by a TiddlyWiki file so
 * that it can be displayed correctly in a web browser.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 * @see Tiddler
 * @see TiddlyWiki
 */
public final class DefaultTiddlyWikiSerializer {

    /**
     * Logger instance.
     */
    private static Logger logger = LoggerFactory.getLogger(DefaultTiddlyWikiSerializer.class);

    /**
     * The Java representation of a TiddlyWiki with all its content.
     */
    private TiddlyWiki wiki = null;

    /**
     * reference to the TiddlyWiki template file
     */
    private FileObject templateFile = null;

    /**
     * The Localizer for this Tiddler.
     */
    Localizer localizer = new Localizer(Locale.getDefault());

    /**
     * Creates a new <code>TiddlyWikiSerializer</code>.
     *
     * @param wiki the TiddlyWiki to serialize
     * @param templateFile reference to the TiddlyWiki template file
     * @throws IOException if object couldn't be created
     */
    public DefaultTiddlyWikiSerializer(TiddlyWiki wiki, FileObject templateFile) throws IOException {
        Assert.notNull(wiki);

        Assert.notNull(templateFile);
        Assert.isTrue(templateFile.exists());
        if (!(templateFile.getType().equals(FileType.FILE))) {
            throw new FileNotFolderException(templateFile.getName().getPath());
        }

        this.wiki = wiki;
        this.templateFile = templateFile;
    }

    /**
     * Serializes a TiddlyWiki to a given
     * <code>java.io.ObjectOutputStream</code>. The ObjectOutputStream has to be
     * already prepared. Furthermore the caller of this function has to manage
     * the resources needed by the ObjectOutputStream. This means for example
     * the caller has to call the method <code>close</code> on the
     * ObjectOutputStream.
     *
     * @param out a already configured <code>ObjectOutputStream</code>.
     * @exception IOException if TiddlyWiki couldn't be serialized for some
     * reason
     * @exception IllegalArgumentException if out is null
     */
    public void writeObject(OutputStream out) throws IOException {
        Assert.notNull(out);

        InputStreamReader isr = null;
        LineNumberReader lnr = null;

        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

            isr = new InputStreamReader(templateFile.getContent().getInputStream(), "UTF-8");
            lnr = new LineNumberReader(isr);
            String line = "";

            while ((line = lnr.readLine()) != null) {
                if (line.contains("<div id=\"storeArea\">")) {
                    writer.append("<div id=\"storeArea\">");
                    logger.info("Writes wiki content...");
                    writeTiddlers(writer);
                    writeLineFeed(writer);
                    writeTitle(writer);
                    writeLineFeed(writer);
                    writeSubtitle(writer);
                    writeLineFeed(writer);
                    writeMainMenu(writer);
                    writeLineFeed(writer);
                    writeDefaultTiddlers(writer);
                    writeLineFeed(writer);
                    writeLineFeed(writer);
                    logger.info("Done.");
                } else {
                    logger.info("Writes line {}...", line);
                    writer.append(line);
                    writeLineFeed(writer);
                    logger.info("Done.");
                }

            }
            writer.flush();
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IOException(ex);
        } finally {
            IOUtilities.closeReader(lnr);
            IOUtilities.closeReader(isr);
        }
    }

    /**
     * Serializes all available Tiddler objects of a TiddlyWiki to a
     * java.io.Writer object.
     *
     * @param writer an already prepared java.io.Writer
     * @throws IOException if serialization of Tiddler's failed for some reason
     */
    private void writeTiddlers(Writer writer) throws IOException {
        for (Tiddler tiddler : wiki.listTiddlers()) {
            writeTiddler(writer, tiddler);
            writeLineFeed(writer);
        }
    }

    /**
     * Serializes the site title of the TiddlyWiki to the java.io.Writer.
     *
     * @param writer the already prepared java.io.Writer
     * @throws IOException if serialization failed
     */
    private void writeTitle(Writer writer) throws IOException {
        Tiddler titleTiddler = wiki.getTitleTiddler();
        writeTiddlerStartElement(writer, titleTiddler);
        writer.append(StringEscapeUtils.escapeHtml(titleTiddler.getText()));
        writeTiddlerEndElement(writer);

    }

    /**
     * Serializes the site sub-title of the TiddlyWiki to the java.io.Writer.
     *
     * @param writer the already prepared java.io.Writer
     * @throws IOException if serialization failed
     */
    private void writeSubtitle(Writer writer) throws IOException {
        Tiddler subtitleTiddler = wiki.getSubtitleTiddler();
        writeTiddlerStartElement(writer, subtitleTiddler);
        writer.append(StringEscapeUtils.escapeHtml(subtitleTiddler.getText()));
        writeTiddlerEndElement(writer);
    }

    /**
     * Serializes the main menu of the TiddlyWiki to the java.io.Writer.
     *
     * @param writer the already prepared java.io.Writer
     * @throws IOException if serialization failed
     */
    private void writeMainMenu(Writer writer) throws IOException {
        Tiddler mainMenu = wiki.getMainMenuTiddler();
        writeTiddlerStartElement(writer, mainMenu);
        for (Tiddler tiddler : mainMenu.listTiddlers()) {
            writeTiddlerReference(writer, tiddler);
            writeLineFeed(writer);
        }
        writeTiddlerEndElement(writer);
    }

    /**
     * Serializes default tiddlers of the TiddlyWiki to the java.io.Writer.
     *
     * @param writer the already prepared java.io.Writer
     * @throws IOException if serialization failed
     */
    private void writeDefaultTiddlers(Writer writer) throws IOException {
        Tiddler defaultTiddler = wiki.getDefaultTiddler();
        writeTiddlerStartElement(writer, defaultTiddler);

        for (Tiddler tiddler : defaultTiddler.listTiddlers()) {
            writeTiddlerReference(writer, tiddler);
            writeLineFeed(writer);
        }

        writeTiddlerEndElement(writer);
    }

    /**
     * Appends a line feed to given java.io.Writer.
     *
     * @param writer the already prepared java.io.Writer
     * @throws IOException if operation failed
     */
    private void writeLineFeed(Writer writer) throws IOException {
        writer.append("\n");
    }

    /**
     * Serializes a given Tiddler to a java.io.Writer object.
     *
     * @param writer the already prepared java.io.Writer.
     * @param tiddler the Tiddler to serialize
     * @throws IOException if operation failed
     */
    private void writeTiddler(Writer writer, Tiddler tiddler) throws IOException {
        writeTiddlerStartElement(writer, tiddler);

        if (tiddler.getParent() != null) {
            writeFolderTiddlerReference(writer, tiddler.getParent());
            writeLineFeed(writer);
        }

        if (tiddler.getText() != null && !tiddler.getText().isEmpty()) {
            writer.append(StringEscapeUtils.escapeHtml(tiddler.getText()));
        }

        if (!tiddler.listTiddlers().isEmpty()) {
            writeLineFeed(writer);
            writer.append("__Referenzen__");
            writeLineFeed(writer);
            writer.append("|!Typ|!Titel|!Author|!Datum|");
            writeLineFeed(writer);
            for (Tiddler refTiddler : tiddler.listTiddlers()) {
                writeTiddlerReferenceAsTable(writer, refTiddler);
                writeLineFeed(writer);
            }
        }

        writeTiddlerEndElement(writer);

        if (!tiddler.listTiddlers().isEmpty()) {
            for (Tiddler refTiddler : tiddler.listTiddlers()) {
                writeTiddler(writer, refTiddler);
            }
        }
    }

    /**
     * <p>
     * Serializes a reference for the given Tiddler to a java.io.Writer. The
     * syntax is as follows:</p>
     * <p>
     * <
     * pre>
     * [[Tiddler Title]]
     * </pre></p>
     *
     * @param writer the already prepared java.io.Writer
     * @param tiddler the "referenced" Tiddler
     * @throws IOException if operation failed
     */
    private void writeTiddlerReference(Writer writer, Tiddler tiddler) throws IOException {
        writeTiddlerReference(writer, tiddler, null);
    }

    /**
     * <p>
     * Serializes a reference for the given Tiddler to a java.io.Writer. The
     * syntax is as follows:</p>
     * <p>
     * <
     * pre>
     * [[title|Tiddler Title]]
     * </pre></p>
     *
     * @param writer the already prepared java.io.Writer
     * @param tiddler the "referenced" Tiddler
     * @param title a custom title for the reference
     * @throws IOException if operation failed
     */
    private void writeTiddlerReference(Writer writer, Tiddler tiddler, String title) throws IOException {
        writer.append("[[");
        if (title != null) {
            writer.append(title);
            writer.append("|");
        }
        writer.append(StringEscapeUtils.escapeHtml(tiddler.getTitle()));
        writer.append("]]");
    }

    /**
     * <p>
     * Serializes a reference for the given Tiddler to a java.io.Writer as a
     * table.</p>
     *
     * @param writer the already prepared java.io.Writer
     * @param tiddler the "referenced" Tiddler
     * @param title a custom title for the reference
     * @throws IOException if operation failed
     */
    private void writeTiddlerReferenceAsTable(Writer writer, Tiddler tiddler) throws IOException {
        //Typ Column
        writer.append("|");
        //Titel Column
        writer.append("|");
        writer.append("[[");
        writer.append(StringEscapeUtils.escapeHtml(tiddler.getTitle()));
        writer.append("]]");
        //Author Column
        writer.append("|");
        writer.append(tiddler.getCreator());
        //Date Column
        writer.append("|");
        writer.append(localizer.formatDateObject(tiddler.getCreateDate(), "dd.MM.yyyy hh:mm:ss"));
        writer.append("|");
    }

    /**
     * <p>
     * Serializes a reference for the given Tiddler to a java.io.Writer. The
     * syntax is as follows:</p>
     * <p>
     * <
     * pre>
     * [[Tiddler Title|../Tiddler Title]]
     * </pre></p>
     *
     * @param writer the Writer
     * @param tiddler the Tiddler
     * @throws IOException if operation failed
     */
    private void writeFolderTiddlerReference(Writer writer, Tiddler tiddler) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("../").append(StringEscapeUtils.escapeHtml(tiddler.getTitle()));
        writeTiddlerReference(writer, tiddler, sb.toString());
    }

    /**
     * Serializes a Tiddler start element to a given java.io.Writer.
     *
     * @param writer the Writer
     * @param tiddler the Tiddler
     * @throws IOException if operation failed
     */
    private void writeTiddlerStartElement(Writer writer, Tiddler tiddler) throws IOException {
        writer.append("<div ");
        writer.append("title=\"").append(StringEscapeUtils.escapeHtml(tiddler.getTitle())).append("\" ");
        writer.append("creator=\"").append(tiddler.getCreator()).append("\" ");
        writer.append("modifier=\"").append(tiddler.getModifier()).append("\" ");
        writer.append("created=\"").append(localizer.formatDateObject(tiddler.getCreateDate(), "yyyyMMddHHmm")).append("\" ");
        writer.append("tags=\"").append(convertList2String(tiddler.getTags())).append("\" ");
        writer.append("changecount=\"1\"");
        writer.append(">");
        writeLineFeed(writer);
        writer.append("<pre>");
    }

    /**
     * Serializes a Tiddler end element to a given java.io.Writer.
     *
     * @param writer the Writer
     * @throws IOException if operation failed
     */
    private void writeTiddlerEndElement(Writer writer) throws IOException {
        writer.append("</pre>");
        writeLineFeed(writer);
        writer.append("</div>");
        writeLineFeed(writer);
    }

    private String convertList2String(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String string : list) {
            if (string.contains(" ")) {
                sb.append(addBrackets2String(string));
            } else {
                sb.append(string);
            }
            sb.append(" ");
        }
        return sb.toString();
    }

    private String addBrackets2String(String string) {
        StringBuilder sb = new StringBuilder();
        if (!string.startsWith("[[")) {
            sb.append("[[");
        }
        sb.append(string);
        if (!string.endsWith("]]")) {
            sb.append("]]");
        }
        return sb.toString();
    }
}
