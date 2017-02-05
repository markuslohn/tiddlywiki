package de.bimalo.tiddlywiki.generator;

import de.bimalo.tiddlywiki.Tiddler;
import java.net.URI;

/**
 * <p>
 * A TiddlyWiki is made up of chunks of information called tiddlers. A <code>
 * Tiddler</code> does represent only one tiddler embedded in a TiddlyWiki.</p>
 * <p>
 * See http://tiddlywiki.com for more information about TiddlyWiki and
 * Tiddlers.</p>
 * <p>
 * This is a special Tiddler type for files/documents.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 */
final class DocumentTiddler extends Tiddler {

    /**
     * The name of the file.
     */
    private String name = null;

    /**
     * A URI to access the file.
     */
    private URI uri = null;

    /**
     * Creates <code>DocumentTiddler</code> with a default title.
     */
    DocumentTiddler() {
        super();
    }

    /**
     * Creates a <code>DocumentTiddler</code> with a specified title.
     *
     * @param title the title, when null a default title is used
     */
    DocumentTiddler(final String title) {
        super(title);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(final URI uri) {
        this.uri = uri;
    }
}
