/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bimalo.tiddlywiki.common.resources;

import java.util.ListResourceBundle;

/**
 * A simple ResourceBundle used in the unit test
 * <code>de.bimalo.common.LocalizerTest</code>.
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public class LocalizerTestBundle1_de extends ListResourceBundle {

    static final Object[][] contents = {
        {"s1", "Wert1"},
        {"s2", "Wert2"},
        {"s3", "Wert3"},
        {"s4", "Test mit {0}, {1} Werten."},};

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
