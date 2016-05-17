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
 * AbstractFlowReader.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.ClassLister;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.flow.core.Actor;
import adams.gui.flow.tree.Node;
import org.apache.commons.io.input.ReaderInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Ancestor for classes that can read flows.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFlowReader
  extends AbstractOptionHandler 
  implements FlowReader {

  /** for serialization. */
  private static final long serialVersionUID = 4828477005893179066L;

  /**
   * How to read the data.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum InputType {
    /** read from a file. */
    FILE,
    /** read using a reader. */
    READER,
    /** read from a stream. */
    STREAM
  }

  /** for storing warnings. */
  protected List<String> m_Warnings;
  
  /** for storing errors. */
  protected List<String> m_Errors;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Warnings = new ArrayList<>();
    m_Errors   = new ArrayList<>();
  }
  
  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  public abstract String getFormatDescription();

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  public abstract String[] getFormatExtensions();

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  public String getDefaultFormatExtension() {
    return getFormatExtensions()[0];
  }

  /**
   * Returns how to read the data, from a file, stream or reader.
   *
   * @return		how to read the data
   */
  protected abstract InputType getInputType();

  /**
   * Hook method to perform some checks before performing the actual read.
   * <br><br>
   * Default implementation only clears warnings/errors.
   */
  protected void check() {
    m_Warnings.clear();
    m_Errors.clear();
  }
  
  /**
   * Reads the flow from the specified file.
   *
   * @param file	the file to read from
   * @return		null in case of an error, otherwise the flow
   */
  public Node readNode(File file) {
    return readNode(file.getAbsolutePath());
  }

  /**
   * Reads the flow from the given file.
   *
   * @param filename	the file to read from
   * @return		the flow or null in case of an error
   */
  public Node readNode(String filename) {
    Node		result;
    BufferedReader	reader;
    InputStream		input;

    check();
    
    reader = null;
    input  = null;
    try {
      if (!FileUtils.fileExists(filename))
        throw new IllegalArgumentException("File does not exist: " + filename);
      switch (getInputType()) {
	case FILE:
	  result = doReadNode(new PlaceholderFile(filename));
	  break;
	case STREAM:
	  input = new FileInputStream(filename);
	  result = doReadNode(input);
	  break;
	case READER:
	  reader = new BufferedReader(new FileReader(filename));
	  result = doReadNode(reader);
	  break;
	default:
	  throw new IllegalStateException("Unhandled input type: " + getInputType());
      }
    }
    catch (Exception e) {
      result = null;
      getLogger().log(Level.SEVERE, "Failed to read node from: " + filename, e);
    }
    finally {
      if (!(this instanceof ChunkedSpreadSheetReader)) {
        FileUtils.closeQuietly(reader);
        FileUtils.closeQuietly(input);
      }
    }
    
    return result;
  }

  /**
   * Reads the flow from the stream. The caller must ensure to
   * close the stream.
   *
   * @param stream	the stream to read from
   * @return		the flow or null in case of an error
   */
  public Node readNode(InputStream stream) {
    Node	result;

    check();

    try {
      switch (getInputType()) {
	case FILE:
	  throw new IllegalStateException("Only supports reading from files, not input streams!");
	case STREAM:
	  result = doReadNode(stream);
	  break;
	case READER:
	  result = doReadNode(new BufferedReader(new InputStreamReader(stream)));
	  break;
	default:
	  throw new IllegalStateException("Unhandled input type: " + getInputType());
      }
    }
    catch (Exception e) {
      result = null;
      getLogger().log(Level.SEVERE, "Failed to read node from input stream!", e);
    }

    return result;
  }

  /**
   * Reads the flow from the given reader. The caller must ensure to
   * close the reader.
   *
   * @param r		the reader to read from
   * @return		the flow or null in case of an error
   */
  public Node readNode(Reader r) {
    Node	result;

    check();

    try {
      switch (getInputType()) {
	case FILE:
	  throw new IllegalStateException("Only supports reading from files, not readers!");
	case STREAM:
	  result = doReadNode(new ReaderInputStream(r));
	  break;
	case READER:
	  result = doReadNode(r);
	  break;
	default:
	  throw new IllegalStateException("Unhandled input type: " + getInputType());
      }
    }
    catch (Exception e) {
      result = null;
      getLogger().log(Level.SEVERE, "Failed to read node from reader!", e);
    }

    return result;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Default implementation returns null.
   *
   * @param file	the file to read from
   * @return		the flow or null in case of an error
   * @see		#getInputType()
   */
  protected Node doReadNode(File file) {
    return null;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Default implementation returns null.
   *
   * @param r		the reader to read from
   * @return		the flow or null in case of an error
   * @see		#getInputType()
   */
  protected Node doReadNode(Reader r) {
    return null;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Default implementation returns null.
   *
   * @param in		the input stream to read from
   * @return		the flow or null in case of an error
   * @see		#getInputType()
   */
  protected Node doReadNode(InputStream in) {
    return null;
  }
  
  /**
   * Reads the flow from the specified file.
   *
   * @param file	the file to read from
   * @return		null in case of an error, otherwise the flow
   */
  public Actor readActor(File file) {
    return readActor(file.getAbsolutePath());
  }

  /**
   * Reads the flow from the given file.
   *
   * @param filename	the file to read from
   * @return		the flow or null in case of an error
   */
  public Actor readActor(String filename) {
    Actor		result;
    BufferedReader	reader;
    InputStream		input;

    check();
    
    reader = null;
    input  = null;
    try {
      if (!FileUtils.fileExists(filename))
	throw new IllegalArgumentException("File does not exist: " + filename);
      switch (getInputType()) {
	case FILE:
	  result = doReadActor(new PlaceholderFile(filename));
	  break;
	case STREAM:
	  input = new FileInputStream(filename);
	  result = doReadActor(input);
	  break;
	case READER:
	  reader = new BufferedReader(new FileReader(filename));
	  result = doReadActor(reader);
	  break;
	default:
	  throw new IllegalStateException("Unhandled input type: " + getInputType());
      }
    }
    catch (Exception e) {
      result = null;
      getLogger().log(Level.SEVERE, "Failed to read actor from file: " + filename, e);
      e.printStackTrace();
    }
    finally {
      if (!(this instanceof ChunkedSpreadSheetReader)) {
        FileUtils.closeQuietly(reader);
        FileUtils.closeQuietly(input);
      }
    }
    
    return result;
  }

  /**
   * Reads the flow from the stream. The caller must ensure to
   * close the stream.
   *
   * @param stream	the stream to read from
   * @return		the flow or null in case of an error
   */
  public Actor readActor(InputStream stream) {
    Actor	result;

    check();

    try {
      switch (getInputType()) {
	case FILE:
	  throw new IllegalStateException("Only supports reading from files, not input streams!");
	case STREAM:
	  result = doReadActor(stream);
	  break;
	case READER:
	  result = doReadActor(new BufferedReader(new InputStreamReader(stream)));
	  break;
	default:
	  throw new IllegalStateException("Unhandled input type: " + getInputType());
      }
    }
    catch (Exception e) {
      result = null;
      getLogger().log(Level.SEVERE, "Failed to read actor from input stream!", e);
    }

    return result;
  }

  /**
   * Reads the flow from the given reader. The caller must ensure to
   * close the reader.
   *
   * @param r		the reader to read from
   * @return		the flow or null in case of an error
   */
  public Actor readActor(Reader r) {
    Actor	result;

    check();

    try {
      switch (getInputType()) {
	case FILE:
	  throw new IllegalStateException("Only supports reading from files, not readers!");
	case STREAM:
	  result = doReadActor(new ReaderInputStream(r));
	  break;
	case READER:
	  result = doReadActor(r);
	  break;
	default:
	  throw new IllegalStateException("Unhandled input type: " + getInputType());
      }
    }
    catch (Exception e) {
      result = null;
      getLogger().log(Level.SEVERE, "Failed to read actor from reader!", e);
    }

    return result;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Default implementation returns null.
   *
   * @param file	the file to read from
   * @return		the flow or null in case of an error
   * @see		#getInputType()
   */
  protected Actor doReadActor(File file) {
    return null;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Default implementation returns null.
   *
   * @param r		the reader to read from
   * @return		the flow or null in case of an error
   * @see		#getInputType()
   */
  protected Actor doReadActor(Reader r) {
    return null;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Default implementation returns null.
   *
   * @param in		the input stream to read from
   * @return		the flow or null in case of an error
   * @see		#getInputType()
   */
  protected Actor doReadActor(InputStream in) {
    return null;
  }
  
  /**
   * Returns any warnings that were encountered while reading.
   * 
   * @return		the warnings
   */
  public List<String> getWarnings() {
    return m_Warnings;
  }
  
  /**
   * Returns any errors that were encountered while reading.
   * 
   * @return		the errors
   */
  public List<String> getErrors() {
    return m_Errors;
  }

  /**
   * Returns a list with classnames of readers.
   *
   * @return		the reader classnames
   */
  public static String[] getReaders() {
    return ClassLister.getSingleton().getClassnames(FlowReader.class);
  }
}
