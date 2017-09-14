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
 * InteractiveActorWithCustomParentComponent.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import java.awt.Component;

/**
 * Interface for interactive actors that allow the selection of a custom
 * parent component using a callable actor; instead of just using the
 * outer dialog/frame.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface InteractiveActorWithCustomParentComponent
  extends InteractiveActor {

  /**
   * Returns the parent component to use.
   *
   * @return		the parent
   */
  public Component getActualParentComponent();

  /**
   * Sets the (optional) callable actor to use as parent component instead of
   * the flow panel.
   *
   * @param value	the callable actor
   */
  public void setParentComponentActor(CallableActorReference value);

  /**
   * Returns the (optional) callable actor to use as parent component instead
   * of the flow panel.
   *
   * @return 		the callable actor
   */
  public CallableActorReference getParentComponentActor();

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String parentComponentActorTipText();

  /**
   * Sets whether to use the outer window as parent.
   *
   * @param value	true if to use outer window
   */
  public void setUseOuterWindow(boolean value);

  /**
   * Returns whether to use the outer window as parent.
   *
   * @return 		true if to use outer window
   */
  public boolean getUseOuterWindow();

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String useOuterWindowTipText();
}
