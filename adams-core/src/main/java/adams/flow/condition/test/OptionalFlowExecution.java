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
 * OptionalFlowExecution.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.condition.test;

import adams.core.io.FlowFile;
import adams.flow.FlowRunner;

/**
 <!-- globalinfo-start -->
 * Checks whether a specified file exists. If not, then the specified flow is run. After the flow finishes, the check is performed once again, whether the file exists or not.
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
 * <pre>-file &lt;adams.core.io.PlaceholderFile&gt; (property: file)
 * &nbsp;&nbsp;&nbsp;The file to look for.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-invert (property: invert)
 * &nbsp;&nbsp;&nbsp;If set to true, then the matching sense is inverted.
 * </pre>
 * 
 * <pre>-headless (property: headless)
 * &nbsp;&nbsp;&nbsp;If set to true, the actor is run in headless mode without GUI components.
 * </pre>
 * 
 * <pre>-flow &lt;adams.core.io.FlowFile&gt; (property: flowFile)
 * &nbsp;&nbsp;&nbsp;The flow to run if the file doesn't exist.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OptionalFlowExecution
  extends FileExists {

  /** for serialization. */
  private static final long serialVersionUID = 3297313699016976690L;

  /** the flow to run. */
  protected FlowFile m_FlowFile;

  /** whether the execution is to be headless, i.e., no GUI components. */
  protected boolean m_Headless;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Checks whether a specified file exists. If not, then the specified "
      + "flow is run. After the flow finishes, the check is performed once "
      + "again, whether the file exists or not.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "headless", "headless",
	    false);

    m_OptionManager.add(
	    "flow", "flowFile",
	    new FlowFile("."));
  }

  /**
   * Sets whether the actor is to be run in headless mode, i.e., suppressing
   * GUI components.
   *
   * @param value	if true then GUI components will be suppressed
   */
  public void setHeadless(boolean value) {
    m_Headless = value;
    reset();
  }

  /**
   * Returns whether the actor is run in headless mode.
   *
   * @return		true if GUI components are suppressed
   */
  public boolean isHeadless() {
    return m_Headless;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String headlessTipText() {
    return "If set to true, the actor is run in headless mode without GUI components.";
  }

  /**
   * Sets the flow to run if necessary.
   *
   * @param value	the flow file
   */
  public void setFlowFile(FlowFile value) {
    m_FlowFile = value;
    reset();
  }

  /**
   * Returns the flow to run if necessary.
   *
   * @return		the flow file
   */
  public FlowFile getFlowFile() {
    return m_FlowFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String flowFileTipText() {
    return "The flow to run if the file doesn't exist.";
  }

  /**
   * Performs the actual testing of the condition.
   *
   * @return		the test result, null if everything OK, otherwise
   * 			the error message
   */
  @Override
  protected String performTest() {
    String	result;
    FlowRunner	flow;

    result = null;

    if (!m_File.exists()) {
      flow = new FlowRunner();
      flow.setInput(m_FlowFile);
      flow.setHeadless(isHeadless());
      result = flow.execute();
    }

    if (result == null)
      result = super.performTest();

    return result;
  }
}
