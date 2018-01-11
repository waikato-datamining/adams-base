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
 * WekaGeneticAlgorithm.java
 * Copyright (C) 2015-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.option.OptionUtils;
import adams.flow.container.WekaGeneticAlgorithmInitializationContainer;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.Token;
import adams.opt.genetic.AbstractClassifierBasedGeneticAlgorithm;
import weka.core.Instances;

import java.util.Properties;

/**
 <!-- globalinfo-start -->
 * Populates a adams.flow.container.WekaGeneticAlgorithmInitializationContainer container from the data obtained from the incoming setup (in properties format, can be gzip compressed).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * &nbsp;&nbsp;&nbsp;java.util.Properties<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaGeneticAlgorithmInitializationContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaGeneticAlgorithmInitializationContainer: Algorithm, Data
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
 * &nbsp;&nbsp;&nbsp;default: WekaGeneticAlgorithmInitializer
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
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the data in internal storage.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 * 
 * <pre>-setup &lt;java.lang.String&gt; (property: setup)
 * &nbsp;&nbsp;&nbsp;The property in the incoming properties that contains the commandline of 
 * &nbsp;&nbsp;&nbsp;the genetic algorithm.
 * &nbsp;&nbsp;&nbsp;default: Commandline
 * </pre>
 * 
 * <pre>-weights &lt;java.lang.String&gt; (property: weights)
 * &nbsp;&nbsp;&nbsp;The optional weights property in the incoming properties for initializing 
 * &nbsp;&nbsp;&nbsp;the algorithm.
 * &nbsp;&nbsp;&nbsp;default: Weights
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaGeneticAlgorithmInitializer
  extends AbstractTransformer
  implements StorageUser {

  /** for serialization. */
  private static final long serialVersionUID = 5071747277597147724L;

  /** the name of the datasets in the internal storage. */
  protected StorageName m_StorageName;

  /** the property for the genetic algorithm setup. */
  protected String m_Setup;

  /** the property for the weights (optional). */
  protected String m_Weights;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Populates a " + WekaGeneticAlgorithmInitializationContainer.class.getName() + " container "
      + "from the data obtained from the incoming setup (in properties format, can be gzip compressed).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName());

    m_OptionManager.add(
      "setup", "setup",
      "Commandline");

    m_OptionManager.add(
      "weights", "weights",
      "Weights");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "storageName", m_StorageName, "data: ");
    result += QuickInfoHelper.toString(this, "setup", m_Setup, ", setup: ");
    result += QuickInfoHelper.toString(this, "weights", m_Weights, ", weights: ");

    return result;
  }

  /**
   * Sets the name for the data in the internal storage.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name for the data in the internal storage.
   *
   * @return		the name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the data in internal storage.";
  }

  /**
   * Sets the property in the incoming properties that contains the commandline
   * of the genetic algorithm.
   *
   * @param value	the property
   */
  public void setSetup(String value) {
    m_Setup = value;
    reset();
  }

  /**
   * Returns the property in the incoming properties that contains the commandline
   * of the genetic algorithm.
   *
   * @return		the property
   */
  public String getSetup() {
    return m_Setup;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String setupTipText() {
    return "The property in the incoming properties that contains the commandline of the genetic algorithm.";
  }

  /**
   * Sets the optional property in the incoming properties for the initial
   * weights to use.
   *
   * @param value 	the weights
   */
  public void setWeights(String value) {
    m_Weights = value;
    reset();
  }

  /**
   * Returns the optional property in the incoming properties for the initial
   * weights to use.
   *
   * @return 		the weights
   */
  public String getWeights() {
    return m_Weights;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String weightsTipText() {
    return "The optional weights property in the incoming properties for initializing the algorithm.";
  }

  /**
   * Returns whether storage items are being used.
   *
   * @return		true if storage items are used
   */
  public boolean isUsingStorage() {
    return !getSkip();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String.class, java.util.Properties.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String.class, Properties.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.WekaGeneticAlgorithmInitializationContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{WekaGeneticAlgorithmInitializationContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String					result;
    Properties					props;
    adams.core.Properties			aprops;
    WekaGeneticAlgorithmInitializationContainer	cont;
    String					cmdline;
    AbstractClassifierBasedGeneticAlgorithm	algorithm;
    String					weights;
    Instances					data;

    result = null;

    cont = null;
    if (m_InputToken.getPayload() instanceof String) {
      aprops = new adams.core.Properties();
      aprops.load((String) m_InputToken.getPayload());
    }
    else {
      props = (Properties) m_InputToken.getPayload();
      if (props instanceof adams.core.Properties)
	aprops = (adams.core.Properties) props;
      else
	aprops = new adams.core.Properties(props);
    }

    // commandline
    cmdline = null;
    if (aprops.hasKey(m_Setup))
      cmdline = aprops.getProperty(m_Setup);
    else
      result = "Failed to locate property '" + m_Setup + "'!";

    // weights
    weights = null;
    if (aprops.hasKey(m_Weights))
      weights = aprops.getProperty(m_Weights);

    // data
    data = (Instances) getStorageHandler().getStorage().get(m_StorageName);
    if (data == null)
      result = "Failed to obtain training data from '" + m_StorageName + "'!";

    if (result == null) {
      try {
	algorithm = (AbstractClassifierBasedGeneticAlgorithm) OptionUtils.forAnyCommandLine(AbstractClassifierBasedGeneticAlgorithm.class, cmdline);
	if (weights != null)
	  algorithm.setInitialWeights(weights);
	cont      = new WekaGeneticAlgorithmInitializationContainer(algorithm, data);
      }
      catch (Exception e) {
	result = handleException("Failed to instantiate genetic algorithm: " + cmdline, e);
      }
    }

    if (cont != null)
      m_OutputToken = new Token(cont);

    return result;
  }
}
