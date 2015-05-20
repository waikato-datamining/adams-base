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
 * StressTestFlow.java
 * Copyright (C) 2009-2010 University of Waikato
 */

package adams.flow;

import java.awt.BorderLayout;

import adams.env.Environment;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorUtils;
import adams.gui.core.BaseFrame;
import adams.gui.flow.FlowEditorPanel;
import adams.gui.scripting.ScriptingEngine;
import adams.test.AbstractFileStressTest;

/**
 <!-- globalinfo-start -->
 * Class for stress-testing flows.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StressTestFlow
  extends AbstractFileStressTest {

  /** for serialization. */
  private static final long serialVersionUID = -2535320030771462923L;

  /** whether to stress test using the GUI. */
  protected boolean m_UseGUI;

  /** the flow editor panel, if the GUI is tested. */
  protected FlowEditorPanel m_Panel;

  /** the frame containing the flow editor panel, if the GUI is tested. */
  protected BaseFrame m_Frame;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return
        "Class for stress-testing flows.\n"
      + "Default is to test without FlowEditor panel.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "use-gui", "useGUI",
	    false);
  }

  /**
   * Sets whether to use GUI or not.
   *
   * @param value 	if true then GUI is used
   */
  public void setUseGUI(boolean value) {
    m_UseGUI = value;
    reset();
  }

  /**
   * Returns whether GUI is used or not.
   *
   * @return 		true if GUI is used
   */
  public boolean getUseGUI() {
    return m_UseGUI;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useJmapTipText() {
    return "Whether to run tests in GUI.";
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
      protected AbstractActor m_Flow;

      protected Object doInBackground() throws Exception {
	if (m_UseGUI) {
	  if (m_Panel == null) {
	    m_Panel = new FlowEditorPanel();
	    m_Frame = new BaseFrame();
	    m_Frame.getContentPane().setLayout(new BorderLayout());
	    m_Frame.getContentPane().add(m_Panel, BorderLayout.CENTER);
	    m_Frame.setSize(600, 400);
	    m_Frame.setLocationRelativeTo(null);
	    m_Frame.setVisible(true);
	  }

	  // load
	  m_Panel.loadUnsafe(((AbstractFileStressTest) m_Owner).getFile());
	  while (m_Panel.isSwingWorkerRunning()) {
	    try {
	      synchronized(this) {
		wait(100);
	      }
	    }
	    catch (Exception e) {
	      // ignored
	    }
	  }

	  // run
	  m_Panel.run(false);
	  while (m_Panel.isRunning() || (!m_Panel.isRunning() && m_Panel.isStopping())) {
	    try {
	      synchronized(this) {
		wait(100);
	      }
	    }
	    catch (Exception e) {
	      // ignored
	    }
	  }
	}
	else {
	  // load flow
	  m_Flow = ActorUtils.read(
	      ((AbstractFileStressTest) m_Owner).getFile().getAbsolutePath());

	  // setup and execute
	  m_Flow.setUp();
	  m_Flow.execute();
	}

	return null;
      }

      protected void done() {
	// finish up
	if (m_UseGUI) {
	  m_Panel.getCurrentPanel().close();
	  m_Panel.cleanUp();
	}
	else {
	  m_Flow.wrapUp();
	  m_Flow.destroy();
	  m_Flow = null;
	}

        super.done();
      }

      public void stopExecution() {
	if (m_UseGUI)
	  m_Panel.stop();
	else
	  m_Flow.stopExecution();
      }
    };

    return result;
  }

  /**
   * For cleaning up.
   */
  protected void postExecute() {
    ScriptingEngine.stopAllEngines();
    if (m_Frame != null)
      m_Frame.dispose();
  }

  /**
   * Runs the tester from commandline.
   *
   * @param args	the commandline options, use -help to see all
   */
  public static void main(String[] args) {
    runStressTester(Environment.class, StressTestFlow.class, args);
  }
}
