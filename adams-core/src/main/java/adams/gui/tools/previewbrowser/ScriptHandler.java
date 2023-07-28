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
 * ScriptHandler.java
 * Copyright (C) 2011-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.Properties;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.env.Environment;
import adams.env.ScriptingDialogDefinition;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextPane;
import adams.gui.scripting.SyntaxDocument;

import java.awt.BorderLayout;
import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Displays the following plain text file types: txt,xml,props
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ScriptHandler
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
    return "Displays script files: " + Utils.arrayToString(getExtensions());
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"script", "scr"};
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  public PreviewPanel createPreview(File file) {
    BasePanel		result;
    BaseTextPane	textPane;
    Properties		props;
    List<String>	lines;

    textPane = new BaseTextPane();
    props    = Environment.getInstance().read(ScriptingDialogDefinition.KEY);
    textPane.setDocument(new SyntaxDocument(props));
    lines    = FileUtils.loadFromFile(file);
    textPane.setText(Utils.flatten(lines, "\n"));

    result = new BasePanel(new BorderLayout());
    result.add(new BaseScrollPane(textPane), BorderLayout.CENTER);

    return new PreviewPanel(result, textPane);
  }
}
