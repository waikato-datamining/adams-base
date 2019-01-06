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
 * FindInFilesPanel.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools;

import adams.core.ClassLister;
import adams.core.CleanUpHandler;
import adams.core.Properties;
import adams.core.StoppableWithFeedback;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.io.lister.LocalDirectoryLister;
import adams.env.Environment;
import adams.gui.chooser.DirectoryChooserPanel;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseButtonWithDropDownMenu;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BaseListWithButtons;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.BaseTextField;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;
import adams.gui.core.RegExpTextField;
import adams.gui.dialog.TextDialog;
import adams.gui.tools.findinfiles.AbstractFindInFilesAction;
import adams.gui.tools.findinfiles.View;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Simple tool for finding text in .
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FindInFilesPanel
  extends BasePanel
  implements StoppableWithFeedback, CleanUpHandler {

  private static final long serialVersionUID = -7039965284917973727L;

  /** the file to store the recent parameters in. */
  public final static String SESSION_FILE = "FindInFilesSession.props";

  public static final String KEY_DIR = "dir";

  public static final String KEY_RECURSIVE = "recursive";

  public static final String KEY_FILEREGEXP = "fileregexp";

  public static final String KEY_MATCHING = "matching";

  public static final String KEY_SEARCHTEXT = "searchtext";

  public static final String KEY_CASESENSITIVE = "casesensitive";

  /** for the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the directory. */
  protected DirectoryChooserPanel m_PanelDir;

  /** whether to search recursively. */
  protected BaseCheckBox m_CheckBoxRecursive;

  /** the regexp for the files. */
  protected RegExpTextField m_TextFileRegExp;

  /** the type of search to perform. */
  protected BaseComboBox<String> m_ComboBoxMatching;

  /** the search expression. */
  protected BaseTextField m_TextSearchText;

  /** whether to search case-sensitive. */
  protected BaseCheckBox m_CheckBoxCaseSensitive;

  /** the button for starting the search. */
  protected BaseButton m_ButtonStart;

  /** the button for stopping the search. */
  protected BaseButton m_ButtonStop;

  /** the model for the results. */
  protected DefaultListModel<String> m_ModelResults;

  /** for listing the files that matched. */
  protected BaseListWithButtons m_ListResults;

  /** the actions. */
  protected List<AbstractFindInFilesAction> m_Actions;

  /** the action button. */
  protected BaseButtonWithDropDownMenu m_ButtonActions;

  /** the status bar. */
  protected BaseStatusBar m_StatusBar;

  /** whether the search has been stopped. */
  protected boolean m_Stopped;

  /** whether search is currently running. */
  protected boolean m_Running;

  /** the file lister to use. */
  protected LocalDirectoryLister m_Lister;

  /**
   * Initializes the members
   */
  @Override
  protected void initialize() {
    AbstractFindInFilesAction	action;

    super.initialize();

    m_Stopped = false;
    m_Running = false;
    m_Lister  = new LocalDirectoryLister();
    m_Actions = new ArrayList<>();
    for (Class cls: ClassLister.getSingleton().getClasses(AbstractFindInFilesAction.class)) {
      try {
        action = (AbstractFindInFilesAction) cls.newInstance();
        action.setOwner(this);
        m_Actions.add(action);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(
	  "Failed to instantiate action: " + Utils.classToString(cls), e);
      }
    }
    Collections.sort(m_Actions);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JPanel	panel2;

    super.initGUI();

    setLayout(new BorderLayout());

    // options
    panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.NORTH);

    m_PanelParameters = new ParameterPanel();
    panel.add(m_PanelParameters, BorderLayout.CENTER);
    m_PanelDir = new DirectoryChooserPanel();
    m_PanelDir.setToolTipText("The directory to search");
    m_PanelParameters.addParameter("Directory", m_PanelDir);
    m_CheckBoxRecursive = new BaseCheckBox();
    m_CheckBoxRecursive.setToolTipText("If checked, sub-directories get searched as well");
    m_PanelParameters.addParameter("Recursive", m_CheckBoxRecursive);
    m_TextFileRegExp = new RegExpTextField(BaseRegExp.MATCH_ALL);
    m_TextFileRegExp.setToolTipText("Files that match this file name pattern get their content searched");
    m_PanelParameters.addParameter("File pattern", m_TextFileRegExp);
    m_ComboBoxMatching = new BaseComboBox<>(new String[]{"simple", "regexp"});
    m_ComboBoxMatching.setToolTipText("The type of matching to perform: simple string matching or regular expressions");
    m_PanelParameters.addParameter("Matching", m_ComboBoxMatching);
    m_TextSearchText = new BaseTextField();
    m_TextSearchText.setToolTipText("The text to look for in the files");
    m_PanelParameters.addParameter("Search text", m_TextSearchText);
    m_CheckBoxCaseSensitive = new BaseCheckBox();
    m_CheckBoxCaseSensitive.setToolTipText("If enabled, the content of the files gets turned into lower-case first before matching");
    m_PanelParameters.addParameter("Case sensitive", m_CheckBoxCaseSensitive);

    // buttons
    panel2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(panel2, BorderLayout.SOUTH);
    m_ButtonStart = new BaseButton("Start");
    m_ButtonStart.setToolTipText("Starts the seach");
    m_ButtonStart.addActionListener((ActionEvent e) -> startSearch());
    panel2.add(m_ButtonStart);
    m_ButtonStop = new BaseButton("Stop");
    m_ButtonStop.setToolTipText("Stops a search that is currently underway");
    m_ButtonStop.addActionListener((ActionEvent e) -> stopExecution());
    panel2.add(m_ButtonStop);

    // results
    m_ModelResults = new DefaultListModel<>();
    m_ListResults  = new BaseListWithButtons(m_ModelResults);
    m_ListResults.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    m_ListResults.addListSelectionListener((ListSelectionEvent e) -> updateButtons());
    add(m_ListResults, BorderLayout.CENTER);

    m_ButtonActions = new BaseButtonWithDropDownMenu();
    for (AbstractFindInFilesAction action: m_Actions) {
      if (action instanceof View)
        m_ListResults.setDoubleClickAction(action);
      m_ButtonActions.addToMenu(action);
    }
    m_ListResults.addToButtonsPanel(m_ButtonActions);

    // status
    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    loadSession();
    updateButtons();
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    boolean selectedAny;
    boolean selectedOne;

    selectedAny = (m_ListResults.getSelectedIndices().length > 0);
    selectedOne = (m_ListResults.getSelectedIndices().length == 1);

    m_ButtonStart.setEnabled(!m_Running);
    m_ButtonStop.setEnabled(m_Running);
    for (AbstractFindInFilesAction action: m_Actions)
      action.update();
  }

  /**
   * Searches the specified file.
   *
   * @param file	the file to search
   * @param searchText	the search text
   * @param regExp	true if the search text is a regular expression
   * @return		true if the search text was found
   */
  protected boolean searchFile(String file, String searchText, boolean regExp, boolean caseSensitive) {
    FileReader		freader;
    BufferedReader	breader;
    String		line;
    Pattern		pattern;
    boolean		result;

    result  = false;
    freader = null;
    breader = null;
    pattern = null;
    if (regExp)
      pattern = Pattern.compile(searchText);
    else if (!caseSensitive)
      searchText = searchText.toLowerCase();

    try {
      freader = new FileReader(file);
      breader = new BufferedReader(freader);
      while ((line = breader.readLine()) != null) {
        if (!caseSensitive)
          line = line.toLowerCase();
        if (pattern != null)
          result = pattern.matcher(line).matches();
        else
          result = line.contains(searchText);
        if (result)
          break;
      }
    }
    catch (Exception e) {
      ConsolePanel.getSingleton().append("Failed to search: " + file, e);
    }
    finally {
      FileUtils.closeQuietly(breader);
      FileUtils.closeQuietly(freader);
    }

    return result;
  }

  /**
   * Starts the search.
   */
  public void startSearch() {
    SwingWorker		worker;
    final boolean	regexp;
    final boolean	caseSensitive;
    final String	searchText;

    saveSession();

    m_Stopped     = false;
    m_Running     = true;
    searchText    = m_TextSearchText.getText();
    regexp        = (m_ComboBoxMatching.getSelectedIndex() == 1);
    caseSensitive = m_CheckBoxCaseSensitive.isSelected();
    m_Lister.setWatchDir(new PlaceholderFile(m_PanelDir.getCurrentDirectory()).getAbsolutePath());
    m_Lister.setRecursive(m_CheckBoxRecursive.isSelected());
    m_Lister.setRegExp(m_TextFileRegExp.getRegExp());
    m_ModelResults.clear();

    if (regexp && !(new BaseRegExp().isValid(searchText))) {
      GUIHelper.showErrorMessage(this, "Invalid regular expression: " + searchText);
      return;
    }

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	m_StatusBar.showStatus("Locating files in: " + m_Lister.getWatchDir());
        String[] files = m_Lister.list();
        for (int i = 0; i < files.length; i++) {
          if (m_Stopped)
            break;
          m_StatusBar.showStatus((i+1) + "/" + files.length + ": " + files[i]);
	  if (searchFile(files[i], searchText, regexp, caseSensitive))
	    m_ModelResults.addElement(files[i]);
	}
	return null;
      }

      @Override
      protected void done() {
	super.done();
	m_Running = false;
	m_StatusBar.clearStatus();
	updateButtons();
      }
    };
    worker.execute();
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Lister.stopExecution();
    m_Stopped = true;
    m_Running = false;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Returns whether the search is currently underway.
   *
   * @return		true if currently running
   */
  public boolean isRunning() {
    return m_Running;
  }

  /**
   * Returns the currently selected file.
   *
   * @return		the file, null if none or more than one selected
   */
  public File getSelectedFile() {
    if (m_ListResults.getSelectedIndices().length != 1)
      return null;
    else
      return new PlaceholderFile("" + m_ListResults.getSelectedValue()).getAbsoluteFile();
  }

  /**
   * Returns the currently selected files.
   *
   * @return		the files
   */
  public File[] getSelectedFiles() {
    List<File> 	result;

    result = new ArrayList<>();
    for (Object o: m_ListResults.getSelectedValues())
      result.add(new PlaceholderFile("" + o).getAbsoluteFile());

    return result.toArray(new File[0]);
  }

  /**
   * Views the currently selected file.
   */
  public void viewFile() {
    TextDialog 		dialog;

    if (m_ListResults.getSelectedIndices().length != 1)
      return;

    if (getParentDialog() != null)
      dialog = new TextDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new TextDialog(getParentFrame(), false);
    dialog.setCanOpenFiles(true);
    dialog.setSize(GUIHelper.getDefaultDialogDimension());
    dialog.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
    dialog.open(getSelectedFile());
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Loads the parameters from an existing session file.
   */
  protected void loadSession() {
    String	filename;
    Properties	props;

    filename = Environment.getInstance().createPropertiesFilename(SESSION_FILE);
    if (FileUtils.fileExists(filename)) {
      props = new Properties();
      if (props.load(filename)) {
        if (props.hasKey(KEY_DIR))
          m_PanelDir.setCurrentDirectory(props.getPath(KEY_DIR));
        if (props.hasKey(KEY_RECURSIVE))
          m_CheckBoxRecursive.setSelected(props.getBoolean(KEY_RECURSIVE));
        if (props.hasKey(KEY_FILEREGEXP))
          m_TextFileRegExp.setText(props.getProperty(KEY_FILEREGEXP));
        if (props.hasKey(KEY_MATCHING)) {
	  m_ComboBoxMatching.setSelectedItem(props.getProperty(KEY_MATCHING));
	  if (m_ComboBoxMatching.getSelectedIndex() == -1)
	    m_ComboBoxMatching.setSelectedIndex(0);
	}
	if (props.hasKey(KEY_SEARCHTEXT))
	  m_TextSearchText.setText(props.getProperty(KEY_SEARCHTEXT));
        if (props.hasKey(KEY_CASESENSITIVE))
          m_CheckBoxCaseSensitive.setSelected(props.getBoolean(KEY_CASESENSITIVE));
      }
    }
  }

  /**
   * Stores the current parameters in a session props file.
   */
  protected void saveSession() {
    Properties	props;
    String	filename;

    props = new Properties();
    props.setPath(KEY_DIR, m_PanelDir.getCurrentDirectory());
    props.setBoolean(KEY_RECURSIVE, m_CheckBoxRecursive.isSelected());
    props.setProperty(KEY_FILEREGEXP, m_TextFileRegExp.getText());
    props.setProperty(KEY_MATCHING, m_ComboBoxMatching.getSelectedItem());
    props.setProperty(KEY_SEARCHTEXT, m_TextSearchText.getText());
    props.setBoolean(KEY_CASESENSITIVE, m_CheckBoxCaseSensitive.isSelected());

    filename = Environment.getInstance().createPropertiesFilename(SESSION_FILE);
    if (!props.save(filename))
      GUIHelper.showErrorMessage(this, "Failed to store session file:\n" + filename);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    if (m_Actions != null) {
      for (AbstractFindInFilesAction action: m_Actions)
        action.cleanUp();
      m_Actions.clear();
      m_Actions = null;
    }
  }
}
