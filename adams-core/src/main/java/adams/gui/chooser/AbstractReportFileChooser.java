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
 * AbstractReportFileChooser.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.io.input.AbstractReportReader;
import adams.data.io.output.AbstractReportWriter;
import adams.data.report.Report;
import adams.gui.core.GUIHelper;
import adams.gui.goe.GenericObjectEditorDialog;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for reports.
 * <br><br>
 * Based on <code>weka.gui.ConverterFileChooser</code>
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see	    weka.gui.ConverterFileChooser
 * @param <T> the type of report
 */
public abstract class AbstractReportFileChooser<T extends Report, R extends AbstractReportReader, W extends AbstractReportWriter>
  extends AbstractConfigurableExtensionFileFilterFileChooser<R, W> {

  /** for serialization. */
  private static final long serialVersionUID = -5373058011025481738L;

  /** the file filters for the readers. */
  protected static Hashtable<Class,List<ExtensionFileFilterWithClass>> m_ReaderFileFilters = new Hashtable<Class,List<ExtensionFileFilterWithClass>>();

  /** the file filters for the writers. */
  protected static Hashtable<Class,List<ExtensionFileFilterWithClass>> m_WriterFileFilters = new Hashtable<Class,List<ExtensionFileFilterWithClass>>();

  /**
   * onstructs a FileChooser pointing to the user's default directory.
   */
  protected AbstractReportFileChooser() {
    super();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  protected AbstractReportFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  protected AbstractReportFileChooser(String currentDirectory) {
    super(currentDirectory);
  }

  /**
   * initializes the ReportFileExtensionFilters.
   *
   * @param chooser	the chooser instance to use as reference
   * @param reader	if true then the reader filters are initialized
   * @param classnames	the classnames of the converters
   */
  protected static void initFilters(AbstractReportFileChooser chooser, boolean reader, String[] classnames) {
    int				i;
    String 			classname;
    Class 			cls;
    String[] 			ext;
    String 			desc;
    Object		 	converter;
    ExtensionFileFilterWithClass 	filter;

    if (reader)
      m_ReaderFileFilters.put(chooser.getClass(), new ArrayList<ExtensionFileFilterWithClass>());
    else
      m_WriterFileFilters.put(chooser.getClass(), new ArrayList<ExtensionFileFilterWithClass>());

    for (i = 0; i < classnames.length; i++) {
      classname = (String) classnames[i];

      // get data from converter
      try {
	cls       = Class.forName(classname);
	converter = cls.newInstance();
	if (reader) {
	  desc = ((AbstractReportReader) converter).getFormatDescription();
	  ext  = ((AbstractReportReader) converter).getFormatExtensions();
	}
	else {
	  desc = ((AbstractReportWriter) converter).getFormatDescription();
	  ext  = ((AbstractReportWriter) converter).getFormatExtensions();
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
   * Returns the default reader.
   *
   * @return		the default reader
   */
  @Override
  protected abstract R getDefaultReader();

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected abstract W getDefaultWriter();

  /**
   * Returns the reader superclass for the GOE.
   *
   * @return		the reader class
   */
  @Override
  protected Class getReaderClass() {
    return AbstractReportReader.class;
  }

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return		the writer class
   */
  @Override
  protected Class getWriterClass() {
    return AbstractReportWriter.class;
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
    PlaceholderFile	selFile;
    String		classname;
    File		currFile;

    selFile = getSelectedPlaceholderFile();

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

    // wrong type?
    if (selFile.isDirectory())
      return;

    try {
      if (m_CurrentHandler instanceof AbstractReportReader)
	currFile = ((AbstractReportReader) m_CurrentHandler).getInput();
      else
	currFile = ((AbstractReportWriter) m_CurrentHandler).getOutput();
      if ((currFile == null) || (!currFile.getAbsolutePath().equals(selFile.getAbsolutePath()))) {
	if (m_CurrentHandler instanceof AbstractReportReader)
	  ((AbstractReportReader) m_CurrentHandler).setInput(selFile);
	else
	  ((AbstractReportWriter) m_CurrentHandler).setOutput(selFile);
      }
    }
    catch (Exception e) {
      handleException("Failed to configure current handler: " + OptionUtils.getCommandLine(m_CurrentHandler), e);
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
