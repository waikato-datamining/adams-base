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
 * SerializationFileChooser.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.chooser;

import adams.data.io.input.AbstractObjectReader;
import adams.data.io.input.SerializedObjectReader;
import adams.data.io.input.XStreamReader;
import adams.data.io.output.AbstractObjectWriter;
import adams.data.io.output.SerializedObjectWriter;
import adams.data.io.output.XStreamWriter;
import adams.gui.core.GUIHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A file chooser for serializable objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SerializationFileChooser
  extends AbstractConfigurableExtensionFileFilterFileChooser<AbstractObjectReader,AbstractObjectWriter> {

  /** for serialization. */
  private static final long serialVersionUID = -4519042048473978377L;

  /** the file filters for the readers. */
  protected static List<ExtensionFileFilterWithClass> m_ReaderFileFilters;

  /** the file filters for the writers. */
  protected static List<ExtensionFileFilterWithClass> m_WriterFileFilters;

  /**
   * Constructs a <code>ObjectFileChooser</code> pointing to the user's
   * default directory. This default depends on the operating system.
   * It is typically the "My Documents" folder on Windows, and the
   * user's home directory on Unix.
   */
  public SerializationFileChooser() {
    super();
  }

  /**
   * Constructs a <code>ObjectFileChooser</code> using the given path.
   * Passing in a <code>null</code>
   * string causes the file chooser to point to the user's default directory.
   * This default depends on the operating system. It is
   * typically the "My Documents" folder on Windows, and the user's
   * home directory on Unix.
   *
   * @param currentDirectoryPath  a <code>String</code> giving the path
   *				to a file or directory
   */
  public SerializationFileChooser(String currentDirectoryPath) {
    super(currentDirectoryPath);
  }

  /**
   * Constructs a <code>ObjectFileChooser</code> using the given <code>File</code>
   * as the path. Passing in a <code>null</code> file
   * causes the file chooser to point to the user's default directory.
   * This default depends on the operating system. It is
   * typically the "My Documents" folder on Windows, and the user's
   * home directory on Unix.
   *
   * @param currentDirectory  a <code>File</code> object specifying
   *				the path to a file or directory
   */
  public SerializationFileChooser(File currentDirectory) {
    super(currentDirectory);
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
   * Returns the default file filter to use.
   *
   * @param dialogType	the dialog type: open/save
   * @return		the default file filter, null if unable find default one
   */
  @Override
  protected ExtensionFileFilterWithClass getDefaultFileFilter(int dialogType) {
    ExtensionFileFilterWithClass	result;
    boolean				found;
    String				preferred;

    result = null;
    found  = false;

    if (dialogType == OPEN_DIALOG) {
      preferred = GUIHelper.getString("PreferredObjectReader", "model");
      for (ExtensionFileFilterWithClass reader: m_ReaderFileFilters) {
	for (String ext: reader.getExtensions()) {
	  if (ext.equalsIgnoreCase(preferred)) {
	    found  = true;
	    result = reader;
	    break;
	  }
	}
	if (found)
	  break;
      }
    }
    else if (dialogType == SAVE_DIALOG) {
      preferred = GUIHelper.getString("PreferredObjectWriter", "model");
      for (ExtensionFileFilterWithClass writer: m_WriterFileFilters) {
	for (String ext: writer.getExtensions()) {
	  if (ext.equalsIgnoreCase(preferred)) {
	    found  = true;
	    result = writer;
	    break;
	  }
	}
	if (found)
	  break;
      }
    }

    if (!found)
      result = super.getDefaultFileFilter(dialogType);

    return result;
  }

  /**
   * Returns the current object reader.
   *
   * @return		the object reader, null if not applicable
   */
  public AbstractObjectReader getObjectReader() {
    configureCurrentHandlerHook(OPEN_DIALOG);

    if (m_CurrentHandler instanceof AbstractObjectReader)
      return (AbstractObjectReader) m_CurrentHandler;
    else
      return null;
  }

  /**
   * Returns the current object writer.
   *
   * @return		the object writer, null if not applicable
   */
  public AbstractObjectWriter getObjectWriter() {
    configureCurrentHandlerHook(SAVE_DIALOG);

    if (m_CurrentHandler instanceof AbstractObjectWriter)
      return (AbstractObjectWriter) m_CurrentHandler;
    else
      return null;
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
  protected synchronized void doInitializeFilters() {
    initializeFilters();
  }

  /**
   * Performs the actual initialization of the filters.
   */
  protected static synchronized void initializeFilters() {
    ExtensionFileFilterWithClass 	filter;
    AbstractObjectReader[]		readers;
    AbstractObjectWriter[]		writers;

    if (m_ReaderFileFilters == null) {
      m_ReaderFileFilters = new ArrayList<>();
      readers = new AbstractObjectReader[]{
	new SerializedObjectReader(),
	new XStreamReader(),
      };
      for (AbstractObjectReader reader: readers) {
	filter = new ExtensionFileFilterWithClass(reader.getClass().getName(), reader.getFormatDescription(), reader.getFormatExtensions());
	m_ReaderFileFilters.add(filter);
      }
    }

    if (m_WriterFileFilters == null) {
      m_WriterFileFilters = new ArrayList<>();
      writers = new AbstractObjectWriter[]{
	new SerializedObjectWriter(),
	new XStreamWriter(),
      };
      for (AbstractObjectWriter writer: writers) {
	filter = new ExtensionFileFilterWithClass(writer.getClass().getName(), writer.getFormatDescription(), writer.getFormatExtensions());
	m_WriterFileFilters.add(filter);
      }
    }
  }

  /**
   * Returns the default reader.
   *
   * @return		the default reader
   */
  @Override
  protected AbstractObjectReader getDefaultReader() {
    return new SerializedObjectReader();
  }

  /**
   * Returns the default writer.
   *
   * @return		the default writer
   */
  @Override
  protected AbstractObjectWriter getDefaultWriter() {
    return new SerializedObjectWriter();
  }

  /**
   * Returns the reader for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the reader, null if none found
   */
  public AbstractObjectReader getReaderForFile(File file) {
    return readerForFile(file);
  }

  /**
   * Returns the writer for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the writer, null if none found
   */
  public AbstractObjectWriter getWriterForFile(File file) {
    return writerForFile(file);
  }

  /**
   * Returns the reader superclass for the GOE.
   *
   * @return		the reader class
   */
  @Override
  protected Class getReaderClass() {
    return AbstractObjectReader.class;
  }

  /**
   * Returns the writer superclass for the GOE.
   *
   * @return		the writer class
   */
  @Override
  protected Class getWriterClass() {
    return AbstractObjectWriter.class;
  }

  /**
   * Returns the reader for the specified file.
   *
   * @param file	the file to determine a reader for
   * @return		the reader, null if none found
   */
  public static AbstractObjectReader readerForFile(File file) {
    AbstractObjectReader	result;

    result = null;

    initializeFilters();

    for (ExtensionFileFilterWithClass filter: m_ReaderFileFilters) {
      if (filter.accept(file)) {
	try {
	  result = (AbstractObjectReader) Class.forName(filter.getClassname()).newInstance();
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
  public static AbstractObjectWriter writerForFile(File file) {
    AbstractObjectWriter	result;

    result = null;

    initializeFilters();

    for (ExtensionFileFilterWithClass filter: m_WriterFileFilters) {
      if (filter.accept(file)) {
	try {
	  result = (AbstractObjectWriter) Class.forName(filter.getClassname()).newInstance();
	}
	catch (Exception e) {
          handleException("Failed to instantiate writer: " + filter.getClassname(), e);
	}
      }
    }

    return result;
  }
}
