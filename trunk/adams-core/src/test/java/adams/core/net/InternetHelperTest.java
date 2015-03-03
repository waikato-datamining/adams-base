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
 * InternetHelperTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.net;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.core.net.InternetHelper class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 5150 $
 */
public class InternetHelperTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public InternetHelperTest(String name) {
    super(name);
  }
  
  /**
   * Tests the {@link InternetHelper#encodeUrlAsFilename(String)} method.
   */
  public void testEncodeUrlAsFilename() {
    String url;
    url = "http://example.com/";
    assertEquals("http(c)(s)(s)example.com(s)", InternetHelper.encodeUrlAsFilename(url));
    url = "http://example.com/?p1=some&p2=other";
    assertEquals("http(c)(s)(s)example.com(s)(q)p1=some(a)p2=other", InternetHelper.encodeUrlAsFilename(url));
  }

  /**
   * Tests the {@link InternetHelper#decodeUrlFromFilename(String)} method.
   */
  public void testDecodeUrlFromFilename() {
    String fname;
    fname = "http(c)(s)(s)example.com(s)";
    assertEquals("http://example.com/", InternetHelper.decodeUrlFromFilename(fname));
    fname = "http(c)(s)(s)example.com(s)(q)p1=some(a)p2=other";
    assertEquals("http://example.com/?p1=some&p2=other", InternetHelper.decodeUrlFromFilename(fname));
  }
  
  /**
   * Tests the {@link InternetHelper#extractUrlFromFilename(String)} method.
   */
  public void testExtractUrlFromFilename() {
    String fname;
    fname = "http(c)(s)(s)example.com(s)";
    assertEquals("http://example.com/", InternetHelper.extractUrlFromFilename(fname));
    fname = "/some/where/pre-((http(c)(s)(s)example.com(s))).txt";
    assertEquals("http://example.com/", InternetHelper.extractUrlFromFilename(fname));
  }
  
  /**
   * Tests the {@link InternetHelper#createUrlFilename(String, String, String, String, String)}
   * method.
   */
  public void testCreateUrlFilename() {
    String s = File.separator;
    assertEquals(
	"((http(c)(s)(s)example.com(s)))", 
	InternetHelper.createUrlFilename(null, null, "http://example.com/", null, null));
    assertEquals(
	s + "some" + s + "where" + s + "pre-((http(c)(s)(s)example.com(s)))-suff.txt", 
	InternetHelper.createUrlFilename(s + "some" + s + "where", "pre-", "http://example.com/", "-suff", ".txt"));
  }
  
  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(InternetHelperTest.class);
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
