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
 * ClipboardTarget.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetprocessor.targets;

import adams.core.MessageCollection;
import adams.core.option.OptionUtils;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.ImageManager;
import adams.gui.core.ParameterPanel;
import adams.gui.event.SpreadSheetProcessorEvent;
import adams.gui.event.SpreadSheetProcessorEvent.EventType;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.spreadsheetprocessor.AbstractWidget;
import adams.gui.tools.spreadsheetviewer.chart.AbstractChartGenerator;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * For copying the processed data to the clipboard.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ClipboardTarget
  extends AbstractTarget {

  private static final long serialVersionUID = 6535516712611654393L;

  public static final String KEY_CHART = "chart";

  /** the widget. */
  protected BasePanel m_Widget;

  /** the writer to use. */
  protected GenericObjectEditorPanel m_PanelWriter;

  /** the button for copying. */
  protected BaseButton m_ButtonCopy;

  /**
   * Returns the name of the widget.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Clipboard";
  }

  /**
   * Returns the widget.
   *
   * @return		the widget
   */
  @Override
  public Component getWidget() {
    ParameterPanel 		panelParams;
    JPanel			panel;
    CsvSpreadSheetWriter	writer;

    if (m_Widget == null) {
      m_Widget = new BasePanel(new BorderLayout());
      panelParams = new ParameterPanel();
      m_Widget.add(panelParams, BorderLayout.CENTER);

      writer = new CsvSpreadSheetWriter();
      writer.setSeparator("\\t");
      m_PanelWriter = new GenericObjectEditorPanel(SpreadSheetWriter.class, writer, false);
      panelParams.addParameter("Writer", m_PanelWriter);

      m_ButtonCopy = new BaseButton(ImageManager.getIcon("run.gif"));
      m_ButtonCopy.addActionListener((ActionEvent e) -> copy());
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      panel.add(m_ButtonCopy);
      panelParams.addParameter("Copy", panel);
    }

    return m_Widget;
  }

  /**
   * Updates the widget.
   */
  public void update() {
    m_ButtonCopy.setEnabled(m_Owner.getProcessorData() != null);
  }

  /**
   * Sets the current writer.
   *
   * @param value	the writer
   */
  public void setCurrentWriter(SpreadSheetWriter value) {
    m_PanelWriter.setCurrent(value);
  }

  /**
   * Returns the current writer.
   *
   * @return		the writer
   */
  public SpreadSheetWriter getCurrentWriter() {
    return (SpreadSheetWriter) m_PanelWriter.getCurrent();
  }

  /**
   * Loads the data
   */
  protected void copy() {
    SwingWorker worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
        m_ButtonCopy.setEnabled(false);
	m_Owner.processorStateChanged(new SpreadSheetProcessorEvent(m_Owner, EventType.OUTPUT_DATA, "Copying to clipboard"));
	return null;
      }

      @Override
      protected void done() {
	super.done();
        m_ButtonCopy.setEnabled(true);
      }
    };
    worker.execute();
  }

  /**
   * Retrieves the values from the other widget, if possible.
   *
   * @param other	the other widget to get the values from
   */
  public void assign(AbstractWidget other) {
    ClipboardTarget widget;

    if (other instanceof ClipboardTarget) {
      widget = (ClipboardTarget) other;
      widget.getWidget();
      setCurrentWriter(widget.getCurrentWriter());
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
    result.put(KEY_CHART, OptionUtils.getCommandLine(getCurrentWriter()));

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
	  setCurrentWriter((SpreadSheetWriter) OptionUtils.forAnyCommandLine(AbstractChartGenerator.class, (String) map.get(KEY_CHART)));
	}
	catch (Exception e) {
	  errors.add(getClass().getName() + ": Failed to instantiate chart from: " + map.get(KEY_CHART));
	}
      }
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
    CsvSpreadSheetWriter	writer;
    StringWriter		swriter;

    swriter = new StringWriter();
    writer  = (CsvSpreadSheetWriter) m_PanelWriter.getCurrent();
    writer.write(data, swriter);
    ClipboardHelper.copyToClipboard(swriter.toString());
    notifyOwner(EventType.DATA_IS_OUTPUT, "Data copied to clipboard");
  }
}
