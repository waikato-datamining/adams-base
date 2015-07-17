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
package weka.gui.experiment.ext;

import adams.core.Utils;
import adams.env.Environment;
import adams.gui.core.BaseFrame;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import weka.core.Memory;
import weka.core.logging.Logger;
import weka.core.logging.Logger.Level;
import weka.gui.LookAndFeel;

import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Extended interface for the WEKA Experimenter, allowing for an arbitrary
 * number of Experimenter panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8799 $
 */
public class MultiExperimenter
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -20320489406680254L;

  /** the default name for new panels. */
  public final static String DEFAULT_NAME = "Experiment";
  
  /** the split pane for the components. */
  protected JSplitPane m_SplitPane;

  /** the history panel. */
  protected ExperimenterEntryPanel m_History;

  /** the actual panel for displaying the other panels. */
  protected BasePanel m_PanelExperimenter;

  /** the history panel. */
  protected BasePanel m_PanelHistory;
  
  /** the panel for the buttons. */
  protected BasePanel m_PanelButtons;
  
  /** the button for adding a panel. */
  protected JButton m_ButtonAdd;
  
  /** the button for removing a panel. */
  protected JButton m_ButtonRemove;

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    int		height;
    
    super.initGUI();

    setLayout(new BorderLayout());
    
    m_SplitPane = new JSplitPane();
    add(m_SplitPane, BorderLayout.CENTER);

    // right
    m_PanelExperimenter = new BasePanel(new BorderLayout());
    m_PanelExperimenter.setMinimumSize(new Dimension(100, 0));
    m_SplitPane.setBottomComponent(m_PanelExperimenter);

    // left
    m_History = new ExperimenterEntryPanel();
    m_History.setPanel(m_PanelExperimenter);
    m_History.setAllowRename(true);
    m_PanelHistory = new BasePanel(new BorderLayout());
    m_PanelHistory.setMinimumSize(new Dimension(100, 0));
    m_PanelHistory.add(m_History, BorderLayout.CENTER);
    m_PanelButtons = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelHistory.add(m_PanelButtons, BorderLayout.SOUTH);
    m_SplitPane.setTopComponent(m_PanelHistory);

    // left buttons
    m_ButtonAdd = new JButton(GUIHelper.getIcon("add.gif"));
    height = m_ButtonAdd.getHeight();
    m_ButtonAdd.setSize(height, height);
    m_ButtonAdd.setToolTipText("Adds a new Experimenter panel");
    m_ButtonAdd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String initial = m_History.newEntryName(DEFAULT_NAME);
	String name = GUIHelper.showInputDialog(
	    MultiExperimenter.this, 
	    "Please enter the name for the Experimenter panel:", 
	    initial);
	if (name == null)
	  return;
	addPanel(new ExperimenterPanel(), name);
      }
    });
    m_PanelButtons.add(m_ButtonAdd);

    m_ButtonRemove = new JButton(GUIHelper.getIcon("remove.gif"));
    m_ButtonRemove.setSize(height, height);
    m_ButtonRemove.setToolTipText("Removes all selected Experimenter panels");
    m_ButtonRemove.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	int[] indices = m_History.getSelectedIndices();
	for (int i = indices.length - 1; i >= 0; i--)
	  removePanel(indices[i]);
      }
    });
    m_PanelButtons.add(m_ButtonRemove);
    
    m_SplitPane.setOneTouchExpandable(true);
    m_SplitPane.setResizeWeight(0);
    m_SplitPane.setDividerLocation(250);
  }

  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    Runnable	run;
    
    super.finishInit();
    
    run = new Runnable() {
      @Override
      public void run() {
	addPanel(new ExperimenterPanel(), DEFAULT_NAME);
      }
    };
    SwingUtilities.invokeLater(run);
  }
  
  /**
   * Removes all panels.
   */
  public void clear() {
    Runnable	run;
    
    run = new Runnable() {
      @Override
      public void run() {
	m_History.clear();
	m_PanelExperimenter.removeAll();
      }
    };
    
    SwingUtilities.invokeLater(run);
  }

  /**
   * Returns the number of experimenter panels.
   *
   * @return		the number of panels
   */
  public int count() {
    return m_History.count();
  }

  /**
   * Returns the underlying history panel.
   *
   * @return		the panel
   */
  public ExperimenterEntryPanel getHistory() {
    return m_History;
  }
  
  /**
   * Adds the given experimenter panel.
   *
   * @param panel	the panel to add
   * @param name	the name for the panel
   */
  public synchronized void addPanel(ExperimenterPanel panel, String name) {
    m_History.addEntry(m_History.newEntryName(name), panel);
    m_History.setSelectedIndex(count() - 1);
  }
  
  /**
   * Removes the panel with the given name.
   * 
   * @param name	the name of the panel to remove
   * @return		true if successfully removed
   */
  public synchronized boolean removePanel(String name) {
    boolean	result;
    int		index;
    
    result = false;
    if (!m_History.hasEntry(name))
      return result;
    index  = m_History.indexOfEntry(name);
    result = (m_History.removeEntry(name) != null);
    
    if (m_History.count() > 0) {
      if (m_History.count() <= index)
	index--;
      m_History.updateEntry(m_History.getEntryName(index));
    }
    
    return result;
  }
  
  /**
   * Removes the panel at the specified index.
   * 
   * @param index	the index of the panel to remove
   * @return		true if successfully removed
   */
  public synchronized boolean removePanel(int index) {
    return removePanel(m_History.getEntryName(index));
  }
  
  /**
   * Returns the panel with the specified name.
   * 
   * @param name	the name of the panel to retrieve
   * @return		the panel, null if not found
   */
  public ExperimenterPanel getPanel(String name) {
    return m_History.getEntry(name);
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
    Runnable	run;
    
    for (final File file: files) {
      name = file.getName();
      if (name.lastIndexOf('.') > -1)
	name = name.substring(0, name.lastIndexOf('.'));
      final ExperimenterPanel panel = new ExperimenterPanel();
      addPanel(panel, name);
      run = new Runnable() {
	@Override
	public void run() {
	  panel.openSetup(file);
	}
      };
      SwingUtilities.invokeLater(run);
    }
  }

  /**
   * Returns the panel with the experimenter panel entries.
   * 
   * @return		the panel entries
   */
  public ExperimenterEntryPanel getEntryPanel() {
    return m_History;
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
    String env = "";
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
      frame.setSize(1000, 800);
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
