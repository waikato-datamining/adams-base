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
 * AbstractFileImportDialog.java
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
 * Ancestor for file import dialogs.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the import format
 */
public abstract class AbstractFileImportDialog<T>
  extends ApprovalDialog {

  /** for serialization. */
  private static final long serialVersionUID = 201725070566669323L;

  /** the panel for the parameters. */
  protected ParameterPanel m_PanelParams;

  /** the panel with the file. */
  protected FileChooserPanel m_PanelFile;

  /** the GOE panel for the import format. */
  protected GenericObjectEditorPanel m_PanelGOE;
  
  /**
   * Creates a modeless dialog without a title with the specified Dialog as
   * its owner.
   *
   * @param owner	the owning dialog
   */
  public AbstractFileImportDialog(Dialog owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Dialog and modality.
   *
   * @param owner	the owning dialog
   * @param modality	the type of modality
   */
  public AbstractFileImportDialog(Dialog owner, ModalityType modality) {
    super(owner, modality);
  }

  /**
   * Creates a modeless dialog without a title with the specified Frame as
   * its owner.
   *
   * @param owner	the owning frame
   */
  public AbstractFileImportDialog(Frame owner) {
    super(owner);
  }

  /**
   * Creates a dialog with the specified owner Frame, modality and an empty
   * title.
   *
   * @param owner	the owning frame
   * @param modal	whether the dialog is modal or not
   */
  public AbstractFileImportDialog(Frame owner, boolean modal) {
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

    m_PanelFile = new FileChooserPanel();
    m_PanelFile.setPromptOverwriteFile(true);
    m_PanelParams.addParameter("_File", m_PanelFile);

    m_PanelGOE = createGOE();
    m_PanelGOE.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	updateFileChooser();
      }
    });
    m_PanelParams.addParameter("For_mat", m_PanelGOE);
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
    return "Import";
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
   * Sets the import format.
   *
   * @param value	the import format
   */
  public void setImport(T value) {
    if (value != null)
      m_PanelGOE.setCurrent(value);
  }

  /**
   * Returns the import format.
   *
   * @return		the import format
   */
  public T getImport() {
    return (T) m_PanelGOE.getCurrent();
  }

  /**
   * Sets the file to read from.
   *
   * @param value	the file
   */
  public void setFile(File value) {
    m_PanelFile.setCurrent(value);
  }

  /**
   * Returns the current file to read from.
   *
   * @return		the file
   */
  public File getFile() {
    return m_PanelFile.getCurrent();
  }
}
