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
 * PredictionUtils.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools.wekainvestigator.tab.classifytab.output.repeatedCV;

import adams.core.MessageCollection;
import adams.core.logging.Logger;
import adams.data.RoundingType;
import adams.data.RoundingUtils;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.StatUtils;
import adams.gui.tools.wekainvestigator.tab.classifytab.PredictionHelper;
import adams.gui.tools.wekainvestigator.tab.classifytab.ResultItem;
import gnu.trove.list.TIntList;

import java.util.logging.Level;

/**
 * Helper class for predictions from repeated cross-validation runs.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PredictionUtils {

  /**
   * Generates a spreadsheet with the statistics.
   *
   * @param item	the item to calculate the statistics from
   * @param errors	for collecting error messages
   * @param centerStat 	the center statistics like mean
   * @param lowerStat 	the lower bound statistic
   * @param upperStat 	the upper bound statistic
   * @param numDecimals	the number of decimals in the spreadsheet, -1 for no rounding
   * @param logger 	for logging problems
   * @param cols 	for storing the column indices of the computed statistics (center, lower, upper), can be null
   * @return		the spreadsheet
   */
  public static SpreadSheet calcStats(ResultItem item, MessageCollection errors,
				      CenterStatistic centerStat, LowerStatistic lowerStat, UpperStatistic upperStat,
				      int numDecimals, Logger logger, TIntList cols) {
    return calcStats(item, errors, centerStat, lowerStat, upperStat, numDecimals, logger, cols, false);
  }

  /**
   * Generates a spreadsheet with the statistics.
   *
   * @param item	the item to calculate the statistics from
   * @param errors	for collecting error messages
   * @param centerStat 	the center statistics like mean
   * @param lowerStat 	the lower bound statistic
   * @param upperStat 	the upper bound statistic
   * @param numDecimals	the number of decimals in the spreadsheet, -1 for no rounding
   * @param logger 	for logging problems
   * @param cols 	for storing the column indices of the computed statistics (center, lower, upper, [actual]), can be null
   * @param addActual	whether to add the "actual" value in a separate column
   * @return		the spreadsheet
   */
  public static SpreadSheet calcStats(ResultItem item, MessageCollection errors,
				      CenterStatistic centerStat, LowerStatistic lowerStat, UpperStatistic upperStat,
				      int numDecimals, Logger logger, TIntList cols, boolean addActual) {
    SpreadSheet 	result;
    int			run;
    int			r;
    int			p;
    SpreadSheet[]	preds;
    Row 		row;
    SpreadSheet		additional;
    double[]		values;
    double 		center;
    double		lower;
    double		upper;
    int 		predCol;
    int			actCol;
    double		actual;
    boolean		addRows;
    int 		colCenter;
    int			colLower;
    int			colUpper;
    int			colActual;

    additional = item.getAdditionalAttributes();

    // align all predictions
    preds = new SpreadSheet[item.getRunEvaluations().length];
    for (run = 0; run < item.getRunEvaluations().length; run++) {
      preds[run] = PredictionHelper.toSpreadSheet(
	null, errors, item.getRunEvaluations()[run], item.getRunOriginalIndices()[run], null, false, false, false, false, false);
    }

    // initialize spreadsheet
    if (additional != null) {
      result    = additional.getClone();
      addRows   = false;
      colCenter = result.getColumnCount();
      colLower  = result.getColumnCount() + 1;
      colUpper  = result.getColumnCount() + 2;
      colActual = result.getColumnCount() + 3;
      result.insertColumn(result.getColumnCount(), centerStat.name());
      result.insertColumn(result.getColumnCount(), lowerStat.name());
      result.insertColumn(result.getColumnCount(), upperStat.name());
      if (addActual)
	result.insertColumn(result.getColumnCount(), "Actual");
    }
    else {
      result    = new DefaultSpreadSheet();
      addRows   = true;
      colCenter = 0;
      colLower  = 1;
      colUpper  = 2;
      colActual = 3;
      row       = result.getHeaderRow();
      row.addCell("statistic-" + centerStat).setContent(centerStat.name());
      row.addCell("lower-" + lowerStat).setContent(lowerStat.name());
      row.addCell("upper-" + upperStat).setContent(upperStat.name());
      if (addActual)
	row.addCell("Actual").setContent("Actual");
    }

    // record column indices
    if (cols != null) {
      cols.clear();
      cols.add(colCenter);
      cols.add(colLower);
      cols.add(colUpper);
      if (addActual)
	cols.add(colActual);
    }

    // compute stats per row
    for (r = 0; r < result.getRowCount(); r++) {
      if (addRows)
	row = result.addRow();
      else
	row = result.getRow(r);
      values = new double[preds.length];
      actual = Double.NaN;
      for (p = 0; p < preds.length; p++) {
	if (preds[p] != null) {
	  predCol   = preds[p].getHeaderRow().indexOfContent("Predicted");
	  values[p] = preds[p].getRow(r).getCell(predCol).toDouble();
	  actCol    = preds[p].getHeaderRow().indexOfContent("Actual");
	  actual    = preds[p].getRow(r).getCell(actCol).toDouble();
	}
	else {
	  values[p] = Double.NaN;
	}
      }

      // center statistic
      center = Double.NaN;
      try {
	switch (centerStat) {
	  case MEAN:
	    center = StatUtils.mean(values);
	    break;
	  case MEDIAN:
	    center = StatUtils.median(values);
	    break;
	  default:
	    logger.severe("Unhandled statistic: " + centerStat);
	}
      }
      catch (Exception e) {
	logger.log(Level.SEVERE, "Failed to compute statistic " + centerStat + " for row #" + (r+1) + "!", e);
      }

      // lower statistic
      lower = Double.NaN;
      try {
	switch (lowerStat) {
	  case MIN:
	    lower = StatUtils.min(values);
	    break;
	  case QUARTILE25:
	    lower = StatUtils.quartile(values, 0.25);
	    break;
	  default:
	    logger.severe("Unhandled lower: " + lowerStat);
	}
      }
      catch (Exception e) {
	logger.log(Level.SEVERE, "Failed to compute lower " + lowerStat + " for row #" + (r+1) + "!", e);
      }

      // upper statistic
      upper = Double.NaN;
      try {
	switch (upperStat) {
	  case MAX:
	    upper = StatUtils.max(values);
	    break;
	  case QUARTILE75:
	    upper = StatUtils.quartile(values, 0.75);
	    break;
	  default:
	    logger.severe("Unhandled upper: " + upperStat);
	}
      }
      catch (Exception e) {
	logger.log(Level.SEVERE, "Failed to compute upper " + upperStat + " for row #" + (r+1) + "!", e);
      }

      // round values?
      if (numDecimals > -1) {
	center = RoundingUtils.apply(RoundingType.ROUND, center,  numDecimals);
	lower = RoundingUtils.apply(RoundingType.ROUND, lower, numDecimals);
	upper = RoundingUtils.apply(RoundingType.ROUND, upper, numDecimals);
      }

      row.getCell(colCenter).setContent(center);
      row.getCell(colLower).setContent(lower);
      row.getCell(colUpper).setContent(upper);
      if (addActual)
	row.getCell(colActual).setContent(actual);
    }

    return result;
  }

}
