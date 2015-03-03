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
 * NamedPlotUpdater.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.sequenceplotter;

import adams.core.NamedCounter;
import adams.flow.container.SequencePlotterContainer;
import adams.flow.container.SequencePlotterContainer.ContentType;

/**
 <!-- globalinfo-start -->
 * Updates the flow after the specified number of tokens per named plot have been processed.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-update-interval &lt;int&gt; (property: updateInterval)
 * &nbsp;&nbsp;&nbsp;Specifies the number of tokens per plot after which the display is being 
 * &nbsp;&nbsp;&nbsp;updated (markers excluded).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NamedPlotUpdater
  extends AbstractPlotUpdater {

  /** for serialization. */
  private static final long serialVersionUID = 4418135588639219439L;

  /** the interval of tokens processed after which to update the display. */
  protected int m_UpdateInterval;

  /** the number of tokens received. */
  protected NamedCounter m_NumTokensAccepted;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Updates the flow after the specified number of tokens per named plot have been processed.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "update-interval", "updateInterval",
	    1, 1, null);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_NumTokensAccepted = new NamedCounter();
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    m_NumTokensAccepted.clear();
  }

  /**
   * Sets the number of tokens after which the display is being updated.
   *
   * @param value 	the number of tokens
   */
  public void setUpdateInterval(int value) {
    if (value >= 1) {
      m_UpdateInterval = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Update interval must be >= 1, provided: " + value);
    }
  }

  /**
   * Returns the number of tokens after which the display is being updated.
   *
   * @return 		the number of tokens
   */
  public int getUpdateInterval() {
    return m_UpdateInterval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String updateIntervalTipText() {
    return 
	"Specifies the number of tokens per plot after which the "
	+ "display is being updated (markers excluded).";
  }

  /**
   * Checks whether all conditions are met to notify the listeners for changes
   * in the plot.
   * 
   * @param plotter	the plotter to potentially update
   * @param cont	the current plot container
   * @return		true if the listeners can be notified
   */
  @Override
  protected boolean canNotify(SequencePlotterPanel plotter, SequencePlotterContainer cont) {
    boolean			result;
    String			plotName;
    ContentType			type;

    result   = false;
    plotName = (String) cont.getValue(SequencePlotterContainer.VALUE_PLOTNAME);
    type     = (ContentType) cont.getValue(SequencePlotterContainer.VALUE_CONTENTTYPE);
    if (type == ContentType.PLOT)
      m_NumTokensAccepted.next(plotName);
    
    if (m_NumTokensAccepted.hasReached(plotName, m_UpdateInterval)) {
      m_NumTokensAccepted.clear(plotName);
      result = true;
    }
      
    return result;
  }
}
