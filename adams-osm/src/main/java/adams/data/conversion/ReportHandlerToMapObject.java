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
 * ReportHandlerToMapObject.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;

import adams.core.QuickInfoHelper;
import adams.data.conversion.mapobject.AbstractReportMapObjectGenerator;
import adams.data.conversion.mapobject.SimpleReportCircleMarkerGenerator;
import adams.data.report.Report;
import adams.data.report.ReportHandler;

/**
 <!-- globalinfo-start -->
 * Creates features from the incoming reporthandler's reports and turns them into a OpenStreetMap MapObject.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-generator &lt;adams.data.conversion.mapobject.AbstractReportMapObjectGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The generator to use for turning the report of the report handler into a 
 * &nbsp;&nbsp;&nbsp;MapObject.
 * &nbsp;&nbsp;&nbsp;default: adams.data.conversion.mapobject.SimpleReportCircleMarkerGenerator
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportHandlerToMapObject
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = -5361157360265877867L;
  
  /** the feature generator to use. */
  protected AbstractReportMapObjectGenerator m_Generator;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Creates features from the incoming reporthandler's reports and turns them into a OpenStreetMap MapObject.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "generator", "generator",
	    new SimpleReportCircleMarkerGenerator());
  }
  
  /**
   * Sets the generator to use.
   *
   * @param value	the generator
   */
  public void setGenerator(AbstractReportMapObjectGenerator value) {
    m_Generator = value;
    reset();
  }

  /**
   * Returns the generator to use.
   *
   * @return		the generator
   */
  public AbstractReportMapObjectGenerator getGenerator() {
    return m_Generator;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generatorTipText() {
    return "The generator to use for turning the report of the report handler into a MapObject.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "generator", m_Generator);
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return ReportHandler.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    if (m_Generator == null)
      return MapObject.class;
    else
      return m_Generator.generates();
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Object		result;
    Report		report;

    report  = ((ReportHandler) m_Input).getReport();
    result = m_Generator.generate(report);

    return result;
  }
}
