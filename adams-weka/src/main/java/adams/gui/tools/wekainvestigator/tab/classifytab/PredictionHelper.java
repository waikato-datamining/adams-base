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
 * PredictionHelper.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab.classifytab;

import adams.core.MessageCollection;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingSupporter;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.container.WekaEvaluationContainer;
import adams.flow.core.Token;
import adams.flow.transformer.SpreadSheetMerge;
import adams.flow.transformer.WekaPredictionsToSpreadSheet;
import weka.classifiers.Evaluation;

/**
 * Helper class for dealing with predictions from result items.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PredictionHelper {

  /**
   * Turns the result item into a spreadsheet with the predictions.
   *
   * @param logger 	the object used for logging, can be null
   * @param errors 	for collecting errors
   * @param item	the result item to use
   * @param addAdditionalAttributes 	whether to add additional attributes
   * @param showError 	whether to add the error in a separate column
   * @return		the generated spreadsheet
   */
  public static SpreadSheet toSpreadSheet(LoggingSupporter logger, MessageCollection errors, ResultItem item, boolean addAdditionalAttributes, boolean showError) {
    return toSpreadSheet(logger, errors, item, addAdditionalAttributes, false, false, false, showError, false);
  }

  /**
   * Turns the result item into a spreadsheet with the predictions.
   *
   * @param logger 	the object used for logging, can be null
   * @param errors 	for collecting errors
   * @param eval	the evaluation to use
   * @param originalIndices 	the original indices to use, can be null
   * @param additionalAttributes 	the additional attributes to add, can be null
   * @param showError 	whether to add the error in a separate column
   * @return		the generated spreadsheet
   */
  public static SpreadSheet toSpreadSheet(LoggingSupporter logger, MessageCollection errors, Evaluation eval, int[] originalIndices, SpreadSheet additionalAttributes, boolean showError) {
    return toSpreadSheet(logger, errors, eval, originalIndices, additionalAttributes, false, false, false, showError, false);
  }

  /**
   * Turns the result item into a spreadsheet with the predictions.
   *
   * @param logger 	the object used for logging, can be null
   * @param errors 	for collecting errors
   * @param item	the result item to use
   * @param addAdditionalAttributes 	whether to add additional attributes
   * @param addLabelIndex 	whether to add the label index in a separate column
   * @param showDistribution 	whether to add the distribution in a separate column
   * @param showProbability 	whether to add the probability in a separate column
   * @param showError 	whether to add the error in a separate column
   * @param showWeight 	whether to add the weight in a separate column
   * @return		the generated spreadsheet, null if failed
   */
  public static SpreadSheet toSpreadSheet(LoggingSupporter logger, MessageCollection errors, ResultItem item, boolean addAdditionalAttributes, boolean addLabelIndex, boolean showDistribution, boolean showProbability, boolean showError, boolean showWeight) {
    return toSpreadSheet(logger, errors, item.getEvaluation(), item.getOriginalIndices(), addAdditionalAttributes ? item.getAdditionalAttributes() : null, addLabelIndex, showDistribution, showProbability, showError, showWeight);
  }

  /**
   * Turns the result item into a spreadsheet with the predictions.
   *
   * @param logger 	the object used for logging, can be null
   * @param errors 	for collecting errors
   * @param eval	the evaluation to use
   * @param originalIndices     the original indices to use, can be null
   * @param additionalAttributes 	the additional attributes to use, can be null
   * @param addLabelIndex 	whether to add the label index in a separate column
   * @param showDistribution 	whether to add the distribution in a separate column
   * @param showProbability 	whether to add the probability in a separate column
   * @param showError 	whether to add the error in a separate column
   * @param showWeight 	whether to add the weight in a separate column
   * @return		the generated spreadsheet, null if failed
   */
  public static SpreadSheet toSpreadSheet(LoggingSupporter logger, MessageCollection errors, Evaluation eval, int[] originalIndices, SpreadSheet additionalAttributes, boolean addLabelIndex, boolean showDistribution, boolean showProbability, boolean showError, boolean showWeight) {
    WekaPredictionsToSpreadSheet 	p2s;
    WekaEvaluationContainer 		cont;
    Token 				token;
    SpreadSheet				sheet;
    SpreadSheetMerge 			merge;
    String				msg;

    cont = new WekaEvaluationContainer(eval);
    if (originalIndices != null)
      cont.setValue(WekaEvaluationContainer.VALUE_ORIGINALINDICES, originalIndices);
    p2s = new WekaPredictionsToSpreadSheet();
    p2s.setAddLabelIndex(addLabelIndex);
    p2s.setShowDistribution(showDistribution);
    p2s.setShowProbability(showProbability);
    p2s.setShowError(showError);
    p2s.setShowWeight(showWeight);
    p2s.setUseOriginalIndices(true);
    p2s.input(new Token(cont));
    try {
      p2s.execute();
    }
    catch (Exception e) {
      msg = "Failed to assemble predictions!";
      if (logger != null)
	LoggingHelper.handleException(logger, msg, e);
      errors.add(msg, e);
      return null;
    }
    token = p2s.output();

    // add additional attributes
    if (additionalAttributes != null) {
      sheet = (SpreadSheet) token.getPayload();
      merge = new SpreadSheetMerge();
      token = new Token(new SpreadSheet[]{sheet, additionalAttributes});
      merge.input(token);
      msg = merge.execute();
      if (msg != null) {
	msg = "Failed to merge predictions and additional attributes!\n" + msg;
	if (logger != null)
	  logger.getLogger().severe(msg);
	errors.add(msg);
	token = new Token(sheet);
      }
      else {
	token = merge.output();
      }
    }

    return (SpreadSheet) token.getPayload();
  }
}
