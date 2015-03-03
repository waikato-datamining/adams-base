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
 * ArrayConsumerTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.env.Environment;
import adams.flow.core.AbstractActor;
import adams.parser.MathematicalExpressionText;

/**
 * Tests the ArrayConsumer class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayConsumerTest
  extends AbstractOptionConsumerTestCase<String[]> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public ArrayConsumerTest(String name) {
    super(name);
  }

  /**
   * Tests a simple option handler.
   */
  public void testConsumeSimple() {
    adams.flow.sink.DumpFile handler = new adams.flow.sink.DumpFile();
    handler.setLoggingLevel(LoggingLevel.INFO);
    handler.setOutputFile(new PlaceholderFile("${TMP}/dumpfile.csv"));
    handler.setAppend(true);

    performInputTest(
	new ArrayConsumer(),
	new String[]{
	  "adams.flow.sink.DumpFile",
	  "-logging-level",
	  "INFO",
	  "-output",
	  "${TMP}/dumpfile.csv",
	  "-append",
	  "true"
	},
	handler);
  }

  /**
   * Tests generating a simple option handler from a string.
   */
  public void testFromStringSimple() {
    adams.flow.sink.DumpFile handler = new adams.flow.sink.DumpFile();
    handler.setLoggingLevel(LoggingLevel.INFO);
    handler.setOutputFile(new PlaceholderFile("${TMP}/dumpfile.csv"));
    handler.setAppend(true);

    performFromStringTest(
	ArrayConsumer.class,
	"adams.flow.sink.DumpFile -logging-level INFO -output ${TMP}/dumpfile.csv -append true",
	handler);
  }

  /**
   * Tests a deeply nested option handler.
   */
  public void testConsumeDeep() {
    adams.data.filter.MultiFilter handler = new adams.data.filter.MultiFilter();
    handler.setLoggingLevel(LoggingLevel.INFO);
    adams.data.filter.AbstractFilter[] filters = new adams.data.filter.AbstractFilter[2];
    filters[0] = new adams.data.filter.PassThrough();
    filters[0].setLoggingLevel(LoggingLevel.FINE);
    filters[1] = new adams.data.filter.MultiFilter();
    filters[1].setLoggingLevel(LoggingLevel.FINEST);
    handler.setSubFilters(filters);

    performInputTest(
	new ArrayConsumer(),
	new String[]{
	  "adams.data.filter.MultiFilter",
	  "-logging-level",
	  "INFO",
	  "-filter",
	  "adams.data.filter.PassThrough -logging-level FINE",
	  "-filter",
	  "adams.data.filter.MultiFilter -logging-level FINEST -filter adams.data.filter.PassThrough"
	},
	handler);
  }

  /**
   * Tests generating a deeply nested option handler from a string.
   */
  public void testFromStringDeep() {
    adams.data.filter.MultiFilter handler = new adams.data.filter.MultiFilter();
    handler.setLoggingLevel(LoggingLevel.INFO);
    adams.data.filter.AbstractFilter[] filters = new adams.data.filter.AbstractFilter[2];
    filters[0] = new adams.data.filter.PassThrough();
    filters[0].setLoggingLevel(LoggingLevel.FINE);
    filters[1] = new adams.data.filter.MultiFilter();
    filters[1].setLoggingLevel(LoggingLevel.FINEST);
    handler.setSubFilters(filters);

    performFromStringTest(
	ArrayConsumer.class,
	"adams.data.filter.MultiFilter -logging-level INFO -filter \"adams.data.filter.PassThrough -logging-level FINE\" -filter \"adams.data.filter.MultiFilter -logging-level FINEST -filter adams.data.filter.PassThrough\"",
	handler);
  }

  /**
   * Tests another deeply nested option handler.
   */
  public void testConsumeDeep2() {
    adams.data.filter.BaselineCorrection handler = new adams.data.filter.BaselineCorrection();
    adams.data.baseline.SlidingWindow baseline = new adams.data.baseline.SlidingWindow();
    baseline.setLoggingLevel(LoggingLevel.FINE);
    handler.setBaselineCorrection(baseline);

    performInputTest(
	new ArrayConsumer(),
	new String[]{
	  "adams.data.filter.BaselineCorrection",
	  "-baseline",
	  "adams.data.baseline.SlidingWindow -logging-level FINE -baseline adams.data.baseline.PassThrough"
	},
	handler);
  }

  /**
   * Tests generating another deeply nested option handler from a string.
   */
  public void testFromStringDeep2() {
    adams.data.filter.BaselineCorrection handler = new adams.data.filter.BaselineCorrection();
    adams.data.baseline.SlidingWindow baseline = new adams.data.baseline.SlidingWindow();
    baseline.setLoggingLevel(LoggingLevel.FINE);
    handler.setBaselineCorrection(baseline);

    performFromStringTest(
	ArrayConsumer.class,
	"adams.data.filter.BaselineCorrection -baseline \"adams.data.baseline.SlidingWindow -logging-level FINE -baseline adams.data.baseline.PassThrough\"",
	handler);
  }

  /**
   * Tests generating another deeply nested option handler from a string.
   */
  public void testFromStringDeep3() {
    adams.flow.control.Flow handler = new adams.flow.control.Flow();
    adams.flow.source.StringConstants sc = new adams.flow.source.StringConstants();
    sc.setStrings(new BaseString[]{
	new BaseString("1"),
	new BaseString("2"),
	new BaseString("3")
    });
    adams.data.conversion.StringToDouble s2d = new adams.data.conversion.StringToDouble();
    adams.flow.transformer.Convert con = new adams.flow.transformer.Convert();
    con.setConversion(s2d);
    adams.flow.transformer.MathExpression me = new adams.flow.transformer.MathExpression();
    me.setExpression(new MathematicalExpressionText("X^2"));
    adams.flow.sink.DumpFile df = new adams.flow.sink.DumpFile();
    df.setAppend(true);
    df.setOutputFile(new PlaceholderFile("${TMP}/dumpfile.txt"));
    handler.setActors(new AbstractActor[]{
	sc,
	con,
	me,
	df
    });

    performFromStringTest(
	ArrayConsumer.class,
	"adams.flow.control.Flow -actor \"adams.flow.source.StringConstants -string 1 -string 2 -string 3\" -actor \"adams.flow.transformer.Convert -conversion adams.data.conversion.StringToDouble\" -actor \"adams.flow.transformer.MathExpression -expression X^2\" -actor \"adams.flow.sink.DumpFile -output ${TMP}/dumpfile.txt -append true\"",
	handler);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(ArrayConsumerTest.class);
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
