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
 * EmailFileChooser.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.ClassLister;
import adams.data.io.input.EmailFileReader;
import adams.data.io.input.PropertiesEmailFileReader;
import adams.data.io.output.EmailFileWriter;
import adams.data.io.output.PropertiesEmailFileWriter;
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditorDialog;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for emails.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EmailFileChooser
  extends AbstractConfigurableExtensionFileFilterFileChooser<EmailFileReader,EmailFileWriter>
  implements FileTypeDeterminingFileChooser<EmailFileReader,EmailFileWriter> {

  /** for serialization. */
  private static final long serialVersionUID = -6341967475735162796L;

  /** the file filters for the readers. */
  protected static List<ExtensionFileFilterWithClass> m_ReaderFileFilters;

  /** the file filters for the writers. */
  protected static List<ExtensionFileFilterWithClass> m_WriterFileFilters;

  /**
   * Constructs a FileChooser pointing to the user's default directory.
   */
  public EmailFileChooser() {
    super();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  public EmailFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  public EmailFileChooser(String currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Returns whether the filters have already been initialized.
   *
   * @return		true if the filters have been initialized
   */
  @Override
  protected boolean getFiltersInitialized() {
    return (m_ReaderFileFilters != null);
  }

  /**
   * Performs the actual initialization of the filters.
   */
  @Override
  protected void doInitializeFilters() {
    initFilters(true, ClassLister.getSingleton().getClassnames(EmailFileReader.class));
    initFilters(false, ClassLister.getSingleton().getClassnames(EmailFileWriter.class));
  }

  /**
   * initializes the SpreadSheetFileExtensionFilters.
   *
   * @param reader	if true then the reader filters are initialized
   * @param classnames	the classnames of the converters
   */
  protected static void initFilters(boolean reader, String[] classnames) {
    int					i;
    String 				classname;
    Class 				cls;
    String[] 				ext;
    String 				desc;
    Object		 		converter;
    ExtensionFileFilterWithClass 	filter;

    if (reader && (m_ReaderFileFilters != null))
      return;
    if (!reader && (m_WriterFileFilters != null))
      return;

    if (reader)
      m_ReaderFileFilters = new ArrayList<>();
    else
      m_WriterFileFilters  = new ArrayList<>();

    for (i = 0; i < classnames.length; i++) {
      classname = (String) classnames[i];

      // get data from converter
      try {
	cls       = Class.forName(classname);
	converter = cls.newInstance();
	if (reader) {
	  desc = ((EmailFileReader) converter).getFormatDescription();
	  ext  = ((EmailFileReader) converter).getFormatExtensions();
	}
	else {
	  desc = ((EmailFileWriter) converter).getFormatDescription();
	  ext  = ((EmailFileWriter) converter).getFormatExtensions();
	}
      }
      catch (Exception e) {
        handleException("Failed to set up: " + classname, e);
	cls       = null;
	converter = null;
	ext       = new String[0];
	desc      = "";
      }

      if (converter == null)
	continue;

      // reader?
      if (reader) {
	filter = new ExtensionFileFilterWithClass(classname, desc, ext);
	m_ReaderFileFilters.add(filter);
      }
      else {
	filter = new ExtensionFileFilterWithClass(classname, desc, ext);
	m_WriterFileFilters.add(filter);
      }
    }

    if (reader)
      Collections.sort(m_ReaderFileFilters);
    else
      Collections.sort(m_WriterFileFilters);
  }

  /**
   * Returns the file filters for opening files.
   *
   * @return		the file filters
   */
  @Override
  protected List<ExtensionFileFilterWithClass> getOpenFileFilters() {
    return m_ReaderFileFilters;
  }

  /**
   * Returns the file filters for writing files.
   *
   * @return		the file filters
   */
  @Override
  protected List<ExtensionFileFilterWithClass> getSaveFileFilters() {
    return m_WriterFileFilters;
  }

  /**
   * initializes the GUI.
   *
   * @param dialogType		the type of dialog to setup the GUI for
   */
  @Override
  protected void initGUI(int dialogType) {
    super.initGUI(dialogType);

    // initial setup
    if (dialogType == OPEN_DIALOG) {
      m_Editor.setClassType(EmailFileReader.class);
      m_Editor.setValue(getDefaultReader());
    }
    else {
      m_Editor.setClassType(EmailFileWriter.class);
      m_Editor.setValue(getDefaultWriter());
    }
    restoreLastFilter(dialogType);
  }

  /**
   * Returns the reader superclass for the GOE.
   *
   * @return		the reader class
   */
  @Override
  protected Class getReaderClass() {
    return EmailFileReader.class;
  }

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return		the writer class
   */
  @Override
  protected Class getWriterClass() {
    return EmailFileWriter.class;
  }

  /**
   * Returns the default reader.
   *
   * @return		the default reader
   */
  @Override
  protected EmailFileReader getDefaultReader() {
    return new PropertiesEmailFileReader();
  }

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected EmailFileWriter getDefaultWriter() {
    return new PropertiesEmailFileWriter();
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
	m_Editor.setValue(m_CurrentHandler);
	GenericObjectEditorDialog dialog = GenericObjectEditorDialog.createDialog(this, m_Editor);
	dialog.setLocationRelativeTo(GUIHelper.getParentComponent(this));
	dialog.setVisible(true);
	result = dialog.getResultType();
	if (result == APPROVE_OPTION)
	  m_CurrentHandler = m_Editor.getValue();
      }
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
	m_Editor.setValue(m_CurrentHandler);
	GenericObjectEditorDialog dialog = GenericObjectEditorDialog.createDialog(this, m_Editor);
	dialog.setLocationRelativeTo(GUIHelper.getParentComponent(this));
	dialog.setVisible(true);
	result = dialog.getResultType();
	if (result == APPROVE_OPTION)
	  m_CurrentHandler = m_Editor.getValue();
      }
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

    try {
      // determine current converter
      classname  = ((ExtensionFileFilterWithClass) getFileFilter()).getClassname();
      newHandler = Class.forName(classname).newInstance();

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
	m_CurrentHandler = Class.forName(classname).newInstance();
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
   * Returns the reader for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the reader, null if none found
   */
  public EmailFileReader getReaderForFile(File file) {
    return readerForFile(file);
  }

  /**
   * Returns the writer for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the writer, null if none found
   */
  public EmailFileWriter getWriterForFile(File file) {
    return writerForFile(file);
  }

  /**
   * Returns the reader for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the reader, null if none found
   */
  public static EmailFileReader readerForFile(File file) {
    EmailFileReader	result;

    result = null;

    initFilters(true, ClassLister.getSingleton().getClassnames(EmailFileReader.class));

    for (ExtensionFileFilterWithClass filter: m_ReaderFileFilters) {
      if (filter.accept(file)) {
	try {
	  result = (EmailFileReader) Class.forName(filter.getClassname()).newInstance();
	}
	catch (Exception e) {
          handleException("Failed to instantiate reader: " + filter.getClassname(), e);
	}
      }
    }

    return result;
  }

  /**
   * Returns the writer for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the writer, null if none found
   */
  public static EmailFileWriter writerForFile(File file) {
    EmailFileWriter	result;

    result = null;

    initFilters(false, ClassLister.getSingleton().getClassnames(EmailFileWriter.class));

    for (ExtensionFileFilterWithClass filter: m_WriterFileFilters) {
      if (filter.accept(file)) {
	try {
	  result = (EmailFileWriter) Class.forName(filter.getClassname()).newInstance();
	}
	catch (Exception e) {
          handleException("Failed to instantiate writer: " + filter.getClassname(), e);
	}
      }
    }

    return result;
  }
}
