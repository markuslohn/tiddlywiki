package de.bimalo.tiddlywiki;

import de.bimalo.common.Assert;
import java.rmi.server.UID;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/*
 * <p>
 * TiddlyWiki is made up of chunks of information called tiddlers. A <code>
 * Tiddler</code> does represent only one tiddler embedded in a TiddlyWiki.</p>
 * <p>
 * See http://tiddlywiki.com for more information about TiddlyWiki and tiddlers.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public class Tiddler implements Comparable<Tiddler> {

    /**
     * Name of the reserved Tiddler SiteTitle.
     */
    public final static String TITLE_TIDDLER_NAME = "SiteTitle";
    /**
     * Name of the reserved Tiddler SiteSubtitle.
     */
    public final static String SUBTITLE_TIDDLER_NAME = "SiteSubtitle";
    /**
     * Name of the reserved Tiddler MainMenu.
     */
    public final static String MAINMENU_TIDDLER_NAME = "MainMenu";
    /**
     * Name of the reserved Tiddler DefaultTiddlers.
     */
    public final static String DEFAULTTIDDLERS_TIDDLER_NAME = "DefaultTiddlers";

    /**
     * An array of Tiddler names reserved by the TiddlyWiki implementation.
     */
    private static String[] RESERVED_TIDDLER_NAMES = new String[]{
        TITLE_TIDDLER_NAME, SUBTITLE_TIDDLER_NAME, MAINMENU_TIDDLER_NAME, DEFAULTTIDDLERS_TIDDLER_NAME
    };
    /**
     * A unique identifier of this Tiddler.
     */
    private UID id = new UID();
    /**
     * The title of this Tiddler.
     */
    private String title = null;
    /**
     * The human readable name of user created this Tiddler.
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
     * The content of this Tiddler. It can contain simple text but also Wiki
     * syntax.
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
     * The hash code for this Tiddler.
     */
    int hashCode = 0;

    /**
     * Creates <code>Tiddler</code> with default values.
     */
    public Tiddler() {
        this.title = "New Tiddler.";
        initDefaultValues();
    }

    /**
     * Creates a <code>Tiddler</code> with a specified title.
     *
     * @param title the title
     * @exception IllegalArgumentException if title is null or empty
     */
    public Tiddler(String title) {
        Assert.notNull(title);
        this.title = title;
        initDefaultValues();
    }

    /**
     * Gets the Parent-Tiddler for this Tiddler.
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
     * Adds an existing Tiddler to the current one. The current Tiddler becomes
     * automatically the "Parent"-Tiddler. If the parameter tiddler is null or
     * it is already added nothing will be done.
     *
     * @param tiddler the Tiddler to add
     */
    public void addTiddler(Tiddler tiddler) {
        if (tiddler == null || tiddlers.contains(tiddler)) {
            return;
        }
        tiddlers.add(tiddler);
        if (!isReservedTiddler()) {
            tiddler.setParent(this);
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
     * Adds a tag to this Tiddler.
     *
     * @param tag the new tag
     * @exception IllegalArgumentException if this Tiddler is a "reserved"
     * Tiddler.
     */
    public void addTag(String tag) {
        Assert.isTrue(!isReservedTiddler(), "addTag can't be used for a reserved Tiddler.");
        if (tag == null || tag.isEmpty() || tags.contains(tag)) {
            return;
        }
        tags.add(tag);
    }

    /**
     * Adds a <code>java.util.List<code> of Strings with tags to this Tiddler.
     *
     * @param tags a list with tags
     * @exception IllegalArgumentException if this Tiddler is a "reserved"
     * Tiddler.
     */
    public void addTags(List<String> tags) {
        Assert.isTrue(!isReservedTiddler(), "addTag can't be used for a reserved Tiddler.");
        if (tags != null && !tags.isEmpty()) {
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
     * Sets a new title for this Tiddler.
     *
     * @param title a new title
     * @exception IllegalArgumentException if title is null
     */
    public void setTitle(String title) {
        Assert.notNull(title);
        Assert.isTrue(!isReservedTiddler(), "setTitle can't be used for a reserved Tiddler.");
        this.title = title;
        hashCode = 0;
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
        sb.append("Tiddler {");
        sb.append("title=").append(title).append(", ");
        sb.append("modifier=").append(modifier).append(", ");
        sb.append("created=").append(createDate);
        sb.append("}");
        return sb.toString();
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

    /**
     * Compares a given <code>Tiddler</code> with this Tiddler. It uses the
     * create date and the title of the Tiddler to make the comparison.
     *
     * @param o the Tiddler to compare
     * @return a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the
     */
    public int compareTo(Tiddler o) {
        Assert.notNull(o);

        String title2 = o.getTitle();
        return title.compareTo(title2);
    }
}
