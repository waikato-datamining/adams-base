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
 * SerializedFileHandler.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.SerializationHelper;
import adams.core.Utils;
import adams.gui.core.BaseTabbedPane;

import java.io.File;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Displays serialized Java objects: model,model.gz,ser
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
public class SerializedFileHandler
  extends AbstractContentHandler {

  /** for serialization. */
  private static final long serialVersionUID = -1277627290853745369L;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays serialized Java objects: " + Utils.arrayToString(getExtensions());
  }

  /**
   * Returns the list of extensions (without dot) that this handler can
   * take care of.
   *
   * @return		the list of extensions (no dot)
   */
  @Override
  public String[] getExtensions() {
    return new String[]{"model", "model.gz", "ser"};
  }

  /**
   * Creates the actual view.
   *
   * @param file	the file to create the view for
   * @return		the view
   */
  @Override
  protected PreviewPanel createPreview(File file) {
    final BaseTabbedPane		tabbedPane;
    Object[]				objects;
    SerializedObjectPanel		panel;
    
    tabbedPane = new BaseTabbedPane();

    try {
      objects = SerializationHelper.readAll(file.getAbsolutePath());
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read '" + file + "':", e);
      objects = new Object[]{Utils.throwableToString(e)};
    }
    
    for (Object obj: objects) {
      panel = new SerializedObjectPanel();
      panel.setCurrent(obj);
      tabbedPane.addTab(Utils.classToString(obj.getClass()).replaceAll(".*\\.", ""), panel);
    }
    
    return new PreviewPanel(tabbedPane);
  }
}
