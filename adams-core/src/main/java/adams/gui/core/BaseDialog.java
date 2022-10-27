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
 * Copyright (C) 2008-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.gui.application.ChildFrame;

import javax.swing.JDialog;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;

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
 */
public class BaseDialog
    extends JDialog {

  /** for serialization. */
  private static final long serialVersionUID = 6155286585412623451L;

  /** whether the dispose method has been called already manually. */
  protected boolean m_DisposeCalled;

  /** the UI settings prefix to use. */
  protected String m_UISettingsPrefix;

  /** whether the UI settings got stored. */
  protected boolean m_UISettingsStored;

  /** whether UI settings were applied. */
  protected boolean m_UISettingsApplied;

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
    m_DisposeCalled     = false;
    m_UISettingsPrefix  = "";
    m_UISettingsStored  = false;
    m_UISettingsApplied = false;
  }

  /**
   * For initializing the GUI.
   */
  protected void initGUI() {
    if (ImageManager.getLogoIcon() != null)
      setIconImage(ImageManager.getLogoIcon().getImage());

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
   * Sets the prefix for the UI settings (eg stores width/height).
   *
   * @param value	the prefix, ignored if null or empty
   */
  public void setUISettingsPrefix(String value) {
    if (value == null)
      value = "";
    m_UISettingsPrefix  = value;
    m_UISettingsStored  = false;
    m_UISettingsApplied = false;
  }

  /**
   * Returns the prefix for the UI settings.
   *
   * @return		the prefix, empty if ignored
   */
  public String getUISettingsPrefix() {
    return m_UISettingsPrefix;
  }

  /**
   * Returns whether UI settings were applied. E.g., to determine whether still necessary to set default dimensions/location.
   *
   * @return		true if applied
   */
  public boolean getUISettingsApplied() {
    return m_UISettingsApplied;
  }

  /**
   * Applies any UI settings if present.
   */
  public void applyUISettings() {
    Dimension 	size;
    int		x;
    int		y;

    // size
    if (UISettings.has(ChildFrame.class, m_UISettingsPrefix + ".width") && UISettings.has(ChildFrame.class, m_UISettingsPrefix + ".width")) {
      m_UISettingsApplied = true;
      size = getSize();
      setSize(new Dimension(
	  UISettings.get(ChildFrame.class, m_UISettingsPrefix + ".width", size.width),
	  UISettings.get(ChildFrame.class, m_UISettingsPrefix + ".height", size.height)));
    }

    // position
    if (UISettings.has(ChildFrame.class, m_UISettingsPrefix + ".x") && UISettings.has(ChildFrame.class, m_UISettingsPrefix + ".y")) {
      m_UISettingsApplied = true;
      x = getX();
      y = getY();
      setLocation(
	  UISettings.get(ChildFrame.class, m_UISettingsPrefix + ".x", x),
	  UISettings.get(ChildFrame.class, m_UISettingsPrefix + ".y", y));
    }

    m_UISettingsStored = false;
  }

  /**
   * Stores the UI settings.
   */
  public void storeUISettings() {
    if (!m_UISettingsStored && !m_UISettingsPrefix.isEmpty()) {
      m_UISettingsStored = true;
      UISettings.set(ChildFrame.class, m_UISettingsPrefix + ".width", getWidth());
      UISettings.set(ChildFrame.class, m_UISettingsPrefix + ".height", getHeight());
      UISettings.set(ChildFrame.class, m_UISettingsPrefix + ".x", getX());
      UISettings.set(ChildFrame.class, m_UISettingsPrefix + ".y", getY());
      UISettings.save();
      m_UISettingsApplied = false;
    }
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  protected void beforeShow() {
    if (!m_UISettingsPrefix.isEmpty())
      applyUISettings();
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
    storeUISettings();
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

  /**
   * de-registers the child frame with the parent first.
   */
  @Override
  public void dispose() {
    if (!m_DisposeCalled)
      storeUISettings();

    m_DisposeCalled = true;

    super.dispose();
  }

  /**
   * Sets the location relative to this component, but adjust window size
   * and position if necessary.
   *
   * @param c		the component to position the window relative to
   */
  @Override
  public void setLocationRelativeTo(Component c) {
    super.setLocationRelativeTo(c);
    GUIHelper.adjustSize(this);
    GUIHelper.fixPosition(this);
  }
}
