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
 * LagMakerOptions.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import weka.classifiers.timeseries.core.TSLagMaker;
import adams.core.option.AbstractOptionGroup;

/**
 * Option group for {@link TSLagMaker} objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LagMakerOptions
  extends AbstractOptionGroup {

  /** for serialization. */
  private static final long serialVersionUID = -2085099247219025554L;

  /** the minimum lag. */
  protected int m_MinLag;
  
  /** the maximum lag. */
  protected int m_MaxLag;
  
  /** lag fine tune. */
  protected String m_LagFineTune;
  
  /** average consecutive long lags. */
  protected boolean m_AverageConsecutiveLongLags;
  
  /** average lags after. */
  protected int m_AverageLagsAfter;
  
  /** number of consecutive long lags to average. */
  protected int m_NumConsecutiveLongLagsToAverage;
  
  /** whether to adjust for trends. */
  protected boolean m_AdjustForTrends;
  
  /** whether to adjust for variance. */
  protected boolean m_AdjustForVariance;

  /** the timestamp field. */
  protected String m_TimeStampField;

  /** the am-pm indicator. */
  protected boolean m_AddAMIndicator;

  /** the day of week indicator. */
  protected boolean m_AddDayOfWeek;

  /** the day of month indicator. */
  protected boolean m_AddDayOfMonth;

  /** the number of days in month indicator. */
  protected boolean m_AddNumDaysInMonth;

  /** the weekend indicator. */
  protected boolean m_AddWeekendIndicator;

  /** the month indicator. */
  protected boolean m_AddMonthOfYear;

  /** the quarted indicator. */
  protected boolean m_AddQuarterOfYear;
  
  /** the entries to skip. */
  protected String m_SkipEntries;
  
  /**
   * Returns the group name.
   * 
   * @return		the name
   */
  @Override
  protected String getGroupName() {
    return "TSLagMaker";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"min-lag", "minLag", 
	1, 1, null);

    m_OptionManager.add(
	"max-lag", "maxLag", 
	12, 1, null);

    m_OptionManager.add(
	"lag-fine-tune", "lagFineTune", 
	"");

    m_OptionManager.add(
	"avg-consecutive-long-lags", "averageConsecutiveLongLags", 
	false);

    m_OptionManager.add(
	"avg-lags-after", "averageLagsAfter", 
	2, 1, null);

    m_OptionManager.add(
	"num-consecutive-long-lags-to-avg", "numConsecutiveLongLagsToAverage", 
	2, 1, null);

    m_OptionManager.add(
	"adjust-for-trends", "adjustForTrends", 
	false);

    m_OptionManager.add(
	"adjust-for-variance", "adjustForVariance", 
	false);

    m_OptionManager.add(
	"time-stamp-field", "timeStampField", 
	"");

    m_OptionManager.add(
	"am", "addAMIndicator", 
	false);

    m_OptionManager.add(
	"day-of-week", "addDayOfWeek", 
	false);

    m_OptionManager.add(
	"day-of-month", "addDayOfMonth", 
	false);

    m_OptionManager.add(
	"num-days-in-month", "addNumDaysInMonth", 
	false);

    m_OptionManager.add(
	"weekend", "addWeekendIndicator", 
	false);

    m_OptionManager.add(
	"month-of-year", "addMonthOfYear", 
	false);

    m_OptionManager.add(
	"quarter", "addQuarterOfYear", 
	false);

    m_OptionManager.add(
	"skip-entries", "skipEntries", 
	"");
  }

  /**
   * Sets the minimum lag.
   * 
   * @param value	the min lag
   */
  public void setMinLag(int value) {
    m_MinLag = value;
    reset();
  }

  /**
   * Returns the maximum lag.
   * 
   * @return 		the max lag
   */
  public int getMinLag() {
    return m_MinLag;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String minLagTipText() {
    return "The minimum lag to create (default = 1, ie t-1).";
  }

  /**
   * Sets the maximum lag.
   * 
   * @param value	the max lag
   */
  public void setMaxLag(int value) {
    m_MaxLag = value;
    reset();
  }

  /**
   * Returns the maximum lag.
   * 
   * @return 		the max lag
   */
  public int getMaxLag() {
    return m_MaxLag;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String maxLagTipText() {
    return "The maximum lag to create (default = 12, ie t-12).";
  }

  /**
   * Sets the lag fine tune.
   * 
   * @param value	the fine tune
   */
  public void setLagFineTune(String value) {
    m_LagFineTune = value;
    reset();
  }

  /**
   * Returns the lag fine tune.
   * 
   * @return 		the fine tune
   */
  public String getLagFineTune() {
    return m_LagFineTune;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String lagFineTuneTipText() {
    return "The ranges to fine tune lag selection.";
  }

  /**
   * Sets whether to average consecutive long lags.
   * 
   * @param value	true if to average
   */
  public void setAverageConsecutiveLongLags(boolean value) {
    m_AverageConsecutiveLongLags = value;
    reset();
  }

  /**
   * Returns whether to average consecutive long lags.
   * 
   * @return 		true if to average
   */
  public boolean getAverageConsecutiveLongLags() {
    return m_AverageConsecutiveLongLags;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String averageConsecutiveLongLagsTipText() {
    return "If enabled, consecutive long lagged variables are to be averaged.";
  }

  /**
   * Sets the number of lags after which to average.
   * 
   * @param value	the number
   */
  public void setAverageLagsAfter(int value) {
    m_AverageLagsAfter = value;
    reset();
  }

  /**
   * Returns the number of lags after which to average.
   * 
   * @return 		the number of lags
   */
  public int getAverageLagsAfter() {
    return m_AverageLagsAfter;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String averageLagsAfterTipText() {
    return "The point after which long lagged variables will be averaged.";
  }

  /**
   * Sets the number of consecutive long lags to average.
   * 
   * @param value	the number
   */
  public void setNumConsecutiveLongLagsToAverage(int value) {
    m_NumConsecutiveLongLagsToAverage = value;
    reset();
  }

  /**
   * Returns the number of consecutive long lags to average.
   * 
   * @return 		the number
   */
  public int getNumConsecutiveLongLagsToAverage() {
    return m_NumConsecutiveLongLagsToAverage;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String numConsecutiveLongLagsToAverageTipText() {
    return "The number of consecutive long lagged variables to average.";
  }

  /**
   * Sets whether to adjust for trends.
   * 
   * @param value	true if to adjust
   */
  public void setAdjustForTrends(boolean value) {
    m_AdjustForTrends = value;
    reset();
  }

  /**
   * Returns whether to adjust for trends.
   * 
   * @return 		true if to adjust
   */
  public boolean getAdjustForTrends() {
    return m_AdjustForTrends;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String adjustForTrendsTipText() {
    return "If enabled, adjusting for trends via a real or artificial timestamp.";
  }

  /**
   * Sets whether to adjust for variance.
   * 
   * @param value	true if to adjust
   */
  public void setAdjustForVariance(boolean value) {
    m_AdjustForVariance = value;
    reset();
  }

  /**
   * Returns whether to adjust for variance.
   * 
   * @return 		true if to adjust
   */
  public boolean getAdjustForVariance() {
    return m_AdjustForVariance;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String adjustForVarianceTipText() {
    return "If enabled, adjusts for variance in the data by taking the log of the target(s).";
  }

  /**
   * Sets the timestamp field.
   * 
   * @param value	the field
   */
  public void setTimeStampField(String value) {
    m_TimeStampField = value;
    reset();
  }

  /**
   * Returns the timestamp field.
   * 
   * @return 		the field
   */
  public String getTimeStampField() {
    return m_TimeStampField;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String timeStampFieldTipText() {
    return "The name of the time stamp field in the data.";
  }

  /**
   * Sets whether to add the AM indicator.
   * 
   * @param value	true if to add indicator
   */
  public void setAddAMIndicator(boolean value) {
    m_AddAMIndicator = value;
    reset();
  }

  /**
   * Returns whether to add the AM indicator.
   * 
   * @return 		true if added
   */
  public boolean getAddAMIndicator() {
    return m_AddAMIndicator;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String addAMIndicatorTipText() {
    return "If enabled, if an AM indicator attribute is to be created.";
  }

  /**
   * Sets whether to add the day of week indicator.
   * 
   * @param value	true if to add indicator
   */
  public void setAddDayOfWeek(boolean value) {
    m_AddDayOfWeek = value;
    reset();
  }

  /**
   * Returns whether to add the day of week indicator.
   * 
   * @return 		true if added
   */
  public boolean getAddDayOfWeek() {
    return m_AddDayOfWeek;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String addDayOfWeekTipText() {
    return "If enabled, a day of the week attribute is to be created.";
  }

  /**
   * Sets whether to add the day of month indicator.
   * 
   * @param value	true if to add indicator
   */
  public void setAddDayOfMonth(boolean value) {
    m_AddDayOfMonth = value;
    reset();
  }

  /**
   * Returns whether to add the day of month indicator.
   * 
   * @return 		true if added
   */
  public boolean getAddDayOfMonth() {
    return m_AddDayOfMonth;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String addDayOfMonthTipText() {
    return "If enabled, a day of the month attribute is to be created.";
  }

  /**
   * Sets whether to add the number of days in month indicator.
   * 
   * @param value	true if to add indicator
   */
  public void setAddNumDaysInMonth(boolean value) {
    m_AddNumDaysInMonth = value;
    reset();
  }

  /**
   * Returns whether to add the number of days in month indicator.
   * 
   * @return 		true if added
   */
  public boolean getAddNumDaysInMonth() {
    return m_AddNumDaysInMonth;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String addNumDaysInMonthTipText() {
    return "If enabled, a num days in the month attribute is to be created.";
  }

  /**
   * Sets whether to add the weekend indicator.
   * 
   * @param value	true if to add indicator
   */
  public void setAddWeekendIndicator(boolean value) {
    m_AddWeekendIndicator = value;
    reset();
  }

  /**
   * Returns whether to add the weekend indicator.
   * 
   * @return 		true if added
   */
  public boolean getAddWeekendIndicator() {
    return m_AddWeekendIndicator;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String addWeekendIndicatorTipText() {
    return "If enabled, a weekend indicator attribute is to be created.";
  }

  /**
   * Sets whether to add the month of year indicator.
   * 
   * @param value	true if to add indicator
   */
  public void setAddMonthOfYear(boolean value) {
    m_AddMonthOfYear = value;
    reset();
  }

  /**
   * Returns whether to add the month of year indicator.
   * 
   * @return 		true if added
   */
  public boolean getAddMonthOfYear() {
    return m_AddMonthOfYear;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String addMonthOfYearTipText() {
    return "If enabled, a month of the year attribute is to be created.";
  }

  /**
   * Sets whether to add the quarter of year indicator.
   * 
   * @param value	true if to add indicator
   */
  public void setAddQuarterOfYear(boolean value) {
    m_AddQuarterOfYear = value;
    reset();
  }

  /**
   * Returns whether to add the quarter of year indicator.
   * 
   * @return 		true if added
   */
  public boolean getAddQuarterOfYear() {
    return m_AddQuarterOfYear;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String addQuarterOfYearTipText() {
    return "If enabled, a quarter attribute is to be created.";
  }

  /**
   * Sets the entries to skip.
   * 
   * @param value	the entries
   */
  public void setSkipEntries(String value) {
    m_SkipEntries = value;
    reset();
  }

  /**
   * Returns the entries to skip.
   * 
   * @return 		the entries
   */
  public String getSkipEntries() {
    return m_SkipEntries;
  }

  /**
   * Returns the tip text for this property.
   * 
   * @return 		tip text for this property suitable for displaying in the GUI or
   *         		for listing the options.
   */
  public String skipEntriesTipText() {
    return "The list of time units to be 'skipped' - i.e. not considered as an increment.";
  }
}
