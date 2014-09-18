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
 * ExperimenterPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.experiment.ext;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ConverterUtils;
import weka.experiment.Experiment;
import weka.gui.ConverterFileChooser;
import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.Utils;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ConsolePanel.OutputType;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.TitleGenerator;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;

/**
 * The Experimenter panel.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExperimenterPanel
  extends BasePanel
  implements MenuBarProvider, StatusMessageHandler {

  /** for serialization. */
  private static final long serialVersionUID = 7314544066929763500L;

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "ExperimenterExtSession.props";

  /** the name of the props file with the general properties. */
  public final static String FILENAME = "weka/gui/experiment/ext/ExperimenterExt.props";

  /** the properties. */
  protected static Properties m_Properties;

  /** the recent files handler for setups. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandlerSetups;

  /** the recent files handler for results. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandlerResults;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "load recent" submenu. */
  protected JMenu m_MenuItemFileLoadRecent;

  /** the save menu item. */
  protected JMenuItem m_MenuItemFileSave;

  /** the save as menu item. */
  protected JMenuItem m_MenuItemFileSaveAs;

  /** the start menu item. */
  protected JMenuItem m_MenuItemExecutionStart;

  /** the stop menu item. */
  protected JMenuItem m_MenuItemExecutionStop;

  /** the "load recent" submenu. */
  protected JMenu m_MenuItemResultsLoadRecent;

  /** the save results menu item. */
  protected JMenuItem m_MenuItemResultsSave;

  /** the status bar. */
  protected BaseStatusBar m_StatusBar;
  
  /** the current file. */
  protected File m_CurrentFile;

  /** for generating the title. */
  protected TitleGenerator m_TitleGenerator;
  
  /** the tabbed pane. */
  protected BaseTabbedPane m_TabbedPane;
  
  /** the current setup panel. */
  protected AbstractSetupPanel m_PanelSetup;

  /** the analysis panel. */
  protected AnalysisPanel m_PanelAnalysis;

  /** the log panel. */
  protected LogPanel m_PanelLog;
  
  /** the current experiment. */
  protected Experiment m_Experiment;
  
  /** the filechooser for loading/saving results. */
  protected ConverterFileChooser m_FileChooserResults;
  
  /** the runner thread. */
  protected AbstractExperimentRunner m_Runner;
  
  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_RecentFilesHandlerSetups  = null;
    m_RecentFilesHandlerResults = null;
    m_TitleGenerator            = new TitleGenerator("Experimenter", true);
    m_Experiment                = null;
    m_FileChooserResults        = new ConverterFileChooser();
    m_Runner                    = null;
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    m_TabbedPane = new BaseTabbedPane();
    add(m_TabbedPane, BorderLayout.CENTER);
    
    m_PanelSetup = new BasicSetupPanel();
    m_PanelSetup.setOwner(this);
    m_TabbedPane.addTab("Setup", m_PanelSetup);
    
    m_PanelAnalysis = new AnalysisPanel();
    m_PanelAnalysis.setOwner(this);
    m_TabbedPane.addTab("Analysis", m_PanelAnalysis);
    
    m_PanelLog = new LogPanel();
    m_PanelLog.setOwner(this);
    m_TabbedPane.addTab("Log", m_PanelLog);
    
    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);
  }
  
  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    update();
  }
  
  /**
   * Closes the dialog.
   */
  public void close() {
    GUIHelper.closeParent(this);
  }

  /**
   * Sets the new setup panel.
   * 
   * @param setup	the new setup panel
   */
  public void newSetup(AbstractSetupPanel setup) {
    AbstractSetupPanel	current;
    
    current = (AbstractSetupPanel) m_TabbedPane.getComponentAt(0);
    current.setOwner(null);
    
    setup.setOwner(this);
    m_TabbedPane.setComponentAt(0, setup);
    
    logMessage("New setup: " + current.getClass().getName());
  }

  /**
   * Lets the user choose an experiment file.
   */
  public void openSetup() {
    int 		retVal;
    BaseFileChooser	filechooser;
    
    filechooser = m_PanelSetup.getExperimentIO().getFileChooser();
    retVal      = filechooser.showOpenDialog(this);
    if (retVal != ConverterFileChooser.APPROVE_OPTION)
      return;

    if (m_RecentFilesHandlerSetups != null)
      m_RecentFilesHandlerSetups.addRecentItem(filechooser.getSelectedFile());
    openSetup(filechooser.getSelectedFile());
    update();
  }
  
  /**
   * For opening an experiment file.
   * 
   * @param file	the file to open
   */
  public void openSetup(File file) {
    Experiment	exp;
    String	msg;
    
    logMessage("Loading setup from " + file + "...");
    exp = m_PanelSetup.getExperimentIO().load(file);
    if (exp == null)
      msg = "Failed to load experiment";
    else
      msg = m_PanelSetup.handlesExperiment(exp);
    if (msg == null) {
      m_PanelSetup.setExperiment(exp);
      m_PanelSetup.setModified(false);
      m_CurrentFile = file;
      update();
      logMessage("Loaded setup from " + file);
    }
    else {
      logError("Cannot handle experiment stored in " + file + "!\n" + msg, "Load experiment");
    }
  }
  
  /**
   * For opening a recently used experiment file.
   * 
   * @param e		the event
   */
  public void openRecentSetup(RecentItemEvent<JMenu,File> e) {
    openSetup(e.getItem());
  }

  /**
   * Allows the user to save the file. Prompts user with dialog if no filename
   * set currently.
   */
  public void saveSetup() {
    if (m_CurrentFile == null) {
      saveSetupAs();
      return;
    }
    
    saveSetup(m_CurrentFile);
  }

  /**
   * Saves the experiment to the specified file.
   */
  public void saveSetup(File file) {
    try {
      logMessage("Saving experiment to " + file);
      Experiment.write(file.getAbsolutePath(), getExperiment());
      m_PanelSetup.setModified(false);
      m_CurrentFile = file;
      if (m_RecentFilesHandlerSetups != null)
	m_RecentFilesHandlerSetups.addRecentItem(m_CurrentFile);
      update();
      logMessage("Saved experiment to " + file);
    }
    catch (Exception e) {
      logError("Failed to save experiment to " + file + "!\n" + Utils.throwableToString(e), "Save experiment");
    }
  }

  /**
   * Allows the user to save the file. Prompts user with dialog.
   */
  public void saveSetupAs() {
    BaseFileChooser	filechooser;
    int			retVal;
    
    filechooser = m_PanelSetup.getExperimentIO().getFileChooser();
    retVal      = filechooser.showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    saveSetup(filechooser.getSelectedFile());
  }

  /**
   * Returns whether an experiment is currently being executed.
   * 
   * @return		true if an experiment is running
   */
  public boolean isExecuting() {
    return (m_Runner != null);
  }
  
  /**
   * Starts the execution.
   */
  public void startExecution() {
    
    try {
      m_Runner = m_PanelSetup.getExperimentIO().createRunner(this);
    }
    catch (Exception e) {
      logError("Failed to run experiment: " + Utils.throwableToString(e), "Execution error");
      return;
    }
    m_Runner.start();
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Runner.abortExperiment();
    update();
  }

  /**
   * Stops the execution.
   */
  public void finishExecution() {
    m_Runner = null;
    update();
  }

  /**
   * Loads the results from a file.
   */
  public void openResults() {
    int 	retVal;
    
    retVal = m_FileChooserResults.showOpenDialog(this);
    if (retVal != ConverterFileChooser.APPROVE_OPTION)
      return;

    if (m_RecentFilesHandlerResults != null)
      m_RecentFilesHandlerSetups.addRecentItem(m_FileChooserResults.getSelectedFile());
    openResults(m_FileChooserResults.getSelectedFile());
    update();
  }
  
  /**
   * For opening a recently used results file.
   * 
   * @param e		the event
   */
  public void openRecentResults(RecentItemEvent<JMenu,File> e) {
    openResults(e.getItem());
  }

  /**
   * Loads the results from the file.
   * 
   * @param file	the file to load the results from
   */
  public void openResults(File file) {
    AbstractFileLoader 	loader;
    Instances		results;
    String		msg;
    
    logMessage("Loading results " + file + "...");
    loader = ConverterUtils.getLoaderForFile(file);
    if (loader == null) {
      logError("Failed to determine file loader for the following file:\n" + file, "Loading results");
      return;
    }
    
    try {
      loader.setFile(file);
      results = loader.getDataSet();
      msg     = m_PanelAnalysis.handlesResults(results);
      if (msg == null)
	m_PanelAnalysis.setResults(results);
      else
	logError("Cannot handle results from " + file + "\n" + msg, "Loading results");
    }
    catch (Exception e) {
      msg = "Failed to load results from " + file + "\n" + Utils.throwableToString(e);
      logError("Cannot handle results from " + file + "\n" + msg, "Loading results");
    }
  }

  /**
   * Loads the results from a database.
   */
  public void openResultsDB() {
    // TODO
  }

  /**
   * Saves the results to a file.
   */
  public void saveResults() {
    int		retVal;
    
    retVal = m_FileChooserResults.showSaveDialog(this);
    if (retVal != ConverterFileChooser.APPROVE_OPTION)
      return;
    
    saveResults(m_FileChooserResults.getSelectedFile(), m_FileChooserResults.getSaver());
  }

  /**
   * Saves the results to the file.
   * 
   * @param file	the file to save the results to
   */
  public void saveResults(File file) {
    AbstractFileSaver	saver;

    saver = ConverterUtils.getSaverForFile(file);
    if (saver == null)
      logError("Failed to determine file saver for " + file, "Saving results");
    else
      saveResults(file, saver);
  }

  /**
   * Saves the results to the file.
   * 
   * @param file	the file to save the results to
   * @param saver	the saver to use
   */
  protected void saveResults(File file, AbstractFileSaver saver) {
    try {
      logMessage("Saving results to " + file + "...");
      saver.setInstances(m_PanelAnalysis.getResults());
      saver.writeBatch();
      logMessage("Results saved to " + file);
      if (m_RecentFilesHandlerResults != null)
	m_RecentFilesHandlerResults.addRecentItem(file);
    }
    catch (Exception e) {
      logError("Failed to save results to " + file + "\n" + Utils.throwableToString(e), "Saving results");
    }
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
    JMenu		submenu;
    JMenuItem		menuitem;
    String[]		classes;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // File/New
      submenu = new JMenu("New");
      menu.add(submenu);
      submenu.setMnemonic('N');
      classes = AbstractSetupPanel.getPanels();
      for (String cls: classes) {
	try {
	  final AbstractSetupPanel setup = (AbstractSetupPanel) Class.forName(cls).newInstance();
	  menuitem = new JMenuItem(setup.getSetupName());
	  if (setup instanceof BasicSetupPanel) {
	    menuitem.setIcon(GUIHelper.getIcon("new.gif"));
	    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
	  }
	  menuitem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      newSetup(setup);
	    }
	  });
	  submenu.add(menuitem);
	}
	catch (Exception e) {
	  logError("Failed to instantiate experiment: " + cls + "\n" + Utils.throwableToString(e), "New experiment");
	}
      }

      // File/Open
      menuitem = new JMenuItem("Open...");
      menu.add(menuitem);
      menuitem.setMnemonic('o');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.setIcon(GUIHelper.getIcon("open.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  openSetup();
	}
      });

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandlerSetups = new RecentFilesHandler<JMenu>(
	  SESSION_FILE, "Setup-", ExperimenterPanel.getProperties().getInteger("SetupsMaxRecent", 5), submenu);
      m_RecentFilesHandlerSetups.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  openRecentSetup(e);
	}
      });
      m_MenuItemFileLoadRecent = submenu;

      // File/Save
      menuitem = new JMenuItem("Save");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('S');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
      menuitem.setIcon(GUIHelper.getIcon("save.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  saveSetup();
	}
      });
      m_MenuItemFileSave = menuitem;

      // File/Save
      menuitem = new JMenuItem("Save as...");
      menu.add(menuitem);
      menuitem.setMnemonic('a');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed S"));
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  saveSetupAs();
	}
      });
      m_MenuItemFileSaveAs = menuitem;

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  close();
	}
      });

      // Execution
      menu = new JMenu("Execution");
      result.add(menu);
      menu.setMnemonic('E');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // Execution/Start
      menuitem = new JMenuItem("Start");
      menu.add(menuitem);
      menuitem.setMnemonic('S');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed R"));
      menuitem.setIcon(GUIHelper.getIcon("run.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  startExecution();
	}
      });
      m_MenuItemExecutionStart = menuitem;

      // Execution/Start
      menuitem = new JMenuItem("Stop");
      menu.add(menuitem);
      menuitem.setMnemonic('p');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed K"));
      menuitem.setIcon(GUIHelper.getIcon("stop_blue.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  stopExecution();
	}
      });
      m_MenuItemExecutionStop = menuitem;

      // Analysis
      menu = new JMenu("Analysis");
      result.add(menu);
      menu.setMnemonic('A');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // Analysis/Open
      menuitem = new JMenuItem("Open...");
      menu.add(menuitem);
      menuitem.setMnemonic('O');
      menuitem.setIcon(GUIHelper.getIcon("open.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  openResults();
	}
      });

      // Analysis/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandlerResults = new RecentFilesHandler<JMenu>(
	  SESSION_FILE, "Results-", ExperimenterPanel.getProperties().getInteger("ResultsMaxRecent", 5), submenu);
      m_RecentFilesHandlerResults.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  openRecentResults(e);
	}
      });
      m_MenuItemResultsLoadRecent = submenu;

      // Analysis/Open DB
      menuitem = new JMenuItem("Open DB...");
      menu.add(menuitem);
      menuitem.setMnemonic('D');
      menuitem.setIcon(GUIHelper.getIcon("database.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  openResultsDB();
	}
      });

      // Analysis/Save
      menuitem = new JMenuItem("Save...");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('S');
      menuitem.setIcon(GUIHelper.getIcon("save.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  saveResults();
	}
      });
      m_MenuItemResultsSave = menuitem;

      // update menu
      m_MenuBar = result;
      updateMenu();
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Updates title and menu items.
   */
  public void update() {
    updateTitle();
    updateMenu();
  }
  
  /**
   * Returns the title generator in use.
   * 
   * @return		the generator
   */
  public TitleGenerator getTitleGenerator() {
    return m_TitleGenerator;
  }

  /**
   * Updates the title of the dialog.
   */
  protected void updateTitle() {
    String	title;

    if (!m_TitleGenerator.isEnabled())
      return;
    
    title = m_TitleGenerator.generate(m_CurrentFile, m_PanelSetup.isModified());
    setParentTitle(title);
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;

    // File
    m_MenuItemFileSave.setEnabled(m_PanelSetup.isModified());
    
    // Execution
    m_MenuItemExecutionStart.setEnabled(!isExecuting());
    m_MenuItemExecutionStop.setEnabled(isExecuting());
    
    // Analysis
    m_MenuItemResultsSave.setEnabled(m_PanelAnalysis.hasResults());
  }
  
  /**
   * Sets the base title to use for the title generator.
   * 
   * @param value	the title to use
   * @see		#m_TitleGenerator
   */
  public void setTitle(String value) {
    m_TitleGenerator.setTitle(value);
    update();
  }
  
  /**
   * Returns the base title in use by the title generator.
   * 
   * @return		the title in use
   * @see		#m_TitleGenerator
   */
  public String getTitle() {
    return m_TitleGenerator.getTitle();
  }
  
  /**
   * Returns the current experiment.
   * 
   * @return		the experiment
   */
  public Experiment getExperiment() {
    return m_PanelSetup.getExperiment();
  }
  
  /**
   * Sets the experiment to use.
   * 
   * @param value	the experiment
   */
  public void setExperiment(Experiment value) {
    m_PanelSetup.setExperiment(value);
  }
  
  /**
   * Checks whether the experiment can be handled.
   * 
   * @param exp		the experiment to check
   * @return		true if can be handled
   */
  public String handlesExperiment(Experiment exp) {
    return m_PanelSetup.handlesExperiment(exp);
  }
  
  /**
   * Logs the exception with no dialog.
   * 
   * @param t		the exception
   */
  public void logMessage(Throwable t) {
    logMessage(Utils.throwableToString(t));
  }
  
  /**
   * Logs the message.
   * 
   * @param msg		the log message
   */
  public void logMessage(String msg) {
    m_PanelLog.append(msg);
  }
  
  /**
   * Logs the exception and also displays an error dialog.
   * 
   * @param t		the exception
   * @param title	the title for the dialog
   */
  public void logError(Throwable t, String title) {
    logError(Utils.throwableToString(t), title);
  }
  
  /**
   * Logs the error message and also displays an error dialog.
   * 
   * @param msg		the error message
   * @param title	the title for the dialog
   */
  public void logError(String msg, String title) {
    m_PanelLog.append(msg);
    GUIHelper.showErrorMessage(this,
	msg,
	title);
  }
  
  /**
   * Displays a message.
   * 
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    m_StatusBar.showStatus(msg);
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    String	msg;
    
    if (m_Properties == null) {
      try {
	m_Properties = Properties.read(FILENAME);
      }
      catch (Exception e) {
	msg = "Failed to load " + FILENAME + "\n" + Utils.throwableToString(e);
	ConsolePanel.getSingleton().append(OutputType.ERROR, msg);
	System.err.println(msg);
	m_Properties = new Properties();
      }
    }

    return m_Properties;
  }
}
