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
 * TimeseriesAdd.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.data.timeseries.TimeseriesUtils;

/**
 <!-- globalinfo-start -->
 * Appends the incoming timeseries to the one available from storage.<br/>
 * If none yet available from storage, then the current one simply put into storage.<br/>
 * If the timeseries already contains elements with the same timestamp, then these will get replaced by the current ones.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.timeseries.Timeseries<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.timeseries.Timeseries<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: TimeseriesAdd
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the timeseries in internal storage.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesAdd
  extends AbstractDataContainerAdd<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = 2022407828928180169L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Superimposes the incoming timeseries on the one in storage, i.e., "
	+ "adds up the values for existing timestamps, otherwise adding new timestamps.\n"
	+ "If none yet available from storage, then the current one simply "
	+ "put into storage.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String storageNameTipText() {
    return "The name of the timeseries in internal storage.";
  }

  /**
   * Returns the data container class that the transformer handles.
   * 
   * @return		the container class
   */
  @Override
  protected Class getDataContainerClass() {
    return Timeseries.class;
  }

  /**
   * Appends the current data container to the stored one.
   * 
   * @param stored	the stored data container
   * @param current	the data to add
   */
  @Override
  protected void add(Timeseries stored, Timeseries current) {
    TimeseriesPoint	addPoint;
    TimeseriesPoint	currPoint;
    TimeseriesPoint	newPoint;
    int			index;
    
    for (Object o: current.toList()) {
      addPoint = (TimeseriesPoint) ((TimeseriesPoint) o).getClone();
      index    = TimeseriesUtils.findTimestamp(stored.toList(), addPoint.getTimestamp());
      if (index == -1) {
	stored.add(addPoint);
      }
      else {
	currPoint = (TimeseriesPoint) stored.toList().get(index);
	newPoint  = new TimeseriesPoint(currPoint.getTimestamp(), currPoint.getValue() + addPoint.getValue());
	stored.add(newPoint);
      }
    }
  }
}
