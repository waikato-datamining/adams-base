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
 * PreviewBrowserPanel.java
 * Copyright (C) 2011-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import adams.core.ClassLister;
import adams.core.CleanUpHandler;
import adams.core.Properties;
import adams.core.ShorteningType;
import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.classmanager.ClassManager;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.io.TempUtils;
import adams.core.io.filechanged.FileChangeMonitor;
import adams.core.io.filechanged.LastModified;
import adams.core.logging.LoggingLevel;
import adams.gui.application.ChildFrame;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.DirectoryChooserPanel;
import adams.gui.chooser.TextFileChooser;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.JTableSupporter;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.MouseUtils;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SearchableBaseList;
import adams.gui.core.TitleGenerator;
import adams.gui.core.UISettings;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.TextDialog;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.event.SearchEvent;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.tools.previewbrowser.AbstractArchiveHandler;
import adams.gui.tools.previewbrowser.ArchiveHandler;
import adams.gui.tools.previewbrowser.NoPreviewAvailablePanel;
import adams.gui.tools.previewbrowser.PreviewDisplay;
import adams.gui.tools.previewbrowser.PropertiesManager;
import adams.gui.tools.previewbrowser.localfiles.AbstractLocalFilesAction;
import adams.gui.tools.previewbrowser.notes.AbstractNotesWriter;
import adams.gui.tools.previewbrowser.notes.NotesFileChooser;
import adams.gui.tools.previewbrowser.notes.NotesManager;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Allows the browsing of files and previewing the content.
 * Also works for archives.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class PreviewBrowserPanel
    extends BasePanel
    implements MenuBarProvider, SendToActionSupporter, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = 5284765971012530865L;

  /**
   * {@link Comparator} for {@link File} objects. Only uses the file name, not
   * the path, for comparison. Also, ignores the case.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class FileComparator
      implements Comparator<File> {

    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.
     * <br><br>
     * Uses only the file name, not the path for comparison (lower case).
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * 	       first argument is less than, equal to, or greater than the
     *	       second.
     */
    @Override
    public int compare(File o1, File o2) {
      if ((o1 == null) && (o2 == null))
	return 0;
      if (o1 == null)
	return -1;
      if (o2 == null)
	return 1;
      return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
    }
  }

  /**
   * Renderer for the local files, which indicates whether a note is present or not.
   */
  public static class LocalFilesListCellRenderer
    extends DefaultListCellRenderer {

    /** the owner. */
    protected PreviewBrowserPanel m_Owner;

    /**
     * Initializes the renderer.
     *
     * @param owner	the owning preview browser
     */
    public LocalFilesListCellRenderer(PreviewBrowserPanel owner) {
      super();
      m_Owner = owner;
    }

    /**
     * Return a component that has been configured to display the specified
     * value. That component's <code>paint</code> method is then called to
     * "render" the cell.  If it is necessary to compute the dimensions
     * of a list because the list cells do not have a fixed size, this method
     * is called to generate a component on which <code>getPreferredSize</code>
     * can be invoked.
     *
     * @param list         The JList we're painting.
     * @param value        The value returned by list.getModel().getElementAt(index).
     * @param index        The cells index.
     * @param isSelected   True if the specified cell was selected.
     * @param cellHasFocus True if the specified cell has the focus.
     * @return A component whose paint() method will render the specified value.
     */
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      File file = new File(m_Owner.getDirPanel().getCurrentDirectory() + File.separator + value);
      if (m_Owner.hasNote(file)) {
	setIcon(ImageManager.getIcon("editor.gif"));
	setToolTipText(GUIHelper.processTipText(m_Owner.getNote(file)));
      }
      else {
	setIcon(ImageManager.getEmptyIcon());
	setToolTipText(null);
      }
      return this;
    }
  }

  /** the file to store the recent directories. */
  public final static String SESSION_FILE = "PreviewBrowserSession.props";

  /** the split pane in use. */
  protected BaseSplitPane m_SplitPane;

  /** for selecting the directory to browse. */
  protected DirectoryChooserPanel m_PanelDir;

  /** the panel for browsing. */
  protected BaseSplitPane m_PaneBrowsing;

  /** the panel with the local files. */
  protected BasePanel m_PanelLocalFiles;

  /** the renderer for the local files. */
  protected ListCellRenderer m_ListLocalFilesRenderer;

  /** the list with the local files. */
  protected SearchableBaseList m_ListLocalFiles;

  /** the model for the local files. */
  protected DefaultListModel<String> m_ModelLocalFiles;

  /** the search panel for the local files. */
  protected SearchPanel m_SearchLocalFiles;

  /** the panel with the archive files. */
  protected BasePanel m_PanelArchiveFiles;

  /** the panel with the archive files (bottom). */
  protected BasePanel m_PanelArchiveFilesBottom;

  /** the list with the archive files. */
  protected SearchableBaseList m_ListArchiveFiles;

  /** the model for the archive files. */
  protected DefaultListModel m_ModelArchiveFiles;

  /** the search panel for the archive files. */
  protected SearchPanel m_SearchArchiveFiles;

  /** the panel for the content display. */
  protected PreviewDisplay m_PanelContent;

  /** the panel with the archive handlers. */
  protected BasePanel m_PanelArchiveHandlers;

  /** the combobox with the archive handlers (if more than one available). */
  protected BaseComboBox m_ComboBoxArchiveHandlers;

  /** the model of the combobox. */
  protected DefaultComboBoxModel<String> m_ModelArchiveHandlers;

  /** whether to ignore selections of the archive handler combobox temporarily. */
  protected boolean m_IgnoreArchiveHandlerChanges;

  /** the current archive handler. */
  protected AbstractArchiveHandler m_ArchiveHandler;

  /** the currently selected files. */
  protected File[] m_CurrentFiles;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "load recent" submenu. */
  protected JMenu m_MenuFileLoadRecent;

  /** the "cache views" menu item. */
  protected JMenuItem m_MenuItemCacheViews;

  /** the "use fixed handler" menu item. */
  protected JMenuItem m_MenuItemUseFixedHandler;

  /** the "show hidden files" menu item. */
  protected JMenuItem m_MenuItemShowHiddenFiles;

  /** the "show temp files" menu item. */
  protected JMenuItem m_MenuItemShowTempFiles;

  /** the "clear notes (global)" menu item. */
  protected JMenuItem m_MenuItemClearNotesAll;

  /** the "clear notes (current dir)" menu item. */
  protected JMenuItem m_MenuItemClearNotesDir;

  /** the "show notes" menu item. */
  protected JMenuItem m_MenuItemViewNotes;

  /** the "save notes" menu item. */
  protected JMenuItem m_MenuItemSaveNotes;

  /** for generating the title of the dialog/frame. */
  protected TitleGenerator m_TitleGenerator;

  /** the file chooser for opening files. */
  protected BaseFileChooser m_FileChooser;

  /** the file change monitor. */
  protected FileChangeMonitor m_ChangeMonitor;

  /** the actions for local files. */
  protected List<AbstractLocalFilesAction> m_LocalFilesActions;

  /** the file chooser for the notes. */
  protected NotesFileChooser m_FileChooserNotes;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ArchiveHandler              = null;
    m_IgnoreArchiveHandlerChanges = false;
    m_CurrentFiles                = null;
    m_RecentFilesHandler          = null;
    m_TitleGenerator              = new TitleGenerator("Preview browser", false, ShorteningType.START);
    m_FileChooser                 = new BaseFileChooser(PropertiesManager.getProperties().getPath("InitialDir", "%h"));
    m_ChangeMonitor               = new LastModified();
    m_LocalFilesActions           = null;
    m_FileChooserNotes            = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    Properties		props;

    super.initGUI();

    props = PropertiesManager.getProperties();

    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setOneTouchExpandable(true);
    m_SplitPane.setDividerLocation(UISettings.get(getClass(), "Divider", props.getInteger("DividerLocation", 200)));
    m_SplitPane.setUISettingsParameters(getClass(), "Divider");
    add(m_SplitPane, BorderLayout.CENTER);

    // browsing
    m_PaneBrowsing = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPane.setLeftComponent(m_PaneBrowsing);

    m_PanelLocalFiles = new BasePanel(new BorderLayout(0, 5));
    m_PaneBrowsing.setTopComponent(m_PanelLocalFiles);
    m_PanelLocalFiles.setBorder(BorderFactory.createTitledBorder("Files"));

    m_PanelDir = new DirectoryChooserPanel(props.getPath("InitialDir", "%h"));
    m_PanelDir.addChangeListener((ChangeEvent e) -> {
      refreshLocalFiles();
      if (m_RecentFilesHandler != null)
	m_RecentFilesHandler.addRecentItem(m_PanelDir.getCurrent());
      if (!m_ModelLocalFiles.isEmpty())
	m_ListLocalFiles.setSelectedIndex(0);
    });
    m_PanelLocalFiles.add(m_PanelDir, BorderLayout.NORTH);

    m_ModelLocalFiles        = new DefaultListModel<>();
    m_ListLocalFilesRenderer = new LocalFilesListCellRenderer(this);
    m_ListLocalFiles         = new SearchableBaseList(m_ModelLocalFiles);
    m_ListLocalFiles.setCellRenderer(m_ListLocalFilesRenderer);
    m_ListLocalFiles.addListSelectionListener((ListSelectionEvent e) -> {
      if (e.getValueIsAdjusting())
	return;
      displayLocalFile();
    });
    m_ListLocalFiles.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isLeftClick(e)) {
	  e.consume();
	  displayLocalFile();
	}
	else if (MouseUtils.isRightClick(e)) {
	  e.consume();
	  JPopupMenu menu = getLocalFilesPopupMenu(e);
	  menu.show(m_ListLocalFiles, e.getX(), e.getY());
	}
	else {
	  super.mouseClicked(e);
	}
      }
    });
    m_ListLocalFiles.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
	super.focusGained(e);
	monitorFile(true);
      }
    });
    m_PanelLocalFiles.add(new BaseScrollPane(m_ListLocalFiles), BorderLayout.CENTER);

    m_SearchLocalFiles = new SearchPanel(LayoutType.HORIZONTAL, false);
    m_SearchLocalFiles.setIncremental(true);
    m_SearchLocalFiles.addSearchListener((SearchEvent e) -> m_ListLocalFiles.search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));
    m_PanelLocalFiles.add(m_SearchLocalFiles, BorderLayout.SOUTH);

    m_PanelArchiveFiles = new BasePanel(new BorderLayout());
    m_PaneBrowsing.setBottomComponent(m_PanelArchiveFiles);
    m_PaneBrowsing.setBottomComponentHidden(true);
    m_PanelArchiveFiles.setBorder(BorderFactory.createTitledBorder("Archive"));

    m_ModelArchiveFiles = new DefaultListModel();
    m_ListArchiveFiles  = new SearchableBaseList(m_ModelArchiveFiles);
    m_ListArchiveFiles.addListSelectionListener((ListSelectionEvent e) -> {
      if (e.getValueIsAdjusting())
	return;
      displayArchiveContent();
    });
    m_ListArchiveFiles.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	displayArchiveContent();
      }
    });
    m_PanelArchiveFiles.add(new BaseScrollPane(m_ListArchiveFiles), BorderLayout.CENTER);

    m_PanelArchiveFilesBottom = new BasePanel(new BorderLayout());
    m_PanelArchiveFiles.add(m_PanelArchiveFilesBottom, BorderLayout.SOUTH);

    m_SearchArchiveFiles = new SearchPanel(LayoutType.HORIZONTAL, false);
    m_SearchArchiveFiles.setIncremental(true);
    m_SearchArchiveFiles.addSearchListener((SearchEvent e) -> m_ListArchiveFiles.search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));
    m_PanelArchiveFilesBottom.add(m_SearchArchiveFiles, BorderLayout.CENTER);

    m_PanelArchiveHandlers = new BasePanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelArchiveHandlers.setVisible(false);
    m_PanelArchiveFilesBottom.add(m_PanelArchiveHandlers, BorderLayout.SOUTH);

    m_ModelArchiveHandlers    = new DefaultComboBoxModel<>();
    m_ComboBoxArchiveHandlers = new BaseComboBox<>(m_ModelArchiveHandlers);
    m_ComboBoxArchiveHandlers.addActionListener((ActionEvent e) -> {
      if (m_IgnoreArchiveHandlerChanges)
	return;
      updatePreferredArchiveHandler();
      displayArchiveContent();
    });
    m_PanelArchiveHandlers.add(new JLabel("Preferred handler"));
    m_PanelArchiveHandlers.add(m_ComboBoxArchiveHandlers);

    // content
    m_PanelContent = new PreviewDisplay();
    m_PanelContent.setPreviewCacheSize(props.getInteger("CacheSize", 100));
    m_SplitPane.setRightComponent(m_PanelContent);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    refreshLocalFiles();
  }

  /**
   * Monitors the selected file and triggers an updated if necessary.
   *
   * @param display	whether to trigger a display update or not
   */
  protected void monitorFile(boolean display) {
    if ((m_CurrentFiles != null) && (m_CurrentFiles.length == 1)) {
      if (m_ChangeMonitor.isInitialized(m_CurrentFiles[0])) {
	if (m_ChangeMonitor.hasChanged(m_CurrentFiles[0])) {
	  if (display)
	    displayLocalFile();
	}
	m_ChangeMonitor.update(m_CurrentFiles[0]);
      }
      else {
	m_ChangeMonitor.initialize(m_CurrentFiles[0]);
      }
    }
  }

  /**
   * Filters the files. Hidden/temp files may get removed.
   *
   * @param files	the files to filter
   * @return		the filtered files
   */
  protected File[] filterFiles(File[] files) {
    List<File>		result;
    boolean		showHidden;
    boolean		showTemp;
    BaseRegExp		regExp;
    String[]		expList;
    String		exp;

    if (files == null)
      return null;

    if (m_MenuItemShowHiddenFiles != null)
      showHidden = m_MenuItemShowHiddenFiles.isSelected();
    else
      showHidden = PropertiesManager.getProperties().getBoolean("ShowHiddenFiles", false);
    if (m_MenuItemShowTempFiles != null)
      showTemp = m_MenuItemShowTempFiles.isSelected();
    else
      showTemp = PropertiesManager.getProperties().getBoolean("ShowTempFiles", false);
    regExp = new BaseRegExp(BaseRegExp.MATCH_ALL);
    exp    = PropertiesManager.getProperties().getProperty("TempFiles", ".*\\.bak|.*\\.backup|.*~");
    if (exp.length() > 0) {
      expList = Utils.split(exp, '|');
      exp = "";
      for (String item: expList) {
	if (exp.length() > 0)
	  exp += "|";
	exp += item;
      }
      regExp = new BaseRegExp("^(" + exp + ")");
    }
    else {
      // no exclusion patterns
      showTemp = true;
    }

    if (showHidden && showTemp)
      return files;

    result = new ArrayList<>();
    for (File file: files) {
      if (!showTemp && regExp.isMatch(file.getName()))
	continue;
      if (!showHidden && file.isHidden())
	continue;
      result.add(file);
    }

    return result.toArray(new File[result.size()]);
  }

  /**
   * Sets whether to reuse previews.
   *
   * @param value	true if to reuse
   */
  public void setReusePreviews(boolean value) {
    m_MenuItemCacheViews.setSelected(value);
    m_PanelContent.setReusePreviews(value);
  }

  /**
   * Returns whether to reuse previews.
   *
   * @return		true if to reuse
   */
  public boolean getReusePreviews() {
    return m_PanelContent.getReusePreviews();
  }

  /**
   * Based on check state of menu item, sets whether to reuse the views.
   */
  protected void updateCacheViews() {
      setReusePreviews(m_MenuItemCacheViews.isSelected());
  }

  /**
   * Based on check state of menu item, sets whether to use a fixed handler.
   */
  protected void updateUseFixedHandler() {
    setUseFixedContentHandler(m_MenuItemUseFixedHandler.isSelected());
  }

  /**
   * Sets whether to use a fixed content handler.
   *
   * @param value	true if to use fixed handler
   */
  public void setUseFixedContentHandler(boolean value) {
    m_MenuItemUseFixedHandler.setSelected(value);
    m_PanelContent.setUseFixedContentHandler(value);
  }

  /**
   * Returns whether to use a fixed content handler.
   *
   * @return		true if to use fixed handler
   */
  public boolean getUseFixedContentHandler() {
    return m_PanelContent.getUseFixedContentHandler();
  }

  /**
   * Based on check state of menu item, sets whether to show/hide hidden files.
   */
  protected void updateShowHiddenFiles() {
    refreshLocalFiles();
  }

  /**
   * Sets whether to show hidden files or not.
   *
   * @param value	true if to show
   */
  public void setShowHiddenFiles(boolean value) {
    m_MenuItemShowHiddenFiles.setSelected(value);
  }

  /**
   * Returns whether to show hidden files or not.
   * 
   * @return		true if to show
   */
  public boolean getShowHiddenFiles() {
    return m_MenuItemShowHiddenFiles.isSelected();
  }

  /**
   * Based on check state of menu item, sets whether to show/hide temp files.
   */
  protected void updateShowTempFiles() {
    refreshLocalFiles();
  }

  /**
   * Sets whether to show temp files or not.
   *
   * @param value	true if to show
   */
  public void setShowTempFiles(boolean value) {
    m_MenuItemShowTempFiles.setSelected(value);
  }

  /**
   * Returns whether to show temp files or not.
   *
   * @return		true if to show
   */
  public boolean getShowTempFiles() {
    return m_MenuItemShowTempFiles.isSelected();
  }

  /**
   * Refreshes the local file list.
   */
  protected synchronized void refreshLocalFiles() {
    File		dir;
    File[]		files;
    List<File>		fileList;

    dir   = m_PanelDir.getCurrent();
    files = filterFiles(dir.listFiles());
    fileList = new ArrayList<>();
    if (files != null) {
      for (File file : files) {
	if (file.isDirectory())
	  continue;
	fileList.add(file);
      }
    }
    Collections.sort(fileList, new FileComparator());
    m_ListLocalFiles.clearSelection();
    m_ModelLocalFiles.clear();
    for (File file: fileList)
      m_ModelLocalFiles.addElement(file.getName());
    m_ListLocalFiles.search(m_ListLocalFiles.getSeachString(), m_ListLocalFiles.isRegExpSearch());

    m_ListArchiveFiles.clearSelection();
    m_ModelArchiveFiles.clear();
    m_PaneBrowsing.setBottomComponentHidden(true);
  }

  /**
   * Displays a local file.
   */
  protected synchronized void displayLocalFile() {
    PlaceholderFile[]	localFiles;
    List<Class>		handlers;
    String[]		files;
    int			n;

    if (m_ListLocalFiles.getSelectedIndex() < 0)
      return;
    if (m_ListLocalFiles.getSelectedIndices().length < 1)
      return;

    localFiles = new PlaceholderFile[m_ListLocalFiles.getSelectedIndices().length];
    for (n = 0; n < localFiles.length; n++)
      localFiles[n] = new PlaceholderFile(m_PanelDir.getCurrent().getAbsolutePath() + File.separator + m_ListLocalFiles.getSelectedValuesList().get(n));

    if (AbstractArchiveHandler.hasHandler(localFiles[0])) {
      m_PaneBrowsing.setBottomComponentHidden(false);
      handlers = AbstractArchiveHandler.getHandlersForFile(localFiles[0]);
      // update combobox
      m_IgnoreArchiveHandlerChanges = true;
      m_ModelArchiveHandlers.removeAllElements();
      for (Class cls: handlers)
	m_ModelArchiveHandlers.addElement(cls.getName());
      m_PanelArchiveHandlers.setVisible(m_ModelArchiveHandlers.getSize() > 1);
      ArchiveHandler preferred = PropertiesManager.getPreferredArchiveHandler(localFiles[0]);
      // set preferred one
      m_ComboBoxArchiveHandlers.setSelectedIndex(0);
      if (preferred != null) {
	for (int i = 0; i < handlers.size(); i++) {
	  if (preferred.getClass() == handlers.get(i)) {
	    m_ComboBoxArchiveHandlers.setSelectedIndex(i);
	    break;
	  }
	}
      }
      m_IgnoreArchiveHandlerChanges = false;
      try {
	Class cls = ClassManager.getSingleton().forName((String) m_ComboBoxArchiveHandlers.getSelectedItem());
	m_ArchiveHandler = (AbstractArchiveHandler) cls.getDeclaredConstructor().newInstance();
      }
      catch (Exception e) {
	m_ArchiveHandler = null;
	ConsolePanel.getSingleton().append(
	    LoggingLevel.SEVERE,
	    "Failed to obtain archive handler for '" + localFiles[0] + "':",
	    e);
      }
    }
    else {
      m_ArchiveHandler = null;
    }

    m_ModelArchiveFiles.clear();
    m_PaneBrowsing.setBottomComponentHidden(m_ArchiveHandler == null);
    if (m_ArchiveHandler == null) {
      m_PanelContent.display(localFiles, false);
      m_CurrentFiles = localFiles;
      monitorFile(false);
    }
    else {
      m_CurrentFiles = null;
      m_ArchiveHandler.setArchive(localFiles[0]);
      files = m_ArchiveHandler.getFiles();
      for (String file: files)
	m_ModelArchiveFiles.addElement(file);
      m_ListArchiveFiles.search(m_ListArchiveFiles.getSeachString(), m_ListArchiveFiles.isRegExpSearch());
      // reset selection
      m_ListArchiveFiles.setSelectedIndices(new int[0]);
      m_PanelContent.displayView(new NoPreviewAvailablePanel());
    }

    update();
  }

  /**
   * Displays an archive file.
   */
  protected synchronized void displayArchiveContent() {
    List	selFiles;
    File[]	tmpFiles;
    int		i;

    if (m_ListArchiveFiles.getSelectedIndex() < 0)
      return;

    // notify user
    m_PanelContent.displayCreatingView();

    selFiles = m_ListArchiveFiles.getSelectedValuesList();
    tmpFiles = new File[selFiles.size()];
    for (i = 0; i < selFiles.size(); i++) {
      try {
	tmpFiles[i] = File.createTempFile("adams-pb-", "." + FileUtils.getExtension(selFiles.get(i).toString()), TempUtils.getTempDirectory());
	if (!m_ArchiveHandler.extract(selFiles.get(i).toString(), tmpFiles[i])) {
	  ConsolePanel.getSingleton().append(
	      LoggingLevel.SEVERE,
	      "Failed to extract file '" + selFiles.get(i) + "'!");
	  return;
	}
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(
	    LoggingLevel.SEVERE,
	    "Failed to extract file '" + selFiles.get(i) + "':",
	    e);
      }
    }
    m_PanelContent.display(tmpFiles, false);
  }

  /**
   * Updates the preferred handler.
   */
  protected void updatePreferredArchiveHandler() {
    String		ext;
    List<String>	exts;
    String		handler;

    if (m_CurrentFiles == null)
      return;

    if (m_ComboBoxArchiveHandlers.getSelectedIndex() < 0)
      handler = (String) m_ComboBoxArchiveHandlers.getItemAt(0);
    else
      handler = (String) m_ComboBoxArchiveHandlers.getSelectedItem();

    exts = new ArrayList<>();
    for (File file: m_CurrentFiles) {
      ext = FileUtils.getExtension(file);
      if (ext == null)
	continue;
      ext = ext.toLowerCase();
      exts.add(ext);
    }

    PropertiesManager.updatePreferredArchiveHandler(exts.toArray(new String[exts.size()]), handler);
  }

  /**
   * Updates menu and title.
   */
  protected void update() {
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
   * Updates the title.
   */
  protected void updateTitle() {
    if (!m_TitleGenerator.isEnabled())
      return;
    if ((m_CurrentFiles == null) || (m_CurrentFiles.length == 0))
      setParentTitle(m_TitleGenerator.generate());
    else if (m_CurrentFiles.length == 1)
      setParentTitle(m_TitleGenerator.generate(m_CurrentFiles[0]));
    else
      setParentTitle(m_TitleGenerator.generate(m_CurrentFiles[0]) + " ...");
  }

  /**
   * Updates the menu.
   */
  protected void updateMenu() {
    boolean hasNotesForDir;

    hasNotesForDir = (NotesManager.getSingleton().hasNotes(m_PanelDir.getCurrentDirectory()));

    m_MenuItemClearNotesAll.setEnabled(NotesManager.getSingleton().hasNotes());
    m_MenuItemClearNotesDir.setEnabled(hasNotesForDir);
    m_MenuItemViewNotes.setEnabled(hasNotesForDir);
    m_MenuItemSaveNotes.setEnabled(hasNotesForDir);
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
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      // File/Open...
      menuitem = new JMenuItem("Open...");
      menu.add(menuitem);
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.setIcon(ImageManager.getIcon("open_dir.gif"));
      menuitem.addActionListener((ActionEvent e) -> m_PanelDir.choose());

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<>(
	  SESSION_FILE, PropertiesManager.getProperties().getInteger("MaxRecentDirs", 5), submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  open(new PlaceholderDirectory(e.getItem()));
	}
      });
      m_MenuFileLoadRecent = submenu;

      // File/Open file...
      menuitem = new JMenuItem("Open file...");
      menu.add(menuitem);
      menuitem.setMnemonic('f');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed O"));
      menuitem.setIcon(ImageManager.getIcon("open.gif"));
      menuitem.addActionListener((ActionEvent e) -> openFile());

      // File/Reload
      menuitem = new JMenuItem("Reload");
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("F5"));
      menuitem.setIcon(ImageManager.getIcon("refresh.gif"));
      menuitem.addActionListener((ActionEvent e) -> reload());

      // File/Send to
      menu.addSeparator();
      if (SendToActionUtils.addSendToSubmenu(this, menu))
	menu.addSeparator();

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(ImageManager.getIcon("exit.png"));
      menuitem.addActionListener((ActionEvent e) -> closeParent());

      // View
      menu = new JMenu("View");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      // View/Cache views
      menuitem = new JCheckBoxMenuItem("Cache views");
      menuitem.setSelected(PropertiesManager.getProperties().getBoolean("CacheViews", true));
      menu.add(menuitem);
      menuitem.setMnemonic('v');
      menuitem.addActionListener((ActionEvent e) -> updateCacheViews());
      m_MenuItemCacheViews = menuitem;

      // View/Use fixed handler
      menuitem = new JCheckBoxMenuItem("Use fixed handler");
      menuitem.setSelected(PropertiesManager.getProperties().getBoolean("UseFixedHandler", true));
      menu.add(menuitem);
      menuitem.setMnemonic('f');
      menuitem.addActionListener((ActionEvent e) -> updateUseFixedHandler());
      m_MenuItemUseFixedHandler = menuitem;

      // View/Show hidden files
      menuitem = new JCheckBoxMenuItem("Show hidden files");
      menuitem.setSelected(PropertiesManager.getProperties().getBoolean("ShowHiddenFiles", false));
      menu.add(menuitem);
      menuitem.setMnemonic('h');
      menuitem.addActionListener((ActionEvent e) -> updateShowHiddenFiles());
      m_MenuItemShowHiddenFiles = menuitem;

      // View/Show temp files
      menuitem = new JCheckBoxMenuItem("Show temporary files");
      menuitem.setSelected(PropertiesManager.getProperties().getBoolean("ShowTempFiles", false));
      menu.add(menuitem);
      menuitem.setMnemonic('t');
      menuitem.addActionListener((ActionEvent e) -> updateShowTempFiles());
      m_MenuItemShowTempFiles = menuitem;

      // Notes
      menu = new JMenu("Notes");
      result.add(menu);
      menu.setMnemonic('N');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      // Notes/Clear all
      menuitem = new JMenuItem("Clear all");
      menu.add(menuitem);
      menuitem.setMnemonic('g');
      menuitem.addActionListener((ActionEvent e) -> clearNotes(null));
      m_MenuItemClearNotesAll = menuitem;

      // Notes/Clear (current dir)
      menuitem = new JMenuItem("Clear directory");
      menu.add(menuitem);
      menuitem.setMnemonic('l');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed N"));
      menuitem.setIcon(ImageManager.getIcon("new.gif"));
      menuitem.addActionListener((ActionEvent e) -> clearNotes(m_PanelDir.getCurrentDirectory()));
      m_MenuItemClearNotesDir = menuitem;

      // Notes/View
      menuitem = new JMenuItem("View...");
      menu.add(menuitem);
      menuitem.setMnemonic('V');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed V"));
      menuitem.setIcon(ImageManager.getIcon("editor.gif"));
      menuitem.addActionListener((ActionEvent e) -> viewNotes());
      m_MenuItemViewNotes = menuitem;

      // Notes/Save
      menuitem = new JMenuItem("Save as...");
      menu.add(menuitem);
      menuitem.setMnemonic('S');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed S"));
      menuitem.setIcon(ImageManager.getIcon("save.gif"));
      menuitem.addActionListener((ActionEvent e) -> saveNotes());
      m_MenuItemSaveNotes = menuitem;

      // Window
      menu = new JMenu("Window");
      result.add(menu);
      menu.setMnemonic('W');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      // Window/New window
      menuitem = new JMenuItem("New window");
      menu.add(menuitem);
      menuitem.setMnemonic('N');
      menuitem.addActionListener((ActionEvent e) -> newWindow());

      menu.addSeparator();

      // Window/Half width
      menuitem = new JMenuItem("Half width");
      menu.add(menuitem);
      menuitem.setMnemonic('i');
      menuitem.addActionListener((ActionEvent e) -> GUIHelper.makeHalfScreenWidth(PreviewBrowserPanel.this));

      // Window/Half height
      menuitem = new JMenuItem("Half height");
      menu.add(menuitem);
      menuitem.setMnemonic('g');
      menuitem.addActionListener((ActionEvent e) -> GUIHelper.makeHalfScreenHeight(PreviewBrowserPanel.this));

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
   * Returns a popup menu for the local files.
   *
   * @param e		the event that triggered the popup menu
   * @return		the menu
   */
  protected JPopupMenu getLocalFilesPopupMenu(MouseEvent e) {
    JPopupMenu			result;
    JMenuItem			menuitem;
    Class[]			classes;
    AbstractLocalFilesAction	local;
    final Object 		obj;
    final File 			file;

    result = new JPopupMenu();

    if (m_ListLocalFiles.getSelectedIndices().length == 1)
      obj = m_ListLocalFiles.getSelectedValue();
    else
      obj = null;
    file = new PlaceholderFile(m_PanelDir.getCurrent() + File.separator + obj);

    menuitem = new JMenuItem("Copy name");
    menuitem.setEnabled(obj != null);
    menuitem.addActionListener((ActionEvent ae) -> ClipboardHelper.copyToClipboard("" + obj));
    result.add(menuitem);

    menuitem = new JMenuItem("Copy path");
    menuitem.setEnabled(obj != null);
    menuitem.addActionListener((ActionEvent ae) -> ClipboardHelper.copyToClipboard(file.getAbsolutePath()));
    result.add(menuitem);

    result.addSeparator();

    if (SendToActionUtils.addSendToSubmenu(this, result))
      result.addSeparator();

    menuitem = new JMenuItem("Edit note...");
    menuitem.setEnabled(obj != null);
    menuitem.addActionListener((ActionEvent ae) -> {
      String initial = "";
      if (hasNote(file))
	initial = getNote(file);
      String comment = GUIHelper.showInputDialog(this, "Please enter note for '" + obj + "' (Ctrl+Enter for new line): ", initial, "Edit note", null, 30, 4);
      if (comment == null)
	return;
      setNote(file, comment);
    });
    result.add(menuitem);

    menuitem = new JMenuItem("Remove note");
    menuitem.setEnabled((obj != null) && hasNote(file));
    menuitem.addActionListener((ActionEvent ae) -> {
      String note = getNote(file);
      int retVal = GUIHelper.showConfirmMessage(this, "Do you want to remove the note for '" + obj + "'?\n" + note);
      if (retVal != ApprovalDialog.APPROVE_OPTION)
	return;
      removeNote(file);
    });
    result.add(menuitem);

    // initialize actions
    if (m_LocalFilesActions == null) {
      m_LocalFilesActions = new ArrayList<>();
      classes             = ClassLister.getSingleton().getClasses(AbstractLocalFilesAction.class);
      for (Class cls: classes) {
	try {
	  local = (AbstractLocalFilesAction) cls.getDeclaredConstructor().newInstance();
	  local.setOwner(this);
	  m_LocalFilesActions.add(local);
	}
	catch (Exception ex) {
	  ConsolePanel.getSingleton().append("Failed to instantiate local files action: " + Utils.classToString(cls), ex);
	}
      }
      Collections.sort(m_LocalFilesActions, (AbstractLocalFilesAction o1, AbstractLocalFilesAction o2) -> {
	return o1.getName().compareToIgnoreCase(o2.getName());
      });
    }

    result.addSeparator();
    for (AbstractLocalFilesAction action: m_LocalFilesActions) {
      action.update();
      result.add(action);
    }

    return result;
  }

  /**
   * Opens the specified directory.
   *
   * @param dir		the directory to display
   */
  public void open(final PlaceholderDirectory dir) {
    SwingUtilities.invokeLater(() -> {
      m_PanelDir.setCurrent(dir);
      m_PanelDir.fireCurrentValueChanged();
    });
  }

  /**
   * Lets the user select a file to preview.
   */
  public void openFile() {
    int		retVal;

    retVal = m_FileChooser.showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    open(m_FileChooser.getSelectedPlaceholderFile());
  }

  /**
   * Displays the specified file.
   *
   * @param file	the file to display
   */
  public void open(final PlaceholderFile file) {
    SwingUtilities.invokeLater(() -> {
      m_PanelDir.setCurrent(file.getParentFile());
      m_PanelDir.fireCurrentValueChanged();
    });
    SwingUtilities.invokeLater(() -> m_ListLocalFiles.setSelectedValue(file.getName(), true));
    SwingUtilities.invokeLater(() -> m_PanelContent.display(new File[]{file}, true));
  }

  /**
   * Reloads the directory and content.
   */
  public void reload() {
    List	selected;

    selected = m_ListLocalFiles.getSelectedValuesList();
    open(new PlaceholderDirectory(m_PanelDir.getCurrent()));
    if (selected.size() > 0)
      m_ListLocalFiles.setSelectedValue(selected.get(0), true);
  }

  /**
   * Clears the preview.
   */
  public void clear() {
    m_CurrentFiles = new File[0];
    m_PanelContent.displayView(new NoPreviewAvailablePanel());
  }

  /**
   * Displays a new preview window/frame.
   *
   * @return		the new panel
   */
  public PreviewBrowserPanel newWindow() {
    PreviewBrowserPanel result;
    ChildFrame 		oldFrame;
    ChildFrame 		newFrame;

    result    = null;
    oldFrame = (ChildFrame) GUIHelper.getParent(this, ChildFrame.class);
    if (oldFrame != null) {
      newFrame = oldFrame.getNewWindow();
      newFrame.setVisible(true);
      result  = (PreviewBrowserPanel) newFrame.getContentPane().getComponent(0);
    }

    if (result != null) {
      // use same directory
      if (!result.getDirPanel().getCurrent().equals(getDirPanel().getCurrent()))
	result.getDirPanel().setCurrent(getDirPanel().getCurrent());
      // search
      if (!result.getLocalSearch().equals(getLocalSearch()))
	result.setLocalSearch(getLocalSearch());
      if (!result.getArchiveSearch().equals(getArchiveSearch()))
	result.setArchiveSearch(getArchiveSearch());
      // view
      if (result.getUseFixedContentHandler() != getUseFixedContentHandler())
	result.setUseFixedContentHandler(getUseFixedContentHandler());
      if (result.getReusePreviews() != getReusePreviews())
	result.setReusePreviews(getReusePreviews());
      if (result.getShowHiddenFiles() != getShowHiddenFiles())
      result.setShowHiddenFiles(getShowHiddenFiles());
      if (result.getShowTempFiles() != getShowTempFiles())
	result.setShowTempFiles(getShowTempFiles());
    }

    if (result != null)
      result.reload();

    return result;
  }

  /**
   * Returns the currently selected files.
   *
   * @return		the files, can be null
   */
  public File[] getCurrentFiles() {
    return m_CurrentFiles;
  }

  /**
   * Returns the directory panel.
   *
   * @return		the panel
   */
  public DirectoryChooserPanel getDirPanel() {
    return m_PanelDir;
  }

  /**
   * Returns the list component for files in the archive.
   *
   * @return		the list component
   */
  public SearchableBaseList getLocalFilesList() {
    return m_ListLocalFiles;
  }

  /**
   * Returns whether the archive files are visible.
   *
   * @return		true if visible
   */
  public boolean isArchiveFilesListVisible() {
    return !m_PaneBrowsing.isBottomComponentHidden();
  }

  /**
   * Returns the list component for files in the archive.
   *
   * @return		the list component
   */
  public SearchableBaseList getArchiveFilesList() {
    return m_ListArchiveFiles;
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  public Class[] getSendToClasses() {
    if ((m_PanelContent.getComponent() instanceof JTable) || (m_PanelContent.getComponent() instanceof JTableSupporter))
      return new Class[]{PlaceholderFile.class, JComponent.class, JTable.class};
    else
      return new Class[]{PlaceholderFile.class, JComponent.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve an item for
   * @return		true if an object is available for sending
   */
  public boolean hasSendToItem(Class[] cls) {
    return (m_CurrentFiles != null) && (m_CurrentFiles.length > 0);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  public Object getSendToItem(Class[] cls) {
    Object	result;
    File	file;
    JTable	table;

    result = null;
    file   = null;
    if ((m_CurrentFiles != null) && (m_CurrentFiles.length > 0))
      file = m_CurrentFiles[0];

    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      if (file != null) {
	result = new PlaceholderFile(file);
      }
    }
    else {
      table = null;
      if (m_PanelContent.getComponent() instanceof JTable)
	table = (JTable) m_PanelContent.getComponent();
      else if (m_PanelContent.getComponent() instanceof JTableSupporter)
	table = ((JTableSupporter) m_PanelContent.getComponent()).getTable();
      if ((table != null) && SendToActionUtils.isAvailable(JTable.class, cls)) {
	result = m_PanelContent.getComponent();
      }
      else if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
	result = m_PanelContent.getComponent();
      }
    }

    return result;
  }

  /**
   * Sets whether the browsing panel is hidden or not.
   *
   * @param value	true to enabled browsing, false to hide panel
   */
  public void setBrowsingEnabled(boolean value) {
    m_SplitPane.setLeftComponentHidden(!value);
  }

  /**
   * Returns whether the browsing panel is hidden or not.
   *
   * @return		true if browsing enabled
   */
  public boolean isBrowsingEnabled() {
    return !m_SplitPane.isLeftComponentHidden();
  }

  /**
   * Returns the search term of the local file search.
   *
   * @return		the search term
   */
  public String getLocalSearch() {
    return m_SearchLocalFiles.getSearchText();
  }

  /**
   * Sets the search term for the local file search.
   *
   * @param search	the search term
   */
  public void setLocalSearch(String search) {
    m_SearchLocalFiles.setSearchText(search);
    m_SearchLocalFiles.search();
  }

  /**
   * Sets the text for the archive file search.
   *
   * @param search	the search term
   */
  public void setArchiveSearch(String search) {
    m_SearchArchiveFiles.setSearchText(search);
    m_SearchArchiveFiles.search();
  }

  /**
   * Returns the search term of the archive file search.
   *
   * @return		the search term
   */
  public String getArchiveSearch() {
    return m_SearchArchiveFiles.getSearchText();
  }

  /**
   * Returns the content panel.
   *
   * @return		the content panel
   */
  public PreviewDisplay getContentPanel() {
    return m_PanelContent;
  }

  /**
   * Sets the note for the file.
   *
   * @param file	the file to set the note for
   * @param note	the note to set
   */
  protected void setNote(File file, String note) {
    NotesManager.getSingleton().addNote(file.getParentFile().getAbsolutePath(), file.getName(), note);
  }

  /**
   * Returns the note for the file.
   *
   * @param file	the file to set the note for
   * @return		the note, null if none available
   */
  protected String getNote(File file) {
    return NotesManager.getSingleton().getNote(file.getParentFile().getAbsolutePath(), file.getName());
  }

  /**
   * Removes the note for the file.
   *
   * @param file	the file to remove the note for
   */
  protected void removeNote(File file) {
    NotesManager.getSingleton().removeNote(file.getParentFile().getAbsolutePath(), file.getName());
  }

  /**
   * Checks whether a note is present for the file.
   *
   * @param file	the file to check
   * @return		true if note present
   */
  protected boolean hasNote(File file) {
    return NotesManager.getSingleton().hasNote(file.getParentFile().getAbsolutePath(), file.getName());
  }

  /**
   * Clears the notes for the dir or global if dir is null.
   *
   * @param dir		the dir to clear the notes for, null for global
   */
  protected void clearNotes(String dir) {
    int 	retVal;

    if (dir == null) {
      retVal = GUIHelper.showConfirmMessage(this, "Do you want to remove the notes for ALL directories?");
      if (retVal != ApprovalDialog.APPROVE_OPTION)
	return;
      NotesManager.getSingleton().clear();
    }
    else {
      retVal = GUIHelper.showConfirmMessage(this, "Do you want to remove the notes for " + m_PanelDir.getCurrentDirectory() + "?");
      if (retVal != ApprovalDialog.APPROVE_OPTION)
	return;
      NotesManager.getSingleton().clear(dir);
    }
  }

  /**
   * Displays the notes.
   */
  protected void viewNotes() {
    TextDialog		dialog;
    Map<String,String> 	notes;
    StringBuilder	notesStr;
    List<String>	keys;

    notes = NotesManager.getSingleton().getNotes(m_PanelDir.getCurrentDirectory());
    notesStr = new StringBuilder();
    keys = new ArrayList<>(notes.keySet());
    Collections.sort(keys);
    for (String key: keys) {
      notesStr.append(key).append("\n");
      notesStr.append(Utils.indent(notes.get(key), 4)).append("\n\n");
    }

    if (getParentDialog() != null)
      dialog = new TextDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new TextDialog(getParentFrame(), false);
    dialog.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
    dialog.setUpdateParentTitle(false);
    dialog.setDialogTitle("Notes");
    dialog.setTitle("Notes");
    dialog.setContent(notesStr.toString());
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Allows the user to save the current notes.
   */
  protected void saveNotes() {
    int			retVal;
    String		msg;
    AbstractNotesWriter	writer;

    if (m_FileChooserNotes == null)
      m_FileChooserNotes = new NotesFileChooser();

    retVal = m_FileChooserNotes.showSaveDialog(this);
    if (retVal != TextFileChooser.APPROVE_OPTION)
      return;

    writer = m_FileChooserNotes.getWriter();
    msg = writer.write(
      NotesManager.getSingleton().getNotes(m_PanelDir.getCurrentDirectory()),
      m_FileChooserNotes.getSelectedPlaceholderFile());
    if (msg != null)
      GUIHelper.showErrorMessage(this, "Failed to save notes to: " + m_FileChooserNotes.getSelectedFile() + "\n" + msg);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_PanelContent.cleanUp();
  }
}
