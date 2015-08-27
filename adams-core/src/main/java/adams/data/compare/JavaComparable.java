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
 * JavaComparable.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.compare;

/**
 <!-- globalinfo-start -->
 * Compares objects that implement the Java java.lang.Comparable interface and returns the result of that comparison.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JavaComparable
  extends AbstractObjectCompare<Comparable, Integer> {

  private static final long serialVersionUID = -1792853083538259085L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Compares objects that implement the Java " + Comparable.class.getName()
	+ " interface and returns the result of that comparison.";
  }

  /**
   * Returns the classes that it can handle.
   *
   * @return		the array of classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Comparable.class};
  }

  /**
   * Returns the type of output that it generates.
   *
   * @return		the class of the output
   */
  public Class generates() {
    return Integer.class;
  }

  /**
   * Performs the actual comparison of the two objects.
   *
   * @param o1		the first object
   * @param o2		the second object
   * @return		the result of the comparison
   */
  @Override
  protected Integer doCompareObjects(Comparable o1, Comparable o2) {
    return o1.compareTo(o2);
  }
}
