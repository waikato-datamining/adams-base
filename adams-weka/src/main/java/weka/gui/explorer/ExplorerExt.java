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
 * ExplorerExt.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.explorer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSink;
import weka.core.logging.Logger;
import weka.core.logging.Logger.Level;
import weka.gui.AdamsHelper;
import weka.gui.ConverterFileChooser;
import weka.gui.LookAndFeel;
import weka.gui.explorer.panels.AbstractAdditionalExplorerPanel;
import weka.gui.explorer.panels.AdditionalExplorerPanel;
import weka.gui.sql.SqlViewerDialog;
import adams.core.Range;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.conversion.WekaInstancesToSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import adams.gui.core.BaseFrame;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.TitleGenerator;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.visualization.instance.InstanceContainerManager;
import adams.gui.visualization.instance.InstancePanel;

/**
 * An extended Explorer interface using menus instead of buttons, as well
 * as remembering recent files.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExplorerExt
  extends Explorer
  implements MenuBarProvider, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 173388459172775839L;

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "ExplorerSession.props";

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "load recent" submenu. */
  protected JMenu m_MenuItemLoadRecent;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /** the save menu item. */
  protected JMenuItem m_MenuItemFileSave;

  /** the save as menu item. */
  protected JMenuItem m_MenuItemFileSaveAs;

  /** the load classifier menu item. */
  protected JMenuItem m_MenuItemFileLoadClassifier;

  /** the load clusterer menu item. */
  protected JMenuItem m_MenuItemFileLoadClusterer;

  /** the undo menu item. */
  protected JMenuItem m_MenuItemEditUndo;

  /** the edit data menu item. */
  protected JMenuItem m_MenuItemEditData;

  /** the view instance explorer menu item. */
  protected JMenuItem m_MenuItemViewInstanceExplorer;

  /** the undo button of the preprocess panel. */
  protected JButton m_ButtonUndo;

  /** the edit button of the preprocess panel. */
  protected JButton m_ButtonEdit;

  /** The file chooser for selecting data files */
  protected ConverterFileChooser m_FileChooser;
  
  /** the current file. */
  protected File m_CurrentFile;

  /** for generating the title. */
  protected TitleGenerator m_TitleGenerator;
  
  /**
   * Default constructor.
   */
  public ExplorerExt() {
    super();
    initialize();
    initGUI();
  }
  
  /**
   * Initializes the members.
   */
  protected void initialize() {
    m_RecentFilesHandler = null;
    m_FileChooser        = null;
    m_ButtonUndo         = null;
    m_ButtonEdit         = null;
    m_CurrentFile        = null;
    m_TitleGenerator     = new TitleGenerator("Explorer", true);
  }
  
  /**
   * Initializes the widgets.
   */
  protected void initGUI() {
    String[]				classnames;
    AdditionalExplorerPanel		additional;
    ExplorerPanel			panel;
    AbstractExplorerPanelHandler	handler;
    
    hideButtons(getPreprocessPanel());
    m_FileChooser = getPreprocessPanel().m_FileChooser;
    AdamsHelper.updateFileChooserAccessory(m_FileChooser);
    
    classnames = AbstractAdditionalExplorerPanel.getPanels();
    for (String classname: classnames) {
      try {
	additional = (AdditionalExplorerPanel) Class.forName(classname).newInstance();
	panel      = additional.getExplorerPanel();
	handler    = additional.getExplorerPanelHandler();
	m_Panels.add(panel);
	if (panel instanceof LogHandler)
	  ((LogHandler) panel).setLog(m_LogPanel);
	m_TabbedPane.addTab(panel.getTabTitle(), null, (JPanel) panel, panel.getTabTitleToolTip());
	// register handler
	WorkspaceHelper.registerAdditionalHandler(panel.getClass(), handler);
      }
      catch (Exception e) {
	System.err.println("Failed to process additional explorer panel: " + classname);
	e.printStackTrace();
      }
    }
  }
  
  /**
   * Hides the buttons of the preprocess panel.
   * 
   * @param cont	the container to search
   * @return		true if hidden
   */
  protected boolean hideButtons(Container cont) {
    boolean	result;
    int		i;
    
    result = false;
    
    for (i = 0; i < cont.getComponentCount(); i++) {
      if (cont.getComponent(i) instanceof JButton) {
	if (((JButton) cont.getComponent(i)).getText().equals("Edit...")) {
	  m_ButtonEdit = (JButton) cont.getComponent(i);
	  cont.setVisible(false);
	  result = true;
	}
	else if (((JButton) cont.getComponent(i)).getText().equals("Undo")) {
	  m_ButtonUndo = (JButton) cont.getComponent(i);
	}
      }
      else if (cont.getComponent(i) instanceof Container) {
	result = hideButtons((Container) cont.getComponent(i));
	if (result)
	  break;
      }
    }
    
    return result;
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

      // File/Load from URL
      menuitem = new JMenuItem("Load from URL...");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('U');
      menuitem.setIcon(GUIHelper.getIcon("internet.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  loadFromURL();
	}
      });

      // File/Load from database
      menuitem = new JMenuItem("Load from database...");
      menu.add(menuitem);
      menuitem.setMnemonic('L');
      menuitem.setIcon(GUIHelper.getIcon("database.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  loadFromDatabase();
	}
      });

      // File/Generate
      menuitem = new JMenuItem("Generate...");
      menu.add(menuitem);
      menuitem.setMnemonic('G');
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  generate();
	}
      });

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

      // File/Load classifier
      menuitem = new JMenuItem("Load classifier model...");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('c');
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  loadClassifier();
	}
      });
      m_MenuItemFileLoadClassifier = menuitem;

      // File/Load classifier
      menuitem = new JMenuItem("Load clusterer model...");
      menu.add(menuitem);
      menuitem.setMnemonic('l');
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  loadClusterer();
	}
      });
      m_MenuItemFileLoadClusterer = menuitem;

      // File/Send to
      menu.addSeparator();
      SendToActionUtils.addSendToSubmenu(this, menu);

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

      // Edit
      menu = new JMenu("Edit");
      menu.setMnemonic('E');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });
      result.add(menu);

      // Edit/Undo
      menuitem = new JMenuItem("Undo");
      menuitem.setMnemonic('U');
      menuitem.setEnabled(canUndo());
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Z"));
      menuitem.setIcon(GUIHelper.getIcon("undo.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  undo();
	}
      });
      menu.add(menuitem);
      m_MenuItemEditUndo = menuitem;

      // Edit/Data editor
      menuitem = new JMenuItem("Data editor");
      menuitem.setMnemonic('D');
      menuitem.setEnabled(canUndo());
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed E"));
      menuitem.setIcon(GUIHelper.getIcon("report.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  edit();
	}
      });
      menu.addSeparator();
      menu.add(menuitem);
      m_MenuItemEditData = menuitem;

      // View
      menu = new JMenu("View");
      menu.setMnemonic('V');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });
      result.add(menu);

      // View/Instance explorer
      menuitem = new JMenuItem("Instance Explorer");
      menuitem.setMnemonic('I');
      menuitem.setEnabled(canUndo());
      menuitem.setIcon(GUIHelper.getIcon("chart.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  showInstanceExplorer();
	}
      });
      menu.add(menuitem);
      m_MenuItemViewInstanceExplorer = menuitem;

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
  protected void update() {
    updateTitle();
    updateMenu();
  }

  /**
   * Updates the title of the dialog.
   */
  protected void updateTitle() {
    String	title;

    title = m_TitleGenerator.generate(m_CurrentFile);
    setParentTitle(title);
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;
    
    // file
    m_MenuItemFileSave.setEnabled(isDataLoaded() && (m_CurrentFile != null));
    m_MenuItemFileSaveAs.setEnabled(isDataLoaded());
    m_MenuItemLoadRecent.setEnabled(m_RecentFilesHandler.size() > 0);
    m_MenuItemFileLoadClassifier.setEnabled(isDataLoaded() && hasClassifyTab());
    m_MenuItemFileLoadClusterer.setEnabled(isDataLoaded() && hasClusterTab());
    
    // edit
    m_MenuItemEditUndo.setEnabled(canUndo());
    m_MenuItemEditData.setEnabled(canEdit());
    
    // view
    m_MenuItemViewInstanceExplorer.setEnabled(isDataLoaded());
  }
  
  /**
   * Returns the hashcode of the current dataset.
   * 
   * @return	the hashcode, null if no data loaded
   */
  protected Integer getDataHashcode() {
    Integer	result;
    
    result = null;
    
    if (getPreprocessPanel().getInstances() != null)
      result = getPreprocessPanel().getInstances().hashCode();
    
    return result;
  }
  
  /**
   * Lets the user choose a file.
   */
  public void open() {
    int 	retVal;
    
    retVal = m_FileChooser.showOpenDialog(ExplorerExt.this);
    if (retVal != ConverterFileChooser.APPROVE_OPTION)
      return;
    
    try {
      getPreprocessPanel().addUndoPoint();
    }
    catch (Exception ignored) {
      // ignored
    }

    if (m_FileChooser.getLoader() == null) {
      GUIHelper.showErrorMessage(ExplorerExt.this,
	  "Cannot determine file loader automatically!",
	  "Load Instances");
      return;
    }
    else {
      if (m_RecentFilesHandler != null)
	m_RecentFilesHandler.addRecentItem(m_FileChooser.getSelectedFile());
      getPreprocessPanel().setInstancesFromFile(m_FileChooser.getLoader());
      m_CurrentFile = m_FileChooser.getSelectedFile();
      update();
    }
  }
  
  /**
   * For opening an external file.
   * 
   * @param file	the file to open
   */
  public void open(File file) {
    AbstractFileLoader 	loader;
    
    loader = ConverterUtils.getLoaderForFile(file);
    if (loader == null) {
      GUIHelper.showErrorMessage(ExplorerExt.this, "Failed to determine file loader for the following file:\n" + file);
      return;
    }
    
    try {
      loader.setFile(file);
      getPreprocessPanel().setInstancesFromFile(loader);
      m_CurrentFile = file;
      update();
    }
    catch (Exception ex) {
      System.err.println("Failed to load file:\n" + file);
      ex.printStackTrace();
    }
  }
  
  /**
   * For opening a recently used file.
   * 
   * @param e		the event
   */
  public void openRecent(RecentItemEvent<JMenu,File> e) {
    AbstractFileLoader 	loader;
    
    loader = ConverterUtils.getLoaderForFile(e.getItem());
    if (loader == null) {
      GUIHelper.showErrorMessage(ExplorerExt.this, "Failed to determine file loader for the following file:\n" + e.getItem());
      return;
    }
    
    try {
      loader.setFile(e.getItem());
      getPreprocessPanel().setInstancesFromFile(loader);
      m_CurrentFile = e.getItem();
      update();
    }
    catch (Exception ex) {
      System.err.println("Failed to load file:\n" + e.getItem());
      ex.printStackTrace();
    }
  }
  
  /**
   * Lets the user load data from a database.
   */
  public void loadFromDatabase() {
    SqlViewerDialog 	dialog;
    
    dialog = new SqlViewerDialog(null);
    dialog.setVisible(true);
    if (dialog.getReturnValue() == JOptionPane.OK_OPTION) {
      getPreprocessPanel().setInstancesFromDBQ(dialog.getURL(), dialog.getUser(),
	  dialog.getPassword(), dialog.getQuery(),
	  dialog.getGenerateSparseData());
      m_CurrentFile = null;
      update();
    }
  }

  /**
   * Lets the user load data from a URL.
   */
  public void loadFromURL() {
    getPreprocessPanel().setInstancesFromURLQ();
    m_CurrentFile = null;
    update();
  }
  
  /**
   * Pops up a dialog that allows the user to generate data.
   */
  public void generate() {
    getPreprocessPanel().generateInstances();
    m_CurrentFile = null;
    update();
  }

  /**
   * Allows the user to save the file. Prompts user with dialog if no filename
   * set currently.
   */
  public void save() {
    AbstractFileSaver	saver;

    if (m_CurrentFile == null) {
      saveAs();
      return;
    }

    saver = ConverterUtils.getSaverForFile(m_CurrentFile);
    if (saver == null) {
      saveAs();
      return;
    }
    else {
      try {
	saver.setFile(m_CurrentFile);
      }
      catch (Exception e) {
	System.err.println("Failed to save data to file '" + m_CurrentFile + "':\n" + e);
	e.printStackTrace();
      }
    }
    
    getPreprocessPanel().saveInstancesToFile(saver, getPreprocessPanel().getInstances());
  }

  /**
   * Allows the user to save the file. Prompts user with dialog.
   */
  public void saveAs() {
    getPreprocessPanel().saveWorkingInstancesToFileQ();
    m_CurrentFile = m_FileChooser.getSelectedFile();
    if (m_RecentFilesHandler != null)
      m_RecentFilesHandler.addRecentItem(m_CurrentFile);
  }

  /**
   * Returns the classify tab, if available.
   * 
   * @return		the tab, null if not available
   */
  public ClassifierPanel getClassifyTab() {
    ClassifierPanel	result;
    
    result = null;
    
    for (ExplorerPanel panel: getPanels()) {
      if (panel instanceof ClassifierPanel) {
	result = (ClassifierPanel) panel;
	break;
      }
    }
    
    return result;
  }
  
  /**
   * Returns whether the classify tab is present.
   * 
   * @return		true if available
   */
  public boolean hasClassifyTab() {
    return (getClassifyTab() != null);
  }
  
  /**
   * Loads a classifier in the classify tab.
   */
  public void loadClassifier() {
    if (!hasClassifyTab() || !isDataLoaded())
      return;
    getClassifyTab().loadClassifier();
  }

  /**
   * Returns the cluster tab, if available.
   * 
   * @return		the tab, null if not available
   */
  public ClassifierPanel getClusterTab() {
    ClassifierPanel	result;
    
    result = null;
    
    for (ExplorerPanel panel: getPanels()) {
      if (panel instanceof ClassifierPanel) {
	result = (ClassifierPanel) panel;
	break;
      }
    }
    
    return result;
  }
  
  /**
   * Returns whether the cluster tab is present.
   * 
   * @return		true if available
   */
  public boolean hasClusterTab() {
    return (getClusterTab() != null);
  }

  /**
   * Loads a clusterer in the cluster tab.
   */
  public void loadClusterer() {
    if (!hasClusterTab() || !isDataLoaded())
      return;
    getClusterTab().loadClassifier();
  }

  /**
   * Closes the dialog.
   */
  public void close() {
    GUIHelper.closeParent(this);
  }
  
  /**
   * Checks whether data is currently loaded.
   * 
   * @return		true if data loaded
   */
  public boolean isDataLoaded() {
    return (getPreprocessPanel().getInstances() != null);
  }
  
  /**
   * Checks whether undo is possible.
   * 
   * @return		true if undo is possible
   */
  public boolean canUndo() {
    if (m_ButtonUndo == null)
      return false;
    return m_ButtonUndo.isEnabled();
  }
  
  /**
   * Performs an undo.
   */
  public void undo() {
    if ((m_ButtonUndo != null) && m_ButtonUndo.isEnabled())
      m_ButtonUndo.doClick();
  }
  
  /**
   * Checks whether editing the data is possible.
   * 
   * @return		true if edit is possible
   */
  public boolean canEdit() {
    if (m_ButtonEdit == null)
      return false;
    return m_ButtonEdit.isEnabled();
  }
  
  /**
   * Performs an undo.
   */
  public void edit() {
    if ((m_ButtonEdit != null) && m_ButtonEdit.isEnabled())
      m_ButtonEdit.doClick();
  }

  /**
   * Displays the data in the Instance Explorer.
   */
  public void showInstanceExplorer() {
    String				rangeStr;
    Range				range;
    int[]				indices;
    ApprovalDialog			dialog;
    InstancePanel			panel;
    Instances				data;
    InstanceContainerManager 		manager;
    adams.data.instance.Instance 	inst;
    
    if (!isDataLoaded())
      return;
    data = getPreprocessPanel().getInstances();
    
    rangeStr = JOptionPane.showInputDialog(this, "Enter range of rows to display:", Range.ALL);
    if (rangeStr == null)
      return;
    if (!Range.isValid(rangeStr, data.numInstances()))
      return;
    range = new Range(rangeStr);
    range.setMax(data.numInstances());
    indices = range.getIntIndices();
    
    if (GUIHelper.getParentDialog(this) != null)
      dialog = ApprovalDialog.getInformationDialog(GUIHelper.getParentDialog(this));
    else
      dialog = ApprovalDialog.getInformationDialog(GUIHelper.getParentFrame(this));
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.setModalityType(ModalityType.MODELESS);
    dialog.setTitle("Instance Explorer - " + data.relationName());
    panel = new InstancePanel();
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setSize(800, 600);
    dialog.setLocationRelativeTo(this);
    manager = panel.getContainerManager();
    manager.startUpdate();
    for (int index: indices) {
      inst = new adams.data.instance.Instance();
      inst.set(data.instance(index));
      inst.setID("" + (index+1));
      manager.add(manager.newContainer(inst));
    }
    manager.finishUpdate();
    dialog.setVisible(true);
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
   * Sets the new title for the parent.
   *
   * @param value	the title to use
   */
  protected void setParentTitle(String value) {
    if (GUIHelper.getParentDialog(this) != null)
      GUIHelper.getParentDialog(this).setTitle(value);
    else if (GUIHelper.getParentFrame(this) != null)
      GUIHelper.getParentFrame(this).setTitle(value);
  }

  /**
   * Only updates the current file member, does not load it.
   * 
   * @param value	the current file
   */
  protected void setCurrentFile(File value) {
    m_CurrentFile = value;
  }
  
  /**
   * Returns the current file.
   * 
   * @return		the current file, can be null
   */
  public File getCurrentFile() {
    return m_CurrentFile;
  }
  
  /**
   * Returns the file chooser in use.
   * 
   * @return		the file chooser
   */
  public ConverterFileChooser getFileChooser() {
    return m_FileChooser;
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  @Override
  public Class[] getSendToClasses() {
    return new Class[]{PlaceholderFile.class, SpreadSheet.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve the item for
   * @return		true if an object is available for sending
   */
  @Override
  public boolean hasSendToItem(Class[] cls) {
    return    SendToActionUtils.isAvailable(new Class[]{PlaceholderFile.class, SpreadSheet.class}, cls)
           && isDataLoaded();
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  @Override
  public Object getSendToItem(Class[] cls) {
    Object			result;
    String			msg;
    WekaInstancesToSpreadSheet	conv;

    result = null;

    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      result = SendToActionUtils.nextTmpFile("wekaexplorer", "arff");
      try {
	DataSink.write(((PlaceholderFile) result).getAbsolutePath(), getPreprocessPanel().getInstances());
      }
      catch (Exception e) {
	msg = "Failed to save current data to '" + result + "':";
	System.err.println(msg);
	e.printStackTrace();
	result = null;
	GUIHelper.showErrorMessage(this, msg + e);
      }
    }
    else if (SendToActionUtils.isAvailable(SpreadSheet.class, cls)) {
      conv = new WekaInstancesToSpreadSheet();
      conv.setInput(getPreprocessPanel().getInstances());
      msg = conv.convert();
      if (msg != null)
	GUIHelper.showErrorMessage(this, msg);
      else
	result = conv.getOutput();
      conv.cleanUp();
    }

    return result;
  }

  /**
   * variable for the Explorer class which would be set to null by the memory
   * monitoring thread to free up some memory if we running out of memory
   */
  private static ExplorerExt m_Explorer;

  /**
   * Runs an explorer instance. Also interpretes the "-env classname" parameter.
   * 
   * @param args	the command-line arguments
   */
  public static void runExplorer(String[] args) {
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
      m_Explorer = new ExplorerExt();
      final BaseFrame frame = new BaseFrame("Weka Explorer");
      frame.setJMenuBar(m_Explorer.getMenuBar());
      frame.getContentPane().setLayout(new BorderLayout());
      frame.getContentPane().add(m_Explorer, BorderLayout.CENTER);
      frame.pack();
      frame.setSize(800, 600);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      if (args.length == 1) {
        System.err.println("Loading instances from " + args[0]);
        AbstractFileLoader loader = ConverterUtils.getLoaderForFile(args[0]);
        loader.setFile(new File(args[0]));
        m_Explorer.m_PreprocessPanel.setInstancesFromFile(loader);
      }

      Thread memMonitor = new Thread() {
        @Override
        public void run() {
          while (true) {
            if (m_Memory.isOutOfMemory()) {
              // clean up
              frame.dispose();
              m_Explorer = null;
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
   * Starts the explorer environment.
   * 
   * @param args	the command-line arguments: [-env classname] [initial dataset]
   */
  public static void main(String[] args) {
    runExplorer(args);
  }
}
