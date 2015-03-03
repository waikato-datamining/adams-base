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
 * ExperimentComparisonField.java
 * Copyright (C) 2009-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.EnumWithCustomDisplay;
import adams.core.option.AbstractOption;

/**
 * The enumeration for the comparison field.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public enum ExperimentStatistic
  implements EnumWithCustomDisplay<ExperimentStatistic> {

  ELAPSED_TIME_TRAINING("Elapsed_Time_training"),
  ELAPSED_TIME_TESTING("Elapsed_Time_testing"),
  USERCPU_TIME_TRAINING("UserCPU_Time_training"),
  USERCPU_TIME_TESTING("UserCPU_Time_testing"),
  SERIALIZED_MODEL_SIZE("Serialized_Model_Size"),
  SERIALIZED_TRAIN_SET_SIZE("Serialized_Train_Set_Size"),
  SERIALIZED_TEST_SET_SIZE("Serialized_Test_Set_Size"),
  NUMBER_OF_TRAINING_INSTANCES("Number_of_training_instances"),
  NUMBER_OF_TESTING_INSTANCES("Number_of_testing_instances"),
  NUMBER_CORRECT("Number_correct (nominal)"),
  NUMBER_INCORRECT("Number_incorrect (nominal)"),
  NUMBER_UNCLASSIFIED("Number_unclassified (nominal)"),
  PERCENT_CORRECT("Percent_correct (nominal)"),
  PERCENT_INCORRECT("Percent_incorrect (nominal)"),
  PERCENT_UNCLASSIFIED("Percent_unclassified (nominal)"),
  KAPPA_STATISTIC("Kappa_statistic (nominal)"),
  MEAN_ABSOLUTE_ERROR("Mean_absolute_error"),
  ROOT_MEAN_SQUARED_ERROR("Root_mean_squared_error"),
  RELATIVE_ABSOLUTE_ERROR("Relative_absolute_error"),
  ROOT_RELATIVE_SQUARED_ERROR("Root_relative_squared_error"),
  CORRELATION_COEFFICIENT("Correlation_coefficient (numeric)"),
  SF_PRIOR_ENTROPY("SF_prior_entropy"),
  SF_SCHEME_ENTROPY("SF_scheme_entropy"),
  SF_ENTROPY_GAIN("SF_entropy_gain"),
  SF_MEAN_PRIOR_ENTROPY("SF_mean_prior_entropy"),
  SF_MEAN_SCHEME_ENTROPY("SF_mean_scheme_entropy"),
  SF_MEAN_ENTROPY_GAIN("SF_mean_entropy_gain"),
  KB_INFORMATION("KB_information (nominal)"),
  KB_MEAN_INFORMATION("KB_mean_information (nominal)"),
  KB_RELATIVE_INFORMATION("KB_relative_information (nominal)"),
  TRUE_POSITIVE_RATE("True_positive_rate (nominal)"),
  NUM_TRUE_POSITIVES("Num_true_positives (nominal)"),
  FALSE_POSITIVE_RATE("False_positive_rate (nominal)"),
  NUM_FALSE_POSITIVES("Num_false_positives (nominal)"),
  TRUE_NEGATIVE_RATE("True_negative_rate (nominal)"),
  NUM_TRUE_NEGATIVES("Num_true_negatives (nominal)"),
  FALSE_NEGATIVE_RATE("False_negative_rate (nominal)"),
  NUM_FALSE_NEGATIVES("Num_false_negatives (nominal)"),
  IR_PRECISION("IR_precision (nominal)"),
  IR_RECALL("IR_recall (nominal)"),
  F_MEASURE("F_measure (nominal)"),
  MATTHEWS_CORRELATION_COEFFICIENT("Matthews_correlation (nominal)"),
  AREA_UNDER_ROC("Area_under_ROC (nominal)"),
  AREA_UNDER_PRC("Area_under_PRC (nominal)"),
  WEIGHTED_TRUE_POSITIVE_RATE("Weighted_avg_true_positive_rate (nominal)"),
  WEIGHTED_FALSE_POSITIVE_RATE("Weighted_avg_false_positive_rate (nominal)"),
  WEIGHTED_TRUE_NEGATIVE_RATE("Weighted_avg_true_negative_rate (nominal)"),
  WEIGHTED_FALSE_NEGATIVE_RATE("Weighted_avg_false_negative_rate (nominal)"),
  WEIGHTED_IR_PRECISION("Weighted_avg_IR_precision (nominal)"),
  WEIGHTED_IR_RECALL("Weighted_avg_IR_recall (nominal)"),
  WEIGHTED_F_MEASURE("Weighted_avg_F_measure (nominal)"),
  WEIGHTED_MATTHEWS_CORRELATION_COEFFICIENT("Weighted_avg_matthews_correlation (nominal)"),
  WEIGHTED_AREA_UNDER_ROC("Weighted_avg_area_under_ROC (nominal)"),
  WEIGHTED_AREA_UNDER_PRC("Weighted_avg_area_under_PRC (nominal)");

  /** the display value. */
  private String m_Display;

  /** the commandline string. */
  private String m_Raw;

  /**
   * Initializes the element.
   *
   * @param display	the display value
   */
  private ExperimentStatistic(String display) {
    m_Display = display;
    m_Raw     = super.toString();
  }

  /**
   * Returns the display string.
   *
   * @return		the display string
   */
  public String toDisplay() {
    return m_Display;
  }

  /**
   * Returns the raw enum string.
   *
   * @return		the raw enum string
   */
  public String toRaw() {
    return m_Raw;
  }

  /**
   * Parses the given string and returns the associated enum.
   *
   * @param s		the string to parse
   * @return		the enum or null if not found
   */
  public ExperimentStatistic parse(String s) {
    return (ExperimentStatistic) valueOf((AbstractOption) null, s);
  }

  /**
   * Returns the display string without the "numeric" or "nominal" in
   * parentheses.
   *
   * @return		the field string
   */
  public String getField() {
    return m_Display.replaceAll(" .*", "");
  }

  /**
   * Returns the displays string.
   *
   * @return		the display string
   */
  public String toString() {
    return m_Display;
  }

  /**
   * Returns the enum as string.
   *
   * @param option	the current option
   * @param object	the enum object to convert
   * @return		the generated string
   */
  public static String toString(AbstractOption option, Object object) {
    return ((ExperimentStatistic) object).toRaw();
  }

  /**
   * Returns an enum generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to an enum
   * @return		the generated enum or null in case of error
   */
  public static ExperimentStatistic valueOf(AbstractOption option, String str) {
    ExperimentStatistic	result;

    result = null;

    // default parsing
    try {
      result = valueOf(str);
    }
    catch (Exception e) {
      // ignored
    }

    // try display
    if (result == null) {
      for (ExperimentStatistic f: values()) {
	if (f.toDisplay().equals(str)) {
	  result = f;
	  break;
	}
      }
    }

    return result;
  }
}