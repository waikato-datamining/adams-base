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
 * AbstractAnnotator.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.annotator;

import adams.core.CleanUpHandler;
import adams.core.option.AbstractOptionHandler;
import adams.gui.visualization.object.ObjectAnnotationPanel;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Ancestor for annotation handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAnnotator
  extends AbstractOptionHandler
  implements CleanUpHandler {

  private static final long serialVersionUID = -7284521891609621197L;

  /** the owner. */
  protected ObjectAnnotationPanel m_Owner;

  /** whether the selection box is currently been drawn. */
  protected boolean m_Selecting;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Owner     = null;
    m_Selecting = false;
  }

  /**
   * Sets the owner.
   *
   * @param value	the owner
   */
  public void setOwner(ObjectAnnotationPanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner
   */
  public ObjectAnnotationPanel getOwner() {
    return m_Owner;
  }

  /**
   * Installs the annotator with the owner.
   */
  protected abstract void doInstall();

  /**
   * Installs the annotator with the owner.
   */
  public void install() {
    if (m_Owner == null)
      throw new IllegalStateException("No owner set, cannot install!");
    doInstall();
  }

  /**
   * Uninstalls the annotator with the owner.
   */
  protected abstract void doUninstall();

  /**
   * Uninstalls the annotator with the owner.
   */
  public void uninstall() {
    if (m_Owner == null)
      throw new IllegalStateException("No owner set, cannot uninstall!");
    doUninstall();
  }

  /**
   * Checks whether an actual label is set.
   *
   * @return		true if actual label set
   */
  public boolean hasCurrentLabel() {
    return (getCurrentLabel() != null) && !getCurrentLabel().isEmpty();
  }

  /**
   * Returns the currently set label.
   *
   * @return		the label, can be null
   */
  public String getCurrentLabel() {
    return getOwner().getCurrentLabel();
  }

  /**
   * Gets called when the label changes.
   * <br>
   * Default implementation does nothing.
   */
  public void labelChanged() {
    if (isLoggingEnabled())
      getLogger().info("Label changed: " + (hasCurrentLabel() ? getCurrentLabel() : "[not set]"));
  }

  /**
   * Returns the thickness of the stroke.
   *
   * @param g		graphics context to get the thickness from
   * @param defValue	the default value to return in case of failure
   * @return		the stroke, default value if failed to extract
   */
  protected float getStrokeWidth(Graphics g, float defValue) {
    Graphics2D g2d;

    if (g instanceof Graphics2D) {
      g2d = (Graphics2D) g;
      if (g2d.getStroke() instanceof BasicStroke)
	return ((BasicStroke) g2d.getStroke()).getLineWidth();
    }

    return defValue;
  }

  /**
   * Applies the stroke thickness.
   *
   * @param stroke	the thickness to apply
   */
  protected void applyStroke(Graphics g, float stroke) {
    Graphics2D 	g2d;

    if (g instanceof Graphics2D) {
      g2d = (Graphics2D) g;
      g2d.setStroke(new BasicStroke(stroke));
    }
  }

  /**
   * Paints the selection.
   *
   * @param g		the graphics context
   */
  protected abstract void doPaintSelection(Graphics g);

  /**
   * Paints the selection.
   *
   * @param g		the graphics context
   */
  public void paintSelection(Graphics g) {
    if (!m_Selecting)
      return;
    doPaintSelection(g);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
  }
}
