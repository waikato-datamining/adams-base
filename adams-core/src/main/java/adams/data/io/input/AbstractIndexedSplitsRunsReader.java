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
 * AbstractIndexedSplitsRunsReader.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.MessageCollection;
import adams.core.io.FileFormatHandler;
import adams.core.io.FileUtils;
import adams.core.option.AbstractOptionHandler;
import adams.data.indexedsplits.IndexedSplitsRuns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

/**
 * Ancestor for readers that load files containing IndexedSplitsRuns.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractIndexedSplitsRunsReader
  extends AbstractOptionHandler
  implements FileFormatHandler {

  private static final long serialVersionUID = -8633292996589233838L;

  /**
   * Reads the split definitions from the specified reader.
   * The caller must close the reader object.
   *
   * @param reader	the reader to read from
   * @param errors	for storing errors
   * @return		the definitions or null in case of an error
   */
  protected abstract IndexedSplitsRuns doRead(Reader reader, MessageCollection errors);

  /**
   * Reads the split definitions from the specified reader.
   * The caller must close the reader object.
   *
   * @param reader	the reader to read from
   * @param errors	for storing errors
   * @return		the definitions or null in case of an error
   */
  public IndexedSplitsRuns read(Reader reader, MessageCollection errors) {
    try {
      return doRead(reader, errors);
    }
    catch (Exception e) {
      errors.add("Failed to read split definitions from reader!", e);
      return null;
    }
  }

  /**
   * Reads the split definitions from the specified file.
   *
   * @param file	the file to read
   * @param errors	for storing errors
   * @return		the definitions or null in case of an error
   */
  public IndexedSplitsRuns read(File file, MessageCollection errors) {
    FileReader		freader;
    BufferedReader	breader;

    if (!file.exists()) {
      errors.add("File does not exist: " + file);
      return null;
    }
    if (file.isDirectory()) {
      errors.add("Input points to a directory: " + file);
      return null;
    }

    freader = null;
    breader = null;
    try {
      freader = new FileReader(file.getAbsolutePath());
      breader = new BufferedReader(freader);
      return read(breader, errors);
    }
    catch (Exception e) {
      errors.add("Failed to read: " + file, e);
      return null;
    }
    finally {
      FileUtils.closeQuietly(freader);
      FileUtils.closeQuietly(breader);
    }
  }
}
