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
 * AbstractOptionConsumerTestCase.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option;

import adams.core.Utils;
import adams.test.AdamsTestCase;

/**
 * Ancestor for option consumer tests.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <C> the type of data to consume
 */
public abstract class AbstractOptionConsumerTestCase<C>
  extends AdamsTestCase {

  /** whether to fail in case of warnings. */
  protected boolean m_FailOnWarnings;

  /** whether to fail in case of errors. */
  protected boolean m_FailOnErrors;

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AbstractOptionConsumerTestCase(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception 	if an error occurs during set up
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_FailOnWarnings = true;
    m_FailOnErrors   = true;
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if an error occurs during finishing up the test
   */
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Tests generating an option handler from a string.
   *
   * @param consumer	the consumer class to use for parsing the string
   * @param s		the string to parse
   * @param handler	the input handler that should be created from the string
   */
  public void performFromStringTest(Class consumer, String s, OptionHandler handler) {
    OptionHandler created = AbstractOptionConsumer.fromString(consumer, s);
    assertEquals("objects differ", handler, created);
  }

  /**
   * Tests generating an option handler from input data.
   *
   * @param consumer	the consumer to use for consuming the input data
   * @param input	the input data
   * @param handler	the input handler that should be created from the input	data
   */
  public void performInputTest(OptionConsumer consumer, C input, OptionHandler handler) {
    consumer.setInput(input);
    consumer.consume();
    if (m_FailOnWarnings && consumer.hasWarnings())
      fail("warnings encountered: " + Utils.flatten(consumer.getWarnings(), ", "));
    if (m_FailOnErrors && consumer.hasErrors())
      fail("errors encountered: " + Utils.flatten(consumer.getErrors(), ", "));
    assertEquals("objects differ", handler, consumer.getOutput());
    consumer.cleanUp();
  }
}
