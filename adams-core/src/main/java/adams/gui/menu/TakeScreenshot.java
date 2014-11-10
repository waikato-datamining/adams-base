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
 * TakeScreenshot.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

import adams.core.DateFormat;
import adams.core.Utils;
import adams.data.image.BufferedImageContainer;
import adams.data.io.output.AbstractImageWriter;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.chooser.ImageFileChooser;
import adams.gui.core.GUIHelper;
import adams.gui.print.JComponentWriterFileChooser;

/**
 * Takes a screenshot of the desktop.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TakeScreenshot
  extends AbstractMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -1502903491659697700L;

  /**
   * Initializes the menu item with no owner.
   */
  public TakeScreenshot() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public TakeScreenshot(AbstractApplicationFrame owner) {
    super(owner);
  }
  
  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "screenshot.png";
  }

  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    Dimension 			size;
    Rectangle 			rect;
    Robot 			robot;
    BufferedImage 		image;
    BufferedImageContainer	cont;
    String			file;
    ImageFileChooser		filechooser;
    int				retVal;
    AbstractImageWriter		writer;
    String			msg;
    
    try {
      size  = Toolkit.getDefaultToolkit().getScreenSize();
      rect  = new Rectangle(size);
      robot = new Robot();
      image = robot.createScreenCapture(rect);
      cont  = new BufferedImageContainer();
      cont.setImage(image);
      file  = "ADAMS-" + new DateFormat("yyyyMMdd_HHmmss").format(new Date());
      filechooser = new ImageFileChooser();
      filechooser.setSelectedFile(new File(file));
      retVal = filechooser.showSaveDialog(getOwner());
      if (retVal != JComponentWriterFileChooser.APPROVE_OPTION)
	return;
      writer = filechooser.getImageWriter();
      msg = writer.write(filechooser.getSelectedPlaceholderFile(), cont);
      if (msg != null)
	GUIHelper.showErrorMessage(
	    getOwner(), 
	    "Failed to save screenshot to '" + filechooser.getSelectedPlaceholderFile() + "':\n" + msg);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	  getOwner(), 
	  "Failed to take screenshot:\n" + Utils.throwableToString(e));
    }
  }
    

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Take screenshot";
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return false;
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  @Override
  public UserMode getUserMode() {
    return UserMode.BASIC;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_HELP;
  }
}