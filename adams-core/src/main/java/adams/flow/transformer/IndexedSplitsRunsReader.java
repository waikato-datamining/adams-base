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
 * IndexedSplitsRunsReader.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.io.input.AbstractIndexedSplitsRunsReader;
import adams.data.io.input.PropertiesIndexedSplitsRunsReader;
import adams.flow.core.Token;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Reads indexed splits runs from disk using the specified reader.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.indexedsplits.IndexedSplitsRuns<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: IndexedSplitsRunsReader
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-reader &lt;adams.data.io.input.AbstractIndexedSplitsRunsReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The reader to use for loading the splits from the file.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.PropertiesIndexedSplitsRunsReader
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IndexedSplitsRunsReader
  extends AbstractTransformer {

  private static final long serialVersionUID = 2274110475414023349L;

  /** the reader to use. */
  protected AbstractIndexedSplitsRunsReader m_Reader;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads indexed splits runs from disk using the specified reader.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "reader", "reader",
      new PropertiesIndexedSplitsRunsReader());
  }

  /**
   * Sets the reader to use.
   *
   * @param value	the reader
   */
  public void setReader(AbstractIndexedSplitsRunsReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the reader to use.
   *
   * @return		the reader
   */
  public AbstractIndexedSplitsRunsReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The reader to use for loading the splits from the file.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "reader", m_Reader);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{IndexedSplitsRuns.class};
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    File		file;
    MessageCollection	errors;
    IndexedSplitsRuns	runs;

    result = null;
    file   = null;
    if (m_InputToken.hasPayload(String.class))
      file = new PlaceholderFile(m_InputToken.getPayload(String.class));
    else if (m_InputToken.hasPayload(File.class))
      file = new PlaceholderFile(m_InputToken.getPayload(File.class));
    else
      result = m_InputToken.unhandledData();

    if (result == null) {
      errors = new MessageCollection();
      try {
        runs = m_Reader.read(file.getAbsoluteFile(), errors);
        if (runs == null) {
          if (errors.isEmpty())
            result = "Failed to read runs from: " + file;
          else
            result = "Failed to read runs from: " + file + "\n" + errors;
        }
        else {
          m_OutputToken = new Token(runs);
	}
      }
      catch (Exception e) {
        result = handleException("Failed to read runs from: " + file, e);
      }
    }

    return result;
  }
}
