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
 * ChartTarget.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetprocessor.targets;

import adams.core.MessageCollection;
import adams.core.logging.LoggingLevel;
import adams.core.option.OptionUtils;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.Flow;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTextField;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.ParameterPanel;
import adams.gui.event.SpreadSheetProcessorEvent;
import adams.gui.event.SpreadSheetProcessorEvent.EventType;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.spreadsheetprocessor.AbstractWidget;
import adams.gui.tools.spreadsheetviewer.chart.AbstractChartGenerator;
import adams.gui.tools.spreadsheetviewer.chart.ScatterPlot;

import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * For displaying the data in a chart.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ChartTarget
  extends AbstractTarget {

  private static final long serialVersionUID = 6535516712611654393L;

  public static final String KEY_CHART = "chart";

  public static final String KEY_TITLE = "title";

  /** the widget. */
  protected BasePanel m_Widget;

  /** the chart to use. */
  protected GenericObjectEditorPanel m_PanelChart;

  /** the chart title to use. */
  protected BaseTextField m_TextTitle;

  /** the button for generating the chart. */
  protected BaseButton m_ButtonGenerate;

  /**
   * Returns the name of the widget.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Chart";
  }

  /**
   * Returns the widget.
   *
   * @return		the widget
   */
  @Override
  public Component getWidget() {
    ParameterPanel 	panelParams;
    JPanel		panel;

    if (m_Widget == null) {
      m_Widget = new BasePanel(new BorderLayout());
      panelParams = new ParameterPanel();
      m_Widget.add(panelParams, BorderLayout.CENTER);

      m_PanelChart = new GenericObjectEditorPanel(AbstractChartGenerator.class, new ScatterPlot());
      panelParams.addParameter("Chart", m_PanelChart);

      m_TextTitle = new BaseTextField();
      panelParams.addParameter("Title", m_TextTitle);

      m_ButtonGenerate = new BaseButton(ImageManager.getIcon("run.gif"));
      m_ButtonGenerate.addActionListener((ActionEvent e) -> generate());
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      panel.add(m_ButtonGenerate);
      panelParams.addParameter("Generate", panel);
    }

    return m_Widget;
  }

  /**
   * Updates the widget.
   */
  public void update() {
    m_ButtonGenerate.setEnabled(m_Owner.getProcessorData() != null);
  }

  /**
   * Loads the data
   */
  protected void generate() {
    SwingWorker worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
        m_ButtonGenerate.setEnabled(false);
	m_Owner.processorStateChanged(new SpreadSheetProcessorEvent(m_Owner, EventType.OUTPUT_DATA, "Generated chart: " + OptionUtils.getCommandLine(m_PanelChart.getCurrent())));
	return null;
      }

      @Override
      protected void done() {
	super.done();
        m_ButtonGenerate.setEnabled(true);
      }
    };
    worker.execute();
  }

  /**
   * Sets the current chart generator.
   *
   * @param value	the generator
   */
  public void setCurrentChart(AbstractChartGenerator value) {
    m_PanelChart.setCurrent(value);
  }

  /**
   * Returns the current chart generator.
   *
   * @return		the generator
   */
  public AbstractChartGenerator getCurrentChart() {
    return (AbstractChartGenerator) m_PanelChart.getCurrent();
  }

  /**
   * Sets the current title.
   *
   * @param value	the title
   */
  public void setCurrentTitle(String value) {
    m_TextTitle.setText(value);
  }

  /**
   * Returns the current title.
   *
   * @return		the title
   */
  public String getCurrentTitle() {
    return m_TextTitle.getText();
  }

  /**
   * Retrieves the values from the other widget, if possible.
   *
   * @param other	the other widget to get the values from
   */
  public void assign(AbstractWidget other) {
    ChartTarget widget;

    if (other instanceof ChartTarget) {
      widget = (ChartTarget) other;
      widget.getWidget();
      setCurrentChart(widget.getCurrentChart());
      setCurrentTitle(widget.getCurrentTitle());
    }
  }

  /**
   * Serializes the setup from the widget.
   *
   * @return		the generated setup representation
   */
  public Object serialize() {
    Map<String,Object> result;

    result = new HashMap<>();
    result.put(KEY_CHART, OptionUtils.getCommandLine(getCurrentChart()));
    result.put(KEY_TITLE, getCurrentTitle());

    return result;
  }

  /**
   * Deserializes the setup and maps it onto the widget.
   *
   * @param data	the setup representation to use
   * @param errors	for collecting errors
   */
  public void deserialize(Object data, MessageCollection errors) {
    Map<String,Object>	map;

    if (data instanceof Map) {
      map = (Map<String,Object>) data;
      if (map.containsKey(KEY_CHART)) {
        try {
	  setCurrentChart((AbstractChartGenerator) OptionUtils.forAnyCommandLine(AbstractChartGenerator.class, (String) map.get(KEY_CHART)));
	}
	catch (Exception e) {
	  errors.add(getClass().getName() + ": Failed to instantiate chart from: " + map.get(KEY_CHART));
	}
      }
      if (map.containsKey(KEY_TITLE))
        m_TextTitle.setText((String) map.get(KEY_TITLE));
      update();
    }
    else {
      errors.add(getClass().getName() + ": Deserialization data is not a map!");
    }
  }

  /**
   * Processes the data.
   *
   * @param data	the input data
   * @param errors	for storing errors
   */
  @Override
  protected void doProcess(SpreadSheet data, MessageCollection errors) {
    final AbstractChartGenerator	generator;
    String				title;
    SwingWorker	  			worker;
    final Flow    			flow;

    generator = (AbstractChartGenerator) m_PanelChart.getCurrent();
    title     = (m_TextTitle.getText().isEmpty() ? "Chart" : m_TextTitle.getText());
    flow      = generator.generate(title, data);
    flow.setParentComponent(m_Owner);

    worker = new SwingWorker() {
      String msg = null;

      @Override
      protected Object doInBackground() throws Exception {
	msg = flow.setUp();
	if (msg != null)
	  msg = "Failed to setup flow for generating chart:\n" + msg;

	if (msg == null) {
	  msg = flow.execute();
	  if (msg != null)
	    msg = "Failed to execute flow for generating chart:\n" + msg;
	}

	if (msg == null) {
	  flow.wrapUp();
	  if (flow.hasStopMessage())
	    msg = "Flow execution for generating chart was stopped:\n" + flow.getStopMessage();
	}

        return msg;
      }

      @Override
      protected void done() {
        super.done();
        if (msg != null) {
          GUIHelper.showErrorMessage(m_Owner, msg);
          ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, msg + "\n");
          ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, flow.toCommandLine() + "\n");
          flow.destroy();
        }
        else {
          m_Owner.addGeneratedFlow(flow);
          notifyOwner(EventType.DATA_IS_OUTPUT, "Generated chart: " + generator.toCommandLine());
        }
      }
    };

    worker.execute();
  }
}
