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
 * AbstractTreeShortcut.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.flow.tree.menu;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import adams.core.logging.LoggingObject;
import adams.gui.core.GUIHelper;
import adams.gui.flow.FlowEditorPanel;
import adams.gui.flow.tree.StateContainer;

/**
 * Ancestor for shortcut definitions.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractTreeShortcut
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -2957040948643353777L;

  /** the keystroke this shortcut is associated with. */
  protected KeyStroke m_KeyStroke;
  
  /**
   * Initializes the shortcut.
   */
  protected AbstractTreeShortcut() {
    super();
    initialize();
  }

  /**
   * Initializes the object.
   */
  protected void initialize() {
    m_KeyStroke = GUIHelper.getKeyStroke(FlowEditorPanel.getTreeShortcut(getTreeShortCutKey()));
  }
  
  /**
   * Checks whether a keystroke is available.
   * 
   * @return		true if a keystroke available
   */
  public boolean hasKeyStroke() {
    return (m_KeyStroke != null);
  }
  
  /**
   * Returns the keystroke associated with the shortcut.
   * 
   * @return		the keystroke, null if not available
   */
  public KeyStroke getKeyStroke() {
    return m_KeyStroke;
  }
  
  /**
   * Returns the key for the tree shortcut in the properties file.
   * 
   * @return		the key
   * @see		FlowEditorPanel#getTreeShortcut(String)
   */
  protected abstract String getTreeShortCutKey();
  
  /**
   * Checks whether the keystroke matches.
   * 
   * @param ks		the keystroke to match
   * @return		true if a match
   */
  protected boolean keyStrokeApplies(KeyStroke ks) {
    return ks.equals(m_KeyStroke);
  }
  
  /**
   * Checks the state of the tree whether the shortcut is applicable.
   * 
   * @param state	the state of the tree
   * @return		true if state is applicable
   */
  public abstract boolean stateApplies(StateContainer state);
  
  /**
   * Returns whether the shortcut applies.
   * 
   * @param ks		the intercepter keystroke
   * @param state	the current state of the tree
   * @return		true if the shortcut applies
   */
  public boolean applies(KeyStroke ks, StateContainer state) {
    return keyStrokeApplies(ks) && stateApplies(state);
  }

  /**
   * Executes the shortcut.
   * 
   * @param state	the current state of the tree
   */
  protected abstract void doExecute(StateContainer state);

  /**
   * Executes the shortcut.
   * 
   * @param e		the keyevent (gets consumed)
   * @param state	the current state of the tree
   */
  public void execute(StateContainer state) {
    doExecute(state);
  }

  /**
   * Executes the shortcut.
   * 
   * @param e		the keyevent (gets consumed)
   * @param state	the current state of the tree
   */
  public void execute(KeyEvent e, StateContainer state) {
    doExecute(state);
    e.consume();
  }
}
