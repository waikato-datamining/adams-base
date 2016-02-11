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
 * WekaForecasting.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.MessageCollection;
import adams.core.QuickInfoHelper;
import adams.core.SerializationHelper;
import adams.core.io.PlaceholderFile;
import adams.flow.container.WekaForecastContainer;
import adams.flow.container.WekaModelContainer;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;
import adams.flow.core.Token;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.timeseries.AbstractForecaster;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Uses a serialized model to perform predictions on the data being passed through.<br>
 * The model can also be obtained from a callable actor, if the model file is pointing to a directory.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaForecastContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaForecastContainer: Model, Forecasts
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
 * &nbsp;&nbsp;&nbsp;default: WekaForecasting
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-model &lt;adams.core.io.PlaceholderFile&gt; (property: modelFile)
 * &nbsp;&nbsp;&nbsp;The model file to load (when not pointing to a directory).
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-model-actor &lt;adams.flow.core.CallableActorReference&gt; (property: modelActor)
 * &nbsp;&nbsp;&nbsp;The callable actor to use for obtaining the model in case serialized model 
 * &nbsp;&nbsp;&nbsp;file points to a directory; can be a adams.flow.container.WekaModelContainer 
 * &nbsp;&nbsp;&nbsp;or a weka.classifiers.timeseries.AbstractForecaster.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-on-the-fly &lt;boolean&gt; (property: onTheFly)
 * &nbsp;&nbsp;&nbsp;If set to true, the model file is not required to be present at set up time 
 * &nbsp;&nbsp;&nbsp;(eg if built on the fly), only at execution time.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-num-steps &lt;int&gt; (property: numSteps)
 * &nbsp;&nbsp;&nbsp;The number of steps to forecast.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaForecasting
  extends AbstractSimpleSource {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the serialized model to load. */
  protected PlaceholderFile m_ModelFile;

  /** the callable actor to get the model from. */
  protected CallableActorReference m_ModelActor;

  /** the model that was loaded from the model file. */
  protected AbstractForecaster m_Model;

  /** whether the model gets built on the fly and might not be present at the start. */
  protected boolean m_OnTheFly;

  /** the number of steps to forecast. */
  protected int m_NumSteps;
  
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
	    "model", "modelFile",
	    new PlaceholderFile("."));

    m_OptionManager.add(
	    "model-actor", "modelActor",
	    new CallableActorReference());

    m_OptionManager.add(
	    "on-the-fly", "onTheFly",
	    false);

    m_OptionManager.add(
	    "num-steps", "numSteps",
	    1, 1, null);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Model = null;
  }

  /**
   * Sets the file to load the model from.
   *
   * @param value	the model file
   */
  public void setModelFile(PlaceholderFile value) {
    m_ModelFile = value;
    reset();
  }

  /**
   * Returns the file to load the model from.
   *
   * @return		the model file
   */
  public PlaceholderFile getModelFile() {
    return m_ModelFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelFileTipText() {
    return "The model file to load (when not pointing to a directory).";
  }

  /**
   * Sets the callable actor to obtain the model from if model file is pointing
   * to a directory.
   *
   * @param value	the actor reference
   */
  public void setModelActor(CallableActorReference value) {
    m_ModelActor = value;
    reset();
  }

  /**
   * Returns the callable actor to obtain the model from if model file is pointing
   * to a directory.
   *
   * @return		the actor reference
   */
  public CallableActorReference getModelActor() {
    return m_ModelActor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelActorTipText() {
    return
        "The callable actor to use for obtaining the model in case serialized "
      + "model file points to a directory; can be a "
      + WekaModelContainer.class.getName() + " or a " + AbstractForecaster.class.getName() + ".";
  }

  /**
   * Sets whether the model file gets built on the fly and might not be present
   * at start up time.
   *
   * @param value	if true then the model does not have to be present at
   * 			start up time
   */
  public void setOnTheFly(boolean value) {
    m_OnTheFly = value;
    reset();
  }

  /**
   * Returns whether the model file gets built on the fly and might not be present
   * at start up time.
   *
   * @return		true if the model is not necessarily present at start
   * 			up time
   */
  public boolean getOnTheFly() {
    return m_OnTheFly;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onTheFlyTipText() {
    return
        "If set to true, the model file is not required to be present at "
      + "set up time (eg if built on the fly), only at execution time.";
  }

  /**
   * Sets the number of steps to forecast.
   *
   * @param value	the steps
   */
  public void setNumSteps(int value) {
    m_NumSteps = value;
    reset();
  }

  /**
   * Returns the number of steps to forecast.
   *
   * @return		the steps
   */
  public int getNumSteps() {
    return m_NumSteps;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numStepsTipText() {
    return "The number of steps to forecast.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result = QuickInfoHelper.toString(this, "modelFile", (m_ModelFile.isDirectory() ? m_ModelActor : m_ModelFile));
    result += QuickInfoHelper.toString(this, "numSteps", m_NumSteps, ", Steps: ");
    
    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.WekaForecastContainer.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{WekaForecastContainer.class};
  }

  /**
   * Loads the model from the model file.
   *
   * @return		null if everything worked, otherwise an error message
   */
  protected String setUpModel() {
    String		result;
    Object		obj;
    MessageCollection	errors;

    result = null;

    if (m_ModelFile.isDirectory()) {
      // obtain model from callable actor
      try {
	errors = new MessageCollection();
	obj    = CallableActorHelper.getSetup(null, m_ModelActor, this, errors);
	if (obj == null) {
	  if (!errors.isEmpty())
	    result = errors.toString();
	}
	else {
	  if (obj instanceof WekaModelContainer)
	    m_Model = (AbstractForecaster) ((WekaModelContainer) obj).getValue(WekaModelContainer.VALUE_MODEL);
	  else if (obj instanceof AbstractForecaster)
	    m_Model = (AbstractForecaster) obj;
	}
      }
      catch (Exception e) {
	m_Model = null;
	result  = handleException("Failed to obtain model from callable actor '" + m_ModelActor + "': ", e);
      }
    }
    else {
      // load model
      try {
	m_Model = (AbstractForecaster) SerializationHelper.read(m_ModelFile.getAbsolutePath());
      }
      catch (Exception e) {
	m_Model = null;
	result  = handleException("Failed to load model from '" + m_ModelFile + "': ", e);
      }
    }

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (!m_OnTheFly)
	result = setUpModel();
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    WekaForecastContainer		cont;
    List<List<NumericPrediction>>	forecasts;

    result = null;

    if (m_OnTheFly && (m_Model == null)) {
      result = setUpModel();
      if (result != null)
	return result;
    }

    try {
      forecasts = m_Model.forecast(m_NumSteps);
      cont = new WekaForecastContainer(m_Model, forecasts);
      m_OutputToken = new Token(cont);
    }
    catch (Exception e) {
      result = handleException("Failed to forecast!", e);
    }

    return result;
  }
}
