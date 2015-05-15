/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * HTMLFileReader.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;

/**
 <!-- globalinfo-start -->
 * Reads an HTML file and forwards the parsed org.w3c.dom.Document object.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.io.File<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;org.w3c.dom.Document<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: HTMLFileReader
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-validating &lt;boolean&gt; (property: validating)
 * &nbsp;&nbsp;&nbsp;If enabled, the parser will validate the HTML.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-name-space-aware &lt;boolean&gt; (property: nameSpaceAware)
 * &nbsp;&nbsp;&nbsp;If enabled, the parser will be namespace aware.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-x-include-aware &lt;boolean&gt; (property: XIncludeAware)
 * &nbsp;&nbsp;&nbsp;If enabled, the parser will be X-include aware.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-expand-entity-references &lt;boolean&gt; (property: expandEntityReferences)
 * &nbsp;&nbsp;&nbsp;If enabled, the parser will expand entity references.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-ignoring-comments &lt;boolean&gt; (property: ignoringComments)
 * &nbsp;&nbsp;&nbsp;If enabled, the parser will ignore comments.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-coalescing &lt;boolean&gt; (property: coalescing)
 * &nbsp;&nbsp;&nbsp;If enabled, then parser will convert CDATA nodes to Text nodes and append 
 * &nbsp;&nbsp;&nbsp;it to the adjacent (if any) text node.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-ignoring-whitespace &lt;boolean&gt; (property: ignoringWhitespace)
 * &nbsp;&nbsp;&nbsp;If enabled, the parser will ignore whitespaces in element content.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HTMLFileReader
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -184602726110144511L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Reads an HTML file and forwards the parsed " + Document.class.getName() + " object.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.io.File.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->org.w3c.dom.Document.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{Document.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    Object			fileObj;
    File			file;
    DOMParser			parser;
    FileInputStream		fis;
    Document 			doc;

    result = null;

    fileObj = m_InputToken.getPayload();
    if (fileObj instanceof File)
      file = (File) fileObj;
    else
      file = new PlaceholderFile((String) fileObj);

    fis = null;
    try {
      fis    = new FileInputStream(file.getAbsoluteFile());
      parser = new DOMParser();
      parser.parse(new InputSource(fis));
      doc    = parser.getDocument();
      
      m_OutputToken = new Token(doc);
    }
    catch (Exception e) {
      result = handleException("Failed to read HTML file: " + file, e);
    }
    finally {
      FileUtils.closeQuietly(fis);
    }

    return result;
  }
}
