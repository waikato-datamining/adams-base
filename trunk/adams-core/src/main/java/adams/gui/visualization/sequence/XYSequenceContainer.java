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
 * XYSequenceContainer.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

import java.awt.Color;

import adams.data.sequence.XYSequence;
import adams.gui.event.DataChangeEvent;
import adams.gui.event.DataChangeEvent.Type;
import adams.gui.visualization.container.ColorContainer;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.NamedContainer;
import adams.gui.visualization.container.VisibilityContainer;

/**
 * A container class for an XY sequence.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class XYSequenceContainer
  extends AbstractContainer
  implements ColorContainer, VisibilityContainer,
             NamedContainer {

  /** for serialization. */
  private static final long serialVersionUID = 4607026625578700170L;

  /** the associated color. */
  protected Color m_Color;

  /** whether the sequence is visible. */
  protected boolean m_Visible;

  /**
   * Initializes the container.
   *
   * @param manager	the owning manager
   * @param data	the sequence to encapsulate
   */
  public XYSequenceContainer(XYSequenceContainerManager manager, XYSequence data) {
    super(manager, data);
  }

  /**
   * Initializes members.
   */
  protected void initialize() {
    super.initialize();

    m_Color   = Color.WHITE;
    m_Visible = true;
  }

  /**
   * Updates itself with the values from given container (the manager is
   * excluded!).
   *
   * @param c		the container to get the values from
   */
  public void assign(XYSequenceContainer c) {
    super.assign(c);

    if (c instanceof XYSequenceContainer) {
      m_Updating = true;

      setColor(c.getColor());
      setVisible(c.isVisible());

      m_Updating = false;
    }
  }

  /**
   * Sets the color to use.
   *
   * @param value	the color
   */
  public void setColor(Color value) {
    m_Color = value;

    if ((!m_Updating) && (getManager() != null))
      getManager().notifyDataChangeListeners(
          new DataChangeEvent(getManager(), Type.UPDATE, getManager().indexOf(this)));
  }

  /**
   * Returns the current color in use.
   *
   * @return		the color
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Sets the sequence's visibility.
   *
   * @param value	if true then the sequence will be visible
   */
  public void setVisible(boolean value) {
    m_Visible = value;

    if ((!m_Updating) && (getManager() != null))
      getManager().notifyDataChangeListeners(
          new DataChangeEvent(getManager(), Type.VISIBILITY, getManager().indexOf(this)));
  }

  /**
   * Returns whether the sequence is visible.
   *
   * @return		true if the sequence is visible
   */
  public boolean isVisible() {
    return m_Visible;
  }

  /**
   * Sets the sequence.
   *
   * @param value	the sequence
   */
  public void setData(XYSequence value) {
    setPayload(value);
  }

  /**
   * Returns the stored sequence.
   *
   * @return		the sequence
   */
  public XYSequence getData() {
    return (XYSequence) getPayload();
  }

  /**
   * Sets the container's ID.
   *
   * @param value	the new ID
   */
  public void setID(String value) {
    getData().setID(value);
  }

  /**
   * Returns the container's ID.
   *
   * @return		the ID
   */
  public String getID() {
    return getData().getID();
  }

  /**
   * Returns the displayed container's ID.
   *
   * @return		the displayed ID
   */
  public String getDisplayID() {
    return getID();
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   *
   * @param   o the object to be compared.
   * @return  a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   * @throws ClassCastException if the specified object's type prevents it
   *         from being compared to this object.
   */
  public int compareTo(AbstractContainer o) {
    XYSequenceContainer		c;

    if (o == null)
      return 1;

    c = (XYSequenceContainer) o;

    return getData().compareTo(c.getData());
  }

  /**
   * Returns the hashcode of the sequence.
   *
   * @return		the hashcode
   */
  public int hashCode() {
    return getData().hashCode();
  }

  /**
   * Returns a short string representation of the container.
   *
   * @return		a string representation
   */
  public String toString() {
    return getData().toString();
  }
}