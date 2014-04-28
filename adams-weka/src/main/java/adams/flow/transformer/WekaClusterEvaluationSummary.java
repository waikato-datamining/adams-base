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
 * WekaClusterEvaluationSummary.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.clusterers.ClusterEvaluation;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseText;
import adams.flow.container.WekaClusterEvaluationContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Generates a summary string of the weka.clusterers.ClusterEvaluation objects that it receives.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;weka.clusterers.ClusterEvaluation<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaClusterEvaluationContainer<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * <p/>
 * Container information:<br/>
 * - adams.flow.container.WekaClusterEvaluationContainer: Evaluation, Model, Log-likelohood
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
 * &nbsp;&nbsp;&nbsp;default: WekaClusterEvaluationSummary
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
 * <pre>-comment &lt;adams.core.base.BaseText&gt; (property: comment)
 * &nbsp;&nbsp;&nbsp;An optional comment to output in the summary.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaClusterEvaluationSummary
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8082115424369061977L;

  /** an optional comment to output. */
  protected BaseText m_Comment;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Generates a summary string of the weka.clusterers.ClusterEvaluation objects "
      + "that it receives.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "comment", "comment",
	    new BaseText(""));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "comment", (m_Comment.stringValue().length() > 0 ? Utils.shorten(m_Comment.stringValue(), 20) : null));
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.clusterers.ClusterEvaluation.class, adams.flow.container.WekaClusterEvaluationContainer.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    return new Class[]{ClusterEvaluation.class, WekaClusterEvaluationContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Sets the comment to output in the summary.
   *
   * @param value	the comment
   */
  public void setComment(BaseText value) {
    m_Comment = value;
    reset();
  }

  /**
   * Returns the comment to output in the summary.
   *
   * @return		the comment
   */
  public BaseText getComment() {
    return m_Comment;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String commentTipText() {
    return "An optional comment to output in the summary.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    ClusterEvaluation	eval;
    StringBuilder	buffer;
    boolean		prolog;
    Double		log;
    String[]		comment;

    result = null;

    eval = null;
    log  = null;
    if (m_InputToken.getPayload() instanceof WekaClusterEvaluationContainer) {
      eval = (ClusterEvaluation) ((WekaClusterEvaluationContainer) m_InputToken.getPayload()).getValue(WekaClusterEvaluationContainer.VALUE_EVALUATION);
      if (eval == null)
	log = (Double) ((WekaClusterEvaluationContainer) m_InputToken.getPayload()).getValue(WekaClusterEvaluationContainer.VALUE_LOGLIKELIHOOD);
    }
    else {
      eval = (ClusterEvaluation) m_InputToken.getPayload();
    }
    buffer = new StringBuilder();
    prolog = false;

    // comments
    if (m_Comment.getValue().length() > 0) {
      comment = m_Comment.getValue().split("\n");
      if (comment.length == 1) {
	buffer.append("Comment: " + m_Comment + "\n");
      }
      else {
	buffer.append("Comment:\n");
	for (String line: comment)
	  buffer.append(line + "\n");
      }
      prolog = true;
    }

    // separator
    if (prolog)
      buffer.append("\n");

    // summary
    if (eval != null)
      buffer.append(eval.clusterResultsToString());
    else if (log != null)
      buffer.append("Log-likelihood: " + Utils.doubleToString(log, 6));

    m_OutputToken = new Token(buffer.toString());

    return result;
  }
}
