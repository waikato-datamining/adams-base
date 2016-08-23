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
 * ManageInteractiveActors.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.core.Actor;
import adams.flow.core.AutomatableInteractiveActor;

import java.lang.reflect.Method;

/**
 <!-- globalinfo-start -->
 * Enables&#47;disables the interactive behaviour of adams.flow.core.AutomatableInteractiveActor actors.
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
 * <pre>-enable (property: enable)
 * &nbsp;&nbsp;&nbsp;If enabled, the interactive behaviour of actors will get enabled.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ManageInteractiveActors
  extends AbstractModifyingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -3520719602643255362L;
  
  /** whether to enable the interactive behaviour. */
  protected boolean m_Enable;
  
  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Enables/disables the interactive behaviour of " 
	+ AutomatableInteractiveActor.class.getName() + " actors.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "enable", "enable",
	    true);
  }

  /**
   * Sets whether to enable or disable the interactive behaviour.
   *
   * @param value 	if true then interactive behaviour will get enabled
   */
  public void setEnable(boolean value) {
    m_Enable = value;
    reset();
  }

  /**
   * Returns whether the interactive behaviour gets enabled or disabled.
   *
   * @return 		true if the interactive behaviour gets enabled
   */
  public boolean getEnable() {
    return m_Enable;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String enableTipText() {
    return "If enabled, the interactive behaviour of actors will get enabled.";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process
   */
  @Override
  protected void processActor(Actor actor) {
    actor.getOptionManager().traverse(new OptionTraverser() {
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	// ignored
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	if (option.getOptionHandler() instanceof AutomatableInteractiveActor) {
	  if (option.getProperty().equals("nonInteractive")) {
	    Method method = option.getDescriptor().getWriteMethod();
	    try {
	      method.invoke(option.getOptionHandler(), !m_Enable);
	      m_Modified = true;
	    }
	    catch (Exception e) {
	      System.err.println("Failed to update " + option + ": ");
	      e.printStackTrace();
	    }
	  }
	}
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	// ignored
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
        return true;
      }
      public boolean canRecurse(Object obj) {
	return canRecurse(obj.getClass());
      }
    });
  }
}
