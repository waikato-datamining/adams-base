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
 * ParserHelper.java
 * Copyright (C) 2013-2021 University of Waikato, Hamilton, New Zealand
 */
package adams.parser;

import adams.core.BusinessDays;
import adams.core.DateUtils;
import adams.core.base.BaseDate;
import adams.core.base.BaseDateTime;
import adams.core.base.BaseTime;
import adams.core.classmanager.ClassManager;
import adams.core.logging.LoggingObject;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.parser.plugin.AbstractParserFunction;
import adams.parser.plugin.AbstractParserProcedure;
import adams.parser.plugin.ParserFunction;
import adams.parser.plugin.ParserProcedure;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

/**
 * Helper class for parsers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ParserHelper
  extends LoggingObject {

  /** for serialization. */
  private static final long serialVersionUID = -4265720482014655040L;

  /** the symbols. */
  protected HashMap m_Symbols;
  
  /** the calendar instance to use. */
  protected Calendar m_Calendar;

  /** the business days to use. */
  protected BusinessDays m_BusinessDays;

  /** the cache of functions. */
  protected static HashMap<String,ParserFunction> m_Functions;

  /** the cache of procedures. */
  protected static HashMap<String,ParserProcedure> m_Procedures;
  
  /**
   * Initializes the helper.
   */
  public ParserHelper() {
    super();
    initialize();
  }

  /**
   * Turns the content of the report into symbols.
   *
   * @param report	the report
   * @return		the evaluated result
   */
  public static HashMap reportToSymbols(Report report) {
    HashMap 		result;
    List<AbstractField>	fields;
    Object		obj;

    // transfer values
    result = new HashMap();
    fields = report.getFields();
    for (AbstractField field: fields) {
      try {
        switch (field.getDataType()) {
          case NUMERIC:
            obj = report.getDoubleValue(field);
            if (obj != null)
	      result.put(field.toString(), obj);
            break;
          case BOOLEAN:
            obj = report.getBooleanValue(field);
            if (obj != null)
	      result.put(field.toString(), obj);
            break;
          default:
            result.put(field.toString(), "" + report.getValue(field));
        }
      }
      catch (Exception e) {
        System.err.println(ParserHelper.class.getName() + ": Failed to retrieve field '" + field + ":");
        e.printStackTrace();
      }
    }

    return result;
  }

  /**
   * Initializes members.
   */
  protected void initialize() {
    m_Calendar     = DateUtils.getCalendar();
    m_Symbols      = new HashMap();
    m_BusinessDays = BusinessDays.MONDAY_TO_FRIDAY;
  }
  
  /**
   * Sets the symbols in use.
   * 
   * @param value	the symbols
   */
  public void setSymbols(HashMap value) {
    m_Symbols = value;
  }
  
  /**
   * Returns the currently used symbols.
   * 
   * @return		the symbols
   */
  public HashMap getSymbols() {
    return m_Symbols;
  }
  
  /**
   * Returns the calendar instance to use.
   *
   * @return 		the calendar
   */
  public Calendar getCalendar() {
    return m_Calendar;
  }

  /**
   * Returns the calendar instance to use.
   *
   * @param date 	the date to initialize the calendar with
   * @return 		the calendar
   */
  public Calendar getCalendar(Date date) {
    m_Calendar.setTime(date);
    return m_Calendar;
  }

  /**
   * Sets the type of business days to use.
   *
   * @param value	the type
   */
  public void setBusinessDays(BusinessDays value) {
    m_BusinessDays = value;
  }

  /**
   * Returns the type of business days to use.
   *
   * @return		the type
   */
  public BusinessDays getBusinessDays() {
    return m_BusinessDays;
  }

  /**
   * Corrects the amount using the factor and the note in the amount.
   *
   * @param t 		the amount to correct
   * @return		the (potentially) corrected amount
   */
  protected TimeAmount adjustAmount(Date d, Double n, TimeAmount t) {
    double	days;
    int		actualDays;
    int		inc;
    Calendar	cal;


    switch (t.getNote()) {
      case NONE:
        return new TimeAmount(t.getType(), (int) (n * t.getAmount()));

      case BUSINESS_DAYS:
        days       = n.intValue();
        actualDays = 0;
        inc        = (days < 0) ? -1 : +1;
        cal        = new GregorianCalendar();
        cal.setTime(d);
	if (days != 0) {
	  // are we on a weekend? find next businessday
	  while (!m_BusinessDays.isBusinessDay(cal.getTime())) {
	    actualDays += inc;
	    cal.add(Calendar.HOUR, 24 * inc);
	  }

	  // we moved 1 day to far, so decrement days to iterate
	  if (actualDays != 0)
	    days -= inc;

	  // iterate
	  if (days != 0) {
	    do {
	      if (m_BusinessDays.isBusinessDay(cal.getTime()))
		  days -= inc;
	      actualDays += inc;
	      cal.add(Calendar.HOUR, 24 * inc);
	    }
	    while (days != 0);
	  }

	  // moved to last day, check whether businessday!
	  while (!m_BusinessDays.isBusinessDay(cal.getTime())) {
	    actualDays += inc;
	    cal.add(Calendar.HOUR, 24 * inc);
	  }
	}
        return new TimeAmount(Calendar.HOUR, actualDays * 24);

      default:
        throw new IllegalStateException("Unhandled note: " + t.getNote());
    }
  }

  /**
   * Adds the request amount of time.
   *
   * @param d		the current date
   * @param n		the factor
   * @param t 		the time amount to use
   * @return		the new date
   */
  public Date add(Date d, Double n, TimeAmount t) {
    t = adjustAmount(d, n, t);
    getCalendar().setTime(d);
    getCalendar().add(t.getType(), t.getAmount());
    return getCalendar().getTime();
  }

  /**
   * Returns the specified date from the object (if it is a String or Date object).
   *
   * @param obj 	the object to extract the date field from
   * @param field 	the date field (see constants of Calendar class)
   * @return 		the value or NaN if failed to convert
   */
  public Double getDateFieldFromString(Object obj, int field) {
    Double result;
    Date date;
    
    result = Double.NaN;
    
    date = null;
    if (obj instanceof String)
      date = toDate((String) obj);
    else if (obj instanceof Date)
      date = (Date) obj;
    
    if (date != null)
      result = (double) getCalendar(date).get(field);
      
    return result;
  }

  /**
   * Checks whether the string/date represents a business day.
   *
   * @param obj		the string/date to check
   * @return		true if business day
   * @see		#getBusinessDays()
   */
  public boolean isBusinessDay(Object obj) {
    Date	d;

    d = null;
    if(obj instanceof Date)
      d = (Date) obj;
    else if (obj instanceof String)
      d = toDate("" + obj);

    return (d != null) && m_BusinessDays.isBusinessDay(d);
  }

  /**
   * Turns the object into a double.
   *
   * @param obj 	the object to convert
   * @return 		the numeric value, NaN if not a number
   */
  public Double toNumber(Object obj) {
    if (obj instanceof Number)
      return ((Number) obj).doubleValue();
    else
      return Double.NaN;
  }

  /**
   * Turns the object into a double.
   *
   * @param obj 	the object to convert
   * @return 		the numeric value, NaN if not a double
   */
  public Double toDouble(Object obj) {
    if (obj instanceof Number)
      return ((Number) obj).doubleValue();
    else
      return Double.NaN;
  }

  /**
   * Turns the object (ie double) into an integer.
   *
   * @param obj 	the object to convert
   * @return 		the numeric value, MAX_VALUE if not a double
   */
  public Integer toInteger(Object obj) {
    if (obj instanceof Number)
      return ((Number) obj).intValue();
    else
      return Integer.MAX_VALUE;
  }

  /**
   * Turns the object into a boolean.
   *
   * @param obj 	the object to convert
   * @return 		the boolean, false if not a boolean
   */
  public Boolean toBoolean(Object obj) {
    if (obj instanceof Boolean)
      return (Boolean) obj;
    else
      return false;
  }

  /**
   * Turns the object into a string.
   *
   * @param obj 	the object to convert
   * @return 		the string representation
   */
  public String toString(Object obj) {
    if (obj instanceof String)
      return (String) obj;
    else
      return obj.toString();
  }

  /**
   * Turns the string into a Date object.
   *
   * @param s 		the string to convert
   * @return 		the Date object, null if failed to convert
   */
  public Date toDate(String s) {
    Date result;
    int posDash;
    int posColon;
    BaseDateTime bdt;
    BaseDate bd;
    BaseTime bt;
    
    result   = null;
    posDash  = s.indexOf("-");
    posColon = s.indexOf(":");
    // date/time
    if ((posDash > -1) && (posColon > -1)) {
      bdt = new BaseDateTime();
      if (bdt.isValid(s)) {
        bdt.setValue(s);
        result = bdt.dateValue();
      }
    }
    // date
    else if (posDash > -1) {
      bd = new BaseDate();
      if (bd.isValid(s)) {
        bd.setValue(s);
        result = bd.dateValue();
      }
    }
    // time
    else if (posColon > -1) {
      bt = new BaseTime();
      if (bt.isValid(s)) {
        bt.setValue(s);
        result = bt.dateValue();
      }
    }
    
    return result;
  }

  /**
   * Compares two objects.
   *
   * @param o1 		the first object
   * @param o2 		the second object
   * @return 		the comparison, NaN if failed to compare
   */
  public Double compare(Object o1, Object o2) {
    if ((o1 instanceof Number) && (o2 instanceof Number))
      return (double) Double.compare(((Number) o1).doubleValue(), ((Number) o2).doubleValue());
    else if ((o1 instanceof Comparable) && (o2 instanceof Comparable) && (o1.getClass().equals(o2.getClass())))
      return (double) (((Comparable) o1).compareTo((Comparable) o2));
    else
      return Double.NaN;
  }
  
  /**
   * Substitutes a occurrences of a string with a replacement string.
   * 
   * @param str		the string to process
   * @param find	the string to replace
   * @param replace	the replacement string
   */
  public String substitute(String str, String find, String replace) {
    return substitute(str, find, replace, -1);
  }
  
  /**
   * Generates a string made up of multiple copies of a string.
   * 
   * @param str		the string to copy multiple times
   * @param num		the number of times to copy
   * @return		the generated string
   */
  public String repeat(String str, int num) {
    StringBuilder 	result;
    int			i;
    
    result = new StringBuilder();
    for (i = 0; i < num; i++)
      result.append(str);
    
    return result.toString(); 
  }
  
  /**
   * Replaces a sub-string at a specified location with a new string.
   * 
   * @param str		the string to update
   * @param pos		the starting position (0-based)
   * @param len		the length of the substring to replace
   * @param newStr	the new String to insert
   * @return		the updated string
   */
  public String replace(String str, int pos, int len, String newStr) {
    StringBuilder 	result;
    
    result = new StringBuilder();
    if (pos > 0)
      result.append(str.substring(0, pos));
    result.append(newStr);
    result.append(str.substring(pos + len));
    
    return result.toString(); 
  }
  
  /**
   * Substitutes a occurrences of a string with a replacement string.
   * 
   * @param str		the string to process
   * @param find	the string to replace
   * @param replace	the replacement string
   * @param occurrences	the maximum number of occurrences, use <= 0 to ignore
   */
  public String substitute(String str, String find, String replace, int occurrences) {
    StringBuilder 	result;
    int 		pos;
    int 		count;
    
    result = new StringBuilder();
    count  = 0;
    while (str.length() > 0) {
      pos = str.indexOf(find);
      if (pos > -1) {
	count++;
	result.append(str.substring(0, pos));
	result.append(replace);
	str = str.substring(pos + find.length());
	if (count == occurrences) {
	  result.append(str);
	  break;
	}
      }
      else {
	result.append(str);
	break;
      }
    }
    return result.toString(); 
  }
  
  /**
   * Returns the specified number of characters from the left of the string.
   * If string is shorter than specified length, all of the string is returned.
   * 
   * @param str		the source string
   * @param len		the number of characters
   * @return		the substring
   */
  public String left(String str, int len) {
    if (str.length() >= len)
      return str.substring(0, len);
    else
      return str;
  }
  
  /**
   * Returns the specified substring from the string.
   * If the start position is greater than the string's length itself, then
   * an empty string is returned. If (pos+len) exceeds the string's length,
   * then only the available substring is returned.
   * 
   * @param str		the source string
   * @param pos		the starting position (0-based)
   * @param len		the number of characters to copy
   * @return		the substring
   */
  public String mid(String str, int pos, int len) {
    if (str.length() > pos) {
      if (str.length() >= pos + len)
	return str.substring(pos, pos + len);
      else
	return str.substring(pos);
    }
    else {
      return "";
    }
  }
  
  /**
   * Returns the specified number of characters from the right of the string.
   * If string is shorter than specified length, all of the string is returned.
   * 
   * @param str		the source string
   * @param len		the number of characters
   * @return		the substring
   */
  public String right(String str, int len) {
    if (str.length() >= len)
      return str.substring(str.length() - len, str.length()); 
    else
      return str;
  }

  /**
   * Initializes the additional functions.
   */
  protected static synchronized void initFunctions() {
    HashMap<String,ParserFunction>	map;
    String[]				cnames;
    ParserFunction			function;
    
    if (m_Functions == null) {
      map    = new HashMap<>();
      cnames = AbstractParserFunction.getFunctions();
      for (String cname: cnames) {
	try {
	  function = (ParserFunction) ClassManager.getSingleton().forName(cname).getDeclaredConstructor().newInstance();
	  map.put(function.getFunctionName(), function);
	}
	catch (Exception e) {
	  System.err.println("Failed to instantiate parser function: " + cname);
	  e.printStackTrace();
	}
      }
      m_Functions = map;
    }
  }

  /**
   * Returns the function associated with the given function name.
   * 
   * @param name	the name of the function
   * @return		the instance of the function
   * @throws IllegalArgumentException	if the function name is not available
   */
  protected static synchronized ParserFunction getFunction(String name) {
    initFunctions();

    if (!m_Functions.containsKey(name))
      throw new IllegalArgumentException("Function '" + name + "' not available!");
    
    return m_Functions.get(name);
  }
  
  /**
   * Builds a string of all the additionally available functions.
   * 
   * @return		the generated overview
   */
  public static String getFunctionOverview() {
    StringBuilder	result;
    List<String>	names;
    
    initFunctions();
    
    result = new StringBuilder();
    names  = new ArrayList<>(m_Functions.keySet());
    Collections.sort(names);
    for (String name: names) {
      if (result.length() > 0)
	result.append("\n");
      result.append("- " + getFunction(name).getFunctionHelp());
    }
    
    return result.toString();
  }
  
  /**
   * Call function.
   * 
   * @param name 	the name of the function
   * @param params 	the parameters of the function
   * @return 		the result of the function call
   */
  public Object callFunction(String name, Object[] params) {
    return getFunction(name).callFunction(params);
  }

  /**
   * Initializes the additional procedures.
   */
  protected static synchronized void initProcedures() {
    HashMap<String,ParserProcedure>	map;
    String[]				cnames;
    ParserProcedure			procedure;
    
    if (m_Procedures == null) {
      map    = new HashMap<>();
      cnames = AbstractParserProcedure.getProcedures();
      for (String cname: cnames) {
	try {
	  procedure = (ParserProcedure) ClassManager.getSingleton().forName(cname).getDeclaredConstructor().newInstance();
	  map.put(procedure.getProcedureName(), procedure);
	}
	catch (Exception e) {
	  System.err.println("Failed to instantiate parser procedure: " + cname);
	  e.printStackTrace();
	}
      }
      m_Procedures = map;
    }
  }

  /**
   * Returns the procedure associated with the given procedure name.
   * 
   * @param name	the name of the procedure
   * @return		the instance of the procedure
   * @throws IllegalArgumentException	if the procedure name is not available
   */
  protected static synchronized ParserProcedure getProcedure(String name) {
    initProcedures();

    if (!m_Procedures.containsKey(name))
      throw new IllegalArgumentException("Procedure '" + name + "' not available!");
    
    return m_Procedures.get(name);
  }
  
  /**
   * Builds a string of all the additionally available procedures.
   * 
   * @return		the generated overview
   */
  public static String getProcedureOverview() {
    StringBuilder	result;
    List<String>	names;
    
    initProcedures();
    
    result = new StringBuilder();
    names  = new ArrayList<>(m_Procedures.keySet());
    Collections.sort(names);
    for (String name: names) {
      if (result.length() > 0)
	result.append("\n");
      result.append("- " + getProcedure(name).getProcedureHelp());
    }
    
    return result.toString();
  }

  /**
   * Call procedure.
   * 
   * @param name 	the name of the procedure
   * @param params 	the parameters of the prodcure
   */
  public void callProcedure(String name, Object[] params) {
    getProcedure(name).callProcedure(params);
  }
}
