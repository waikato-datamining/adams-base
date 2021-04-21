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
 * Mat5FileGetArray.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.core.Token;
import us.hebi.matlab.mat.format.Mat5File;
import us.hebi.matlab.mat.types.Array;
import us.hebi.matlab.mat.types.MatFile;

/**
 <!-- globalinfo-start -->
 * Retrieves the specified array from the mat file object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;us.hebi.matlab.mat.format.Mat5File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;us.hebi.matlab.mat.types.Array<br>
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
 * &nbsp;&nbsp;&nbsp;default: Mat5FileGetArray
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
 * <pre>-entry-name &lt;java.lang.String&gt; (property: entryName)
 * &nbsp;&nbsp;&nbsp;The name of the entry to retrieve, the first one if empty.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Mat5FileGetArray
  extends AbstractTransformer {

  private static final long serialVersionUID = -4381778255320714964L;

  /** the name of the entry to retrieve. */
  protected String m_EntryName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Retrieves the specified array from the mat file object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "entry-name", "entryName",
      "");
  }

  /**
   * Sets the name of the entry to retrieve.
   *
   * @param value	the name
   */
  public void setEntryName(String value) {
    m_EntryName = value;
    reset();
  }

  /**
   * Returns the name of the entry to retrieve.
   *
   * @return		the name
   */
  public String getEntryName() {
    return m_EntryName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String entryNameTipText() {
    return "The name of the entry to retrieve, the first one if empty.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Mat5File.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Array.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "entryName", m_EntryName, "entry: ");
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Mat5File 		mat5;

    if (m_EntryName.isEmpty())
      getLogger().warning("Entry name is empty, returning first entry!");

    result = null;
    mat5   = m_InputToken.getPayload(Mat5File.class);
    for (MatFile.Entry entry: mat5.getEntries()) {
      if (entry.getName().equals(m_EntryName) || m_EntryName.isEmpty()) {
	m_OutputToken = new Token(entry.getValue());
	break;
      }
    }
    if (m_OutputToken == null)
      getLogger().warning("Entry not found: " + m_EntryName);

    return result;
  }
}
