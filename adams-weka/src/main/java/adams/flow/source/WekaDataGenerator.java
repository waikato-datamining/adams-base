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
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.Shortening;
import weka.core.Instances;
import weka.datagenerators.classifiers.classification.Agrawal;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

/**
 <!-- globalinfo-start -->
 * Generates artificial data using a Weka data generator.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br>
 * - generates:<br>
 * <pre>   weka.core.Instances</pre>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 *         The name of the actor.
 *         default: DataGenerator
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseString&gt; [-annotation ...] (property: annotations)
 *         The annotations to attach to this actor.
 * </pre>
 *
 * <pre>-skip (property: skip)
 *         If set to true, transformation is skipped and the input token is just forwarded
 *          as it is.
 * </pre>
 *
 * <pre>-generator &lt;weka.datagenerators.DataGenerator [options]&gt; (property: dataGenerator)
 *         The data generator to use for generating the weka.core.Instances object.
 *         default: weka.datagenerators.classifiers.classification.Agrawal -r weka.datagenerators.classifiers.classification.Agrawal-S_1_-n_100_-F_1_-P_0.05 -S 1 -n 100 -F 1 -P 0.05
 * </pre>
 *
 * Default options for weka.datagenerators.classifiers.classification.Agrawal (-generator/dataGenerator):
 *
 * <pre>See Weka Javadoc
 * </pre>
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaDataGenerator
  extends AbstractSimpleSource
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
    return QuickInfoHelper.toString(this, "generator", Shortening.shortenEnd(OptionUtils.getShortCommandLine(m_DataGenerator), 40));
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

    result        = null;
    m_OutputToken = null;

    try {
      m_DataGenerator.setDatasetFormat(m_DataGenerator.defineDataFormat());
      data = m_DataGenerator.generateExamples();
      if (data == null)
	result = "No data obtained from data generator!";
      else
	m_OutputToken = new Token(data);

      updateProvenance(m_OutputToken);
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
