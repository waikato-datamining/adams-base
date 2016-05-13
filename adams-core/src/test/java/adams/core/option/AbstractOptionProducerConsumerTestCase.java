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
 * AbstractOptionProducerConsumerTestCase.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option;

import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.test.AdamsTestCase;

/**
 * Ancestor of test classes that test option producer and consumer classes in
 * tandem.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractOptionProducerConsumerTestCase
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AbstractOptionProducerConsumerTestCase(String name) {
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
   * Returns a default producer.
   *
   * @return		the producer
   */
  protected abstract OptionProducer getProducer();

  /**
   * Returns a default consumer.
   *
   * @return		the consumer
   */
  protected abstract OptionConsumer getConsumer();

  /**
   * Compares the object created by the producer-consumer tandem with the
   * original one.
   *
   * @param handler	the original input object to compare against after
   * 			using the producer and the consumer in tandem
   * @param producer	the producer to use
   * @param consumer	the consumer to use
   */
  protected void performTest(OptionHandler handler, OptionProducer producer, OptionConsumer consumer) {
    producer.produce(handler);

    consumer.setInput(producer.getOutput());
    consumer.consume();
    OptionHandler created = consumer.getOutput();

    assertEquals("objects differ", handler, created);

    producer.cleanUp();
    consumer.cleanUp();
  }

  /**
   * Tests a simple option handler.
   */
  public void testProduceSimple() {
    adams.flow.sink.DumpFile handler = new adams.flow.sink.DumpFile();
    handler.setLoggingLevel(LoggingLevel.INFO);
    handler.setOutputFile(new PlaceholderFile("${TMP}/dumpfile.csv"));
    handler.setAppend(true);

    performTest(handler, getProducer(), getConsumer());
  }

  /**
   * Tests a deeply nested option handler.
   */
  public void testProduceDeep() {
    adams.data.filter.MultiFilter handler = new adams.data.filter.MultiFilter();
    handler.setLoggingLevel(LoggingLevel.INFO);
    adams.data.filter.Filter[] filters = new adams.data.filter.Filter[2];
    filters[0] = new adams.data.filter.PassThrough();
    filters[0].setLoggingLevel(LoggingLevel.FINE);
    filters[1] = new adams.data.filter.MultiFilter();
    filters[1].setLoggingLevel(LoggingLevel.FINEST);
    handler.setSubFilters(filters);

    performTest(handler, getProducer(), getConsumer());
  }

  /**
   * Tests another deeply nested option handler.
   */
  public void testProduceDeep2() {
    adams.data.filter.BaselineCorrection handler = new adams.data.filter.BaselineCorrection();
    adams.data.baseline.SlidingWindow baseline = new adams.data.baseline.SlidingWindow();
    baseline.setLoggingLevel(LoggingLevel.FINE);
    handler.setBaselineCorrection(baseline);

    performTest(handler, getProducer(), getConsumer());
  }

  /**
   * For classes (with default constructor) that are serializable, are tested
   * whether they are truly serializable.
   * Tests producer and consumer classes.
   *
   * @see		#getProducer()
   * @see		#getConsumer()
   */
  @Override
  public void testSerializable() {
    performSerializableTest(getProducer().getClass());
    performSerializableTest(getConsumer().getClass());
  }
}
