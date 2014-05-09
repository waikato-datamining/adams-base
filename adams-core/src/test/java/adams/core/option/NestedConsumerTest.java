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
 * NestedConsumerTest.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.util.ArrayList;
import java.util.Arrays;

import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.core.option.NestedFormatHelper.Line;
import adams.flow.core.AbstractActor;
import adams.parser.MathematicalExpressionText;

/**
 * Tests the NestedConsumer class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NestedConsumerTest
  extends AbstractOptionConsumerTestCase<ArrayList> {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public NestedConsumerTest(String name) {
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

    ArrayList input = new ArrayList();
    input.add(new Line("adams.flow.sink.DumpFile"));
    ArrayList options = new ArrayList();
    input.add(options);
    options.addAll(Arrays.asList(new Line[]{
	new Line("-logging-level"),
	new Line("INFO"),
	new Line("-output"),
	new Line("${TMP}/dumpfile.csv"),
	new Line("-append"),
	new Line("true")
    }));

    performInputTest(new NestedConsumer(), input, handler);
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
	NestedConsumer.class,
	  "adams.flow.sink.DumpFile\n"
	+ "\t-logging-level\n"
	+ "\tINFO\n"
	+ "\t-output\n"
	+ "\t${TMP}/dumpfile.csv\n"
	+ "\t-append\n"
	+ "\ttrue\n",
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

    ArrayList input = new ArrayList();
    input.add(new Line("adams.data.filter.MultiFilter"));
    ArrayList options = new ArrayList();
    input.add(options);
    options.add(new Line("-logging-level"));
    options.add(new Line("INFO"));

    options.add(new Line("-filter"));
    ArrayList filter1 = new ArrayList();
    options.add(filter1);
    filter1.add(new Line("adams.data.filter.PassThrough"));
    ArrayList options1 = new ArrayList();
    filter1.add(options1);
    options1.add(new Line("-logging-level"));
    options1.add(new Line("FINE"));

    options.add(new Line("-filter"));
    ArrayList filter2 = new ArrayList();
    options.add(filter2);
    filter2.add(new Line("adams.data.filter.MultiFilter"));
    ArrayList options2 = new ArrayList();
    filter2.add(options2);
    options2.add(new Line("-logging-level"));
    options2.add(new Line("FINEST"));
    options2.add(new Line("-filter"));
    ArrayList filter3 = new ArrayList();
    options2.add(filter3);
    filter3.add(new Line("adams.data.filter.PassThrough"));

    performInputTest(new NestedConsumer(), input, handler);
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
	NestedConsumer.class,
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

    ArrayList input = new ArrayList();
    input.add(new Line("adams.data.filter.BaselineCorrection"));
    ArrayList options = new ArrayList();
    input.add(options);
    options.add(new Line("-baseline"));
    ArrayList baseline = new ArrayList();
    options.add(baseline);
    baseline.add(new Line("adams.data.baseline.SlidingWindow"));
    ArrayList optionsBase = new ArrayList();
    baseline.add(optionsBase);
    optionsBase.add(new Line("-logging-level"));
    optionsBase.add(new Line("FINE"));
    optionsBase.add(new Line("-baseline"));
    ArrayList baseline2 = new ArrayList();
    optionsBase.add(baseline2);
    baseline2.add(new Line("adams.data.baseline.PassThrough"));

    performInputTest(new NestedConsumer(), input, handler);
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
	NestedConsumer.class,
	  "adams.data.filter.BaselineCorrection\n"
	+ "\t-baseline\n"
	+ "\t\tadams.data.baseline.SlidingWindow\n"
	+ "\t\t\t-logging-level\n"
	+ "\t\t\tFINE\n"
	+ "\t\t\t-baseline\n"
	+ "\t\t\t\tadams.data.baseline.PassThrough\n",
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
	NestedConsumer.class,
	  "adams.flow.control.Flow\n"
	+ "\t-actor\n"
	+ "\t\tadams.flow.source.StringConstants\n"
	+ "\t\t\t-string\n"
	+ "\t\t\t1\n"
	+ "\t\t\t-string\n"
	+ "\t\t\t2\n"
	+ "\t\t\t-string\n"
	+ "\t\t\t3\n"
	+ "\t-actor\n"
	+ "\t\tadams.flow.transformer.Convert\n"
	+ "\t\t\t-conversion\n"
	+ "\t\t\t\tadams.data.conversion.StringToDouble\n"
	+ "\t-actor\n"
	+ "\t\tadams.flow.transformer.MathExpression\n"
	+ "\t\t\t-expression\n"
	+ "\t\t\tX^2\n"
	+ "\t-actor\n"
	+ "\t\tadams.flow.sink.DumpFile\n"
	+ "\t\t\t-output\n"
	+ "\t\t\t${TMP}/dumpfile.txt\n"
	+ "\t\t\t-append\n"
	+ "\t\t\ttrue\n",
	handler);
  }
}
