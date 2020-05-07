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
 * FilterMap.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.ObjectCopyHelper;
import adams.core.QuickInfoHelper;
import adams.data.InPlaceProcessing;
import adams.flow.core.Token;
import adams.flow.transformer.mapfilter.AbstractMapFilter;
import adams.flow.transformer.mapfilter.PassThrough;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Applies the specified filter to the map object passing through.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.util.Map<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.util.Map<br>
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
 * &nbsp;&nbsp;&nbsp;default: FilterMap
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
 * <pre>-filter &lt;adams.flow.transformer.mapfilter.AbstractMapFilter&gt; (property: filter)
 * &nbsp;&nbsp;&nbsp;The filter to apply to the map.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.mapfilter.PassThrough
 * </pre>
 *
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the map is created before filtering.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FilterMap
  extends AbstractTransformer
  implements InPlaceProcessing {

  private static final long serialVersionUID = 8610396047582387719L;

  /** the map filter to apply. */
  protected AbstractMapFilter m_Filter;

  /** whether to skip creating a copy of the container. */
  protected boolean m_NoCopy;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified filter to the map object passing through.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "filter", "filter",
      new PassThrough());

    m_OptionManager.add(
      "no-copy", "noCopy",
      false);
  }

  /**
   * Sets the filter to apply.
   *
   * @param value	the filter
   */
  public void setFilter(AbstractMapFilter value) {
    m_Filter = value;
    reset();
  }

  /**
   * Returns the filter to apply.
   *
   * @return		the filter
   */
  public AbstractMapFilter getFilter() {
    return m_Filter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String filterTipText() {
    return "The filter to apply to the map.";
  }

  /**
   * Sets whether to skip creating a copy of the map before filtering.
   *
   * @param value	true if to skip creating copy
   */
  public void setNoCopy(boolean value) {
    m_NoCopy = value;
    reset();
  }

  /**
   * Returns whether to skip creating a copy of the map before filtering.
   *
   * @return		true if copying is skipped
   */
  public boolean getNoCopy() {
    return m_NoCopy;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noCopyTipText() {
    return "If enabled, no copy of the map is created before filtering.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "filter", m_Filter, "filter: ");
    result += QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no-copy", ", ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Map.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Map.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Map		map;

    result = null;
    map    = m_InputToken.getPayload(Map.class);

    if (!m_NoCopy)
      map = ObjectCopyHelper.copyObject(map);

    try {
      map = m_Filter.filterMap(map);
      m_OutputToken = new Token(map);
    }
    catch (Exception e) {
      result = handleException("Failed to filter map!", e);
    }

    return result;
  }
}
