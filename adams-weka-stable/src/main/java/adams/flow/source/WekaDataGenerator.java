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
 * WekaDataGenerator.java
 * Copyright (C) 2009-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.Shortening;
import adams.core.option.OptionUtils;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import weka.core.Instances;
import weka.datagenerators.classifiers.classification.Agrawal;

/**
 <!-- globalinfo-start -->
 * Generates artificial data using a Weka data generator.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaDataGenerator
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
 * <pre>-property &lt;adams.core.base.BaseString&gt; [-property ...] (property: properties)
 * &nbsp;&nbsp;&nbsp;The properties to update with the values associated with the specified values.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-variable &lt;adams.core.VariableName&gt; [-variable ...] (property: variableNames)
 * &nbsp;&nbsp;&nbsp;The names of the variables to update the properties with.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-generator &lt;weka.datagenerators.DataGenerator&gt; (property: dataGenerator)
 * &nbsp;&nbsp;&nbsp;The data generator to use for generating the weka.core.Instances object.
 * &nbsp;&nbsp;&nbsp;default: weka.datagenerators.classifiers.classification.Agrawal -r weka.datagenerators.classifiers.classification.Agrawal-S_1_-n_100_-F_1_-P_0.05 -S 1 -n 100 -F 1 -P 0.05
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaDataGenerator
  extends AbstractSimpleSourceWithPropertiesUpdating
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 1862828539481494711L;

  /** the filter to apply. */
  protected weka.datagenerators.DataGenerator m_DataGenerator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates artificial data using a Weka data generator.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generator", "dataGenerator",
	    new Agrawal());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = super.getQuickInfo();
    if (!result.isEmpty())
      result += ", ";
    result += QuickInfoHelper.toString(this, "generator", Shortening.shortenEnd(OptionUtils.getShortCommandLine(m_DataGenerator), 40));

    return result;
  }

  /**
   * Sets the data generator to use.
   *
   * @param value	the data generator
   */
  public void setDataGenerator(weka.datagenerators.DataGenerator value) {
    m_DataGenerator = value;
    reset();
  }

  /**
   * Returns the data generator in use.
   *
   * @return		the data generator
   */
  public weka.datagenerators.DataGenerator getDataGenerator() {
    return m_DataGenerator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataGeneratorTipText() {
    return "The data generator to use for generating the weka.core.Instances object.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.core.Instances.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Instances.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Instances	data;

    m_OutputToken = null;

    try {
      result = setUpContainersIfNecessary(m_DataGenerator);
      if (result == null)
        result = updateObject(m_DataGenerator);
      if (result == null) {
	m_DataGenerator.setDatasetFormat(m_DataGenerator.defineDataFormat());
	data = m_DataGenerator.generateExamples();
	if (data == null)
	  result = "No data obtained from data generator!";
	else
	  m_OutputToken = new Token(data);

	updateProvenance(m_OutputToken);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to generate data: ", e);
    }

    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, this, m_OutputToken.getPayload().getClass()));
  }
}
