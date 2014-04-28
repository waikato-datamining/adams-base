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
 * FlowFileChooser.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adams.data.io.input.AbstractFlowReader;
import adams.data.io.input.DefaultFlowReader;
import adams.data.io.input.FlowReader;
import adams.data.io.output.AbstractFlowWriter;
import adams.data.io.output.DefaultFlowWriter;
import adams.data.io.output.FlowWriter;
import adams.gui.chooser.AbstractConfigurableExtensionFileFilterFileChooser;
import adams.gui.chooser.ExtensionFileFilterWithClass;
import adams.gui.chooser.FileTypeDeterminingFileChooser;

/**
 * A specialized JFileChooser that lists all available file Readers and Writers
 * for flows.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowFileChooser
  extends AbstractConfigurableExtensionFileFilterFileChooser<FlowReader,FlowWriter>
  implements FileTypeDeterminingFileChooser<FlowReader,FlowWriter> {

  /** for serialization. */
  private static final long serialVersionUID = -6341967475735162796L;

  /** the file filters for the readers. */
  protected static List<ExtensionFileFilterWithClass> m_ReaderFileFilters;

  /** the file filters for the writers. */
  protected static List<ExtensionFileFilterWithClass> m_WriterFileFilters;

  /**
   * Constructs a FileChooser pointing to the user's default directory.
   */
  public FlowFileChooser() {
    super();
  }

  /**
   * Constructs a FileChooser using the given File as the path.
   *
   * @param currentDirectory	the path to start in
   */
  public FlowFileChooser(File currentDirectory) {
    super(currentDirectory);
  }

  /**
   * Constructs a FileChooser using the given path.
   *
   * @param currentDirectory	the path to start in
   */
  public FlowFileChooser(String currentDirectory) {
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
    initFilters(true, AbstractFlowReader.getReaders());
    initFilters(false, AbstractFlowWriter.getWriters());
  }

  /**
   * initializes the Filters.
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

    if (reader)
      m_ReaderFileFilters = new ArrayList<ExtensionFileFilterWithClass>();
    else
      m_WriterFileFilters  = new ArrayList<ExtensionFileFilterWithClass>();

    for (i = 0; i < classnames.length; i++) {
      classname = (String) classnames[i];

      // get data from converter
      try {
	cls       = Class.forName(classname);
	converter = cls.newInstance();
	if (reader) {
	  desc = ((FlowReader) converter).getFormatDescription();
	  ext  = ((FlowReader) converter).getFormatExtensions();
	}
	else {
	  desc = ((FlowWriter) converter).getFormatDescription();
	  ext  = ((FlowWriter) converter).getFormatExtensions();
	}
      }
      catch (Exception e) {
	System.err.println("Failed to set up '" + classname + "':");
	e.printStackTrace();
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
   * Returns the reader superclass for the GOE.
   *
   * @return		the reader class
   */
  @Override
  protected Class getReaderClass() {
    return FlowReader.class;
  }

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return		the writer class
   */
  @Override
  protected Class getWriterClass() {
    return FlowWriter.class;
  }

  /**
   * Returns the default reader.
   *
   * @return		the default reader
   */
  @Override
  protected FlowReader getDefaultReader() {
    return new DefaultFlowReader();
  }

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected FlowWriter getDefaultWriter() {
    return new DefaultFlowWriter();
  }

  /**
   * Returns the reader for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the reader, null if none found
   */
  public FlowReader getReaderForFile(File file) {
    FlowReader	result;

    result = null;

    for (ExtensionFileFilterWithClass filter: m_ReaderFileFilters) {
      if (filter.accept(file)) {
	try {
	  result = (FlowReader) Class.forName(filter.getClassname()).newInstance();
	}
	catch (Exception e) {
	  System.err.println("Failed to instantiate reader '" + filter.getClassname() + "':");
	  e.printStackTrace();
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
  public FlowWriter getWriterForFile(File file) {
    FlowWriter	result;

    result = null;

    for (ExtensionFileFilterWithClass filter: m_WriterFileFilters) {
      if (filter.accept(file)) {
	try {
	  result = (FlowWriter) Class.forName(filter.getClassname()).newInstance();
	}
	catch (Exception e) {
	  System.err.println("Failed to instantiate writer '" + filter.getClassname() + "':");
	  e.printStackTrace();
	}
      }
    }

    return result;
  }
}
