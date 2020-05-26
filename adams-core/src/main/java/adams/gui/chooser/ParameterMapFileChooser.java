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
 * ParameterMapFileChooser.java
 * Copyright (C) 2019-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.ClassLister;
import adams.core.classmanager.ClassManager;
import adams.data.io.input.AbstractParameterMapReader;
import adams.data.io.input.SerializedParameterMapReader;
import adams.data.io.output.AbstractParameterMapWriter;
import adams.data.io.output.SerializedParameterMapWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for parameter maps.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ParameterMapFileChooser
  extends AbstractConfigurableExtensionFileFilterFileChooser<AbstractParameterMapReader,AbstractParameterMapWriter>
  implements FileTypeDeterminingFileChooser<AbstractParameterMapReader,AbstractParameterMapWriter> {

  /** for serialization. */
  private static final long serialVersionUID = -6341967475735162796L;

  /** the file filters for the readers. */
  protected static List<ExtensionFileFilterWithClass> m_ReaderFileFilters;

  /** the file filters for the writers. */
  protected static List<ExtensionFileFilterWithClass> m_WriterFileFilters;

  /**
   * Constructs a FileChooser pointing to the user's default directory.
   */
  public ParameterMapFileChooser() {
    super();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  public ParameterMapFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  public ParameterMapFileChooser(String currentDirectory) {
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
    initFilters(true, ClassLister.getSingleton().getClasses(AbstractParameterMapReader.class));
    initFilters(false, ClassLister.getSingleton().getClasses(AbstractParameterMapWriter.class));
  }

  /**
   * initializes the Filters.
   *
   * @param reader	if true then the reader filters are initialized
   * @param classes	the classes of the converters
   */
  protected static synchronized void initFilters(boolean reader, Class[] classes) {
    int					i;
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

    for (i = 0; i < classes.length; i++) {
      cls = classes[i];

      // get data from converter
      try {
	converter = cls.newInstance();
	if (reader) {
	  desc = ((AbstractParameterMapReader) converter).getFormatDescription();
	  ext  = ((AbstractParameterMapReader) converter).getFormatExtensions();
	}
	else {
	  desc = ((AbstractParameterMapWriter) converter).getFormatDescription();
	  ext  = ((AbstractParameterMapWriter) converter).getFormatExtensions();
	}
      }
      catch (Exception e) {
        handleException("Failed to set up: " + cls.getName(), e);
	cls       = null;
	converter = null;
	ext       = new String[0];
	desc      = "";
      }

      if (converter == null)
	continue;

      // reader?
      if (reader) {
	filter = new ExtensionFileFilterWithClass(cls.getName(), desc, ext);
	m_ReaderFileFilters.add(filter);
      }
      else {
	filter = new ExtensionFileFilterWithClass(cls.getName(), desc, ext);
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
   * Returns the reader superclass for the GOE.
   *
   * @return		the reader class
   */
  @Override
  protected Class getReaderClass() {
    return AbstractParameterMapReader.class;
  }

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return		the writer class
   */
  @Override
  protected Class getWriterClass() {
    return AbstractParameterMapWriter.class;
  }

  /**
   * Returns the default reader.
   *
   * @return		the default reader
   */
  @Override
  protected AbstractParameterMapReader getDefaultReader() {
    return new SerializedParameterMapReader();
  }

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected AbstractParameterMapWriter getDefaultWriter() {
    return new SerializedParameterMapWriter();
  }

  /**
   * Returns the reader for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the reader, null if none found
   */
  public AbstractParameterMapReader getReaderForFile(File file) {
    return readerForFile(file);
  }

  /**
   * Returns the writer for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the writer, null if none found
   */
  public AbstractParameterMapWriter getWriterForFile(File file) {
    return writerForFile(file);
  }

  /**
   * Returns the reader for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the reader, null if none found
   */
  public static AbstractParameterMapReader readerForFile(File file) {
    AbstractParameterMapReader	result;

    result = null;

    initFilters(true, ClassLister.getSingleton().getClasses(AbstractParameterMapReader.class));

    for (ExtensionFileFilterWithClass filter: m_ReaderFileFilters) {
      if (filter.accept(file)) {
	try {
	  result = (AbstractParameterMapReader) ClassManager.getSingleton().forName(filter.getClassname()).newInstance();
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
  public static AbstractParameterMapWriter writerForFile(File file) {
    AbstractParameterMapWriter	result;

    result = null;

    initFilters(false, ClassLister.getSingleton().getClasses(AbstractParameterMapWriter.class));

    for (ExtensionFileFilterWithClass filter: m_WriterFileFilters) {
      if (filter.accept(file)) {
	try {
	  result = (AbstractParameterMapWriter) ClassManager.getSingleton().forName(filter.getClassname()).newInstance();
	}
	catch (Exception e) {
          handleException("Failed to instantiate writer: " + filter.getClassname(), e);
	}
      }
    }

    return result;
  }
}
