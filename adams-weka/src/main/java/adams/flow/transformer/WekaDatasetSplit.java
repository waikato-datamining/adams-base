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
 * WekaDatasetSplit.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.weka.datasetsplitter.AbstractSplitter;
import adams.data.weka.datasetsplitter.RowSplitter;
import adams.flow.core.Token;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Splits the incoming dataset into sub-sets using the specified splitter.
 * <br><br>
 <!-- globalinfo-end -->
 * <p>
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances[]<br>
 * <br><br>
 <!-- flow-summary-end -->
 * <p>
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaDatasetSplit
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
 * <pre>-splitter &lt;adams.data.weka.datasetsplitter.AbstractSplitter&gt; (property: splitter)
 * &nbsp;&nbsp;&nbsp;The splitter to use to split the dataset.
 * &nbsp;&nbsp;&nbsp;default: adams.data.weka.datasetsplitter.RowSplitter -row-finder adams.data.weka.rowfinder.NullFinder
 * </pre>
 *
 <!-- options-end -->
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class WekaDatasetSplit extends AbstractTransformer {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = -2718426142990052207L;

  /** The splitter to use. */
  protected AbstractSplitter m_Splitter;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("splitter", "splitter", new RowSplitter());
  }

  /**
   * Gets the splitter to use.
   *
   * @return	The splitter to use.
   */
  public AbstractSplitter getSplitter()
  {
    return m_Splitter;
  }

  /**
   * Sets the splitter to use.
   *
   * @param value	The splitter to use.
   */
  public void setSplitter(AbstractSplitter value) {
    m_Splitter = value;
    reset();
  }

  /**
   * Gets the tip-text for the splitter option.
   *
   * @return	The tip-text as a string.
   */
  public String splitterTipText() {
    return "The splitter to use to split the dataset.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    try {
      Instances dataset = (Instances) m_InputToken.getPayload();

      Instances[] result = m_Splitter.split(dataset);

      m_OutputToken = new Token();
      m_OutputToken.setPayload(result);

      return null;
    } catch (Exception e) {
      return e.getMessage();
    }
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits the incoming dataset into sub-sets using the specified splitter.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "splitter", m_Splitter, "splitter: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[] { Instances.class };
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[] { Instances[].class };
  }
}