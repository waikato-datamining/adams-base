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
 * AbstractImageTransformer.java
 * Copyright (C) 2012-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.data.image;

import adams.core.CleanUpHandler;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.flow.core.Actor;
import adams.flow.core.FlowContextHandler;

/**
 * Abstract base class for AbstractImage transformations.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of image to process
 */
public abstract class AbstractImageTransformer<T extends AbstractImageContainer>
  extends AbstractOptionHandler
  implements Comparable, CleanUpHandler, QuickInfoSupporter, FlowContextHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4566948525813804085L;

  /** the flow context. */
  protected Actor m_FlowContext;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FlowContext = null;
  }

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  public void setFlowContext(Actor value) {
    m_FlowContext = value;
  }

  /**
   * Returns the flow context, if any.
   *
   * @return		the actor, null if none available
   */
  public Actor getFlowContext() {
    return m_FlowContext;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br><br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Optional checks of the image.
   * <br><br>
   * Default implementation only checks whether image is null.
   *
   * @param img		the image to check
   */
  protected void checkImage(T img) {
    if (img == null)
      throw new IllegalStateException("No image provided!");
  }

  /**
   * Performs the actual transforming of the image.
   *
   * @param img		the image to transform (can be modified, since it is a copy)
   * @return		the generated image(s)
   */
  protected abstract T[] doTransform(T img);

  /**
   * Transforms the given image.
   *
   * @param img		the image to transform
   * @return		the generated image(s)
   */
  public T[] transform(T img) {
    T[]		result;
    int		i;

    checkImage(img);
    result = doTransform((T) img.getClone());
    for (i = 0; i < result.length; i++)
      result[i].getNotes().addProcessInformation(this);

    return result;
  }

  /**
   * Compares this object with the specified object for order.  Returns a
   * negative integer, zero, or a positive integer as this object is less
   * than, equal to, or greater than the specified object.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o 	the object to be compared.
   * @return  	a negative integer, zero, or a positive integer as this object
   *		is less than, equal to, or greater than the specified object.
   *
   * @throws ClassCastException 	if the specified object's type prevents it
   *         				from being compared to this object.
   */
  public int compareTo(Object o) {
    if (o == null)
      return 1;

    return OptionUtils.getCommandLine(this).compareTo(OptionUtils.getCommandLine(o));
  }

  /**
   * Returns whether the two objects are the same.
   * <br><br>
   * Only compares the commandlines of the two objects.
   *
   * @param o	the object to be compared
   * @return	true if the object is the same as this one
   */
  @Override
  public boolean equals(Object o) {
    return (compareTo(o) == 0);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public T shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public T shallowCopy(boolean expand) {
    return (T) OptionUtils.shallowCopy(this, expand);
  }

  /**
   * Cleans up data structures, frees up memory.
   * <br><br>
   * The default implementation does nothing.
   */
  public void cleanUp() {
  }

  /**
   * Frees up memory in a "destructive" non-reversible way.
   * <br><br>
   * Calls cleanUp() and cleans up the options.
   */
  @Override
  public void destroy() {
    cleanUp();
    super.destroy();
  }
}
