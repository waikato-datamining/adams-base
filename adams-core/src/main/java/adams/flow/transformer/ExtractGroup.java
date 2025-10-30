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
 * ExtractGroup.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.groupextraction.GroupExtractor;
import adams.data.groupextraction.Manual;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Extracts the group from the object and forwards it.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: ExtractGroup
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
 * <pre>-extractor &lt;adams.data.groupextraction.GroupExtractor&gt; (property: extractor)
 * &nbsp;&nbsp;&nbsp;The scheme to use for extracting the group.
 * &nbsp;&nbsp;&nbsp;default: adams.data.groupextraction.Manual
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ExtractGroup
  extends AbstractTransformer {

  private static final long serialVersionUID = -2978040822861434285L;

  /** the group extractor to use. */
  protected GroupExtractor m_Extractor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Extracts the group from the object and forwards it.";
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "extractor", "extractor",
      new Manual());
  }

  /**
   * Sets the group extractor to use.
   *
   * @param value	the extractor
   */
  public void setExtractor(GroupExtractor value) {
    m_Extractor = value;
    reset();
  }

  /**
   * Returns the group extractor to use.
   *
   * @return		the extractor
   */
  public GroupExtractor getExtractor() {
    return m_Extractor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String extractorTipText() {
    return "The scheme to use for extracting the group.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "extractor", m_Extractor, "extractor: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Object.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
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
    String	result;

    result = null;

    if (!m_Extractor.handles(m_InputToken.getPayload()))
      result = m_InputToken.unhandledData();

    try {
      m_OutputToken = new Token(m_Extractor.extractGroup(m_InputToken.getPayload()));
    }
    catch (Exception e) {
      result = handleException("Failed to extract group from: " + m_InputToken.getPayload(), e);
    }

    return result;
  }
}
