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
 * SimplePreviewBrowserDialog.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import adams.core.io.PlaceholderFile;
import adams.gui.core.BaseDialog;
import adams.gui.core.GUIHelper;
import adams.gui.tools.previewbrowser.PreviewDisplay;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;

/**
 * Convenience dialog for the preview browser (only displays the preview).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimplePreviewBrowserDialog
  extends BaseDialog {

  /** for serialization. */
  private static final long serialVersionUID = -1765490389240554880L;

  /** the preview panel. */
  protected PreviewDisplay m_PanelDisplay;

  /**
   * Creates a modeless dialog without a title and without a specified Frame
   * owner.
   */
  public SimplePreviewBrowserDialog() {
    super();
  }

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public SimplePreviewBrowserDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public SimplePreviewBrowserDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public SimplePreviewBrowserDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public SimplePreviewBrowserDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setTitle("Preview browser");
    getContentPane().setLayout(new BorderLayout());
    setSize(GUIHelper.getDefaultDialogDimension());

    m_PanelDisplay = new PreviewDisplay();
    getContentPane().add(m_PanelDisplay);
  }
  
  /**
   * Previews the specified file.
   * 
   * @param file	the file to preview
   */
  public void open(PlaceholderFile file) {
    m_PanelDisplay.display(new File[]{file}, false);
  }
}
