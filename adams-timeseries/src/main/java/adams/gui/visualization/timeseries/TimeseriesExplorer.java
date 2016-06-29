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
 * TimeseriesExplorer.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.timeseries;

import adams.core.CleanUpHandler;
import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.base.BaseObject;
import adams.core.base.BaseString;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.core.option.OptionUtils;
import adams.data.io.input.AbstractDataContainerReader;
import adams.data.timeseries.PeriodicityHelper;
import adams.data.timeseries.PeriodicityType;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.data.timeseries.TimeseriesUtils;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnection;
import adams.db.DatabaseConnectionHandler;
import adams.db.SQLStatement;
import adams.event.DatabaseConnectionChangeEvent;
import adams.event.DatabaseConnectionChangeEvent.EventType;
import adams.event.DatabaseConnectionChangeListener;
import adams.flow.control.Flow;
import adams.flow.core.Token;
import adams.flow.source.SequenceSource;
import adams.flow.source.StringConstants;
import adams.flow.transformer.AbstractReportDbUpdater.QueryType;
import adams.flow.transformer.TimeseriesDbReader;
import adams.flow.transformer.TimeseriesReportDbUpdater;
import adams.gui.chooser.TimeseriesFileChooser;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTable;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandlerWithCommandline;
import adams.gui.core.RecentFilesHandlerWithCommandline.Setup;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.Undo.UndoPoint;
import adams.gui.core.UndoPanel;
import adams.gui.dialog.SQLStatementDialog;
import adams.gui.event.DataChangeEvent;
import adams.gui.event.DataChangeListener;
import adams.gui.event.FilterEvent;
import adams.gui.event.FilterListener;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import adams.gui.event.UndoEvent;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.scripting.AbstractScriptingEngine;
import adams.gui.scripting.AddDataFile;
import adams.gui.scripting.AddDataFiles;
import adams.gui.scripting.ClearData;
import adams.gui.scripting.DisableUndo;
import adams.gui.scripting.EnableUndo;
import adams.gui.scripting.Filter;
import adams.gui.scripting.FilterOverlay;
import adams.gui.scripting.RunFlow;
import adams.gui.scripting.RunFlowOverlay;
import adams.gui.scripting.ScriptingDialog;
import adams.gui.scripting.ScriptingEngine;
import adams.gui.scripting.ScriptingEngineHandler;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.visualization.container.ContainerListManager;
import adams.gui.visualization.container.ContainerTable;
import adams.gui.visualization.container.FilterDialog;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.Paintlet;
import adams.gui.visualization.core.axis.PeriodicityTickGenerator;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.report.ReportContainer;
import adams.gui.visualization.report.ReportFactory;
import adams.gui.wizard.DatabaseConnectionPage;
import adams.gui.wizard.ListPage;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static adams.gui.flow.FlowEditorPanel.getPropertiesEditor;

