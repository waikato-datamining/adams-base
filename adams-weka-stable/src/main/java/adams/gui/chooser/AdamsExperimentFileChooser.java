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
 * AdamsExperimentFileChooser.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.ClassLister;
import adams.data.io.input.AbstractAdamsExperimentReader;
import adams.data.io.input.NestedAdamsExperimentReader;
import adams.data.io.output.AbstractAdamsExperimentWriter;
import adams.data.io.output.NestedAdamsExperimentWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for ADAMS Experiments.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AdamsExperimentFileChooser
  extends AbstractConfigurableExtensionFileFilterFileChooser<AbstractAdamsExperimentReader,AbstractAdamsExperimentWriter>
  implements FileTypeDeterminingFileChooser<AbstractAdamsExperimentReader,AbstractAdamsExperimentWriter> {

  /** for serialization. */
  private static final long serialVersionUID = -6341967475735162796L;

  /** the file filters for the readers. */
  protected static List<ExtensionFileFilterWithClass> m_ReaderFileFilters;

  /** the file filters for the writers. */
  protected static List<ExtensionFileFilterWithClass> m_WriterFileFilters;

  /**
   * Constructs a FileChooser pointing to the user's default directory.
   */
  public AdamsExperimentFileChooser() {
    super();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  public AdamsExperimentFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  public AdamsExperimentFileChooser(String currentDirectory) {
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
    try {
      initFilters(true, ClassLister.getSingleton().getClasses(AbstractAdamsExperimentReader.class));
      initFilters(false, ClassLister.getSingleton().getClasses(AbstractAdamsExperimentWriter.class));
    }
    catch (Exception e) {
      handleException("Failed to initialize Weka loader/saver filters!", e);
    }
  }

  /**
   * initializes the filters.
   *
   * @param reader	if true then the reader filters are initialized
   * @param classes	the classes of the schemes
   */
  protected static void initFilters(boolean reader, Class[] classes) {
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
	  desc = ((AbstractAdamsExperimentReader) converter).getFormatDescription();
	  ext  = ((AbstractAdamsExperimentReader) converter).getFormatExtensions();
	}
	else {
	  desc = ((AbstractAdamsExperimentWriter) converter).getFormatDescription();
	  ext  = ((AbstractAdamsExperimentWriter) converter).getFormatExtensions();
	}
      }
      catch (Exception e) {
	handleException("Failed to set up: " + cls, e);
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
    return AbstractAdamsExperimentReader.class;
  }

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return		the writer class
   */
  @Override
  protected Class getWriterClass() {
    return AbstractAdamsExperimentWriter.class;
  }

  /**
   * Returns the default reader.
   *
   * @return		the default reader
   */
  @Override
  protected AbstractAdamsExperimentReader getDefaultReader() {
    return new NestedAdamsExperimentReader();
  }

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected AbstractAdamsExperimentWriter getDefaultWriter() {
    return new NestedAdamsExperimentWriter();
  }

  /**
   * Returns the reader for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the reader, null if none found
   */
  public AbstractAdamsExperimentReader getReaderForFile(File file) {
    return readerForFile(file);
  }

  /**
   * Returns the writer for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the writer, null if none found
   */
  public AbstractAdamsExperimentWriter getWriterForFile(File file) {
    return writerForFile(file);
  }

  /**
   * Returns the reader for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the reader, null if none found
   */
  public static AbstractAdamsExperimentReader readerForFile(File file) {
    AbstractAdamsExperimentReader	result;

    result = null;

    try {
      initFilters(true, ClassLister.getSingleton().getClasses(AbstractAdamsExperimentReader.class));
    }
    catch (Exception e) {
      handleException("Failed to initialize ADAMS Experiment readers/writers!", e);
    }

    for (ExtensionFileFilterWithClass filter: m_ReaderFileFilters) {
      if (filter.accept(file)) {
	try {
	  result = (AbstractAdamsExperimentReader) Class.forName(filter.getClassname()).newInstance();
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
  public static AbstractAdamsExperimentWriter writerForFile(File file) {
    AbstractAdamsExperimentWriter	result;

    result = null;

    try {
      initFilters(false, ClassLister.getSingleton().getClasses(AbstractAdamsExperimentWriter.class.getName()));
    }
    catch (Exception e) {
      handleException("Failed to initialize ADAMS Experiment readers/writers!", e);
    }

    for (ExtensionFileFilterWithClass filter: m_WriterFileFilters) {
      if (filter.accept(file)) {
	try {
	  result = (AbstractAdamsExperimentWriter) Class.forName(filter.getClassname()).newInstance();
	}
	catch (Exception e) {
	  handleException("Failed to instantiate writer: " + filter.getClassname(), e);
	}
      }
    }

    return result;
  }
}
