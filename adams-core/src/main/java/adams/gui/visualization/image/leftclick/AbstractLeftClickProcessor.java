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
 * AbstractSelectionProcessor.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.leftclick;

import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.gui.event.ImagePanelLeftClickEvent;
import adams.gui.event.ImagePanelLeftClickListener;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Point;

/**
 * Ancestor for classes that react to selection in an image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractLeftClickProcessor
  extends AbstractOptionHandler
  implements ImagePanelLeftClickListener, ShallowCopySupporter<AbstractLeftClickProcessor> {

  /** for serialization. */
  private static final long serialVersionUID = 3515366296579391750L;

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  protected void doImageChanged(PaintPanel panel) {
    reset();
  }

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  public void imageChanged(PaintPanel panel) {
    doImageChanged(panel);
  }

  /**
   * Process the click that occurred in the image panel.
   * 
   * @param panel	the origin
   * @param position	the position of the click
   * @param modifiersEx	the associated modifiers
   */
  protected abstract void doProcessClick(ImagePanel panel, Point position, int modifiersEx);

  /**
   * Process the click that occurred in the image panel.
   * 
   * @param panel	the origin
   * @param position	the position of the click
   * @param modifiersEx	the associated modifiers
   */
  public void processClick(ImagePanel panel, Point position, int modifiersEx) {
    doProcessClick(panel, position, modifiersEx);
    panel.repaint();
  }
  
  /**
   * Performs a check on the event.
   * 
   * @param e		the event to check
   * @return		null if OK, otherwise error message
   */
  protected String check(ImagePanelLeftClickEvent e) {
    if (e.getImagePanel() == null)
      return "No ImagePanel associated with event!";
    if (e.getPosition() == null)
      return "No position associated with event!";
    return null;
  }
  
  /**
   * Invoked when a left-click happened in a {@link ImagePanel}.
   * 
   * @param e		the event
   */
  @Override
  public void clicked(ImagePanelLeftClickEvent e) {
    String	msg;
    
    msg = check(e);
    if (msg == null)
      processClick(e.getImagePanel(), e.getPosition(), e.getModifiersEx());
    else
      getLogger().severe(msg);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractLeftClickProcessor shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractLeftClickProcessor shallowCopy(boolean expand) {
    return (AbstractLeftClickProcessor) OptionUtils.shallowCopy(this, expand);
  }
}
