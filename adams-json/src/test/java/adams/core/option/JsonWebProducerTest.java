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
 * JsonWebProducerTest.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the JsonWebProducer class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class JsonWebProducerTest
  extends AbstractOptionProducerTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public JsonWebProducerTest(String name) {
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

    JsonWebProducer producer = new JsonWebProducer();
    producer.setOutputFull(true);
    producer.produce(handler);
    // for updating the string uncomment:
    //System.out.println("" + producer.getOutput());
    //System.out.println(Utils.backQuoteChars("" + producer.getOutput()));

    assertEquals(
      "getOutput() differs",
      "{\"options\":{\"numAttempts\":1,\"stopFlowOnError\":false,\"outputFile\":\"${TMP}\\/dumpfile.arff\",\"silent\":false,\"attemptInterval\":1000,\"name\":\"DumpFile\",\"annotations\":\"\",\"skip\":false,\"encoding\":\"Default\",\"loggingLevel\":\"INFO\",\"append\":true,\"bufferSize\":1},\"class\":\"adams.flow.sink.DumpFile\"}",
      "" + producer.getOutput());
    assertEquals(
      "toString() differs",
      "{\"options\":{\"numAttempts\":1,\"stopFlowOnError\":false,\"outputFile\":\"${TMP}\\/dumpfile.arff\",\"silent\":false,\"attemptInterval\":1000,\"name\":\"DumpFile\",\"annotations\":\"\",\"skip\":false,\"encoding\":\"Default\",\"loggingLevel\":\"INFO\",\"append\":true,\"bufferSize\":1},\"class\":\"adams.flow.sink.DumpFile\"}",
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

    JsonWebProducer producer = new JsonWebProducer();
    producer.setOutputFull(false);
    producer.produce(handler);
    // for updating the string uncomment:
    //System.out.println("" + producer.getOutput());
    //System.out.println(Utils.backQuoteChars("" + producer.getOutput()));

    assertEquals(
      "getOutput() differs",
      "{\"options\":{\"outputFile\":\"${TMP}\\/dumpfile.arff\",\"loggingLevel\":\"INFO\",\"append\":true},\"class\":\"adams.flow.sink.DumpFile\"}",
      "" + producer.getOutput());
    assertEquals(
      "toString() differs",
      "{\"options\":{\"outputFile\":\"${TMP}\\/dumpfile.arff\",\"loggingLevel\":\"INFO\",\"append\":true},\"class\":\"adams.flow.sink.DumpFile\"}",
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

    JsonWebProducer producer = new JsonWebProducer();
    producer.setOutputFull(true);
    producer.produce(handler);
    // for updating the string uncomment:
    //System.out.println("" + producer.getOutput());
    //System.out.println(Utils.backQuoteChars("" + producer.getOutput()));

    assertEquals(
      "getOutput() differs",
      "{\"options\":{\"dontUpdateProcessingInfo\":false,\"dontUpdateID\":false,\"subFilters\":[{\"options\":{\"dontUpdateProcessingInfo\":false,\"dontUpdateID\":false,\"loggingLevel\":\"FINE\"},\"class\":\"adams.data.filter.PassThrough\"},{\"options\":{\"dontUpdateProcessingInfo\":false,\"dontUpdateID\":false,\"subFilters\":[{\"options\":{\"dontUpdateProcessingInfo\":false,\"dontUpdateID\":false,\"loggingLevel\":\"WARNING\"},\"class\":\"adams.data.filter.PassThrough\"}],\"loggingLevel\":\"FINEST\"},\"class\":\"adams.data.filter.MultiFilter\"}],\"loggingLevel\":\"INFO\"},\"class\":\"adams.data.filter.MultiFilter\"}",
      "" + producer.getOutput());
    assertEquals(
      "toString() differs",
      "{\"options\":{\"dontUpdateProcessingInfo\":false,\"dontUpdateID\":false,\"subFilters\":[{\"options\":{\"dontUpdateProcessingInfo\":false,\"dontUpdateID\":false,\"loggingLevel\":\"FINE\"},\"class\":\"adams.data.filter.PassThrough\"},{\"options\":{\"dontUpdateProcessingInfo\":false,\"dontUpdateID\":false,\"subFilters\":[{\"options\":{\"dontUpdateProcessingInfo\":false,\"dontUpdateID\":false,\"loggingLevel\":\"WARNING\"},\"class\":\"adams.data.filter.PassThrough\"}],\"loggingLevel\":\"FINEST\"},\"class\":\"adams.data.filter.MultiFilter\"}],\"loggingLevel\":\"INFO\"},\"class\":\"adams.data.filter.MultiFilter\"}",
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

    JsonWebProducer producer = new JsonWebProducer();
    producer.setOutputFull(true);
    producer.produce(handler);
    // for updating the string uncomment:
    //System.out.println("" + producer.getOutput());
    //System.out.println(Utils.backQuoteChars("" + producer.getOutput()));

    assertEquals(
      "getOutput() differs",
      "{\"options\":{\"dontUpdateProcessingInfo\":false,\"dontUpdateID\":false,\"baselineCorrection\":{\"options\":{\"numLeft\":30,\"numRight\":30,\"baselineCorrection\":{\"options\":{\"loggingLevel\":\"WARNING\"},\"class\":\"adams.data.baseline.PassThrough\"},\"loggingLevel\":\"FINE\"},\"class\":\"adams.data.baseline.SlidingWindow\"},\"loggingLevel\":\"WARNING\"},\"class\":\"adams.data.filter.BaselineCorrection\"}",
      "" + producer.getOutput());
    assertEquals(
      "toString() differs",
      "{\"options\":{\"dontUpdateProcessingInfo\":false,\"dontUpdateID\":false,\"baselineCorrection\":{\"options\":{\"numLeft\":30,\"numRight\":30,\"baselineCorrection\":{\"options\":{\"loggingLevel\":\"WARNING\"},\"class\":\"adams.data.baseline.PassThrough\"},\"loggingLevel\":\"FINE\"},\"class\":\"adams.data.baseline.SlidingWindow\"},\"loggingLevel\":\"WARNING\"},\"class\":\"adams.data.filter.BaselineCorrection\"}",
      producer.toString());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(JsonWebProducerTest.class);
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
