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
 * InstanceExplorer.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.instance;

import adams.core.CleanUpHandler;
import adams.core.Properties;
import adams.core.Range;
import adams.core.StatusMessageHandler;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.instance.Instance;
import adams.data.statistics.ArrayHistogram;
import adams.data.statistics.InformativeStatistic;
import adams.data.weka.ArffUtils;
import adams.env.Environment;
import adams.env.InstanceExplorerDefinition;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTable;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.DataChangeEvent;
import adams.gui.event.DataChangeListener;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.visualization.container.ContainerListManager;
import adams.gui.visualization.container.ContainerListPopupMenuSupplier;
import adams.gui.visualization.container.ContainerTable;
import adams.gui.visualization.container.NotesFactory;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.PopupMenuCustomizer;
import adams.gui.visualization.report.ReportContainerList;
import adams.gui.visualization.report.ReportFactory;
import adams.gui.visualization.statistics.InformativeStatisticFactory;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ConverterUtils.DataSink;
import weka.experiment.InstanceQuery;
import weka.gui.AdamsHelper;
import weka.gui.ConverterFileChooser;
import weka.gui.sql.SqlViewerDialog;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A panel for exploring Instances visually.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstanceExplorer
  extends BasePanel
  implements MenuBarProvider, StatusMessageHandler,
             ContainerListManager<InstanceContainerManager>,
             DataChangeListener, PopupMenuCustomizer,
             ContainerListPopupMenuSupplier<InstanceContainerManager,InstanceContainer>,
             CleanUpHandler, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 4478483903068117980L;

  /** the name of the props file. */
  public final static String FILENAME = "InstanceExplorer.props";

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "InstanceExplorerSession.props";

  /** the properties. */
  protected static Properties m_Properties;

  /** the panel for displaying. */
  protected InstancePanel m_PanelInstance;

  /** the status bar. */
  protected BaseStatusBar m_StatusBar;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "load recent" submenu. */
  protected JMenu m_MenuItemLoadRecent;

  /** the menu item for view related stuff. */
  protected JMenu m_MenuView;

  /** the toggle zoom overview menu item. */
  protected JMenuItem m_MenuItemViewZoomOverview;

  /** the toggle anti-aliasing menu item. */
  protected JMenuItem m_MenuItemViewAntiAliasing;

  /** the color provider menu item. */
  protected JMenuItem m_MenuItemViewColorProvider;

  /** the clear data menu item. */
  protected JMenuItem m_MenuItemClearData;

  /** the tabbed pane for the data to display. */
  protected BaseTabbedPane m_TabbedPane;

  /** the reports. */
  protected ReportFactory.Panel m_Reports;

  /** for searching the fields in the reports. */
  protected SearchPanel m_SearchPanel;

  /** the dialog for loading datasets. */
  protected LoadDatasetDialog m_LoadFromDiskDialog;

  /** the SQL viewer dialog. */
  protected SqlViewerDialog m_LoadFromDatabaseDialog;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /** the dialog for the histogram setup. */
  protected HistogramFactory.SetupDialog m_HistogramSetup;
  
  /** the dialog for selecting the color provider. */
  protected GenericObjectEditorDialog m_DialogColorProvider;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_LoadFromDiskDialog     = null;
    m_LoadFromDatabaseDialog = null;
    m_RecentFilesHandler     = null;
    m_HistogramSetup         = null;
    m_DialogColorProvider    = null;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JPanel	panelData;
    JPanel	panelReports;

    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new BaseTabbedPane();
    add(m_TabbedPane, BorderLayout.CENTER);

    // 1. page: graph
    panelData = new JPanel(new BorderLayout());
    m_TabbedPane.addTab("Data", panelData);
    m_TabbedPane.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	ContainerTable dtable = getInstanceContainerList().getTable();
	// data
	if (m_TabbedPane.getSelectedIndex() == 0) {
	  BaseTable rtable = m_Reports.getReportContainerList().getTable();
	  if ((rtable == null) || (rtable.getSelectedRowCount() != 1))
	    return;
	  int row = rtable.getSelectedRow();
	  dtable.getSelectionModel().clearSelection();
	  dtable.getSelectionModel().setSelectionInterval(row, row);
	}
	// reports
	else if (m_TabbedPane.getSelectedIndex() == 1) {
	  if (dtable.getSelectedRowCount() != 1)
	    return;
	  m_Reports.setCurrentTable(dtable.getSelectedRow());
	}
      }
    });

    // the instances
    m_PanelInstance = new InstancePanel();
    m_PanelInstance.getContainerManager().addDataChangeListener(this);
    m_PanelInstance.getPlot().setPopupMenuCustomizer(this);
    m_PanelInstance.setStatusMessageHandler(this);
    panelData.add(m_PanelInstance, BorderLayout.CENTER);

    // 2. page: information
    panelReports = new JPanel(new BorderLayout());
    m_TabbedPane.addTab("Information", panelReports);
    m_Reports = newReportPanel();
    m_Reports.setDataContainerPanel(m_PanelInstance);
    panelReports.add(m_Reports, BorderLayout.CENTER);

    m_SearchPanel = new SearchPanel(LayoutType.HORIZONTAL, true);
    m_SearchPanel.addSearchListener(new SearchListener() {
      public void searchInitiated(SearchEvent e) {
        m_Reports.search(
            m_SearchPanel.getSearchText(), m_SearchPanel.isRegularExpression());
	m_SearchPanel.grabFocus();
      }
    });
    panel = new JPanel(new BorderLayout());
    panel.add(m_SearchPanel, BorderLayout.WEST);
    panelReports.add(panel, BorderLayout.SOUTH);

    // the status bar
    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);
  }

  /**
   * Returns the panel for painting the instances.
   *
   * @return		the panel
   */
  public InstancePanel getInstancePanel() {
    return m_PanelInstance;
  }

  /**
   * Returns the panel listing the instances.
   *
   * @return		the panel
   */
  public InstanceContainerList getInstanceContainerList() {
    return m_PanelInstance.getInstanceContainerList();
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
   * Creates a new tabbed pane for the reports.
   *
   * @return		the tabbed pane
   */
  protected InstanceReportFactory.Panel newReportPanel() {
    return new InstanceReportFactory.Panel();
  }

  /**
   * Gets called if the data of the instance panel has changed.
   *
   * @param e		the event that the instance panel sent
   */
  public void dataChanged(DataChangeEvent e) {
    updateMenu();
  }

  /**
   * Returns the current container manager.
   *
   * @return		the manager
   */
  public InstanceContainerManager getContainerManager() {
    return m_PanelInstance.getContainerManager();
  }

  /**
   * Sets the manager for handling the containers.
   *
   * @param value	the manager
   */
  public void setContainerManager(InstanceContainerManager value) {
    m_PanelInstance.setContainerManager(value);
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    boolean	dataLoaded;

    if (m_MenuBar == null)
      return;

    dataLoaded = (getContainerManager().count() > 0);

    m_MenuItemClearData.setEnabled(dataLoaded);
    m_MenuItemLoadRecent.setEnabled(m_RecentFilesHandler.size() > 0);
    m_MenuItemViewAntiAliasing.setSelected(getInstancePanel().isAntiAliasingEnabled());
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

      // File/Clear
      menuitem = new JMenuItem("Clear data");
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
      menuitem.setIcon(GUIHelper.getIcon("new.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  clearData();
	}
      });
      m_MenuItemClearData = menuitem;

      // File/Load from file
      menuitem = new JMenuItem("Load data from disk...");
      menu.add(menuitem);
      menuitem.setMnemonic('o');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.setIcon(GUIHelper.getIcon("open.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  loadDataFromDisk();
	}
      });

      // File/Recent files
      submenu = new JMenu("Load recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<JMenu>(
	  SESSION_FILE, getProperties().getInteger("MaxRecentFiles", 5), submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  loadDataFromDisk(e.getItem());
	}
      });
      m_MenuItemLoadRecent = submenu;

      // File/Load data from database
      menuitem = new JMenuItem("Load data from database");
      menu.add(menuitem);
      menuitem.setMnemonic('L');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed O"));
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  loadDataFromDatabase();
	}
      });

      // File/Send to
      menu.addSeparator();
      if (SendToActionUtils.addSendToSubmenu(this, menu))
	menu.addSeparator();

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  close();
	}
      });

      // View
      menu = new JMenu("View");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });
      m_MenuView = menu;

      // View/Display zoom overview
      menuitem = new JCheckBoxMenuItem("Display zoom overview");
      menu.add(menuitem);
      menuitem.setMnemonic('Z');
      menuitem.setSelected(isZoomOverviewPanelVisible());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  setZoomOverviewPanelVisible(m_MenuItemViewZoomOverview.isSelected());
	}
      });
      m_MenuItemViewZoomOverview = menuitem;

      // View/Anti-aliasing
      menuitem = new JCheckBoxMenuItem("Anti-aliasing");
      menu.add(menuitem);
      menuitem.setMnemonic('A');
      menuitem.setSelected(getInstancePanel().isAntiAliasingEnabled());
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  getInstancePanel().setAntiAliasingEnabled(m_MenuItemViewAntiAliasing.isSelected());
	}
      });
      m_MenuItemViewAntiAliasing = menuitem;

      // View/Color provider
      menuitem = new JMenuItem("Color provider...");
      menu.add(menuitem);
      menuitem.setMnemonic('P');
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  selectColorProvider();
	}
      });
      m_MenuItemViewColorProvider = menuitem;

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
   * Removes all the data.
   */
  public void clearData() {
    getContainerManager().clear();
  }

  /**
   * pops up SQL Viewer for SQL statement.
   */
  public void loadDataFromDatabase() {
    InstanceQuery		query;
    List<InstanceContainer>	data;
    Instances			dataset;
    Instance			inst;
    int				i;

    if (m_LoadFromDatabaseDialog == null) {
      if ((getParentFrame() != null) && (getParentFrame() instanceof JFrame))
	m_LoadFromDatabaseDialog = new SqlViewerDialog((JFrame) getParentFrame());
      else
	m_LoadFromDatabaseDialog = new SqlViewerDialog(null);
    }

    m_LoadFromDatabaseDialog.setVisible(true);
    if (m_LoadFromDatabaseDialog.getReturnValue() != JOptionPane.OK_OPTION)
      return;

    try {
      showStatus("Executing query: " + m_LoadFromDatabaseDialog.getQuery());
      query = new InstanceQuery();
      query.setDatabaseURL(m_LoadFromDatabaseDialog.getURL());
      query.setUsername(m_LoadFromDatabaseDialog.getUser());
      query.setPassword(m_LoadFromDatabaseDialog.getPassword());
      query.setQuery(m_LoadFromDatabaseDialog.getQuery());
      query.setSparseData(m_LoadFromDatabaseDialog.getGenerateSparseData());
      if (query.isConnected())
	query.disconnectFromDatabase();
      query.connectToDatabase();

      showStatus("Loading data...");
      data    = new ArrayList<InstanceContainer>();
      dataset = query.retrieveInstances();
      for (i = 0; i < dataset.numInstances(); i++) {
        inst = new Instance();
        inst.set(dataset.instance(i));
        inst.setID(dataset.relationName() + "." + i);
        data.add(getContainerManager().newContainer(inst));
        showStatus("Loading data " + (i+1) + "/" + dataset.numInstances());
      }
      loadData(dataset, data);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
        this,
        "Failed to load data from database:\n" + Utils.throwableToString(e),
        "Database error");
    }

    showStatus("");
  }

  /**
   * pops up file dialog for loading dataset form disk.
   */
  public void loadDataFromDisk() {
    loadDataFromDisk(null);
  }

  /**
   * pops up file dialog for loading dataset form disk.
   *
   * @param file	an optional file, use null to ignore
   */
  public void loadDataFromDisk(File file) {
    int[]			indices;
    int[]			additional;
    int				i;
    Instances 			dataset;
    weka.core.Instance		winst;
    Instance			inst;
    List<InstanceContainer>	data;
    Range			range;
    HashSet<Integer>		attTypes;
    int 			id;

    if (m_LoadFromDiskDialog == null) {
      if (getParentDialog() != null)
	m_LoadFromDiskDialog = new LoadDatasetDialog(getParentDialog());
      else
	m_LoadFromDiskDialog = new LoadDatasetDialog(getParentFrame());
      m_LoadFromDiskDialog.setCurrent(new File(getProperties().getPath("InitialDir", "%h")));
      m_LoadFromDiskDialog.setDefaultAttributeRange(getProperties().getPath("AttributeRange", "first-last"));
      m_LoadFromDiskDialog.setDefaultClassIndex(getProperties().getPath("ClassIndex", ""));
      m_LoadFromDiskDialog.setDefaultSortIndex(getProperties().getPath("SortIndex", ""));
      m_LoadFromDiskDialog.setDefaultIncludeAttributes(Attribute.NUMERIC, getProperties().getBoolean("IncludeNumericAttributes", true));
      m_LoadFromDiskDialog.setDefaultIncludeAttributes(Attribute.DATE, getProperties().getBoolean("IncludeDateAttributes", false));
      m_LoadFromDiskDialog.setDefaultIncludeAttributes(Attribute.NOMINAL, getProperties().getBoolean("IncludeNominalAttributes", false));
      m_LoadFromDiskDialog.setDefaultIncludeAttributes(Attribute.STRING, getProperties().getBoolean("IncludeStringAttributes", false));
      m_LoadFromDiskDialog.setDefaultIncludeAttributes(Attribute.RELATIONAL, getProperties().getBoolean("IncludeRelationalAttributes", false));
    }

    if (file != null)
      m_LoadFromDiskDialog.setCurrent(file);
    m_LoadFromDiskDialog.setVisible(true);
    indices = m_LoadFromDiskDialog.getIndices();
    if (indices == null)
      return;
    if (m_RecentFilesHandler != null)
      m_RecentFilesHandler.addRecentItem(m_LoadFromDiskDialog.getCurrent());

    attTypes = new HashSet<Integer>();
    if (m_LoadFromDiskDialog.getIncludeAttributes(Attribute.NUMERIC))
      attTypes.add(Attribute.NUMERIC);
    if (m_LoadFromDiskDialog.getIncludeAttributes(Attribute.DATE))
      attTypes.add(Attribute.DATE);
    if (m_LoadFromDiskDialog.getIncludeAttributes(Attribute.NOMINAL))
      attTypes.add(Attribute.NOMINAL);
    if (m_LoadFromDiskDialog.getIncludeAttributes(Attribute.STRING))
      attTypes.add(Attribute.STRING);
    if (m_LoadFromDiskDialog.getIncludeAttributes(Attribute.RELATIONAL))
      attTypes.add(Attribute.RELATIONAL);

    showStatus("Loading data...");
    data       = new ArrayList<InstanceContainer>();
    dataset    = m_LoadFromDiskDialog.getDataset();
    additional = m_LoadFromDiskDialog.getAdditionalAttributes();
    range      = m_LoadFromDiskDialog.getCurrentAttributeRange();
    id         = m_LoadFromDiskDialog.getCurrentIDIndex();
    for (i = 0; i < indices.length; i++) {
      winst = dataset.instance(indices[i]);
      inst = new Instance();
      inst.set(winst, i, additional, range, attTypes);
      if (id == -1) {
	inst.setID((indices[i] + 1) + "." + dataset.relationName());
      }
      else {
	if (winst.attribute(id).isNumeric())
	  inst.setID("" + winst.value(id));
	else
	  inst.setID(winst.stringValue(id));
      }
      data.add(getContainerManager().newContainer(inst));
      showStatus("Loading data " + (i+1) + "/" + dataset.numInstances());
    }
    loadData(dataset, data);

    showStatus("");
  }

  /**
   * Loads the given data into the container manager.
   *
   * @param dataset	the dataset
   * @param data	the data to add to the manager
   */
  protected void loadData(Instances dataset, List<InstanceContainer> data) {
    boolean			hasDBID;
    InstanceContainerList	listInst;
    ReportContainerList		listReport;

    // turn off anti-aliasing to speed up display
    if (getContainerManager().count() + data.size() > getProperties().getInteger("MaxNumContainersWithAntiAliasing", 50)) {
      if (getInstancePanel().isAntiAliasingEnabled())
	getInstancePanel().setAntiAliasingEnabled(false);
    }

    listInst   = m_PanelInstance.getInstanceContainerList();
    listReport = m_Reports.getReportContainerList();

    hasDBID = (dataset.attribute(ArffUtils.getDBIDName()) != null);
    listInst.setDisplayDatabaseID(hasDBID);
    listReport.setDisplayDatabaseID(hasDBID);
    getContainerManager().addAll(data);
  }

  /**
   * closes the dialog/frame.
   */
  public void close() {
    cleanUp();
    if (getParentFrame() != null) {
      getParentFrame().setVisible(false);
      getParentFrame().dispose();
    }
    else if (getParentDialog() != null) {
      getParentDialog().setVisible(false);
      getParentDialog().dispose();
    }
  }

  /**
   * Whether to display the side panel or not.
   *
   * @param visible	if true, then the side panel will be displayed
   */
  public void setSidePanelVisible(boolean visible) {
    m_PanelInstance.setSidePanelVisible(visible);
  }

  /**
   * Returns whether the side panel is visible or not.
   *
   * @return		true if the side panel is visible
   */
  public boolean isSidePanelVisible() {
    return m_PanelInstance.isSidePanelVisible();
  }

  /**
   * Returns the side panel.
   *
   * @return		the side panel
   */
  public JPanel getSidePanel() {
    return m_PanelInstance.getSidePanel();
  }

  /**
   * Sets the zoom overview panel visible or not.
   * 
   * @param value	if true then the panel is visible
   */
  public void setZoomOverviewPanelVisible(boolean value) {
    m_PanelInstance.setZoomOverviewPanelVisible(value);
  }
  
  /**
   * Returns whether the zoom overview panel is visible or not.
   * 
   * @return		true if visible
   */
  public boolean isZoomOverviewPanelVisible() {
    return m_PanelInstance.isZoomOverviewPanelVisible();
  }

  /**
   * Optional customizing of the menu that is about to be popped up.
   *
   * @param e		the mous event
   * @param menu	the menu to customize
   */
  public void customizePopupMenu(MouseEvent e, JPopupMenu menu) {
    JMenuItem	item;

    menu.addSeparator();

    item = new JMenuItem();
    item.setIcon(GUIHelper.getEmptyIcon());
    if (!getInstancePanel().getInstancePaintlet().isMarkersDisabled())
      item.setText("Disable markers");
    else
      item.setText("Enable markers");
    item.addActionListener((ActionEvent ae) -> {
      getInstancePanel().getInstancePaintlet().setMarkersDisabled(
        !getInstancePanel().getInstancePaintlet().isMarkersDisabled());
      repaint();
    });
    menu.add(item);

    item = new JMenuItem();
    item.setIcon(GUIHelper.getEmptyIcon());
    if (isSidePanelVisible())
      item.setText("Hide side panel");
    else
      item.setText("Show side panel");
    item.addActionListener((ActionEvent ae) -> setSidePanelVisible(!isSidePanelVisible()));
    menu.add(item);

    item = new JMenuItem();
    item.setIcon(GUIHelper.getEmptyIcon());
    if (getInstancePanel().getAdjustToVisibleData())
      item.setText("Adjust to loaded data");
    else
      item.setText("Adjust to visible data");
    item.addActionListener((ActionEvent ae) -> getInstancePanel().setAdjustToVisibleData(!getInstancePanel().getAdjustToVisibleData()));
    menu.add(item);

    menu.addSeparator();

    item = new JMenuItem("Instance histogram", GUIHelper.getIcon("histogram.png"));
    item.addActionListener((ActionEvent ae) -> showHistogram(getContainerManager().getAllVisible()));
    menu.add(item);

    item = new JMenuItem("Instance notes", GUIHelper.getEmptyIcon());
    item.addActionListener((ActionEvent ae) -> showNotes(getContainerManager().getAllVisible()));
    menu.add(item);

    menu.addSeparator();

    item = new JMenuItem("Save visible instances...", GUIHelper.getIcon("save.gif"));
    item.addActionListener((ActionEvent ae) -> {
      ConverterFileChooser fc = new ConverterFileChooser();
      AdamsHelper.updateFileChooserAccessory(fc);
      int retval = fc.showSaveDialog(InstanceExplorer.this);
      if (retval != ConverterFileChooser.APPROVE_OPTION)
        return;
      weka.core.Instances dataset = null;
      for (int i = 0; i < getContainerManager().count(); i++) {
        InstanceContainer cont = getContainerManager().get(i);
        if (i == 0)
          dataset = new weka.core.Instances(cont.getData().getDatasetHeader(), 0);
        if (cont.isVisible())
          dataset.add((weka.core.Instance) cont.getData().toInstance().copy());
      }
      if (dataset == null)
        return;
      AbstractFileSaver saver = fc.getSaver();
      saver.setInstances(dataset);
      try {
        saver.writeBatch();
      }
      catch (Exception ex) {
        ex.printStackTrace();
        GUIHelper.showErrorMessage(
          InstanceExplorer.this, "Error saving instances:\n" + ex);
      }
    });
    menu.add(item);
  }

  /**
   * Returns a popup menu for the table of the instance list.
   *
   * @param table	the affected table
   * @param row	the row the mouse is currently over
   * @return		the popup menu
   */
  public BasePopupMenu getContainerListPopupMenu(final ContainerTable<InstanceContainerManager,InstanceContainer> table, final int row) {
    BasePopupMenu	result;
    JMenuItem		item;
    final int[] 	indices;

    result = new BasePopupMenu();
    if (table.getSelectedRows().length == 0)
      indices = new int[]{row};
    else
      indices = table.getSelectedRows();

    item = new JMenuItem("Toggle visibility");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        getContainerManager().startUpdate();
	for (int i = 0; i < indices.length; i++) {
	  InstanceContainer cont = getContainerManager().get(indices[i]);
	  cont.setVisible(!cont.isVisible());
	}
        getContainerManager().finishUpdate();
      }
    });
    result.add(item);

    item = new JMenuItem("Choose color...");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	Color c = null;
	if (indices.length == 1) {
	  c = JColorChooser.showDialog(
	      table,
	      "Choose color for " + getContainerManager().get(indices[0]).getData().getID(),
	      getContainerManager().get(indices[0]).getColor());
	}
	else {
	  c = JColorChooser.showDialog(
	      table,
	      "Choose color",
	      getContainerManager().get(row).getColor());
	}
	if (c != null) {
          getContainerManager().startUpdate();
	  for (int index: indices)
	    getContainerManager().get(index).setColor(c);
          getContainerManager().finishUpdate();
	}
      }
    });
    result.add(item);

    if (getContainerManager().getAllowRemoval()) {
      result.addSeparator();

      item = new JMenuItem("Remove");
      item.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  getContainerManager().startUpdate();
	  for (int i = indices.length - 1; i >= 0; i--)
	    getContainerManager().remove(indices[i]);
	  getContainerManager().finishUpdate();
	}
      });
      result.add(item);

      item = new JMenuItem("Remove all");
      item.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  getContainerManager().clear();
	}
      });
      result.add(item);
    }

    result.addSeparator();

    item = new JMenuItem("Notes");
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	List<InstanceContainer> data = new ArrayList<InstanceContainer>();
	for (int i = 0; i < indices.length; i++)
	  data.add(getContainerManager().get(indices[i]));
	showNotes(data);
      }
    });
    result.add(item);

    return result;
  }

  /**
   * Displays the notes for the given chromatograms.
   *
   * @param data	the chromatograms to display
   */
  protected void showNotes(List<InstanceContainer> data) {
    NotesFactory.Dialog		dialog;

    if (getParentDialog() != null)
      dialog = NotesFactory.getDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = NotesFactory.getDialog(getParentFrame(), false);
    dialog.setData(data);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Displays a dialog with the given statistics.
   *
   * @param stats	the statistics to display
   */
  protected void showStatistics(List<InformativeStatistic> stats) {
    InformativeStatisticFactory.Dialog	dialog;

    if (getParentDialog() != null)
      dialog = InformativeStatisticFactory.getDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = InformativeStatisticFactory.getDialog(getParentFrame(), false);

    dialog.setStatistics(stats);
    dialog.setVisible(true);
  }

  /**
   * Displays the histograms for the given instances.
   *
   * @param data	the instances to display
   */
  protected void showHistogram(List<InstanceContainer> data) {
    HistogramFactory.Dialog	dialog;
    int				i;
    Instance			inst;

    // get parameters for histograms
    if (m_HistogramSetup == null) {
      if (getParentDialog() != null)
	m_HistogramSetup = HistogramFactory.getSetupDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_HistogramSetup = HistogramFactory.getSetupDialog(getParentFrame(), true);
    }
    m_HistogramSetup.setLocationRelativeTo(this);
    m_HistogramSetup.setVisible(true);
    if (m_HistogramSetup.getResult() != HistogramFactory.SetupDialog.APPROVE_OPTION)
      return;

    // generate histograms and display them
    if (getParentDialog() != null)
      dialog = HistogramFactory.getDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = HistogramFactory.getDialog(getParentFrame(), false);
    for (i = 0; i < data.size(); i++) {
      inst = data.get(i).getData();
      dialog.add((ArrayHistogram) m_HistogramSetup.getCurrent(), inst, data.get(i).getID());
    }
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Lets the user select a new color provider.
   */
  protected void selectColorProvider() {
    if (m_DialogColorProvider == null) {
      if (getParentDialog() != null)
	m_DialogColorProvider = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_DialogColorProvider = new GenericObjectEditorDialog(getParentFrame(), true);
      m_DialogColorProvider.setTitle("Select color provider");
      m_DialogColorProvider.getGOEEditor().setClassType(AbstractColorProvider.class);
      m_DialogColorProvider.getGOEEditor().setCanChangeClassInDialog(true);
      m_DialogColorProvider.setLocationRelativeTo(this);
    }
    
    m_DialogColorProvider.setCurrent(getContainerManager().getColorProvider().shallowCopy());
    m_DialogColorProvider.setVisible(true);
    if (m_DialogColorProvider.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    getContainerManager().setColorProvider(((AbstractColorProvider) m_DialogColorProvider.getCurrent()).shallowCopy());
  }
  
  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  public Class[] getSendToClasses() {
    return new Class[]{PlaceholderFile.class, JComponent.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve an item for
   * @return		true if an object is available for sending
   */
  public boolean hasSendToItem(Class[] cls) {
    return (getContainerManager().countVisible() > 0);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  public Object getSendToItem(Class[] cls) {
    Object	result;
    Instances	inst;

    result = null;

    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      if (getContainerManager().countVisible() > 0) {
	result = SendToActionUtils.nextTmpFile("instanceexplorer", "arff");
	inst   = m_PanelInstance.getInstances();
	try {
	  DataSink.write(((PlaceholderFile) result).getAbsolutePath(), inst);
	}
	catch (Exception e) {
	  result = null;
	  GUIHelper.showErrorMessage(this, "Failed to write instances to '" + result + "'!");
	}
      }
    }
    else if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
      if (getContainerManager().countVisible() > 0) {
	result = this;
      }
    }

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_PanelInstance.cleanUp();
    if (m_DialogColorProvider != null) {
      m_DialogColorProvider.dispose();
      m_DialogColorProvider = null;
    }
  }

  /**
   * Returns the properties that define the editor.
   *
   * @return		the properties
   */
  public static synchronized Properties getProperties() {
    if (m_Properties == null)
      m_Properties = Environment.getInstance().read(InstanceExplorerDefinition.KEY);

    return m_Properties;
  }
}
