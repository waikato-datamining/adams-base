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
 * FTPGetTest.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BasePassword;
import adams.core.base.BaseRegExp;
import adams.env.Environment;
import adams.flow.AbstractOnlineFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;
import adams.flow.source.FTPLister;
import adams.flow.standalone.FTPConnection;
import adams.test.TmpDirectory;

/**
 * Test case for the FTPGet actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FTPGetTest
  extends AbstractOnlineFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public FTPGetTest(String name) {
    super(name);
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>AbstractActor</code> value
   */
  public AbstractActor getActor() {
    FTPConnection fc = new FTPConnection();
    fc.setHost("ftp.suse.com");
    fc.setUser("anonymous");
    fc.setPassword(new BasePassword(""));
    fc.setUsePassiveMode(true);

    FTPLister fl = new FTPLister();
    fl.setRemoteDir("/pub");
    fl.setListFiles(true);
    fl.setRegExp(new BaseRegExp("README.*"));

    FTPGet fg = new FTPGet();
    fg.setRemoteDir("/pub");
    fg.setOutputDirectory(new TmpDirectory());

    Flow flow = new Flow();
    flow.setActors(new AbstractActor[]{
	fc,
	fl,
	fg
    });

    return flow;
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(FTPGetTest.class);
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
