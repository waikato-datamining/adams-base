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
 * SimpleSavitzkyGolay.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control.plotprocessor;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.flow.container.SequencePlotterContainer;

/**
 <!-- globalinfo-start -->
 * A processor that applies SimpleSavitzkyGolay smoothing.<br>
 * <br>
 * For more information see:<br>
 * <br>
 * A. Savitzky, Marcel J.E. Golay (1964). Smoothing and Differentiation of Data by Simplified Least Squares Procedures. Analytical Chemistry. 36:1627-1639.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;article{Savitzky1964,
 *    author = {A. Savitzky and Marcel J.E. Golay},
 *    journal = {Analytical Chemistry},
 *    pages = {1627-1639},
 *    title = {Smoothing and Differentiation of Data by Simplified Least Squares Procedures},
 *    volume = {36},
 *    year = {1964},
 *    HTTP = {http:&#47;&#47;dx.doi.org&#47;10.1021&#47;ac60214a047}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-plot-name-suffix &lt;java.lang.String&gt; (property: plotNameSuffix)
 * &nbsp;&nbsp;&nbsp;The suffix for the plot name; if left empty, the plot container automatically 
 * &nbsp;&nbsp;&nbsp;becomes an OVERLAY.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-window-size &lt;int&gt; (property: windowSize)
 * &nbsp;&nbsp;&nbsp;The window size to use, must be at least 20.
 * &nbsp;&nbsp;&nbsp;default: 7
 * &nbsp;&nbsp;&nbsp;minimum: 3
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleSavitzkyGolay
  extends AbstractPlotProcessor
  implements TechnicalInformationHandler {
  
  /** for serialization. */
  private static final long serialVersionUID = 5171916489269022308L;
  
  /** Size of window size for calculating lowess. */
  protected int m_WindowSize;

  /** for storing the plot data. */
  protected List<Point2D> m_Data;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "A processor that applies SimpleSavitzkyGolay smoothing.\n\n"
      + "For more information see:\n\n"
      + getTechnicalInformation().toString();
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Data = new ArrayList<Point2D>();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_Data.clear();
  }
  
  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.ARTICLE);
    result.setValue(Field.AUTHOR, "A. Savitzky and Marcel J.E. Golay");
    result.setValue(Field.TITLE, "Smoothing and Differentiation of Data by Simplified Least Squares Procedures");
    result.setValue(Field.JOURNAL, "Analytical Chemistry");
    result.setValue(Field.VOLUME, "36");
    result.setValue(Field.PAGES, "1627-1639");
    result.setValue(Field.YEAR, "1964");
    result.setValue(Field.HTTP, "http://dx.doi.org/10.1021/ac60214a047");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "window-size", "windowSize",
	    7, 3, null);
  }

  /**
   * Sets the polynomial order.
   *
   * @param value 	the order
   */
  public void setWindowSize(int value) {
    if ( (value == -1) || ((value >= 3) && (value % 2 == 1)) ) {
      m_WindowSize = value;
      reset();
    }
    else {
      getLogger().severe(
	  "The window size must be at least 3 and an odd number "
	  + "or -1 if no optimization is to be used (provided: " + value + ")!");
    }
  }

  /**
   * Returns the polynominal order.
   *
   * @return 		the order
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
    return "The window size to use, must be at least 20.";
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
    int					winOff;
    int					i;
    int					j;
    double				val;

    result = null;
    
    x = (Comparable) cont.getValue(SequencePlotterContainer.VALUE_X);
    y = (Comparable) cont.getValue(SequencePlotterContainer.VALUE_Y);
    
    if ((x instanceof Number) && (y instanceof Number)) {
      point = new Point2D.Double(((Number) x).doubleValue(), ((Number) y).doubleValue());
      m_Data.add(point);
      while (m_Data.size() > m_WindowSize)
	m_Data.remove(0);
      if (m_Data.size() == m_WindowSize) {
	result = new ArrayList<SequencePlotterContainer>();
	winOff = (m_WindowSize - 1)/2;
	for (i = winOff; i < m_Data.size() - winOff; i++) {
	  val = 0;
	  for (j = -winOff; j <= winOff; j++)
	    val += j * m_Data.get(i+j).getY();
	  result.add(new SequencePlotterContainer(getPlotName(cont), m_Data.get(i - winOff).getX(), val, getPlotType()));
	}
      }
    }
    
    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();
    
    m_Data.clear();
  }
}