/**
 * A panel for exploring Timeseries, manipulating them with filters, etc.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesExplorer
  extends UndoPanel
  implements MenuBarProvider, StatusMessageHandler,
             ContainerListManager<TimeseriesContainerManager>,
             DataChangeListener, ScriptingEngineHandler, CleanUpHandler,
             FilterListener<Timeseries>, SendToActionSupporter,
             DatabaseConnectionHandler, DatabaseConnectionChangeListener {

  /** for serialization. */
  private static final long serialVersionUID = 3953271131937711340L;

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "TimeseriesExplorerSession.props";

  /** the panel for displaying. */
  protected TimeseriesPanel m_PanelTimeseries;

  /** the status bar. */
  protected BaseStatusBar m_StatusBar;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the clear data menu item. */
  protected JMenuItem m_MenuItemClearData;

  /** the toggle undo menu item. */
  protected JCheckBoxMenuItem m_MenuItemEnableUndo;

  /** the undo menu item. */
  protected JMenuItem m_MenuItemUndo;

  /** the redo menu item. */
  protected JMenuItem m_MenuItemRedo;

  /** the filter menu item. */
  protected JMenuItem m_MenuItemFilter;

  /** the menu item for scripts. */
  protected JMenu m_MenuScripts;

  /** the start recording menu item. */
  protected JMenuItem m_MenuItemStartRecording;

  /** the stop recording menu item. */
  protected JMenuItem m_MenuItemStopRecording;

  /** the overlay flow output menu item. */
  protected JMenuItem m_MenuItemOverlayFlowOutput;

  /** the refresh scripts menu item. */
  protected JMenuItem m_MenuItemRefreshScripts;

  /** the menu item for view related stuff. */
  protected JMenu m_MenuView;

  /** the toggle selected timestamp menu item. */
  protected JMenuItem m_MenuItemViewSelectedTimestamp;

  /** the toggle zoom overview menu item. */
  protected JMenuItem m_MenuItemViewZoomOverview;

  /** the toggle anti-aliasing menu item. */
  protected JMenuItem m_MenuItemViewAntiAliasing;

  /** the periodicity submenu. */
  protected JMenu m_MenuItemViewPeriodicity;

  /** the color provider menu item. */
  protected JMenuItem m_MenuItemViewColorProvider;

  /** the paintlet  menu item. */
  protected JMenuItem m_MenuItemViewPaintlet;

  /** the current filter. */
  protected adams.data.filter.Filter<Timeseries> m_CurrentFilter;

  /** indicates whether the filtered data was overlayed over the original. */
  protected boolean m_FilterOverlayOriginalData;

  /** the filter dialog. */
  protected FilterDialog m_DialogFilter;

  /** the file chooser for importing data. */
  protected TimeseriesFileChooser m_TimeseriesFileChooser;

  /** the dialog for managing scripts. */
  protected ScriptingDialog m_ScriptingDialog;

  /** the tabbed pane for the data to display. */
  protected BaseTabbedPane m_TabbedPane;

  /** the sample data reports. */
  protected ReportFactory.Panel m_Reports;

  /** for searching the fields in the reports. */
  protected SearchPanel m_SearchPanel;

  /** the database connection. */
  protected AbstractDatabaseConnection m_DatabaseConnection;
  
  /** the dialog for loading timeseries using custom SQL statements. */
  protected TimeseriesImportDatabaseDialog m_DialogSQL;
  
  /** the dialog for selecting the color provider. */
  protected GenericObjectEditorDialog m_DialogColorProvider;

  /** the dialog for selecting the paintlet. */
  protected GenericObjectEditorDialog m_DialogPaintlet;

  /** the recent files handler. */
  protected RecentFilesHandlerWithCommandline<JMenu> m_RecentFilesHandler;

  /**
   * default constructor.
   */
  public TimeseriesExplorer() {
    super(Timeseries.class, true);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_ScriptingDialog       = null;
    m_DialogColorProvider   = null;
    m_DialogPaintlet        = null;
    m_TimeseriesFileChooser = new TimeseriesFileChooser();
    m_TimeseriesFileChooser.setMultiSelectionEnabled(true);
    m_CurrentFilter         = new adams.data.filter.PassThrough();
    m_DialogSQL             = null;
    m_RecentFilesHandler    = null;
    m_DatabaseConnection    = getDefaultDatabaseConnection();
    m_DatabaseConnection.addChangeListener(this);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JPanel	panelData;
    JPanel	panelReport;

    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new BaseTabbedPane();
    add(m_TabbedPane, BorderLayout.CENTER);

    // 1. page: graph
    panelData = new JPanel(new BorderLayout());
    m_TabbedPane.addTab("Data", panelData);
    m_TabbedPane.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
	ContainerTable dtable = getTimeseriesPanel().getTimeseriesContainerList().getTable();
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

    // the timeseries
    m_PanelTimeseries = new TimeseriesPanel();
    m_PanelTimeseries.getContainerManager().addDataChangeListener(this);
    m_PanelTimeseries.setStatusMessageHandler(this);
    panelData.add(m_PanelTimeseries, BorderLayout.CENTER);

    // 2. page: report
    panelReport = new JPanel(new BorderLayout());
    m_TabbedPane.addTab("Report", panelReport);
    m_Reports = ReportFactory.getPanel((List<ReportContainer>) null);
    m_Reports.setDataContainerPanel(m_PanelTimeseries);
    panelReport.add(m_Reports, BorderLayout.CENTER);

    m_SearchPanel = new SearchPanel(LayoutType.HORIZONTAL, true);
    m_SearchPanel.addSearchListener(new SearchListener() {
      @Override
      public void searchInitiated(SearchEvent e) {
        m_Reports.search(
            m_SearchPanel.getSearchText(), m_SearchPanel.isRegularExpression());
	m_SearchPanel.grabFocus();
      }
    });
    panel = new JPanel(new BorderLayout());
    panel.add(m_SearchPanel, BorderLayout.WEST);
    panelReport.add(panel, BorderLayout.SOUTH);

    // the status bar
    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);

    // if the plot is focussed, display selected point
    getTimeseriesPanel().getPlot().getContent().addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
	TimeseriesPoint point = getTimeseriesPanel().getSelectedTimestampPaintlet().getPoint();
	// no point selected? -> select one in the middle
	if ((point == null) && (getContainerManager().countVisible() > 0)) {
	  for (int i = 0; i < getContainerManager().count(); i++) {
	    if (getContainerManager().isVisible(i)) {
	      List<TimeseriesPoint> points = ((TimeseriesContainer) getContainerManager().get(i)).getData().toList();
	      if (points.size() > 0) {
		point = points.get(points.size() / 2);
		getTimeseriesPanel().getSelectedTimestampPaintlet().setPoint(point);
		break;
	      }
	    }
	  }
	}
      }
    });

    // add KeyListener for moving the selected TimeseriesPoint around
    getTimeseriesPanel().getPlot().getContent().addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	int movement = 0;
	TimeseriesPoint point = getTimeseriesPanel().getSelectedTimestampPaintlet().getPoint();

	// determine direction and increment of movement
	if (point != null) {
	  if (e.getKeyCode() == KeyEvent.VK_LEFT) {
	    if (!e.isAltDown() && !e.isControlDown()) {
	      if (e.isShiftDown())
		movement = -10;
	      else
		movement = -1;
	      e.consume();
	    }
	  }
	  else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
	    if (!e.isAltDown() && !e.isControlDown()) {
	      if (e.isShiftDown())
		movement = 10;
	      else
		movement = 1;
	      e.consume();
	    }
	  }
	}

	// move selected point
	if (movement != 0) {
	  Timeseries data = (Timeseries) point.getParent();
	  if (data != null) {
	    int index = TimeseriesUtils.findTimestamp(data.toList(), point);
	    index += movement;
	    if (index < 0)
	      index = 0;
	    if (index >= data.size())
	      index = data.size() - 1;
	    // set new points
	    TimeseriesPoint newPoint = (TimeseriesPoint) data.toList().get(index);
	    getTimeseriesPanel().getSelectedTimestampPaintlet().setPoint(newPoint);
	  }
	}

	if (!e.isConsumed())
	  super.keyPressed(e);
      }
    });

    // disable selection of timestamps by default (use menu to enable again)
    getTimeseriesPanel().getSelectedTimestampPaintlet().setEnabled(false);
  }
  
  /**
   * Returns the default database connection.
   * 
   * @return		the default connection
   */
  protected AbstractDatabaseConnection getDefaultDatabaseConnection() {
    return DatabaseConnection.getSingleton();
  }

  /**
   * Returns the panel for painting the timeseries.
   *
   * @return		the panel
   */
  public TimeseriesPanel getTimeseriesPanel() {
    return m_PanelTimeseries;
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
   * Gets called if the data of the timeseries panel has changed.
   *
   * @param e		the event that the timeseries panel sent
   */
  @Override
  public void dataChanged(DataChangeEvent e) {
    updateMenu();
  }

  /**
   * Returns the current scripting engine, can be null.
   *
   * @return		the current engine
   */
  @Override
  public AbstractScriptingEngine getScriptingEngine() {
    return ScriptingEngine.getSingleton(DatabaseConnection.getSingleton());
  }

  /**
   * An undo event occurred.
   *
   * @param e		the event
   */
  @Override
  public void undoOccurred(UndoEvent e) {
    updateMenu();
  }

  /**
   * Returns the current container manager.
   *
   * @return		the manager
   */
  @Override
  public TimeseriesContainerManager getContainerManager() {
    return (TimeseriesContainerManager) m_PanelTimeseries.getContainerManager();
  }

  /**
   * Sets the manager for handling the containers.
   *
   * @param value	the manager
   */
  @Override
  public void setContainerManager(TimeseriesContainerManager value) {
    m_PanelTimeseries.setContainerManager(value);
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

    m_MenuItemEnableUndo.setSelected(m_Undo.isEnabled());
    m_MenuItemUndo.setEnabled(m_Undo.canUndo());
    if (m_Undo.canUndo()) {
      m_MenuItemUndo.setText("Undo - " + m_Undo.peekUndoComment());
      m_MenuItemUndo.setToolTipText(m_Undo.peekUndoComment());
    }
    else {
      m_MenuItemUndo.setText("Undo");
      m_MenuItemUndo.setToolTipText(null);
    }
    m_MenuItemRedo.setEnabled(m_Undo.canRedo());
    if (m_Undo.canRedo()) {
      m_MenuItemRedo.setText("Redo - " + m_Undo.peekRedoComment());
      m_MenuItemRedo.setToolTipText(m_Undo.peekRedoComment());
    }
    else {
      m_MenuItemRedo.setText("Redo");
      m_MenuItemRedo.setToolTipText(null);
    }
    m_MenuItemFilter.setEnabled(dataLoaded);

    m_MenuItemStartRecording.setEnabled(!getScriptingEngine().isRecording());
    m_MenuItemStopRecording.setEnabled(getScriptingEngine().isRecording());
    m_MenuItemViewAntiAliasing.setEnabled(getTimeseriesPanel().isAntiAliasingEnabled());
  }

  /**
   * re-builds the "Scripts" menu.
   */
  public void refreshScripts() {
    JMenuItem		menuitem;
    List<String>	scripts;
    int			i;
    String		name;

    scripts = getScriptingEngine().getAvailableScripts();

    // remove currently listed scripts
    i = 0;
    while (i < m_MenuScripts.getItemCount()) {
      if (m_MenuScripts.getItem(i) != m_MenuItemRefreshScripts) {
	i++;
      }
      else {
	i++;
	while (i < m_MenuScripts.getItemCount())
	  m_MenuScripts.remove(i);
      }
    }
    m_MenuScripts.addSeparator();

    // add scripts
    if (scripts.size() > 0) {
      for (i = 0; i < scripts.size(); i++) {
	final File file = new File(scripts.get(i));
	name = file.getName().replaceAll("_", " ");
	final boolean isFlow = name.endsWith("." + Flow.FILE_EXTENSION);
	if (isFlow)
	  name = name.replaceAll("\\." + Flow.FILE_EXTENSION + "$", "") + " [Flow]";
	menuitem = new JMenuItem(name);
	m_MenuScripts.add(menuitem);
	menuitem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    getScriptingEngine().clear();
	    if (isFlow) {
	      if (m_MenuItemOverlayFlowOutput.isSelected())
		getScriptingEngine().add(
		    getTimeseriesPanel(),
		    RunFlowOverlay.ACTION + " " + file.getAbsolutePath());
	      else
		getScriptingEngine().add(
		    getTimeseriesPanel(),
		    RunFlow.ACTION + " " + file.getAbsolutePath());
	    }
	    else {
	      getScriptingEngine().add(
		  getTimeseriesPanel(),
		  file);
	    }
	  }
	});
      }
    }
    else {
      menuitem = new JMenuItem("no scripts available");
      menuitem.setEnabled(false);
      m_MenuScripts.add(menuitem);
    }
  }

  /**
   * Executes a script.
   */
  public void manageScripts() {
    if (m_ScriptingDialog == null) {
      if (getParentDialog() != null)
	m_ScriptingDialog = new ScriptingDialog(getParentDialog(), this);
      else
	m_ScriptingDialog = new ScriptingDialog(getParentFrame(), this);

      m_ScriptingDialog.setLocationRelativeTo(this);
    }

    m_ScriptingDialog.setVisible(true);
  }

  /**
   * Starts the recording of commands.
   */
  public void startRecording() {
    if (!getScriptingEngine().isRecording())
      getScriptingEngine().startRecording();
    updateMenu();
  }

  /**
   * Stops the recording of commands.
   */
  public void stopRecording() {
    if (getScriptingEngine().isRecording())
      getScriptingEngine().stopRecording();
    updateMenu();
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  @Override
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
    JMenu		submenu;
    JMenuItem		menuitem;
    ButtonGroup		group;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      // File/Clear
      menuitem = new JMenuItem("Clear");
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
      menuitem.setIcon(GUIHelper.getIcon("new.gif"));
      menuitem.addActionListener((ActionEvent e) -> clearData());
      m_MenuItemClearData = menuitem;

      menu.addSeparator();

      // File/Database
      menuitem = new JMenuItem("Database...");
      menu.add(menuitem);
      menuitem.setMnemonic('D');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed D"));
      menuitem.setIcon(GUIHelper.getIcon("database.gif"));
      menuitem.addActionListener(e -> loadDataFromDatabase());

      // File/Open
      menuitem = new JMenuItem("Open...");
      menu.add(menuitem);
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.setIcon(GUIHelper.getIcon("open.gif"));
      menuitem.addActionListener(e -> loadDataFromDisk());

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandlerWithCommandline<>(
	  SESSION_FILE, getPropertiesEditor().getInteger("MaxRecentFlows", 5), submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,Setup>() {
        @Override
        public void recentItemAdded(RecentItemEvent<JMenu, Setup> e) {
          // ignored
        }

        @Override
        public void recentItemSelected(RecentItemEvent<JMenu, Setup> e) {
          AbstractDataContainerReader reader = (AbstractDataContainerReader) e.getItem().getHandler();
          reader.setInput(new PlaceholderFile(e.getItem().getFile()));
          getScriptingEngine().setDatabaseConnection(getDatabaseConnection());
          getScriptingEngine().add(TimeseriesExplorer.this, AddDataFile.ACTION + " " + OptionUtils.getCommandLine(reader));
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
      menuitem.addActionListener((ActionEvent e) -> close());

      // Edit
      menu = new JMenu("Edit");
      result.add(menu);
      menu.setMnemonic('E');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      // Edit/Enable Undo
      menuitem = new JCheckBoxMenuItem("Undo enabled");
      menu.add(menuitem);
      menuitem.setMnemonic('n');
      menuitem.setSelected(m_Undo.isEnabled());
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener((ActionEvent e) -> {
        if (m_MenuItemEnableUndo.isSelected())
          getScriptingEngine().add(
            getTimeseriesPanel(),
            EnableUndo.ACTION);
        else
          getScriptingEngine().add(
            getTimeseriesPanel(),
            DisableUndo.ACTION);
      });
      m_MenuItemEnableUndo = (JCheckBoxMenuItem) menuitem;

      // Edit/Undo
      menuitem = new JMenuItem("Undo");
      menu.add(menuitem);
      menuitem.setMnemonic('U');
      menuitem.setEnabled(m_Undo.canUndo());
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Z"));
      menuitem.setIcon(GUIHelper.getIcon("undo.gif"));
      menuitem.addActionListener((ActionEvent e) -> undo());
      m_MenuItemUndo = menuitem;

      menuitem = new JMenuItem("Redo");
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.setEnabled(m_Undo.canUndo());
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Y"));
      menuitem.setIcon(GUIHelper.getIcon("redo.gif"));
      menuitem.addActionListener((ActionEvent e) -> redo());
      m_MenuItemRedo = menuitem;

      // Process
      menu = new JMenu("Process");
      result.add(menu);
      menu.setMnemonic('P');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      // Process/Filter
      menuitem = new JMenuItem("Filter...");
      menu.add(menuitem);
      menuitem.setMnemonic('F');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed F"));
      menuitem.setIcon(GUIHelper.getIcon("run.gif"));
      menuitem.addActionListener((ActionEvent e) -> filter());
      m_MenuItemFilter = menuitem;

      // Scripts
      menu = new JMenu("Scripts");
      result.add(menu);
      menu.setMnemonic('S');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());
      m_MenuScripts = menu;

      // Scripts/Manage scripts
      menuitem = new JMenuItem("Manage...");
      menu.add(menuitem);
      menuitem.setMnemonic('m');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed M"));
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener((ActionEvent e) -> manageScripts());

      // Scripts/Start recording
      menuitem = new JMenuItem("Start recording");
      menu.add(menuitem);
      menuitem.setMnemonic('S');
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener((ActionEvent e) -> startRecording());
      m_MenuItemStartRecording = menuitem;

      // Scripts/Start recording
      menuitem = new JMenuItem("Stop recording");
      menu.add(menuitem);
      menuitem.setMnemonic('t');
      menuitem.setIcon(GUIHelper.getEmptyIcon());
      menuitem.addActionListener((ActionEvent e) -> stopRecording());
      m_MenuItemStopRecording = menuitem;

      // Scripts/Overlay flow output
      menuitem = new JCheckBoxMenuItem("Overlay flow output");
      menu.add(menuitem);
      menuitem.setMnemonic('o');
      menuitem.setSelected(true);
      m_MenuItemOverlayFlowOutput = menuitem;

      // Scripts/Refresh
      menuitem = new JMenuItem("Refresh");
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.addActionListener((ActionEvent e) -> refreshScripts());
      m_MenuItemRefreshScripts = menuitem;

      // View
      menu = new JMenu("View");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());
      m_MenuView = menu;

      // View/Display selected timestamp
      menuitem = new JCheckBoxMenuItem("Display selected timestamp");
      menu.add(menuitem);
      menuitem.setMnemonic('G');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed W"));
      menuitem.setSelected(getTimeseriesPanel().getSelectedTimestampPaintlet().isEnabled());
      menuitem.addActionListener((ActionEvent e) -> {
        getTimeseriesPanel().getSelectedTimestampPaintlet().setEnabled(m_MenuItemViewSelectedTimestamp.isSelected());
        getTimeseriesPanel().update();
      });
      m_MenuItemViewSelectedTimestamp = menuitem;

      // View/Display zoom overview
      menuitem = new JCheckBoxMenuItem("Display zoom overview");
      menu.add(menuitem);
      menuitem.setMnemonic('Z');
      menuitem.setSelected(isZoomOverviewPanelVisible());
      menuitem.addActionListener((ActionEvent e) -> 
	  setZoomOverviewPanelVisible(m_MenuItemViewZoomOverview.isSelected()));
      m_MenuItemViewZoomOverview = menuitem;

      // View/Anti-aliasing
      menuitem = new JCheckBoxMenuItem("Anti-aliasing");
      menu.add(menuitem);
      menuitem.setMnemonic('A');
      menuitem.setSelected(getTimeseriesPanel().isAntiAliasingEnabled());
      menuitem.addActionListener((ActionEvent e) -> 
	  getTimeseriesPanel().setAntiAliasingEnabled(m_MenuItemViewAntiAliasing.isSelected()));
      m_MenuItemViewAntiAliasing = menuitem;

      // View/Periodicity
      submenu = new JMenu("Periodicity");
      menu.add(submenu);
      submenu.setMnemonic('P');
      m_MenuItemViewPeriodicity = submenu;
      group = new ButtonGroup();
      for (final PeriodicityType type: PeriodicityType.values()) {
	menuitem = new JRadioButtonMenuItem(type.toString());
	group.add(menuitem);
	submenu.add(menuitem);
	menuitem.setSelected(type == getTimeseriesPanel().getPeriodicityPaintlet().getPeriodicity());
	menuitem.addActionListener((ActionEvent e) -> {
          getTimeseriesPanel().getPeriodicityPaintlet().setPeriodicity(type);
          if (getTimeseriesPanel().getPlot().getAxis(Axis.BOTTOM).getTickGenerator() instanceof PeriodicityTickGenerator)
            ((PeriodicityTickGenerator) getTimeseriesPanel().getPlot().getAxis(Axis.BOTTOM).getTickGenerator()).setPeriodicity(type);
          getTimeseriesPanel().getPlot().getAxis(Axis.BOTTOM).setNumberFormat(PeriodicityHelper.getFormat(type));
	});
      }

      // View/Color provider
      menuitem = new JMenuItem("Color provider...");
      menu.add(menuitem);
      menuitem.setMnemonic('P');
      menuitem.addActionListener((ActionEvent e) -> selectColorProvider());
      m_MenuItemViewColorProvider = menuitem;

      // View/Paintlet
      menuitem = new JMenuItem("Paintlet...");
      menu.add(menuitem);
      menuitem.setMnemonic('P');
      menuitem.addActionListener((ActionEvent e) -> selectPaintlet());
      m_MenuItemViewPaintlet = menuitem;

      // update menu
      m_MenuBar = result;
      refreshScripts();
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
    getScriptingEngine().setDatabaseConnection(DatabaseConnection.getSingleton());
    getScriptingEngine().add(
	getTimeseriesPanel(),
	ClearData.ACTION);
  }

  /**
   * pops up file chooser dialog for timeseries readers.
   */
  public void loadDataFromDisk() {
    int				retVal;
    int				i;
    PlaceholderFile[]		files;
    List<String>		opts;
    AbstractDataContainerReader	reader;

    retVal = m_TimeseriesFileChooser.showOpenDialog(this);
    if (retVal != TimeseriesFileChooser.APPROVE_OPTION)
      return;

    files  = m_TimeseriesFileChooser.getSelectedPlaceholderFiles();
    reader = m_TimeseriesFileChooser.getReader();
    if (files.length == 1) {
      reader.setInput(files[0]);
      getScriptingEngine().setDatabaseConnection(DatabaseConnection.getSingleton());
      getScriptingEngine().add(
	getTimeseriesPanel(), AddDataFile.ACTION + " " + OptionUtils.getCommandLine(reader));
      if (m_RecentFilesHandler != null)
	m_RecentFilesHandler.addRecentItem(new Setup(files[0], reader));
    }
    else {
      opts = new ArrayList<>();
      opts.add(OptionUtils.getCommandLine(reader));
      for (i = 0; i < files.length; i++)
	opts.add(files[i].toString());
      getScriptingEngine().setDatabaseConnection(DatabaseConnection.getSingleton());
      getScriptingEngine().add(
	getTimeseriesPanel(), AddDataFiles.ACTION + " " + OptionUtils.joinOptions(opts.toArray(new String[opts.size()])));
      if (m_RecentFilesHandler != null) {
	for (i = 0; i < files.length; i++)
	  m_RecentFilesHandler.addRecentItem(new Setup(files[i], reader));
      }
    }
  }

  /**
   * pops up dialog for SQL statement.
   */
  public void loadDataFromDatabase() {
    SequenceSource				seq;
    String					msg;
    List<Token>					tokens;
    TimeseriesContainerManager			manager;
    TimeseriesContainer				cont;
    Properties					props;
    String					query;
    
    if (m_DialogSQL == null) {
      if (getParentDialog() != null)
	m_DialogSQL = new TimeseriesImportDatabaseDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_DialogSQL = new TimeseriesImportDatabaseDialog(getParentFrame(), true);
      m_DialogSQL.setDefaultCloseOperation(TimeseriesImportDatabaseDialog.HIDE_ON_CLOSE);
      m_DialogSQL.setTitle("Load timeseries from database");
      m_DialogSQL.setSize(GUIHelper.getDefaultDialogDimension());
    }
    m_DialogSQL.setLocationRelativeTo(this);
    m_DialogSQL.setVisible(true);
    if (m_DialogSQL.getOption() != SQLStatementDialog.APPROVE_OPTION)
      return;

    // load timeseries from DB
    seq = new SequenceSource();
    
    props = m_DialogSQL.getProperties(false);
    adams.flow.standalone.DatabaseConnection dbcon = new adams.flow.standalone.DatabaseConnection();
    dbcon.setURL(props.getProperty(DatabaseConnectionPage.CONNECTION_URL));
    dbcon.setUser(props.getProperty(DatabaseConnectionPage.CONNECTION_USER));
    dbcon.setPassword(props.getPassword(DatabaseConnectionPage.CONNECTION_PASSWORD));
    seq.add(dbcon);
    
    String[] ids = props.getProperty(ListPage.KEY_SELECTED).split(",");
    StringConstants sconst = new StringConstants();
    sconst.setStrings((BaseString[]) BaseObject.toObjectArray(ids, BaseString.class));
    seq.add(sconst);

    TimeseriesDbReader reader = new TimeseriesDbReader();
    query = props.getProperty(TimeseriesImportDatabaseDialog.QUERY_DATA, "");
    reader.setSQL(new SQLStatement(query));
    seq.add(reader);

    TimeseriesReportDbUpdater upd = new TimeseriesReportDbUpdater();
    upd.setLenient(true);
    if (props.getProperty(TimeseriesImportDatabaseDialog.QUERY_METADATA_KEYVALUE, "").trim().length() > 0) {
      upd.setQueryType(QueryType.KEY_VALUE);
      upd.setSQL(new SQLStatement(props.getProperty(TimeseriesImportDatabaseDialog.QUERY_METADATA_KEYVALUE)));
      seq.add(upd);
    }
    else if (props.getProperty(TimeseriesImportDatabaseDialog.QUERY_METADATA_ROW, "").trim().length() > 0) {
      upd.setQueryType(QueryType.COLUMN_AS_KEY);
      upd.setSQL(new SQLStatement(props.getProperty(TimeseriesImportDatabaseDialog.QUERY_METADATA_ROW)));
      seq.add(upd);
    }
    
    // execute flow
    msg = seq.setUp();
    if (msg != null) {
      GUIHelper.showErrorMessage(this, msg);
      seq.cleanUp();
      return;
    }
    msg = seq.execute();
    if (msg != null) {
      GUIHelper.showErrorMessage(this, msg);
      seq.cleanUp();
      return;
    }
    
    // display timeseries
    tokens = seq.getOutputTokens();
    manager = getContainerManager();
    manager.startUpdate();
    for (Token token: tokens) {
      if (token.getPayload() == null)
	continue;
      cont = manager.newContainer((Timeseries) token.getPayload());
      manager.add(cont);
    }
    manager.finishUpdate();

    ConsolePanel.getSingleton().append(
	LoggingLevel.INFO, "Timeseries from database:\n" + seq.toCommandLine());
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
   * peforms an undo if possible.
   */
  public void undo() {
    if (!m_Undo.canUndo())
      return;

    SwingWorker worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	showStatus("Performing Undo...");

	// add redo point
	m_Undo.addRedo(getContainerManager().getAll(), m_Undo.peekUndoComment());

	UndoPoint point = m_Undo.undo();
	List<TimeseriesContainer> data = (List<TimeseriesContainer>) point.getData();
	getContainerManager().clear();
	getContainerManager().addAll(data);

	return "Done!";
      };

      @Override
      protected void done() {
        super.done();
	showStatus("");
      }
    };
    worker.execute();
  }

  /**
   * peforms a redo if possible.
   */
  public void redo() {
    if (!m_Undo.canRedo())
      return;

    SwingWorker worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	showStatus("Performing Redo...");

	// add undo point
	m_Undo.addUndo(getContainerManager().getAll(), m_Undo.peekRedoComment(), true);

	UndoPoint point = m_Undo.redo();
	List<TimeseriesContainer> data = (List<TimeseriesContainer>) point.getData();
	getContainerManager().clear();
	getContainerManager().addAll(data);

	return "Done!";
      };

      @Override
      protected void done() {
        super.done();
	showStatus("");
      }
    };
    worker.execute();
  }

  /**
   * pops up GOE dialog for filter.
   */
  public void filter() {
    if (m_DialogFilter == null) {
      if (getParentDialog() != null)
	m_DialogFilter = new FilterDialog(getParentDialog());
      else
	m_DialogFilter = new FilterDialog(getParentFrame());
      m_DialogFilter.setFilterListener(this);
    }

    m_DialogFilter.setFilter(m_CurrentFilter);
    m_DialogFilter.setOverlayOriginalData(m_FilterOverlayOriginalData);
    m_DialogFilter.setLocationRelativeTo(this);
    m_DialogFilter.setVisible(true);
  }

  /**
   * Filters the data.
   *
   * @param e		the event
   */
  @Override
  public void filter(FilterEvent<Timeseries> e) {
    m_CurrentFilter             = e.getFilter();
    m_FilterOverlayOriginalData = e.getOverlayOriginalData();

    if (m_FilterOverlayOriginalData)
      getScriptingEngine().add(
	  getTimeseriesPanel(),
	  FilterOverlay.ACTION + " " + OptionUtils.getCommandLine(m_CurrentFilter));
    else
      getScriptingEngine().add(
	  getTimeseriesPanel(),
	  Filter.ACTION + " " + OptionUtils.getCommandLine(m_CurrentFilter));
  }

  /**
   * Sets the zoom overview panel visible or not.
   *
   * @param value	if true then the panel is visible
   */
  public void setZoomOverviewPanelVisible(boolean value) {
    m_PanelTimeseries.setZoomOverviewPanelVisible(value);
  }

  /**
   * Returns whether the zoom overview panel is visible or not.
   *
   * @return		true if visible
   */
  public boolean isZoomOverviewPanelVisible() {
    return m_PanelTimeseries.isZoomOverviewPanelVisible();
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
   * Lets the user select a new paintlet.
   */
  protected void selectPaintlet() {
    Paintlet paintlet;
    boolean	zoomVisible;

    if (m_DialogPaintlet == null) {
      if (getParentDialog() != null)
	m_DialogPaintlet = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
      else
	m_DialogPaintlet = new GenericObjectEditorDialog(getParentFrame(), true);
      m_DialogPaintlet.setTitle("Select paintlet");
      m_DialogPaintlet.getGOEEditor().setClassType(AbstractTimeseriesPaintlet.class);
      m_DialogPaintlet.getGOEEditor().setCanChangeClassInDialog(true);
      m_DialogPaintlet.setLocationRelativeTo(this);
    }
    
    m_DialogPaintlet.setCurrent(getTimeseriesPanel().getTimeseriesPaintlet().shallowCopy());
    m_DialogPaintlet.setVisible(true);
    if (m_DialogPaintlet.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;
    paintlet = (Paintlet) m_DialogPaintlet.getCurrent();
    paintlet.setPanel(getTimeseriesPanel());
    getTimeseriesPanel().removePaintlet(getTimeseriesPanel().getTimeseriesPaintlet());
    getTimeseriesPanel().addPaintlet(paintlet);
    zoomVisible = getTimeseriesPanel().isZoomOverviewPanelVisible();
    getTimeseriesPanel().getZoomOverviewPanel().setDataContainerPanel(getTimeseriesPanel());
    getTimeseriesPanel().setZoomOverviewPanelVisible(zoomVisible);
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  @Override
  public Class[] getSendToClasses() {
    return new Class[]{JComponent.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve an item for
   * @return		true if an object is available for sending
   */
  @Override
  public boolean hasSendToItem(Class[] cls) {
    return (getContainerManager().countVisible() > 0);
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  @Override
  public Object getSendToItem(Class[] cls) {
    Object	result;

    result = null;

    if (SendToActionUtils.isAvailable(JComponent.class, cls)) {
      if (getContainerManager().countVisible() > 0) {
	result = this;
      }
    }

    return result;
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_DatabaseConnection;
  }

  /**
   * Sets the database connection object to use.
   *
   * @param value	the object to use
   */
  public void setDatabaseConnection(AbstractDatabaseConnection value) {
    m_DatabaseConnection = value;
  }

  /**
   * A change in the database connection occurred.
   *
   * @param e		the event
   */
  @Override
  public void databaseConnectionStateChanged(DatabaseConnectionChangeEvent e) {
    if (e.getType() == EventType.CONNECT)
      m_DatabaseConnection = e.getDatabaseConnection();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    m_PanelTimeseries.getContainerManager().removeDataChangeListener(this);
    m_PanelTimeseries.cleanUp();
    if (m_ScriptingDialog != null)
      m_ScriptingDialog.cleanUp();
    if (m_DialogSQL != null) {
      m_DialogSQL.dispose();
      m_DialogSQL = null;
    }
    if (m_DialogColorProvider != null) {
      m_DialogColorProvider.dispose();
      m_DialogColorProvider = null;
    }
    if (m_DialogPaintlet != null) {
      m_DialogPaintlet.dispose();
      m_DialogPaintlet = null;
    }
  }
}
