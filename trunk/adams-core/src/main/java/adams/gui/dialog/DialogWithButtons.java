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
 * DialogWithButtons.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.JPanel;

import adams.gui.core.BaseDialog;

/**
 * Ancestor for dialogs that offer buttons, e.g., OK and Cancel (default).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5703 $
 * @see #getOption()
 */
public class DialogWithButtons
  extends BaseDialog {

  /** for serialization. */
  private static final long serialVersionUID = -7382983170735594052L;

  /** the panel with the buttons (left and right). */
  protected JPanel m_PanelButtons;

  /** the panel with the buttons (left). */
  protected JPanel m_PanelButtonsLeft;

  /** the panel with the buttons (right). */
  protected JPanel m_PanelButtonsRight;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public DialogWithButtons(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public DialogWithButtons(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public DialogWithButtons(Dialog owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified title, modality and the specified
   * owner Dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   * @param modality	the type of modality
   */
  public DialogWithButtons(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public DialogWithButtons(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public DialogWithButtons(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public DialogWithButtons(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public DialogWithButtons(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    getContentPane().setLayout(new BorderLayout());

    // buttons
    m_PanelButtons = new JPanel(new BorderLayout());
    getContentPane().add(m_PanelButtons, BorderLayout.SOUTH);
    
    m_PanelButtonsLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelButtons.add(m_PanelButtonsLeft, BorderLayout.WEST);

    m_PanelButtonsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_PanelButtons.add(m_PanelButtonsRight, BorderLayout.EAST);
  }
  
  /**
   * Returns the parent panel for left/right buttons.
   * 
   * @return		the panel
   */
  public JPanel getButtonsPanel() {
    return m_PanelButtons;
  }
  
  /**
   * Returns a panel for buttons. By default, only the right one is 
   * initialized with buttons.
   * 
   * @param left	whether to retrieve the left or right panel
   * @return		the specified panel
   */
  public JPanel getButtonsPanel(boolean left) {
    if (left)
      return m_PanelButtonsLeft;
    else
      return m_PanelButtonsRight;
  }
}
