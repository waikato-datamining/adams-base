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
 * BaseColorChooser.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.gui.chooser;

import adams.gui.dialog.ApprovalDialog;

import javax.swing.JColorChooser;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;

/**
 * Dialog for selecting a color.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseColorChooser
  extends ApprovalDialog {

  private static final long serialVersionUID = -3614758629231416689L;

  /** the color chooser panel. */
  protected JColorChooser m_PanelColorChooser;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public BaseColorChooser(Dialog owner) {
    this(owner, "Select color");
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public BaseColorChooser(Dialog owner, ModalityType modality) {
    this(owner, "Select color", modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public BaseColorChooser(Dialog owner, String title) {
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
  public BaseColorChooser(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public BaseColorChooser(Frame owner) {
    this(owner, "Select color");
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public BaseColorChooser(Frame owner, boolean modal) {
    this(owner, "Select color", modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public BaseColorChooser(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public BaseColorChooser(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    m_PanelColorChooser = new JColorChooser();
    add(m_PanelColorChooser, BorderLayout.CENTER);
    pack();
  }

  /**
   * Sets the color.
   *
   * @return		the color
   */
  public void setColor(Color value) {
    m_PanelColorChooser.setColor(value);
  }

  /**
   * Returns the color.
   *
   * @return 		the color
   */
  public Color getColor() {
    return m_PanelColorChooser.getColor();
  }
}
