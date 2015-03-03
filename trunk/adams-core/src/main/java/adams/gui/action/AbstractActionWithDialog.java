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
 * AbstractActionWithDialog.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.action;

import java.awt.Dialog;

/**
 * Actions that have an optional dialog associated which needs cleaning up.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <D> the type of dialog
 */
public abstract class AbstractActionWithDialog<D extends Dialog>
  extends AbstractBaseAction {

  /** for serialization. */
  private static final long serialVersionUID = 1688747085207787478L;
  
  /** the dialog in use. */
  protected D m_Dialog;
  
  /**
   * Initializes the action.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Dialog = null;
  }
  
  /**
   * Creates a new dialog.
   * <p/>
   * Default implementation does NOT create a dialog, throws an 
   * {@link IllegalStateException} instead.
   * 
   * @return		the dialog
   */
  protected D createDialog() {
    throw new IllegalStateException("Creation of dialog not implemented!");
  }
  
  /**
   * Returns the dialog to use, creates it if necessary.
   * 
   * @return		the dialog
   */
  protected synchronized D getDialog() {
    if (m_Dialog == null)
      m_Dialog = createDialog();
    return m_Dialog;
  }
  
  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();
    
    if (m_Dialog != null) {
      m_Dialog.dispose();
      m_Dialog = null;
    }
  }
}
