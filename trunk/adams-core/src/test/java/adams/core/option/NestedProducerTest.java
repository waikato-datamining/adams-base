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
 * NestedProducerTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.env.Environment;

/**
 * Tests the NestedProducer class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NestedProducerTest
  extends AbstractOptionProducerTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public NestedProducerTest(String name) {
    super(name);
  }

  /**
   * Returns the option handler dumped in nested format.
   *
   * @param handler	the option handler to dump
   * @return		always empty string
   */
  @Override
  protected String dumpOptionHandler(OptionHandler handler) {
    return "";
  }

  /**
   * Tests a simple option handler.
   */
  public void testSimple() {
    adams.flow.sink.DumpFile handler = new adams.flow.sink.DumpFile();
    handler.setLoggingLevel(LoggingLevel.INFO);
    handler.setOutputFile(new PlaceholderFile("${TMP}/dumpfile.csv"));
    handler.setAppend(true);

    NestedProducer producer = new NestedProducer();
    producer.setOutputProlog(false);
    producer.produce(handler);

    assertEquals(
	"getOutput() differs",
	"[adams.flow.sink.DumpFile, [-logging-level, INFO, -output, ${TMP}/dumpfile.csv, -append, true]]",
	"" + producer.getOutput());
    assertEquals(
	"toString() differs",
	  "adams.flow.sink.DumpFile\n"
	+ "\t-logging-level\n"
	+ "\tINFO\n"
	+ "\t-output\n"
	+ "\t${TMP}/dumpfile.csv\n"
	+ "\t-append\n"
	+ "\ttrue\n",
	producer.toString());
  }

  /**
   * Tests a deeply nested option handler.
   */
  public void testDeep() {
    adams.data.filter.MultiFilter handler = new adams.data.filter.MultiFilter();
    handler.setLoggingLevel(LoggingLevel.INFO);
    adams.data.filter.AbstractFilter[] filters = new adams.data.filter.AbstractFilter[2];
    filters[0] = new adams.data.filter.PassThrough();
    filters[0].setLoggingLevel(LoggingLevel.FINE);
    filters[1] = new adams.data.filter.MultiFilter();
    filters[1].setLoggingLevel(LoggingLevel.FINEST);
    handler.setSubFilters(filters);

    NestedProducer producer = new NestedProducer();
    producer.setOutputProlog(false);
    producer.produce(handler);

    assertEquals(
	"getOutput() differs",
	"[adams.data.filter.MultiFilter, [-logging-level, INFO, -filter, [adams.data.filter.PassThrough, [-logging-level, FINE]], -filter, [adams.data.filter.MultiFilter, [-logging-level, FINEST, -filter, [adams.data.filter.PassThrough, []]]]]]",
	"" + producer.getOutput());
    assertEquals(
	"toString() differs",
	  "adams.data.filter.MultiFilter\n"
	+ "\t-logging-level\n"
	+ "\tINFO\n"
	+ "\t-filter\n"
	+ "\t\tadams.data.filter.PassThrough\n"
	+ "\t\t\t-logging-level\n"
	+ "\t\t\tFINE\n"
	+ "\t-filter\n"
	+ "\t\tadams.data.filter.MultiFilter\n"
	+ "\t\t\t-logging-level\n"
	+ "\t\t\tFINEST\n"
	+ "\t\t\t-filter\n"
	+ "\t\t\t\tadams.data.filter.PassThrough\n",
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

    NestedProducer producer = new NestedProducer();
    producer.setOutputProlog(false);
    producer.produce(handler);

    assertEquals(
	"getOutput() differs",
	"[adams.data.filter.BaselineCorrection, [-baseline, [adams.data.baseline.SlidingWindow, [-logging-level, FINE, -baseline, [adams.data.baseline.PassThrough, []]]]]]",
	"" + producer.getOutput());
    assertEquals(
	"toString() differs",
	  "adams.data.filter.BaselineCorrection\n"
	+ "\t-baseline\n"
	+ "\t\tadams.data.baseline.SlidingWindow\n"
	+ "\t\t\t-logging-level\n"
	+ "\t\t\tFINE\n"
	+ "\t\t\t-baseline\n"
	+ "\t\t\t\tadams.data.baseline.PassThrough\n",
	producer.toString());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(NestedProducerTest.class);
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
