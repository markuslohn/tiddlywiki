package de.bimalo.tiddlywiki.generator;

import de.bimalo.tiddlywiki.Tiddler;

/**
 * <p>
 * A TiddlyWiki is made up of chunks of information called tiddlers. A <code>
 * Tiddler</code> does represent only one tiddler embedded in a TiddlyWiki.</p>
 * <p>
 * See http://tiddlywiki.com for more information about TiddlyWiki and Tiddlers.</p>
 * <p>
 * This special Tiddler type for folders.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version 1.0
 * @since 1.7
 */
public final class DirectoryTiddler extends Tiddler {

    /**
     * The name of the file.
     */
    private String name = null;

    /**
     * Creates <code>FolderTiddler</code> with a default title.
     */
    public DirectoryTiddler() {
        super();
    }

    /**
     * Creates a <code>FolderTiddler</code> with a specified title.
     *
     * @param title the title, when null a default title is used
     */
    public DirectoryTiddler(final String title) {
        super(title);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
