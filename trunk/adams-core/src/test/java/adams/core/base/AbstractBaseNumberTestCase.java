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
 * AbstractBaseNumberTestCase.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.core.base;


/**
 * Abstract ancestor for tests for base objects handling numbers.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of class to handle
 */
public abstract class AbstractBaseNumberTestCase<T extends BaseObject>
  extends AbstractBaseObjectTestCase<T> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AbstractBaseNumberTestCase(String name) {
    super(name);
  }

  /**
   * Returns the string representing a value below the parsing range that must
   * fail parsing.
   *
   * @return		the value, if null the test gets ignored
   */
  protected abstract String getBelowValue();

  /**
   * Returns the string representing a value above the parsing range that must
   * fail parsing.
   *
   * @return		the value, if null the test gets ignored
   */
  protected abstract String getAboveValue();

  /**
   * Tests the parsing of a value outside (below) the range.
   */
  public void testBelowValue() {
    try {
      if (getBelowValue() != null) {
	T b = getCustom(getBelowValue());
	assertTrue("Parsing should have failed and resulted in default value", getDefaultValue().equals(b.getValue()));
      }
    }
    catch (Exception e) {
      // expected, hence ignored
    }
  }

  /**
   * Tests the parsing of a value outside (above) the range.
   */
  public void testAboveValue() {
    try {
      if (getAboveValue() != null) {
	T b = getCustom(getAboveValue());
	assertTrue("Parsing should have failed and resulted in default value", getDefaultValue().equals(b.getValue()));
      }
    }
    catch (Exception e) {
      // expected, hence ignored
    }
  }
}
