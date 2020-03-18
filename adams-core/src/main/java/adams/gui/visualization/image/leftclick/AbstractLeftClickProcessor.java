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
 * AbstractSelectionProcessor.java
 * Copyright (C) 2013-2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.leftclick;

import adams.core.ShallowCopySupporter;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.gui.core.KeyUtils;
import adams.gui.event.ImagePanelLeftClickEvent;
import adams.gui.event.ImagePanelLeftClickListener;
import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

import java.awt.Point;

/**
 * Ancestor for classes that react to selection in an image.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractLeftClickProcessor
  extends AbstractOptionHandler
  implements ImagePanelLeftClickListener, ShallowCopySupporter<AbstractLeftClickProcessor> {

  /** for serialization. */
  private static final long serialVersionUID = 3515366296579391750L;

  /** whether shift needs to be down. */
  protected boolean m_ShiftDown;

  /** whether alt needs to be down. */
  protected boolean m_AltDown;

  /** whether ctrl needs to be down. */
  protected boolean m_CtrlDown;

  /** whether meta needs to be down. */
  protected boolean m_MetaDown;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "shift-down", "shiftDown",
      false);

    m_OptionManager.add(
      "alt-down", "altDown",
      false);

    m_OptionManager.add(
      "ctrl-down", "ctrlDown",
      false);

    m_OptionManager.add(
      "meta-down", "metaDown",
      false);
  }

  /**
   * Sets whether the shift key needs to be down.
   *
   * @param value 	true if needs to be down
   */
  public void setShiftDown(boolean value) {
    m_ShiftDown = value;
    reset();
  }

  /**
   * Returns whether the shift key needs to be down.
   *
   * @return 		true if needs to be down
   */
  public boolean getShiftDown() {
    return m_ShiftDown;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shiftDownTipText() {
    return "If enabled, the SHIFT key must be down to trigger.";
  }

  /**
   * Sets whether the alt key needs to be down.
   *
   * @param value 	true if needs to be down
   */
  public void setAltDown(boolean value) {
    m_AltDown = value;
    reset();
  }

  /**
   * Returns whether the alt key needs to be down.
   *
   * @return 		true if needs to be down
   */
  public boolean getAltDown() {
    return m_AltDown;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String altDownTipText() {
    return "If enabled, the ALT key must be down to trigger.";
  }

  /**
   * Sets whether the ctrl key needs to be down.
   *
   * @param value 	true if needs to be down
   */
  public void setCtrlDown(boolean value) {
    m_CtrlDown = value;
    reset();
  }

  /**
   * Returns whether the ctrl key needs to be down.
   *
   * @return 		true if needs to be down
   */
  public boolean getCtrlDown() {
    return m_CtrlDown;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String ctrlDownTipText() {
    return "If enabled, the CTRL key must be down to trigger.";
  }

  /**
   * Sets whether the meta key needs to be down.
   *
   * @param value 	true if needs to be down
   */
  public void setMetaDown(boolean value) {
    m_MetaDown = value;
    reset();
  }

  /**
   * Returns whether the meta key needs to be down.
   *
   * @return 		true if needs to be down
   */
  public boolean getMetaDown() {
    return m_MetaDown;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDownTipText() {
    return "If enabled, the META key must be down to trigger.";
  }

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
   * @param repaint 	whether to repaint the panel
   */
  public void processClick(ImagePanel panel, Point position, int modifiersEx, boolean repaint) {
    // ensure that correct keys are pressed
    if ((m_ShiftDown && !KeyUtils.isShiftDown(modifiersEx)) || (!m_ShiftDown && KeyUtils.isShiftDown(modifiersEx)))
      return;
    if ((m_AltDown && !KeyUtils.isAltDown(modifiersEx)) || (!m_AltDown && KeyUtils.isAltDown(modifiersEx)))
      return;
    if ((m_CtrlDown && !KeyUtils.isCtrlDown(modifiersEx)) || (!m_CtrlDown && KeyUtils.isCtrlDown(modifiersEx)))
      return;
    if ((m_MetaDown && !KeyUtils.isMetaDown(modifiersEx)) || (!m_MetaDown && KeyUtils.isMetaDown(modifiersEx)))
      return;

    doProcessClick(panel, position, modifiersEx);
    if (repaint)
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
      processClick(e.getImagePanel(), e.getPosition(), e.getModifiersEx(), true);
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
