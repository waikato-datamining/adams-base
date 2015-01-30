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
 * NotesTest.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package adams.data;

import adams.env.Environment;
import adams.test.AdamsTestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the adams.data.Notes class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9527 $
 */
public class NotesTest
  extends AdamsTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public NotesTest(String name) {
    super(name);
  }

  /**
   * Tests adding notes.
   */
  public void testAddNotes() {
    Notes notes = new Notes();

    assertEquals("size differs", 0, notes.size());
    assertNull("should be null", notes.getNotes(getClass()));
    assertEquals("size differs", 0, notes.getErrors().size());
    assertEquals("size differs", 0, notes.getWarnings().size());
    assertEquals("size differs", 0, notes.getProcessInformation().size());

    notes.addNote(getClass(), "note");
    assertEquals("size differs", 1, notes.getNotes(getClass()).size());
    assertEquals("size differs", 1, notes.size());

    notes.addError(getClass(), "error");
    assertEquals("size differs", 2, notes.getNotes(getClass()).size());
    assertEquals("size differs", 1, notes.size());

    notes.addWarning(getClass(), "warning");
    assertEquals("size differs", 3, notes.getNotes(getClass()).size());
    assertEquals("size differs", 1, notes.size());

    notes.addProcessInformation("pi");
    assertEquals("size differs", 2, notes.size());
  }

  /**
   * Tests adding duplicates.
   */
  public void testAddDuplicates() {
    Notes notes = new Notes();

    notes.addError(getClass(), "error");
    assertEquals("size differs", 1, notes.getNotes(getClass()).size());
    assertEquals("size differs", 1, notes.size());

    notes.addError(getClass(), "error");
    assertEquals("size differs", 1, notes.getNotes(getClass()).size());
    assertEquals("size differs", 1, notes.size());
  }

  /**
   * Tests getting subsets.
   */
  public void testSubsets() {
    Notes notes = new Notes();

    notes.addNote(getClass(), "note");
    notes.addWarning(getClass(), "warning");
    notes.addError(getClass(), "error");
    notes.addError(getClass(), "error2");
    notes.addProcessInformation("pi");

    assertEquals("size differs", 1, notes.getWarnings().getNotes(getClass()).size());
    assertEquals("content differs", "[WARNING: warning]", notes.getWarnings().getNotes(getClass()).toString());
    assertEquals("size differs", 2, notes.getErrors().getNotes(getClass()).size());
    assertEquals("content differs", "[ERROR: error, ERROR: error2]", notes.getErrors().getNotes(getClass()).toString());
    assertEquals("size differs", 1, notes.getOthers().getNotes(getClass()).size());
    assertEquals("content differs", "[note]", notes.getOthers().getNotes(getClass()).toString());
    assertEquals("size differs", 1, notes.getProcessInformation().getNotes(Notes.PROCESS_INFORMATION).size());
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(NotesTest.class);
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
