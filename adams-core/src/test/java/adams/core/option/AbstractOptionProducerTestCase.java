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
 * AbstractOptionProducerTestCase.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option;

import adams.test.AdamsTestCase;

/**
 * Ancestor for option producer tests.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractOptionProducerTestCase
  extends AdamsTestCase {

  /** the current option handler that is being tested. */
  protected OptionHandler m_OptionHandler;
  
  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AbstractOptionProducerTestCase(String name) {
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
    
    m_OptionHandler = null;
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
   * Returns the option handler dumped in nested format.
   *
   * @param handler	the option handler to dump
   * @return		the handler in the specified format dump
   */
  protected String dumpOptionHandler(OptionHandler handler) {
    String		format;
    NestedProducer	producer;

    producer = new NestedProducer();
    producer.produce(handler);

    return producer.toString();
  }

  /**
   * Dumps the actor, in case of an error.
   * 
   * @throws Throwable		any test failure
   */
  @Override
  public void runBare() throws Throwable {
    try {
      super.runBare();
    }
    catch (Throwable t) {
      if (m_OptionHandler != null)
	System.out.println(dumpOptionHandler(m_OptionHandler));
      throw t;
    }
    m_OptionHandler = null;
  }
}
