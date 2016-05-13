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
 * JsonConsumerTest.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.env.Environment;
import adams.flow.core.AbstractActor;
import adams.parser.MathematicalExpressionText;

/**
 * Tests the JsonConsumer class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JsonConsumerTest
  extends AbstractOptionConsumerTestCase<JSONObject> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public JsonConsumerTest(String name) {
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

    JSONObject input = new JSONObject();
    input.put(JsonConsumer.KEY_CLASS, "adams.flow.sink.DumpFile");
    input.put("loggingLevel", "INFO");
    input.put("outputFile", "${TMP}/dumpfile.csv");
    input.put("append", true);

    performInputTest(new JsonConsumer(), input, handler);
  }

  /**
   * Tests generating a simple option handler from a string.
   */
  public void testFromStringSimple() {
    adams.flow.sink.DumpFile handler = new adams.flow.sink.DumpFile();
    handler.setLoggingLevel(LoggingLevel.INFO);
    handler.setOutputFile(new PlaceholderFile("${TMP}/dumpfile.csv"));
    handler.setAppend(true);

    JSONObject input = new JSONObject();
    input.put(JsonConsumer.KEY_CLASS, "adams.flow.sink.DumpFile");
    input.put("loggingLevel", "INFO");
    input.put("outputFile", "${TMP}/dumpfile.csv");
    input.put("append", true);

    performFromStringTest(
	JsonConsumer.class,
	input.toJSONString(),
	handler);
  }

  /**
   * Tests a deeply nested option handler.
   */
  public void testConsumeDeep() {
    adams.data.filter.MultiFilter handler = new adams.data.filter.MultiFilter();
    handler.setLoggingLevel(LoggingLevel.INFO);
    adams.data.filter.Filter[] filters = new adams.data.filter.Filter[2];
    filters[0] = new adams.data.filter.PassThrough();
    filters[0].setLoggingLevel(LoggingLevel.FINE);
    filters[1] = new adams.data.filter.MultiFilter();
    filters[1].setLoggingLevel(LoggingLevel.FINEST);
    handler.setSubFilters(filters);

    JSONObject input = new JSONObject();
    input.put(JsonConsumer.KEY_CLASS, "adams.data.filter.MultiFilter");
    JSONArray options = new JSONArray();
    input.put("subFilters", options);
    input.put("loggingLevel", "INFO");

    JSONObject filter1 = new JSONObject();
    options.add(filter1);
    filter1.put(JsonConsumer.KEY_CLASS, "adams.data.filter.PassThrough");
    filter1.put("loggingLevel", "FINE");

    JSONObject filter2 = new JSONObject();
    options.add(filter2);
    filter2.put(JsonConsumer.KEY_CLASS, "adams.data.filter.MultiFilter");
    filter2.put("loggingLevel", "FINEST");
    JSONArray options2 = new JSONArray();
    filter2.put("subFilters", options2);
    JSONObject filter3 = new JSONObject();
    options2.add(filter3);
    filter3.put(JsonConsumer.KEY_CLASS, "adams.data.filter.PassThrough");

    performInputTest(new JsonConsumer(), input, handler);
  }

  /**
   * Tests generating a deeply nested option handler from a string.
   */
  public void testFromStringDeep() {
    adams.data.filter.MultiFilter handler = new adams.data.filter.MultiFilter();
    handler.setLoggingLevel(LoggingLevel.INFO);
    adams.data.filter.Filter[] filters = new adams.data.filter.Filter[2];
    filters[0] = new adams.data.filter.PassThrough();
    filters[0].setLoggingLevel(LoggingLevel.FINE);
    filters[1] = new adams.data.filter.MultiFilter();
    filters[1].setLoggingLevel(LoggingLevel.FINEST);
    handler.setSubFilters(filters);

    JSONObject input = new JSONObject();
    input.put(JsonConsumer.KEY_CLASS, "adams.data.filter.MultiFilter");
    JSONArray options = new JSONArray();
    input.put("subFilters", options);
    input.put("loggingLevel", "INFO");

    JSONObject filter1 = new JSONObject();
    options.add(filter1);
    filter1.put(JsonConsumer.KEY_CLASS, "adams.data.filter.PassThrough");
    filter1.put("loggingLevel", "FINE");

    JSONObject filter2 = new JSONObject();
    options.add(filter2);
    filter2.put(JsonConsumer.KEY_CLASS, "adams.data.filter.MultiFilter");
    filter2.put("loggingLevel", "FINEST");
    JSONArray options2 = new JSONArray();
    filter2.put("subFilters", options2);
    JSONObject filter3 = new JSONObject();
    options2.add(filter3);
    filter3.put(JsonConsumer.KEY_CLASS, "adams.data.filter.PassThrough");

    performFromStringTest(
	JsonConsumer.class,
	input.toJSONString(),
	handler);
  }

  /**
   * Tests another deeply nested option handler.
   */
  public void testConsumeDeep2() {
    adams.data.filter.BaselineCorrection handler = new adams.data.filter.BaselineCorrection();
    adams.data.baseline.SlidingWindow baselineObj = new adams.data.baseline.SlidingWindow();
    baselineObj.setLoggingLevel(LoggingLevel.FINE);
    handler.setBaselineCorrection(baselineObj);

    JSONObject input = new JSONObject();
    input.put(JsonConsumer.KEY_CLASS, "adams.data.filter.BaselineCorrection");
    JSONObject input2 = new JSONObject();
    input.put("baselineCorrection", input2);
    input2.put(JsonConsumer.KEY_CLASS, "adams.data.baseline.SlidingWindow");
    input2.put("loggingLevel", "FINE");
    JSONObject input3 = new JSONObject();
    input2.put("baselineCorrection", input3);
    input3.put(JsonConsumer.KEY_CLASS, "adams.data.baseline.PassThrough");

    performInputTest(new JsonConsumer(), input, handler);
  }

  /**
   * Tests generating another deeply nested option handler from a string.
   */
  public void testFromStringDeep2() {
    adams.data.filter.BaselineCorrection handler = new adams.data.filter.BaselineCorrection();
    adams.data.baseline.SlidingWindow baseline = new adams.data.baseline.SlidingWindow();
    baseline.setLoggingLevel(LoggingLevel.FINE);
    handler.setBaselineCorrection(baseline);

    JSONObject input = new JSONObject();
    input.put(JsonConsumer.KEY_CLASS, "adams.data.filter.BaselineCorrection");
    JSONObject input2 = new JSONObject();
    input.put("baselineCorrection", input2);
    input2.put(JsonConsumer.KEY_CLASS, "adams.data.baseline.SlidingWindow");
    input2.put("loggingLevel", "FINE");
    JSONObject input3 = new JSONObject();
    input2.put("baselineCorrection", input3);
    input3.put(JsonConsumer.KEY_CLASS, "adams.data.baseline.PassThrough");

    performFromStringTest(
	JsonConsumer.class,
	input.toJSONString(),
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

    JSONObject input = new JSONObject();
    input.put(JsonConsumer.KEY_CLASS, "adams.flow.control.Flow");
    JSONArray actors = new JSONArray();
    input.put("actors", actors);

    JSONObject input2 = new JSONObject();
    actors.add(input2);
    input2.put(JsonConsumer.KEY_CLASS, "adams.flow.source.StringConstants");
    JSONArray strings = new JSONArray();
    strings.add("1");
    strings.add("2");
    strings.add("3");
    input2.put("strings", strings);

    JSONObject input3 = new JSONObject();
    actors.add(input3);
    input3.put(JsonConsumer.KEY_CLASS, "adams.flow.transformer.Convert");
    JSONObject input4 = new JSONObject();
    input4.put(JsonConsumer.KEY_CLASS, "adams.data.conversion.StringToDouble");
    input3.put("conversion", input4);

    JSONObject input5 = new JSONObject();
    actors.add(input5);
    input5.put(JsonConsumer.KEY_CLASS, "adams.flow.transformer.MathExpression");
    input5.put("expression", "X^2");

    JSONObject input6 = new JSONObject();
    actors.add(input6);
    input6.put(JsonConsumer.KEY_CLASS, "adams.flow.sink.DumpFile");
    input6.put("append", true);
    input6.put("outputFile", "${TMP}/dumpfile.txt");

    performFromStringTest(
	JsonConsumer.class,
	input.toJSONString(),
	handler);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(JsonConsumerTest.class);
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
