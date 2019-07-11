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
 * Splitting.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.splitgenerator.generic.splitter;

import adams.data.binning.Binnable;
import com.github.fracpete.javautils.struct.Struct2;

import java.util.List;

/**
 * Interface for classes that split data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface Splitter {

  /**
   * Resets the scheme.
   */
  public void reset();

  /**
   * Splits the data into two.
   *
   * @param data	the data to split
   * @param <T>		the payload type
   * @return		the split data
   */
  public <T> Struct2<List<Binnable<T>>,List<Binnable<T>>> split(List<Binnable<T>> data);
}
