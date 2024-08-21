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
 * AbstractFlowEditorAction.java
 * Copyright (C) 2014-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.action;

import adams.core.Properties;
import adams.gui.core.ImageManager;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import java.awt.Dialog;

/**
 * Ancestor for actions that use a Properties file as basis for shortcuts,
 * icons and mnemonics.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of state
 * @param <D> the type of dialog
 */
public abstract class AbstractPropertiesAction<T, D extends Dialog>
  extends AbstractActionWithDialog<D> {

  /** for serialization. */
  private static final long serialVersionUID = 9209507880496036402L;

  /** the state. */
  protected T m_State;

  /**
   * Initializes the action.
   */
  @Override
  protected void initialize() {
    String	shortcut;
    String	icon;
    String	mnemonic;

    super.initialize();

    m_State = null;

    setName(getTitle());

    shortcut = getProperties().getProperty(getClass().getName() + "-Shortcut");
    if (shortcut != null)
      setAccelerator(shortcut);

    icon = getProperties().getProperty(getClass().getName() + "-Icon");
    if ((icon != null) && canUseIcon())
      setIcon(ImageManager.getIcon(icon));

    mnemonic = getProperties().getProperty(getClass().getName() + "-Mnemonic");
    if ((mnemonic != null) && (mnemonic.length() == 1))
      setMnemonic(KeyStroke.getKeyStroke(mnemonic.toUpperCase()).getKeyCode());
  }

  /**
   * Returns whether the icon (if available) can be used.
   *
   * @return		true if it can be used
   */
  protected boolean canUseIcon() {
    return true;
  }

  /**
   * Returns the underlying properties.
   *
   * @return		the properties
   */
  protected abstract Properties getProperties();

  /**
   * Returns the caption of this action.
   *
   * @return		the caption, null if not applicable
   */
  protected abstract String getTitle();

  /**
   * Creates a new menuitem.
   */
  public abstract JMenuItem getMenuItem();

  /**
   * Performs the actual update of the state of the action.
   */
  protected abstract void doUpdate();

  /**
   * Updates the state of the action.
   *
   * @param state	the current state
   */
  public void update(T state) {
    m_State = state;
    doUpdate();
  }
}
