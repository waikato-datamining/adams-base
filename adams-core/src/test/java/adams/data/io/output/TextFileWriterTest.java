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
 * TextFileWriterTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output;

import java.util.Arrays;
import java.util.HashSet;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.io.SimpleFilenameGenerator;
import adams.env.Environment;
import adams.test.Platform;
import adams.test.TmpDirectory;

/**
 * Tests the adams.core.io.TextFileWriter class. Run from commandline with: <p/>
 * java adams.core.io.TextFileWriter
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TextFileWriterTest
  extends AbstractTextWriterTestCase {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public TextFileWriterTest(String name) {
    super(name);
  }

  /**
   * Returns the platform this test class is for.
   * 
   * @return		the platform.
   */
  @Override
  protected HashSet<Platform> getPlatforms() {
    return new HashSet<Platform>(Arrays.asList(new Platform[]{Platform.LINUX}));
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"sample.txt",
	"sample.txt",
	"sample.txt",
	"sample.txt"
    };
  }

  /**
   * Returns the names for the input content in the regression test.
   *
   * @return		the names
   */
  @Override
  protected String[] getRegressionInputNames() {
    return new String[]{
	"dummy1",
	"dummy2",
	"dummy3",
	"dummy4"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractTextWriter[] getRegressionSetups() {
    TextFileWriter[]		result;
    SimpleFilenameGenerator	fgen;

    result = new TextFileWriter[4];

    result[0] = new TextFileWriter();
    fgen = new SimpleFilenameGenerator();
    fgen.setDirectory(new TmpDirectory());
    fgen.setExtension(".txt");
    result[0].setFilenameGenerator(fgen);

    result[1] = new TextFileWriter();
    fgen = new SimpleFilenameGenerator();
    fgen.setDirectory(new TmpDirectory());
    fgen.setExtension(".txt");
    result[1].setFilenameGenerator(fgen);
    result[1].setIgnoreName(true);

    result[2] = new TextFileWriter();
    fgen = new SimpleFilenameGenerator();
    fgen.setDirectory(new TmpDirectory());
    fgen.setPrefix("prefix-");
    fgen.setExtension(".txt");
    result[2].setFilenameGenerator(fgen);

    result[3] = new TextFileWriter();
    fgen = new SimpleFilenameGenerator();
    fgen.setDirectory(new TmpDirectory());
    fgen.setSuffix("-suffix");
    result[3].setFilenameGenerator(fgen);

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(TextFileWriterTest.class);
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
