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
 * AbstractMultiReportWriter.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.data.report.Report;

import java.lang.reflect.Array;

/**
 * Ancestor for report writers that write multiple reports into a single file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMultiReportWriter<T extends Report>
  extends AbstractReportWriter<T>
  implements MultiReportWriter<T> {

  private static final long serialVersionUID = -7111019097066994653L;

  /**
   * Performs checks and writes the data.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  public boolean write(T[] data) {
    checkData(data);
    return writeData(data);
  }

  /**
   * The default implementation only checks whether the provided file is an
   * actual file and whether it exists (if m_OutputIsFile is TRUE). Otherwise
   * the directory has to exist.
   *
   * @param data	the data to write
   */
  protected void checkData(T[] data) {
    if (m_Output.isDirectory())
      throw new IllegalStateException("No output file but directory provided ('" + m_Output + "')!");
    if (!m_Output.getParentFile().exists())
      throw new IllegalStateException("Output file's directory '" + m_Output.getParentFile() + "' does not exist!");
  }

  /**
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  protected abstract boolean writeData(T[] data);

  /**
   * Performs the actual writing.
   *
   * @param data	the data to write
   * @return		true if successfully written
   */
  @Override
  public boolean writeData(T data) {
    Class	cls;
    Object	array;

    cls   = getReportClass();
    array = Array.newInstance(cls, 1);
    Array.set(array, 0, data);

    return writeData((T[]) array);
  }
}
