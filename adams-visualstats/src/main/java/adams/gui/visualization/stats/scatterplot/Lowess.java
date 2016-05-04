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
 * Lowess.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.scatterplot;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.utils.LOWESS;
import adams.gui.visualization.stats.paintlet.LowessPaintlet;

/**
 <!-- globalinfo-start -->
 * Display a lowess curve as an overlay. <br>
 * WikiPedia. Local Regression. URL http:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Lowess.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * BibTeX:
 * <pre>
 * &#64;misc{missing_id,
 *    author = {WikiPedia},
 *    title = {Local Regression},
 *    URL = {http:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Lowess}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-line-thickness &lt;float&gt; (property: thickness)
 * &nbsp;&nbsp;&nbsp;Thickness of the overlay line
 * &nbsp;&nbsp;&nbsp;default: 2.0
 * &nbsp;&nbsp;&nbsp;minimum: 1.0
 * &nbsp;&nbsp;&nbsp;maximum: 5.0
 * </pre>
 *
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;Color of the overlay line
 * &nbsp;&nbsp;&nbsp;default: #0000ff
 * </pre>
 *
 * <pre>-window-size &lt;int&gt; (property: windowSize)
 * &nbsp;&nbsp;&nbsp;Set the size of the data window for lowess fitting
 * &nbsp;&nbsp;&nbsp;default: 120
 * &nbsp;&nbsp;&nbsp;minimum: 20
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class Lowess
extends AbstractScatterPlotOverlay
implements TechnicalInformationHandler{

  /** for serialization */
  private static final long serialVersionUID = -6140660593813082123L;

  /** Size of window for calculating lowess */
  protected int m_WindowSize;

  @Override
  public String globalInfo() {
    return
    "Display a lowess curve as an overlay. \n"
    + getTechnicalInformation().toString();
  }

  @Override
  public void setUp() {
    m_Paintlet = new LowessPaintlet();
    m_Paintlet.parameters(m_Data, m_Parent.getX_Index(), m_Parent.getY_Index());
    m_Paintlet.setWindowSize(m_WindowSize);
    m_Paintlet.setStrokeThickness(m_Thickness);
    m_Paintlet.setRepaintOnChange(true);
    m_Paintlet.setColor(m_Color);
    m_Paintlet.setPanel(m_Parent);
    m_Paintlet.calculate();
  }

  @Override
  public void defineOptions() {
    super.defineOptions();
    //Choose the size of the window of data points
    m_OptionManager.add(
	"window-size", "windowSize",
	120, LOWESS.MIN_WINDOW_SIZE, null);
  }

  /**
   * Set the size of the data window for the lowess fitting
   * @param val		Size of data window
   */
  public void setWindowSize(int val) {
    m_WindowSize = val;
  }

  /**
   * get the size of the data window
   * @return		size of the data window
   */
  public int getWindowSize() {
    return m_WindowSize;
  }

  /**
   * Returns a tip text for the data window size property
   * @return		String explaining the property
   */
  public String windowSizeTipText() {
    return "Set the size of the data window for lowess fitting";
  }

  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation result;
    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.AUTHOR, "WikiPedia");
    result.setValue(Field.TITLE, "Local Regression");
    result.setValue(Field.URL, "http://en.wikipedia.org/wiki/Lowess");
    return result;
  }
}