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
 * TextWriter.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.QuickInfoHelper;
import adams.data.io.output.AbstractTextWriter;
import adams.data.io.output.NullWriter;

/**
 <!-- globalinfo-start -->
 * Writes incoming textual data to a text file.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: TextWriter
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-content-name &lt;java.lang.String&gt; (property: contentName)
 * &nbsp;&nbsp;&nbsp;The name of the content, might be used in the filename of the output.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-writer &lt;adams.core.io.AbstractTextWriter [options]&gt; (property: writer)
 * &nbsp;&nbsp;&nbsp;The writer to use for ouputting the textual data.
 * &nbsp;&nbsp;&nbsp;default: adams.core.io.NullWriter
 * </pre>
 *
 * Default options for adams.core.io.NullWriter (-writer/writer):
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TextWriter
  extends AbstractSink {

  /** for serialization. */
  private static final long serialVersionUID = 798901949899149659L;

  /** the name of the content (e.g., can be used in the filename). */
  protected String m_ContentName;

  /** the writer to use. */
  protected AbstractTextWriter m_Writer;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes incoming textual data to a text file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "content-name", "contentName",
	    "");

    m_OptionManager.add(
	    "writer", "writer",
	    new NullWriter());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "writer", m_Writer);
  }

  /**
   * Sets name of the content.
   *
   * @param value 	the content name
   */
  public void setContentName(String value) {
    m_ContentName = value;
    reset();
  }

  /**
   * Returns the name of the content.
   *
   * @return 		the content name
   */
  public String getContentName() {
    return m_ContentName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String contentNameTipText() {
    return "The name of the content, might be used in the filename of the output.";
  }

  /**
   * Sets whether to append to the file or not.
   *
   * @param value 	true if appending to file instead of rewriting it
   */
  public void setWriter(AbstractTextWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns whether files gets only appended or not.
   *
   * @return 		true if appending is turned on
   */
  public AbstractTextWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use for ouputting the textual data.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;

    try {
      if (m_Writer.write(m_InputToken.getPayload().toString(), m_ContentName) == null)
	result = "Error writing data: " + m_Writer.toCommandLine();
      else
	result = null;
    }
    catch (Exception e) {
      result = handleException("Failed to write data:", e);
    }

    return result;
  }
}
