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
 * JsonProducerConsumerTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;


/**
 * Tests the JsonProducer/Consumer classes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JsonProducerConsumerTest
  extends AbstractOptionProducerConsumerTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public JsonProducerConsumerTest(String name) {
    super(name);
  }

  /**
   * Returns a default producer.
   *
   * @return		the producer
   */
  @Override
  protected OptionProducer getProducer() {
    return new JsonProducer();
  }

  /**
   * Returns a default consumer.
   *
   * @return		the consumer
   */
  @Override
  protected OptionConsumer getConsumer() {
    return new JsonConsumer();
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(JsonProducerConsumerTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}
