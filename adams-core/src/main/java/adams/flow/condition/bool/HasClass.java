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
 * HasClass.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.flow.core.Actor;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Evaluates to 'true' if the specified class is available on the classpath.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-class-name &lt;java.lang.String&gt; (property: className)
 * &nbsp;&nbsp;&nbsp;default: java.lang.Object
 * </pre>
 * 
Missing tooltip: adams.flow.condition.bool.HasClass.classNameTipText
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 15570 $
 */
public class HasClass
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = 912094591109587908L;

  /** the class to check. */
  protected String m_ClassName;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Evaluates to 'true' if the specified class is available on the classpath.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "class-name", "className",
	    Object.class.getName());
  }

  /**
   * Sets the class to look for.
   *
   * @param value	the class name
   */
  public void setClassName(String value) {
    m_ClassName = value;
    reset();
  }

  /**
   * Returns the class to look for.
   *
   * @return		the class name
   */
  public String getClassName() {
    return m_ClassName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String superClassTipText() {
    return "The class to look for.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "className", m_ClassName, "class: ");
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
      if ((m_ClassName == null) || (m_ClassName.trim().length() == 0))
	result = "No class provided!";
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
    try {
      Class.forName(m_ClassName);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }
}
