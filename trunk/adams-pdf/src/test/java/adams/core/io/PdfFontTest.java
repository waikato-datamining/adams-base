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
 * PdfFontTest.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.AdamsTestCase;

/**
 * Tests the adams.core.io.PdfFont class. Run from commandline with: <p/>
 * java adams.core.io.PdfFontTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PdfFontTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public PdfFontTest(String name) {
    super(name);
  }

  /**
   * Tests parsing of strings.
   */
  public void testStringParsing() {
    PdfFont	font;

    font = new PdfFont(PdfFont.HELVETICA + PdfFont.SEPARATOR + PdfFont.NORMAL + PdfFont.SEPARATOR + 12);
    assertEquals(font.getFontFamilyName(), PdfFont.HELVETICA);
    assertEquals(font.getFontFace(), PdfFont.getFontFace(PdfFont.NORMAL));
    assertEquals(font.getSize(), 12.0f);

    font = new PdfFont(PdfFont.HELVETICA + PdfFont.SEPARATOR + PdfFont.BOLD + PdfFont.SEPARATOR + 13);
    assertEquals(font.getFontFamilyName(), PdfFont.HELVETICA);
    assertEquals(font.getFontFace(), PdfFont.getFontFace(PdfFont.BOLD));
    assertEquals(font.getSize(), 13.0f);

    font = new PdfFont(PdfFont.COURIER + PdfFont.SEPARATOR + PdfFont.ITALIC + PdfFont.SEPARATOR + 14);
    assertEquals(font.getFontFamilyName(), PdfFont.COURIER);
    assertEquals(font.getFontFace(), PdfFont.getFontFace(PdfFont.ITALIC));
    assertEquals(font.getSize(), 14.0f);

    font = new PdfFont(PdfFont.TIMES_ROMAN + PdfFont.SEPARATOR + PdfFont.UNDERLINE + PdfFont.SEPARATOR + 16);
    assertEquals(font.getFontFamilyName(), PdfFont.TIMES_ROMAN);
    assertEquals(font.getFontFace(), PdfFont.getFontFace(PdfFont.UNDERLINE));
    assertEquals(font.getSize(), 16.0f);
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(PdfFontTest.class);
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
