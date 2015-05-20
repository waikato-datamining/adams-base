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
 * HTMLHandler.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.gui.core.TextEditorPanel;
import adams.gui.core.dom.DOMTreeWithPreview;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;

/**
 <!-- globalinfo-start -->
 * Displays HTML files: html,htm
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
public class HTMLHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4859255638148506547L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays HTML files: " + Utils.arrayToString(getExtensions());
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"html", "htm"};
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    PreviewPanel		result;
    TextEditorPanel		textPanel;
    DOMParser			parser;
    FileInputStream		fis;
    Document 			doc;
    DOMTreeWithPreview		domPanel;

    fis = null;
    try {
      parser = new DOMParser();
      fis    = new FileInputStream(file.getAbsoluteFile());
      parser.parse(new InputSource(fis));
      doc      = parser.getDocument();
      domPanel = new DOMTreeWithPreview();
      domPanel.setDOM(doc);
      domPanel.getTree().expandAll();
      domPanel.setSplitterPosition(500);
      result = new PreviewPanel(domPanel);
    }
    catch (Exception e) {
      textPanel = new TextEditorPanel();
      textPanel.open(file);
      textPanel.setEditable(false);
      result = new PreviewPanel(textPanel, textPanel.getTextArea());
    }
    finally {
      FileUtils.closeQuietly(fis);
    }
    
    return result;
  }
}
