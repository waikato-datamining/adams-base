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
 * MultiSelectionProcessor.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.selection;

import java.awt.Point;

import adams.gui.visualization.image.ImagePanel;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

/**
 <!-- globalinfo-start -->
 * Applies all the sub-processors sequentially.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-processors &lt;adams.gui.visualization.image.selection.AbstractSelectionProcessor&gt; [-processors ...] (property: processors)
 * &nbsp;&nbsp;&nbsp;The sub-processors to apply sequentially to the selection.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiSelectionProcessor
  extends AbstractSelectionProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 6323046923720400796L;
  
  /** the processors. */
  protected AbstractSelectionProcessor[] m_Processors;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Applies all the sub-processors sequentially.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"processors", "processors",
	new AbstractSelectionProcessor[0]);
  }

  /**
   * Sets the sub-processors.
   *
   * @param value 	the processors
   */
  public void setProcessors(AbstractSelectionProcessor[] value) {
    m_Processors = value;
    reset();
  }

  /**
   * Returns the sub-processors.
   *
   * @return 		the processors
   */
  public AbstractSelectionProcessor[] getProcessors() {
    return m_Processors;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String processorsTipText() {
    return "The sub-processors to apply sequentially to the selection.";
  }

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  @Override
  protected void doImageChanged(PaintPanel panel) {
    super.doImageChanged(panel);
    for (AbstractSelectionProcessor processor: m_Processors)
      processor.imageChanged(panel);
  }

  /**
   * Process the selection that occurred in the image panel.
   * 
   * @param panel	the origin
   * @param topLeft	the top-left position of the selection
   * @param bottomRight	the bottom-right position of the selection
   * @param modifiersEx	the associated modifiers
   */
  @Override
  protected void doProcessSelection(ImagePanel panel, Point topLeft, Point bottomRight, int modifiersEx) {
    for (AbstractSelectionProcessor processor: m_Processors)
      processor.processSelection(panel, topLeft, bottomRight, modifiersEx);
  }
}
