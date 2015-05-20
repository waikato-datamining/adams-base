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
 * StressTestControlSetup.java
 * Copyright (C) 2009-2010 University of Waikato
 */

package adams.flow;

import adams.env.Environment;
import adams.flow.setup.FlowSetup;
import adams.flow.setup.FlowSetupManager;
import adams.gui.scripting.ScriptingEngine;
import adams.test.AbstractFileStressTest;

/**
 <!-- globalinfo-start -->
 * Class for stress-testing flow control center setups.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-driver &lt;java.lang.String&gt; (property: driver)
 * &nbsp;&nbsp;&nbsp;The JDBC driver.
 * &nbsp;&nbsp;&nbsp;default: com.mysql.jdbc.Driver
 * </pre>
 *
 * <pre>-url &lt;java.lang.String&gt; (property: URL)
 * &nbsp;&nbsp;&nbsp;The database URL.
 * </pre>
 *
 * <pre>-user &lt;java.lang.String&gt; (property: user)
 * &nbsp;&nbsp;&nbsp;The database user.
 * </pre>
 *
 * <pre>-password &lt;java.lang.String&gt; (property: password)
 * &nbsp;&nbsp;&nbsp;The password of the database user.
 * </pre>
 *
 * <pre>-log &lt;adams.core.io.PlaceholderFile&gt; (property: log)
 * &nbsp;&nbsp;&nbsp;The optional log file to store the jmap output in; gets ignored if pointing
 * &nbsp;&nbsp;&nbsp;to a directory.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-num-iter &lt;int&gt; (property: numIterations)
 * &nbsp;&nbsp;&nbsp;The number of iterations to perform.
 * &nbsp;&nbsp;&nbsp;default: 10
 * </pre>
 *
 * <pre>-num-sec &lt;int&gt; (property: numSeconds)
 * &nbsp;&nbsp;&nbsp;The number of seconds before stopping the thread again; use -1 to let thread
 * &nbsp;&nbsp;&nbsp;finish.
 * &nbsp;&nbsp;&nbsp;default: 10
 * </pre>
 *
 * <pre>-reg-exp &lt;java.lang.String&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the jmap output must match.
 * &nbsp;&nbsp;&nbsp;default: .*adams\\..*
 * </pre>
 *
 * <pre>-jmap (property: useJmap)
 * &nbsp;&nbsp;&nbsp;Whether to run jmap.
 * </pre>
 *
 * <pre>-jmap-options &lt;java.lang.String&gt; (property: jmapOptions)
 * &nbsp;&nbsp;&nbsp;The commandline options for jmap, eg '-histo:live'.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-file &lt;adams.core.io.PlaceholderFile&gt; (property: file)
 * &nbsp;&nbsp;&nbsp;The file to load and execute.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-target &lt;java.lang.String&gt; (property: target)
 * &nbsp;&nbsp;&nbsp;The target in the flow control center setup to execute.
 * &nbsp;&nbsp;&nbsp;default: blah
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StressTestControlSetup
  extends AbstractFileStressTest {

  /** for serialization. */
  private static final long serialVersionUID = 4802926584425588665L;

  /** the control center target to execute. */
  protected String m_Target;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Class for stress-testing flow control center setups.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "target", "target",
	    "blah");
  }

  /**
   * Sets the target of the setup to execute.
   *
   * @param value 	the target
   */
  public void setTarget(String value) {
    m_Target = value;
    reset();
  }

  /**
   * Returns the target of the setup to execute.
   *
   * @return 		the target
   */
  public String getTarget() {
    return m_Target;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String targetTipText() {
    return "The target in the flow control center setup to execute.";
  }

  /**
   * Sets up a Worker instance, ready to be executed.
   *
   * @param iteration	the current iteration
   * @return		the Worker instance
   */
  protected Worker setupWorker(int iteration) {
    Worker	result;

    result = new Worker(this, iteration) {
      protected FlowSetupManager m_Manager;
      protected FlowSetup m_Setup;

      protected Object doInBackground() throws Exception {
	int	index;

	// load setup
	m_Manager = new FlowSetupManager();
	m_Manager.read(
	    ((AbstractFileStressTest) m_Owner).getFile().getAbsolutePath());
	index   = m_Manager.indexOf(((StressTestControlSetup) m_Owner).getTarget());
	m_Setup = m_Manager.get(index);

	// execute setup
	m_Setup.execute(true);

	return null;
      }

      protected void done() {
	// finish up
	m_Setup.cleanUp();
	m_Setup = null;

        super.done();
      }

      public void stopExecution() {
	m_Setup.stopExecution();
      }
    };

    return result;
  }

  /**
   * For cleaning up.
   */
  protected void postExecute() {
    ScriptingEngine.stopAllEngines();
  }

  /**
   * Runs the tester from commandline.
   *
   * @param args	the commandline options, use -help to see all
   */
  public static void main(String[] args) {
    runStressTester(Environment.class, StressTestControlSetup.class, args);
  }
}
