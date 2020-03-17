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
 * MultiProcessor.java
 * Copyright (C) 2020 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.leftclick;

import adams.gui.visualization.image.ImagePanel;

import java.awt.Point;

/**
 <!-- globalinfo-start -->
 * Forwards the click event to all specified sub-processors.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-processor &lt;adams.gui.visualization.image.leftclick.AbstractLeftClickProcessor&gt; [-processor ...] (property: processors)
 * &nbsp;&nbsp;&nbsp;The processors to use
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultiProcessor
  extends AbstractLeftClickProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -7720853761742788354L;

  /** the processors to use. */
  protected AbstractLeftClickProcessor[] m_Processors;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Forwards the click event to all specified sub-processors.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "processor", "processors",
      new AbstractLeftClickProcessor[0]);
  }

  /**
   * Sets the processors to use.
   *
   * @param value 	the processors
   */
  public void setProcessors(AbstractLeftClickProcessor[] value) {
    m_Processors = value;
    reset();
  }

  /**
   * Returns the processors to use.
   *
   * @return 		the processors
   */
  public AbstractLeftClickProcessor[] getProcessors() {
    return m_Processors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String processorsTipText() {
    return "The processors to use";
  }

  /**
   * Does nothing.
   * 
   * @param panel	the origin
   * @param position	the top-left position of the clicl
   * @param modifiersEx	the associated modifiers
   */
  @Override
  protected void doProcessClick(ImagePanel panel, Point position, int modifiersEx) {
    int  	i;

    for (i = 0; i < m_Processors.length; i++)
      m_Processors[i].processClick(panel, position, modifiersEx, false);
    panel.repaint();
  }
}
