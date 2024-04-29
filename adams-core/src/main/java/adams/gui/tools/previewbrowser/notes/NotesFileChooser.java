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
 * NotesFileChooser.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.previewbrowser.notes;

import adams.core.ClassLister;
import adams.core.classmanager.ClassManager;
import adams.gui.chooser.AbstractConfigurableExtensionFileFilterFileChooser;
import adams.gui.chooser.ExtensionFileFilterWithClass;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for image notes in the preview browser.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class NotesFileChooser
  extends AbstractConfigurableExtensionFileFilterFileChooser<AbstractNotesReader, AbstractNotesWriter> {

  /** the file filters for the readers. */
  protected static List<ExtensionFileFilterWithClass> m_ReaderFileFilters;

  /** the file filters for the writers. */
  protected static List<ExtensionFileFilterWithClass> m_WriterFileFilters;

  /**
   * Constructs a FileChooser pointing to the user's default directory.
   */
  public NotesFileChooser() {
    super();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  public NotesFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  public NotesFileChooser(String currentDirectory) {
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
    initFilters(true, ClassLister.getSingleton().getClassnames(AbstractNotesReader.class));
    initFilters(false, ClassLister.getSingleton().getClassnames(AbstractNotesWriter.class));
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
      classname = classnames[i];

      // get data from converter
      try {
	cls       = ClassManager.getSingleton().forName(classname);
	converter = cls.getDeclaredConstructor().newInstance();
	if (reader) {
	  desc = ((AbstractNotesReader) converter).getFormatDescription();
	  ext  = ((AbstractNotesReader) converter).getFormatExtensions();
	}
	else {
	  desc = ((AbstractNotesWriter) converter).getFormatDescription();
	  ext  = ((AbstractNotesWriter) converter).getFormatExtensions();
	}
      }
      catch (Exception e) {
	handleException("Failed to set up: " + classname, e);
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
   * Returns the default reader.
   *
   * @return the default reader
   */
  @Override
  protected AbstractNotesReader getDefaultReader() {
    return new PropertiesNotesReader();
  }

  /**
   * Returns the reader superclass for the GOE.
   *
   * @return the reader class
   */
  @Override
  protected Class getReaderClass() {
    return AbstractNotesReader.class;
  }

  /**
   * Returns the default writer.
   *
   * @return the default writer
   */
  @Override
  protected AbstractNotesWriter getDefaultWriter() {
    return new PropertiesNotesWriter();
  }

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return the writer class
   */
  @Override
  protected Class getWriterClass() {
    return AbstractNotesWriter.class;
  }
}
