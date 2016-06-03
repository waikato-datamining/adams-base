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
 * SimpleSpreadSheetRowPanelUpdater.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.spreadsheet;

/**
 <!-- globalinfo-start -->
 * Updates the flow after the specified number of tokens have been processed.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-update-interval &lt;int&gt; (property: updateInterval)
 * &nbsp;&nbsp;&nbsp;Specifies the number of tokens after which the display is being updated 
 * &nbsp;&nbsp;&nbsp;(markers excluded); &lt;= 0 means no update until flow finished.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 10855 $
 */
public class SimpleSpreadSheetRowPanelUpdater
  extends AbstractSpreadSheetRowPanelUpdater {

  /** for serialization. */
  private static final long serialVersionUID = 4418135588639219439L;

  /** the interval of tokens processed after which to update the display. */
  protected int m_UpdateInterval;

  /** the number of tokens accepted. */
  protected int m_NumTokensAccepted;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Updates the flow after the specified number of tokens have been processed.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "update-interval", "updateInterval",
	    1, -1, null);
  }

  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_NumTokensAccepted = 0;
  }

  /**
   * Sets the number of tokens after which the display is being updated.
   *
   * @param value 	the number of tokens
   */
  public void setUpdateInterval(int value) {
    if (value >= -1) {
      m_UpdateInterval = value;
      reset();
    }
    else {
      getLogger().severe(
	  "Update interval must be >= -1, provided: " + value);
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
	"Specifies the number of tokens after which the "
	+ "display is being updated (markers excluded); "
	+ "<= 0 means no update until flow finished.";
  }

  /**
   * Checks whether all conditions are met to notify the listeners for changes
   * in the plot.
   * 
   * @param panel	the plotter to potentially update
   * @param cont	the current plot container
   * @return		true if the listeners can be notified
   */
  @Override
  protected boolean canNotify(SpreadSheetRowPanel panel, SpreadSheetRowContainer cont) {
    m_NumTokensAccepted++;
    return (m_UpdateInterval > 0) && (m_NumTokensAccepted % m_UpdateInterval == 0);
  }
}
