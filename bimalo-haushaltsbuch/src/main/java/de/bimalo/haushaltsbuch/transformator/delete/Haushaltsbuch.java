package de.bimalo.haushaltsbuch.transformator.delete;

import de.bimalo.haushaltsbuch.transformator.HaushaltsbuchRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * <p>
 * Represents the book of household accounts. It manages <code>HaushaltsbuchRecords</code>s.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 * @see HaushaltsbuchRecords
 */
public final class Haushaltsbuch extends ArrayList<HaushaltsbuchRecord> {

    /**
     * Creates a new <code>Haushaltsbuch</code> with default values.
     */
    public Haushaltsbuch() {
        super();
    }
    
    /**
     * Creates a new <code>Haushaltsbuch</code> with a given Collection of
     * HaushaltsbuchRecord's.
     * @param c the collection whose elements are to be placed into this list
     * @exception NullPointerException if the specified collection is null
     */
    public Haushaltsbuch(Collection<HaushaltsbuchRecord> c) {
        super(c);
    }
    
    /**
    * Creates a new <code>Haushaltsbuch</code> with the specified initial capacity.
     * @param initialCapacity the initial capacity of the list
     * @exception IllegalArgumentException if the specified initial capacity is negative
     */
    public Haushaltsbuch(int initialCapacity) {
        super(initialCapacity);
    }
   
}
