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
 * AbstractIndexedSplitsRunsWriter.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.MessageCollection;
import adams.core.io.FileFormatHandler;
import adams.core.io.FileUtils;
import adams.core.option.AbstractOptionHandler;
import adams.data.indexedsplits.IndexedSplitsRuns;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

/**
 * Ancestor for writers that writes files containing IndexedSplitsRuns.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractIndexedSplitsRunsWriter
  extends AbstractOptionHandler
  implements FileFormatHandler  {

  private static final long serialVersionUID = -8633292996589233838L;

  /**
   * Writes the split definitions to the specified reader.
   * The caller must close the writer object.
   *
   * @param writer	the writer to write to
   * @param runs 	the runs to write
   * @param errors	for storing errors
   * @return		whether successfully written
   */
  protected abstract boolean doWrite(Writer writer, IndexedSplitsRuns runs, MessageCollection errors);

  /**
   * Writes the split definitions to the specified reader.
   * The caller must close the writer object.
   *
   * @param writer	the writer to write to
   * @param runs 	the runs to write
   * @param errors	for storing errors
   * @return		whether successfully written
   */
  public boolean write(Writer writer, IndexedSplitsRuns runs, MessageCollection errors) {
    try {
      return doWrite(writer, runs, errors);
    }
    catch (Exception e) {
      errors.add("Failed to write split definitions to reader!", e);
      return false;
    }
  }

  /**
   * Writes the split definitions to the specified file.
   *
   * @param file	the file to write to
   * @param runs 	the runs to write
   * @param errors	for storing errors
   * @return		whether successfully written
   */
  public boolean write(File file, IndexedSplitsRuns runs, MessageCollection errors) {
    FileWriter 		fwriter;
    BufferedWriter 	bwriter;

    if (file.isDirectory()) {
      errors.add("Input points to a directory: " + file);
      return false;
    }

    fwriter = null;
    bwriter = null;
    try {
      fwriter = new FileWriter(file.getAbsolutePath());
      bwriter = new BufferedWriter(fwriter);
      return write(bwriter, runs, errors);
    }
    catch (Exception e) {
      errors.add("Failed to write: " + file, e);
      return false;
    }
    finally {
      FileUtils.closeQuietly(fwriter);
      FileUtils.closeQuietly(bwriter);
    }
  }
}
