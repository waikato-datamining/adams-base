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
 * JsonProducerTest.java
 * Copyright (C) 2011-2024 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the JsonProducer class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JsonProducerTest
  extends AbstractOptionProducerTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public JsonProducerTest(String name) {
    super(name);
  }

  /**
   * Tests a simple option handler.
   */
  public void testSimple() {
    adams.flow.sink.DumpFile handler = new adams.flow.sink.DumpFile();
    handler.setLoggingLevel(LoggingLevel.INFO);
    handler.setOutputFile(new PlaceholderFile("${TMP}/dumpfile.arff"));
    handler.setAppend(true);

    JsonProducer producer = new JsonProducer();
    producer.setOutputFull(true);
    producer.produce(handler);

    assertEquals(
	"getOutput() differs",
	"{\"numAttempts\":1,\"stopFlowOnError\":false,\"silent\":false,\"attemptInterval\":1000,\"annotations\":\"\",\"skip\":false,\"encoding\":\"Default\",\"outputFile\":\"${TMP}\\/dumpfile.arff\",\"name\":\"DumpFile\",\"class\":\"adams.flow.sink.DumpFile\",\"loggingLevel\":\"INFO\",\"append\":true,\"bufferSize\":1}",
	"" + producer.getOutput());
    assertEquals(
	"toString() differs",
	"{\"numAttempts\":1,\"stopFlowOnError\":false,\"silent\":false,\"attemptInterval\":1000,\"annotations\":\"\",\"skip\":false,\"encoding\":\"Default\",\"outputFile\":\"${TMP}\\/dumpfile.arff\",\"name\":\"DumpFile\",\"class\":\"adams.flow.sink.DumpFile\",\"loggingLevel\":\"INFO\",\"append\":true,\"bufferSize\":1}",
	producer.toString());
  }

  /**
   * Tests a simple option handler with only outputting options that differ from the default ones.
   */
  public void testNotFull() {
    adams.flow.sink.DumpFile handler = new adams.flow.sink.DumpFile();
    handler.setLoggingLevel(LoggingLevel.INFO);
    handler.setOutputFile(new PlaceholderFile("${TMP}/dumpfile.arff"));
    handler.setAppend(true);

    JsonProducer producer = new JsonProducer();
    producer.setOutputFull(false);
    producer.produce(handler);

    assertEquals(
      "getOutput() differs",
      "{\"outputFile\":\"${TMP}\\/dumpfile.arff\",\"class\":\"adams.flow.sink.DumpFile\",\"loggingLevel\":\"INFO\",\"append\":true}",
      "" + producer.getOutput());
    assertEquals(
      "toString() differs",
      "{\"outputFile\":\"${TMP}\\/dumpfile.arff\",\"class\":\"adams.flow.sink.DumpFile\",\"loggingLevel\":\"INFO\",\"append\":true}",
      producer.toString());
  }

  /**
   * Tests a deeply nested option handler.
   */
  public void testDeep() {
    adams.data.filter.MultiFilter handler = new adams.data.filter.MultiFilter();
    handler.setLoggingLevel(LoggingLevel.INFO);
    adams.data.filter.Filter[] filters = new adams.data.filter.Filter[2];
    filters[0] = new adams.data.filter.PassThrough();
    filters[0].setLoggingLevel(LoggingLevel.FINE);
    filters[1] = new adams.data.filter.MultiFilter();
    filters[1].setLoggingLevel(LoggingLevel.FINEST);
    handler.setSubFilters(filters);

    JsonProducer producer = new JsonProducer();
    producer.setOutputFull(true);
    producer.produce(handler);

    assertEquals(
	"getOutput() differs",
	"{\"dontUpdateID\":false,\"subFilters\":[{\"dontUpdateID\":false,\"class\":\"adams.data.filter.PassThrough\",\"loggingLevel\":\"FINE\"},{\"dontUpdateID\":false,\"subFilters\":[{\"dontUpdateID\":false,\"class\":\"adams.data.filter.PassThrough\",\"loggingLevel\":\"WARNING\"}],\"class\":\"adams.data.filter.MultiFilter\",\"loggingLevel\":\"FINEST\"}],\"class\":\"adams.data.filter.MultiFilter\",\"loggingLevel\":\"INFO\"}",
	"" + producer.getOutput());
    assertEquals(
	"toString() differs",
	"{\"dontUpdateID\":false,\"subFilters\":[{\"dontUpdateID\":false,\"class\":\"adams.data.filter.PassThrough\",\"loggingLevel\":\"FINE\"},{\"dontUpdateID\":false,\"subFilters\":[{\"dontUpdateID\":false,\"class\":\"adams.data.filter.PassThrough\",\"loggingLevel\":\"WARNING\"}],\"class\":\"adams.data.filter.MultiFilter\",\"loggingLevel\":\"FINEST\"}],\"class\":\"adams.data.filter.MultiFilter\",\"loggingLevel\":\"INFO\"}",
	producer.toString());
  }

  /**
   * Tests another deeply nested option handler.
   */
  public void testDeep2() {
    adams.data.filter.BaselineCorrection handler = new adams.data.filter.BaselineCorrection();
    adams.data.baseline.SlidingWindow baseline = new adams.data.baseline.SlidingWindow();
    baseline.setLoggingLevel(LoggingLevel.FINE);
    handler.setBaselineCorrection(baseline);

    JsonProducer producer = new JsonProducer();
    producer.setOutputFull(true);
    producer.produce(handler);

    assertEquals(
	"getOutput() differs",
	"{\"dontUpdateID\":false,\"baselineCorrection\":{\"numLeft\":30,\"numRight\":30,\"baselineCorrection\":{\"class\":\"adams.data.baseline.PassThrough\",\"loggingLevel\":\"WARNING\"},\"class\":\"adams.data.baseline.SlidingWindow\",\"loggingLevel\":\"FINE\"},\"class\":\"adams.data.filter.BaselineCorrection\",\"loggingLevel\":\"WARNING\"}",
	"" + producer.getOutput());
    assertEquals(
	"toString() differs",
	"{\"dontUpdateID\":false,\"baselineCorrection\":{\"numLeft\":30,\"numRight\":30,\"baselineCorrection\":{\"class\":\"adams.data.baseline.PassThrough\",\"loggingLevel\":\"WARNING\"},\"class\":\"adams.data.baseline.SlidingWindow\",\"loggingLevel\":\"FINE\"},\"class\":\"adams.data.filter.BaselineCorrection\",\"loggingLevel\":\"WARNING\"}",
	producer.toString());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(JsonProducerTest.class);
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
