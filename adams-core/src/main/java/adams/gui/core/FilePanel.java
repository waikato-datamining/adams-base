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
 * FilePanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

import adams.core.base.BaseRegExp;
import adams.core.io.FileObject;
import adams.core.io.FileObjectComparator;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.lister.DirectoryLister;
import adams.core.io.lister.LocalDirectoryLister;
import adams.core.io.lister.RecursiveDirectoryLister;
import adams.env.Environment;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.SearchEvent;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Displays files and directories.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FilePanel
  extends BasePanel
  implements ListSelectionListener {

  private static final long serialVersionUID = -3704016792074293012L;

  /**
   * Event for double clicks on files.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FileDoubleClickEvent
    extends EventObject {

    private static final long serialVersionUID = 6602862166542558303L;

    /** the file that got double clicked. */
    protected File m_File;

    /**
     * Initializes the event.
     *
     * @param source	the panel that triggered the event
     * @param file	the file that got double clicked
     */
    public FileDoubleClickEvent(FilePanel source, File file) {
      super(source);
      m_File = file;
    }

    /**
     * Returns the file panel that triggered the event.
     *
     * @return		the panel
     */
    public FilePanel getFilePanel() {
      return (FilePanel) getSource();
    }

    /**
     * Returns the file that got double-clicked.
     *
     * @return		the file
     */
    public File getFile() {
      return m_File;
    }
  }

  /**
   * Interface for classes that listen to double clicks on files.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public interface FileDoubleClickListener {

    /**
     * Gets called when a file got double-clicked.
     *
     * @param e		the event
     */
    public void fileDoubleClicked(FileDoubleClickEvent e);
  }

  /** whether to show details on the files. */
  protected Boolean m_ShowDetails;

  /** whether to allow multiple files to be selected. */
  protected boolean m_MultiSelection;

  /** whether to show hidden files. */
  protected boolean m_ShowHidden;

  /** for listing the content of the directory. */
  protected DirectoryLister m_Lister;

  /** the comparator. */
  protected FileObjectComparator m_Comparator;

  /** the currently listed files. */
  protected List<FileObject> m_Files;

  /** the currently running swingworker. */
  protected SwingWorker m_Worker;

  /** whether to ignore changes. */
  protected boolean m_IgnoreChanges;

  /** the List for the non-detailed view. */
  protected SearchableBaseList m_List;

  /** the table for the detailed view. */
  protected SortableAndSearchableTable m_Table;

  /** the search panel. */
  protected SearchPanel m_PanelSearch;

  /** the listeners for when the directory gets updated. */
  protected Set<ChangeListener> m_DirectoryChangeListeners;

  /** the listeners for when the files get updated. */
  protected Set<ChangeListener> m_FilesChangeListeners;

  /** the listeners for when the selection changes. */
  protected Set<ChangeListener> m_SelectionChangeListeners;

  /** the double click listener for a file. */
  protected Set<FileDoubleClickListener> m_FileDoubleClickListeners;

  /** the focus adapter. */
  protected FocusAdapter m_FocusAdapter;

  /** the key adapter. */
  protected KeyAdapter m_KeyAdapter;

  /** the mouse adapter. */
  protected MouseAdapter m_MouseAdapter;

  /**
   * Initializes the panel.
   *
   * @param showDetails		whether to show details like DIR and size
   */
  public FilePanel(boolean showDetails) {
    this(showDetails, new LocalDirectoryLister());
  }

  /**
   * Initializes the panel.
   *
   * @param showDetails		whether to show details like DIR and size
   */
  public FilePanel(boolean showDetails, DirectoryLister lister) {
    super();
    m_ShowDetails = showDetails;
    m_Lister      = lister;
    initialize();
    initGUI();
    finishInit();
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    if (m_ShowDetails == null)
      return;

    super.initialize();

    m_IgnoreChanges = false;
    m_ShowHidden    = false;

    m_Lister = new LocalDirectoryLister();
    m_Lister.setMaxItems(-1);
    ((RecursiveDirectoryLister) m_Lister).setRecursive(false);

    m_Comparator = new FileObjectComparator(false, true, false);

    m_Files = new ArrayList<>();

    m_DirectoryChangeListeners = new HashSet<>();
    m_FilesChangeListeners     = new HashSet<>();
    m_SelectionChangeListeners = new HashSet<>();
    m_FileDoubleClickListeners = new HashSet<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    if (m_ShowDetails == null)
      return;

    super.initGUI();

    setLayout(new BorderLayout(5, 5));

    m_FocusAdapter = new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
	notifySelectionChangeListeners();
      }
    };

    m_KeyAdapter = new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if (e.getKeyCode() == KeyEvent.VK_ENTER) {
	  File file = getSelectedFile(false);
	  if (file != null) {
	    if (file.isDirectory()) {
	      e.consume();
	      setCurrentDir(file.getAbsolutePath());
	    }
	  }
	}
	if (!e.isConsumed())
	  super.keyPressed(e);
      }
    };

    m_MouseAdapter = new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (MouseUtils.isDoubleClick(e)) {
	  FileObject wrapper = getSelectedWrapper(false);
	  if (wrapper != null) {
	    File file = wrapper.getActualFile();
	    if (file.isDirectory()) {
	      e.consume();
	      if (file.getName().equals(".."))
		setCurrentDir(new PlaceholderDirectory(getCurrentDir()).getParentFile().getAbsolutePath());
	      else
		setCurrentDir(file.getAbsolutePath());
	    }
	    else {
              notifyFileDoubleClickListeners(new FileDoubleClickEvent(FilePanel.this, file));
            }
	  }
	}
	if (!e.isConsumed())
	  super.mouseClicked(e);
      }
    };

    if (m_ShowDetails) {
      m_Table = new SortableAndSearchableTable(new FileWrapperTableModel(new ArrayList<>(), false));
      m_Table.setUseOptimalColumnWidths(true);
      m_Table.getSelectionModel().addListSelectionListener(this);
      m_Table.addFocusListener(m_FocusAdapter);
      m_Table.addKeyListener(m_KeyAdapter);
      m_Table.addMouseListener(m_MouseAdapter);
      add(new BaseScrollPane(m_Table), BorderLayout.CENTER);

      m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, false);
      m_PanelSearch.setVisible(false);
      m_PanelSearch.addSearchListener((SearchEvent e) ->
	m_Table.search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));
      add(m_PanelSearch, BorderLayout.SOUTH);
    }
    else {
      m_List = new SearchableBaseList();
      m_List.addListSelectionListener(this);
      m_List.addFocusListener(m_FocusAdapter);
      m_List.addKeyListener(m_KeyAdapter);
      m_List.addMouseListener(m_MouseAdapter);
      add(new BaseScrollPane(m_List), BorderLayout.CENTER);

      m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, false);
      m_PanelSearch.setVisible(false);
      m_PanelSearch.addSearchListener((SearchEvent e) ->
	m_List.search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));
      add(m_PanelSearch, BorderLayout.SOUTH);
    }
  }

  /**
   * Finalizes the initialization.
   */
  @Override
  protected void finishInit() {
    if (m_ShowDetails == null)
      return;

    super.finishInit();

    setMultiSelection(false);
    update();
  }

  /**
   * Sets the directory lister to use.
   *
   * @param value	the lister
   */
  public void setDirectoryLister(DirectoryLister value) {
    m_Lister = value;
    update();
    notifyDirectoryChangeListeners();
  }

  /**
   * Returns the current directory lister.
   *
   * @return		the lister
   */
  public DirectoryLister getDirectoryLister() {
    return m_Lister;
  }

  /**
   * Sets the directory to list.
   *
   * @param value	the directory
   */
  public void setCurrentDir(String value) {
    m_Lister.setWatchDir(value);
    update();
    notifyDirectoryChangeListeners();
  }

  /**
   * Returns the current directory.
   *
   * @return		the directory
   */
  public String getCurrentDir() {
    return m_Lister.getWatchDir();
  }

  /**
   * Sets whether to list directories.
   *
   * @param value	true if to list directories
   */
  public void setListDirs(boolean value) {
    m_Lister.setListDirs(value);
    update();
  }

  /**
   * Returns whether directories are listed.
   *
   * @return		true if to list directories
   */
  public boolean getListDirs() {
    return m_Lister.getListDirs();
  }

  /**
   * Sets whether to list files.
   *
   * @param value	true if to list files
   */
  public void setListFiles(boolean value) {
    m_Lister.setListFiles(value);
    update();
  }

  /**
   * Returns whether files are listed.
   *
   * @return		true if to list files
   */
  public boolean getListFiles() {
    return m_Lister.getListFiles();
  }

  /**
   * Sets the filter to use.
   *
   * @param value	the filter
   */
  public void setFilter(BaseRegExp value) {
    m_Lister.setRegExp(value);
    update();
  }

  /**
   * Return the filter in use.
   *
   * @return		the filter
   */
  public BaseRegExp getFilter() {
    return m_Lister.getRegExp();
  }

  /**
   * Sets whether to use case-sensitive comparison.
   *
   * @param value	true if case-sensitive
   */
  public void setCaseSensitive(boolean value) {
    m_Comparator = new FileObjectComparator(value, m_Comparator.isListDirsFirst(), m_Comparator.isIncludeParentDirs());
    update();
  }

  /**
   * Returns whether comparison is case-sensitive.
   *
   * @return		true if case-sensitive
   */
  public boolean isCaseSensitive() {
    return m_Comparator.isCaseSensitive();
  }

  /**
   * Sets whether to list directories first.
   *
   * @param value	true if to list directories first
   */
  public void setListDirsFirst(boolean value) {
    m_Comparator = new FileObjectComparator(m_Comparator.isCaseSensitive(), value, m_Comparator.isIncludeParentDirs());
    update();
  }

  /**
   * Returns whether to list directories first.
   *
   * @return		true if to list dirs first
   */
  public boolean isListDirsFirst() {
    return m_Comparator.isListDirsFirst();
  }

  /**
   * Sets whether to use include the parent directories in the comparison.
   *
   * @param value	true if to include parent directories
   */
  public void setIncludeParentDirs(boolean value) {
    m_Comparator = new FileObjectComparator(m_Comparator.isCaseSensitive(), m_Comparator.isListDirsFirst(), value);
    update();
  }

  /**
   * Returns whether to include parent directories in the comparison.
   *
   * @return		true if included
   */
  public boolean isIncludeParentDirs() {
    return m_Comparator.isIncludeParentDirs();
  }

  /**
   * Sets whether to show hidden files or not.
   *
   * @param value	true if to show
   */
  public void setShowHidden(boolean value) {
    m_ShowHidden = value;
    update();
  }

  /**
   * Returns whether to show hidden files or not.
   *
   * @return		true if to show
   */
  public boolean isShowHidden() {
    return m_ShowHidden;
  }

  /**
   * Sets whether to allow multi-selection.
   *
   * @param value	true if to allow
   */
  public void setMultiSelection(boolean value) {
    m_MultiSelection = value;
    if (m_Table != null) {
      if (value)
	m_Table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      else
	m_Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    else {
      if (value)
	m_List.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      else
	m_List.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
  }

  /**
   * Returns whether multi-selection is enabled.
   *
   * @return		true if enabled
   */
  public boolean isMultiSelection() {
    return m_MultiSelection;
  }

  /**
   * Returns the currently selected file wrapper, if any.
   *
   * @param skipDotDot	whether to skip the dot dot file
   * @return		the file wrapper, null if none selected
   */
  protected FileObject getSelectedWrapper(boolean skipDotDot) {
    FileObject wrapper;

    wrapper = null;
    if (m_Table != null) {
      if (m_Table.getSelectedRow() > -1)
	wrapper = m_Files.get(m_Table.getActualRow(m_Table.getSelectedRow()));
    }
    else if (m_List != null) {
      if (m_List.getSelectedIndex() > -1)
	wrapper = m_Files.get(m_List.getSelectedIndex());
    }

    if (wrapper != null) {
      if (wrapper.getName().equals("..") && skipDotDot)
	return null;
      else
	return wrapper;
    }
    else {
      return null;
    }
  }

  /**
   * Returns the currently selected file, if any.
   *
   * @param skipDotDot	whether to skip the dot dot file
   * @return		the file, null if none selected
   */
  protected File getSelectedFile(boolean skipDotDot) {
    FileObject wrapper;

    wrapper = getSelectedWrapper(skipDotDot);
    if (wrapper != null)
      return wrapper.getFile();
    else
      return null;
  }

  /**
   * Returns the currently selected file, if any.
   *
   * @return		the file, null if none selected
   */
  public File getSelectedFile() {
    return getSelectedFile(true);
  }

  /**
   * Returns the currently selected files, if any.
   *
   * @return		the files
   */
  public File[] getSelectedFiles() {
    List<File>		result;
    FileObject wrapper;

    result = new ArrayList<>();
    if (m_Table != null) {
      for (int index: m_Table.getSelectedRows()) {
	wrapper = m_Files.get(m_Table.getActualRow(index));
	if (wrapper.getName().equals(".."))
	  continue;
	result.add(wrapper.getFile());
      }
    }
    else if (m_List != null) {
      for (int index: m_List.getSelectedIndices()){
	wrapper = m_Files.get(index);
	if (wrapper.getName().equals(".."))
	  continue;
	result.add(wrapper.getFile());
      }
    }

    return result.toArray(new File[result.size()]);
  }

  /**
   * Clears the selection.
   */
  public void clearSelection() {
    if (m_Table != null)
      m_Table.clearSelection();
    else
      m_List.clearSelection();
  }

  /**
   * Allows the update of several parameters without triggering an update
   * each time.
   */
  public void startUpdate() {
    m_IgnoreChanges = true;
  }

  /**
   * Triggers an update after updating several parameters.
   */
  public void finishUpdate() {
    m_IgnoreChanges = false;
    update();
  }

  /**
   * Updates the view.
   */
  protected void update() {
    if (m_IgnoreChanges)
      return;

    m_Worker = new SwingWorker() {
      protected List<FileObject> files = new ArrayList<>();
      @Override
      protected Object doInBackground() throws Exception {
	for (FileObject wrapper: m_Lister.listObjects()) {
	  if (!m_ShowHidden && wrapper.isHidden())
	    continue;
	  files.add(wrapper);
	}
	Collections.sort(files, m_Comparator);
	if (m_Lister.hasParentDirectory())
	  files.add(0, m_Lister.newDirectory(".."));
	return null;
      }
      @Override
      protected void done() {
	super.done();
	m_Files = files;
	updateGUI();
	m_Worker = null;
      }
    };
    m_Worker.execute();
  }

  /**
   * Updates the GUI.
   */
  protected void updateGUI() {
    FileWrapperTableModel	tableModel;
    FileWrapperListModel	listModel;

    if (m_ShowDetails) {
      tableModel = new FileWrapperTableModel(m_Files, isIncludeParentDirs());
      m_Table.setModel(tableModel);
      m_Table.setOptimalColumnWidth();
    }
    else {
      listModel = new FileWrapperListModel(m_Files, isIncludeParentDirs());
      m_List.setModel(listModel);
    }

    notifyFilesChangeListeners();
  }

  /**
   * Updates the files (if not busy).
   */
  public void reload() {
    update();
  }

  /**
   * Sets the visibility state of the search box.
   * NB: only available when details are shown using a table.
   *
   * @param value	true if visible
   */
  public void setSearchVisible(boolean value) {
    m_PanelSearch.setVisible(value);
    if (!value) {
      if (m_Table != null)
	m_Table.search(null, false);
      else
	m_List.search(null, false);
    }
  }

  /**
   * Returns whether the search box is visible.
   *
   * @return		true of visible
   */
  public boolean isSearchVisible() {
    return m_PanelSearch.isVisible();
  }

  /**
   * Checks whether we're currently busy.
   *
   * @return		true if busy
   */
  public boolean isBusy() {
    return (m_Worker != null);
  }

  /**
   * Adds the listener to the list of listeners that get notified when
   * the directory changes.
   *
   * @param l		the listener to add
   */
  public void addDirectoryChangeListener(ChangeListener l) {
    m_DirectoryChangeListeners.add(l);
  }

  /**
   * Removes the listener from the list of listeners that get notified when
   * the directory changes.
   *
   * @param l		the listener to add
   */
  public void removeDirectoryChangeListener(ChangeListener l) {
    m_DirectoryChangeListeners.remove(l);
  }

  /**
   * Notifies the listeners when the directory has changed.
   */
  protected synchronized void notifyDirectoryChangeListeners() {
    ChangeEvent		e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_DirectoryChangeListeners)
      l.stateChanged(e);
  }

  /**
   * Adds the listener to the list of listeners that get notified when
   * the files change.
   *
   * @param l		the listener to add
   */
  public void addFilesChangeListener(ChangeListener l) {
    m_FilesChangeListeners.add(l);
  }

  /**
   * Removes the listener from the list of listeners that get notified when
   * the files change.
   *
   * @param l		the listener to add
   */
  public void removeFilesChangeListener(ChangeListener l) {
    m_FilesChangeListeners.remove(l);
  }

  /**
   * Notifies the listeners when the files have changed.
   */
  protected synchronized void notifyFilesChangeListeners() {
    ChangeEvent		e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_FilesChangeListeners)
      l.stateChanged(e);
  }

  /**
   * Adds the listener to the list of listeners that get notified when
   * the selection changes.
   *
   * @param l		the listener to add
   */
  public void addSelectionChangeListener(ChangeListener l) {
    m_SelectionChangeListeners.add(l);
  }

  /**
   * Removes the listener from the list of listeners that get notified when
   * the selection changes.
   *
   * @param l		the listener to add
   */
  public void removeSelectionChangeListener(ChangeListener l) {
    m_SelectionChangeListeners.remove(l);
  }

  /**
   * Notifies the listeners when the selection has changed.
   */
  protected synchronized void notifySelectionChangeListeners() {
    ChangeEvent		e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_SelectionChangeListeners)
      l.stateChanged(e);
  }

  /**
   * Gets called when the list selection changes.
   *
   * @param e		the event
   */
  @Override
  public void valueChanged(ListSelectionEvent e) {
    notifySelectionChangeListeners();
  }

  /**
   * Adds the listener to the list of listeners that get notified when
   * a file gets double-clicked.
   *
   * @param l		the listener to add
   */
  public void addFileDoubleClickListener(FileDoubleClickListener l) {
    m_FileDoubleClickListeners.add(l);
  }

  /**
   * Removes the listener from the list of listeners that get notified when
   * a file gets double-clicked.
   *
   * @param l		the listener to add
   */
  public void removeFileDoubleClickListener(FileDoubleClickListener l) {
    m_FileDoubleClickListeners.remove(l);
  }

  /**
   * Notifies the listeners when a file got double-clicked.
   */
  protected synchronized void notifyFileDoubleClickListeners(FileDoubleClickEvent e) {
    for (FileDoubleClickListener l: m_FileDoubleClickListeners)
      l.fileDoubleClicked(e);
  }

  /**
   * Just for testing.
   *
   * @param args	the initial directory
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    FilePanel simple = new FilePanel(false);
    simple.setSearchVisible(true);
    simple.addFilesChangeListener((ChangeEvent e) -> {
      System.out.println("simple: files changed");
    });
    simple.addSelectionChangeListener((ChangeEvent e) -> {
      System.out.println("simple: selected files=" + simple.getSelectedFiles().length);
    });
    FilePanel details = new FilePanel(true);
    details.setSearchVisible(true);
    details.addFilesChangeListener((ChangeEvent e) -> {
      System.out.println("details: files changed");
    });
    details.addSelectionChangeListener((ChangeEvent e) -> {
      System.out.println("details: selected files=" + details.getSelectedFiles().length);
    });
    details.startUpdate();
    details.setListDirs(true);
    details.setShowHidden(false);
    if (args.length > 0)
      details.setCurrentDir(args[0]);
    BaseFrame frame = new BaseFrame("Files");
    frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new GridLayout(1, 2));
    frame.getContentPane().add(simple);
    frame.getContentPane().add(details);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    details.finishUpdate();
  }
}
