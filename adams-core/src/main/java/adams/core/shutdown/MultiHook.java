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
 * MultiHook.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.shutdown;

/**
 * Executes the specified hooks sequentially.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiHook
  extends AbstractShutdownHook {

  private static final long serialVersionUID = -4069724495805072093L;

  /** the hooks. */
  protected AbstractShutdownHook[] m_Hooks;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes the specified hooks sequentially.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "hook", "hooks",
      new AbstractShutdownHook[0]);
  }

  /**
   * Sets the hooks to execute sequentially.
   *
   * @param value	the hooks
   */
  public void setHooks(AbstractShutdownHook[] value) {
    m_Hooks = value;
    reset();
  }

  /**
   * Returns the hooks to execute sequentially.
   *
   * @return		the hooks
   */
  public AbstractShutdownHook[] getHooks() {
    return m_Hooks;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hooksTipText() {
    return "The hooks to execute sequentially.";
  }

  /**
   * Hook method before generating the runnable and installing it.
   *
   * @return		null if successful, otherwise error message
   */
  public String check() {
    String	result;
    int		i;

    result = null;

    for (i = 0; i < m_Hooks.length; i++) {
      result = m_Hooks[i].check();
      if (result != null) {
	result = "Shutdown hook #" + (i + 1) + ": " + result;
	break;
      }
    }

    return result;
  }

  /**
   * Configures the runnable that gets executed when shutting down.
   *
   * @return		the runnable
   */
  @Override
  public Runnable configure() {
    final Runnable[]	hooks;
    int			i;

    hooks = new Runnable[m_Hooks.length];
    for (i = 0; i < m_Hooks.length; i++)
      hooks[i] = m_Hooks[i].configure();

    return () -> {
      for (int n = 0; n < hooks.length; n++) {
        try {
          hooks[n].run();
	}
	catch (Throwable t) {
          System.err.println("Failed to run shutdown hook #" + (n+1));
          t.printStackTrace();
	}
      }
    };
  }
}
