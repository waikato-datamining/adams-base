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
 * AbstractPlotProcessor.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.control.plotprocessor;

import java.util.List;

import adams.core.CleanUpHandler;
import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.container.SequencePlotterContainer.ContentType;
import adams.flow.control.PlotProcessor;

/**
 * Ancestor for processors of plot containers. A processor produces "additional"
 * containers using the the input provided by the {@link PlotProcessor} actor.
 * This allows, for instance, for applying smoothing algorithms to the stream
 * of plot containers passing through.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPlotProcessor
  extends AbstractOptionHandler
  implements QuickInfoSupporter, CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = 6709551233400282459L;

  /** the suffix for the plotname to use (becomes overlay if none provided). */
  protected String m_PlotNameSuffix;
  
  /** for storing an error message. */
  protected String m_LastError;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "plot-name-suffix", "plotNameSuffix",
	    "");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_LastError = null;
  }
  
  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_LastError = null;
  }

  /**
   * Sets the suffix for the plot name. 
   * If left empty, plot container will become a {@link ContentType#OVERLAY}.
   *
   * @param value 	the suffix
   */
  public void setPlotNameSuffix(String value) {
    m_PlotNameSuffix = value;
    reset();
  }

  /**
   * Returns the current suffix for the plot name.
   * If left empty, plot container will become a {@link ContentType#OVERLAY}.
   *
   * @return 		the suffix
   */
  public String getPlotNameSuffix() {
    return m_PlotNameSuffix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plotNameSuffixTipText() {
    return 
	"The suffix for the plot name; if left empty, the plot container "
	+ "automatically becomes an " + ContentType.OVERLAY + ".";
  }
  
  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <p/>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    String	result;
    
    result = QuickInfoHelper.toString(this, "plotNameSuffix", (m_PlotNameSuffix.isEmpty() ? "" : m_PlotNameSuffix));
    if (result == null)
      result = "overlay";
    else
      result = "suffix: " + result;
    
    return result;
  }

  /**
   * Checks the current container.
   * <p/>
   * Default implementation only checks whether a container was provided at all.
   * 
   * @param cont	the container to check
   * @return		null if everything OK, otherwise error message
   */
  protected String check(SequencePlotterContainer cont) {
    if (cont == null)
      return "No plot container provided!";
    return null;
  }

  /**
   * Returns the plot name. Takes a potentially set suffix into account.
   * 
   * @param cont	the container to extract the name from
   * @return		the plot name
   */
  protected String getPlotName(SequencePlotterContainer cont) {
    String	result;
    
    result = (String) cont.getValue(SequencePlotterContainer.VALUE_PLOTNAME);
    if ((m_PlotNameSuffix != null) && !m_PlotNameSuffix.isEmpty())
      result += m_PlotNameSuffix;
    
    return result;
  }
  
  /**
   * Returns the type of plot to use. In case a plot name suffix is set,
   * it will be normal {@link ContentType#PLOT}, otherwise {@link ContentType#OVERLAY}.
   * 
   * @return		the type
   */
  protected ContentType getPlotType() {
    if ((m_PlotNameSuffix != null) && !m_PlotNameSuffix.isEmpty())
      return ContentType.PLOT;
    else
      return ContentType.OVERLAY;
  }

  /**
   * Processes the provided container. Generates new containers
   * if applicable.
   * 
   * @param cont	the container to process
   * @return		null if no new containers were produced
   */
  protected abstract List<SequencePlotterContainer> doProcess(SequencePlotterContainer cont);
  
  /**
   * Process the given container.
   * 
   * @param cont	the container to process
   * @return		null if no new containers were produced
   */
  public List<SequencePlotterContainer> process(SequencePlotterContainer cont) {
    m_LastError = check(cont);
    
    if (m_LastError == null) {
      return doProcess(cont);
    }
    else {
      getLogger().severe(m_LastError);
      return null;
    }
  }
  
  /**
   * Returns whether the last processing generated an error message.
   * 
   * @return		true if an error message was generated
   */
  public boolean hasLastError() {
    return (m_LastError != null);
  }
  
  /**
   * Returns the error message from the last processing (if any).
   * 
   * @return		the last error message, null if none occurred
   */
  public String getLastError() {
    return m_LastError;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_LastError = null;
  }
}
