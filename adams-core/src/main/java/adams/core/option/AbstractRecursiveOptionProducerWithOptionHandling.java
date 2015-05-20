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
 * AbstractRecursiveOptionProducerWithOptionHandling.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.logging.LoggingLevel;

/**
 * Ancestor for recursive option producers that offer option handling.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <O> the type of output data that gets generated
 * @param <I> the internal type used while nesting
 */
public abstract class AbstractRecursiveOptionProducerWithOptionHandling<O,I>
  extends AbstractRecursiveOptionProducer<O,I>
  implements OptionProducerWithOptionHandling<O,I> {

  /** for serialization. */
  private static final long serialVersionUID = 7710947781968446114L;

  /** for managing the available options. */
  protected OptionManager m_OptionManager;
  
  /**
   * Initializes the visitor.
   */
  public AbstractRecursiveOptionProducerWithOptionHandling() {
    super();
    defineOptions();
    getOptionManager().setDefaults();
    finishInit();
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
	LoggingLevel.OFF);

    m_OptionManager.add(
	"output-var-values", "outputVariableValues",
	false);
  }

  /**
   * Finishes the initialization in the constructor.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void finishInit() {
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
}
