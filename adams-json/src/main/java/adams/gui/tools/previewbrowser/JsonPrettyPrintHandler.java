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
 * JsonPrettyPrintHandler.java
 * Copyright (C) 2016-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.data.json.JsonHelper;
import adams.gui.core.TextEditorPanel;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Displays JSON files in pretty print format: json
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 13654 $
 */
public class JsonPrettyPrintHandler
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
    return "Displays JSON files in pretty print format: " + Utils.arrayToString(getExtensions());
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
  public PreviewPanel createPreview(File file) {
    PreviewPanel	result;
    TextEditorPanel	textPanel;
    Object		obj;
    String		content;

    try {
      obj = JsonHelper.parse(file.getAbsoluteFile(), this);
      if (obj == null)
        throw new IllegalStateException("Failed to parse file as JSON: " + file);
      content = JsonHelper.prettyPrint(obj.toString());
      textPanel = new TextEditorPanel();
      textPanel.setContent(content);
      textPanel.setEditable(false);
      result = new PreviewPanel(textPanel, textPanel.getTextArea());
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
