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
 * DataContainerFileChooser.java
 * Copyright (C) 2009-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.classmanager.ClassManager;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.container.DataContainer;
import adams.data.io.input.DataContainerReader;
import adams.data.io.output.DataContainerWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for data containers.
 * <br><br>
 * Based on <code>weka.gui.ConverterFileChooser</code>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see	    weka.gui.ConverterFileChooser
 * @param <T> the type of container
 */
public abstract class AbstractDataContainerFileChooser<T extends DataContainer, R extends DataContainerReader, W extends DataContainerWriter>
  extends AbstractConfigurableExtensionFileFilterFileChooser<R, W> {

  /** for serialization. */
  private static final long serialVersionUID = -5373058011025481738L;

  /** the file filters for the readers. */
  protected static Hashtable<Class,List<ExtensionFileFilterWithClass>> m_ReaderFileFilters = new Hashtable<>();

  /** the file filters for the writers. */
  protected static Hashtable<Class,List<ExtensionFileFilterWithClass>> m_WriterFileFilters = new Hashtable<>();

  /**
   * Constructs a FileChooser pointing to the user's default directory.
   */
  protected AbstractDataContainerFileChooser() {
    super();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  protected AbstractDataContainerFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  protected AbstractDataContainerFileChooser(String currentDirectory) {
    super(currentDirectory);
  }

  /**
   * initializes the DataContainerFileExtensionFilters.
   *
   * @param chooser	the chooser instance to use as reference
   * @param reader	if true then the reader filters are initialized
   * @param classnames	the classnames of the converters
   */
  protected static void initFilters(AbstractDataContainerFileChooser chooser, boolean reader, String[] classnames) {
    int					i;
    String 				classname;
    Class 				cls;
    String[] 				ext;
    String 				desc;
    Object		 		converter;
    ExtensionFileFilterWithClass 	filter;

    if (reader)
      m_ReaderFileFilters.put(chooser.getClass(), new ArrayList<>());
    else
      m_WriterFileFilters.put(chooser.getClass(), new ArrayList<>());

    for (i = 0; i < classnames.length; i++) {
      classname = (String) classnames[i];

      // get data from converter
      try {
	cls       = ClassManager.getSingleton().forName(classname);
	converter = cls.getDeclaredConstructor().newInstance();
	if (reader) {
	  desc = ((DataContainerReader) converter).getFormatDescription();
	  ext  = ((DataContainerReader) converter).getFormatExtensions();
	}
	else {
	  desc = ((DataContainerWriter) converter).getFormatDescription();
	  ext  = ((DataContainerWriter) converter).getFormatExtensions();
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
	m_ReaderFileFilters.get(chooser.getClass()).add(filter);
      }
      else {
	filter = new ExtensionFileFilterWithClass(classname, desc, ext);
	m_WriterFileFilters.get(chooser.getClass()).add(filter);
      }
    }

    if (reader)
      Collections.sort(m_ReaderFileFilters.get(chooser.getClass()));
    else
      Collections.sort(m_WriterFileFilters.get(chooser.getClass()));
  }

  /**
   * Returns the file filters for opening files.
   *
   * @return		the file filters
   */
  @Override
  protected List<ExtensionFileFilterWithClass> getOpenFileFilters() {
    return m_ReaderFileFilters.get(getClass());
  }

  /**
   * Returns the file filters for writing files.
   *
   * @return		the file filters
   */
  @Override
  protected List<ExtensionFileFilterWithClass> getSaveFileFilters() {
    return m_WriterFileFilters.get(getClass());
  }

  /**
   * sets the current converter according to the current filefilter.
   */
  @Override
  protected void updateCurrentHandlerHook() {
    String	classname;
    Object	newHandler;
    boolean	onlyFiles;

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

      // files or directories?
      if (m_DialogType == OPEN_DIALOG)
	onlyFiles = ((DataContainerReader) m_CurrentHandler).isInputFile();
      else
	onlyFiles = ((DataContainerWriter) m_CurrentHandler).isOutputFile();
      if (onlyFiles)
	setFileSelectionMode(FILES_ONLY);
      else
	setFileSelectionMode(DIRECTORIES_ONLY);
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
    PlaceholderFile	selFile;
    String		classname;
    File		currFile;
    boolean		onlyFiles;

    selFile = getSelectedPlaceholderFile();

    if (m_CurrentHandler == null) {
      classname = ((ExtensionFileFilterWithClass) getFileFilter()).getClassname();
      try {
	m_CurrentHandler = ClassManager.getSingleton().forName(classname).getDeclaredConstructor().newInstance();
      }
      catch (Exception e) {
        m_CurrentHandler = null;
        handleException("Failed to update current handler:", e);
      }

      // none found?
      if (m_CurrentHandler == null)
	return;
    }

    // wrong type?
    if (m_CurrentHandler instanceof DataContainerReader)
      onlyFiles = ((DataContainerReader) m_CurrentHandler).isInputFile();
    else
      onlyFiles = ((DataContainerWriter) m_CurrentHandler).isOutputFile();
    if ((onlyFiles && selFile.isDirectory()) || (!onlyFiles && !selFile.isDirectory()))
      return;

    try {
      if (m_CurrentHandler instanceof DataContainerReader)
	currFile = ((DataContainerReader) m_CurrentHandler).getInput();
      else
	currFile = ((DataContainerWriter) m_CurrentHandler).getOutput();
      if ((currFile == null) || (!currFile.getAbsolutePath().equals(selFile.getAbsolutePath()))) {
	if (m_CurrentHandler instanceof DataContainerReader)
	  ((DataContainerReader) m_CurrentHandler).setInput(selFile);
	else
	  ((DataContainerWriter) m_CurrentHandler).setOutput(selFile);
      }
    }
    catch (Exception e) {
      handleException("Failed to update current handler: " + OptionUtils.getCommandLine(m_CurrentHandler), e);
    }
  }

  /**
   * Returns whether the filters have already been initialized.
   *
   * @return		true if the filters have been initialized
   */
  @Override
  protected boolean getFiltersInitialized() {
    return (m_ReaderFileFilters.containsKey(getClass()));
  }

  /**
   * Ignored.
   *
   * @param value	ignored
   */
  @Override
  public void setAllowGlobFilters(boolean value) {
  }
}
