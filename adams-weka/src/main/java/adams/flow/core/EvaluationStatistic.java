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
 * EvaluationStatistic.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
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
public enum EvaluationStatistic
  implements EnumWithCustomDisplay<EvaluationStatistic> {

  NUMBER_CORRECT("Number correct", true),
  NUMBER_INCORRECT("Number incorrect", true),
  NUMBER_UNCLASSIFIED("Number unclassified", true),
  PERCENT_CORRECT("Percent correct", true),
  PERCENT_INCORRECT("Percent incorrect", true),
  PERCENT_UNCLASSIFIED("Percent unclassified", true),
  KAPPA_STATISTIC("Kappa statistic", true),
  MEAN_ABSOLUTE_ERROR("Mean absolute error", null),
  ROOT_MEAN_SQUARED_ERROR("Root mean squared error", null),
  RELATIVE_ABSOLUTE_ERROR("Relative absolute error", null),
  ROOT_RELATIVE_SQUARED_ERROR("Root relative squared error", null),
  CORRELATION_COEFFICIENT("Correlation coefficient", false),
  SF_PRIOR_ENTROPY("SF prior entropy", null),
  SF_SCHEME_ENTROPY("SF scheme entropy", null),
  SF_ENTROPY_GAIN("SF entropy gain", null),
  SF_MEAN_PRIOR_ENTROPY("SF mean prior entropy", null),
  SF_MEAN_SCHEME_ENTROPY("SF mean scheme entropy", null),
  SF_MEAN_ENTROPY_GAIN("SF mean entropy gain", null),
  KB_INFORMATION("KB information", true),
  KB_MEAN_INFORMATION("KB mean information", true),
  KB_RELATIVE_INFORMATION("KB relative information", true),
  TRUE_POSITIVE_RATE("True positive rate", true, true),
  NUM_TRUE_POSITIVES("Num true positives", true, true),
  FALSE_POSITIVE_RATE("False positive rate", true, true),
  NUM_FALSE_POSITIVES("Num false positives", true, true),
  TRUE_NEGATIVE_RATE("True negative rate", true, true),
  NUM_TRUE_NEGATIVES("Num true negatives", true, true),
  FALSE_NEGATIVE_RATE("False negative rate", true, true),
  NUM_FALSE_NEGATIVES("Num false negatives", true, true),
  IR_PRECISION("IR precision", true, true),
  IR_RECALL("IR recall", true, true),
  F_MEASURE("F measure", true, true),
  MATTHEWS_CORRELATION_COEFFICIENT("Matthews correlation coefficient", true, true),
  AREA_UNDER_ROC("Area under ROC", true, true),
  AREA_UNDER_PRC("Area under PRC", true, true),
  WEIGHTED_TRUE_POSITIVE_RATE("Weighted true positive rate", true),
  WEIGHTED_FALSE_POSITIVE_RATE("Weighted false positive rate", true),
  WEIGHTED_TRUE_NEGATIVE_RATE("Weighted true negative rate", true),
  WEIGHTED_FALSE_NEGATIVE_RATE("Weighted false negative rate", true),
  WEIGHTED_IR_PRECISION("Weighted IR precision", true),
  WEIGHTED_IR_RECALL("Weighted IR recall", true),
  WEIGHTED_F_MEASURE("Weighted F measure", true),
  WEIGHTED_MATTHEWS_CORRELATION_COEFFICIENT("Weighted Matthews correlation coefficient", true),
  WEIGHTED_AREA_UNDER_ROC("Weighted area under ROC", true),
  WEIGHTED_AREA_UNDER_PRC("Weighted area under PRC", true);
  
  /** the display value. */
  private String m_Display;

  /** the commandline string. */
  private String m_Raw;

  /** whether applies only to nominal classes or not. */
  protected Boolean m_OnlyNominal;
  
  /** whether the statistic is per class. */
  private boolean m_PerClass;

  /**
   * Initializes the element. Sets {@link #m_PerClass} to false
   *
   * @param display	the display value
   * @param onlyNominal	null if applies to nominal and numeric, true if only
   * 			nominal, false if only numeric classes
   */
  private EvaluationStatistic(String display, Boolean onlyNominal) {
    this(display, onlyNominal, false);
  }

  /**
   * Initializes the element.
   *
   * @param display	the display value
   * @param onlyNominal	null if applies to nominal and numeric, true if only
   * 			nominal, false if only numeric classes
   * @param perClass	whether this element is per class
   */
  private EvaluationStatistic(String display, Boolean onlyNominal, boolean perClass) {
    m_Display     = display;
    m_Raw         = super.toString();
    m_OnlyNominal = onlyNominal;
    m_PerClass    = perClass;
  }
  
  /**
   * Returns whether the statistic is a per-class one.
   * 
   * @return		true if per class
   */
  public boolean isPerClass() {
    return m_PerClass;
  }
  
  /**
   * Returns whether the statistic applies to nominal attributes only.
   * 
   * @return		true if only for nominal attributes
   */
  public boolean isOnlyNominal() {
    return (m_OnlyNominal != null) && m_OnlyNominal;
  }
  
  /**
   * Returns whether the statistic applies to numeric attributes only.
   * 
   * @return		true if only for numeric attributes
   */
  public boolean isOnlyNumeric() {
    return (m_OnlyNominal != null) && !m_OnlyNominal;
  }
  
  /**
   * Returns the display string, including nominal/numeric if it applies.
   *
   * @return		the display string
   */
  public String toDisplay() {
    String	result;
    
    result = m_Display;
    
    if (m_OnlyNominal != null) {
      if (m_OnlyNominal)
	result += " (nominal)";
      else
	result += " (numeric)";
    }
    
    return result;
  }
  
  /**
   * Returns the display string.
   *
   * @return		the display string
   */
  public String toDisplayShort() {
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
  public EvaluationStatistic parse(String s) {
    return (EvaluationStatistic) valueOf((AbstractOption) null, s);
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
  @Override
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
    return ((EvaluationStatistic) object).toRaw();
  }

  /**
   * Returns an enum generated from the string.
   *
   * @param option	the current option
   * @param str		the string to convert to an enum
   * @return		the generated enum or null in case of error
   */
  public static EvaluationStatistic valueOf(AbstractOption option, String str) {
    EvaluationStatistic	result;

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
      for (EvaluationStatistic f: values()) {
	if (f.toDisplay().equals(str)) {
	  result = f;
	  break;
	}
	else if (f.toDisplayShort().equals(str)) {
	  result = f;
	  break;
	}
      }
    }
    
    return result;
  }
}