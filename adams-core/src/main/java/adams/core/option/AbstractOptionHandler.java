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
 * AbstractOptionHandler.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option;

import adams.core.GlobalInfoSupporter;
import adams.core.logging.CustomLoggingLevelObject;
import adams.core.logging.LoggingLevel;

/**
 * Abstract superclass for classes that handle options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractOptionHandler
  extends CustomLoggingLevelObject
  implements OptionHandler, GlobalInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -4086279255884465893L;

  /** for managing the available options. */
  protected OptionManager m_OptionManager;

  /**
   * Initializes the object.
   */
  public AbstractOptionHandler() {
    super();
    initialize();
    defineOptions();
    getOptionManager().setDefaults();
    finishInit();
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public abstract String globalInfo();

  /**
   * Initializes the members.
   */
  protected void initialize() {
  }

  /**
   * Resets the scheme.
   * Derived classes must call this method in set-methods of parameters to
   * assure the invalidation of previously generated data.
   */
  protected void reset() {
  }

  /**
   * Returns a new instance of the option manager.
   *
   * @return		the manager to use
   */
  protected OptionManager newOptionManager() {
    return new OptionManager(this);
  }

  /**
   * Adds options to the internal list of options. Derived classes must
   * override this method to add additional options.
   */
  public void defineOptions() {
    m_OptionManager = newOptionManager();

    m_OptionManager.add(
	"logging-level", "loggingLevel",
	getDefaultLoggingLevel());
  }

  /**
   * Finishes the initialization in the constructor.
   * <br><br>
   * Initializes the logger again.
   */
  protected void finishInit() {
    configureLogger();
  }

  /**
   * Returns the option manager.
   *
   * @return		the manager
   */
  public OptionManager getOptionManager() {
    if (m_OptionManager == null)
      defineOptions();

    return m_OptionManager;
  }

  /**
   * Cleans up the options.
   */
  public void cleanUpOptions() {
    if (m_OptionManager != null) {
      m_OptionManager.cleanUp();
      m_OptionManager = null;
    }
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <br><br>
   * Cleans up the options.
   *
   * @see	#cleanUpOptions()
   */
  public void destroy() {
    cleanUpOptions();
  }
  
  /**
   * Returns the default logging level to use.
   * 
   * @return		the logging level
   */
  protected LoggingLevel getDefaultLoggingLevel() {
    return LoggingLevel.WARNING;
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  public synchronized void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    reset();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String loggingLevelTipText() {
    return "The logging level for outputting errors and debugging output.";
  }

  /**
   * Returns a string representation of the options.
   *
   * @return		 a string representation
   */
  @Override
  public String toString() {
    return OptionUtils.getCommandLine(this);
  }

  /**
   * Returns the commandline string.
   *
   * @return		 the commandline
   */
  public String toCommandLine() {
    return OptionUtils.getCommandLine(this);
  }
}
