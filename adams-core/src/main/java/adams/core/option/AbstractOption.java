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
 * AbstractOption.java
 * Copyright (C) 2010-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option;

import adams.core.CleanUpHandler;
import adams.core.logging.LoggingHelper;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * The ancestor of all option classes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractOption
  implements Serializable, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = 8825127756251485512L;

  /** the tooltip suffix for properties. */
  public final static String TOOLTIP_SUFFIX = "TipText";

  /** the owning object. */
  protected OptionManager m_Owner;

  /** the commandline option, without the leading "-". */
  protected String m_Commandline;

  /** the bean property to use for getting/setting. */
  protected String m_Property;

  /** the default value for this option. */
  protected Object m_DefaultValue;

  /** whether to output the default value for this option. */
  protected boolean m_OutputDefaultValue;

  /** whether debugging is enabled. */
  protected boolean m_Debug;

  /** in what usermode to show this option. */
  protected UserMode m_MinUserMode;

  /**
   * Initializes the option.
   *
   * @param owner		the owner of this option
   * @param commandline		the commandline string to identify the option (no leading dash)
   * @param property 		the name of the bean property
   * @param defValue		the default value, if null then the owner's
   * 				current state is used
   */
  protected AbstractOption(OptionManager owner, String commandline, String property, Object defValue) {

    super();

    OptionUtils.registerCustomHooks();

    m_Owner        	 = owner;
    m_Commandline  	 = commandline;
    m_Property     	 = property;
    m_DefaultValue 	 = defValue;
    m_OutputDefaultValue = true;
    m_MinUserMode        = UserMode.LOWEST;
    m_Debug              = OptionUtils.getDebug();

    // obtain default value if not provided
    if (m_DefaultValue == null) {
      try {
	m_DefaultValue = getDescriptor().getReadMethod().invoke(getOptionHandler());
      }
      catch (Exception e) {
	if (!m_Owner.isQuiet())
	  System.err.println(
	    "Cannot determine default value: "
	      + getOptionHandler().getClass().getName() + "." + getProperty());
      }
    }
  }

  /**
   * Returns whether debugging output is enabled.
   *
   * @return		true if debugging output is enabled
   */
  public boolean getDebug() {
    return m_Debug;
  }

  /**
   * Suppresses the output of the default value in help strings etc.
   *
   * @return		itself
   */
  public AbstractOption dontOutputDefaultValue() {
    return setOutputDefaultValue(false);
  }

  /**
   * Sets whether the default is to be output or not in help strings etc.
   *
   * @param value	true if the default value is to be output
   * @return		itself
   */
  public AbstractOption setOutputDefaultValue(boolean value) {
    m_OutputDefaultValue = value;
    return this;
  }

  /**
   * Returns whether the default value is to be output or not in help
   * strings, etc.
   *
   * @return		true if the default value is to be output
   */
  public boolean getOutputDefaultValue() {
    return m_OutputDefaultValue;
  }

  /**
   * Sets the minimum user mode.
   *
   * @param value	the minimum user mode
   * @return		itself
   */
  public AbstractOption setMinUserMode(UserMode value) {
    m_MinUserMode = value;
    return this;
  }

  /**
   * Returns the minimum user mode before this option is displayed.
   *
   * @return		the minimum user mode
   */
  public UserMode getMinUserMode() {
    return m_MinUserMode;
  }

  /**
   * Returns the owning object.
   *
   * @return		the owner of this option
   */
  public OptionManager getOwner() {
    return m_Owner;
  }

  /**
   * Returns the option handler this option belongs to.
   *
   * @return		the owning OptionHandler
   */
  public OptionHandler getOptionHandler() {
    return m_Owner.getOwner();
  }

  /**
   * Returns the commandline options string, without the "-".
   *
   * @return		the commandline string
   */
  public String getCommandline() {
    return m_Commandline;
  }

  /**
   * Returns the property used for getting/setting the option.
   *
   * @return		the property responsible for getting/setting
   */
  public String getProperty() {
    return m_Property;
  }

  /**
   * Returns the default value for this option.
   *
   * @return		the default value
   */
  public Object getDefaultValue() {
    return m_DefaultValue;
  }

  /**
   * Returns the current value for this option (obtained via the get-method).
   *
   * @return		the current value
   */
  public Object getCurrentValue() {
    Object	result;
    Method	method;

    try {
      method = getReadMethod();
      if (method != null)
	result = method.invoke(getOptionHandler());
      else
	result = m_DefaultValue;
    }
    catch (Exception e) {
      if (!m_Owner.isQuiet()) {
	LoggingHelper.global().log(Level.SEVERE, "Error getting current value of '" + getOptionHandler().getClass().getName() + "/" +  getProperty() + "':", e);
      }
      result = m_DefaultValue;
    }

    return result;
  }

  /**
   * Sets the current value.
   *
   * @param value	the value to set
   * @return		true if successfully set
   */
  public boolean setCurrentValue(Object value) {
    boolean	result;
    Method	method;

    method = getWriteMethod();
    try {
      method.invoke(getOptionHandler(), value);
      result = true;
    }
    catch (Exception e) {
      if (!m_Owner.isQuiet()) {
	LoggingHelper.global().log(Level.SEVERE, "Error setting value for '" + getOptionHandler().getClass().getName() + "/" +  getProperty() + "':", e);
      }
      result = false;
    }

    return result;
  }

  /**
   * Returns the bean property descriptor for the get/set methods. Should
   * never be null, unless the property cannot be found in the owner.
   *
   * @return		the bean property descriptor
   */
  public synchronized PropertyDescriptor getDescriptor() {
    return OptionUtils.getDescriptor(getOptionHandler(), getProperty());
  }

  /**
   * Returns the method for obtaining the tooltip. Can be null, if no
   * corresponding tooltip was found for the property.
   *
   * @return		the method for returning the tooltip, can be null
   */
  public synchronized Method getToolTipMethod() {
    Method	result;

    try {
      result = getOptionHandler().getClass().getMethod(getProperty() + TOOLTIP_SUFFIX);
    }
    catch (Exception e) {
      // ignored, means that there's no tooltip available
      result = null;
      if (!m_Owner.isQuiet())
	System.err.println("Missing tooltip: " + getOptionHandler().getClass().getName() + "." + getProperty() + TOOLTIP_SUFFIX);
    }

    return result;
  }

  /**
   * Returns the read method for the property.
   *
   * @return		the method, null if no property descriptor available
   */
  protected Method getReadMethod() {
    Method	result;

    if (getDescriptor() != null)
      result = getDescriptor().getReadMethod();
    else
      result = null;

    if (result == null) {
      if (!m_Owner.isQuiet())
	System.err.println("No read method for '" + getProperty() + "'??");
    }

    return result;
  }

  /**
   * Returns the write method for the property.
   *
   * @return		the method, null if no property descriptor available
   */
  protected Method getWriteMethod() {
    Method	result;

    if (getDescriptor() != null)
      result = getDescriptor().getWriteMethod();
    else
      result = null;

    if (result == null) {
      if (!m_Owner.isQuiet())
	System.err.println("No write method for '" + getProperty() + "'??");
    }

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_Owner         = null;
    m_DefaultValue  = null;
  }
}
