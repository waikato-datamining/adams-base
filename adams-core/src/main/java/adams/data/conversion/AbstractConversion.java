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
 * AbstractConversion.java
 * Copyright (C) 2011-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.AdditionalInformationHandler;
import adams.core.CleanUpHandler;
import adams.core.ShallowCopySupporter;
import adams.core.Utils;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.flow.core.Actor;
import adams.flow.core.Unknown;

import java.util.logging.Level;

/**
 * Ancestor for all conversions.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractConversion
  extends AbstractOptionHandler
  implements Conversion, CleanUpHandler, ShallowCopySupporter<AbstractConversion>, AdditionalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 1576117521811422376L;

  /** the owner. */
  protected Object m_Owner;

  /** the input data. */
  protected Object m_Input;

  /** the generated output. */
  protected Object m_Output;

  /** whether the conversion was stopped. */
  protected boolean m_Stopped;

  /**
   * Returns the additional information.
   *
   * @return		the additional information, null or 0-length string for no information
   */
  public String getAdditionalInformation() {
    StringBuilder	result;

    result = new StringBuilder();

    result.append("Conversion input/output:");
    result.append("\n- input: ");
    result.append(Utils.classToString(accepts()));
    result.append("\n- output: ");
    result.append(Utils.classToString(generates()));

    return result.toString();
  }

  /**
   * Resets the converter.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Output = null;
  }

  /**
   * Sets the owner of this conversion.
   *
   * @param value	the owner
   */
  @Override
  public void setOwner(Object value) {
    m_Owner = value;
    reset();
  }

  /**
   * Returns the owner of this conversion.
   *
   * @return		the owner, null if none set
   */
  @Override
  public Object getOwner() {
    return m_Owner;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br><br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public abstract Class accepts();

  /**
   * Sets the original data to convert.
   * For converters implementing {@link StreamConversion} this only resets
   * the m_Output variable, but not the whole converter like it does nor
   * simple converters.
   *
   * @param value	the data to convert
   * @see		StreamConversion
   * @see		#m_Output
   */
  @Override
  public void setInput(Object value) {
    m_Input  = value;
    if (this instanceof StreamConversion)
      m_Output = null;
    else
      reset();
  }

  /**
   * The currently set input data to convert.
   *
   * @return		the data to convert, can be null if not yet set
   */
  @Override
  public Object getInput() {
    return m_Input;
  }

  /**
   * The currently set input data to convert.
   *
   * @param cls 	for casting
   * @return		the data to convert, can be null if not yet set
   */
  @Override
  public <T> T getInput(Class<T> cls) {
    return (T) m_Input;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public abstract Class generates();

  /**
   * Returns the generated output.
   *
   * @return		the output, null if none produced yet
   */
  public Object getOutput() {
    return m_Output;
  }

  /**
   * Returns the generated output.
   *
   * @param cls 	for casting
   * @return		the output, null if none produced yet
   */
  public <T> T getOutput(Class<T> cls) {
    return (T) m_Output;
  }

  /**
   * Checks whether the data can be processed.
   *
   * @return		null if checks passed, otherwise error message
   */
  protected String checkData() {
    if (m_Input == null)
      return "No input data available!";

    if ((accepts() != Unknown.class) && !accepts().isAssignableFrom(m_Input.getClass()))
      return
          "Data cannot be processed: input=" + Utils.classToString(m_Input)
        + ", accepts=" + Utils.classToString(accepts());

    return null;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected abstract Object doConvert() throws Exception;

  /**
   * Performs the conversion.
   *
   * @return		null if everything worked otherwise the error message
   */
  @Override
  public String convert() {
    String	result;
    String	msg;
    boolean     output;

    m_Stopped = false;
    
    if (isLoggingEnabled())
      getLogger().info("Input: " + m_Input);
    
    m_Output = null;
    result   = checkData();

    if (isLoggingEnabled())
      getLogger().info("Check: " + result);

    if (result == null) {
      if (this instanceof ConversionWithInitialization) {
	if (((ConversionWithInitialization) this).requiresSetUp()) {
	  result = ((ConversionWithInitialization) this).setUp();
	  if (isLoggingEnabled())
	    getLogger().info("SetUp: " + result);
	  if (((ConversionWithInitialization) this).requiresSetUp())
	    result = "Delayed setUp call failed, still requiring setUp!";
	}
      }
    }
    
    if (result == null) {
      try {
	m_Output = doConvert();

	if (isLoggingEnabled())
	  getLogger().info("Output: " + m_Output);
      }
      catch (Exception e) {
        output = true;
        if ((m_Owner instanceof Actor) && ((Actor) m_Owner).getSilent())
          output = false;
	msg    = "Failed to convert data (" + Utils.classToString(accepts()) + " -> " + Utils.classToString(generates()) + "):";
	result = msg + " " + e;
        if (output)
          getLogger().log(Level.SEVERE, msg, e);
      }
    }

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_Input  = null;
    m_Output = null;
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  @Override
  public AbstractConversion shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  @Override
  public AbstractConversion shallowCopy(boolean expand) {
    return (AbstractConversion) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
  }
}
