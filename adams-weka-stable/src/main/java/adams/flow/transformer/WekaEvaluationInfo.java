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

/**
 * WekaEvaluationInfo.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.DataInfoActor;
import adams.flow.core.Token;
import weka.classifiers.Evaluation;
import weka.core.Instances;

/**
 <!-- globalinfo-start -->
 * Outputs information about a Weka weka.classifiers.Evaluation object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Evaluation<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaEvaluationContainer<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaEvaluationContainer: Evaluation, Model, Prediction output, Original indices
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
 * &nbsp;&nbsp;&nbsp;default: WekaEvaluationInfo
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
 * <pre>-type &lt;RELATION_NAME|HEADER|CLASS_ATTRIBUTE_NAME|PREDICTIONS_RECORDED|NUM_PREDICTIONS&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of information to generate.
 * &nbsp;&nbsp;&nbsp;default: RELATION_NAME
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaEvaluationInfo
  extends AbstractTransformer
  implements DataInfoActor {

  private static final long serialVersionUID = -4898802545222942458L;

  /**
   * The type of information to output.
   */
  public enum InfoType {
    RELATION_NAME,
    HEADER,
    CLASS_ATTRIBUTE_NAME,
    PREDICTIONS_RECORDED,
    NUM_PREDICTIONS,
  }

  /** the type of information to generate. */
  protected InfoType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs information about a Weka " + Evaluation.class.getName() + " object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "type", "type",
      InfoType.RELATION_NAME);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "type", m_Type);
  }

  /**
   * Sets the type of information to generate.
   *
   * @param value	the type
   */
  public void setType(InfoType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of information to generate.
   *
   * @return		the type
   */
  public InfoType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of information to generate.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Evaluation.class, WekaEvaluationContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    switch (m_Type) {
      case RELATION_NAME:
      case CLASS_ATTRIBUTE_NAME:
	return new Class[]{String.class};
      case HEADER:
	return new Class[]{Instances.class};
      case PREDICTIONS_RECORDED:
	return new Class[]{Boolean.class};
      case NUM_PREDICTIONS:
	return new Class[]{Integer.class};
      default:
	throw new IllegalStateException("Unhandled info type: " + m_Type);
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    Evaluation	eval;

    if (m_InputToken.getPayload() instanceof Evaluation)
      eval = (Evaluation) m_InputToken.getPayload();
    else
      eval = (Evaluation) ((WekaEvaluationContainer) m_InputToken.getPayload()).getValue(WekaEvaluationContainer.VALUE_EVALUATION);

    switch (m_Type) {
      case RELATION_NAME:
	m_OutputToken = new Token(eval.getHeader().relationName());
	break;
      case CLASS_ATTRIBUTE_NAME:
	m_OutputToken = new Token(eval.getHeader().classAttribute().name());
	break;
      case HEADER:
	m_OutputToken = new Token(eval.getHeader());
	break;
      case PREDICTIONS_RECORDED:
	m_OutputToken = new Token(eval.predictions() != null);
	break;
      case NUM_PREDICTIONS:
	if (eval.predictions() == null)
	  m_OutputToken = new Token(-1);
	else
	  m_OutputToken = new Token(eval.predictions().size());
	break;
      default:
	throw new IllegalStateException("Unhandled info type: " + m_Type);
    }

    return null;
  }
}
