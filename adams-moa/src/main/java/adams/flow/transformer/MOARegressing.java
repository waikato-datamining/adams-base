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
 * MOARegressing.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.flow.container.WekaPredictionContainer;
import adams.flow.core.Token;
import weka.core.Instance;

/**
 <!-- globalinfo-start -->
 * Uses a serialized MOA model to perform predictions on the data being passed through.<br>
 * The model can also be obtained from a callable actor, if the model file is pointing to a directory.<br>
 * Optionally, the model can be updated with data being passed through.
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
 * &nbsp;&nbsp;&nbsp;default: MOARegressing
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-model &lt;adams.core.io.PlaceholderFile&gt; (property: modelFile)
 * &nbsp;&nbsp;&nbsp;The model file to load (when not pointing to a directory).
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-model-actor &lt;adams.flow.core.CallableActorReference&gt; (property: modelActor)
 * &nbsp;&nbsp;&nbsp;The callable actor to use for obtaining the model in case serialized model 
 * &nbsp;&nbsp;&nbsp;file points to a directory.
 * &nbsp;&nbsp;&nbsp;default: 
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
 * <pre>-update-model &lt;boolean&gt; (property: updateModel)
 * &nbsp;&nbsp;&nbsp;Whether to update the model with the Instance (in case its class value isn't 
 * &nbsp;&nbsp;&nbsp;missing) after making the prediction.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MOARegressing<T extends moa.classifiers.Classifier & moa.classifiers.Regressor>
  extends AbstractProcessWekaInstanceWithModel<T> {

  /** for serialization. */
  private static final long serialVersionUID = 5781363684886301467L;

  /** whether to output weka.core.Instance objects or PredictionContainers. */
  protected boolean m_OutputInstance;

  /** whether to update the model after making the prediction. */
  protected boolean m_UpdateModel;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Uses a serialized MOA model to perform predictions on the data being "
      + "passed through.\n"
      + "The model can also be obtained from a callable actor, if the model "
      + "file is pointing to a directory.\n"
      + "Optionally, the model can be updated with data being passed through.";
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

    m_OptionManager.add(
	    "update-model", "updateModel",
	    false);
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Model = null;
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
   * Sets whether to update the model after making the prediction (in case the
   * class value isn't missing).
   *
   * @param value	if true then update model
   */
  public void setUpdateModel(boolean value) {
    m_UpdateModel = value;
    reset();
  }

  /**
   * Returns whether update the model after making the prediction (in case the
   * class value isn't missing).
   *
   * @return		true if model gets updated
   */
  public boolean getUpdateModel() {
    return m_UpdateModel;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String updateModelTipText() {
    return "Whether to update the model with the Instance (in case its class value isn't missing) after making the prediction.";
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
    double[]			votes;

    votes = m_Model.getVotesForInstance(inst);
    cont  = new WekaPredictionContainer(
	inst,
	votes[0],
	votes);
    if (m_UpdateModel && !inst.classIsMissing())
      m_Model.trainOnInstance(inst);
    if (m_OutputInstance) {
      inst = (Instance) ((Instance) cont.getValue(WekaPredictionContainer.VALUE_INSTANCE)).copy();
      inst.setClassValue((Double) cont.getValue(WekaPredictionContainer.VALUE_CLASSIFICATION));
      result = new Token(inst);
    }
    else {
      result = new Token(cont.getClone());
    }

    return result;
  }
}
