package de.bimalo.tiddlywiki.common;

/**
 * A marker interface indicating that a object is enabled to provide a XML representation of its
 * data.
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @since 1.0
 */
public interface XMLPrintable {

  /**
   * Gets the XML representation of the implementing object.
   *
   * @return XML data as simple String
   */
  String toXML();
}
