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
 * AbstractEditorRegistration.java
 * Copyright (C) 2011-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.goe;

import adams.core.ClassLister;
import adams.core.classmanager.ClassManager;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;

import java.io.Serializable;
import java.util.logging.Level;

/**
 * Ancestor for classes that register GenericObjectEditor editors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractEditorRegistration
  implements Serializable {

  /** for serialization. */
  private static final long serialVersionUID = 4364270584642868600L;

  /** the static logger. */
  protected static Logger LOGGER = LoggingHelper.getLogger(AbstractEditorRegistration.class);

  /** the logger in use. */
  protected Logger m_Logger;

  /**
   * Returns the logger in use.
   *
   * @return		the logger
   */
  protected synchronized Logger getLogger() {
    if (m_Logger == null)
      m_Logger = LoggingHelper.getLogger(getClass());
    return m_Logger;
  }

  /**
   * Returns whether registration already occurred.
   *
   * @return		true if registration already occurred
   */
  protected abstract boolean hasRegistered();

  /**
   * Performs the registration of the editors.
   *
   * @return		true if registration successful
   */
  protected abstract boolean doRegister();

  /**
   * Performs the registration of the editors.
   *
   * @return		true if registration successful
   */
  public boolean register() {
    if (hasRegistered())
      return true;

    return doRegister();
  }

  /**
   * Returns a list with classnames of registration classes.
   *
   * @return		the registration classnames
   */
  public static String[] getRegistrations() {
    return ClassLister.getSingleton().getClassnames(AbstractEditorRegistration.class);
  }

  /**
   * Registers the GOE editors, using all available registration schemes.
   */
  public static synchronized void registerEditors() {
    String[]			registrations;
    AbstractEditorRegistration	registration;

    registrations = getRegistrations();
    for (String classname: registrations) {
      try {
	registration = (AbstractEditorRegistration) ClassManager.getSingleton().forName(classname).getDeclaredConstructor().newInstance();
	if (registration instanceof AdamsEditorsRegistration)
	  continue;
	if (!registration.register())
	  System.err.println("Failed to register editors successfully: " + classname);
      }
      catch (Throwable t) {
	LOGGER.log(Level.SEVERE, "Failed to register editors: " + classname, t);
      }
    }

    // register ADAMS as last one
    registration = new AdamsEditorsRegistration();
    if (!registration.register())
      LOGGER.severe("Failed to register ADMAS editors successfully!");
  }
}
