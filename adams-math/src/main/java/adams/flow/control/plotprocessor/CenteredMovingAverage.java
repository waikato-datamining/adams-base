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
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control.plotprocessor;

import adams.core.QuickInfoHelper;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.flow.container.SequencePlotterContainer;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * A processor that applies Centered Moving Average smoothing.<br>
 * <br>
 * For more information see:<br>
 * <br>
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
 * <pre>-plot-name-suffix &lt;java.lang.String&gt; (property: plotNameSuffix)
 * &nbsp;&nbsp;&nbsp;The suffix for the plot name; if left empty, the plot container automatically
 * &nbsp;&nbsp;&nbsp;becomes an OVERLAY.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-window-size &lt;int&gt; (property: windowSize)
 * &nbsp;&nbsp;&nbsp;The window size to use, an uneven number.
 * &nbsp;&nbsp;&nbsp;default: 15
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CenteredMovingAverage
  extends AbstractPlotProcessorWithBuffer<Point2D>
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 5171916489269022308L;

  /** Size of window. */
  protected int m_WindowSize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "A processor that applies Centered Moving Average smoothing.\n\n"
	+ "For more information see:\n\n"
	+ getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
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
   * Sets the window size.
   *
   * @param value 	the size
   */
  public void setWindowSize(int value) {
    if (getOptionManager().isValid("windowSize", value)) {
      m_WindowSize = value;
      reset();
    }
  }

  /**
   * Returns the window size.
   *
   * @return 		the size
   */
  public int getWindowSize() {
    return m_WindowSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String windowSizeTipText() {
    return "The window size to use, an uneven number.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "windowSize", m_WindowSize, ", window: ");

    return result;
  }

  /**
   * Processes the provided container. Generates new containers
   * if applicable.
   *
   * @param cont	the container to process
   * @return		null if no new containers were produced
   */
  @Override
  protected List<SequencePlotterContainer> doProcess(SequencePlotterContainer cont) {
    List<SequencePlotterContainer>	result;
    Point2D				point;
    Comparable				x;
    Comparable				y;
    List<Point2D>			smoothed;

    result = null;

    x = (Comparable) cont.getValue(SequencePlotterContainer.VALUE_X);
    if (x == null)
      x = m_XIndex;
    y = (Comparable) cont.getValue(SequencePlotterContainer.VALUE_Y);

    if ((x instanceof Number) && (y instanceof Number)) {
      point = new Point2D.Double(((Number) x).doubleValue(), ((Number) y).doubleValue());
      m_Data.add(point);
      while (m_Data.size() > m_WindowSize)
	m_Data.remove(0);
      if (m_Data.size() == m_WindowSize) {
	smoothed = adams.data.utils.CenteredMovingAverage.calculate(m_Data, m_WindowSize);
	if (!smoothed.isEmpty()) {
	  point  = smoothed.get(smoothed.size() / 2);
	  result = new ArrayList<>();
	  result.add(new SequencePlotterContainer(getPlotName(cont), point.getX(), point.getY(), getPlotType()));
	}
	else {
	  m_LastError = "Expected at least one smoothed point, encountered none!";
	}
      }
    }

    return result;
  }
}
