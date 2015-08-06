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
 * FlowControlCenterPanel.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow;

import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.io.FilenameProposer;
import adams.env.Environment;
import adams.env.FlowControlCenterPanelDefinition;
import adams.flow.setup.FlowSetup;
import adams.flow.setup.FlowSetupManager;
import adams.gui.application.ChildFrame;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.BaseStatusBar.StatusProcessor;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTableWithButtons;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.MouseUtils;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.TitleGenerator;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.event.UndoEvent;
import adams.gui.flow.setup.FlowSetupCellEditor;
import adams.gui.flow.setup.FlowSetupTableModel;
import adams.gui.goe.GenericObjectEditorDialog;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * A panel that functions as control center for flows.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowControlCenterPanel
  extends BasePanel
  implements MenuBarProvider, StatusMessageHandler, TableModelListener {

  /** for serialization. */
  private static final long serialVersionUID = 4732793588874582421L;

  /** the name of the props file. */
  public final static String FILENAME = "FlowControlCenter.props";

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "FlowControlCenterSession.props";

  /** the file to store the recent files in. */
  public final static String FILE_EXTENSION = "fcc";

  /** the properties. */
  protected static Properties m_Properties;

  /** the panel itself. */
  protected FlowControlCenterPanel m_Self;

  /** for generating the title. */
  protected TitleGenerator m_TitleGenerator;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "new" menu item. */
  protected JMenuItem m_MenuItemFileNew;

  /** the "open" menu item. */
  protected JMenuItem m_MenuItemFileOpen;

  /** the "open recent" menu. */
  protected JMenu m_MenuItemFileOpenRecent;

  /** the "save" menu item. */
  protected JMenuItem m_MenuItemFileSave;

  /** the "save as" menu item. */
  protected JMenuItem m_MenuItemFileSaveAs;

  /** the "rever" menu item. */
  protected JMenuItem m_MenuItemFileRevert;

  /** the "exit" menu item. */
  protected JMenuItem m_MenuItemFileClose;

  /** the status. */
  protected BaseStatusBar m_StatusBar;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /** the filename of the current flow. */
  protected File m_CurrentFile;

  /** the filedialog for loading/saving flows. */
  protected BaseFileChooser m_FileChooser;

  /** for managing the setups. */
  protected FlowSetupManager m_Manager;

  /** the table model for the setups. */
  protected FlowSetupTableModel m_TableModelSetups;

  /** the table displaying the setups. */
  protected BaseTableWithButtons m_TableSetups;

  /** the button for adding a setup. */
  protected JButton m_ButtonAdd;

  /** the button for editing a setup. */
  protected JButton m_ButtonEdit;

  /** the button for editing a flow directly. */
  protected JButton m_ButtonEditFlow;

  /** the button for starting setups. */
  protected JButton m_ButtonStart;

  /** the button for pausing/resuming setups. */
  protected JButton m_ButtonPauseAndResume;

  /** the button for stopping setups. */
  protected JButton m_ButtonStop;

  /** the button for moving the selected setups up. */
  protected JButton m_ButtonMoveUp;

  /** the button for moving the selected setups down. */
  protected JButton m_ButtonMoveDown;

  /** the button for removing a setup. */
  protected JButton m_ButtonRemove;

  /** the button for removing all setups. */
  protected JButton m_ButtonRemoveAll;

  /** the GOE for editing the setups. */
  protected GenericObjectEditorDialog m_GOEDialog;

  /** for proposing filenames for new flows. */
  protected FilenameProposer m_FilenameProposer;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(FlowControlCenterPanelDefinition.KEY);

    m_Self                = this;
    m_Manager             = new FlowSetupManager();
    m_Manager.setStatusMessageHandler(this);
    m_CurrentFile         = null;
    m_RecentFilesHandler  = null;
    m_TitleGenerator      = new TitleGenerator("Flow Control Center", true);
    m_FileChooser         = new BaseFileChooser();
    m_FileChooser.addChoosableFileFilter(new ExtensionFileFilter("Flow control center setups", FILE_EXTENSION));
    m_FileChooser.setCurrentDirectory(new File(m_Properties.getPath("InitialDir", "%h")));
    m_FileChooser.setDefaultExtension(FILE_EXTENSION);
    m_FileChooser.setAutoAppendExtension(true);
    m_FilenameProposer   = new FilenameProposer("new", FILE_EXTENSION, m_Properties.getPath("InitialDir", "%h"));
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());

    // the setups
    panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.CENTER);
    m_TableModelSetups = new FlowSetupTableModel(m_Manager);
    m_TableSetups      = new BaseTableWithButtons(m_TableModelSetups);
    m_TableSetups.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_TableSetups.setRowHeight(40);
    m_TableSetups.getColumnModel().getColumn(FlowSetupTableModel.COLUMN_ONERROR).setCellEditor(new FlowSetupCellEditor(m_TableModelSetups));
    m_TableSetups.getColumnModel().getColumn(FlowSetupTableModel.COLUMN_ONFINISH).setCellEditor(new FlowSetupCellEditor(m_TableModelSetups));
    m_TableSetups.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	updateButtons();
      }
    });
    m_TableSetups.getComponent().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isRightClick(e)) {
	  showTablePopup(e);
	}
	else {
	  super.mouseClicked(e);
	}
      }
    });
    m_TableModelSetups.addTableModelListener(this);
    m_Manager.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	m_TableModelSetups.fireTableDataChanged();
      }
    });
    panel.add(new BaseScrollPane(m_TableSetups), BorderLayout.CENTER);

    // the buttons
    m_ButtonAdd = new JButton("Add...");
    m_ButtonAdd.setMnemonic('A');
    m_ButtonAdd.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	addSetup();
      }
    });
    m_ButtonEdit = new JButton("Edit...");
    m_ButtonEdit.setMnemonic('E');
    m_TableSetups.setDoubleClickButton(m_ButtonEdit);
    m_ButtonEdit.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	editSetup();
      }
    });
    m_ButtonEditFlow = new JButton("Edit flow...");
    m_ButtonEditFlow.setMnemonic('f');
    m_ButtonEditFlow.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	editFlow();
      }
    });
    m_ButtonStart = new JButton("Start");
    m_ButtonStart.setMnemonic('S');
    m_ButtonStart.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	startSetups();
      }
    });
    m_ButtonPauseAndResume = new JButton("Pause");
    m_ButtonPauseAndResume.setMnemonic('u');
    m_ButtonPauseAndResume.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	pauseAndResumeSetups();
      }
    });
    m_ButtonStop = new JButton("Stop");
    m_ButtonStop.setMnemonic('p');
    m_ButtonStop.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	stopSetups();
      }
    });
    m_ButtonMoveUp = new JButton("Move up");
    m_ButtonMoveUp.setMnemonic('u');
    m_ButtonMoveUp.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	moveRows(true);
      }
    });
    m_ButtonMoveDown = new JButton("Move down");
    m_ButtonMoveDown.setMnemonic('d');
    m_ButtonMoveDown.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	moveRows(false);
      }
    });
    m_ButtonRemove = new JButton("Remove");
    m_ButtonRemove.setMnemonic('R');
    m_ButtonRemove.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	removeSetups();
      }
    });
    m_ButtonRemoveAll = new JButton("Remove all");
    m_ButtonRemoveAll.setMnemonic('m');
    m_ButtonRemoveAll.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	removeAllSetups();
      }
    });
    m_TableSetups.addToButtonsPanel(m_ButtonAdd);
    m_TableSetups.addToButtonsPanel(m_ButtonEdit);
    m_TableSetups.addToButtonsPanel(m_ButtonEditFlow);
    m_TableSetups.addToButtonsPanel(new JLabel(""));
    m_TableSetups.addToButtonsPanel(m_ButtonStart);
    m_TableSetups.addToButtonsPanel(m_ButtonPauseAndResume);
    m_TableSetups.addToButtonsPanel(m_ButtonStop);
    m_TableSetups.addToButtonsPanel(new JLabel(""));
    m_TableSetups.addToButtonsPanel(m_ButtonMoveUp);
    m_TableSetups.addToButtonsPanel(m_ButtonMoveDown);
    m_TableSetups.addToButtonsPanel(new JLabel(""));
    m_TableSetups.addToButtonsPanel(m_ButtonRemove);
    m_TableSetups.addToButtonsPanel(m_ButtonRemoveAll);

    // the status
    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);
    m_StatusBar.setMouseListenerActive(true);
    m_StatusBar.setStatusProcessor(new StatusProcessor() {
      public String process(String msg) {
        return msg.replace(": ", ":\n");
      }
    });

    // set initial status
    update();
  }

  /**
   * Displays a popup for the table.
   *
   * @param e		the mouse event that triggered the popup
   */
  protected void showTablePopup(MouseEvent e) {
    BasePopupMenu 	menu;
    JMenuItem		menuitem;

    menu = new BasePopupMenu();

    // edit
    menuitem = new JMenuItem(m_ButtonEdit.getText());
    menuitem.setEnabled(m_ButtonEdit.isEnabled());
    menuitem.setIcon(GUIHelper.getIcon("properties.gif"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	editSetup();
      }
    });
    menu.add(menuitem);

    // edit flow
    menuitem = new JMenuItem(m_ButtonEditFlow.getText());
    menuitem.setEnabled(m_ButtonEdit.isEnabled());
    menuitem.setIcon(GUIHelper.getIcon("flow.gif"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	editFlow();
      }
    });
    menu.add(menuitem);

    // start
    menuitem = new JMenuItem(m_ButtonStart.getText());
    menuitem.setEnabled(m_ButtonStart.isEnabled());
    menuitem.setIcon(GUIHelper.getIcon("run.gif"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	startSetups();
      }
    });
    menu.addSeparator();
    menu.add(menuitem);

    // pause/resume
    menuitem = new JMenuItem(m_ButtonPauseAndResume.getText());
    menuitem.setEnabled(m_ButtonPauseAndResume.isEnabled());
    menuitem.setIcon(GUIHelper.getIcon("pause.gif"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	pauseAndResumeSetups();
      }
    });
    menu.add(menuitem);

    // stop
    menuitem = new JMenuItem(m_ButtonStop.getText());
    menuitem.setEnabled(m_ButtonStop.isEnabled());
    menuitem.setIcon(GUIHelper.getIcon("stop.gif"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	stopSetups();
      }
    });
    menu.add(menuitem);

    // remove
    menuitem = new JMenuItem(m_ButtonRemove.getText());
    menuitem.setEnabled(m_ButtonRemove.isEnabled());
    menuitem.setIcon(GUIHelper.getIcon("delete.gif"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	removeSetups();
      }
    });
    menu.addSeparator();
    menu.add(menuitem);

    // remove all
    menuitem = new JMenuItem(m_ButtonRemoveAll.getText());
    menuitem.setEnabled(m_ButtonRemoveAll.isEnabled());
    menuitem.setIcon(GUIHelper.getEmptyIcon());
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	removeAllSetups();
      }
    });
    menu.add(menuitem);

    menu.showAbsolute(this, e);
  }

  /**
   * Adds a setup.
   */
  protected void addSetup() {
    getGOEDialog().getEditor().setValue(new FlowSetup());
    getGOEDialog().setLocationRelativeTo(FlowControlCenterPanel.this);
    getGOEDialog().setVisible(true);
    if (getGOEDialog().getResult() == GenericObjectEditorDialog.APPROVE_OPTION) {
      m_TableModelSetups.addSetup((FlowSetup) getGOEDialog().getCurrent());
      m_TableSetups.setOptimalColumnWidth();
      update();
    }
  }

  /**
   * Edits the selected setup.
   */
  protected void editSetup() {
    getGOEDialog().getEditor().setValue(m_TableModelSetups.getSetup(m_TableSetups.getSelectedRow()).shallowCopy());
    getGOEDialog().setLocationRelativeTo(FlowControlCenterPanel.this);
    getGOEDialog().setVisible(true);
    if (getGOEDialog().getResult() == GenericObjectEditorDialog.APPROVE_OPTION) {
      m_TableModelSetups.setSetup(m_TableSetups.getSelectedRow(), (FlowSetup) getGOEDialog().getCurrent());
      m_TableSetups.setOptimalColumnWidth();
      update();
    }
  }

  /**
   * Edits the selected flow.
   */
  protected void editFlow() {
    FlowSetup		setup;
    BaseDialog 		dialog;
    FlowEditorPanel 	panel;
    String 		classname;

    setup = m_TableModelSetups.getSetup(m_TableSetups.getSelectedRow());
    if (getParentDialog() != null)
      dialog = new BaseDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new BaseDialog(getParentFrame(), true);
    panel     = new FlowEditorPanel();
    classname = FlowEditorPanel.getPropertiesEditor().getPath(
	"FlowEditorClass", FlowEditorPanel.class.getName());
    try {
      panel = (FlowEditorPanel) Class.forName(classname).newInstance();
    }
    catch (Exception ex) {
      panel = new FlowEditorPanel();
    }
    panel.loadUnsafe(setup.getFile());
    dialog.setTitle("Flow editor");
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.setJMenuBar(panel.getMenuBar());
    dialog.setSize(640, 480);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Starts the selected setups.
   */
  protected void startSetups() {
    int[] 	indices;
    int 	i;

    indices = m_TableSetups.getSelectedRows();
    for (i = 0; i < indices.length; i++) {
      try {
	if (!m_TableModelSetups.getSetup(indices[i]).execute())
	  GUIHelper.showErrorMessage(
	      FlowControlCenterPanel.this,
	      "Error executing flow '" + m_TableModelSetups.getSetup(indices[i]).getName() + "':\n"
	      + m_TableModelSetups.getSetup(indices[i]).retrieveLastError(),
  	      "Flow execution error");
      }
      catch (Exception ex) {
	GUIHelper.showErrorMessage(
	    FlowControlCenterPanel.this,
	    "Error executing flow '" + m_TableModelSetups.getSetup(indices[i]).getName() + "':\n"
	    + ex.toString(),
	    "Flow execution error");
      }
    }

    update();
  }

  /**
   * Pauses/resumes the selected setups.
   */
  protected void pauseAndResumeSetups() {
    int[] 	indices;
    int 	i;
    FlowSetup	setup;

    indices = m_TableSetups.getSelectedRows();
    for (i = 0; i < indices.length; i++) {
      setup = m_TableModelSetups.getSetup(indices[i]);
      if (setup.isPaused())
	setup.resumeExecution();
      else
	setup.pauseExecution();
    }

    update();
  }

  /**
   * Stops the selected setups.
   */
  protected void stopSetups() {
    int[] 	indices;
    int 	i;

    indices = m_TableSetups.getSelectedRows();
    for (i = 0; i < indices.length; i++)
      m_TableModelSetups.getSetup(indices[i]).stopExecution();

    update();
  }

  /**
   * Moves the selected rows and updates the selection.
   *
   * @param up		if true then the selected rows are moved up, otherwise
   * 			down
   */
  protected void moveRows(boolean up) {
    int[] 		indices;
    ListSelectionModel	selModel;
    int			i;

    // move rows
    if (up)
      indices = m_TableModelSetups.moveUp(m_TableSetups.getSelectedRows());
    else
      indices = m_TableModelSetups.moveDown(m_TableSetups.getSelectedRows());

    // update selection
    selModel = m_TableSetups.getSelectionModel();
    selModel.clearSelection();
    for (i = 0; i < indices.length; i++)
      selModel.addSelectionInterval(indices[i], indices[i]);
  }

  /**
   * Removes the selected setups.
   */
  protected void removeSetups() {
    int[] 		indices;
    ListSelectionModel	selModel;
    int			i;

    indices = m_TableSetups.getSelectedRows();

    selModel = m_TableSetups.getSelectionModel();
    selModel.clearSelection();

    for (i = indices.length - 1; i >= 0; i--)
      m_TableModelSetups.removeSetup(indices[i]);
    m_TableSetups.setOptimalColumnWidth();
    update();
  }

  /**
   * Removes all setups.
   */
  protected void removeAllSetups() {
    ListSelectionModel	selModel;

    selModel = m_TableSetups.getSelectionModel();
    selModel.clearSelection();

    m_TableModelSetups.clearSetups();
    m_TableSetups.setOptimalColumnWidth();
    update();
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
      // register window listener since we're part of a dialog or frame
      if (getParentFrame() != null) {
	final JFrame frame = (JFrame) getParentFrame();
	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	frame.addWindowListener(new WindowAdapter() {
	  @Override
	  public void windowClosing(WindowEvent e) {
	    close();
	  }
	});
      }
      else if (getParentDialog() != null) {
	final JDialog dialog = (JDialog) getParentDialog();
	dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	dialog.addWindowListener(new WindowAdapter() {
	  @Override
	  public void windowClosing(WindowEvent e) {
	    close();
	  }
	});
      }

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
      menuitem = new JMenuItem("New");
      menu.add(menuitem);
      menuitem.setMnemonic('N');
      menuitem.setIcon(GUIHelper.getIcon("new.gif"));
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  reset();
	}
      });
      m_MenuItemFileNew = menuitem;

      // File/Open setup
      menuitem = new JMenuItem("Open...");
      menu.add(menuitem);
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.setIcon(GUIHelper.getIcon("open.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  open();
	}
      });
      m_MenuItemFileOpen = menuitem;

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<JMenu>(
	  SESSION_FILE, m_Properties.getInteger("MaxRecentFlows", 5), submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  if (!checkForModified())
	    return;
	  load(e.getItem());
	}
      });
      m_MenuItemFileOpenRecent = submenu;

      // File/Save
      menuitem = new JMenuItem("Save");
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

      // File/Revert
      menuitem = new JMenuItem("Revert");
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.setIcon(GUIHelper.getIcon("refresh.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  revert();
	}
      });
      m_MenuItemFileRevert = menuitem;

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
      m_MenuItemFileClose = menuitem;

      if (GUIHelper.getParent(m_Self, ChildFrame.class) != null) {
	// Window
	menu = new JMenu("Window");
	result.add(menu);
	menu.setMnemonic('W');
	menu.addChangeListener(new ChangeListener() {
	  public void stateChanged(ChangeEvent e) {
	    updateMenu();
	  }
	});

	// Window/New Window
	menuitem = new JMenuItem("New Window");
	menu.add(menuitem);
	menuitem.setMnemonic('w');
	menuitem.setIcon(GUIHelper.getIcon("new.gif"));
	menuitem.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	    ChildFrame oldFrame = (ChildFrame) GUIHelper.getParent(m_Self, ChildFrame.class);
	    ChildFrame newFrame = oldFrame.getNewWindow();
	    newFrame.setVisible(true);
	  }
	});
      }

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
   * Initializes the GOE dialog if necessary and returns the instance.
   *
   * @return		the GOE dialog
   */
  protected GenericObjectEditorDialog getGOEDialog() {
    if (m_GOEDialog == null) {
      m_GOEDialog = GenericObjectEditorDialog.createDialog(this);
      m_GOEDialog.setTitle("Flow setup");
      m_GOEDialog.getGOEEditor().setClassType(FlowSetup.class);
      m_GOEDialog.getGOEEditor().setValue(new FlowSetup());
    }

    return m_GOEDialog;
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
   * An undo event occurred.
   *
   * @param e		the event
   */
  public void undoOccurred(UndoEvent e) {
    updateMenu();
  }

  /**
   * updates the enabled state etc. of all the GUI elements.
   */
  protected void update() {
    updateMenu();
    updateButtons();
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
    if (!m_TitleGenerator.isEnabled())
      return;
    setParentTitle(m_TitleGenerator.generate(m_CurrentFile, m_Manager.isModified()));
  }

  /**
   * Updates the buttons.
   */
  protected void updateButtons() {
    FlowSetup	setup;
    int		selRowCount;

    selRowCount = m_TableSetups.getSelectedRowCount();
    if (selRowCount == 1)
      setup = m_TableModelSetups.getSetup(m_TableSetups.getSelectedRow());
    else
      setup = null;

    m_ButtonAdd.setEnabled(true);
    m_ButtonEdit.setEnabled((selRowCount == 1) && (setup != null) && (!setup.isRunning()));
    m_ButtonEditFlow.setEnabled((selRowCount == 1) && (setup != null) && (!setup.isRunning()));
    if (selRowCount > 1) {
      m_ButtonStart.setEnabled(true);
      m_ButtonPauseAndResume.setEnabled(true);
      m_ButtonStop.setEnabled(true);
    }
    else if (selRowCount == 1) {
      m_ButtonStart.setEnabled(!setup.isRunning());
      m_ButtonPauseAndResume.setEnabled(setup.isRunning());
      m_ButtonStop.setEnabled(setup.isRunning());
    }
    else {
      m_ButtonStart.setEnabled(false);
      m_ButtonPauseAndResume.setEnabled(false);
      m_ButtonStop.setEnabled(false);
    }
    if ((setup != null) && (setup.isPaused()))
      m_ButtonPauseAndResume.setText("Resume");
    else
      m_ButtonPauseAndResume.setText("Pause");
    m_ButtonMoveUp.setEnabled(m_TableModelSetups.canMoveUp(m_TableSetups.getSelectedRows()));
    m_ButtonMoveDown.setEnabled(m_TableModelSetups.canMoveDown(m_TableSetups.getSelectedRows()));
    m_ButtonRemove.setEnabled(selRowCount > 0);
    m_ButtonRemoveAll.setEnabled(m_TableSetups.getRowCount() > 0);
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    updateTitle();

    if (m_MenuBar == null)
      return;

    m_MenuItemFileRevert.setEnabled(m_Manager.isModified());
    m_MenuItemFileSave.setEnabled(m_Manager.isModified());
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the flow or discarded the changes.
   *
   * @return		true if safe to proceed
   */
  protected boolean checkForModified() {
    boolean 	result;
    int		retVal;
    String	msg;

    result = !m_Manager.isModified();

    if (!result) {
      if (m_CurrentFile == null)
	msg = "Setup not saved - save?";
      else
	msg = "Setup not saved - save?\n" + m_CurrentFile;
      retVal = GUIHelper.showConfirmMessage(this, msg,"Setup not saved");
      switch (retVal) {
	case GUIHelper.APPROVE_OPTION:
	  if (m_CurrentFile != null)
	    save();
	  else
	    saveAs();
	  result = !m_Manager.isModified();
	  break;
	case GUIHelper.DISCARD_OPTION:
	  result = true;
	  break;
	case GUIHelper.CANCEL_OPTION:
	  result = false;
	  break;
      }
    }

    return result;
  }

  /**
   * Cleans up.
   */
  protected void cleanUp() {
    int		i;

    for (i = 0; i < m_Manager.size(); i++) {
      m_Manager.get(i).stopExecution();
      m_Manager.get(i).cleanUp();
    }
  }

  /**
   * Resets the GUI to default values.
   */
  protected void reset() {
    if (!checkForModified())
      return;

    cleanUp();

    m_CurrentFile = null;
    m_TableSetups.getSelectionModel().clearSelection();
    m_TableModelSetups.clearSetups();
    m_TableSetups.setOptimalColumnWidth();
    m_Manager.setModified(false);

    update();
  }

  /**
   * Returns the current file in use.
   *
   * @return		the current file, can be null
   */
  public File getCurrentFile() {
    return m_CurrentFile;
  }

  /**
   * Loads a setup.
   *
   * @param file	the setup to load
   */
  public void load(File file) {
    cleanUp();

    m_TableSetups.getSelectionModel().clearSelection();

    showStatus("Loading '" + file + "'...");
    m_Manager.read(file.getAbsolutePath());
    m_TableModelSetups.fireTableDataChanged();
    showStatus("");

    m_CurrentFile = file;

    m_FileChooser.setCurrentDirectory(file.getParentFile());
    if (m_RecentFilesHandler != null)
      m_RecentFilesHandler.addRecentItem(file);

    m_TableSetups.setOptimalColumnWidth();
    update();
  }

  /**
   * Opens a setup.
   */
  protected void open() {
    int		retVal;

    if (!checkForModified())
      return;

    retVal = m_FileChooser.showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    load(m_FileChooser.getSelectedPlaceholderFile());
  }

  /**
   * Reverts a setup.
   */
  protected void revert() {
    if (!checkForModified())
      return;

    load(m_CurrentFile);
  }

  /**
   * Saves the setup.
   */
  protected void save() {
    SwingWorker		worker;

    if (m_CurrentFile == null) {
      saveAs();
      return;
    }

    final File currFile = m_CurrentFile;
    worker = new SwingWorker() {
      boolean m_Result;

      @Override
      protected Object doInBackground() throws Exception {
	showStatus("Saving '" + currFile + "'...");
	m_Result = m_Manager.write(currFile.getAbsolutePath());
	showStatus("");
        return null;
      }

      @Override
      protected void done() {
	if (!m_Result) {
	  GUIHelper.showErrorMessage(
	      m_Self, "Error saving setup to '" + currFile.getAbsolutePath() + "'!");
	}
	else {
	  if (m_RecentFilesHandler != null)
	    m_RecentFilesHandler.addRecentItem(currFile);
	}

	update();

        super.done();
      }
    };
    worker.execute();
  }

  /**
   * Saves the setups.
   */
  protected void saveAs() {
    int		retVal;

    m_FileChooser.setSelectedFile(m_FilenameProposer.propose(m_CurrentFile));
    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    m_CurrentFile = m_FileChooser.getSelectedPlaceholderFile();

    save();
  }

  /**
   * Closes the dialog or frame.
   */
  protected void close() {
    if (!checkForModified()) {
      if (getParentDialog() != null)
	getParentDialog().setVisible(true);
      else if (getParentFrame() != null)
	getParentFrame().setVisible(true);
      return;
    }

    cleanUp();

    if (getParentFrame() != null)
      ((JFrame) getParentFrame()).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    closeParent();
  }

  /**
   * Displays the given message in a separate dialog.
   *
   * @param msg		the message to display
   */
  protected void showMessage(String msg) {
    String	status;

    status = msg.replaceAll(": ", ":\n");

    GUIHelper.showInformationMessage(
	this, status, "Status");
  }

  /**
   * This fine grain notification tells listeners the exact range
   * of cells, rows, or columns that changed.
   *
   * @param e		the event
   */
  public void tableChanged(TableModelEvent e) {
    update();
  }
}
