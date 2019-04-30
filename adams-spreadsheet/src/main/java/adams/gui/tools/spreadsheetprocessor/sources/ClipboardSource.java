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
 * ClipboardSource.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetprocessor.sources;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;
import adams.gui.event.SpreadSheetProcessorEvent.EventType;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.spreadsheetprocessor.AbstractWidget;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * For retrieving data from clipboard.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ClipboardSource
  extends AbstractSource {

  private static final long serialVersionUID = -4475860171792209905L;

  public static final String KEY_READER = "reader";

  /** the widget. */
  protected BasePanel m_Widget;

  /** the reader to use. */
  protected GenericObjectEditorPanel m_PanelReader;

  /** the button for pasting. */
  protected BaseButton m_ButtonPaste;

  /** the data. */
  protected SpreadSheet m_Data;

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
    JPanel 			panel;
    CsvSpreadSheetReader	reader;

    if (m_Widget == null) {
      m_Widget = new BasePanel(new BorderLayout());
      panelParams = new ParameterPanel();
      m_Widget.add(panelParams, BorderLayout.CENTER);

      reader = new CsvSpreadSheetReader();
      reader.setSeparator("\\t");
      m_PanelReader = new GenericObjectEditorPanel(SpreadSheetReader.class, reader, false);
      panelParams.addParameter("Reader", m_PanelReader);

      m_ButtonPaste = new BaseButton(GUIHelper.getIcon("run.gif"));
      m_ButtonPaste.addActionListener((ActionEvent e) -> paste());
      panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      panel.add(m_ButtonPaste);
      panelParams.addParameter("Paste", panel);
    }

    return m_Widget;
  }

  /**
   * Loads the data
   */
  protected void paste() {
    SwingWorker	worker;

    worker = new SwingWorker() {
      String error;
      @Override
      protected Object doInBackground() throws Exception {
        m_ButtonPaste.setEnabled(false);
        StringReader sreader = new StringReader(ClipboardHelper.pasteStringFromClipboard());
        CsvSpreadSheetReader reader = (CsvSpreadSheetReader) m_PanelReader.getCurrent();
	try {
          m_Data = reader.read(sreader);
	  if (m_Data == null)
	    error = reader.getLastError();
	  else
	    notifyOwner(EventType.DATA_IS_AVAILABLE, "Data pasted from clipboard!");
	}
	catch (Exception e) {
	  error  = Utils.handleException(ClipboardSource.this, "Failed to paste data from clipboard!", e);
	  m_Data = null;
	}
	return null;
      }

      @Override
      protected void done() {
	super.done();
        m_ButtonPaste.setEnabled(true);
	if (error != null)
	  GUIHelper.showErrorMessage(m_Widget.getParent(), error);
      }
    };
    worker.execute();
  }

  /**
   * Sets the current reader.
   *
   * @param value	the reader
   */
  public void setCurrentReader(SpreadSheetReader value) {
    m_PanelReader.setCurrent(value);
  }

  /**
   * Returns the current reader.
   *
   * @return		the reader
   */
  public SpreadSheetReader getCurrentReader() {
    return (SpreadSheetReader) m_PanelReader.getCurrent();
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
   * Retrieves the values from the other widget, if possible.
   *
   * @param other	the other widget to get the values from
   */
  public void assign(AbstractWidget other) {
    ClipboardSource widget;

    if (other instanceof ClipboardSource) {
      widget = (ClipboardSource) other;
      widget.getWidget();
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
    m_ButtonPaste.setEnabled(ClipboardHelper.canPasteStringFromClipboard());
  }
}
