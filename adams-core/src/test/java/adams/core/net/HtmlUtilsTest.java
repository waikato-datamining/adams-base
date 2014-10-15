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
 * HtmlUtilsTest.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.net;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.core.net.HtmlUtils class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class HtmlUtilsTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public HtmlUtilsTest(String name) {
    super(name);
  }
  
  /**
   * Tests the fromHtml method.
   */
  public void testFromHtml() {
    assertEquals("output differs", "hello world", HtmlUtils.fromHTML("hello world"));
    assertEquals("output differs", "<a href=\"blah.com\">", HtmlUtils.fromHTML("&lt;a href=\"blah.com\"&gt;"));
    assertEquals("output differs", "<<&>>", HtmlUtils.fromHTML("&lt;&lt;&amp;&gt;&gt;"));
    assertEquals("output differs", "<>@/&", HtmlUtils.fromHTML("&lt;&gt;&#64;&#47;&amp;"));
  }
  
  /**
   * Tests the toHtml method.
   */
  public void testToHtml() {
    assertEquals("output differs", "hello world", HtmlUtils.toHTML("hello world"));
    assertEquals("output differs", "&lt;a href=\"blah.com\"&gt;", HtmlUtils.toHTML("<a href=\"blah.com\">"));
    assertEquals("output differs", "&lt;&lt;&amp;&gt;&gt;", HtmlUtils.toHTML("<<&>>"));
    assertEquals("output differs", "&lt;&gt;&#64;&#47;&amp;", HtmlUtils.toHTML("<>@/&"));
  }
  
  /**
   * Tests the markUpURLs method.
   */
  public void testMarkUpURLs() {
    assertEquals(
	"output differs", 
	"hello world", 
	HtmlUtils.markUpURLs("hello world", "<a href=\"$0\">$0</a>", false));

    assertEquals(
	"output differs", 
	"one url <a href=\"http://www.waikato.ac.nz/\">http://www.waikato.ac.nz/</a> in the text", 
	HtmlUtils.markUpURLs("one url http://www.waikato.ac.nz/ in the text", "<a href=\"$0\">$0</a>", false));
    
    assertEquals(
	"output differs", 
	"first url <a href=\"http://www.waikato.ac.nz/\">http://www.waikato.ac.nz/</a> and the second url <a href=\"http://www.cs.waikato.ac.nz/\">http://www.cs.waikato.ac.nz/</a>", 
	HtmlUtils.markUpURLs("first url http://www.waikato.ac.nz/ and the second url http://www.cs.waikato.ac.nz/", "<a href=\"$0\">$0</a>", false));
    
    assertEquals(
	"output differs", 
	"a &lt; b and b &gt; c; first url <a href=\"http://www.waikato.ac.nz/\">http://www.waikato.ac.nz/</a> and the second url <a href=\"http://www.cs.waikato.ac.nz/\">http://www.cs.waikato.ac.nz/</a>", 
	HtmlUtils.markUpURLs("a < b and b > c; first url http://www.waikato.ac.nz/ and the second url http://www.cs.waikato.ac.nz/", "<a href=\"$0\">$0</a>", true));
    
    assertEquals(
	"output differs", 
	"a &lt; b and b &gt; c; first url <a href=\"https://adams.cms.waikato.ac.nz:8080/\">https://adams.cms.waikato.ac.nz:8080/</a> and the second url <a href=\"http://www.cs.waikato.ac.nz/\">http://www.cs.waikato.ac.nz/</a>", 
	HtmlUtils.markUpURLs("a < b and b > c; first url https://adams.cms.waikato.ac.nz:8080/ and the second url http://www.cs.waikato.ac.nz/", "<a href=\"$0\">$0</a>", true));
    
    assertEquals(
	"output differs", 
	"first document <a href=\"file:///some/file.txt\">file:///some/file.txt</a> and a URL at <a href=\"ftp://ftp.suse.com/\">ftp://ftp.suse.com/</a>", 
	HtmlUtils.markUpURLs("first document file:///some/file.txt and a URL at ftp://ftp.suse.com/", "<a href=\"$0\">$0</a>", true));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(HtmlUtilsTest.class);
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
