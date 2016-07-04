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
 * JsonPrettyPrintHandler.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.gui.core.TextEditorPanel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
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
  protected PreviewPanel createPreview(File file) {
    PreviewPanel	result;
    TextEditorPanel	textPanel;
    FileReader		freader;
    BufferedReader 	breader;
    Gson 		gson;
    JsonParser 		jp;
    JsonElement 	je;
    String		content;

    freader = null;
    breader = null;
    try {
      freader = new FileReader(file.getAbsolutePath());
      breader = new BufferedReader(freader);
      gson    = new GsonBuilder().setPrettyPrinting().create();
      jp      = new JsonParser();
      je      = jp.parse(breader);
      content = gson.toJson(je);
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
    finally {
      FileUtils.closeQuietly(breader);
      FileUtils.closeQuietly(freader);
    }
    
    return result;
  }
}
