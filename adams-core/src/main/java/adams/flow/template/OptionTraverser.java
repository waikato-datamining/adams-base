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
 * OptionTraverser.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.template;

import adams.core.option.VariableLister;
import adams.flow.control.Once;
import adams.flow.control.Trigger;
import adams.flow.core.Actor;
import adams.flow.core.MutableActorHandler;
import adams.flow.sink.Display;
import adams.flow.sink.HistoryDisplay;
import adams.flow.source.OptionTraverser.TraversalStart;

/**
 <!-- globalinfo-start -->
 * Generates a sub-flow that displays the result of the specified option traversal algorithm, e.g., for displaying currently attached variables.
 * <br><br>
 <!-- globalinfo-end -->
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
 * &nbsp;&nbsp;&nbsp;The new name for the actor; leave empty to use current.
 * &nbsp;&nbsp;&nbsp;default: 
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
 * <pre>-once (property: once)
 * &nbsp;&nbsp;&nbsp;If enabled, the option traversal gets executed only once.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OptionTraverser
  extends AbstractActorTemplate {

  /** for serialization. */
  private static final long serialVersionUID = 7111338348242418621L;

  /** the traverser to use. */
  protected adams.core.option.OptionTraverser m_Traverser;

  /** the start of the traversal. */
  protected TraversalStart m_Start;

  /** whether to execute the traversal only once. */
  protected boolean m_Once;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Generates a sub-flow that displays the result of the specified option "
	+ "traversal algorithm, e.g., for displaying currently attached variables.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "traverser", "traverser",
	    new VariableLister());

    m_OptionManager.add(
	    "start", "start",
	    TraversalStart.ROOT);

    m_OptionManager.add(
	    "once", "once",
	    false);
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
   * Sets whether to execute the traversal only once.
   *
   * @param value	true if only once
   */
  public void setOnce(boolean value) {
    m_Once = value;
    reset();
  }

  /**
   * Returns whether to execute the traversal only once.
   *
   * @return		true if only once
   */
  public boolean getOnce() {
    return m_Once;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String onceTipText() {
    return "If enabled, the option traversal gets executed only once.";
  }

  /**
   * Adds the display actors.
   * 
   * @param handler	the handler to add the display actors to
   */
  protected void addDisplay(MutableActorHandler handler) {
    if (m_Once)
      handler.add(new Display());
    else
      handler.add(new HistoryDisplay());
  }
  
  /**
   * Generates the actor.
   *
   * @return 		the generated acto
   */
  protected Actor doGenerate() {
    Actor 				result;
    Trigger				trigger;
    Once				once;
    adams.flow.source.OptionTraverser	traverser;

    trigger = new Trigger();
    
    traverser = new adams.flow.source.OptionTraverser();
    traverser.setTraverser(m_Traverser);
    traverser.setStart(m_Start);
    trigger.add(traverser);

    addDisplay(trigger);

    if (m_Once) {
      once = new Once();
      once.add(trigger);
      result = once;
    }
    else {
      result = trigger;
    }
    
    return result;
  }
}
