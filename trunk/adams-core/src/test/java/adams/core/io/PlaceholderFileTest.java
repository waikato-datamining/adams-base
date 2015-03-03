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
 * PlaceholderFileTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io;

import adams.test.AdamsTestCase;

/**
 * Tests for the PlaceholderFile class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PlaceholderFileTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public PlaceholderFileTest(String name) {
    super(name);
  }
  
  /**
   * Tests the getExtension methods.
   */
  public void testGetExtension() {
    PlaceholderFile file;
    
    file = new PlaceholderFile("file.ext");
    assertEquals("extensions differ", "ext", file.getExtension());

    file = new PlaceholderFile("file.ext.gz");
    assertEquals("extensions differ", "gz", file.getExtension());
    assertEquals("extensions differ", "ext.gz", file.getExtension(".gz"));
    assertEquals("extensions differ", "ext.gz", file.getExtension("gz"));
  }
  
  /**
   * Tests the changeExtension methods.
   */
  public void testChangeExtension() {
    PlaceholderFile file;
    
    file = new PlaceholderFile("file.ext");
    assertEquals("file names differ", "file.new", file.changeExtension("new").getName());

    file = new PlaceholderFile("file.ext.gz");
    assertEquals("file names differ", "file.ext.new", file.changeExtension("new").getName());
    assertEquals("file names differ", "file.new", file.changeExtension("ext.gz", "new").getName());
    assertEquals("file names differ", "file.ext.gz", file.changeExtension("ext.bz2", "new").getName());
  }
  
  /**
   * Tests the UNC handling.
   */
  public void testUNC() {
    PlaceholderFile file;
    
    file = new PlaceholderFile("\\\\server\\share\\file.txt");
    assertEquals("UNC backslashes lost", "\\\\", file.toString().substring(0, 2));
    
    file = new PlaceholderFile("//server/share/file.txt");
    assertEquals("UNC backslashes lost", "\\\\", file.toString().substring(0, 2));
  }
}
