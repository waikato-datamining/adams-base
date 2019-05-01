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
 * AbstractMetaFileSearchHandler.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filesearch;

/**
 * Ancestor for file search handlers that use the base handler to do the
 * actual search.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractMetaFileSearchHandler
  extends AbstractFileSearchHandler {

  private static final long serialVersionUID = -2862640864352798521L;

  /** the base handler. */
  protected StreamableFileSearchHandler m_Handler;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "handler", "handler",
      getDefaultHandler());
  }

  /**
   * Returns the default handler.
   *
   * @return		the default
   */
  protected StreamableFileSearchHandler getDefaultHandler() {
    return new TextFileSearchHandler();
  }

  /**
   * Sets the handler to use.
   *
   * @param value	the handler
   */
  public void setHandler(StreamableFileSearchHandler value) {
    m_Handler = value;
    reset();
  }

  /**
   * Returns the handler to use.
   *
   * @return		the handler
   */
  public StreamableFileSearchHandler getHandler() {
    return m_Handler;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String handlerTipText() {
    return "The handler to use.";
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    if (m_Handler != null)
      m_Handler.stopExecution();
    super.stopExecution();
  }
}
