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
 * BaseDialog.java
 * Copyright (C) 2008-2012 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JDialog;

/**
 * A dialog that loads the size and location from the props file automatically.
 * <br><br>
 * Calling code needs to dispose the dialog manually or enable automatic
 * disposal:
 * <pre>
 * BaseDialog dialog = new ...
 * dialog.setDefaultCloseOperation(BaseDialog.DISPOSE_ON_CLOSE);
 * </pre>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see GUIHelper#setSizeAndLocation(java.awt.Window, java.awt.Component)
 */
public class BaseDialog
  extends JDialog {

  /** for serialization. */
  private static final long serialVersionUID = 6155286585412623451L;

  /**
   * Creates a modeless dialog without a title and without a specified Frame
   * owner.
   */
  public BaseDialog() {
    this((Frame) null);
  }

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public BaseDialog(Dialog owner) {
    this(owner, ModalityType.MODELESS);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public BaseDialog(Dialog owner, ModalityType modality) {
    this(owner, "", modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public BaseDialog(Dialog owner, String title) {
    this(owner, title, ModalityType.MODELESS);
  }

  /**
   * Creates a dialog with the specified title, modality and the specified
   * owner Dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   * @param modality	the type of modality
   */
  public BaseDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);

    initialize();
    initGUI();
    finishInit();
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public BaseDialog(Frame owner) {
    this(owner, false);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public BaseDialog(Frame owner, boolean modal) {
    this(owner, "", modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public BaseDialog(Frame owner, String title) {
    this(owner, title, false);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public BaseDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);

    initialize();
    initGUI();
    finishInit();
  }

  /**
   * For initializing members.
   */
  protected void initialize() {
  }

  /**
   * For initializing the GUI.
   */
  protected void initGUI() {
    if (GUIHelper.getLogoIcon() != null)
      setIconImage(GUIHelper.getLogoIcon().getImage());

    setDefaultCloseOperation(BaseDialog.HIDE_ON_CLOSE);
  }

  /**
   * finishes the initialization, by setting size/location.
   */
  protected void finishInit() {
    // size and location
    GUIHelper.setSizeAndLocation(this, this);
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  protected void beforeShow() {
  }

  /**
   * Hook method just after the dialog was made visible.
   */
  protected void afterShow() {
  }

  /**
   * Hook method just before the dialog is hidden.
   */
  protected void beforeHide() {
  }

  /**
   * Hook method just after the dialog was hidden.
   */
  protected void afterHide() {
  }

  /**
   * closes/shows the dialog.
   *
   * @param value	if true then display the dialog, otherwise close it
   */
  public void setVisible(boolean value) {
    if (value)
      beforeShow();
    else
      beforeHide();

    super.setVisible(value);

    if (value)
      afterShow();
    else
      afterHide();
  }
}
