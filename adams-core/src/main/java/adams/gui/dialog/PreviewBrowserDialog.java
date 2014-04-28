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
 * PreviewBrowserDialog.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;

import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.gui.core.BaseDialog;
import adams.gui.tools.PreviewBrowserPanel;

/**
 * Convenience dialog for the preview browser.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PreviewBrowserDialog
  extends BaseDialog {
  
  /** for serialization. */
  private static final long serialVersionUID = -1765490389240554880L;
  
  /** the preview panel. */
  protected PreviewBrowserPanel m_PanelPreview;

  /**
   * Creates a modeless dialog without a title and without a specified Frame
   * owner.
   */
  public PreviewBrowserDialog() {
    super();
  }

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public PreviewBrowserDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public PreviewBrowserDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public PreviewBrowserDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public PreviewBrowserDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setTitle("Preview browser");
    m_PanelPreview = new PreviewBrowserPanel();
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(m_PanelPreview, BorderLayout.CENTER);
    setJMenuBar(m_PanelPreview.getMenuBar());
    setSize(800, 800);
  }
  
  /**
   * Previews the specified file.
   * 
   * @param file	the file to preview
   */
  public void open(PlaceholderFile file) {
    m_PanelPreview.open(file);
  }
  
  /**
   * Previews the specified directory.
   * 
   * @param dir		the dir to preview
   */
  public void open(PlaceholderDirectory dir) {
    m_PanelPreview.open(dir);
  }
  
  /**
   * Returns the preview panel.
   * 
   * @return		the panel
   */
  public PreviewBrowserPanel getPreviewPanel() {
    return m_PanelPreview;
  }
}
