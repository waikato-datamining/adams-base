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

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.base.BaseRegExp;
import adams.core.io.DirectoryLister;
import adams.core.io.PlaceholderDirectory;
import adams.env.Environment;
import com.googlecode.jfilechooserbookmarks.gui.BaseScrollPane;

import javax.swing.AbstractListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
   * Wraps a file and avoids costly API calls by caching values.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FileWrapper
    implements Serializable {

    private static final long serialVersionUID = -9056432057204433829L;

    /** the wrapped file. */
    protected File m_File;

    /** the length. */
    protected long m_Length;

    /** whether a directory. */
    protected boolean m_Directory;

    /** the modified date. */
    protected Date m_LastModified;

    /**
     * Initializes the wrapper.
     *
     * @param file	the file to wrap
     */
    public FileWrapper(File file) {
      m_File         = file;
      m_Directory    = m_File.isDirectory();
      m_Length       = m_File.length();
      m_LastModified = new Date(m_File.lastModified());
    }

    /**
     * Returns the wrapped file.
     *
     * @return		the file
     */
    public File getFile() {
      return m_File;
    }

    /**
     * Returns the file name.
     *
     * @return		the name
     */
    public String getName() {
      return m_File.getName();
    }

    /**
     * Returns the size of the file.
     *
     * @return		the size
     */
    public long getLength() {
      return m_Length;
    }

    /**
     * Returns whether the file represents a directory.
     *
     * @return		true if directory
     */
    public boolean isDirectory() {
      return m_Directory;
    }

    /**
     * Returns the date when the file was last modified.
     *
     * @return		date when last modified
     */
    public Date getLastModified() {
      return m_LastModified;
    }

    /**
     * Returns just the file's string representation.
     *
     * @return		the string representation
     */
    @Override
    public String toString() {
      return m_File.toString();
    }
  }

  /**
   * Comparator for {@link adams.gui.core.FilePanel.FileWrapper} objects.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FileWrapperComparator
    implements Serializable, Comparator<FileWrapper> {

    private static final long serialVersionUID = -4630678890271018588L;

    /** whether to perform case-sensitive comparisons. */
    protected boolean m_CaseSensitive;

    /** whether to list directoris first. */
    protected boolean m_ListDirsFirst;

    /** whether to include parent directories in sorting. */
    protected boolean m_IncludeParentDirs;

    /**
     * Initializes the comparator.
     *
     * @param caseSensitive	true if to perform case-sensitive comparisons
     * @param listDirsFirst	whether to list directories first or to mix
     *                          them in with files
     * @param includeParentDirs	whether to include parent directories in the comparison
     */
    public FileWrapperComparator(boolean caseSensitive, boolean listDirsFirst, boolean includeParentDirs) {
      m_CaseSensitive     = caseSensitive;
      m_ListDirsFirst     = listDirsFirst;
      m_IncludeParentDirs = includeParentDirs;
    }

    /**
     * Returns whether comparison is case-sensitive.
     *
     * @return		true if case-sensitive
     */
    public boolean isCaseSensitive() {
      return m_CaseSensitive;
    }

    /**
     * Returns whether to list directories first.
     *
     * @return		true if to list dirs first
     */
    public boolean isListDirsFirst() {
      return m_ListDirsFirst;
    }

    /**
     * Returns whether to include parent directories in the comparison.
     *
     * @return		true if included
     */
    public boolean isIncludeParentDirs() {
      return m_IncludeParentDirs;
    }

    /**
     * Compares the two file wrappers.
     *
     * @param o1	the first wrapper
     * @param o2	the second wrapper
     * @return		less than, equal to or greater than zero if the first
     * 			wrapper is less than, equal to or greater than the
     * 			second one.
     */
    @Override
    public int compare(FileWrapper o1, FileWrapper o2) {
      int	result;
      String	s1;
      String	s2;

      // set up comparison
      if (m_IncludeParentDirs) {
	s1 = o1.getFile().getAbsolutePath();
	s2 = o2.getFile().getAbsolutePath();
      }
      else {
	s1 = o1.getName();
	s2 = o2.getName();
      }
      if (!m_CaseSensitive) {
	s1 = s1.toLowerCase();
	s2 = s2.toLowerCase();
      }

      // compare
      if (m_ListDirsFirst) {
	if (o1.isDirectory() && o2.isDirectory())
	  result = s1.compareTo(s2);
	else if (!o1.isDirectory() && !o2.isDirectory())
	  result = s1.compareTo(s2);
	else if (o1.isDirectory())
	  result = -1;
	else
	  result = 1;
      }
      else {
	result = s1.compareTo(s2);
      }

      return result;
    }
  }

  /**
   * List model for showing the files.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FileWrapperListModel
    extends AbstractListModel<String> {

    private static final long serialVersionUID = -5631974196097641601L;

    /** the files. */
    protected List<FileWrapper> m_Files;

    /** whether to show parent dirs. */
    protected boolean m_ShowParentDirs;

    /**
     * Initializes the model.
     *
     * @param files		the files to display
     * @param showParentDirs	true if to show parent dirs
     */
    public FileWrapperListModel(List<FileWrapper> files, boolean showParentDirs) {
      m_Files          = files;
      m_ShowParentDirs = showParentDirs;
    }

    /**
     * Returns the number of files/elements.
     *
     * @return		the number of elements
     */
    @Override
    public int getSize() {
      return m_Files.size();
    }

    /**
     * Returns the element at the specified position.
     *
     * @param index	the index
     * @return		the element
     */
    @Override
    public String getElementAt(int index) {
      FileWrapper	wrapper;

      wrapper = m_Files.get(index);

      if (m_ShowParentDirs)
	return wrapper.getFile().getAbsolutePath();
      else
	return wrapper.getName();
    }
  }

  /**
   * The model for the table.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class FileWrapperTableModel
    extends AbstractBaseTableModel {

    private static final long serialVersionUID = -4874766549376555318L;

    /** the files. */
    protected List<FileWrapper> m_Files;

    /** whether to show parent dirs. */
    protected boolean m_ShowParentDirs;

    /** the date formatter. */
    protected DateFormat m_DateFormat;

    /**
     * Initializes the model.
     *
     * @param files		the files
     * @param showParentDirs	true if to show parent dirs
     */
    public FileWrapperTableModel(List<FileWrapper> files, boolean showParentDirs) {
      m_Files          = files;
      m_ShowParentDirs = showParentDirs;
      m_DateFormat     = DateUtils.getTimestampFormatter();
    }

    /**
     * Returns whether to show the parent directories.
     *
     * @return		true if to show
     */
    public boolean getShowParentDirs() {
      return m_ShowParentDirs;
    }

    /**
     * Returns the number of files/rows.
     *
     * @return		the number of rows
     */
    @Override
    public int getRowCount() {
      return m_Files.size();
    }

    /**
     * Returns the number of columns.
     *
     * @return		the number of columns
     */
    @Override
    public int getColumnCount() {
      int	result;

      result = 0;

      result++;  // file
      result++;  // DIR
      result++;  // size
      result++;  // last mod

      return result;
    }

    /**
     * Returns the name of the column.
     *
     * @param column	the index of the column
     * @return		the name, null if not available
     */
    @Override
    public String getColumnName(int column) {
      switch (column) {
	case 0:
	  return "File";
	case 1:
	  return "";  // DIR
	case 2:
	  return "Size";
	case 3:
	  return "Date modified";
	default:
	  return null;
      }
    }

    /**
     * Returns the class of the column.
     *
     * @param columnIndex	the index of the column
     * @return			the class, null if not available
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
      switch (columnIndex) {
	case 0:
	  return String.class;
	case 1:
	  return String.class;
	case 2:
	  return Long.class;
	case 3:
	  return String.class;
	default:
	  return null;
      }
    }

    /**
     * Returns the value at the specified location.
     *
     * @param rowIndex		the row
     * @param columnIndex	the column
     * @return			the value, null if not available
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      FileWrapper	wrapper;

      if (rowIndex >= m_Files.size())
	return null;

      wrapper = m_Files.get(rowIndex);
      switch (columnIndex) {
	case 0:
	  if (m_ShowParentDirs)
	    return wrapper.getFile().getAbsolutePath();
	  else
	    return wrapper.getName();
	case 1:
	  if (wrapper.isDirectory())
	    return "DIR";
	  else
	    return null;
	case 2:
	  if (wrapper.isDirectory())
	    return null;
	  else
	    return wrapper.getLength();
	case 3:
	  return m_DateFormat.format(wrapper.getLastModified());
	default:
	  return null;
      }
    }
  }

  /** whether to show details on the files. */
  protected Boolean m_ShowDetails;

  /** whether to allow multiple files to be selected. */
  protected boolean m_MultiSelection;

  /** for listing the content of the directory. */
  protected DirectoryLister m_Lister;

  /** the comparator. */
  protected FileWrapperComparator m_Comparator;

  /** the currently listed files. */
  protected List<FileWrapper> m_Files;

  /** the currently running swingworker. */
  protected SwingWorker m_Worker;

  /** whether to ignore changes. */
  protected boolean m_IgnoreChanges;

  /** the List for the non-detailed view. */
  protected BaseList m_List;

  /** the table for the detailed view. */
  protected SortableAndSearchableTable m_Table;

  /** the listeners for when the files get updated. */
  protected Set<ChangeListener> m_FilesChangeListeners;

  /** the listeners for when the selection changes. */
  protected Set<ChangeListener> m_SelectionChangeListeners;

  /**
   * Initializes the panel.
   *
   * @param showDetails		whether to show details like DIR and size
   */
  public FilePanel(boolean showDetails) {
    super();
    m_ShowDetails = showDetails;
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

    m_Lister = new DirectoryLister();
    m_Lister.setRecursive(false);
    m_Lister.setMaxItems(-1);

    m_Comparator = new FileWrapperComparator(false, true, false);

    m_Files = new ArrayList<>();

    m_FilesChangeListeners     = new HashSet<>();
    m_SelectionChangeListeners = new HashSet<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    if (m_ShowDetails == null)
      return;

    super.initGUI();

    setLayout(new BorderLayout());

    if (m_ShowDetails) {
      m_Table = new SortableAndSearchableTable(new FileWrapperTableModel(new ArrayList<>(), false));
      m_Table.setUseOptimalColumnWidths(true);
      m_Table.getSelectionModel().addListSelectionListener(this);
      add(new BaseScrollPane(m_Table), BorderLayout.CENTER);
    }
    else {
      m_List = new BaseList();
      m_List.addListSelectionListener(this);
      add(new BaseScrollPane(m_List), BorderLayout.CENTER);
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
   * Sets the directory to list.
   *
   * @param value	the directory
   */
  public void setCurrentDir(PlaceholderDirectory value) {
    m_Lister.setWatchDir(value);
    update();
  }

  /**
   * Returns the current directory.
   *
   * @return		the directory
   */
  public PlaceholderDirectory getCurrentDir() {
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
   * Sets whether to recursively list files.
   *
   * @param value	true if to list them recursively
   */
  public void setRecursive(boolean value) {
    m_Lister.setRecursive(value);
    update();
  }

  /**
   * Returns whether the files are listed recursively.
   *
   * @return		true if to list recursively
   */
  public boolean isRecursive() {
    return m_Lister.getRecursive();
  }

  /**
   * Sets whether to use case-sensitive comparison.
   *
   * @param value	true if case-sensitive
   */
  public void setCaseSensitive(boolean value) {
    m_Comparator = new FileWrapperComparator(value, m_Comparator.isListDirsFirst(), m_Comparator.isIncludeParentDirs());
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
    m_Comparator = new FileWrapperComparator(m_Comparator.isCaseSensitive(), value, m_Comparator.isIncludeParentDirs());
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
    m_Comparator = new FileWrapperComparator(m_Comparator.isCaseSensitive(), m_Comparator.isListDirsFirst(), value);
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
   * Returns the currently selected file, if any.
   *
   * @return		the file, null if none selected
   */
  public File getSelectedFile() {
    FileWrapper		wrapper;

    wrapper = null;
    if (m_Table != null) {
      if (m_Table.getSelectedRow() > -1)
	wrapper = m_Files.get(m_Table.getActualRow(m_Table.getSelectedRow()));
    }
    else if (m_List != null) {
      if (m_List.getSelectedIndex() > -1)
	wrapper = m_Files.get(m_List.getSelectedIndex());
    }

    if (wrapper != null)
      return wrapper.getFile();
    else
      return null;
  }

  /**
   * Returns the currently selected files, if any.
   *
   * @return		the files
   */
  public File[] getSelectedFiles() {
    List<File>		result;
    FileWrapper		wrapper;

    result = new ArrayList<>();
    if (m_Table != null) {
      for (int index: m_Table.getSelectedRows()) {
	wrapper = m_Files.get(m_Table.getActualRow(index));
	result.add(wrapper.getFile());
      }
    }
    else if (m_List != null) {
      for (int index: m_List.getSelectedIndices()){
	wrapper = m_Files.get(index);
	result.add(wrapper.getFile());
      }
    }

    return result.toArray(new File[result.size()]);
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
      protected List<FileWrapper> files = new ArrayList<>();
      @Override
      protected Object doInBackground() throws Exception {
	for (String file: m_Lister.list())
	  files.add(new FileWrapper(new File(file)));
	Collections.sort(files, m_Comparator);
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
   * Checks whether we're currently busy.
   *
   * @return		true if busy
   */
  public boolean isBusy() {
    return (m_Worker != null);
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
    if (e.getValueIsAdjusting())
      return;
    notifySelectionChangeListeners();
  }

  /**
   * Just for testing.
   *
   * @param args	the initial directory
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    FilePanel simple = new FilePanel(false);
    simple.addFilesChangeListener((ChangeEvent e) -> {
	System.out.println("simple: files changed");
    });
    simple.addSelectionChangeListener((ChangeEvent e) -> {
      System.out.println("simple: selected files=" + simple.getSelectedFiles().length);
    });
    FilePanel details = new FilePanel(true);
    details.addFilesChangeListener((ChangeEvent e) -> {
      System.out.println("details: files changed");
    });
    details.addSelectionChangeListener((ChangeEvent e) -> {
      System.out.println("details: selected files=" + details.getSelectedFiles().length);
    });
    details.startUpdate();
    details.setListDirs(true);
    if (args.length > 0)
      details.setCurrentDir(new PlaceholderDirectory(args[0]));
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
