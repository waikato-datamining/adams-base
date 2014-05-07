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
 * WekaPrimeForecaster.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.classifiers.timeseries.AbstractForecaster;
import weka.classifiers.timeseries.core.IncrementallyPrimeable;
import weka.core.Instance;
import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.flow.container.WekaModelContainer;
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
 * Primes a forecaster with the incoming data and outputs the updated forecaster alongside the training header (in a model container).
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br/>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaModelContainer<br/>
 * <p/>
 * Container information:<br/>
 * - adams.flow.container.WekaModelContainer: Model, Header, Dataset
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: WekaPrimeForecaster
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
 * &nbsp;&nbsp;&nbsp;The Weka forecaster to prime on the input data; can be a adams.flow.container.WekaModelContainer 
 * &nbsp;&nbsp;&nbsp;or a weka.classifiers.timeseries.AbstractForecaster.
 * &nbsp;&nbsp;&nbsp;default: WekaForecasterSetup
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaPrimeForecaster
  extends AbstractTransformer 
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -3019442578354930841L;

  /** the name of the callable weka forecaster. */
  protected CallableActorReference m_Forecaster;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Primes a forecaster with the incoming data and outputs the "
      + "updated forecaster alongside the training header (in a model container).";
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
    return 
	"The Weka forecaster to prime on the input data; can be a " 
	+ WekaModelContainer.class.getName() + " or a " + AbstractForecaster.class.getName() + ".";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "forecaster", m_Forecaster);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instances.class, weka.core.Instance.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instances.class, Instance.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.flow.container.WekaModelContainer.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{WekaModelContainer.class};
  }

  /**
   * Returns an instance of the callable forecaster.
   *
   * @return		the forecaster
   */
  protected AbstractForecaster getForecasterInstance() {
    AbstractForecaster	result;
    Object		obj;
    
    result = null;
    
    obj = CallableActorHelper.getSetup(Object.class, m_Forecaster, this);
    if (obj instanceof WekaModelContainer)
      result = (AbstractForecaster) ((WekaModelContainer) obj).getValue(WekaModelContainer.VALUE_MODEL);
    else if (obj instanceof AbstractForecaster)
      result = (AbstractForecaster) obj;
    
    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Instances		data;
    Instance		inst;
    AbstractForecaster	cls;

    result = null;

    try {
      cls = getForecasterInstance();
      if (cls == null)
	result = "Failed to obtain forecaster!";
      
      if (result == null) {
	if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instances)) {
	  data = (Instances) m_InputToken.getPayload();
	  cls.primeForecaster(data);
	  m_OutputToken = new Token(new WekaModelContainer(cls, new Instances(data, 0), data));
	}
	else if ((m_InputToken != null) && (m_InputToken.getPayload() instanceof Instance)) {
	  inst = (Instance) m_InputToken.getPayload();
	  data = inst.dataset();
	  if (cls instanceof IncrementallyPrimeable) {
	    ((IncrementallyPrimeable) cls).primeForecasterIncremental(inst);
	    m_OutputToken = new Token(new WekaModelContainer(cls, new Instances(data, 0), data));
	  }
	  else {
	    result = m_Forecaster.getValue() + " (= " + cls.getClass().getName() + ") does not implement " + IncrementallyPrimeable.class.getName() + "! Cannot prime incrementally!";
	  }
	}
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
