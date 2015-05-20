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
 * WekaAttributeSelectionSummary.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.attributeSelection.AttributeSelection;
import adams.flow.container.WekaAttributeSelectionContainer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Outputs a summary string of the attribute selection.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.container.WekaAttributeSelectionContainer<br>
 * &nbsp;&nbsp;&nbsp;weka.attributeSelection.AttributeSelection<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 * Container information:<br>
 * - adams.flow.container.WekaAttributeSelectionContainer: Train, Reduced, Transformed, Evaluation, Statistics, Seed, FoldCount
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
 * &nbsp;&nbsp;&nbsp;default: WekaAttributeSelectionSummary
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
public class WekaAttributeSelectionSummary
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 4145361817914402084L;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Outputs a summary string of the attribute selection.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.container.WekaAttributeSelectionContainer.class, weka.attributeSelection.AttributeSelection.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{WekaAttributeSelectionContainer.class, AttributeSelection.class};
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    AttributeSelection			eval;
    boolean				crossValidation;
    WekaAttributeSelectionContainer	cont;

    result = null;

    if (m_InputToken.getPayload() instanceof AttributeSelection) {
      eval = (AttributeSelection) m_InputToken.getPayload();
      try {
	eval.CVResultsString();  // throws an exception if no CV performed
	crossValidation = true;
      }
      catch (Exception e) {
	crossValidation = false;
      }
    }
    else {
      cont            = (WekaAttributeSelectionContainer) m_InputToken.getPayload();
      eval            = (AttributeSelection) cont.getValue(WekaAttributeSelectionContainer.VALUE_EVALUATION);
      crossValidation = cont.hasValue(WekaAttributeSelectionContainer.VALUE_FOLD_COUNT);
    }

    try {
      if (crossValidation)
	m_OutputToken = new Token(eval.CVResultsString());
      else
	m_OutputToken = new Token(eval.toResultsString());
    }
    catch (Exception e) {
      result = handleException("Failed to generate summary string!", e);
    }

    return result;
  }
}
