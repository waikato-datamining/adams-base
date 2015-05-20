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
 * JsonHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import net.minidev.json.JSONAware;
import net.minidev.json.parser.JSONParser;
import adams.core.Utils;
import adams.gui.core.TextEditorPanel;
import adams.gui.core.json.JsonTreeWithPreview;

/**
 <!-- globalinfo-start -->
 * Displays JSON files: json
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JsonHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -5643331918040646266L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays JSON files: " + Utils.arrayToString(getExtensions());
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"json"};
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    PreviewPanel	result;
    TextEditorPanel	textPanel;
    JSONParser		parser;
    BufferedReader	reader;
    Object		obj;
    JsonTreeWithPreview	jsonPanel;

    try {
      reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
      parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
      obj    = parser.parse(reader);
      if (obj == null)
	throw new IllegalStateException("Failed to parse: " + file);
      if (!(obj instanceof JSONAware))
	throw new IllegalStateException("Cannot display: " + obj.getClass().getName());
      jsonPanel = new JsonTreeWithPreview();
      jsonPanel.setJSON((JSONAware) obj);
      jsonPanel.getTree().expandAll();
      result = new PreviewPanel(jsonPanel);
    }
    catch (Exception e) {
      textPanel = new TextEditorPanel();
      textPanel.open(file);
      textPanel.setEditable(false);
      result = new PreviewPanel(textPanel, textPanel.getTextArea());
    }
    
    return result;
  }
}
