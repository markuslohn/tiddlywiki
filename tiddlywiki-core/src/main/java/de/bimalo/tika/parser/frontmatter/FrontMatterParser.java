package de.bimalo.tika.parser.frontmatter;

import com.esotericsoftware.yamlbeans.YamlReader;
import org.apache.tika.detect.AutoDetectReader;
import org.apache.tika.detect.EncodingDetector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractEncodingDetectorParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * <p>This parser tries to detect and read a "Front Matter Block" in a text file.
 * The Front Matter Block must be written in YAML notation. A Front Matter Block
 * starts with "---" and also ends with "---". </p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 */
public class FrontMatterParser extends AbstractEncodingDetectorParser {

  /**
   * Serial version UID.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The default media type for this parser implementation.
   */
  private static final MediaType MEDIA_TYPE = MediaType.text("x-web-markdown");

  /**
   * A list of supported types for this parser implementation.
   */
  private static final Set<MediaType> SUPPORTED_TYPES
    = Collections.unmodifiableSet(new HashSet<MediaType>(
      Arrays.asList(MEDIA_TYPE, MediaType.TEXT_PLAIN)));

  /**
   * Regular expression indicating the start of a Front Matter block.
   */
  private static final Pattern FRONTMATTERBLOCK_REGEX_BEGIN
    = Pattern.compile("^-{3}(\\s.*)?");
  /**
   * Regular expression indicating the end of a Front Matter block.
   */
  private static final Pattern FRONTMATTERBLOCK_REGEX_END
    = Pattern.compile("^(-{3}|\\.{3})(\\s.*)?");

  /**
   * Platform dependent line separator.
   */
  private static final String LINE_SEPARATOR = System.getProperty("line.separator");

  @Override
  public Set<MediaType> getSupportedTypes(ParseContext context) {
    return SUPPORTED_TYPES;
  }

  /**
   * Create a default YamlFrontMatterParser.
   */
  public FrontMatterParser() {
    super();
  }

  /**
   * Create a YamlFrontMatterParser with a EncodingDetector.
   * @param encodingDetector a EncodingDetector
   */
  public FrontMatterParser(EncodingDetector encodingDetector) {
    super(encodingDetector);
  }

  @Override
  public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {

    AutoDetectReader reader
      = new AutoDetectReader(new CloseShieldInputStream(stream), metadata, getEncodingDetector(context));

    XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
    xhtml.startDocument();
    xhtml.startElement("p");

    boolean inFrontMatterBlock = false;
    StringBuilder frontMatterBlock = new StringBuilder();
    String line = null;

    while ((line = reader.readLine()) != null) {
      if (inFrontMatterBlock) {
        if (FRONTMATTERBLOCK_REGEX_END.matcher(line).matches()) {
          inFrontMatterBlock = false;
        }
        frontMatterBlock.append(line);
        frontMatterBlock.append(LINE_SEPARATOR);
      } else if (FRONTMATTERBLOCK_REGEX_BEGIN.matcher(line).matches()) {
        inFrontMatterBlock = true;
        frontMatterBlock.append(line);
        frontMatterBlock.append(LINE_SEPARATOR);
      } else {
        xhtml.characters(line);
        xhtml.characters(LINE_SEPARATOR);
      }
    }

    xhtml.endElement("p");
    xhtml.endDocument();

    String incomingMime = metadata.get(Metadata.CONTENT_TYPE);
    MediaType mediaType = MEDIA_TYPE;
    if (incomingMime != null) {
      MediaType tmpMediaType = MediaType.parse(incomingMime);
      if (tmpMediaType != null) {
        mediaType = tmpMediaType;
      }
    }

    Charset charset = reader.getCharset();
    MediaType type = new MediaType(mediaType, charset);
    metadata.set(Metadata.CONTENT_TYPE, type.toString());
    metadata.set(Metadata.CONTENT_ENCODING, charset.name());

    if (frontMatterBlock.length() > 0) {
      YamlReader yamlReader = new YamlReader(frontMatterBlock.toString());
      Map frontMatterProperties = (Map) yamlReader.read();
      for (Iterator iter = frontMatterProperties.entrySet().iterator(); iter.hasNext();) {
        Map.Entry entry = (Map.Entry) iter.next();
        String key = String.valueOf(entry.getKey());
        Object valueObj = entry.getValue();
        if (valueObj instanceof List) {
          List values = (List) valueObj;
          for (int i = 0; i < values.size(); i++) {
            metadata.add(key, String.valueOf(values.get(i)));
          }
        } else {
          metadata.add(key, String.valueOf(valueObj));
        }
      }
    }
  }
}
