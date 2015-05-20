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
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.List;

import weka.classifiers.AbstainingClassifier;
import weka.classifiers.RangeCheckClassifier;
import weka.core.Instance;
import adams.core.Utils;
import adams.data.statistics.StatUtils;
import adams.flow.container.WekaPredictionContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Uses a serialized model to perform predictions on the data being passed through.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.PredictionContainer<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: Classifying
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-model &lt;adams.core.io.PlaceholderFile&gt; (property: modelFile)
 * &nbsp;&nbsp;&nbsp;The model file to load.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-on-the-fly (property: onTheFly)
 * &nbsp;&nbsp;&nbsp;If set to true, the model file is not required to be present at set up time
 * &nbsp;&nbsp;&nbsp;(eg if built on the fly), only at execution time.
 * </pre>
 *
 * <pre>-output-instance (property: outputInstance)
 * &nbsp;&nbsp;&nbsp;Whether to output weka.core.Instance objects or PredictionContainer objects.
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
      + "The model can also be obtained from a callable actor, if the model "
      + "file is pointing to a directory.";
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
   * @return		<!-- flow-generates-start -->adams.flow.container.PredictionContainer.class, weka.core.Instance.class<!-- flow-generates-end -->
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
