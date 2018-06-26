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
 * CompactFlowProducerConsumerTest.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.env.Environment;
import adams.flow.control.Flow;
import adams.flow.control.Tee;
import adams.flow.control.Trigger;
import adams.flow.sink.Console;
import adams.flow.sink.Null;
import adams.flow.source.ForLoop;
import adams.flow.source.Start;
import adams.flow.transformer.MathExpression;
import adams.flow.transformer.PassThrough;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the CompactFlowProducer/Consumer classes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CompactFlowProducerConsumerTest
  extends AbstractOptionProducerConsumerTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public CompactFlowProducerConsumerTest(String name) {
    super(name);
  }

  /**
   * Returns a default producer.
   *
   * @return		the producer
   */
  @Override
  protected OptionProducer getProducer() {
    return new CompactFlowProducer();
  }

  /**
   * Returns a default consumer.
   *
   * @return		the consumer
   */
  @Override
  protected OptionConsumer getConsumer() {
    return new CompactFlowConsumer();
  }

  /**
   * Tests a deeply nested option handler.
   */
  public void testProduceDeep() {
    Flow flow = new Flow();
    flow.add(new Start());
    Tee tee = new Tee();
    tee.add(new PassThrough());
    tee.add(new Console());
    flow.add(tee);
    Trigger trigger = new Trigger();
    trigger.add(new ForLoop());
    trigger.add(new MathExpression());
    trigger.add(new Null());
    flow.add(trigger);

    performTest(flow, getProducer(), getConsumer());
  }

  /**
   * Ignored
   */
  public void testProduceDeep2() {
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(CompactFlowProducerConsumerTest.class);
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
