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
 * ReportDateTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.timeseriessplit;

import adams.data.io.input.AbstractTimeseriesReader;
import adams.data.io.input.SimpleTimeseriesReader;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.flow.transformer.timeseriessplit.AbstractSplitOnDate.Segments;

/**
 * Tests the ReportDate row score.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9765 $
 */
public class ReportDateTest
  extends AbstractTimeseriesSplitterTestCase {

  /**
   * Initializes the test.
   * 
   * @param name	the name of the test
   */
  public ReportDateTest(String name) {
    super(name);
  }
  
  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"simple_with_date.sts",
	"simple_with_date.sts",
	"simple_with_date.sts",
	"simple_with_date.sts",
    };
  }

  /**
   * Returns the reader setups to read the input data files to use
   * in the regression test.
   *
   * @return		the readers
   */
  @Override
  protected AbstractTimeseriesReader[] getRegressionInputReaders() {
    return new AbstractTimeseriesReader[]{
	new SimpleTimeseriesReader(),
	new SimpleTimeseriesReader(),
	new SimpleTimeseriesReader(),
	new SimpleTimeseriesReader(),
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractTimeseriesSplitter[] getRegressionSetups() {
    ReportDate[]	result;
    
    result    = new ReportDate[4];

    result[0] = new ReportDate();
    result[0].setField(new Field("Date", DataType.STRING));
    
    result[1] = new ReportDate();
    result[1].setField(new Field("Date", DataType.STRING));
    result[1].setSegments(Segments.BEFORE);
    
    result[2] = new ReportDate();
    result[2].setField(new Field("Date", DataType.STRING));
    result[2].setSegments(Segments.AFTER);
    result[2].setIncludeSplitDate(true);
    
    result[3] = new ReportDate();
    result[3].setField(new Field("DateCustom", DataType.STRING));
    result[3].setCustomFormat("yyyy|MM|dd|HH|mm");;
    
    return result;
  }
}
