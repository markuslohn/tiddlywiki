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
public class LocalizerTestBundle2 extends ListResourceBundle {

    static final Object[][] contents = {
        {"x1", "Value1"},
        {"x2", "Value2"},
        {"x3", "Value3"}
    };

    @Override
    protected Object[][] getContents() {
        return contents;
    }
}
