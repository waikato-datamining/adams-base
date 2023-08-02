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
 * JavaRandomByteUnseeded.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

/**
 <!-- globalinfo-start -->
 * Random generator that generates random integers using Java's java.util.Random class (unseeded).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see java.util.Random#nextInt(int)
 */
public class JavaRandomByteUnseeded
  extends AbstractRandomNumberGenerator<Byte> {

  /** for serialization. */
  private static final long serialVersionUID = 8754317840175980117L;

  /** the random number generator to use. */
  protected java.util.Random m_Random;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Random generator that generates random integers using Java's java.util.Random class (unseeded).";
  }

  /**
   * Resets the generator.
   */
  @Override
  public void reset() {
    super.reset();

    m_Random = null;
  }

  /**
   * Performs optional checks.
   * <br><br>
   * Initializes the random number generator.
   */
  @Override
  protected void check() {
    super.check();

    if (m_Random == null)
      m_Random = new java.util.Random();
  }

  /**
   * Returns the next random number. Does the actual computation.
   *
   * @return		the next number
   */
  @Override
  protected Byte doNext() {
    return (byte) m_Random.nextInt(256);
  }
}
