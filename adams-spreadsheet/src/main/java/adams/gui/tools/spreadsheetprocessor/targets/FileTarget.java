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
 * FileTarget.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetprocessor.targets;

import adams.core.MessageCollection;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.chooser.SpreadSheetFileChooserPanel;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.ImageManager;
import adams.gui.core.ParameterPanel;
import adams.gui.event.SpreadSheetProcessorEvent;
import adams.gui.event.SpreadSheetProcessorEvent.EventType;
import adams.gui.tools.spreadsheetprocessor.AbstractWidget;

import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * For storing the processed data in a file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FileTarget
  extends AbstractTarget {

  private static final long serialVersionUID = 6535516712611654393L;

  public static final String KEY_OUTPUT = "output";

  public static final String KEY_WRITER = "writer";

  /** the widget. */
  protected BasePanel m_Widget;

  /** the writer to use. */
  protected SpreadSheetFileChooserPanel m_PanelOutput;

  /** the button for saving. */
  protected BaseButton m_ButtonSave;

  /**
   * Returns the name of the widget.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "File";
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

      m_PanelOutput = new SpreadSheetFileChooserPanel();
      m_PanelOutput.setUseSaveDialog(true);
      panelParams.addParameter("Output", m_PanelOutput);

      m_ButtonSave = new BaseButton(ImageManager.getIcon("run.gif"));
      m_ButtonSave.addActionListener((ActionEvent e) -> save());
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      panel.add(m_ButtonSave);
      panelParams.addParameter("Save", panel);
    }

    return m_Widget;
  }

  /**
   * Updates the widget.
   */
  public void update() {
    m_ButtonSave.setEnabled(m_Owner.getProcessorData() != null);
  }

  /**
   * Loads the data
   */
  protected void save() {
    SwingWorker worker;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
        m_ButtonSave.setEnabled(false);
	m_Owner.processorStateChanged(new SpreadSheetProcessorEvent(m_Owner, EventType.OUTPUT_DATA, "Saving to file: " + m_PanelOutput.getCurrent()));
	return null;
      }

      @Override
      protected void done() {
	super.done();
        m_ButtonSave.setEnabled(true);
      }
    };
    worker.execute();
  }

  /**
   * Sets the current file.
   *
   * @param value	the current file
   */
  public void setCurrentFile(File value) {
    m_PanelOutput.setCurrent(value);
  }

  /**
   * Returns the current file.
   *
   * @return		the current file
   */
  public File getCurrentFile() {
    return m_PanelOutput.getCurrent();
  }

  /**
   * Sets the current writer.
   *
   * @param writer	the current writer
   */
  public void setCurrentWriter(SpreadSheetWriter writer) {
    m_PanelOutput.setWriter(writer);
  }

  /**
   * Returns the current writer.
   *
   * @return		the current writer
   */
  public SpreadSheetWriter getCurrentWriter() {
    return m_PanelOutput.getWriter();
  }

  /**
   * Retrieves the values from the other widget, if possible.
   *
   * @param other	the other widget to get the values from
   */
  public void assign(AbstractWidget other) {
    FileTarget widget;

    if (other instanceof FileTarget) {
      widget = (FileTarget) other;
      widget.getWidget();
      setCurrentFile(widget.getCurrentFile());
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
    result.put(KEY_OUTPUT, getCurrentFile().getAbsolutePath());
    result.put(KEY_WRITER, OptionUtils.getCommandLine(getCurrentWriter()));

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
      if (map.containsKey(KEY_OUTPUT))
        m_PanelOutput.setCurrent(new PlaceholderFile((String) map.get(KEY_OUTPUT)));
      if (map.containsKey(KEY_WRITER)) {
        try {
	  setCurrentWriter((SpreadSheetWriter) OptionUtils.forAnyCommandLine(SpreadSheetWriter.class, (String) map.get(KEY_WRITER)));
	}
	catch (Exception e) {
	  errors.add(getClass().getName() + ": Failed to instantiate writer from: " + map.get(KEY_WRITER));
	}
      }
      update();
    }
    else {
      errors.add(getClass().getName() + ": Deserialization data is not a map!");
    }
  }

  /**
   * Hook method for performing checks.
   *
   * @param data	the data to check
   * @param errors	for storing errors
   */
  @Override
  protected void check(SpreadSheet data, MessageCollection errors) {
    super.check(data, errors);
    if (m_PanelOutput.getWriter() == null)
      errors.add("No output writer available - no file selected?");
    if (m_PanelOutput.getCurrent().isDirectory())
      errors.add("Output points to a directory!");
  }

  /**
   * Processes the data.
   *
   * @param data	the input data
   * @param errors	for storing errors
   */
  @Override
  protected void doProcess(SpreadSheet data, MessageCollection errors) {
    SpreadSheetWriter		writer;
    File			output;

    writer = m_PanelOutput.getWriter();
    output = m_PanelOutput.getCurrent();
    if (!writer.write(data, output))
      errors.add("Failed to write data to: " + output);
    else
      notifyOwner(EventType.DATA_IS_OUTPUT, "Data saved to: " + output);
  }
}
