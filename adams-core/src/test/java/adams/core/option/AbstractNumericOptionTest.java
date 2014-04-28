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
 * AbstractNumericOptionTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.option.ArrayConsumer;

/**
 * Ancestor test class for numeric options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of number the test is for
 */
public abstract class AbstractNumericOptionTest<T>
  extends AbstractArgumentOptionTest {

  /** the default value (lower bound). */
  protected T m_DefaultValueLower;

  /** the default value (upper bound). */
  protected T m_DefaultValueUpper;

  /** the lower bound option. */
  protected AbstractOption m_OptionLower;

  /** the upper bound option. */
  protected AbstractOption m_OptionUpper;

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AbstractNumericOptionTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method. Sets up the dummy object that
   * is used for testing the options.
   *
   * @throws Exception 	if an error occurs during set up
   */
  protected void setUp() throws Exception {
    super.setUp();

    m_OptionLower = getOption(getLowerBoundTestProperty());
    if (m_OptionLower == null)
      throw new IllegalStateException("No option found for lower bound test!");
    m_DefaultValueLower = getDefaultValue(getLowerBoundTestProperty());
    if (m_DefaultValueLower == null)
      throw new IllegalStateException("No default value available for lower bound test!");

    m_OptionUpper = getOption(getUpperBoundTestProperty());
    if (m_OptionUpper == null)
      throw new IllegalStateException("No option found for upper bound test!");
    m_DefaultValueUpper = getDefaultValue(getUpperBoundTestProperty());
    if (m_DefaultValueUpper == null)
      throw new IllegalStateException("No default value available for upper bound test!");
  }

  /**
   * Called by JUnit after each test method. Freeing up memory etc.
   *
   * @throws Exception	if an error occurs during finishing up the test
   */
  protected void tearDown() throws Exception {
    super.tearDown();

    m_OptionLower       = null;
    m_DefaultValueLower = null;
    m_OptionUpper       = null;
    m_DefaultValueUpper = null;
  }

  /**
   * Returns the name of the property to use for testing the lower bound.
   *
   * @return		the property
   */
  protected abstract String getLowerBoundTestProperty();

  /**
   * Returns the (outside) value to test the lower bound with.
   *
   * @return		the value
   */
  protected abstract T getLowerBoundTestValue();

  /**
   * Returns the name of the property to use for testing the upper bound.
   *
   * @return		the property
   */
  protected abstract String getUpperBoundTestProperty();

  /**
   * Returns the (outside) value to test the upper bound with.
   *
   * @return		the value
   */
  protected abstract T getUpperBoundTestValue();

  /**
   * Returns the option associated with the property.
   *
   * @param property	the property name of the option to retrieve
   * @return		the option or null if not found
   */
  protected AbstractOption getOption(String property) {
    return m_OptionHandler.getOptionManager().findByProperty(property);
  }

  /**
   * Returns the option's default value identified by the property name.
   *
   * @param property	the property name that identifies the option
   * @return		the default value or null if not found
   */
  protected T getDefaultValue(String property) {
    T			result;
    AbstractOption	option;

    result = null;
    option = getOption(property);
    if (option != null)
      result = (T) option.getDefaultValue();

    return result;
  }

  /**
   * Test the lower bound.
   */
  public void testLowerBound() {
    String[]	options;

    options = new String[]{
	"-" + m_OptionLower.getCommandline(),
	((AbstractNumericOption) m_OptionLower).toString(getLowerBoundTestValue())};
    ArrayConsumer.setOptions(m_OptionHandler, options);
    assertEquals("Out of bounds value (lower) did not result in default value", m_DefaultValueLower, m_OptionLower.getCurrentValue());
  }

  /**
   * Test the upper bound.
   */
  public void testUpperBound() {
    String[]	options;

    options = new String[]{
	"-" + m_OptionUpper.getCommandline(),
	((AbstractNumericOption) m_OptionUpper).toString(getUpperBoundTestValue())};
    ArrayConsumer.setOptions(m_OptionHandler, options);
    assertEquals("Out of bounds value (upper) did not result in default value", m_DefaultValueUpper, m_OptionUpper.getCurrentValue());
  }
}
