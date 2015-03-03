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
 * XSLT.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.XSLTStyleSheet;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Applies an XSLT stylesheet to the DOM document object.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;org.w3c.dom.Document<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
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
 * &nbsp;&nbsp;&nbsp;default: XSLT
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
 * <pre>-stylesheet &lt;adams.core.io.PlaceholderFile&gt; (property: styleSheet)
 * &nbsp;&nbsp;&nbsp;The XSLT stylesheet to apply to the input.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-inline &lt;adams.core.base.XSLTStyleSheet&gt; (property: inline)
 * &nbsp;&nbsp;&nbsp;The inline XSLT stylesheet to apply to the input if the stylesheet file 
 * &nbsp;&nbsp;&nbsp;points to a directory.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XSLT
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -184602726110144511L;

  /** the XSLT stylesheet to apply. */
  protected PlaceholderFile m_StyleSheet;
  
  /** the inline stylesheet to apply. */
  protected XSLTStyleSheet m_Inline;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Applies an XSLT stylesheet to the DOM document object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "stylesheet", "styleSheet",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "inline", "inline",
	    new XSLTStyleSheet());
  }

  /**
   * Sets the expression to apply.
   *
   * @param value	the type
   */
  public void setStyleSheet(PlaceholderFile value) {
    m_StyleSheet = value;
    reset();
  }

  /**
   * Returns the expression to apply.
   *
   * @return		the type
   */
  public PlaceholderFile getStyleSheet() {
    return m_StyleSheet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String styleSheetTipText() {
    return "The XSLT stylesheet to apply to the input.";
  }

  /**
   * Sets the stylesheet to apply.
   *
   * @param value	the stylesheet
   */
  public void setInline(XSLTStyleSheet value) {
    m_Inline = value;
    reset();
  }

  /**
   * Returns the stylesheet to apply.
   *
   * @return		the stylesheet
   */
  public XSLTStyleSheet getInline() {
    return m_Inline;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inlineTipText() {
    return "The inline XSLT stylesheet to apply to the input if the stylesheet file points to a directory.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    if (QuickInfoHelper.hasVariable(this, "styleSheet") || !m_StyleSheet.isDirectory())
      return super.getQuickInfo();
    else
      return QuickInfoHelper.toString(this, "inline", Utils.shorten(m_Inline.stringValue(), 50));
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->org.w3c.dom.Document.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Document.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    DOMSource 			dsource;
    StreamSource 		stylesource;
    TransformerFactory 		tFactory;
    Transformer 		transformer;
    ByteArrayOutputStream	ostream;
    StreamResult 		sresult;

    result = null;

    try {
      dsource     = new DOMSource((Document) m_InputToken.getPayload());
      if (m_StyleSheet.isDirectory())
	stylesource = new StreamSource(new StringReader(m_Inline.getValue()));
      else
	stylesource = new StreamSource(m_StyleSheet.getAbsoluteFile());
      tFactory    = TransformerFactory.newInstance();
      transformer = tFactory.newTransformer(stylesource);
      ostream     = new ByteArrayOutputStream();
      sresult     = new StreamResult(ostream);
      transformer.transform(dsource, sresult);
      m_OutputToken = new Token(new String(ostream.toByteArray()));
    }
    catch (Exception e) {
      result = handleException("Failed to apply XSLT styleshet: " + m_StyleSheet, e);
    }

    return result;
  }
}
