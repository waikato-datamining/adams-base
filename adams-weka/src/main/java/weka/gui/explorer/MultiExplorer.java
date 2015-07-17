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
 * MultiExplorer.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.explorer;

import adams.core.Utils;
import adams.env.Environment;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.AbstractNamedHistoryPanel.HistoryEntrySelectionEvent;
import adams.gui.core.AbstractNamedHistoryPanel.HistoryEntrySelectionListener;
import adams.gui.core.AbstractNamedHistoryPanel.PopupCustomizer;
import adams.gui.core.BaseFrame;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import weka.core.Memory;
import weka.core.logging.Logger;
import weka.core.logging.Logger.Level;
import weka.gui.LookAndFeel;

import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Extended interface for the WEKA Explorer, allowing for an arbitrary
 * number of Explorer panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiExplorer
  extends BasePanel
  implements PopupCustomizer {

  /** for serialization. */
  private static final long serialVersionUID = -20320489406680254L;

  /** the default name for new panels. */
  public final static String DEFAULT_NAME = "Session";
  
  /** the split pane for the components. */
  protected JSplitPane m_SplitPane;

  /** the history panel. */
  protected ExplorerEntryPanel m_History;

  /** the actual panel for displaying the other panels. */
  protected BasePanel m_PanelExplorer;

  /** the history panel. */
  protected BasePanel m_PanelHistory;
  
  /** the panel for the buttons. */
  protected BasePanel m_PanelButtons;
  
  /** the button for adding a panel. */
  protected JButton m_ButtonAdd;
  
  /** the button for copying a panel. */
  protected JButton m_ButtonCopy;

  /** the button for removing a panel. */
  protected JButton m_ButtonRemove;

  /** the button for managing the workspaces. */
  protected JButton m_ButtonWorkspace;
  
  /** the file chooser for the workspaces. */
  protected BaseFileChooser m_WorkspaceFileChooser;
  
  @Override
  protected void initialize() {
    super.initialize();
    
    m_WorkspaceFileChooser = WorkspaceHelper.newFileChooser();
  }
  
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
    m_PanelExplorer = new BasePanel(new BorderLayout());
    m_PanelExplorer.setMinimumSize(new Dimension(100, 0));
    m_SplitPane.setBottomComponent(m_PanelExplorer);

    // left
    m_History = new ExplorerEntryPanel();
    m_History.setPanel(m_PanelExplorer);
    m_History.setPopupCustomizer(this);
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
    m_ButtonAdd.setToolTipText("Adds a new Explorer panel");
    m_ButtonAdd.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String initial = m_History.newEntryName(DEFAULT_NAME);
	String name = GUIHelper.showInputDialog(
	    MultiExplorer.this, 
	    "Please enter the name for the Explorer panel:", 
	    initial);
	if (name == null)
	  return;
	addPanel(new ExplorerExt(), name);
      }
    });
    m_PanelButtons.add(m_ButtonAdd);

    m_ButtonCopy = new JButton(GUIHelper.getIcon("copy.gif"));
    m_ButtonCopy.setSize(height, height);
    m_ButtonCopy.setToolTipText("Creates a copy of the currently selected Explorer panel");
    m_ButtonCopy.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	String name = "Copy of " + m_History.getSelectedEntry();
	name = GUIHelper.showInputDialog(MultiExplorer.this, "Please enter new name:", name);
	if (name == null)
	  return;
	ExplorerExt oldExplorer = m_History.getEntry(m_History.getSelectedIndex());
        ExplorerExt newExplorer;
	name = m_History.newEntryName(name);
        try {
          newExplorer = WorkspaceHelper.copy(oldExplorer);
        }
        catch (Exception ex) {
          System.err.println("Failed to copy explorer instance, creating simple copy.");
          ex.printStackTrace();
          newExplorer = new ExplorerExt();
	  if (oldExplorer.getPreprocessPanel().getInstances() != null)
	    newExplorer.getPreprocessPanel().setInstances(oldExplorer.getPreprocessPanel().getInstances());
        }
	newExplorer.getFileChooser().setCurrentDirectory(oldExplorer.getFileChooser().getCurrentDirectory());
	newExplorer.setCurrentFile(oldExplorer.getCurrentFile());
        addPanel(newExplorer, name);
      }
    });
    m_PanelButtons.add(m_ButtonCopy);

    m_ButtonRemove = new JButton(GUIHelper.getIcon("remove.gif"));
    m_ButtonRemove.setSize(height, height);
    m_ButtonRemove.setToolTipText("Removes all selected Explorer panels");
    m_ButtonRemove.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	int[] indices = m_History.getSelectedIndices();
	for (int i = indices.length - 1; i >= 0; i--)
	  removePanel(indices[i]);
      }
    });
    m_PanelButtons.add(m_ButtonRemove);

    m_ButtonWorkspace = new JButton(GUIHelper.getIcon("workspace.png"));
    m_ButtonWorkspace.setSize(height, height);
    m_ButtonWorkspace.setToolTipText("Loading/saving of workspaces");
    m_ButtonWorkspace.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	JPopupMenu menu = new JPopupMenu();
	JMenuItem menuitem;
	
	// load workspace
	menuitem = new JMenuItem("Open...");
	menuitem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    openWorkspace();
	  }
	});
	menu.add(menuitem);

	// save workspace
	menuitem = new JMenuItem("Save as...");
	menuitem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    saveWorkspace();
	  }
	});
	menu.add(menuitem);
	
	// show menu
        menu.show(m_ButtonWorkspace, 0, m_ButtonWorkspace.getHeight());
      }
    });
    m_PanelButtons.add(m_ButtonWorkspace);
    
    m_SplitPane.setOneTouchExpandable(true);
    m_SplitPane.setResizeWeight(0);
    m_SplitPane.setDividerLocation(250);
    
    m_History.addHistoryEntrySelectionListener(new HistoryEntrySelectionListener() {
      @Override
      public void historyEntrySelected(HistoryEntrySelectionEvent e) {
	m_ButtonCopy.setEnabled(e.getNames().length == 1);
      }
    });
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
	addPanel(new ExplorerExt(), DEFAULT_NAME);
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
	m_PanelExplorer.removeAll();
      }
    };
    
    SwingUtilities.invokeLater(run);
  }

  /**
   * Returns the number of explorer panels.
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
  public ExplorerEntryPanel getHistory() {
    return m_History;
  }
  
  /**
   * Adds the given explorer panel.
   *
   * @param panel	the panel to add
   * @param name	the name for the panel
   */
  public synchronized void addPanel(ExplorerExt panel, String name) {
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
  public ExplorerExt getPanel(String name) {
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
      final ExplorerExt panel = new ExplorerExt();
      addPanel(panel, name);
      run = new Runnable() {
	@Override
	public void run() {
	  panel.open(file);
	}
      };
      SwingUtilities.invokeLater(run);
    }
  }

  /**
   * Gets called before the popup for the entries is displayed.
   *
   * @param entries	the selected entries
   * @param menu	the menu so far
   */
  @Override
  public void customizePopup(final String[] entries, JPopupMenu menu) {
    JMenuItem	menuitem;
    
    if (entries.length == 1) {
      menuitem = new JMenuItem("Rename...");
      menuitem.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          String newName = GUIHelper.showInputDialog(
              MultiExplorer.this, "Please enter the new name:", entries[0]);
          if (newName == null)
            return;
          if (entries[0].equals(newName))
            return;
          String msg = m_History.renameEntry(entries[0], newName);
          if (msg != null)
            GUIHelper.showErrorMessage(MultiExplorer.this, msg);
        }
      });
      menu.add(menuitem);
    }
  }
  
  /**
   * Opens a workspace.
   */
  protected void openWorkspace() {
    int	 	retVal;
    File 		file;
    
    retVal = m_WorkspaceFileChooser.showOpenDialog(MultiExplorer.this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;
    
    file = m_WorkspaceFileChooser.getSelectedFile();
    try {
      WorkspaceHelper.read(file, this);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	  this, 
	  "Failed to open workspace '" + file + "'!\n" + Utils.throwableToString(e));
    }
  }
  
  /**
   * Saves the current workspace.
   */
  public void saveWorkspace() {
    int		 	retVal;
    File 		file;
    
    retVal = m_WorkspaceFileChooser.showSaveDialog(MultiExplorer.this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    file = m_WorkspaceFileChooser.getSelectedFile();
    try {
      WorkspaceHelper.write(this, file);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	  this, 
	  "Failed to save workspace to '" + file + "'!\n" + Utils.throwableToString(e));
    }
  }
  
  /**
   * Returns the panel with the explorer panel entries.
   * 
   * @return		the panel entries
   */
  public ExplorerEntryPanel getEntryPanel() {
    return m_History;
  }

  /**
   * variable for the Explorer class which would be set to null by the memory
   * monitoring thread to free up some memory if we running out of memory
   */
  private static MultiExplorer m_Explorer;

  /** for monitoring the Memory consumption */
  protected static Memory m_Memory = new Memory(true);

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
      m_Explorer = new MultiExplorer();
      final BaseFrame frame = new BaseFrame("Multi-Explorer");
      frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
      frame.getContentPane().setLayout(new BorderLayout());
      frame.getContentPane().add(m_Explorer, BorderLayout.CENTER);
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
