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
 * AndrewsCurvesGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.plotgenerator;

import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;
import adams.flow.container.SequencePlotterContainer;

/**
 <!-- globalinfo-start -->
 * Generates plot containers from row-based data for Andrews Curves.<br/>
 * César Ignacio García Osorio, Colin Fyfe (2003). AN EXTENSION OF ANDREWS CURVES FOR DATA ANALYSIS.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-default-cell-value &lt;double&gt; (property: defaultCellValue)
 * &nbsp;&nbsp;&nbsp;The default value for missing or non-numeric cells.
 * &nbsp;&nbsp;&nbsp;default: -1.0
 * </pre>
 * 
 * <pre>-num-points &lt;int&gt; (property: numPoints)
 * &nbsp;&nbsp;&nbsp;The number of points to generate for the curves.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use for the plot names.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-columns &lt;java.lang.String&gt; (property: plotColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to include in the curves; A range is a comma-separated 
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); '
 * &nbsp;&nbsp;&nbsp;inv(...)' inverts the range '...'; apart from column names (case-sensitive
 * &nbsp;&nbsp;&nbsp;), the following placeholders can be used as well: first, second, third, 
 * &nbsp;&nbsp;&nbsp;last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-meta-data-columns &lt;java.lang.String&gt; (property: metaDataColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to add as meta-data in the plot; A range is a comma-separated 
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); '
 * &nbsp;&nbsp;&nbsp;inv(...)' inverts the range '...'; apart from column names (case-sensitive
 * &nbsp;&nbsp;&nbsp;), the following placeholders can be used as well: first, second, third, 
 * &nbsp;&nbsp;&nbsp;last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AndrewsCurvesGenerator
  extends AbstractPlotGenerator
  implements TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 5687113015681744506L;

  /** the number of data points. */
  protected int m_NumPoints;
  
  /** the prefix for the plots. */
  protected String m_Prefix;
  
  /** the columns to plot. */
  protected SpreadSheetColumnRange m_PlotColumns;
  
  /** the meta-data columns. */
  protected SpreadSheetColumnRange m_MetaDataColumns;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Generates plot containers from row-based data for Andrews Curves.\n"
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
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.ARTICLE);
    result.setValue(Field.YEAR, "2003");
    result.setValue(Field.AUTHOR, "César Ignacio García Osorio and Colin Fyfe");
    result.setValue(Field.TITLE, "AN EXTENSION OF ANDREWS CURVES FOR DATA ANALYSIS");
    result.setValue(Field.HTTP, "http://cib.uco.es/documents/Garcia03SIGEF.pdf");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "num-points", "numPoints",
	    100, 1, null);

    m_OptionManager.add(
	    "prefix", "prefix",
	    "");

    m_OptionManager.add(
	    "columns", "plotColumns",
	    "");

    m_OptionManager.add(
	    "meta-data-columns", "metaDataColumns",
	    "");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_PlotColumns     = new SpreadSheetColumnRange();
    m_MetaDataColumns = new SpreadSheetColumnRange();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "plotColumns", (getPlotColumns().isEmpty() ? "-none-" : getPlotColumns()), "cols: ");
    result += QuickInfoHelper.toString(this, "prefix", (getPrefix().isEmpty() ? "-none-" : getPrefix()), ", prefix: ");
    result += QuickInfoHelper.toString(this, "metaDataColumns", (getMetaDataColumns().isEmpty() ? "-none-" : getMetaDataColumns()), ", meta-data: ");
    
    return result;
  }

  /**
   * Sets the number of points to generate.
   *
   * @param value	the number of points
   */
  public void setNumPoints(int value) {
    m_NumPoints = value;
    reset();
  }

  /**
   * Returns the number of points to generate.
   *
   * @return		the number of points
   */
  public int getNumPoints() {
    return m_NumPoints;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numPointsTipText() {
    return "The number of points to generate for the curves.";
  }

  /**
   * Sets the prefix for the plot names.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix for the plot names.
   *
   * @return		the prefix
   */
  public String getPrefix() {
    return m_Prefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prefixTipText() {
    return "The prefix to use for the plot names.";
  }

  /**
   * Sets the column range to use in the curves.
   *
   * @param value	the column range
   */
  public void setPlotColumns(String value) {
    m_PlotColumns.setRange(value);
    reset();
  }

  /**
   * Returns the current column range to use in the curves.
   *
   * @return		the column range
   */
  public String getPlotColumns() {
    return m_PlotColumns.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String plotColumnsTipText() {
    return "The range of columns to include in the curves; " + m_PlotColumns.getExample();
  }

  /**
   * Sets the column range to add as meta-data in the plot.
   *
   * @param value	the column range
   */
  public void setMetaDataColumns(String value) {
    m_MetaDataColumns.setRange(value);
    reset();
  }

  /**
   * Returns the current column range to add as meta-data in the plot.
   *
   * @return		the column range
   */
  public String getMetaDataColumns() {
    return m_MetaDataColumns.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String metaDataColumnsTipText() {
    return "The range of columns to add as meta-data in the plot; " + m_MetaDataColumns.getExample();
  }

  /**
   * Checks the spreadsheet.
   * 
   * @param sheet	the sheet to check
   */
  @Override
  protected void check(SpreadSheet sheet) {
    super.check(sheet);

    m_PlotColumns.setSpreadSheet(sheet);
    if (m_PlotColumns.getIntIndices().length == 0)
      throw new IllegalStateException("No plot columns defined/available?");

    m_MetaDataColumns.setSpreadSheet(sheet);
  }

  /**
   * Performs the actual generation of containers.
   * 
   * @param sheet	the basis for the containers
   * @return		the generated containers
   */
  @Override
  protected List<SequencePlotterContainer> doGenerate(SpreadSheet sheet) {
    List<SequencePlotterContainer>	result;
    int					r;
    int					i;
    double				t;
    double				y;
    int					n;
    SequencePlotterContainer		cont;
    int[]				cols;
    int					m;
    int[]				metaCols;
    
    result = new ArrayList<SequencePlotterContainer>();
    
    r        = 0;
    cols     = m_PlotColumns.getIntIndices();
    metaCols = m_MetaDataColumns.getIntIndices();
    for (Row row: sheet.rows()) {
      r++;
      for (i = 0; i < m_NumPoints; i++) {
	t = -Math.PI + (Math.PI * 2) / m_NumPoints * i;
	y = row.getCell(cols[0]).toDouble() / Math.sqrt(2);
	for (n = 1; n < cols.length; n++) {
	  if ((n + 1) % 2 == 0)
	    y += row.getCell(cols[n]).toDouble() * Math.sin(t * Math.ceil(n / 2));
	  else
	    y += row.getCell(cols[n]).toDouble() * Math.cos(t * Math.ceil(n / 2));
	}
	cont = new SequencePlotterContainer(m_Prefix + r, t, y);
	// meta-data
	for (m = 0; m < metaCols.length; m++)
	  cont.addMetaData(sheet.getColumnName(metaCols[m]), getCellObject(row, metaCols[m], null));
	result.add(cont);
      }
    }
    
    return result;
  }
}
