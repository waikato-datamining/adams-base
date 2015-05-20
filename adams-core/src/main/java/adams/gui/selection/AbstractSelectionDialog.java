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
 * AbstractSelectionDialog.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.selection;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import adams.core.CleanUpHandler;
import adams.gui.dialog.ApprovalDialog;

/**
 * Abstract ancestor for dialogs that allow the selection of items.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of item that can be selected
 * @param <P> the type of selection panel
 */
public abstract class AbstractSelectionDialog<T, P extends AbstractSelectionPanel>
  extends ApprovalDialog
  implements CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8270032576082341389L;

  /** the selection panel. */
  protected P m_Panel;

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  protected AbstractSelectionDialog(Dialog owner, String title) {
    super(owner, title, ModalityType.DOCUMENT_MODAL);
  }

  /**
   * Creates a modal dialog.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  protected AbstractSelectionDialog(Frame owner, String title) {
    super(owner, title, true);
  }

  /**
   * initializes the GUI elements.
   */
  protected void initGUI() {
    super.initGUI();

    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
	beforeHide();
        super.windowClosing(e);
      }
    });
  }

  /**
   * Cleans up data structures, frees up memory.
   * <br><br>
   * Default implementation does nothing.
   */
  public void cleanUp() {
  }
}
