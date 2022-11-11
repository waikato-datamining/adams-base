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
 * Mat5ArrayInfo.java
 * Copyright (C) 2022 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.matlab.MatlabUtils;
import adams.flow.core.DataInfoActor;
import us.hebi.matlab.mat.types.Array;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Provides information on a Matlab5 array.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;us.hebi.matlab.mat.types.Array<br>
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
 * &nbsp;&nbsp;&nbsp;default: Mat5ArrayInfo
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
 * <pre>-type &lt;ALL|TYPE_NAME|TYPE_ID|NUM_DIMENSIONS|STR_DIMENSIONS|NUM_COLS|NUM_ROWS|NUM_ELEMENTS|DIMENSIONS&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of information to generate.
 * &nbsp;&nbsp;&nbsp;default: NUM_DIMENSIONS
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Mat5ArrayInfo
    extends AbstractArrayProvider
    implements DataInfoActor {

  private static final long serialVersionUID = 8251699709312918726L;

  /**
   * The type of information to generate.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public enum InfoType {
    ALL,
    TYPE_NAME,
    TYPE_ID,
    NUM_DIMENSIONS,
    STR_DIMENSIONS,
    NUM_COLS,
    NUM_ROWS,
    NUM_ELEMENTS,
    DIMENSIONS,
  }

  /** the type of information to generate. */
  protected InfoType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Provides information on a Matlab5 array.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"type", "type",
	InfoType.NUM_DIMENSIONS);
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
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Array.class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return the class
   */
  @Override
  protected Class getItemClass() {
    switch (m_Type) {
      case ALL:
	return Map.class;
      case TYPE_NAME:
      case STR_DIMENSIONS:
	return String.class;
      case TYPE_ID:
      case NUM_DIMENSIONS:
      case NUM_COLS:
      case NUM_ROWS:
      case DIMENSIONS:
      case NUM_ELEMENTS:
	return Integer.class;
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

    result = QuickInfoHelper.toString(this, "type", m_Type);

    return result;
  }

  /**
   * Returns the specified info as list.
   *
   * @param array	the array to get the info for
   * @param type	the type of info to return
   * @return		the generated info
   */
  protected List getInfo(Array array, InfoType type) {
    List		result;

    result = new ArrayList();
    switch (type) {
      case ALL:
	// nothing
	break;
      case TYPE_NAME:
	result.add(array.getType().name());
	break;
      case TYPE_ID:
	result.add(array.getType().id());
	break;
      case NUM_DIMENSIONS:
	result.add(array.getNumDimensions());
	break;
      case STR_DIMENSIONS:
	result.add(MatlabUtils.arrayDimensionsToString(array));
	break;
      case NUM_COLS:
	result.add(array.getNumCols());
	break;
      case NUM_ROWS:
	result.add(array.getNumRows());
	break;
      case NUM_ELEMENTS:
	result.add(array.getNumElements());
	break;
      case DIMENSIONS:
	for (int dim: array.getDimensions())
	  result.add(dim);
	break;
      default:
	throw new IllegalStateException("Unhandled info type: " + type);
    }

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
    Array		array;
    StringBuilder	dims;
    Map			map;
    List		info;

    result = null;

    array = m_InputToken.getPayload(Array.class);
    try {
      m_Queue.clear();
      switch (m_Type) {
	case ALL:
	  map = new HashMap();
	  for (InfoType type: InfoType.values()) {
	    if (type == InfoType.ALL)
	      continue;
	    info = getInfo(array, type);
	    if (info.size() == 1)
	      map.put(type.name(), info.get(0));
	    else
	      map.put(type.name(), info.toArray());
	  }
	  m_Queue.add(map);
	  break;
	case TYPE_NAME:
	case TYPE_ID:
	case NUM_DIMENSIONS:
	case STR_DIMENSIONS:
	case NUM_COLS:
	case NUM_ROWS:
	case NUM_ELEMENTS:
	case DIMENSIONS:
	  m_Queue.addAll(getInfo(array, m_Type));
	  break;
	default:
	  throw new IllegalStateException("Unhandled info type: " + m_Type);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to generate information: " + m_Type, e);
    }

    return result;
  }
}
