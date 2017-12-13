package de.bimalo.tiddlywiki;

import de.bimalo.tiddlywiki.common.Assert;
import java.net.URI;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * TiddlyWiki is made up of chunks of information called Tiddler's. A <code>
 * Tiddler</code> represents only one Tiddler embedded in a TiddlyWiki.</p>
 * <p>
 * See http://tiddlywiki.com for more information about TiddlyWiki and
 * tiddlers.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @see TiddlyWiki
 */
public class Tiddler implements Comparable<Tiddler> {

    /**
     * Name of the reserved Tiddler SiteTitle.
     */
    public static final String TITLE_TIDDLER_NAME = "SiteTitle";
    /**
     * Name of the reserved Tiddler SiteSubtitle.
     */
    public static final String SUBTITLE_TIDDLER_NAME = "SiteSubtitle";
    /**
     * Name of the reserved Tiddler MainMenu.
     */
    public static final String MAINMENU_TIDDLER_NAME = "MainMenu";
    /**
     * Name of the reserved Tiddler DefaultTiddlers.
     */
    public static final String DEFAULTTIDDLERS_TIDDLER_NAME = "DefaultTiddlers";

    /**
     * An array of Tiddler names reserved by the TiddlyWiki implementation.
     */
    private static final String[] RESERVED_TIDDLER_NAMES = new String[]{
        TITLE_TIDDLER_NAME, SUBTITLE_TIDDLER_NAME, MAINMENU_TIDDLER_NAME, DEFAULTTIDDLERS_TIDDLER_NAME
    };
    /**
     * A unique identifier for this Tiddler.
     */
    private UID id = new UID();
    /**
     * The Tiddler's title.
     */
    private String title = null;
    /**
     * The human readable name of the user created this Tiddler.
     */
    private String creator = null;
    /**
     * The human readable name of the user last modified this Tiddler.
     */
    private String modifier = null;
    /**
     * Date when this Tiddler was created.
     */
    private Date createDate = null;

    /**
     * Date when this Tiddler was modified.
     */
    private Date lastModifyDate = null;

    /**
     * The content of this Tiddler. It can contain simple text or text following
     * wiki syntax or markup language.
     */
    private String text = null;
    /**
     * A List containing Strings representing keywords to classify this Tiddler.
     */
    private List<String> tags = new ArrayList<String>();

    /**
     * Reference to the "parent" Tiddler where this Tiddler belongs to. This
     * attribute enables to build a hierarchy of Tiddler's. It is optional!
     */
    private Tiddler parent = null;
    /**
     * A List of references to "Sub"-Tiddler objects.
     */
    private List<Tiddler> tiddlers = new ArrayList<Tiddler>();

    /**
     * The content type of this Tiddler, like application/pdf.
     */
    private String contentType = null;
    /**
     * The absolute path of this Tiddler.
     */
    private String path = null;

    /**
     * The hash code for this Tiddler.
     */
    private int hashCode = 0;

    /**
     * When it is a defined as default it will be opened automatically when the
     * TiddlyWiki is started.
     */
    private boolean isDefault = false;

    /**
     * Creates a new <code>Tiddler</code> with default values.
     */
    public Tiddler() {
        title = "New Tiddler";
        initDefaultValues();
    }

    /**
     * Creates a new <code>Tiddler</code> with a specified title.
     *
     * @param title the title, if null a default value will be used instead
     */
    public Tiddler(String title) {
        if (title == null || title.isEmpty()) {
            this.title = "New Tiddler";
        } else {
            this.title = title;
        }
        initDefaultValues();
    }

    /**
     * Gets the Parent-Tiddler.
     *
     * @return the Parent-Tiddler or null if this Tiddler does not have a
     * Parent-Tiddler.
     */
    public Tiddler getParent() {
        return parent;
    }

    /**
     * Gets the title for this Tiddler.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns a unique identifier for this Tiddler.
     *
     * @return a unique title
     */
    public String getUniqueTitle() {
        StringBuilder sb = new StringBuilder();
        if (parent != null) {
            sb.append(parent.getTitle());
            sb.append(" - ");
        }
        sb.append(title);
        return sb.toString();
    }

    /**
     * Adds a Tiddler to this Tiddler. This Tiddler becomes automatically the
     * Parent-Tiddler of the provided Tiddler.
     *
     * @param tiddler a Tiddler to add, if null nothing will be modified
     */
    public void addTiddler(Tiddler tiddler) {
        if (tiddler != null && !tiddlers.contains(tiddler)) {
            tiddlers.add(tiddler);
            if (!isReservedTiddler()) {
                tiddler.setParent(this);
            }
        }
    }

