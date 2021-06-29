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
 * IndexedSplitsRunsGenerator.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.base.BaseKeyValuePair;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.flow.core.Token;
import adams.flow.transformer.indexedsplitsrunsgenerator.AbstractIndexedSplitsRunsGenerator;
import adams.flow.transformer.indexedsplitsrunsgenerator.ManualSplitGenerator;

/**
 <!-- globalinfo-start -->
 * Uses the specified generator for generating indexed splits from the incoming data.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.Object[]<br>
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
 * &nbsp;&nbsp;&nbsp;default: IndexedSplitsRunsGenerator
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
 * <pre>-generator &lt;adams.flow.transformer.indexedsplitsrunsgenerator.AbstractIndexedSplitsRunsGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator to use for generating the indexed splits.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.transformer.indexedsplitsrunsgenerator.ManualSplitGenerator
 * </pre>
 *
 * <pre>-meta-data &lt;adams.core.base.BaseKeyValuePair&gt; [-meta-data ...] (property: metaData)
 * &nbsp;&nbsp;&nbsp;The meta-data to attach; any variables in the 'value' parts get automatically
 * &nbsp;&nbsp;&nbsp;expanded.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IndexedSplitsRunsGenerator
  extends AbstractTransformer {

  private static final long serialVersionUID = 7448032116260228656L;

  /** the generator to use. */
  protected AbstractIndexedSplitsRunsGenerator m_Generator;

  /** the meta-data to add. */
  protected BaseKeyValuePair[] m_MetaData;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Uses the specified generator for generating indexed splits from the incoming data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "generator", "generator",
      new ManualSplitGenerator());

    m_OptionManager.add(
      "meta-data", "metaData",
      new BaseKeyValuePair[0]);
  }

  /**
   * Sets the generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractIndexedSplitsRunsGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator to use.
   *
   * @return		the generator
   */
  public AbstractIndexedSplitsRunsGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The generator to use for generating the indexed splits.";
  }

  /**
   * Sets the meta-data to attach. Variables in 'value' parts get automatically expanded.
   *
   * @param value	the meta-data
   */
  public void setMetaData(BaseKeyValuePair[] value) {
    m_MetaData = value;
    reset();
  }

  /**
   * Returns the meta-data to attach. Variables in 'value' parts get automatically expanded.
   *
   * @return		the meta-data
   */
  public BaseKeyValuePair[] getMetaData() {
    return m_MetaData;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataTipText() {
    return "The meta-data to attach; any variables in the 'value' parts get automatically expanded.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "generator", m_Generator, "generator: ");
    result += QuickInfoHelper.toString(this, "metaData", m_MetaData, ", meta-data: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return m_Generator.accepts();
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
    MessageCollection	errors;
    IndexedSplitsRuns	runs;

    result = null;
    errors = new MessageCollection();
    try {
      runs = m_Generator.generate(m_InputToken.getPayload(), errors);
      if (runs == null) {
	if (errors.isEmpty())
	  result = "Failed to generate runs!";
	else
	  result = "Failed to generate runs:\n" + errors;
      }
      else {
        if (m_MetaData.length > 0) {
          for (BaseKeyValuePair metaData: m_MetaData)
            runs.getMetaData().put(metaData.getPairKey(), getVariables().expand(metaData.getPairValue()));
	}
        m_OutputToken = new Token(runs);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to generate runs!", e);
    }

    return result;
  }
}
