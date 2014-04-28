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
 * AbstractMapObjectHitListenerWithDialog.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import java.awt.Dialog;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;

import adams.flow.core.ActorUtils;

/**
 * Ancestor for dialog-based hit listeners.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of dialog in use
 */
public abstract class AbstractMapObjectHitListenerWithDialog<T extends Dialog>
  extends AbstractMapObjectHitListener {

  /** for serialization. */
  private static final long serialVersionUID = -613241778857988225L;
  
  /** the title. */
  protected String m_Title;
  
  /** the width of the dialog. */
  protected int m_Width;

  /** the height of the dialog. */
  protected int m_Height;

  /** the X position of the dialog. */
  protected int m_X;

  /** the Y position of the dialog. */
  protected int m_Y;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "title", "title",
	    getDefaultTitle());

    m_OptionManager.add(
	    "width", "width",
	    getDefaultWidth(), -1, null);

    m_OptionManager.add(
	    "height", "height",
	    getDefaultHeight(), -1, null);

    m_OptionManager.add(
	    "x", "x",
	    getDefaultX(), -3, null);

    m_OptionManager.add(
	    "y", "y",
	    getDefaultY(), -3, null);
  }

  /**
   * Returns the default title for the dialog.
   * 
   * @return		the default title
   */
  protected String getDefaultTitle() {
    return "Hits";
  }

  /**
   * Sets the title of the dialog.
   *
   * @param value 	the title
   */
  public void setTitle(String value) {
    m_Title = value;
    reset();
  }

  /**
   * Returns the currently set title of the dialog.
   *
   * @return 		the title
   */
  public String getTitle() {
    return m_Title;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String titleTipText() {
    return "The title of the dialog.";
  }

  /**
   * Returns the default width for the dialog.
   *
   * @return		the default width
   */
  protected int getDefaultWidth() {
    return 800;
  }

  /**
   * Sets the width of the dialog.
   *
   * @param value 	the width
   */
  public void setWidth(int value) {
    m_Width = value;
    reset();
  }

  /**
   * Returns the currently set width of the dialog.
   *
   * @return 		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The width of the dialog.";
  }

  /**
   * Returns the default height for the dialog.
   *
   * @return		the default height
   */
  protected int getDefaultHeight() {
    return 600;
  }

  /**
   * Sets the height of the dialog.
   *
   * @param value 	the height
   */
  public void setHeight(int value) {
    m_Height = value;
    reset();
  }

  /**
   * Returns the currently set height of the dialog.
   *
   * @return 		the height
   */
  public int getHeight() {
    return m_Height;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String heightTipText() {
    return "The height of the dialog.";
  }
  
  /**
   * Returns the default X position for the dialog.
   *
   * @return		the default X position
   */
  protected int getDefaultX() {
    return -2;
  }

  /**
   * Sets the X position of the dialog.
   *
   * @param value 	the X position
   */
  public void setX(int value) {
    m_X = value;
    reset();
  }

  /**
   * Returns the currently set X position of the dialog.
   *
   * @return 		the X position
   */
  public int getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String xTipText() {
    return "The X position of the dialog (>=0: absolute, -1: left, -2: center, -3: right).";
  }

  /**
   * Returns the default Y position for the dialog.
   *
   * @return		the default Y position
   */
  protected int getDefaultY() {
    return -2;
  }

  /**
   * Sets the Y position of the dialog.
   *
   * @param value 	the Y position
   */
  public void setY(int value) {
    m_Y = value;
    reset();
  }

  /**
   * Returns the currently set Y position of the dialog.
   *
   * @return 		the Y position
   */
  public int getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String yTipText() {
    return "The Y position of the dialog (>=0: absolute, -1: top, -2: center, -3: bottom).";
  }
  
  /**
   * Performs the actual processing of the hits, returns the generated dialog.
   * 
   * @param viewer	the associated viewer
   * @param hits	the objects that were "hit"
   * @return		the generated dialog
   */
  protected abstract T doProcessHits(JMapViewer viewer, List<MapObject> hits);
  
  /**
   * Performs the processing of the hits.
   * 
   * @param viewer	the associated viewer
   * @param hits	the objects that were "hit"
   */
  @Override
  protected void processHits(JMapViewer viewer, List<MapObject> hits) {
    T		dialog;
    
    dialog = doProcessHits(viewer, hits);
    dialog.setSize(ActorUtils.determineSize(dialog, m_X, m_Y, m_Width, m_Height));
    dialog.setLocation(ActorUtils.determineLocation(dialog, m_X, m_Y));
    dialog.setVisible(true);
  }
}
