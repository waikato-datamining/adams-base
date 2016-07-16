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

/**
 * MultiExperimenter.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekaevaluator;

import adams.core.Utils;
import adams.env.Environment;
import adams.gui.core.BaseFrame;
import adams.gui.core.GUIHelper;
import adams.gui.workspace.AbstractWorkspaceManagerPanel;
import weka.core.Memory;
import weka.core.logging.Logger;
import weka.core.logging.Logger.Level;
import weka.gui.LookAndFeel;

import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.io.File;

/**
 * Extended interface for the WEKA Experimenter, allowing for an arbitrary
 * number of Experimenter panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8799 $
 */
public class MultiExperimenter
  extends AbstractWorkspaceManagerPanel<ExperimenterPanel> {

  /** for serialization. */
  private static final long serialVersionUID = -20320489406680254L;

  /**
   * The default name for a workspace.
   *
   * @return		the default
   */
  protected String getDefaultWorkspaceName() {
    return "Experiment";
  }

  /**
   * Returns a new workspace instance.
   *
   * @return		the workspace
   */
  @Override
  protected ExperimenterPanel newWorkspace() {
    return new ExperimenterPanel();
  }

  /**
   * Instantiates a new panel for workspaces.
   *
   * @return		the list panel
   */
  protected ExperimenterEntryPanel newWorkspaceList() {
    return new ExperimenterEntryPanel();
  }

  /**
   * Loads the specified file in a new panel.
   * 
   * @param file	the file to load
   */
  public void load(File file) {
    load(new File[]{file});
  }
  
  /**
   * Loads the specified files in new panels.
   * 
   * @param files	the files to load
   */
  public void load(File[] files) {
    String	name;

    for (final File file: files) {
      name = file.getName();
      if (name.lastIndexOf('.') > -1)
	name = name.substring(0, name.lastIndexOf('.'));
      final ExperimenterPanel panel = new ExperimenterPanel();
      addPanel(panel, name);
      SwingUtilities.invokeLater(() -> panel.openSetup(file));
    }
  }

  /**
   * variable for the Experimenter class which would be set to null by the memory
   * monitoring thread to free up some memory if we running out of memory
   */
  private static MultiExperimenter m_Experimenter;

  /** for monitoring the Memory consumption */
  protected static Memory m_Memory = new Memory(true);

  /**
   * Runs an experimenter instance. Also interpretes the "-env classname" parameter.
   * 
   * @param args	the command-line arguments
   */
  public static void runExperimenter(String[] args) {
    // configure environment
    String env;
    try {
      env = weka.core.Utils.getOption("env", args);
    }
    catch (Exception e) {
      env = "";
    }
    if (env.isEmpty())
      env = Environment.class.getName();
    try {
      Environment.setEnvironmentClass(Class.forName(env));
    }
    catch (Exception e) {
      System.err.println("Failed to instantiate environment class: " + env);
      e.printStackTrace();
      Environment.setEnvironmentClass(Environment.class);
    }
    
    Logger.log(Level.INFO, "Logging started");
    LookAndFeel.setLookAndFeel();
    // make sure that packages are loaded and the GenericPropertiesCreator
    // executes to populate the lists correctly
    weka.gui.GenericObjectEditor.determineClasses();

    try {
      m_Experimenter = new MultiExperimenter();
      final BaseFrame frame = new BaseFrame("Multi-Experimenter");
      frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
      frame.getContentPane().setLayout(new BorderLayout());
      frame.getContentPane().add(m_Experimenter, BorderLayout.CENTER);
      frame.pack();
      frame.setSize(GUIHelper.getDefaultLargeDialogDimension());
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      Thread memMonitor = new Thread() {
        @Override
        public void run() {
          while (true) {
            if (m_Memory.isOutOfMemory()) {
              // clean up
              frame.dispose();
              m_Experimenter = null;
              System.gc();

              // display error
              System.err.println("\ndisplayed message:");
              m_Memory.showOutOfMemory();
              System.err.println("\nexiting");
              System.exit(-1);
            }
          }
        }
      };

      memMonitor.setPriority(Thread.MAX_PRIORITY);
      memMonitor.start();
    } 
    catch (Exception ex) {
      Logger.log(Level.SEVERE, Utils.throwableToString(ex));
      System.err.println("An Exception occurred: ");
      ex.printStackTrace();
    }
  }
  
  /**
   * Starts the experimenter environment.
   * 
   * @param args	the command-line arguments: [-env classname] [initial dataset]
   */
  public static void main(String[] args) {
    runExperimenter(args);
  }
}
