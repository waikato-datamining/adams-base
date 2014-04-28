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
 * OptionTraverser.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.option.OptionTraverserWithResult;
import adams.core.option.VariableLister;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Traverses the options of the flow at run-time.<br/>
 * Used for debugging purposes.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
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
 * &nbsp;&nbsp;&nbsp;default: OptionTraverser
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
 * <pre>-traverser &lt;adams.core.option.OptionTraverser&gt; (property: traverser)
 * &nbsp;&nbsp;&nbsp;The traverser to use.
 * &nbsp;&nbsp;&nbsp;default: adams.core.option.VariableLister
 * </pre>
 * 
 * <pre>-start &lt;ROOT|PARENT&gt; (property: start)
 * &nbsp;&nbsp;&nbsp;Defines where to start the traversal from.
 * &nbsp;&nbsp;&nbsp;default: ROOT
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OptionTraverser
  extends AbstractSimpleSource {

  /** for serialization. */
  private static final long serialVersionUID = -4834816502537753658L;

  /**
   * Defines where to start the traversal.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum TraversalStart {
    /** from the root of the flow. */
    ROOT,
    /** from the parent of this actor. */
    PARENT
  }
  
  /** the option traverser to use. */
  protected adams.core.option.OptionTraverser m_Traverser;

  /** the start of the traversal. */
  protected TraversalStart m_Start;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Traverses the options of the flow at run-time."
	+ "\nUsed for debugging purposes.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "traverser", "traverser",
	    new VariableLister());

    m_OptionManager.add(
	    "start", "start",
	    TraversalStart.ROOT);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "traverser", m_Traverser.getClass(), "using: ");
    result += QuickInfoHelper.toString(this, "start", m_Start, ", starting from: ");

    return result;
  }

  /**
   * Sets the traverser to use.
   *
   * @param value	the traverser
   */
  public void setTraverser(adams.core.option.OptionTraverser value) {
    m_Traverser = value;
    reset();
  }

  /**
   * Returns the traverser to use.
   *
   * @return		the traverser
   */
  public adams.core.option.OptionTraverser getTraverser() {
    return m_Traverser;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String traverserTipText() {
    return "The traverser to use.";
  }

  /**
   * Sets where to start the traversal.
   *
   * @param value	the starting point
   */
  public void setStart(TraversalStart value) {
    m_Start = value;
    reset();
  }

  /**
   * Returns where to start the traversal.
   *
   * @return		the starting point
   */
  public TraversalStart getStart() {
    return m_Start;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String startTipText() {
    return "Defines where to start the traversal from.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String				result;
    adams.core.option.OptionTraverser	traverser;

    result        = null;
    traverser     = (adams.core.option.OptionTraverser) Utils.deepCopy(m_Traverser);
    m_OutputToken = null;

    if (traverser instanceof OptionTraverserWithResult)
      ((OptionTraverserWithResult) traverser).resetResult();

    switch (m_Start) {
      case ROOT:
	if (getRoot() != null)
	  getRoot().getOptionManager().traverse(traverser);
	else
	  result = "No root available!";
	break;
      case PARENT:
	if (getParent() != null)
	  getParent().getOptionManager().traverse(traverser);
	else
	  result = "No parent available!";
	break;
    }

    if (result == null) {
      if (traverser instanceof OptionTraverserWithResult)
	m_OutputToken = new Token(((OptionTraverserWithResult) traverser).getResult());
    }
    
    return result;
  }
}
