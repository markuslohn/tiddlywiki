package de.bimalo.tiddlywiki;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * <p>
 * A TiddlyWiki, a reusable non-linear personal web notebook. It's a unique wiki 
 * that people love using to keep ideas and information organised.</p>
 * <p>
 * TiddlyWiki is made up of chunks of information called tiddlers. A <code>
 * Tiddler</code> does represent only one tiddler embedded in a TiddlyWiki.</p>
 * <p>
 * See http://tiddlywiki.com for more information about TiddlyWiki and tiddlers.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 * @see Tiddler
 */
public final class TiddlyWiki {

    /**
     * A Tiddler representing the title of this TiddlyWiki.
     */
    private Tiddler siteTitle;
    /**
     * A Tiddler representing the sub-title of this TiddlyWiki.
     */
    private Tiddler siteSubtitle;
    /**
     * A Tiddler representing the main menu of this TiddlyWiki.
     */
    private Tiddler mainMenu;
    /**
     * A Tiddler containing references to other Tiddler's that have to be
     * automatically opened when the TiddlyWiki will be opened in a web browser.
     */
    private Tiddler defaultTiddlers;
    /**
     * All Tiddler's containing the data of this TiddlyWiki
     */
    private List<Tiddler> tiddlers = new ArrayList<Tiddler>();

    /**
     * Creates a new <code>TiddlyWiki</code> with default values.
     */
    public TiddlyWiki() {
        siteTitle = Tiddler.createTitleTiddler();
        siteSubtitle = Tiddler.createSubTitleTiddler();
        mainMenu = Tiddler.createMainMenuTiddler();
        defaultTiddlers = Tiddler.createDefaultTiddler();
    }

    /**
     * Sets a title for this TiddlyWiki.
     *
     * @param title the title
     */
    public void setTitle(String title) {
        if (title != null && !title.isEmpty()) {
            siteTitle.setText(title);
        }
    }

    /**
     * Sets a sub-title for this TiddlyWiki.
     *
     * @param subTitle the sub-title
     * @throws IllegalArgumentException if subTitle is null or empty
     */
    public void setSubtitle(String subTitle) {
        if (subTitle != null && !subTitle.isEmpty()) {
            siteSubtitle.setText(subTitle);
        }
    }

    /**
     * Adds an already prepared Tiddler to this TiddlyWiki.
     *
     * @param tiddler a Tiddler to add to this TiddlyWiki
     */
    public void addTiddler(Tiddler tiddler) {
        if (tiddler != null) {
            tiddlers.add(tiddler);
        }
    }

    /**
     * Adds an already prepared Tiddler to the main menu.
     *
     * @param tiddler a Tiddler for the main menu
     */
    public void addMainMenuTiddler(Tiddler tiddler) {
        if (tiddler != null) {
            mainMenu.addTiddler(tiddler);
        }
    }

    /**
     * Adds an already prepared Tiddler as default tiddler.
     *
     * @param tiddler a Tiddler for the main menu
     */
    public void addDefaultTiddler(Tiddler tiddler) {
        if (tiddler != null) {
            defaultTiddlers.addTiddler(tiddler);
        }
    }

    /**
     * Gets the title for this TiddlyWiki.
     *
     * @return the title
     */
    public String getTitle() {
        return siteTitle.getText();
    }

    /**
     * Gets the sub-title for this TiddlyWiki
     *
     * @return the sub-title
     */
    public String getSubTitle() {
        return siteSubtitle.getText();
    }

    public Tiddler getTitleTiddler() {
        return siteTitle;
        //TODO kopie erstellen
    }

    public Tiddler getSubtitleTiddler() {
        return siteSubtitle;
        //TODO kopie erstellen
    }

    public Tiddler getMainMenuTiddler() {
        return mainMenu;
        //TODO kopie erstellen
    }

    public Tiddler getDefaultTiddler() {
        return defaultTiddlers;
        //TODO kopie erstellen
    }

    /**
     * Gets all <code>Tiddler</code>'s as an unmodifiable
     * <code>java.util.List</code> that will be placed in the main menu of this
     * TiddlyWiki.
     *
     * @return an unmodifiable list of Tiddler's.
     *
     * public List<Tiddler> listMainMenuTiddlers() { return
     * Collections.unmodifiableList(mainMenu.listTiddlers()); }
     *
     */
    /**
     * Gets all <code>Tiddler</code>'s as an unmodifiable
     * <code>java.util.List</code> that will be automatically opened at startup
     * of this TiddlyWiki.
     *
     * @return an unmodifiable list of Tiddler's.
     *
     * public List<Tiddler> listDefaultTiddlers() { return
     * Collections.unmodifiableList(defaultTiddlers.listTiddlers()); }
     *
     */
    /**
     * Gets an unmodifiable <code>java.util.List</code> of
     * <code>Tiddler</code>'s that belongs to this TiddlyWiki.
     *
     * @return an unmodifiable list of Tiddler's.
     */
    public List<Tiddler> listTiddlers() {
        return Collections.unmodifiableList(tiddlers);
    }

}
