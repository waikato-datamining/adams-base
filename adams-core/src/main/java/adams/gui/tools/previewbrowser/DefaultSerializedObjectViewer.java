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
 * DefaultSerializedObjectViewer.java
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.core.Utils;
import adams.core.option.OptionUtils;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;

import java.awt.BorderLayout;

/**
 * Default viewer that just uses the Object's toString() method to
 * display it.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultSerializedObjectViewer
  extends AbstractSerializedObjectViewer {

  /** for serialization. */
  private static final long serialVersionUID = 491669321468347478L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply uses the Object's toString() method to display it.";
  }

  /**
   * Returns whether viewer handles this object.
   * 
   * @param obj		the object to check
   * @return		true if the object can be handled
   */
  public boolean handles(Object obj) {
    return true;
  }

  /**
   * Creates the actual preview.
   *
   * @param obj		the object to create the preview for
   * @return		the preview, null if failed to generate preview
   */
  @Override
  protected PreviewPanel createPreview(Object obj) {
    PreviewPanel	result;
    BaseTextArea	textOptions;
    BaseTextArea	textObject;
    
    textOptions = new BaseTextArea(3, 80);
    textOptions.setEditable(false);
    textOptions.setLineWrap(true);
    textOptions.setWrapStyleWord(true);
    textOptions.setFont(Fonts.getMonospacedFont());
    if (obj == null)
      textOptions.setText("");
    else if (obj.getClass().isArray())
      textOptions.setText(Utils.classToString(obj.getClass()));
    else
      textOptions.setText(OptionUtils.getCommandLine(obj));
    textObject = new BaseTextArea();
    textObject.setEditable(false);
    textObject.setFont(Fonts.getMonospacedFont());
    if (obj == null)
      textObject.setText("");
    else if (obj.getClass().isArray())
      textObject.setText(Utils.arrayToString(obj));
    else
      textObject.setText(obj.toString());
    result = new PreviewPanel(new BaseScrollPane(textObject), textObject);
    result.add(new BaseScrollPane(textOptions), BorderLayout.NORTH);

    return result;
  }
}
