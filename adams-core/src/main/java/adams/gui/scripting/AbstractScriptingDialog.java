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
 * ScriptingDialog.java
 * Copyright (C) 2008-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.scripting;

import adams.core.CleanUpHandler;
import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.Utils;
import adams.core.io.FilenameProposer;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnectionHandler;
import adams.env.Environment;
import adams.env.ScriptingDialogDefinition;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BaseDialog;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.TitleGenerator;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.event.ScriptingInfoEvent;
import adams.gui.event.ScriptingInfoListener;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

/**
 * A dialog for loading/saving and executing scripts.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractScriptingDialog
  extends BaseDialog
  implements ScriptingEngineHandler, ScriptingInfoListener, StatusMessageHandler,
             DatabaseConnectionHandler, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = -4712521872103092592L;

  /** the name of the props file. */
  public final static String FILENAME = "ScriptingDialog.props";

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "ScriptingDialogSession.props";

  /** the dialog itself. */
  protected AbstractScriptingDialog m_Self;

  /** for generating the title. */
  protected TitleGenerator m_TitleGenerator;

  /** for loading/saving the scripts. */
  protected BaseFileChooser m_FileChooser;

  /** the menu. */
  protected JMenuBar m_Menu;

  /** the new menu item. */
  protected JMenuItem m_MenuItemFileNew;

  /** the load menu item. */
  protected JMenuItem m_MenuItemFileLoad;

  /** the load recent submenu. */
  protected JMenu m_MenuItemFileLoadRecent;

  /** the save menu item. */
  protected JMenuItem m_MenuItemFileSave;

  /** the save as menu item. */
  protected JMenuItem m_MenuItemFileSaveAs;

  /** the print menu item. */
  protected JMenuItem m_MenuItemFilePrint;

  /** the close menu item. */
  protected JMenuItem m_MenuItemFileClose;

  /** the undo menu item. */
  protected JMenuItem m_MenuItemEditUndo;

  /** the redo menu item. */
  protected JMenuItem m_MenuItemEditRedo;

  /** the clear menu item. */
  protected JMenuItem m_MenuItemEditClear;

  /** the append recorded menu item. */
  protected JMenuItem m_MenuItemEditAppendRecorded;

  /** the execute menu item. */
  protected JMenuItem m_MenuItemScriptStart;

  /** the stop menu item. */
  protected JMenuItem m_MenuItemScriptStop;

  /** the line wrap menu item. */
  protected JMenuItem m_MenuItemViewWordWrap;

  /** the help menu item. */
  protected JMenuItem m_MenuItemHelp;

  /** the text panel with the loaded script. */
  protected ScriptingTextEditorPanel m_TextScript;

  /** the status. */
  protected BaseStatusBar m_StatusBar;

  /** the currently loaded file. */
  protected File m_CurrentFile;

  /** whether the current content was modified. */
  protected boolean m_Modified;

  /** the properties for scripting. */
  protected Properties m_Properties;

  /** the undo manager for the text pane. */
  protected UndoManager m_Undo;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /** the base panel this dialog should operate on. */
  protected BasePanel m_BasePanel;

  /** for proposing filenames for new flows. */
  protected FilenameProposer m_FilenameProposer;

  /** the database connection to use. */
  protected AbstractDatabaseConnection m_DatabaseConnection;

  /**
   * Creates a non-modal dialog.
   *
   * @param owner	the owning dialog
   * @param panel	the base panel this dialog belongs to
   */
  public AbstractScriptingDialog(Dialog owner, BasePanel panel) {
    super(owner, "Scripting", ModalityType.MODELESS);
    m_BasePanel = panel;
  }

  /**
   * Creates a non-modal dialog.
   *
   * @param owner	the owning frame
   * @param panel	the base panel this dialog belongs to
   */
  public AbstractScriptingDialog(Frame owner, BasePanel panel) {
    super(owner, "Scripting", false);
    m_BasePanel = panel;
  }

  /**
   * initializes member variables.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Properties         = Environment.getInstance().read(ScriptingDialogDefinition.KEY);
    m_Self               = this;
    m_CurrentFile        = null;
    m_Modified           = false;
    m_Undo               = new UndoManager();
    m_RecentFilesHandler = null;
    m_TitleGenerator     = new TitleGenerator("Scripting", true);
    m_FileChooser        = new BaseFileChooser();
    m_FileChooser.setCurrentDirectory(new File(getScriptingEngine().getScriptsHome()));
    m_FilenameProposer   = new FilenameProposer("script", "", getScriptingEngine().getScriptsHome());

    setDatabaseConnection(getDefaultDatabaseConnection());
  }

  /**
   * Returns the default database connection.
   *
   * @return		the database connection
   */
  protected abstract AbstractDatabaseConnection getDefaultDatabaseConnection();

  /**
   * Initializes the GUI.
   */
  @Override
  protected void initGUI() {
    JMenu	menu;
    JMenu	submenu;
    JMenuItem	menuitem;

    super.initGUI();

    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    getContentPane().setLayout(new BorderLayout());
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
	close();
      }
    });

    // the script content
    m_TextScript = new ScriptingTextEditorPanel();
    m_TextScript.getDocument().addUndoableEditListener(new UndoableEditListener() {
      @Override
      public void undoableEditHappened(UndoableEditEvent e) {
	m_Undo.addEdit(e.getEdit());
	updateMenu();
      }
    });
    m_TextScript.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void changedUpdate(DocumentEvent e) {
	m_Modified = true;
	updateMenu();
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
	m_Modified = true;
	updateMenu();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
	m_Modified = true;
	updateMenu();
      }
    });
    getContentPane().add(m_TextScript, BorderLayout.CENTER);

    // the status
    m_StatusBar = new BaseStatusBar();
    getContentPane().add(m_StatusBar, BorderLayout.SOUTH);

    // the menu
    m_Menu = new JMenuBar();
    setJMenuBar(m_Menu);

    // File
    menu = new JMenu("File");
    menu.setMnemonic('F');
    menu.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        updateMenu();
      }
    });
    m_Menu.add(menu);

    // File/New
    menuitem = new JMenuItem("New", GUIHelper.getIcon("new.gif"));
    menuitem.setMnemonic('N');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        newScript();
      }
    });
    menu.add(menuitem);
    m_MenuItemFileLoad = menuitem;

    // File/Load
    menuitem = new JMenuItem("Open...", GUIHelper.getIcon("open.gif"));
    menuitem.setMnemonic('O');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        load();
      }
    });
    menu.add(menuitem);
    m_MenuItemFileLoad = menuitem;

    // File/Recent files
    submenu = new JMenu("Open recent");
    menu.add(submenu);
    m_RecentFilesHandler = new RecentFilesHandler<JMenu>(
	  getSessionFile(), m_Properties.getInteger("MaxRecentScripts", 5), submenu);
    m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	@Override
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	@Override
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  if (!checkForModified())
	    return;
	  load(e.getItem(), true);
	}
    });
    m_MenuItemFileLoadRecent = submenu;

    // File/Save
    menuitem = new JMenuItem("Save", GUIHelper.getIcon("save.gif"));
    menuitem.setMnemonic('S');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        save();
      }
    });
    menu.add(menuitem);
    m_MenuItemFileSave = menuitem;

    // File/Save
    menuitem = new JMenuItem("Save as...", GUIHelper.getEmptyIcon());
    menuitem.setMnemonic('S');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed S"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saveAs();
      }
    });
    menu.add(menuitem);
    m_MenuItemFileSaveAs = menuitem;

    // File/Print
    menuitem = new JMenuItem("Print", GUIHelper.getIcon("print.gif"));
    menuitem.setMnemonic('P');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed P"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TextScript.printText();
      }
    });
    menu.addSeparator();
    menu.add(menuitem);
    m_MenuItemFileClose = menuitem;

    // File/Close
    menuitem = new JMenuItem("Close", GUIHelper.getEmptyIcon());
    menuitem.setMnemonic('l');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("alt pressed F4"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	close();
        setVisible(false);
      }
    });
    menu.addSeparator();
    menu.add(menuitem);
    m_MenuItemFileClose = menuitem;

    // Edit
    menu = new JMenu("Edit");
    menu.setMnemonic('E');
    menu.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        updateMenu();
      }
    });
    m_Menu.add(menu);

    // Edit/Undo
    menuitem = new JMenuItem("Undo");
    menu.add(menuitem);
    menuitem.setMnemonic('U');
    menuitem.setEnabled(m_Undo.canUndo());
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Z"));
    menuitem.setIcon(GUIHelper.getIcon("undo.gif"));
    menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  m_Undo.undo();
	  updateMenu();
	}
    });
    m_MenuItemEditUndo = menuitem;

    menuitem = new JMenuItem("Redo");
    menu.add(menuitem);
    menuitem.setMnemonic('R');
    menuitem.setEnabled(m_Undo.canUndo());
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Y"));
    menuitem.setIcon(GUIHelper.getIcon("redo.gif"));
    menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  m_Undo.redo();
	  updateMenu();
	}
    });
    m_MenuItemEditRedo = menuitem;

    // Edit/Clear
    menuitem = new JMenuItem("Clear", GUIHelper.getEmptyIcon());
    menuitem.setMnemonic('C');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed N"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        clear();
      }
    });
    menu.addSeparator();
    menu.add(menuitem);
    m_MenuItemEditClear = menuitem;

    // Edit/Recorded
    menuitem = new JMenuItem("Append recorded commands", GUIHelper.getIcon("log.gif"));
    menuitem.setMnemonic('R');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("shift pressed INSERT"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        appendRecorded();
      }
    });
    menu.add(menuitem);
    m_MenuItemEditAppendRecorded = menuitem;

    // Script
    menu = new JMenu("Script");
    menu.setMnemonic('S');
    menu.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        updateMenu();
      }
    });
    m_Menu.add(menu);

    // Script/Start
    menuitem = new JMenuItem("Start", GUIHelper.getIcon("run.gif"));
    menuitem.setMnemonic('S');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("F5"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        startExecution();
      }
    });
    menu.add(menuitem);
    m_MenuItemScriptStart = menuitem;

    // Script/Stop
    menuitem = new JMenuItem("Stop", GUIHelper.getIcon("stop.gif"));
    menuitem.setMnemonic('o');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed F5"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        stopExecution();
      }
    });
    menu.add(menuitem);
    m_MenuItemScriptStop = menuitem;

    // View
    menu = new JMenu("View");
    menu.setMnemonic('V');
    menu.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        updateMenu();
      }
    });
    m_Menu.add(menu);

    // View/Word wrap
    menuitem = new JCheckBoxMenuItem("Word wrap", GUIHelper.getEmptyIcon());
    menuitem.setMnemonic('W');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed W"));
    menuitem.setSelected(m_TextScript.getWordWrap());
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	setWordWrap(!m_TextScript.getWordWrap());
      }
    });
    menu.add(menuitem);
    m_MenuItemViewWordWrap = menuitem;

    // Help
    menu = new JMenu("Help");
    menu.setMnemonic('H');
    menu.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        updateMenu();
      }
    });
    m_Menu.add(menu);

    // Help/Commands
    menuitem = new JMenuItem("Commands", GUIHelper.getIcon("help.gif"));
    menuitem.setMnemonic('C');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("F1"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        help();
      }
    });
    menu.add(menuitem);
    m_MenuItemHelp = menuitem;

    pack();
    setSize(600, 400);
    m_MenuItemViewWordWrap.setSelected(true);

    showStatus("Idle");
    updateMenu();
  }

  /**
   * Returns the underlying base panel.
   *
   * @return		the panel
   */
  public BasePanel getBasePanel() {
    return m_BasePanel;
  }

  /**
   * toggles the wordwrap.
   *
   * @param wrap	whether to wrap or not
   */
  protected void setWordWrap(boolean wrap) {
    m_TextScript.setWordWrap(wrap);
    m_TextScript.setCaretPosition(m_TextScript.getDocument().getLength());
  }

  /**
   * Returns the wordwrap status.
   *
   * @return		true if wordwrap is on
   */
  public boolean getWordWrap() {
    return m_TextScript.getWordWrap();
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the script or discarded the changes.
   *
   * @return		true if safe to proceed
   */
  protected boolean checkForModified() {
    boolean 	result;
    int		retVal;
    String	msg;

    result = !m_Modified;

    if (!result) {
      if (m_CurrentFile == null)
	msg = "Script not saved - save?";
      else
	msg = "Script not saved - save?\n" + m_CurrentFile;
      retVal = GUIHelper.showConfirmMessage(this, msg, "Script not saved");
      switch (retVal) {
	case GUIHelper.APPROVE_OPTION:
	  if (m_CurrentFile != null)
	    save();
	  else
	    saveAs();
	  result = !m_Modified;
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
   * Starts a new script.
   */
  protected void newScript() {
    if (!checkForModified())
      return;

    m_TextScript.setContent("");
    m_CurrentFile = null;
    m_Modified    = false;

    updateMenu();
  }

  /**
   * loads a script from a file.
   */
  protected void load() {
    int		retVal;

    if (!checkForModified())
      return;

    retVal = m_FileChooser.showOpenDialog(this);

    if (retVal == BaseFileChooser.APPROVE_OPTION)
      load(m_FileChooser.getSelectedFile(), true);
  }

  /**
   * Loads the specified file from disk, optionally shows an error dialog if
   * an error is encountered. If successful the current filename is set to the
   * one given here.
   *
   * @param file	the file to load
   * @param errorDlg	if true and an error is encountered an error dialog
   * 			is displayed
   * @return		true if successfully loaded.
   * @see		#m_CurrentFile
   */
  protected boolean load(File file, boolean errorDlg) {
    boolean		result;
    List<String>	script;

    script = AbstractScriptingEngine.load(file);
    result = (script != null);

    if (result) {
      m_CurrentFile = file;
      if (m_RecentFilesHandler != null)
	m_RecentFilesHandler.addRecentItem(file);
      m_TextScript.setContent(Utils.flatten(script, "\n"));
      m_TextScript.setCaretPosition(0);
      m_Modified = false;
      updateMenu();
    }
    else if (errorDlg) {
      GUIHelper.showErrorMessage(
	  this, "Failed to open script '" + file + "'!");
    }

    return result;
  }

  /**
   * saves the current script to a file.
   */
  protected void save() {
    if (m_CurrentFile == null)
      saveAs();
    else
      save(m_CurrentFile, true);
  }

  /**
   * saves the current script to a file.
   */
  protected void saveAs() {
    int		retVal;

    m_FileChooser.setSelectedFile(m_FilenameProposer.propose(m_CurrentFile));
    retVal = m_FileChooser.showSaveDialog(this);

    if (retVal == BaseFileChooser.APPROVE_OPTION)
      save(m_FileChooser.getSelectedFile(), true);
  }

  /**
   * Saves the current content to the specified file. Sets the current file
   * to the one given here.
   *
   * @param file	the file to save the content to
   * @param errorDlg	if true shows an error dialog in case of an error
   * @return		true if saving was successful
   * @see		#m_CurrentFile
   */
  protected boolean save(File file, boolean errorDlg) {
    boolean 	result;

    result = AbstractScriptingEngine.save(
  		m_TextScript.getContent().split("\n"),
  		file);

    if (!result && errorDlg)
      GUIHelper.showErrorMessage(
	  this,"Failed to save script '" + file + "'!");

    if (result) {
      if (m_RecentFilesHandler != null)
	m_RecentFilesHandler.addRecentItem(file);
      m_CurrentFile = file;
      m_Modified    = false;
    }

    updateMenu();

    return result;
  }

  /**
   * Closes the dialog.
   */
  protected void close() {
    if (!checkForModified())
      return;

    setVisible(false);
    dispose();
  }

  /**
   * Displays a help dialog.
   */
  protected void help() {
    JTextArea	textHelp;
    JPanel	panel;
    JButton	buttonClose;

    final JDialog dialog = new JDialog(this, "Help on scripting", false);
    dialog.getContentPane().setLayout(new BorderLayout());
    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

    textHelp = new JTextArea(30, 80);
    textHelp.setText(getScriptingEngine().getProcessor().globalInfo());
    textHelp.setFont(Fonts.getMonospacedFont());
    textHelp.setCaretPosition(0);
    textHelp.setEditable(false);
    dialog.getContentPane().add(new BaseScrollPane(textHelp), BorderLayout.CENTER);

    buttonClose = new JButton("Close", GUIHelper.getIcon("exit.png"));
    buttonClose.setMnemonic('l');
    buttonClose.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dialog.setVisible(false);
      }
    });

    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(buttonClose);
    dialog.getContentPane().add(panel, BorderLayout.SOUTH);

    dialog.pack();
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * executes the currently loaded script.
   */
  protected void startExecution() {
    String[]	commands;
    int		i;

    m_MenuItemFileLoad.setEnabled(false);
    m_MenuItemFileLoadRecent.setEnabled(false);
    m_MenuItemFileSave.setEnabled(false);
    m_MenuItemFileClose.setEnabled(false);
    m_MenuItemEditClear.setEnabled(false);
    m_MenuItemEditAppendRecorded.setEnabled(false);
    m_MenuItemScriptStart.setEnabled(false);
    m_MenuItemHelp.setEnabled(false);
    m_TextScript.setEnabled(false);

    commands = AbstractScriptingEngine.filter(m_TextScript.getContent().split("\n"));
    for (i = 0; i < commands.length; i++) {
      if (i < commands.length - 1)
	getScriptingEngine().add(
	    new ScriptingCommand(
		getBasePanel(),
		commands[i]));
      else
	getScriptingEngine().add(
	    new ScriptingCommand(
		getBasePanel(),
		commands[i],
		new ScriptingCommandCode() {
		  @Override
		  public void execute() {
		    m_TextScript.setEnabled(true);
		    showStatus("");
		    updateMenu();
		  }
		}));
    }
  }

  /**
   * stops the currently running script.
   */
  protected void stopExecution() {
    getScriptingEngine().stop();
  }

  /**
   * appends the recorded commands.
   */
  protected void appendRecorded() {
    List<String>	commands;
    int			i;
    Document		doc;

    doc = m_TextScript.getDocument();

    try {
      if (m_TextScript.getContent().length() > 0)
	doc.insertString(doc.getLength(), "\n", null);

      commands = getScriptingEngine().getRecordedCommands();
      for (i = 0; i < commands.size(); i++)
	doc.insertString(doc.getLength(), commands.get(i) + "\n", null);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * clears the input.
   */
  protected void clear() {
    if (!checkForModified())
      return;

    m_TextScript.setContent("");
  }

  /**
   * updates the state of the menu.
   */
  protected void updateMenu() {
    boolean	running;
    boolean	empty;

    running = !getScriptingEngine().isEmpty() || getScriptingEngine().isProcessing();
    empty   = (m_TextScript.getDocument().getLength() == 0);

    // file
    m_MenuItemFileLoad.setEnabled(!running);
    m_MenuItemFileLoadRecent.setEnabled(!running && (m_RecentFilesHandler.size() > 0));
    m_MenuItemFileSave.setEnabled(!running && (m_CurrentFile != null) && m_Modified);
    m_MenuItemFileSaveAs.setEnabled(!running && !empty);
    m_MenuItemFileClose.setEnabled(!running);

    // edit
    m_MenuItemEditUndo.setEnabled(m_Undo.canUndo());
    if (m_Undo.canUndo())
      m_MenuItemEditUndo.setText("Undo - " + m_Undo.getUndoPresentationName());
    else
      m_MenuItemEditUndo.setText("Undo");
    m_MenuItemEditRedo.setEnabled(m_Undo.canRedo());
    if (m_Undo.canRedo())
      m_MenuItemEditRedo.setText("Redo - " + m_Undo.getRedoPresentationName());
    else
      m_MenuItemEditRedo.setText("Redo");
    m_MenuItemEditClear.setEnabled(!running && !empty);
    m_MenuItemEditAppendRecorded.setEnabled((!getScriptingEngine().isRecording()) && (getScriptingEngine().hasRecording()) && !running);

    // script
    m_MenuItemScriptStart.setEnabled(!empty && !running);
    m_MenuItemScriptStop.setEnabled(running);

    // view
    m_MenuItemViewWordWrap.setEnabled(!running);

    // help
    m_MenuItemHelp.setEnabled(!running);

    updateTitle();
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
    setTitle(m_TitleGenerator.generate(m_CurrentFile, m_Modified));
  }

  /**
   * Returns the current scripting engine, can be null.
   *
   * @return		the current engine
   */
  @Override
  public abstract AbstractScriptingEngine getScriptingEngine();

  /**
   * Returns the name of the session file to use.
   *
   * @return		the filename (no path)
   */
  protected String getSessionFile() {
    return SESSION_FILE;
  }

  /**
   * The scripting engine fired an event.
   *
   * @param e		the event
   */
  @Override
  public void scriptingInfo(ScriptingInfoEvent e) {
    if (!e.hasCmd())
      showStatus("Idle");
    else
      showStatus("Running: " + e.getCmd());

    updateMenu();
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  @Override
  public void showStatus(String msg) {
    m_StatusBar.showStatus(msg);
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  @Override
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DatabaseConnection;
  }

  /**
   * Sets the database connection object to use.
   *
   * @param value	the object to use
   */
  @Override
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    if (getScriptingEngine() != null)
      getScriptingEngine().removeScriptingInfoListener(this);
    m_DatabaseConnection = value;
    if (getScriptingEngine() != null)
      getScriptingEngine().addScriptingInfoListener(this);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (getScriptingEngine() != null)
      getScriptingEngine().removeScriptingInfoListener(this);
  }
}
