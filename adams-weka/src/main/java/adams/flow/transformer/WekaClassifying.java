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
 * WekaClassifying.java
 * Copyright (C) 2009-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Utils;
import adams.data.statistics.StatUtils;
import adams.flow.container.WekaPredictionContainer;
import adams.flow.core.AbstractModelLoader;
import adams.flow.core.Token;
import adams.flow.core.WekaClassifierModelLoader;
import weka.classifiers.AbstainingClassifier;
import weka.classifiers.RangeCheckClassifier;
import weka.core.Instance;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses a serialized model to perform predictions on the data being passed through.<br>
 * The following order is used to obtain the model (when using AUTO):<br>
 * 1. model file present?<br>
 * 2. source actor present?<br>
 * 3. storage item present?
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaPredictionContainer<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaPredictionContainer: Instance, Classification, Classification label, Distribution, Range check, Abstention classification, Abstention classification label, Abstention distribution
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
 * &nbsp;&nbsp;&nbsp;default: WekaClassifying
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
 * <pre>-model-loading-type &lt;AUTO|FILE|SOURCE_ACTOR|STORAGE&gt; (property: modelLoadingType)
 * &nbsp;&nbsp;&nbsp;Determines how to load the model, in case of AUTO, first the model file
 * &nbsp;&nbsp;&nbsp;is checked, then the callable actor and then the storage.
 * &nbsp;&nbsp;&nbsp;default: AUTO
 * </pre>
 *
 * <pre>-model &lt;adams.core.io.PlaceholderFile&gt; (property: modelFile)
 * &nbsp;&nbsp;&nbsp;The file to load the model from, ignored if pointing to a directory.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 *
 * <pre>-model-actor &lt;adams.flow.core.CallableActorReference&gt; (property: modelActor)
 * &nbsp;&nbsp;&nbsp;The callable actor (source) to obtain the model from, ignored if not present.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-model-storage &lt;adams.flow.control.StorageName&gt; (property: modelStorage)
 * &nbsp;&nbsp;&nbsp;The storage item to obtain the model from, ignored if not present.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-on-the-fly &lt;boolean&gt; (property: onTheFly)
 * &nbsp;&nbsp;&nbsp;If set to true, the model file is not required to be present at set up time
 * &nbsp;&nbsp;&nbsp;(eg if built on the fly), only at execution time.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-use-model-reset-variable &lt;boolean&gt; (property: useModelResetVariable)
 * &nbsp;&nbsp;&nbsp;If enabled, chnages to the specified variable are monitored in order to
 * &nbsp;&nbsp;&nbsp;reset the model, eg when a storage model changed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-model-reset-variable &lt;adams.core.VariableName&gt; (property: modelResetVariable)
 * &nbsp;&nbsp;&nbsp;The variable to monitor for changes in order to reset the model, eg when
 * &nbsp;&nbsp;&nbsp;a storage model changed.
 * &nbsp;&nbsp;&nbsp;default: variable
 * </pre>
 *
 * <pre>-output-instance &lt;boolean&gt; (property: outputInstance)
 * &nbsp;&nbsp;&nbsp;Whether to output weka.core.Instance objects or PredictionContainer objects.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClassifying
  extends AbstractProcessWekaInstanceWithModel<weka.classifiers.Classifier> {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** whether to output weka.core.Instance objects or PredictionContainers. */
  protected boolean m_OutputInstance;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Uses a serialized model to perform predictions on the data being "
        + "passed through.\n"
        + m_ModelLoader.automaticOrderInfo();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output-instance", "outputInstance",
	    false);
  }

  /**
   * Instantiates the model loader to use.
   *
   * @return		the model loader to use
   */
  @Override
  protected AbstractModelLoader newModelLoader() {
    return new WekaClassifierModelLoader();
  }

  /**
   * Sets whether to output Instance objects instead of PredictionContainer
   * ones.
   *
   * @param value	if true then Instance objects are output
   */
  public void setOutputInstance(boolean value) {
    m_OutputInstance = value;
    reset();
  }

  /**
   * Returns whether Instance objects are output instead of PredictionContainer
   * ones.
   *
   * @return		true if Instance objects are output
   */
  public boolean getOutputInstance() {
    return m_OutputInstance;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputInstanceTipText() {
    return "Whether to output weka.core.Instance objects or PredictionContainer objects.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.WekaPredictionContainer.class, weka.core.Instance.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{WekaPredictionContainer.class, Instance.class};
  }

  /**
   * Processes the instance and generates the output token.
   *
   * @param inst	the instance to process
   * @return		the generated output token (e.g., container)
   * @throws Exception	if processing fails
   */
  @Override
  protected Token processInstance(Instance inst) throws Exception {
    Token			result;
    WekaPredictionContainer	cont;
    List<String>		rangeChecks;
    String			rangeCheck;
    AbstainingClassifier	abstain;
    double			classification;
    double[]			distribution;
    
    
    // does the classifier support range checks?
    rangeCheck = null;
    if (m_Model instanceof RangeCheckClassifier) {
      rangeChecks = ((RangeCheckClassifier) m_Model).checkRangeForInstance(inst);
      if (rangeChecks.size() > 0)
	rangeCheck = Utils.flatten(rangeChecks, "\n");
    }

    if (inst.classAttribute().isNumeric()) {
      classification = m_Model.classifyInstance(inst);
      distribution   = new double[]{classification};
    }
    else {
      distribution   = m_Model.distributionForInstance(inst);
      classification = StatUtils.maxIndex(distribution);
      if (distribution[(int) Math.round(classification)] == 0)
	classification = weka.core.Utils.missingValue();
    }
    cont = new WekaPredictionContainer(inst, classification, distribution, rangeCheck);
    
    // abstaining classifier?
    if (m_Model instanceof AbstainingClassifier) {
      abstain = (AbstainingClassifier) m_Model;
      if (abstain.canAbstain()) {
	if (inst.classAttribute().isNumeric()) {
	  classification = abstain.getAbstentionClassification(inst);
	  distribution   = new double[]{classification};
	}
	else {
	  distribution   = abstain.getAbstentionDistribution(inst);
	  classification = StatUtils.maxIndex(distribution);
	  if (distribution[(int) Math.round(classification)] == 0)
	    classification = weka.core.Utils.missingValue();
	}
	cont.setValue(WekaPredictionContainer.VALUE_ABSTENTION_CLASSIFICATION, classification);
	if (inst.classAttribute().isNominal() && !weka.core.Utils.isMissingValue(classification))
	  cont.setValue(WekaPredictionContainer.VALUE_ABSTENTION_CLASSIFICATION_LABEL, inst.classAttribute().value((int) Math.round(classification)));
	cont.setValue(WekaPredictionContainer.VALUE_ABSTENTION_DISTRIBUTION, distribution);
      }
    }
    
    if (m_OutputInstance) {
      inst = (Instance) ((Instance) cont.getValue(WekaPredictionContainer.VALUE_INSTANCE)).copy();
      inst.setClassValue((Double) cont.getValue(WekaPredictionContainer.VALUE_CLASSIFICATION));
      result = new Token(inst);
    }
    else {
      result = new Token((WekaPredictionContainer) cont);
    }

    return result;
  }
}
