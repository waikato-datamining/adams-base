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
 * AbstractBuiltInShutdownHook.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.core.shutdownbuiltin;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.core.logging.LoggingHelper;
import adams.core.option.AbstractOptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Ancestor for built-in/automatic shutdown hooks for the JVM.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractBuiltInShutdownHook
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 6684292517732217844L;

  /** the installed hook. */
  protected Thread m_Hook;

  /** the hooks. */
  protected static List<AbstractBuiltInShutdownHook> m_Hooks;

  /**
   * Hook method before generating the runnable and installing it.
   * <br>
   * Default implementation just returns null.
   *
   * @return		null if successful, otherwise error message
   */
  public String check() {
    return null;
  }

  /**
   * Configures the runnable that gets executed when shutting down.
   *
   * @return		the runnable
   */
  public abstract Runnable configure();

  /**
   * Installs the hook, if possible.
   *
   * @return		null if successful, otherwise error message
   */
  public String install() {
    String	result;

    result = check();
    if (result == null) {
      m_Hook = new Thread(configure());
      Runtime.getRuntime().addShutdownHook(m_Hook);
    }

    return result;
  }

  /**
   * Removes the hook, if possible.
   *
   * @return		true if successfully removed (or not installed)
   */
  public boolean remove() {
    if (m_Hook == null)
      return true;
    return Runtime.getRuntime().removeShutdownHook(m_Hook);
  }

  /**
   * Installs all the built-in shutdown hooks.
   */
  public static synchronized void installAll() {
    if (m_Hooks != null)
      removeAll();

    m_Hooks = new ArrayList<>();
    for (Class cls: ClassLister.getSingleton().getClasses(AbstractBuiltInShutdownHook.class)) {
      try {
	AbstractBuiltInShutdownHook hook = (AbstractBuiltInShutdownHook) cls.getDeclaredConstructor().newInstance();
	hook.install();
	m_Hooks.add(hook);
      }
      catch (Exception e) {
	LoggingHelper.global().log(Level.SEVERE, "Failed to install built-in shutdown hook: " + Utils.classToString(cls));
      }
    }
  }

  /**
   * Removes all the installed built-in shutdown hooks.
   */
  public static synchronized void removeAll() {
    if (m_Hooks != null) {
      for (AbstractBuiltInShutdownHook hook: m_Hooks)
	hook.remove();
    }
    m_Hooks = null;
  }
}
