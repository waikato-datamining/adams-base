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
 * AbstractScriplet.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import java.util.Hashtable;

import adams.core.ClassLister;
import adams.core.logging.LoggingObject;
import adams.core.option.OptionUtils;
import adams.db.DataProvider;
import adams.gui.core.BasePanel;

/**
 * Abstract superclass for action scriplets. An action scriplet processes
 * a single command in the command processor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see AbstractCommandProcessor
 */
public abstract class AbstractScriptlet
  extends LoggingObject
  implements Comparable {

  /** for serialization. */
  private static final long serialVersionUID = 8460215813832005436L;

  /** the owner. */
  protected AbstractCommandProcessor m_Owner;

  /** the data provider to use. */
  protected DataProvider m_DataProvider;

  /** additional parameters. */
  protected Hashtable<String,Object> m_Parameters;

  /**
   * Initializes the action.
   */
  public AbstractScriptlet() {
    super();

    setOwner(null);
  }

  /**
   * Sets the owning command processor.
   *
   * @param value	the command processor
   */
  public void setOwner(AbstractCommandProcessor value) {
    m_Owner = value;

    initialize();
  }

  /**
   * For initializing the member variables.
   */
  protected void initialize() {
    m_Parameters = new Hashtable<String,Object>();
  }

  /**
   * Returns the owning command processor.
   *
   * @return		the command processor, null if none set
   */
  public AbstractCommandProcessor getOwner() {
    return m_Owner;
  }

  /**
   * Checks whether an owner is set.
   *
   * @return		true if owner available
   * @see		#getOwner()
   */
  public boolean hasOwner() {
    return (m_Owner != null);
  }

  /**
   * Sets the parameter and the associated value.
   *
   * @param key		the name of the parameter
   * @param value	the value of the parameter
   */
  public void setParameter(String key, Object value) {
    m_Parameters.put(key, value);
  }

  /**
   * Checks whether a parameter is present.
   *
   * @param key		the name of the parameter to check
   * @return		true if present
   */
  public boolean hasParameter(String key) {
    return m_Parameters.containsKey(key);
  }

  /**
   * Returns the associated value of the parameter.
   *
   * @param key		the name of the parameter to retrieve
   * @param defValue	the default value
   * @return		the stored value or, if parameter not found, the
   * 			default value
   */
  public Object getParameter(String key, Object defValue) {
    if (m_Parameters.containsKey(key))
      return m_Parameters.get(key);
    else
      return defValue;
  }

  /**
   * Sets the data provider to use for accessing the database.
   *
   * @param value	the data provider
   */
  public void setDataProvider(DataProvider value) {
    m_DataProvider = value;
  }

  /**
   * Returns the current data provider.
   *
   * @return		the data provider, can be null if not set
   */
  public DataProvider getDataProvider() {
    return m_DataProvider;
  }

  /**
   * Returns the underlying BasePanel if available.
   *
   * @return		the current BasePanel, if any
   */
  public BasePanel getBasePanel() {
    if (hasOwner())
      return getOwner().getBasePanel();
    else
      return null;
  }

  /**
   * Displays the status, if the owner is a StatusMessageHandler, otherwise it
   * just prints the status on the commandline.
   *
   * @param msg		the message to display
   * @see		AbstractCommandProcessor#showStatus(String)
   */
  protected void showStatus(String msg) {
    if (hasOwner())
      getOwner().showStatus(msg);
  }

  /**
   * Returns the action string used in the command processor.
   *
   * @return		the action string
   */
  public abstract String getAction();

  /**
   * Returns a one-line listing of the parameters.
   *
   * @return		the command format
   */
  public String getParameterDescription() {
    String	result;

    result = getAction();
    if (getOptionsDescription() != null)
      result += " " + getOptionsDescription();

    return result;
  }

  /**
   * Returns a one-line listing of the options of the action.
   * <br><br>
   * Default implementation returns null.
   *
   * @return		the options or null if none
   */
  protected String getOptionsDescription() {
    return null;
  }

  /**
   * Returns the full description of the action.
   *
   * @return		the full description
   */
  public abstract String getDescription();

  /**
   * Returns the class(es) of an object that must be present for this action
   * to be executed.
   * <br><br>
   * The default implementation returns null.
   *
   * @return		the class(es) of which an instance must be present for
   * 			execution, null if none necessary
   */
  public Class[] getRequirements() {
    return null;
  }

  /**
   * Processes the options.
   *
   * @param options	additional/optional options for the action
   * @return		null if no error, otherwise error message
   * @throws Exception 	if something goes wrong
   */
  public abstract String process(String options) throws Exception;

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Only compares the action name of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    if (!(o instanceof AbstractScriptlet))
      return -1;

    return getAction().compareTo(((AbstractScriptlet) o).getAction());
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
   * Only compares the action name of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Hashcode so can be used as hashtable key. Returns the hashcode of the
   * {@link #getAction()} string.
   *
   * @return		the hashcode
   */
  public int hashCode() {
    return getAction().hashCode();
  }

  /**
   * Returns a string representation of the action.
   *
   * @return		the string representation
   */
  public String toString() {
    String	result;
    Class[]	cls;
    int		i;

    // name
    result = getAction();

    // requirements
    cls = getRequirements();
    if (cls != null) {
      result += ", requires=";
      for (i = 0; i < cls.length; i++) {
	if (i > 0)
	  result += ",";
	result += cls[i].getName();
      }
    }

    // parameters
    result += ", parameters=" + m_Parameters;

    return result;
  }

  /**
   * Returns a list with classnames of filters.
   *
   * @return		the filter classnames
   */
  public static String[] getScriptlets() {
    return ClassLister.getSingleton().getClassnames(AbstractScriptlet.class);
  }

  /**
   * Instantiates the scriptlet.
   *
   * @param classname	the classname of the scriptlet to instantiate
   * @return		the instantiated scriptlet or null if an error occurred
   */
  public static AbstractScriptlet forName(String classname) {
    AbstractScriptlet	result;

    try {
      result = (AbstractScriptlet) OptionUtils.forName(AbstractScriptlet.class, classname, new String[0]);
    }
    catch (Exception e) {
      e.printStackTrace();
      result = null;
    }

    return result;
  }
}
