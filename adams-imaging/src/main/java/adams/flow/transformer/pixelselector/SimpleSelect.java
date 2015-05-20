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
 * SimpleSelect.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pixelselector;

import java.awt.event.ActionEvent;

import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

/**
 <!-- globalinfo-start -->
 * Allows the user to select a pixel location.<br>
 * Stores the location in 'Pixel.X' and 'Pixel.Y'.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleSelect
  extends AbstractPixelSelectorAction {

  /** for serialization. */
  private static final long serialVersionUID = 8827194675683943806L;

  /** the pixel location X. */
  public final static String PIXEL_X = "Pixel.X";

  /** the pixel location Y. */
  public final static String PIXEL_Y = "Pixel.Y";
  
  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return 
	"Allows the user to select a pixel location.\n"
	+ "Stores the location in '" + PIXEL_X + "' and '" + PIXEL_Y + "'.";
  }

  /**
   * Returns the title of the action (used as menu item text).
   * 
   * @return		the title
   */
  protected String getTitle() {
    return "Select pixel";
  }

  /**
   * Reacts to the action event.
   * 
   * @param e		the event
   * @return		true if to update the report table
   */
  protected boolean doProcessAction(ActionEvent e) {
    Report	report;
    Field	field;
    
    report = getPanel().getImage().getReport();
    
    field = new Field(PIXEL_X, DataType.NUMERIC);
    report.addField(field);
    report.setValue(field, getPixelPosition().getX());

    field = new Field(PIXEL_Y, DataType.NUMERIC);
    report.addField(field);
    report.setValue(field, getPixelPosition().getY());
    
    return true;
  }
}
