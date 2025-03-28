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
 * AbstractConfigurableExtensionFileFilterFileChooser.java
 * Copyright (C) 2013-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.ObjectCopyHelper;
import adams.core.classmanager.ClassManager;
import adams.gui.core.BaseCheckBox;
import adams.gui.goe.GenericObjectEditor;
import adams.gui.goe.GenericObjectEditorDialog;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.util.List;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for spreadsheets and allows the user to invoke an options dialog to customize
 * the reader/writer.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractConfigurableExtensionFileFilterFileChooser<R,W>
  extends AbstractExtensionFileFilterFileChooser<ExtensionFileFilterWithClass> {

  /** for serialization. */
  private static final long serialVersionUID = 6245115901277297175L;

  /** the checkbox for bringing up the GenericObjectEditor. */
  protected BaseCheckBox m_CheckBoxOptions;

  /** the GOE for displaying the options of a reader/writer. */
  protected transient GenericObjectEditor m_Editor;

  /** the last "open" handler. */
  protected transient Object m_LastOpenHandler;
  
  /** the last "save" handler. */
  protected transient Object m_LastSaveHandler;
  
  /**
   * Constructs a FileChooser pointing to the user's default directory.
   */
  protected AbstractConfigurableExtensionFileFilterFileChooser() {
    super();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  protected AbstractConfigurableExtensionFileFilterFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  protected AbstractConfigurableExtensionFileFilterFileChooser(String currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Further initializations.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Editor = null;

    setAutoAppendExtension(true);
    setAllowGlobFilters(false);
  }

  /**
   * Returns the GOE, initializes it if necessary.
   * 
   * @return		the GOE
   */
  protected synchronized GenericObjectEditor getEditor() {
    if (m_Editor == null)
      m_Editor = new GenericObjectEditor(false);
    return m_Editor;
  }
  
  /**
   * Creates an accessory panel displayed next to the files.
   * 
   * @return		the panel or null if none available
   */
  @Override
  protected JComponent createAccessoryPanel() {
    JPanel	result;
    Dimension	dim;
    
    super.createAccessoryPanel();

    m_CheckBoxOptions = new BaseCheckBox("Edit options");
    m_CheckBoxOptions.setMnemonic('E');
    result = new JPanel(new BorderLayout());
    result.add(m_CheckBoxOptions, BorderLayout.NORTH);
    result.add(m_PanelBookmarksAndFilter, BorderLayout.CENTER);
    dim = getDefaultAccessoryDimension();
    if (dim != null) {
      result.setSize(dim);
      result.setMinimumSize(dim);
      result.setPreferredSize(dim);
    }
    
    return result;
  }

  /**
   * Returns whether the filters have already been initialized.
   *
   * @return		true if the filters have been initialized
   */
  @Override
  protected abstract boolean getFiltersInitialized();

  /**
   * Performs the actual initialization of the filters.
   */
  @Override
  protected abstract void doInitializeFilters();

  /**
   * Returns the file filters for opening files.
   *
   * @return		the file filters
   */
  @Override
  protected abstract List<ExtensionFileFilterWithClass> getOpenFileFilters();

  /**
   * Returns the file filters for writing files.
   *
   * @return		the file filters
   */
  @Override
  protected abstract List<ExtensionFileFilterWithClass> getSaveFileFilters();

  /**
   * Returns the default file filter to use.
   * 
   * @param dialogType	the dialog type: open/save
   * @return		the default file filter, null if unable find default one
   */
  @Override
  protected ExtensionFileFilterWithClass getDefaultFileFilter(int dialogType) {
    ExtensionFileFilterWithClass	result;
    List<ExtensionFileFilterWithClass>	list;
    String				cls;
    
    result = null;
    
    cls = null;
    if (dialogType == OPEN_DIALOG) {
      list = getOpenFileFilters();
      if (getDefaultReader() != null)
	cls  = getDefaultReader().getClass().getName();
    }
    else {
      list = getSaveFileFilters();
      if (getDefaultWriter() != null)
	cls  = getDefaultWriter().getClass().getName();
    }
    
    if ((list.size() > 0) && (cls != null)) {
      for (ExtensionFileFilterWithClass ext: list) {
	if (ext.getClassname().equals(cls)) {
	  result = ext;
	  break;
	}
      }
    }
    
    return result;
  }

  /**
   * initializes the GUI.
   *
   * @param dialogType		the type of dialog to setup the GUI for
   */
  @Override
  protected void initGUI(int dialogType) {
    ExtensionFileFilterWithClass	filter;
    
    super.initGUI(dialogType);

    if (dialogType == OPEN_DIALOG) {
      getEditor().setClassType(getReaderClass());
      getEditor().setValue(getDefaultReader());
    }
    else {
      getEditor().setClassType(getWriterClass());
      getEditor().setValue(getDefaultWriter());
    }
    
    filter = restoreLastFilter(dialogType);

    if (dialogType == OPEN_DIALOG) {
      // last handler != filter?
      if ((filter != null) && (m_LastOpenHandler != null)) {
        if (!filter.getClassname().equals(m_LastOpenHandler.getClass().getName()))
          m_LastOpenHandler = null;
      }
      if ((filter != null) && (m_LastOpenHandler == null)) {
	try {
	  m_LastOpenHandler = ClassManager.getSingleton().forName(filter.getClassname()).getDeclaredConstructor().newInstance();
	}
	catch (Exception e) {
          handleException("Failed to instantiate last open handler: " + filter.getClassname(), e);
	}
      }
      m_CurrentHandler = (m_LastOpenHandler == null) ? getDefaultReader() : m_LastOpenHandler;
      getEditor().setValue(m_CurrentHandler);
    }
    else {
      // last handler != filter?
      if ((filter != null) && (m_LastSaveHandler != null)) {
        if (!filter.getClassname().equals(m_LastSaveHandler.getClass().getName()))
          m_LastSaveHandler = null;
      }
      if ((filter != null) && (m_LastSaveHandler == null)) {
	try {
	  m_LastSaveHandler = ClassManager.getSingleton().forName(filter.getClassname()).getDeclaredConstructor().newInstance();
	}
	catch (Exception e) {
          handleException("Failed to instantiate last save handler: " + filter.getClassname(), e);
	}
      }
      m_CurrentHandler = (m_LastSaveHandler == null) ? getDefaultWriter() : m_LastSaveHandler;
      getEditor().setValue(m_CurrentHandler);
    }
  }

  /**
   * Returns the default reader.
   *
   * @return		the default reader
   */
  protected abstract R getDefaultReader();

  /**
   * Returns the reader superclass for the GOE.
   *
   * @return		the reader class
   */
  protected abstract Class getReaderClass();

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  protected abstract W getDefaultWriter();

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return		the writer class
   */
  protected abstract Class getWriterClass();

  /**
   * returns the reader that was chosen by the user, can be null in case the
   * user aborted the dialog or the save dialog was shown.
   *
   * @return		the chosen reader, if any
   */
  public R getReader() {
    configureCurrentHandlerHook(OPEN_DIALOG);

    if (    !ClassLocator.isSubclass(getReaderClass(), m_CurrentHandler.getClass())
 	 && !ClassLocator.hasInterface(getReaderClass(), m_CurrentHandler.getClass()) ) 
      return null;
    else
      return (R) ObjectCopyHelper.copyObject(m_CurrentHandler);
  }

  /**
   * Sets the reader to use.
   *
   * @param reader	the reader
   */
  public void setReader(R reader) {
    ExtensionFileFilterWithClass	extFilter;

    m_LastOpenHandler = ObjectCopyHelper.copyObject(reader);
    for (FileFilter filter: getChoosableFileFilters()) {
      if (filter instanceof ExtensionFileFilterWithClass) {
        extFilter = (ExtensionFileFilterWithClass) filter;
        if (extFilter.getClassname().equals(m_LastOpenHandler.getClass().getName())) {
          setFileFilter(extFilter);
          break;
	}
      }
    }
  }

  /**
   * returns the writer that was chosen by the user, can be null in case the
   * user aborted the dialog or the open dialog was shown.
   *
   * @return		the chosen writer, if any
   */
  public W getWriter() {
    configureCurrentHandlerHook(SAVE_DIALOG);

    if (    !ClassLocator.isSubclass(getWriterClass(), m_CurrentHandler.getClass()) 
	 && !ClassLocator.hasInterface(getWriterClass(), m_CurrentHandler.getClass()) )
      return null;
    else
      return (W) ObjectCopyHelper.copyObject(m_CurrentHandler);
  }

  /**
   * Sets the writer to use.
   *
   * @param writer	the writer
   */
  public void setWriter(W writer) {
    ExtensionFileFilterWithClass	extFilter;

    m_LastSaveHandler = ObjectCopyHelper.copyObject(writer);
    for (FileFilter filter: getChoosableFileFilters()) {
      if (filter instanceof ExtensionFileFilterWithClass) {
        extFilter = (ExtensionFileFilterWithClass) filter;
        if (extFilter.getClassname().equals(m_LastSaveHandler.getClass().getName())) {
          setFileFilter(extFilter);
          break;
	}
      }
    }
  }

  /**
   * Pops up an "Open File" file chooser dialog.
   *
   * @param parent		the parent of this file chooser
   * @return			the result of the user's action
   */
  @Override
  public int showOpenDialog(Component parent) {
    int result = super.showOpenDialog(parent);

    if (result == APPROVE_OPTION) {
      // bring up options dialog?
      if (m_CheckBoxOptions.isSelected()) {
	getEditor().setValue(m_CurrentHandler);
	GenericObjectEditorDialog dialog = GenericObjectEditorDialog.createDialog(this, getEditor());
	dialog.setUISettingsPrefix(this.getClass());
	dialog.setLocationRelativeTo(dialog.getParent());
	dialog.setVisible(true);
	result = dialog.getResultType();
	if (result == APPROVE_OPTION)
	  m_CurrentHandler = getEditor().getValue();
      }
      if (result == APPROVE_OPTION)
	m_LastOpenHandler = m_CurrentHandler;
    }

    return result;
  }

  /**
   * Pops up an "Save File" file chooser dialog.
   *
   * @param parent		the parent of this file chooser
   * @return			the result of the user's action
   */
  @Override
  public int showSaveDialog(Component parent) {
    int result = super.showSaveDialog(parent);

    if (result == APPROVE_OPTION) {
      // bring up options dialog?
      if (m_CheckBoxOptions.isSelected()) {
	getEditor().setValue(m_CurrentHandler);
	GenericObjectEditorDialog dialog = GenericObjectEditorDialog.createDialog(this, getEditor());
	dialog.setLocationRelativeTo(dialog.getParent());
	dialog.setVisible(true);
	result = dialog.getResultType();
	if (result == APPROVE_OPTION)
	  m_CurrentHandler = getEditor().getValue();
      }
      if (result == APPROVE_OPTION)
	m_LastSaveHandler = m_CurrentHandler;
    }

    return result;
  }

  /**
   * sets the current converter according to the current filefilter.
   */
  @Override
  protected void updateCurrentHandlerHook() {
    String	classname;
    Object	newHandler;

    if (isGlobFilter(getFileFilter()))
      return;
    
    try {
      // determine current converter
      classname  = ((ExtensionFileFilterWithClass) getFileFilter()).getClassname();
      newHandler = ClassManager.getSingleton().forName(classname).getDeclaredConstructor().newInstance();

      if (m_CurrentHandler == null) {
	m_CurrentHandler = newHandler;
      }
      else {
	if (!m_CurrentHandler.getClass().equals(newHandler.getClass()))
	  m_CurrentHandler = newHandler;
      }
      setFileSelectionMode(FILES_ONLY);
    }
    catch (Exception e) {
      m_CurrentHandler = null;
      handleException("Failed to update current handler:", e);
    }
  }

  /**
   * configures the current converter.
   *
   * @param dialogType		the type of dialog to configure for
   */
  @Override
  protected void configureCurrentHandlerHook(int dialogType) {
    String		classname;

    if (m_CurrentHandler == null) {
      classname = ((ExtensionFileFilterWithClass) getFileFilter()).getClassname();
      try {
	m_CurrentHandler = ClassManager.getSingleton().forName(classname).getDeclaredConstructor().newInstance();
      }
      catch (Exception e) {
	m_CurrentHandler = null;
        handleException("Failed to configure current handler:", e);
      }

      // none found?
      if (m_CurrentHandler == null)
	return;
    }
  }
  
  /**
   * Attempts to set the correct file filter for the specified file, using its
   * extension to determine the file filter.
   * 
   * @param file	the file to set the filter for
   * @return		true if successfully set filter
   */
  @Override
  public boolean setCorrectOpenFileFilter(File file) {
    boolean	result;
    
    result = super.setCorrectOpenFileFilter(file);
    if (result)
      m_LastOpenHandler = m_CurrentHandler;
    
    return result;
  }
  
  /**
   * Attempts to set the correct file filter for the specified file, using its
   * extension to determine the file filter.
   * 
   * @param file	the file to set the filter for
   * @return		true if successfully set filter
   */
  @Override
  public boolean setCorrectSaveFileFilter(File file) {
    boolean	result;

    result = super.setCorrectSaveFileFilter(file);
    if (result)
      m_LastSaveHandler = m_CurrentHandler;
    
    return result;
  }
}