    /**
     * Gets the <code>java.util.Date</code> when this Tiddler was created.
     *
     * @return the date of creation
     */
    public Date getCreateDate() {
        return new Date(createDate.getTime());
    }

    /**
     * Gets the <code>java.util.Date</code> when this Tiddler was modified.
     *
     * @return the date of last modification
     */
    public Date getLastModifyDate() {
        return new Date(lastModifyDate.getTime());
    }

    /**
     * Gets the human readable name of the creator of this Tiddler.
     *
     * @return the name of the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Gets the unique identifier for this Tiddler.
     *
     * @return the unique identifier
     */
    public UID getId() {
        return id;
    }

    /**
     * Gets the human readable name of the last modifier of this Tiddler.
     *
     * @return the name of the last modifier.
     */
    public String getModifier() {
        return modifier;
    }

    /**
     * Gets an unmodifiable <code>java.util.List</code> of String representing
     * keywords/tags to describe/categorize this Tiddler.
     *
     * @return <code>java.util.List</code> of String with tags
     */
    public List<String> getTags() {
        return Collections.unmodifiableList(tags);
    }

    /**
     * Gets the text of this Tiddler.
     *
     * @return the text of this Tiddler.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns the absolute path to this Tiddler.
     *
     * @return the absolute path
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the absolute URI of this Tiddler.
     *
     * @return the absolute URI of this Tiddler.
     */
    public String getURI() {
        String uri = null;
        if (path != null) {
            URI uriobj = URI.create(path);
            uri = uriobj.toString();
        }
        return uri;
    }

    /**
     * Returns the content type of this Tiddler.
     *
     * @return the content type.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets a new content type for this Tiddler.
     *
     * @param contentType the new content type, like application/pdf.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Sets the absolute path for this Tiddler.
     *
     * @param uri the new absolute path for this Tiddler. NULL as value is
     * ignored.
     */
    public void setPath(final String path) {
        if (path != null) {
            this.path = path;
        }
    }

    /**
     * Adds a tag to this Tiddler. This function cannot be applied to a
     * "reserved" Tiddler.
     *
     * @param tag the new tag
     * @exception IllegalArgumentException if this Tiddler is a "reserved"
     * Tiddler.
     */
    public void addTag(String tag) {
        Assert.isTrue(!isReservedTiddler(), "addTag can't be used for a reserved Tiddler.");
        if (tag != null && !tag.isEmpty()) {
            tags.add(tag);
        }
    }

