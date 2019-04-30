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
 * FileBrowserSource.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.spreadsheetprocessor.sources;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.option.OptionUtils;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.chooser.DirectoryChooserPanel;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.FilePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;
import adams.gui.event.SpreadSheetProcessorEvent.EventType;
import adams.gui.goe.GenericObjectEditorPanel;
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
 * For browsing files and reading them.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FileBrowserSource
  extends AbstractSource {

  private static final long serialVersionUID = -4475860171792209905L;

  public static final String KEY_DIRECTORY = "directory";

  public static final String KEY_READER = "reader";

  /** the widget. */
  protected BasePanel m_Widget;

  /** the directory to browse. */
  protected DirectoryChooserPanel m_PanelDirectory;

  /** the list of files. */
  protected FilePanel m_PanelFiles;

  /** the reader to use. */
  protected GenericObjectEditorPanel m_PanelReader;

  /** the button for loading. */
  protected BaseButton m_ButtonLoad;

  /** the data. */
  protected SpreadSheet m_Data;

  /** whether to ignore dir updates. */
  protected boolean m_IgnoreDirUpdates;

  /**
   * Returns the name of the widget.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "File browser";
  }

  /**
   * Returns the widget.
   *
   * @return		the widget
   */
  @Override
  public Component getWidget() {
    JPanel 		panel;
    ParameterPanel	panelParams;

    if (m_Widget == null) {
      m_Widget = new BasePanel(new BorderLayout());

      panel = new JPanel(new BorderLayout(5, 5));
      m_Widget.add(panel, BorderLayout.CENTER);

      m_PanelDirectory = new DirectoryChooserPanel();
      m_PanelDirectory.addChangeListener((ChangeEvent e) -> {
        if (!m_IgnoreDirUpdates)
	  m_PanelFiles.setCurrentDir(m_PanelDirectory.getCurrent().getAbsolutePath());
      });
      panel.add(m_PanelDirectory, BorderLayout.NORTH);

      m_PanelFiles = new FilePanel(false);
      m_PanelFiles.setListDirs(true);
      m_PanelFiles.addSelectionChangeListener((ChangeEvent e) -> update());
      m_PanelFiles.addDirectoryChangeListener((ChangeEvent e) -> {
        m_IgnoreDirUpdates = true;
        m_PanelDirectory.setCurrent(new PlaceholderFile(m_PanelFiles.getCurrentDir()));
        m_IgnoreDirUpdates = false;
      });
      panel.add(m_PanelFiles, BorderLayout.CENTER);

      panelParams = new ParameterPanel();
      m_Widget.add(panelParams, BorderLayout.SOUTH);

      m_PanelReader = new GenericObjectEditorPanel(SpreadSheetReader.class, new CsvSpreadSheetReader());
      panelParams.addParameter("Reader", m_PanelReader);

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
	SpreadSheetReader reader = (SpreadSheetReader) m_PanelReader.getCurrent();
	File input = m_PanelFiles.getSelectedFile();
	try {
	  m_Data = reader.read(input);
	  if (m_Data == null)
	    error = reader.getLastError();
	  else
	    notifyOwner(EventType.DATA_IS_AVAILABLE, "Data loaded from: " + input);
	}
	catch (Exception e) {
	  error  = Utils.handleException(FileBrowserSource.this, "Failed to load data from: " + input, e);
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
   * Sets the current directory.
   *
   * @param value	the current directory
   */
  public void setCurrentDirectory(File value) {
    m_PanelFiles.setCurrentDir(value.getAbsolutePath());
  }

  /**
   * Returns the current directory.
   *
   * @return		the current directory
   */
  public File getCurrentDirectory() {
    return m_PanelDirectory.getCurrent();
  }

  /**
   * Returns the current file.
   *
   * @return		the current file
   */
  public File getCurrentFile() {
    return m_PanelFiles.getSelectedFile();
  }

  /**
   * Sets the current reader.
   *
   * @param reader	the current reader
   */
  public void setCurrentReader(SpreadSheetReader reader) {
    m_PanelReader.setCurrent(reader);
  }

  /**
   * Returns the current reader.
   *
   * @return		the current reader
   */
  public SpreadSheetReader getCurrentReader() {
    return (SpreadSheetReader) m_PanelReader.getCurrent();
  }

  /**
   * Retrieves the values from the other widget, if possible.
   *
   * @param other	the other widget to get the values from
   */
  public void assign(AbstractWidget other) {
    FileBrowserSource widget;

    if (other instanceof FileBrowserSource) {
      widget = (FileBrowserSource) other;
      widget.getWidget();
      setCurrentDirectory(widget.getCurrentDirectory());
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
    result.put(KEY_DIRECTORY, getCurrentDirectory().getAbsolutePath());
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
      if (map.containsKey(KEY_DIRECTORY))
        setCurrentDirectory(new PlaceholderFile((String) map.get(KEY_DIRECTORY)));
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
    m_ButtonLoad.setEnabled(getCurrentFile() != null);
  }
}
