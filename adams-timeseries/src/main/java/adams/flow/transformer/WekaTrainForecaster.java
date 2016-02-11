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
 * WekaTrainForecaster.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.MessageCollection;
import weka.classifiers.timeseries.AbstractForecaster;
import weka.classifiers.timeseries.core.TSLagUser;
import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.flow.container.WekaForecastModelContainer;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;
import adams.flow.source.WekaForecasterSetup;

/**
 <!-- globalinfo-start -->
 * Trains a forecaster based on the incoming dataset and outputs the built model alongside the training header (in a model container).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaForecastModelContainer<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaForecastModelContainer: Model, Header, Dataset, Transformed
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
 * &nbsp;&nbsp;&nbsp;default: WekaTrainForecaster
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
 * <pre>-forecaster &lt;adams.flow.core.CallableActorReference&gt; (property: forecaster)
 * &nbsp;&nbsp;&nbsp;The Weka forecaster to train on the input data.
 * &nbsp;&nbsp;&nbsp;default: WekaForecasterSetup
 * </pre>
 * 
 * <pre>-store-transformed &lt;boolean&gt; (property: storeTransformed)
 * &nbsp;&nbsp;&nbsp;If enabled, the transformed data is stored as well in the output container 
 * &nbsp;&nbsp;&nbsp;in case the forecaster implements weka.classifiers.timeseries.core.TSLagUser.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaTrainForecaster
  extends AbstractTransformer 
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;
  
  /** the name of the callable weka forecaster. */
  protected CallableActorReference m_Forecaster;

  /** whether to store the transformed data as well. */
  protected boolean m_StoreTransformed;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Trains a forecaster based on the incoming dataset and outputs the "
      + "built model alongside the training header (in a model container).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "forecaster", "forecaster",
	    new CallableActorReference(WekaForecasterSetup.class.getSimpleName()));

    m_OptionManager.add(
	    "store-transformed", "storeTransformed",
	    false);
  }

  /**
   * Sets the name of the callable forecaster to use.
   *
   * @param value	the name
   */
  public void setForecaster(CallableActorReference value) {
    m_Forecaster = value;
    reset();
  }

  /**
   * Returns the name of the callable forecaster in use.
   *
   * @return		the name
   */
  public CallableActorReference getForecaster() {
    return m_Forecaster;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String forecasterTipText() {
    return "The Weka forecaster to train on the input data.";
  }

  /**
   * Sets whether to store the transformed data as well.
   *
   * @param value	true if store the transformed data
   */
  public void setStoreTransformed(boolean value) {
    m_StoreTransformed = value;
    reset();
  }

  /**
   * Returns whether to store the transformed data as well.
   *
   * @return		true if transformed data stored
   */
  public boolean getStoreTransformed() {
    return m_StoreTransformed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storeTransformedTipText() {
    return 
	"If enabled, the transformed data is stored as well in the output "
	+ "container in case the forecaster implements " + TSLagUser.class.getName() + ".";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;
    
    result = QuickInfoHelper.toString(this, "forecaster", m_Forecaster);
    value  = QuickInfoHelper.toString(this, "storeTransformed", m_StoreTransformed, "store transformed", ", ");
    if (value != null)
      result += value;
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.WekaForecastModelContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{WekaForecastModelContainer.class};
  }

  /**
   * Returns an instance of the callable forecaster.
   *
   * @return		the forecaster
   */
  protected AbstractForecaster getForecasterInstance() {
    AbstractForecaster	result;
    MessageCollection	errors;

    errors = new MessageCollection();
    result = (AbstractForecaster) CallableActorHelper.getSetup(AbstractForecaster.class, m_Forecaster, this, errors);
    if (result == null) {
      if (!errors.isEmpty())
	getLogger().severe(errors.toString());
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
    String			result;
    Instances			data;
    AbstractForecaster		cls;
    WekaForecastModelContainer	cont;

    result = null;

    try {
      cls = getForecasterInstance();
      if (cls == null) {
	result = "Failed to obtain forecaster!";
	return result;
      }
      if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instances)) {
	data = (Instances) m_InputToken.getPayload();
	cls.buildForecaster(data);
	cont = new WekaForecastModelContainer(cls, new Instances(data, 0), data);
	if (m_StoreTransformed) {
	  if (cls instanceof TSLagUser)
	    cont.setValue(WekaForecastModelContainer.VALUE_TRANSFORMED, ((TSLagUser) cls).getTSLagMaker().getTransformedData(data));
	}
	m_OutputToken = new Token(cont);
      }
    }
    catch (Exception e) {
      m_OutputToken = null;
      result        = handleException("Failed to process data:", e);
    }

    if (m_OutputToken != null)
      updateProvenance(m_OutputToken);
    
    return result;
  }

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled()) {
      if (m_InputToken.hasProvenance())
	cont.setProvenance(m_InputToken.getProvenance().getClone());
      cont.addProvenance(new ProvenanceInformation(ActorType.MODEL_GENERATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
    }
  }
}
