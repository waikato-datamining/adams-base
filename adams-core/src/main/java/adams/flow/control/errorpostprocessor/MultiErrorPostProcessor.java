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
 * MultiErrorPostProcessor.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control.errorpostprocessor;

import adams.flow.core.Actor;
import adams.flow.core.ErrorHandler;

/**
 * Applies the specified post-processors subsequently.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultiErrorPostProcessor
  extends AbstractErrorPostProcessor {

  private static final long serialVersionUID = -2333634374360228602L;

  /** the post-processors to use. */
  protected ErrorPostProcessor[] m_PostProcessors;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies the specified post-processors subsequently.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "post-processor", "postProcessors",
      new ErrorPostProcessor[0]);
  }

  /**
   * Sets the post-processors to apply.
   *
   * @param value 	the post-processors
   */
  public void setPostProcessors(ErrorPostProcessor[] value) {
    m_PostProcessors = value;
    reset();
  }

  /**
   * Returns the post-processors to apply.
   *
   * @return 		the post-processors
   */
  public ErrorPostProcessor[] getPostProcessors() {
    return m_PostProcessors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String postProcessorsTipText() {
    return "The post-processors to apply sequentially to the error data.";
  }

  /**
   * Performs the actual post-processing of the error.
   *
   * @param handler the error handler that this call comes from
   * @param source  the source actor where the error originated
   * @param type    the type of error
   * @param msg     the error message
   * @return the (potentially) updated error message
   */
  @Override
  protected String doPostProcessError(ErrorHandler handler, Actor source, String type, String msg) {
    String	result;

    result = msg;
    for (ErrorPostProcessor postProcessor: m_PostProcessors)
      result = postProcessor.postProcessError(handler, source, type, result);

    return result;
  }
}