    /**
     * Adds a <code>java.util.List</code> of Strings with tags to this Tiddler.
     * This function cannot be applied to a "reserved" Tiddler.
     *
     * @param tags a list with tags
     * @exception IllegalArgumentException if this Tiddler is a "reserved"
     * Tiddler.
     */
    public void addTags(List<String> tags) {
        Assert.isTrue(!isReservedTiddler(), "addTag can't be used for a reserved Tiddler.");
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Sets the date when this Tiddler was created.
     *
     * @param createDate the create date
     */
    public void setCreateDate(Date createDate) {
        if (createDate != null) {
            this.createDate = new Date(createDate.getTime());
            this.hashCode = 0;
        }
    }

    /**
     * Sets the date when this Tiddler was modified.
     *
     * @param lastModifyDate the modification date
     */
    public void setLastModifyDate(Date lastModifyDate) {
        if (lastModifyDate != null) {
            this.lastModifyDate = new Date(lastModifyDate.getTime());
            this.hashCode = 0;
        }
    }

    /**
     * Sets the human readable name for the creator of this Tiddler.
     *
     * @param creator the name of the creator
     */
    public void setCreator(String creator) {
        if (creator != null) {
            this.creator = creator;
            this.hashCode = 0;
        }
    }

    /**
     * Sets the human readable name of the last modifier of this Tiddler.
     *
     * @param modifier the last modifier
     */
    public void setModifier(String modifier) {
        if (modifier != null) {
            this.modifier = modifier;
            this.hashCode = 0;
        }

    }

    /**
     * Is this "Default-Tiddler" or not? A Default-Tiddler will be automatically
     * opened and displayed on the start page of the TiddlyWiki.
     *
     * @return true = Default-Tiddler otherwise not
     */
    public boolean isDefault() {
        return isDefault;
    }

    /**
     * Defines this Tiddler as Default-Tiddler.
     */
    public void defineDefault() {
        isDefault = true;
    }

    /**
     * After executing this function this Tiddler is not a Default-Tiddler.
     */
    public void undefineDefault() {
        isDefault = false;
    }

    /**
     * Sets a new text for this Tiddler. When this Tiddler already have a text
     * it will be completely overwritten with the new text.
     *
     * @param text the new text for this Tiddler, it can also be null
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Appends the given text to the already existing text of this Tiddler. If
     * text is null nothing will be done.
     *
     * @param text the text to append
     */
    public void appendText(String text) {
        if (text != null) {
            StringBuilder sb = new StringBuilder();
            if (this.text != null) {
                sb.append(this.text);
            }
            sb.append(text);
            this.text = sb.toString();
        }
    }

    /**
     * Sets a new title for this Tiddler. Cannot be applied to a "reserved"
     * Tiddler. If title is null nothing will be modified.
     *
     * @param title the new title
     * @exception IllegalArgumentException if this is a "reserved" Tiddler.
     */
    public void setTitle(String title) {
        Assert.isTrue(!isReservedTiddler(), "setTitle can't be used for a reserved Tiddler.");
        if (title != null && !title.isEmpty()) {
            this.title = title;
            this.hashCode = 0;
        }
    }

    /**
     * Gets an unmodifiable and sorted <code>java.util.List</code> of Tiddler's
     * that have this Tiddler as parent.
     *
     * @return a <code>java.util.List</code> of Tiddler's
     */
    public List<Tiddler> listTiddlers() {
        Collections.sort(tiddlers);
        return Collections.unmodifiableList(tiddlers);
    }

    public List<Tiddler> getTiddlers() {
        return listTiddlers();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = true;
        if (obj == null || getClass() != obj.getClass()) {
            result = false;
        } else {
            final Tiddler other = (Tiddler) obj;
            if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
                result = false;
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = 3;
            hashCode = 29 * hashCode + (this.id != null ? this.id.hashCode() : 0);
        }
        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tiddler [");
        sb.append("title=").append(title).append(", ");
        sb.append("modifier=").append(modifier).append(", ");
        sb.append("created=").append(createDate);
        sb.append("]");
        return sb.toString();
    }

    /**
     * Compares a given <code>Tiddler</code> with this Tiddler. It uses the
     * create date and the title of the Tiddler to make the comparison.
     *
     * @param o the Tiddler to compare
     * @return a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the
     */
    @Override
    public int compareTo(Tiddler o) {
        Assert.notNull(o);

        String title2 = o.getTitle();
        return title.compareTo(title2);
    }

    /**
     * Creates special Tiddler for the TiddlyWiki title.
     *
     * @return a new Tiddler for the TiddlyWiki title
     */
    public static Tiddler createTitleTiddler() {
        Tiddler tiddler = new Tiddler(TITLE_TIDDLER_NAME);
        tiddler.setText("");
        return tiddler;
    }

    /**
     * Creates a special Tiddler for the TiddlyWiki subtitle.
     *
     * @return a new Tiddler for the TiddlyWiki subtitle.
     */
    public static Tiddler createSubTitleTiddler() {
        Tiddler tiddler = new Tiddler(SUBTITLE_TIDDLER_NAME);
        tiddler.setText("");
        return tiddler;
    }

    /**
     * Creates a special Tiddler for the TiddlyWiki main menu.
     *
     * @return a new Tiddler for the TiddlyWiki main menu.
     */
    public static Tiddler createMainMenuTiddler() {
        Tiddler tiddler = new Tiddler(MAINMENU_TIDDLER_NAME);
        tiddler.setText("");
        return tiddler;
    }

    /**
     * Creates a special Tiddler for the TiddlyWiki default tiddlers.
     *
     * @return a new Tiddler for the TiddlyWiki default tiddlers.
     */
    public static Tiddler createDefaultTiddler() {
        Tiddler tiddler = new Tiddler(DEFAULTTIDDLERS_TIDDLER_NAME);
        tiddler.setText("");
        return tiddler;
    }

    private void setParent(Tiddler tiddler) {
        Assert.isTrue(!isReservedTiddler(), "setParent can't be used for a reserved Tiddler.");
        parent = tiddler;
    }

    /**
     * Initialize this Tiddler with default values.
     */
    private void initDefaultValues() {
        creator = System.getProperty("user.name");
        modifier = System.getProperty("user.name");
        text = "Type the text for 'New Tiddler'";
        createDate = new Date();
        lastModifyDate = new Date();
    }

    /**
     * Checks whether this Tiddler object is a "reserved" Tiddler or not.
     * Reserved Tiddler is defined by a specific title expected by the
     * TiddlyWiki.
     *
     * @return true if this Tiddler is a reserved on otherwise false
     */
    private boolean isReservedTiddler() {
        boolean reserved = false;

        for (int i = 0; i < RESERVED_TIDDLER_NAMES.length; i++) {
            if (RESERVED_TIDDLER_NAMES[i].equalsIgnoreCase(title)) {
                reserved = true;
                break;
            }
        }
        return reserved;
    }

}
