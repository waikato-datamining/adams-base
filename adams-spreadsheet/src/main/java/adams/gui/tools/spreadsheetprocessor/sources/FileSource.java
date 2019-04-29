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
 * FileSource.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetprocessor.sources;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.chooser.SpreadSheetFileChooserPanel;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;
import adams.gui.event.SpreadSheetProcessorEvent.EventType;
import adams.gui.tools.spreadsheetprocessor.AbstractWidget;

import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * For selecting a single file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FileSource
  extends AbstractSource {

  private static final long serialVersionUID = -4475860171792209905L;

  public static final String KEY_INPUT = "input";

  public static final String KEY_READER = "reader";

  /** the widget. */
  protected BasePanel m_Widget;

  /** the reader to use. */
  protected SpreadSheetFileChooserPanel m_PanelInput;

  /** the button for loading. */
  protected BaseButton m_ButtonLoad;

  /** the data. */
  protected SpreadSheet m_Data;

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
    JPanel 		panel;

    if (m_Widget == null) {
      m_Widget = new BasePanel(new BorderLayout());
      panelParams = new ParameterPanel();
      m_Widget.add(panelParams, BorderLayout.CENTER);

      m_PanelInput = new SpreadSheetFileChooserPanel();
      m_PanelInput.addChangeListener((ChangeEvent e) -> update());
      m_PanelInput.setUseSaveDialog(false);
      panelParams.addParameter("Input", m_PanelInput);

      m_ButtonLoad = new BaseButton(GUIHelper.getIcon("run.gif"));
      m_ButtonLoad.addActionListener((ActionEvent e) -> load());
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      panel.add(m_ButtonLoad);
      panelParams.addParameter("Load", panel);
    }

    return m_Widget;
  }

  /**
   * Loads the data
   */
  protected void load() {
    SwingWorker	worker;

    worker = new SwingWorker() {
      String error;
      @Override
      protected Object doInBackground() throws Exception {
        m_ButtonLoad.setEnabled(false);
	SpreadSheetReader reader = m_PanelInput.getReader();
	File input = m_PanelInput.getCurrent();
	try {
	  m_Data = reader.read(input);
	  if (m_Data == null)
	    error = reader.getLastError();
	  else
	    notifyOwner(EventType.DATA_IS_AVAILABLE, "Data loaded from: " + input);
	}
	catch (Exception e) {
	  error  = Utils.handleException(FileSource.this, "Failed to load data from: " + input, e);
	  m_Data = null;
	}
	return null;
      }

      @Override
      protected void done() {
	super.done();
        m_ButtonLoad.setEnabled(true);
	if (error != null)
	  GUIHelper.showErrorMessage(m_Widget.getParent(), error);
      }
    };
    worker.execute();
  }

  /**
   * Checks whether data is available.
   *
   * @return		true if available
   */
  @Override
  public boolean hasData() {
    return (m_Data != null);
  }

  /**
   * Returns the currently available data
   *
   * @return		the data, null if none available
   */
  @Override
  public SpreadSheet getData() {
    return m_Data;
  }

  /**
   * Sets the current file.
   *
   * @param value	the current file
   */
  public void setCurrentFile(File value) {
    m_PanelInput.setCurrent(value);
  }

  /**
   * Returns the current file.
   *
   * @return		the current file
   */
  public File getCurrentFile() {
    return m_PanelInput.getCurrent();
  }

  /**
   * Sets the current reader.
   *
   * @param reader	the current reader
   */
  public void setCurrentReader(SpreadSheetReader reader) {
    m_PanelInput.setReader(reader);
  }

  /**
   * Returns the current reader.
   *
   * @return		the current reader
   */
  public SpreadSheetReader getCurrentReader() {
    return m_PanelInput.getReader();
  }

  /**
   * Retrieves the values from the other widget, if possible.
   *
   * @param other	the other widget to get the values from
   */
  public void assign(AbstractWidget other) {
    FileSource 	widget;

    if (other instanceof FileSource) {
      widget = (FileSource) other;
      widget.getWidget();
      setCurrentFile(widget.getCurrentFile());
      setCurrentReader(widget.getCurrentReader());
    }
  }

  /**
   * Serializes the setup from the widget.
   *
   * @return		the generated setup representation
   */
  public Object serialize() {
    Map<String,Object> 	result;

    result = new HashMap<>();
    result.put(KEY_INPUT, getCurrentFile().getAbsolutePath());
    result.put(KEY_READER, OptionUtils.getCommandLine(getCurrentReader()));

    return result;
  }

  /**
   * Deserializes the setup and maps it onto the widget.
   *
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
      if (map.containsKey(KEY_INPUT))
        setCurrentFile(new PlaceholderFile((String) map.get(KEY_INPUT)));
      if (map.containsKey(KEY_READER)) {
        try {
	  setCurrentReader((SpreadSheetReader) OptionUtils.forAnyCommandLine(SpreadSheetReader.class, (String) map.get(KEY_READER)));
	}
	catch (Exception e) {
	  errors.add(getClass().getName() + ": Failed to instantiate reader from: " + map.get(KEY_READER));
	}
      }
      update();
    }
    else {
      errors.add(getClass().getName() + ": Deserialization data is not a map!");
    }
  }

  /**
   * Updates the widget.
   */
  public void update() {
    m_ButtonLoad.setEnabled(!m_PanelInput.getCurrent().isDirectory());
  }
}
