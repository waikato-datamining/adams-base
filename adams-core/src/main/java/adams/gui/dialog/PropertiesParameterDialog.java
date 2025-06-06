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
 * PropertiesParameterDialog.java
 * Copyright (C) 2019-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.dialog;

import adams.core.Properties;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextPane;
import adams.gui.core.PropertiesParameterPanel;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

/**
 * Dialog for displaying a {@link adams.gui.core.PropertiesParameterPanel}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PropertiesParameterDialog
  extends ApprovalDialog {

  private static final long serialVersionUID = -3557570624720397564L;

  /** for displaying some info. */
  protected BaseTextPane m_PaneInfo;

  /** the properties panel in use. */
  protected PropertiesParameterPanel m_PropertiesParameterPanel;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public PropertiesParameterDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public PropertiesParameterDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner dialog.
   *
   * @param owner	the owning dialog
   * @param title	the title of the dialog
   */
  public PropertiesParameterDialog(Dialog owner, String title) {
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
  public PropertiesParameterDialog(Dialog owner, String title, ModalityType modality) {
    super(owner, title, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public PropertiesParameterDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public PropertiesParameterDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Creates a modeless dialog with the specified title and with the specified
   * owner frame.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   */
  public PropertiesParameterDialog(Frame owner, String title) {
    super(owner, title);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and title.
   *
   * @param owner	the owning frame
   * @param title	the title of the dialog
   * @param modal	whether the dialog is modal or not
   */
  public PropertiesParameterDialog(Frame owner, String title, boolean modal) {
    super(owner, title, modal);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    panel = new JPanel(new BorderLayout(5, 5));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    getContentPane().add(panel, BorderLayout.CENTER);

    m_PaneInfo = new BaseTextPane();
    m_PaneInfo.setEditable(false);
    m_PaneInfo.setVisible(false);
    panel.add(new BaseScrollPane(m_PaneInfo), BorderLayout.NORTH);

    m_PropertiesParameterPanel = new PropertiesParameterPanel();
    panel.add(m_PropertiesParameterPanel, BorderLayout.CENTER);
  }

  /**
   * Sets the info text to display.
   * Automatically displays the pane if there is text or hides it if empty/null.
   *
   * @param value	the info to display, null or empty to hide
   */
  public void setInfo(String value) {
    m_PaneInfo.setText(value);
    m_PaneInfo.setVisible((value != null) && !value.isEmpty());
  }

  /**
   * Returns the current info text.
   *
   * @return		the text
   */
  public String getInfo() {
    return m_PaneInfo.getText();
  }

  /**
   * Returns the underlying panel.
   *
   * @return		the panel
   */
  public PropertiesParameterPanel getPropertiesParameterPanel() {
    return m_PropertiesParameterPanel;
  }

  /**
   * Sets the properties in the underlying {@link PropertiesParameterPanel}.
   *
   * @param value	the properties to apply
   */
  public void setProperties(Properties value) {
    m_PropertiesParameterPanel.setProperties(value);
  }

  /**
   * Returns the current properties from the underlying {@link PropertiesParameterPanel}.
   *
   * @return		the properties
   */
  public Properties getProperties() {
    return m_PropertiesParameterPanel.getProperties();
  }
}
