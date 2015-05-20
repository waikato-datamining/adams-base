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
 * HasInterface.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.ClassLocator;
import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Evaluates to 'true' if the objects implement the specified interface.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-interface &lt;java.lang.String&gt; (property: interface)
 * &nbsp;&nbsp;&nbsp;The interface class that the objects must implement.
 * &nbsp;&nbsp;&nbsp;default: java.lang.Object
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HasInterface
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = 912094591109587908L;

  /** the superclass that the objects must be derived from. */
  protected String m_Interface;
  
  /** the actual interface class. */
  protected Class m_Class;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates to 'true' if the objects implement the specified interface.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "interface", "interface",
	    Cloneable.class.getName());
  }

  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Class = null;
  }
  
  /**
   * Sets the directory to look for.
   *
   * @param value	the directory
   */
  public void setInterface(String value) {
    m_Interface = value;
    reset();
  }

  /**
   * Returns the directory to look for.
   *
   * @return		the directory
   */
  public String getInterface() {
    return m_Interface;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String interfaceTipText() {
    return "The interface class that the objects must implement.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "interface", m_Interface, "interface: ");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		Unknown
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Configures the condition.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp(Actor owner) {
    String	result;

    result = super.setUp(owner);

    if (result == null) {
      if ((m_Interface == null) || (m_Interface.trim().length() == 0)) {
	result = "No interface class provided!";
      }
      else {
	try {
	  m_Class = Class.forName(m_Interface);
	}
	catch (Exception e) {
	  result = "Failed to load interface class: " + e;
	}
      }
    }

    return result;
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    if ((token != null) && (token.getPayload() != null))
      return ClassLocator.hasInterface(m_Class, token.getPayload().getClass());
    else
      return false;
  }
}
