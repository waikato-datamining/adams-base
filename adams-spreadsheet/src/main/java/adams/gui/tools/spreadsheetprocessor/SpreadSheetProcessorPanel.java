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
 * SpreadSheetProcessorPanel.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetprocessor;

import adams.core.ClassLister;
import adams.core.MessageCollection;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.application.ChildFrame;
import adams.gui.application.ChildWindow;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.core.SpreadSheetTableWithSearch;
import adams.gui.core.UISettings;
import adams.gui.event.SpreadSheetProcessorEvent;
import adams.gui.event.SpreadSheetProcessorListener;
import adams.gui.tools.spreadsheetprocessor.processors.AbstractProcessor;
import adams.gui.tools.spreadsheetprocessor.processors.QueryProcessor;
import adams.gui.tools.spreadsheetprocessor.sources.AbstractSource;
import adams.gui.tools.spreadsheetprocessor.sources.FileSource;
import adams.gui.tools.spreadsheetprocessor.targets.AbstractTarget;
import adams.gui.tools.spreadsheetprocessor.targets.FileTarget;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The main panel for processing spreadsheets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetProcessorPanel
  extends BasePanel
  implements MenuBarProvider, SpreadSheetProcessorListener {

  private static final long serialVersionUID = -8779070213062972306L;

  /**
   * Encapsulates combobox to select a widget and the selected widget.
   */
  public static class WidgetPanel
    extends BasePanel {

    private static final long serialVersionUID = -2509485998585342076L;

    /** the owner. */
    protected SpreadSheetProcessorPanel m_Owner;

    /** the label to use. */
    protected String m_Label;

    /** the widget class. */
    protected Class m_WidgetClass;

    /** the default widget. */
    protected AbstractWidget m_DefaultWidget;

    /** the available widgets. */
    protected List<AbstractWidget> m_Widgets;

    /** the combobox with the widgets. */
    protected BaseComboBox<AbstractWidget> m_ComboBoxWidgets;

    /** the panel for the widget. */
    protected BasePanel m_PanelWidget;

    /** the current widget. */
    protected AbstractWidget m_CurrentWidget;

    /**
     * Initializes the panel.
     *
     * @param label		the label
     * @param widgetClass	the superclass
     * @param defaultWidget	the default widget
     */
    public WidgetPanel(SpreadSheetProcessorPanel owner, String label, Class widgetClass, AbstractWidget defaultWidget) {
      m_Owner         = owner;
      m_Label         = label;
      m_WidgetClass   = widgetClass;
      m_DefaultWidget = defaultWidget;
      m_CurrentWidget = null;

      initGUI();
      finishInit();
    }

    /**
     * For initializing the GUI.
     */
    @Override
    protected void initGUI() {
      Class[]		classes;
      AbstractWidget	widget;
      JPanel		panel;
      JLabel		label;

      if (m_WidgetClass == null)
        return;

      super.initGUI();

      classes   = ClassLister.getSingleton().getClasses(m_WidgetClass);
      m_Widgets = new ArrayList<>();
      for (Class cls: classes) {
        try {
          widget = (AbstractWidget) cls.newInstance();
          widget.setOwner(m_Owner);
          m_Widgets.add(widget);
	}
	catch (Exception e) {
	  ConsolePanel.getSingleton().append("Failed to instantiate: " + cls.getName(), e);
	}
      }
      Collections.sort(m_Widgets);
      m_ComboBoxWidgets = new BaseComboBox<>(m_Widgets);
      m_ComboBoxWidgets.addActionListener((ActionEvent e) -> update());
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      add(panel, BorderLayout.NORTH);
      label = new JLabel(m_Label);
      label.setLabelFor(m_ComboBoxWidgets);
      panel.add(label);
      panel.add(m_ComboBoxWidgets);

      m_PanelWidget = new BasePanel(new BorderLayout());
      add(m_PanelWidget, BorderLayout.CENTER);
    }

    /**
     * Finishes the initialization.
     */
    @Override
    protected void finishInit() {
      if (m_WidgetClass == null)
        return;

      super.finishInit();

      m_ComboBoxWidgets.setSelectedItem(m_DefaultWidget);
    }

    /**
     * Updates the widget.
     */
    protected void update() {
      if (m_ComboBoxWidgets.getSelectedIndex() < 0)
        return;

      m_CurrentWidget = m_ComboBoxWidgets.getSelectedItem();

      m_PanelWidget.removeAll();
      if (m_CurrentWidget.getWidget() != null)
	m_PanelWidget.add(m_CurrentWidget.getWidget(), BorderLayout.CENTER);
      m_PanelWidget.invalidate();
      m_PanelWidget.revalidate();
      m_PanelWidget.repaint();
    }

    /**
     * Returns the current widget.
     *
     * @return		the widget, null if none set
     */
    public AbstractWidget getCurrentWidget() {
      return m_CurrentWidget;
    }

    /**
     * Sets the current widget.
     *
     * @param value	the widget
     */
    public void setCurrentWidget(AbstractWidget value) {
      int	i;

      if (ClassLocator.isSubclass(m_WidgetClass, value.getClass())) {
        for (i = 0; i < m_Widgets.size(); i++) {
          if (m_Widgets.get(i).equals(value)) {
            m_Widgets.get(i).assign(value);
	    m_ComboBoxWidgets.setSelectedIndex(i);
	    break;
	  }
	}
      }
    }
  }

  /** the 1st vertical split pane. */
  protected BaseSplitPane m_SplitPanelVert1;

  /** the 2nd vertical split pane. */
  protected BaseSplitPane m_SplitPanelVert2;

  /** the left split pane. */
  protected BaseSplitPane m_SplitPanelLeft;

  /** the center split pane. */
  protected BaseSplitPane m_SplitPanelCenter;

  /** the right split pane. */
  protected BaseSplitPane m_SplitPanelRight;

  /** the widget panel for the source. */
  protected WidgetPanel m_PanelSource;

  /** the table for displaying the spreadsheet. */
  protected SpreadSheetTableWithSearch m_TableSource;

  /** the spreadsheet model. */
  protected SpreadSheetTableModel m_TableModelSource;

  /** the widget panel for the processor. */
  protected WidgetPanel m_PanelProcessor;

  /** the table for displaying the processed spreadsheet. */
  protected SpreadSheetTableWithSearch m_TableProcessor;

  /** the spreadsheet model. */
  protected SpreadSheetTableModel m_TableModelProcessor;

  /** the widget panel for the target. */
  protected WidgetPanel m_PanelTarget;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the status bar. */
  protected BaseStatusBar m_StatusBar;

  /** the source data. */
  protected SpreadSheet m_DataSource;

  /** the processed data. */
  protected SpreadSheet m_DataProcessor;

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();
    
    setLayout(new BorderLayout());

    m_PanelSource    = new WidgetPanel(this, "Source", AbstractSource.class, new FileSource());
    m_PanelProcessor = new WidgetPanel(this, "Processor", AbstractProcessor.class, new QueryProcessor());
    m_PanelTarget    = new WidgetPanel(this, "Target", AbstractTarget.class, new FileTarget());

    m_TableModelSource = new SpreadSheetTableModel(new DefaultSpreadSheet());
    m_TableModelSource.setReadOnly(true);
    m_TableSource      = new SpreadSheetTableWithSearch(m_TableModelSource);
    m_TableSource.setUseOptimalColumnWidths(true);
    m_TableSource.setColumnsDropdownVisible(true);

    m_TableModelProcessor = new SpreadSheetTableModel(new DefaultSpreadSheet());
    m_TableModelProcessor.setReadOnly(true);
    m_TableProcessor      = new SpreadSheetTableWithSearch(m_TableModelProcessor);
    m_TableProcessor.setUseOptimalColumnWidths(true);
    m_TableProcessor.setColumnsDropdownVisible(true);

    m_SplitPanelLeft = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPanelLeft.setOneTouchExpandable(true);
    m_SplitPanelLeft.setDividerLocation(UISettings.get(getClass(), "DividerLeft", 300));
    m_SplitPanelLeft.setUISettingsParameters(getClass(), "DividerLeft");
    m_SplitPanelLeft.setTopComponent(m_PanelSource);
    m_SplitPanelLeft.setBottomComponent(m_TableSource);

    m_SplitPanelCenter = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPanelCenter.setOneTouchExpandable(true);
    m_SplitPanelCenter.setDividerLocation(UISettings.get(getClass(), "DividerCenter", 300));
    m_SplitPanelCenter.setUISettingsParameters(getClass(), "DividerCenter");
    m_SplitPanelCenter.setTopComponent(m_PanelProcessor);
    m_SplitPanelCenter.setBottomComponent(m_TableProcessor);

    m_SplitPanelRight = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPanelRight.setOneTouchExpandable(true);
    m_SplitPanelRight.setDividerLocation(UISettings.get(getClass(), "DividerRight", 300));
    m_SplitPanelRight.setUISettingsParameters(getClass(), "DividerRight");
    m_SplitPanelRight.setTopComponent(m_PanelTarget);

    m_SplitPanelVert2 = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPanelVert2.setOneTouchExpandable(true);
    m_SplitPanelVert2.setDividerLocation(UISettings.get(getClass(), "DividerVert2", 300));
    m_SplitPanelVert2.setUISettingsParameters(getClass(), "DividerVert2");
    m_SplitPanelVert2.setLeftComponent(m_SplitPanelCenter);
    m_SplitPanelVert2.setRightComponent(m_SplitPanelRight);

    m_SplitPanelVert1 = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPanelVert1.setOneTouchExpandable(true);
    m_SplitPanelVert1.setDividerLocation(UISettings.get(getClass(), "DividerVert1", 300));
    m_SplitPanelVert1.setUISettingsParameters(getClass(), "DividerVert1");
    m_SplitPanelVert1.setLeftComponent(m_SplitPanelLeft);
    m_SplitPanelVert1.setRightComponent(m_SplitPanelVert2);
    add(m_SplitPanelVert1, BorderLayout.CENTER);

    m_StatusBar = new BaseStatusBar();
    m_StatusBar.setMouseListenerActive(true);
    add(m_StatusBar, BorderLayout.SOUTH);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    getSourceWidget().update();
    getProcessorWidget().update();
    getTargetWidget().update();
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
    JMenuItem		menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener((ActionEvent e) -> closeParent());

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
   * Updates the menu.
   */
  protected void updateMenu() {
  }

  /**
   * Sets the source widget.
   *
   * @param value	the source
   */
  public void setSourceWidget(AbstractSource value) {
    m_PanelSource.setCurrentWidget(value);
    m_PanelSource.update();
  }

  /**
   * Returns the source widget.
   *
   * @return		the source
   */
  public AbstractSource getSourceWidget() {
    return (AbstractSource) m_PanelSource.getCurrentWidget();
  }

  /**
   * Sets the processor widget.
   * 
   * @param value	the processor
   */
  public void setProcessorWidget(AbstractProcessor value) {
    m_PanelProcessor.setCurrentWidget(value);
    m_PanelProcessor.update();
  }
  
  /**
   * Returns the processor widget.
   *
   * @return		the process
   */
  public AbstractProcessor getProcessorWidget() {
    return (AbstractProcessor) m_PanelProcessor.getCurrentWidget();
  }

  /**
   * Sets the target widget.
   * 
   * @param value	the target
   */
  public void setTargetWidget(AbstractTarget value) {
    m_PanelTarget.setCurrentWidget(value);
    m_PanelTarget.update();
  }

  /**
   * Returns the target widget.
   *
   * @return		the target
   */
  public AbstractTarget getTargetWidget() {
    return (AbstractTarget) m_PanelTarget.getCurrentWidget();
  }

  /**
   * Returns the source data.
   *
   * @return		the data, null if not available
   */
  public SpreadSheet getSourceData() {
    return m_DataSource;
  }

  /**
   * Returns the processed data.
   *
   * @return		the data, null if not available
   */
  public SpreadSheet getProcessorData() {
    return m_DataProcessor;
  }

  /**
   * Displays a new preview window/frame.
   *
   * @return		the new panel
   */
  public SpreadSheetProcessorPanel newWindow() {
    SpreadSheetProcessorPanel 	result;
    ChildFrame 			oldFrame;
    ChildFrame 			newFrame;
    ChildWindow 		oldWindow;
    ChildWindow 		newWindow;

    result    = null;
    oldFrame = (ChildFrame) GUIHelper.getParent(this, ChildFrame.class);
    if (oldFrame != null) {
      newFrame = oldFrame.getNewWindow();
      newFrame.setVisible(true);
      result  = (SpreadSheetProcessorPanel) newFrame.getContentPane().getComponent(0);
    }
    else {
      oldWindow = (ChildWindow) GUIHelper.getParent(this, ChildWindow.class);
      if (oldWindow != null) {
	newWindow = oldWindow.getNewWindow();
	newWindow.setVisible(true);
	result  = (SpreadSheetProcessorPanel) newWindow.getContentPane().getComponent(0);
      }
    }

    if (result != null) {
      // use same source/processor/target
      result.setSourceWidget(getSourceWidget());
      result.setProcessorWidget(getProcessorWidget());
      result.setTargetWidget(getTargetWidget());
    }

    return result;
  }

  /**
   * Gets triggered whenever the processor changes state.
   *
   * @param e		the event
   */
  public void processorStateChanged(SpreadSheetProcessorEvent e) {
    SwingWorker		worker;

    m_StatusBar.setStatus(e.getMessage() == null ? "" : e.getMessage());

    switch (e.getType()) {
      case DATA_IS_AVAILABLE:
        worker = new SwingWorker() {
	  @Override
	  protected Object doInBackground() throws Exception {
	    AbstractSource widget = getSourceWidget();
	    if (widget.hasData()) {
	      m_DataSource = widget.getData();
	      m_DataProcessor = null;
	      m_TableModelSource = new SpreadSheetTableModel(m_DataSource);
	      m_TableSource.setModel(m_TableModelSource);
	    }
	    else {
	      m_DataSource = null;
	      m_DataProcessor = null;
	      m_TableModelSource = new SpreadSheetTableModel();
	      m_TableSource.setModel(m_TableModelSource);
	    }
	    getProcessorWidget().update();
	    getTargetWidget().update();
	    return null;
	  }
	};
        worker.execute();
        break;

      case PROCESS_DATA:
        worker = new SwingWorker() {
	  @Override
	  protected Object doInBackground() throws Exception {
	    AbstractProcessor widget = getProcessorWidget();
	    MessageCollection errors = new MessageCollection();
	    if (m_DataSource != null) {
	      m_DataProcessor = widget.process(m_DataSource, errors);
	      if (errors.isEmpty()) {
		m_TableModelProcessor = new SpreadSheetTableModel(m_DataProcessor);
		m_TableProcessor.setModel(m_TableModelProcessor);
	      }
	      else {
	        m_DataProcessor = null;
		m_TableModelProcessor = new SpreadSheetTableModel();
		m_TableProcessor.setModel(m_TableModelProcessor);
	      }
	      getTargetWidget().update();
	    }
	    if (!errors.isEmpty())
	      GUIHelper.showErrorMessage(SpreadSheetProcessorPanel.this, errors.toString());
	    return null;
	  }
	};
        worker.execute();
        break;

      case OUTPUT_DATA:
        worker = new SwingWorker() {
	  @Override
	  protected Object doInBackground() throws Exception {
	    AbstractTarget widget = getTargetWidget();
	    MessageCollection errors = new MessageCollection();
	    if (m_DataProcessor != null) {
	      widget.process(m_DataProcessor, errors);
	      if (errors.isEmpty()) {
	        // TODO display if graphical
		m_SplitPanelRight.setBottomComponentHidden(true);
	      }
	    }
	    if (!errors.isEmpty())
	      GUIHelper.showErrorMessage(SpreadSheetProcessorPanel.this, errors.toString());
	    return null;
	  }
	};
        worker.execute();
        break;
    }
  }
}
