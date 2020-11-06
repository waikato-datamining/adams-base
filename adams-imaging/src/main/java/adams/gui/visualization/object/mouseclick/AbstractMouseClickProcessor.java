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
 * AbstractMouseClickProcessor.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.mouseclick;

import adams.core.option.AbstractOptionHandler;
import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.core.KeyUtils;
import adams.gui.visualization.object.ObjectAnnotationPanel;

import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 * Ancestor for schemes that react to mouse clicks.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMouseClickProcessor
  extends AbstractOptionHandler {

  private static final long serialVersionUID = 3132566195997194745L;

  /**
   * The mouse buttons.
   */
  public enum MouseButton {
    LEFT,
    MIDDLE,
    RIGHT
  }

  /** whether the overlay is enabled. */
  protected boolean m_Enabled;

  /** the mouse button to react to. */
  protected MouseButton m_Button;

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
      "enabled", "enabled",
      true);

    m_OptionManager.add(
      "button", "button",
      MouseButton.LEFT);

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
   * Sets whether the click processor is enabled.
   *
   * @param value 	true if enabled
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
    reset();
  }

  /**
   * Returns whether the click processor is enabled.
   *
   * @return 		true if enabled
   */
  public boolean getEnabled() {
    return m_Enabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "Determines whether the click processor is enabled or not.";
  }

  /**
   * Sets the mouse button to react to.
   *
   * @param value 	the button
   */
  public void setButton(MouseButton value) {
    m_Button = value;
    reset();
  }

  /**
   * Returns the mouse button to react to.
   *
   * @return 		the button
   */
  public MouseButton getButton() {
    return m_Button;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String buttonDownTipText() {
    return "The mouse button to react to.";
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
   * Determines the object hits for the mouse location.
   *
   * @param panel	the owning panel
   * @param e		the mouse event/location
   * @return		the hits
   */
  protected LocatedObjects determineHits(ObjectAnnotationPanel panel, MouseEvent e) {
    LocatedObjects 	result;
    LocatedObjects	objects;
    Point 		location;
    boolean		add;

    objects  = panel.getObjects();
    result = new LocatedObjects();
    location = panel.mouseToPixelLocation(e.getPoint());
    for (LocatedObject object: objects) {
      if (object.hasPolygon())
	add = object.getActualPolygon().contains(location);
      else
	add = object.getActualRectangle().contains(location);
      if (add)
	result.add(object.getClone());
    }

    if (isLoggingEnabled())
      getLogger().info("hits: " + result);

    return result;
  }

  /**
   * Processes the mouse event.
   *
   * @param panel 	the owning panel
   * @param e		the event
   */
  protected abstract void doProcess(ObjectAnnotationPanel panel, MouseEvent e);

  /**
   * Processes the mouse event.
   *
   * @param panel 	the owning panel
   * @param e		the event
   */
  public void process(ObjectAnnotationPanel panel, MouseEvent e) {
    if (!getEnabled())
      return;

    // ensure correct button is pressed
    if ((m_Button == MouseButton.LEFT) && (e.getButton() != MouseEvent.BUTTON1))
      return;
    if ((m_Button == MouseButton.MIDDLE) && (e.getButton() != MouseEvent.BUTTON2))
      return;
    if ((m_Button == MouseButton.RIGHT) && (e.getButton() != MouseEvent.BUTTON3))
      return;

    // ensure that correct keys are pressed
    if ((m_ShiftDown && !KeyUtils.isShiftDown(e.getModifiersEx())) || (!m_ShiftDown && KeyUtils.isShiftDown(e.getModifiersEx())))
      return;
    if ((m_AltDown && !KeyUtils.isAltDown(e.getModifiersEx())) || (!m_AltDown && KeyUtils.isAltDown(e.getModifiersEx())))
      return;
    if ((m_CtrlDown && !KeyUtils.isCtrlDown(e.getModifiersEx())) || (!m_CtrlDown && KeyUtils.isCtrlDown(e.getModifiersEx())))
      return;
    if ((m_MetaDown && !KeyUtils.isMetaDown(e.getModifiersEx())) || (!m_MetaDown && KeyUtils.isMetaDown(e.getModifiersEx())))
      return;

    doProcess(panel, e);
  }
}
