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
public class LocalizerTestBundle1 extends ListResourceBundle {

    static final Object[][] contents = {
        {"s1", "Value1"},
        {"s2", "Value2"},
        {"s3", "Value3"},
        {"s4", "Test with {0}, {1} values."},};

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
