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
 * AbstractDirectoryExportDialog.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;

import adams.gui.chooser.DirectoryChooserPanel;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericObjectEditorPanel;

/**
 * Ancestor for export dialogs that export into directories rather than 
 * specific file.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the export format
 */
public abstract class AbstractDirectoryExportDialog<T>
  extends ApprovalDialog {

  /** for serialization. */
  private static final long serialVersionUID = 201725070566669323L;

  /** the panel for the parameters. */
  protected ParameterPanel m_PanelParams;

  /** the GOE panel for the export format. */
  protected GenericObjectEditorPanel m_PanelGOE;

  /** the panel with the directory. */
  protected DirectoryChooserPanel m_PanelDir;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public AbstractDirectoryExportDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public AbstractDirectoryExportDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public AbstractDirectoryExportDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public AbstractDirectoryExportDialog(Frame owner, boolean modal) {
    super(owner, modal);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setTitle(getDefaultTitle());

    m_PanelParams = new ParameterPanel();
    getContentPane().add(m_PanelParams, BorderLayout.CENTER);

    m_PanelGOE = createGOE();
    m_PanelParams.addParameter("For_mat", m_PanelGOE);

    m_PanelDir = new DirectoryChooserPanel();
    m_PanelParams.addParameter("_Directory", m_PanelDir);
  }

  /**
   * Finishes the initialization
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    pack();
  }
  
  /**
   * Returns the default title for the dialog.
   */
  protected String getDefaultTitle() {
    return "Export";
  }
  
  /**
   * Creates the GOE panel to use.
   */
  protected abstract GenericObjectEditorPanel createGOE();

  /**
   * Sets the export format.
   *
   * @param value	the export format
   */
  public void setExport(T value) {
    if (value != null)
      m_PanelGOE.setCurrent(value);
  }

  /**
   * Returns the export format.
   *
   * @return		the export format
   */
  public T getExport() {
    return (T) m_PanelGOE.getCurrent();
  }

  /**
   * Sets the directory to save to.
   *
   * @param value	the dir
   */
  public void setDirectory(File value) {
    m_PanelDir.setCurrent(value);
  }

  /**
   * Returns the current directory to save to.
   *
   * @return		the dir
   */
  public File getDirectory() {
    return m_PanelDir.getCurrent();
  }
}
