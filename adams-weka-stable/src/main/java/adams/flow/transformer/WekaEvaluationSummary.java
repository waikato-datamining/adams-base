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
 * WekaEvaluationSummary.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;

import adams.core.Shortening;
import weka.classifiers.Evaluation;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.base.BaseText;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Generates a summary string of the weka.classifiers.Evaluation objects that it receives.
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
 * - adams.flow.container.WekaEvaluationContainer: Evaluation, Model
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
 * &nbsp;&nbsp;&nbsp;default: WekaEvaluationSummary
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
 * <pre>-relation (property: outputRelationName)
 * &nbsp;&nbsp;&nbsp;If set to true, then the relation name of the dataset is output as well.
 * </pre>
 * 
 * <pre>-confusion-matrix (property: confusionMatrix)
 * &nbsp;&nbsp;&nbsp;If set to true, then the confusion matrix will be output as well.
 * </pre>
 * 
 * <pre>-complexity-stats (property: complexityStatistics)
 * &nbsp;&nbsp;&nbsp;If set to true, then the complexity statistics will be output as well.
 * </pre>
 * 
 * <pre>-class-details (property: classDetails)
 * &nbsp;&nbsp;&nbsp;If set to true, then the class details are output as well.
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
public class WekaEvaluationSummary
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 8082115424369061977L;

  /** whether to print the relation name of the dataset a well. */
  protected boolean m_OutputRelationName;

  /** whether to print the confusion matrix as well. */
  protected boolean m_ConfusionMatrix;

  /** whether to print the complexity statistics as well. */
  protected boolean m_ComplexityStatistics;

  /** whether to print the class details as well. */
  protected boolean m_ClassDetails;

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
        "Generates a summary string of the weka.classifiers.Evaluation objects "
      + "that it receives.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "relation", "outputRelationName",
	    false);

    m_OptionManager.add(
	    "confusion-matrix", "confusionMatrix",
	    false);

    m_OptionManager.add(
	    "complexity-stats", "complexityStatistics",
	    false);

    m_OptionManager.add(
	    "class-details", "classDetails",
	    false);

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
    String		result;
    ArrayList<String>	options;
    String		value;

    options = new ArrayList<String>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "outputRelationName", m_OutputRelationName, "output relation"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "confusionMatrix", m_ConfusionMatrix, "confusion matrix"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "classDetails", m_ClassDetails, "class details"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "complexityStatistics", m_ComplexityStatistics, "complexity stats"));
    result = QuickInfoHelper.flatten(options);
    
    value = QuickInfoHelper.toString(this, "comment", (m_Comment.stringValue().length() > 0 ? Shortening.shortenEnd(m_Comment.stringValue(), 20) : null));
    if (value != null) {
      if (result.length() > 0)
	result += ", ";
      result += "comment: " + value;
    }
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.classifiers.Evaluation.class, adams.flow.container.WekaEvaluationContainer.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Evaluation.class, WekaEvaluationContainer.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.String.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{String.class};
  }

  /**
   * Sets whether to output the relation name as well.
   *
   * @param value	if true then the relation name is output as well
   */
  public void setOutputRelationName(boolean value) {
    m_OutputRelationName = value;
    reset();
  }

  /**
   * Returns whether the relation name is output as well.
   *
   * @return		true if the relation name is output as well
   */
  public boolean getOutputRelationName() {
    return m_OutputRelationName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputRelationNameTipText() {
    return "If set to true, then the relation name of the dataset is output as well.";
  }

  /**
   * Sets whether to output the confusion matrix as well.
   *
   * @param value	if true then the confusion matrix will be output as well
   */
  public void setConfusionMatrix(boolean value) {
    m_ConfusionMatrix = value;
    reset();
  }

  /**
   * Returns whether to output the confusion matrix as well.
   *
   * @return		true if the confusion matrix stats are output as well
   */
  public boolean getConfusionMatrix() {
    return m_ConfusionMatrix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String confusionMatrixTipText() {
    return "If set to true, then the confusion matrix will be output as well.";
  }

  /**
   * Sets whether to output complexity stats as well.
   *
   * @param value	if true then the complexity stats will be output as well
   */
  public void setComplexityStatistics(boolean value) {
    m_ComplexityStatistics = value;
    reset();
  }

  /**
   * Returns whether the complexity stats are output as well.
   *
   * @return		true if the complexity stats are output as well
   */
  public boolean getComplexityStatistics() {
    return m_ComplexityStatistics;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String complexityStatisticsTipText() {
    return "If set to true, then the complexity statistics will be output as well.";
  }

  /**
   * Sets whether the class details are output as well.
   *
   * @param value	if true then the class details are output as well
   */
  public void setClassDetails(boolean value) {
    m_ClassDetails = value;
    reset();
  }

  /**
   * Returns whether the class details are output as well.
   *
   * @return		true if the class details are output as well
   */
  public boolean getClassDetails() {
    return m_ClassDetails;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classDetailsTipText() {
    return "If set to true, then the class details are output as well.";
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
    Evaluation		eval;
    StringBuilder	buffer;
    boolean		prolog;
    String[]		comment;

    result = null;

    if (m_InputToken.getPayload() instanceof WekaEvaluationContainer)
      eval = (Evaluation) ((WekaEvaluationContainer) m_InputToken.getPayload()).getValue(WekaEvaluationContainer.VALUE_EVALUATION);
    else
      eval = (Evaluation) m_InputToken.getPayload();
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

    // relation name
    if (m_OutputRelationName) {
      buffer.append("Relation: " + eval.getHeader().relationName() + "\n");
      prolog = true;
    }

    // separator
    if (prolog)
      buffer.append("\n");

    // summary
    buffer.append(eval.toSummaryString(m_ComplexityStatistics));

    // confusion matrix
    if (m_ConfusionMatrix) {
      try {
	buffer.append("\n\n" + eval.toMatrixString());
      }
      catch (Exception e) {
	result = handleException("Failed to generate confusion matrix: ", e);
      }
    }

    // class details
    if (m_ClassDetails) {
      try {
	buffer.append("\n\n" + eval.toClassDetailsString());
      }
      catch (Exception e) {
	result = handleException("Failed to generate class details: ", e);
      }
    }

    m_OutputToken = new Token(buffer.toString());

    return result;
  }
}
