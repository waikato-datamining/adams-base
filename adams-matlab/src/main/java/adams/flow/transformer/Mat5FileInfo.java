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
 * Mat5FileInfo.java
 * Copyright (C) 2021-2022 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.core.DataInfoActor;
import us.hebi.matlab.mat.format.Mat5;
import us.hebi.matlab.mat.format.Mat5File;
import us.hebi.matlab.mat.types.MatFile;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

/**
 <!-- globalinfo-start -->
 * Provides information from a .mat file.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.io.File<br>
 * &nbsp;&nbsp;&nbsp;us.hebi.matlab.mat.format.Mat5File<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Integer<br>
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
 * &nbsp;&nbsp;&nbsp;default: Mat5FileInfo
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
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, outputs the items as array rather than one-by-one.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-type &lt;DESCRIPTION|VERSION|BYTE_ORDER|NUM_ENTRIES|ENTRY_NAMES&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of information to generate.
 * &nbsp;&nbsp;&nbsp;default: NUM_ENTRIES
 * </pre>
 *
 * <pre>-sort &lt;boolean&gt; (property: sort)
 * &nbsp;&nbsp;&nbsp;If enabled, lists (eg names, values) are sorted.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Mat5FileInfo
  extends AbstractArrayProvider
  implements DataInfoActor {

  private static final long serialVersionUID = 8251699709312918726L;

  /**
   * The type of information to generate.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum InfoType {
    DESCRIPTION,
    VERSION,
    BYTE_ORDER,
    NUM_ENTRIES,
    ENTRY_NAMES,
  }

  /** the type of information to generate. */
  protected InfoType m_Type;

  /** whether to sort lists. */
  protected boolean m_Sort;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Provides information from a .mat file.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      InfoType.NUM_ENTRIES);

    m_OptionManager.add(
      "sort", "sort",
      true);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, outputs the items as array rather than one-by-one.";
  }

  /**
   * Sets the type of information to generate.
   *
   * @param value	the type
   */
  public void setType(InfoType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of information to generate.
   *
   * @return		the type
   */
  public InfoType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of information to generate.";
  }

  /**
   * Sets whether to sort lists (eg names, values).
   *
   * @param value	true if to sort
   */
  public void setSort(boolean value) {
    m_Sort = value;
    reset();
  }

  /**
   * Returns whether lists (eg names, values) are sorted.
   *
   * @return		true if to sort
   */
  public boolean getSort() {
    return m_Sort;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortTipText() {
    return "If enabled, lists (eg names, values) are sorted.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String.class, File.class, Mat5File.class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return the class
   */
  @Override
  protected Class getItemClass() {
    switch (m_Type) {
      case DESCRIPTION:
	return String.class;
      case VERSION:
	return Integer.class;
      case BYTE_ORDER:
	return String.class;
      case NUM_ENTRIES:
	return Integer.class;
      case ENTRY_NAMES:
	return String.class;
      default:
	throw new IllegalStateException("Unhandled info type: " + m_Type);
    }
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    HashSet<InfoType>   types;

    result = QuickInfoHelper.toString(this, "type", m_Type);

    types = new HashSet<>(
      Arrays.asList(
	InfoType.ENTRY_NAMES));
    if (types.contains(m_Type) || QuickInfoHelper.hasVariable(this, "type"))
      result += QuickInfoHelper.toString(this, "sort", m_Sort, (m_Sort ? "sorted" : "unsorted"), ", ");

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    PlaceholderFile	file;
    Mat5File		mat5;

    result = null;

    mat5 = null;
    file = null;
    if (m_InputToken.hasPayload(String.class))
      file = new PlaceholderFile(m_InputToken.getPayload(String.class));
    else if (m_InputToken.hasPayload(File.class))
      file = new PlaceholderFile(m_InputToken.getPayload(File.class));
    else if (m_InputToken.hasPayload(Mat5File.class))
      mat5 = m_InputToken.getPayload(Mat5File.class);
    else
      result = m_InputToken.unhandledData();

    if (file != null) {
      try {
	mat5 = Mat5.readFromFile(file.getAbsoluteFile());
      }
      catch (Exception e) {
        result = handleException("Failed to load file: " + file, e);
      }
    }

    if (result == null) {
      try {
	m_Queue.clear();
	switch (m_Type) {
	  case DESCRIPTION:
	    m_Queue.add(mat5.getDescription());
	    break;
	  case VERSION:
	    m_Queue.add((int) mat5.getVersion());
	    break;
	  case BYTE_ORDER:
	    m_Queue.add(mat5.getByteOrder().toString());
	    break;
	  case NUM_ENTRIES:
	    m_Queue.add(mat5.getNumEntries());
	    break;
	  case ENTRY_NAMES:
	    for (MatFile.Entry entry : mat5.getEntries())
	      m_Queue.add(entry.getName());
	    break;
	  default:
	    throw new IllegalStateException("Unhandled info type: " + m_Type);
	}
      }
      catch (Exception e) {
	result = handleException("Failed to generate information: " + m_Type, e);
      }

      if ((result == null) && m_Sort && (m_Queue.size() > 1))
        Collections.sort(m_Queue);
    }

    return result;
  }
}
