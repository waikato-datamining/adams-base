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
 * UtilsTest.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.core;

import adams.env.Environment;
import adams.test.AdamsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.lang.reflect.Array;

/**
 * Tests the adams.core.Utils class. Run from commandline with: <br><br>
 * java adams.core.UtilsTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UtilsTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public UtilsTest(String name) {
    super(name);
  }

  /**
   * Test the {@link Utils#padLeft(String, char, int)} method with no truncating.
   */
  public void testPadLeftNoTruncate() {
    String 	in;
    String	out;

    in  = "blah";
    out = Utils.padLeft(in, '0', 10);
    assertEquals("Length wrong", 10, out.length());
    assertEquals("Content wrong", "000000blah", out);

    in  = "blah";
    out = Utils.padLeft(in, '0', 4);
    assertEquals("Length wrong", 4, out.length());
    assertEquals("Content wrong", in, out);

    in  = "blah";
    out = Utils.padLeft(in, '0', 3);
    assertEquals("Length wrong", 4, out.length());
    assertEquals("Content wrong", in, out);
  }

  /**
   * Test the {@link Utils#padLeft(String, char, int, boolean)} method 
   * with truncating.
   */
  public void testPadLeftWithTruncate() {
    String 	in;
    String	out;

    in  = "blah";
    out = Utils.padLeft(in, '0', 10, true);
    assertEquals("Length wrong", 10, out.length());
    assertEquals("Content wrong", "000000blah", out);

    in  = "blah";
    out = Utils.padLeft(in, '0', 4, true);
    assertEquals("Length wrong", 4, out.length());
    assertEquals("Content wrong", in, out);

    in  = "blah";
    out = Utils.padLeft(in, '0', 3, true);
    assertEquals("Length wrong", 3, out.length());
    assertEquals("Content wrong", "lah", out);
  }

  /**
   * Performs a test for the {@link Utils#split(String, char)} method.
   *
   * @param in		the string to split
   * @param delim	the delimiter to use
   * @param exp		the expected output
   */
  protected void performSplitTest(String in, char delim, String[] exp) {
    String[]	out;
    int		i;

    out = Utils.split(in, delim);
    assertEquals("# of cells differs", exp.length, out.length);
    for (i = 0; i < exp.length; i++)
      assertEquals("cell #" + (i+1) + " differs", exp[i], out[i]);
  }

  /**
   * Tests the {@link Utils#split(String, char)} method.
   */
  public void testSplit() {
    performSplitTest("hello", ',', new String[]{"hello"});
    performSplitTest("hello,world", ',', new String[]{"hello", "world"});
    performSplitTest("hello", '|', new String[]{"hello"});
    performSplitTest("hello|world", '|', new String[]{"hello", "world"});
    performSplitTest("hello,world,out,there", ',', new String[]{"hello", "world", "out", "there"});
    performSplitTest("hello,world,out,there", '\t', new String[]{"hello,world,out,there"});
    performSplitTest("hello\tworld\tout\tthere", '\t', new String[]{"hello", "world", "out", "there"});
    performSplitTest("hello\t\tout\tthere", '\t', new String[]{"hello", "", "out", "there"});
    performSplitTest("hello\t\t\t", '\t', new String[]{"hello", "", "", ""});
  }

  /**
   * Tests the {@link Utils#doubleToStringFixed(double, int)} method.
   */
  public void testDoubleToStringFixed() {
    double val;
    
    // positive
    val = 1.234;
    assertEquals("output differs", "1.234", Utils.doubleToStringFixed(val, -1));
    assertEquals("output differs", "1",     Utils.doubleToStringFixed(val,  0));
    assertEquals("output differs", "1.2",   Utils.doubleToStringFixed(val,  1));
    assertEquals("output differs", "1.23",  Utils.doubleToStringFixed(val,  2));

    val = 9.876;
    assertEquals("output differs", "9.876", Utils.doubleToStringFixed(val, -1));
    assertEquals("output differs", "9",     Utils.doubleToStringFixed(val,  0));
    assertEquals("output differs", "9.8",   Utils.doubleToStringFixed(val,  1));
    assertEquals("output differs", "9.87",  Utils.doubleToStringFixed(val,  2));

    val = 0.071;
    assertEquals("output differs", "0.071", Utils.doubleToStringFixed(val, -1));
    assertEquals("output differs", "0",     Utils.doubleToStringFixed(val,  0));
    assertEquals("output differs", "0.0",   Utils.doubleToStringFixed(val,  1));
    assertEquals("output differs", "0.07",  Utils.doubleToStringFixed(val,  2));

    val = 0.0071;
    assertEquals("output differs", "0.0071", Utils.doubleToStringFixed(val, -1));
    assertEquals("output differs", "0",      Utils.doubleToStringFixed(val,  0));
    assertEquals("output differs", "0.0",    Utils.doubleToStringFixed(val,  1));
    assertEquals("output differs", "0.00",   Utils.doubleToStringFixed(val,  2));
    assertEquals("output differs", "0.007",  Utils.doubleToStringFixed(val,  3));
    
    // negative
    val = -1.234;
    assertEquals("output differs", "-1.234", Utils.doubleToStringFixed(val, -1));
    assertEquals("output differs", "-1",     Utils.doubleToStringFixed(val,  0));
    assertEquals("output differs", "-1.2",   Utils.doubleToStringFixed(val,  1));
    assertEquals("output differs", "-1.23",  Utils.doubleToStringFixed(val,  2));

    val = -9.876;
    assertEquals("output differs", "-9.876", Utils.doubleToStringFixed(val, -1));
    assertEquals("output differs", "-9",     Utils.doubleToStringFixed(val,  0));
    assertEquals("output differs", "-9.8",   Utils.doubleToStringFixed(val,  1));
    assertEquals("output differs", "-9.87",  Utils.doubleToStringFixed(val,  2));

    val = -0.071;
    assertEquals("output differs", "-0.071", Utils.doubleToStringFixed(val, -1));
    assertEquals("output differs", "0",     Utils.doubleToStringFixed(val,  0));
    assertEquals("output differs", "0.0",   Utils.doubleToStringFixed(val,  1));
    assertEquals("output differs", "-0.07",  Utils.doubleToStringFixed(val,  2));

    val = -0.0071;
    assertEquals("output differs", "-0.0071", Utils.doubleToStringFixed(val, -1));
    assertEquals("output differs", "0",      Utils.doubleToStringFixed(val,  0));
    assertEquals("output differs", "0.0",    Utils.doubleToStringFixed(val,  1));
    assertEquals("output differs", "0.00",   Utils.doubleToStringFixed(val,  2));
    assertEquals("output differs", "-0.007",  Utils.doubleToStringFixed(val,  3));
  }

  /**
   * Tests the {@link Utils#toFloat(String)} method.
   */
  public void testToFloat() {
    String s;
    
    s = "1";
    assertEquals("failed to parse: " + s, 1.0f, Utils.toFloat(s));
    s = "1.0";
    assertEquals("failed to parse: " + s, 1.0f, Utils.toFloat(s));
    s = "A";
    assertEquals("failed to parse: " + s, null, Utils.toFloat(s));
    s = "1E10";
    assertEquals("failed to parse: " + s, 1E10f, Utils.toFloat(s));
    s = "1,230";
    assertEquals("failed to parse: " + s, 1230.0f, Utils.toFloat(s));
    s = "1.anneal";
    assertEquals("failed to parse: " + s, null, Utils.toFloat(s));
    s = "1,234.78";
    assertEquals("failed to parse: " + s, 1234.78f, Utils.toFloat(s));
  }

  /**
   * Tests the {@link Utils#toDouble(String)} method.
   */
  public void testToDouble() {
    String s;
    
    s = "1";
    assertEquals("failed to parse: " + s, 1.0, Utils.toDouble(s));
    s = "1.0";
    assertEquals("failed to parse: " + s, 1.0, Utils.toDouble(s));
    s = "A";
    assertEquals("failed to parse: " + s, null, Utils.toDouble(s));
    s = "1E10";
    assertEquals("failed to parse: " + s, 1E10, Utils.toDouble(s));
    s = "1,230";
    assertEquals("failed to parse: " + s, 1230.0, Utils.toDouble(s));
    s = "1.anneal";
    assertEquals("failed to parse: " + s, null, Utils.toDouble(s));
    s = "1,234.78";
    assertEquals("failed to parse: " + s, 1234.78, Utils.toDouble(s));
  }
  
  /**
   * Test the {@link Utils#doubleUpQuotes(String, char, char[], String[])}
   * method.
   */
  public void testDoubleUpQuotes() {
    String s;
    
    s = "hello world";
    assertEquals("strings differ", "\"hello world\"", Utils.doubleUpQuotes(s, '"', new char[]{}, new String[]{}));

    s = "\"hello world\"";
    assertEquals("strings differ", "\"\"\"hello world\"\"\"", Utils.doubleUpQuotes(s, '"', new char[]{}, new String[]{}));

    s = "my name is \"Blah\". what is yours?";
    assertEquals("strings differ", "\"my name is \"\"Blah\"\". what is yours?\"", Utils.doubleUpQuotes(s, '"', new char[]{}, new String[]{}));

    s = "an sql string's content with a \t (tab)";
    assertEquals("strings differ", "'an sql string''s content with a \\t (tab)'", Utils.doubleUpQuotes(s, '\'', new char[]{'\t'}, new String[]{"\\t"}));
  }
  
  /**
   * Test the {@link Utils#unDoubleUpQuotes(String, char, String[], char[])}
   * method.
   */
  public void testUnDoubleUpQuotes() {
    String s;
    
    s = "\"hello world\"";
    assertEquals("strings differ", "hello world", Utils.unDoubleUpQuotes(s, '"', new String[]{}, new char[]{}));

    s = "\"\"\"hello world\"\"\"";
    assertEquals("strings differ", "\"hello world\"", Utils.unDoubleUpQuotes(s, '"', new String[]{}, new char[]{}));

    s = "\"my name is \"\"Blah\"\". what is yours?\"";
    assertEquals("strings differ", "my name is \"Blah\". what is yours?", Utils.unDoubleUpQuotes(s, '"', new String[]{}, new char[]{}));

    s = "'an sql string''s content with a \\t (tab)'";
    assertEquals("strings differ", "an sql string's content with a \t (tab)", Utils.unDoubleUpQuotes(s, '\'', new String[]{"\\t"}, new char[]{'\t'}));
  }

  /**
   * Tests the {@link Utils#classToString(Class)} method.
   */
  public void testClassToString() {
    assertEquals("java.lang.String", Utils.classToString(String.class));
    assertEquals("java.lang.String[]", Utils.classToString(String[].class));
    assertEquals("java.lang.String[][]", Utils.classToString(String[][].class));
    assertEquals("java.lang.String[][][]", Utils.classToString(String[][][].class));
  }

  /**
   * Tests the {@link Utils#classesToString(Class[])} method.
   */
  public void testClassesToString() {
    assertEquals("java.lang.String", Utils.classesToString(new Class[]{String.class}));
    assertEquals("java.lang.String, java.lang.Integer", Utils.classesToString(new Class[]{String.class, Integer.class}));
    assertEquals("java.lang.String[]", Utils.classesToString(new Class[]{String[].class}));
    assertEquals("java.lang.String, java.lang.Integer[][]", Utils.classesToString(new Class[]{String.class, Integer[][].class}));
  }

  /**
   * Tests the {@link Utils#stringToClass(String)} method.
   */
  public void testStringToClass() {
    assertEquals(Utils.stringToClass("java.lang.String"), String.class);
    assertEquals(Utils.stringToClass("java.lang.String[]"), String[].class);
    assertEquals(Utils.stringToClass("java.lang.String[][]"), String[][].class);
    assertEquals(Utils.stringToClass("java.lang.String[][][]"), String[][][].class);
  }

  /**
   * Tests the {@link Utils#newArray(String, int)} method.
   */
  public void testNewArray() {
    Object	obj;
    
    obj = Utils.newArray("java.lang.String", 5);
    assertEquals(obj.getClass(), String[].class);
    assertEquals(Array.getLength(obj), 5);
    
    obj = Utils.newArray("java.lang.String[]", 3);
    assertEquals(obj.getClass(), String[][].class);
    assertEquals(Array.getLength(obj), 3);
  }

  /**
   * Tests the {@link Utils#toHex(byte)} method.
   */
  public void testToHex() {
    assertEquals("00", Utils.toHex((byte) 0));
    assertEquals("01", Utils.toHex((byte) 1));
    assertEquals("0A", Utils.toHex((byte) 10));
    assertEquals("10", Utils.toHex((byte) 16));
    assertEquals("FF", Utils.toHex((byte) 255));
    assertEquals("80", Utils.toHex(Byte.MIN_VALUE));
    assertEquals("7F", Utils.toHex(Byte.MAX_VALUE));
  }

  /**
   * Tests the {@link Utils#toHexArray(byte[])} method.
   */
  public void testToHexArray() {
    assertEquals("00", Utils.toHexArray(new byte[]{0}));
    assertEquals("00010A10FF807F", Utils.toHexArray(new byte[]{0, 1, 10, 16, (byte) 255, Byte.MIN_VALUE, Byte.MAX_VALUE}));
  }

  /**
   * Tests the {@link Utils#fromHexArray(String)} method.
   */
  public void testFromHexArray() {
    assertEqualsArrays(new byte[]{0}, Utils.fromHexArray("00"));
    assertEqualsArrays(new byte[]{0, 1, 10, 16, (byte) 255, Byte.MIN_VALUE, Byte.MAX_VALUE}, Utils.fromHexArray("00010A10FF807F"));
  }

  /**
   * Tests the {@link Utils#fromHex(String)} method.
   */
  public void testFromHex() {
    assertEquals((byte) 0, Utils.fromHex("00"));
    assertEquals((byte) 1, Utils.fromHex("01"));
    assertEquals((byte) 10, Utils.fromHex("0A"));
    assertEquals((byte) 16, Utils.fromHex("10"));
    assertEquals((byte) 255, Utils.fromHex("FF"));
    assertEquals(Byte.MIN_VALUE, Utils.fromHex("80"));
    assertEquals(Byte.MAX_VALUE, Utils.fromHex("7F"));
  }

  /**
   * Tests the {@link Utils#escapeUnicode(String)} method.
   */
  public void testEscapeUnicode() {
    assertEquals("", Utils.escapeUnicode(""));
    assertEquals("abcdefgu31415!~", Utils.escapeUnicode("abcdefgu31415!~"));
    assertEquals("abc\\u1234blah", Utils.escapeUnicode("abc\u1234blah"));
  }

  /**
   * Tests the {@link Utils#unescapeUnicode(String)} method.
   */
  public void testUnescapeUnicode() {
    assertEquals("", Utils.unescapeUnicode(""));
    assertEquals("abcdefgu31415!~", Utils.unescapeUnicode("abcdefgu31415!~"));
    assertEquals("abc\u1234blah", Utils.unescapeUnicode("abc\\u1234blah"));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(UtilsTest.class);
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
