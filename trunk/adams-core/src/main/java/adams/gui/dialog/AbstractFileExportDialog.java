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
 * AbstractFileExportDialog.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.io.File;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.io.FileFormatHandler;
import adams.gui.chooser.FileChooserPanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.ParameterPanel;
import adams.gui.goe.GenericObjectEditorPanel;

/**
 * Ancestor for file export dialogs.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the export format
 */
public abstract class AbstractFileExportDialog<T>
  extends ApprovalDialog {

  /** for serialization. */
  private static final long serialVersionUID = 201725070566669323L;

  /** the panel for the parameters. */
  protected ParameterPanel m_PanelParams;

  /** the GOE panel for the export format. */
  protected GenericObjectEditorPanel m_PanelGOE;

  /** the panel with the file. */
  protected FileChooserPanel m_PanelFile;

  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public AbstractFileExportDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public AbstractFileExportDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public AbstractFileExportDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public AbstractFileExportDialog(Frame owner, boolean modal) {
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
    m_PanelGOE.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	updateFileChooser();
      }
    });
    m_PanelParams.addParameter("For_mat", m_PanelGOE);

    m_PanelFile = new FileChooserPanel();
    m_PanelFile.setPromptOverwriteFile(true);
    m_PanelFile.setUseSaveDialog(true);
    m_PanelParams.addParameter("_File", m_PanelFile);
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
   * Updates the file chooser's extension file filters.
   */
  protected void updateFileChooser() {
    FileFormatHandler	producer;
    ExtensionFileFilter	filter;

    m_PanelFile.setDefaultExtension(null);
    m_PanelFile.removeChoosableFileFilters();
    m_PanelFile.setAcceptAllFileFilterUsed(true);
    m_PanelFile.setAutoAppendExtension(false);

    if (m_PanelGOE.getCurrent() instanceof FileFormatHandler) {
      producer = (FileFormatHandler) m_PanelGOE.getCurrent();
      filter   = new ExtensionFileFilter(producer.getFormatDescription(), producer.getFormatExtensions());
      m_PanelFile.setDefaultExtension(producer.getDefaultFormatExtension());
      m_PanelFile.setAutoAppendExtension(true);
      m_PanelFile.addChoosableFileFilter(filter);
      m_PanelFile.setFileFilter(filter);
    }
  }

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
   * Sets the file to save to.
   *
   * @param value	the file
   */
  public void setFile(File value) {
    m_PanelFile.setCurrent(value);
  }

  /**
   * Returns the current file to save to.
   *
   * @return		the file
   */
  public File getFile() {
    return m_PanelFile.getCurrent();
  }
}
