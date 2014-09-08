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

import weka.experiment.Experiment;
import weka.gui.ConverterFileChooser;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.TitleGenerator;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;

import com.googlecode.jfilechooserbookmarks.core.Utils;

/**
 * The Experimenter panel.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExperimenterPanel
  extends BasePanel
  implements MenuBarProvider {

  /** for serialization. */
  private static final long serialVersionUID = 7314544066929763500L;

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "ExperimenterSession.props";

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "load recent" submenu. */
  protected JMenu m_MenuItemLoadRecent;

  /** the save menu item. */
  protected JMenuItem m_MenuItemFileSave;

  /** the save as menu item. */
  protected JMenuItem m_MenuItemFileSaveAs;
  
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
  
  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_RecentFilesHandler = null;
    m_TitleGenerator     = new TitleGenerator("Experimenter", true);
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
   * Lets the user choose a file.
   */
  public void open() {
    int 		retVal;
    BaseFileChooser	filechooser;
    
    filechooser = m_PanelSetup.getExperimentIO().getFileChooser();
    retVal      = filechooser.showOpenDialog(this);
    if (retVal != ConverterFileChooser.APPROVE_OPTION)
      return;

    if (m_RecentFilesHandler != null)
      m_RecentFilesHandler.addRecentItem(filechooser.getSelectedFile());
    open(filechooser.getSelectedFile());
    update();
  }
  
  /**
   * For opening an external file.
   * 
   * @param file	the file to open
   */
  public void open(File file) {
    Experiment	exp;
    String	msg;
    
    logMessage("Loading " + file + "...");
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
      logMessage("Loaded " + file);
    }
    else {
      logError("Cannot handle experiment stored in " + file + "!\n" + msg, "Load experiment");
    }
  }
  
  /**
   * For opening a recently used file.
   * 
   * @param e		the event
   */
  public void openRecent(RecentItemEvent<JMenu,File> e) {
    open(e.getItem());
  }

  /**
   * Allows the user to save the file. Prompts user with dialog if no filename
   * set currently.
   */
  public void save() {
    if (m_CurrentFile == null) {
      saveAs();
      return;
    }
    
    save(m_CurrentFile);
  }

  /**
   * Saves the experiment to the specified file.
   */
  public void save(File file) {
    try {
      logMessage("Saving experiment to " + file);
      Experiment.write(file.getAbsolutePath(), getExperiment());
      m_PanelSetup.setModified(false);
      m_CurrentFile = file;
      if (m_RecentFilesHandler != null)
	m_RecentFilesHandler.addRecentItem(m_CurrentFile);
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
  public void saveAs() {
    BaseFileChooser	filechooser;
    int			retVal;
    
    filechooser = m_PanelSetup.getExperimentIO().getFileChooser();
    retVal      = filechooser.showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    save(filechooser.getSelectedFile());
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
	  open();
	}
      });

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<JMenu>(SESSION_FILE, 10, submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  openRecent(e);
	}
      });
      m_MenuItemLoadRecent = submenu;

      // File/Save
      menuitem = new JMenuItem("Save");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('S');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
      menuitem.setIcon(GUIHelper.getIcon("save.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  save();
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
	  saveAs();
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
   * Updates the title of the dialog.
   */
  protected void updateTitle() {
    String	title;

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
   * Logs the message.
   * 
   * @param msg		the log message
   */
  public void logMessage(String msg) {
    m_PanelLog.append(msg);
  }
  
  /**
   * Logs the error message and also displays an error dialog.
   * 
   * @param msg		the error message
   */
  public void logError(String msg, String title) {
    m_PanelLog.append(msg);
    GUIHelper.showErrorMessage(this,
	msg,
	title);
  }
}
