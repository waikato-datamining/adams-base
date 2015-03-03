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
 * WekaAggregateEvaluations.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.util.Hashtable;

import weka.classifiers.AggregateableEvaluation;
import weka.classifiers.Evaluation;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Aggregates incoming weka.classifiers.Evaluation objects and forwards the current aggregated state.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Evaluation<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;weka.classifiers.Evaluation<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
 * &nbsp;&nbsp;&nbsp;default: WekaAggregateEvaluations
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaAggregateEvaluations
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 3799673803958040769L;

  /** the key for storing the current accumulated error in the backup. */
  public final static String BACKUP_EVALUATION = "evaluation";

  /** the current evaluation state. */
  protected AggregateableEvaluation m_Evaluation;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Aggregates incoming " + Evaluation.class.getName() + " objects "
	+ "and forwards the current aggregated state.";
  }
  
  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Evaluation = null;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();

    pruneBackup(BACKUP_EVALUATION);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    result.put(BACKUP_EVALUATION, m_Evaluation);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_EVALUATION)) {
      m_Evaluation = (AggregateableEvaluation) state.get(BACKUP_EVALUATION);
      state.remove(BACKUP_EVALUATION);
    }

    super.restoreState(state);
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
    return new Class[]{WekaEvaluationContainer.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Evaluation	input;
    
    result = null;
    if (m_InputToken.getPayload() instanceof WekaEvaluationContainer)
      input = (Evaluation) ((WekaEvaluationContainer) m_InputToken.getPayload()).getValue(WekaEvaluationContainer.VALUE_EVALUATION);
    else
      input = (Evaluation) m_InputToken.getPayload();
    
    try {
      if (m_Evaluation == null)
	m_Evaluation = new AggregateableEvaluation(input);
      m_Evaluation.aggregate(input);
      m_OutputToken = new Token(m_Evaluation);
    }
    catch (Exception e) {
      result = handleException("Failed to aggregate evaluation!", e);
    }
    
    return result;
  }
}
