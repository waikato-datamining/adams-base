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
 * CenteredMovingAverage.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.scatterplot;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.gui.visualization.stats.paintlet.CenteredMovingAveragePaintlet;

/**
 <!-- globalinfo-start -->
 * Display a smoothed curve as an overlay. <br>
 * WikiPedia. Moving average. URL https:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Moving_average
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;misc{missing_id,
 *    author = {WikiPedia},
 *    title = {Moving average},
 *    URL = {https:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Moving_average}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-line-thickness &lt;float&gt; (property: thickness)
 * &nbsp;&nbsp;&nbsp;Thickness of the overlay line
 * &nbsp;&nbsp;&nbsp;default: 2.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.0
 * </pre>
 *
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;Color of the overlay line
 * &nbsp;&nbsp;&nbsp;default: #0000ff
 * </pre>
 *
 * <pre>-window-size &lt;int&gt; (property: windowSize)
 * &nbsp;&nbsp;&nbsp;Set the window size for smoothing, uneven number.
 * &nbsp;&nbsp;&nbsp;default: 15
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class CenteredMovingAverage
  extends AbstractScatterPlotOverlay
  implements TechnicalInformationHandler{

  /** for serialization */
  private static final long serialVersionUID = -6140660593813082123L;

  /** the window size */
  protected int m_WindowSize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Display a smoothed curve as an overlay. \n"
	+ getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    return adams.data.utils.CenteredMovingAverage.getTechnicalInformation();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "window-size", "windowSize",
      15, 1, null);
  }

  /**
   * Set the size of the data window for the lowess fitting.
   *
   * @param value		Size of data window
   */
  public void setWindowSize(int value) {
    if (getOptionManager().isValid("windowSize", value)) {
      m_WindowSize = value;
      reset();
    }
  }

  /**
   * get the size of the data window.
   *
   * @return		size of the data window
   */
  public int getWindowSize() {
    return m_WindowSize;
  }

  /**
   * Returns a tip text for the data window size property.
   *
   * @return		String explaining the property
   */
  public String windowSizeTipText() {
    return "Set the window size for smoothing, uneven number.";
  }

  /**
   * set up the overlay and its paintlet.
   */
  @Override
  public void setUp() {
    m_Paintlet = new CenteredMovingAveragePaintlet();
    m_Paintlet.parameters(m_Data, m_Parent.getXIntIndex(), m_Parent.getYIntIndex());
    m_Paintlet.setWindowSize(m_WindowSize);
    m_Paintlet.setStrokeThickness(m_Thickness);
    m_Paintlet.setRepaintOnChange(true);
    m_Paintlet.setColor(m_Color);
    m_Paintlet.setPanel(m_Parent);
    m_Paintlet.calculate();
  }
}
