/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bimalo.common.resources;

import java.util.ListResourceBundle;

/**
 * A simple ResourceBundle used in the unit test
 * <code>de.bimalo.common.LocalizerTest</code>.
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public class LocalizerTestBundle2_de extends ListResourceBundle {

    static final Object[][] contents = {
        {"x1", "Wert1"},
        {"x2", "Wert2"},
        {"x3", "Wert3"}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
