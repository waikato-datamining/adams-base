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
 * FileUtilsTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests for the FileUtils utility class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileUtilsTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public FileUtilsTest(String name) {
    super(name);
  }

  /**
   * Tests the quoteExcutable() methods.
   */
  public void testQuoteExecutable() {
    String filename = "/usr/bin/ls";
    assertEquals("No quotes expected", filename, FileUtils.quoteExecutable(filename));

    filename = "C:\\Program files\\Blah\\funky.exe";
    assertEquals("Quotes expected", "\"" + filename + "\"", FileUtils.quoteExecutable(filename));
  }

  /**
   * Tests the getExtension() methods.
   */
  public void testGetExtension() {
    String filename = "hello.txt";
    assertEquals("Extensions differ", "txt", FileUtils.getExtension(filename));
    filename = "hello.props";
    assertEquals("Extensions differ", "props", FileUtils.getExtension(filename));
    filename = "/some/where/hello.props";
    assertEquals("Extensions differ", "props", FileUtils.getExtension(filename));
    filename = "/some/where/hello";
    assertNull("No extension available", FileUtils.getExtension(filename));
    filename = "/some/where/hello.tar.gz";
    assertEquals("Extensions differ", "tar.gz", FileUtils.getExtension(filename));

    File file = new File("hello.txt");
    assertEquals("Extensions differ", "txt", FileUtils.getExtension(file));
    file = new File("hello.props");
    assertEquals("Extensions differ", "props", FileUtils.getExtension(file));
    file = new File("/some/where/hello.props");
    assertEquals("Extensions differ", "props", FileUtils.getExtension(file));
    file = new File("/some/where/hello");
    assertNull("No extension available", FileUtils.getExtension(file));
    file = new File("/some/where/hello.tar.gz");
    assertEquals("Extensions differ", "tar.gz", FileUtils.getExtension(file));
    file = new File("/some/where/hello.model.gz");
    assertEquals("Extensions differ", "model.gz", FileUtils.getExtension(file));
    file = new File("/some/where/213.213.model.gz");
    assertEquals("Extensions differ", "model.gz", FileUtils.getExtension(file));
  }

  /**
   * Tests the getExtensions() methods.
   */
  public void testGetExtensions() {
    String filename = "hello.txt";
    assertEquals("# of Extensions differ", 1, FileUtils.getExtensions(filename).length);
    assertEquals("Extension differs", "txt", FileUtils.getExtensions(filename)[0]);
    filename = "/some/where/hello.tar.gz";
    assertEquals("# of Extensions differ", 1, FileUtils.getExtensions(filename).length);
    assertEquals("Extension differs", "tar.gz", FileUtils.getExtensions(filename)[0]);

    File file = new File("hello.txt");
    assertEquals("# of Extensions differ", 1, FileUtils.getExtensions(file).length);
    assertEquals("Extension differs", "txt", FileUtils.getExtensions(file)[0]);
    file = new File("/some/where/hello.tar.gz");
    assertEquals("# of Extensions differ", 1, FileUtils.getExtensions(file).length);
    assertEquals("Extension differs", "tar.gz", FileUtils.getExtensions(file)[0]);
  }

  /**
   * Tests the {@link FileUtils#createFilename(String, String)} method.
   */
  public void testCreateFilename() {
    String file = "hello world! .txt";
    assertEquals("Filename differs", "helloworld.txt", FileUtils.createFilename(file, ""));
    assertEquals("Filename differs", "hello_world__.txt", FileUtils.createFilename(file, "_"));

    file = "someone@something.com";
    assertEquals("Filename differs", file, FileUtils.createFilename(file, ""));
    assertEquals("Filename differs", file, FileUtils.createFilename(file, "_"));

    file = "blah#$%^().txt";
    assertEquals("Filename differs", "blah#().txt", FileUtils.createFilename(file, ""));
    assertEquals("Filename differs", "blah#___().txt", FileUtils.createFilename(file, "_"));
    
    file = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_-.,;()#@";
    assertEquals("Filename differs", file, FileUtils.createFilename(file, ""));
    assertEquals("Filename differs", file, FileUtils.createFilename(file, "_"));
  }

  /**
   * Tests the {@link FileUtils#getDirectoryDepth(File)} method.
   */
  public void testDirectoryDepth() {
    File file;
    
    file = new File("/home/blah/some/where.txt");
    assertEquals("depth differs", 3, FileUtils.getDirectoryDepth(file));

    file = new File("/");
    assertEquals("depth differs", 0, FileUtils.getDirectoryDepth(file));
  }
  
  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(FileUtilsTest.class);
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
