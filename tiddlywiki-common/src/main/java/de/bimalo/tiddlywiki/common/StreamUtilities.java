package de.bimalo.tiddlywiki.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It provides helper functions when dealing with interfaces/classes from java.io.
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @since 1.0
 */
public abstract class StreamUtilities {

  /**
   * Logger for this class.
   */
  static final Logger LOGGER = LoggerFactory.getLogger(StreamUtilities.class);

  /**
   * Private constructor to avoid creating instances of this class.
   */
  private StreamUtilities() {}

  /**
   * Closes an already open InputStream. A possible IOException can be avoided when throwException
   * is set to false. In this case only a warning will be written to the log file.
   *
   * @param is the InputStream to close
   * @param throwException true throw an exception if the stream could not be closed, otherwise not
   * @throws IOException if the InputSream could not be closed
   */
  public static void closeInputStream(InputStream is, boolean throwException) throws IOException {
    if (is == null) {
      return;
    }
    try {
      is.close();
    } catch (IOException ex) {
      if (throwException) {
        throw ex;
      } else {
        LOGGER.warn("An InputStream could not be closed!");
      }
    }
  }

  /**
   * A convenient method for <code>closeInputStream(InputStream, boolean)</code>. The parameter
   * throw Exception is automatically set to false. In this case an exception will never arise.
   *
   * @param is the InputStream to close
   */
  public static void closeInputStream(InputStream is) {
    try {
      closeInputStream(is, false);
    } catch (Throwable ex) {
      /**
       * It is not necessary to handle this exception because the parameter throwException was set
       * to false. In this case the exception will never arise!
       */
    }
  }

  /**
   * Closes an already open OutputStream. A possible IOException can be avoided when throwException
   * is set to false. In this case only a warning will be written to the log file.
   *
   * @param out the OutputStream to close
   * @param throwException true throw an exception if the stream could not be closed, otherwise not
   * @throws IOException if the OutputStream could not be closed
   */
  public static void closeOutputStream(OutputStream out, boolean throwException)
      throws IOException {
    try {
      if (out != null) {
        out.close();
      }
    } catch (IOException ex) {
      if (throwException) {
        throw ex;
      } else {
        LOGGER.warn("A OutputStream could not be closed.");
      }
    }
  }

  /**
   * A convinent method for <code>closeOutputStream(InputStream, boolean)</code>. The parameter
   * throw Exception is automatically set to false. In this case an exception will never arise.
   *
   * @param out the OutputStream to close
   */
  public static void closeOutputStream(OutputStream out) {
    try {
      closeOutputStream(out, false);
    } catch (Throwable ex) {
      /**
       * It is not necessary to handle this exception because the parameter throwException was set
       * to false. In this case the exception will never arise!
       */
    }
  }

  /**
   * Closes an already open Reader. A possible IOException can be avoided when throwException is set
   * to false. In this case only a warning will be written to the log file.
   *
   * @param reader the Reader to close
   * @param throwException true throw an exception if the Reader could not be closed, otherwise not
   * @throws IOException if the Reader could not be closed
   */
  public static void closeReader(Reader reader, boolean throwException) throws IOException {
    if (reader == null) {
      return;
    }
    try {
      reader.close();
    } catch (IOException ex) {
      if (throwException) {
        throw ex;
      } else {
        LOGGER.warn("A Reader could not be closed.");
      }
    }
  }

  /**
   * A convinent method for <code>closeReader(InputStream, boolean)</code>. The parameter throw
   * Exception is automatically set to false. In this case an exception will never arise.
   *
   * @param reader the Reader to close
   */
  public static void closeReader(Reader reader) {
    try {
      closeReader(reader, false);
    } catch (Throwable ex) {
      /**
       * It is not necessary to handle this exception because the parameter throwException was set
       * to false. In this case the exception will never arise!
       */
    }
  }

  /**
   * Closes an already open Writer. A possible IOException can be avoided when throwException is set
   * to false. In this case only a warning will be written to the log file.
   *
   * @param writer the Writer to close
   * @param throwException true throw an exception if the Writer could not be closed, otherwise not
   * @throws IOException if the Writer could not be closed
   */
  public static void closeWriter(Writer writer, boolean throwException) throws IOException {
    if (writer == null) {
      return;
    }
    try {
      writer.close();
    } catch (IOException ex) {
      if (throwException) {
        throw ex;
      } else {
        LOGGER.warn("A Writer could not be closed.");
      }
    }
  }

  /**
   * A convinent method for <code>closeWriter(InputStream, boolean)</code>. The parameter throw
   * Exception is automatically set to false. In this case an exception will never arise.
   *
   * @param writer the Writer to close
   */
  public static void closeWriter(Writer writer) {
    try {
      closeWriter(writer, false);
    } catch (Throwable ex) {
      /**
       * It is not necessary to handle this exception because the parameter throwException was set
       * to false. In this case the exception will never arise!
       */
    }
  }
}
