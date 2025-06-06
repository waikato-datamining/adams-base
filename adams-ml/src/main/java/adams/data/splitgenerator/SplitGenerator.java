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
 * SplitGenerator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.data.splitgenerator;

import adams.core.Randomizable;
import adams.core.option.OptionHandler;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Interface for classes that generate dataset splits.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <I> the input data
 * @param <O> the output data
 */
public interface SplitGenerator<I,O>
  extends Serializable, OptionHandler, Iterator<O>, Randomizable {

  /**
   * Sets the original data.
   *
   * @param value	the data
   */
  public void setData(I value);

  /**
   * Returns the original data.
   *
   * @return		the data
   */
  public I getData();

  /**
   * Initializes the iterator (gets implicitly called, when calling next()).
   */
  public void initializeIterator() ;

  /**
   * Returns <tt>true</tt> if the iteration has more elements. (In other
   * words, returns <tt>true</tt> if <tt>next</tt> would return an element
   * rather than throwing an exception.)
   *
   * @return 		<tt>true</tt> if the iterator has more elements.
   */
  @Override
  public boolean hasNext();

  /**
   * Returns the next element in the iteration.
   *
   * @return 				the next element in the iteration.
   * @throws NoSuchElementException 	iteration has no more elements.
   */
  @Override
  public O next();

  /**
   * Unsupported.
   */
  @Override
  public void remove();

  /**
   * Returns a short description of the generator.
   *
   * @return		a short description
   */
  @Override
  public String toString();
}
