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
 * LabelCounts.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet.statistic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Counts how often a label (ie string) occurs.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-prefix &lt;java.lang.String&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix to use for the label, eg, to distinguish them from other statistics.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LabelCounts
  extends AbstractColumnStatistic {

  /** for serialization. */
  private static final long serialVersionUID = 330391755072250767L;

  /** the prefix to use. */
  protected String m_Prefix;
  
  /** the label counts. */
  protected HashMap<String,Integer> m_Counts;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Counts how often a label (ie string) occurs.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "prefix", "prefix",
	    "");
  }

  /**
   * Sets the prefix to use for the labels.
   *
   * @param value	the prefix
   */
  public void setPrefix(String value) {
    m_Prefix = value;
    reset();
  }

  /**
   * Returns the prefix to use for the labels.
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
    return "The prefix to use for the label, eg, to distinguish them from other statistics.";
  }

  /**
   * Performs initialization before the cells are being visited.
   * 
   * @param sheet	the spreadsheet to generate the stats for
   * @param colIndex	the column index
   */
  @Override
  protected void preVisit(SpreadSheet sheet, int colIndex) {
    m_Counts = new HashMap<String,Integer>();
  }

  /**
   * Gets called with every row in the spreadsheet for generating the stats.
   * 
   * @param row		the current row
   * @param colIndex	the column index
   */
  @Override
  protected void doVisit(Row row, int colIndex) {
    String	label;
    
    if (row.hasCell(colIndex) && row.getCell(colIndex).getContentType() == ContentType.STRING) {
      label = row.getCell(colIndex).getContent();
      if (!m_Counts.containsKey(label))
	m_Counts.put(label, 1);
      else
	m_Counts.put(label, m_Counts.get(label) + 1);
    }
  }

  /**
   * Finishes up the stats generation after all the cells have been visited.
   * 
   * @param sheet	the spreadsheet to generate the stats for
   * @param colIndex	the column index
   * @return		the generated stats
   */
  @Override
  protected SpreadSheet postVisit(SpreadSheet sheet, int colIndex) {
    SpreadSheet		result;
    Row			row;
    List<String>	labels;

    result = createOutputHeader();

    labels = new ArrayList<String>(m_Counts.keySet());;
    Collections.sort(labels);
    
    for (String label: labels) {
      row = result.addRow();
      row.addCell(0).setContent(m_Prefix + label);
      row.addCell(1).setContent(m_Counts.get(label));
    }

    m_Counts = null;
    
    return result;
  }
}
