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
 * Utils.java
 * Copyright (C) 2008-2017 University of Waikato, Hamilton, New Zealand
 * Copyright (C) 2006 Dr. Herong Yang, http://www.herongyang.com/
 * Copyright (C) 2008 Dave L., stackoverflow
 */

package adams.core;

import adams.core.annotation.MixedCopyright;
import adams.core.base.BaseBoolean;
import adams.core.base.BaseByte;
import adams.core.base.BaseCharacter;
import adams.core.base.BaseDouble;
import adams.core.base.BaseFloat;
import adams.core.base.BaseInteger;
import adams.core.base.BaseLong;
import adams.core.base.BaseObject;
import adams.core.base.BaseShort;
import adams.core.base.BaseString;
import adams.core.logging.LoggingObject;
import adams.core.logging.LoggingSupporter;
import adams.core.management.LocaleHelper;
import gnu.trove.list.array.TByteArrayList;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

/**
 * Class implementing some simple utility methods.
 *
 * @author Eibe Frank
 * @author Yong Wang
 * @author Len Trigg
 * @author Julien Prados
 * @author FracPete (fracpete at waikat dot ac dot nz)
 * @author Herong Yang
 * @version $Revision$
 * @see weka.core.Utils
 */
public class Utils {

  /** hexadecimal digits. */
  public static final char HEX_DIGIT[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

  /** the indicator for arrays. */
  public static final String ARRAY_INDICATOR = "[]";
  
  /**
   * Rounds a double and converts it into String. Always displays the
   * specified number of decimals. Uses the default locale.
   *
   * @param value 		the double value
   * @param afterDecimalPoint 	the number of digits permitted
   * 				after the decimal point; if -1 then all
   * 				decimals are displayed; also if number > Long.MAX_VALUE
   * @return 			the double as a formatted string
   */
  public static String doubleToStringFixed(double value, int afterDecimalPoint) {
    return doubleToStringFixed(value, afterDecimalPoint, LocaleHelper.getSingleton().getDefault());
  }

  /**
   * Rounds a double and converts it into String. Always displays the
   * specified number of decimals.
   *
   * @param value 		the double value
   * @param afterDecimalPoint 	the number of digits permitted
   * 				after the decimal point; if -1 then all
   * 				decimals are displayed; also if number > Long.MAX_VALUE
   * @param locale		the locale to use
   * @return 			the double as a formatted string
   */
  public static String doubleToStringFixed(double value, int afterDecimalPoint, Locale locale) {
    StringBuilder	result;
    double		valueNew;
    double		factor;
    StringBuilder	remainder;
    char		separator;
    boolean		negative;

    if (Double.isNaN(value) || Double.isInfinite(value))
      return Double.toString(value);

    if (    (afterDecimalPoint < 0)
	 || (value > Long.MAX_VALUE)
	 || (value < Long.MIN_VALUE) ) {
      result = new StringBuilder(LocaleHelper.getSingleton().getNumberFormat(locale).format(value));
    }
    else {
      negative  = (value < 0);
      if (negative)
	value *= -1.0;
      separator = LocaleHelper.getSingleton().getDecimalSeparator(locale);
      factor    = Math.pow(10, afterDecimalPoint);
      valueNew  = Math.floor(value * factor) / factor;
      result    = new StringBuilder(Long.toString(Math.round(Math.floor(valueNew))));
      remainder = new StringBuilder("" + (long) Math.round((valueNew - Math.floor(valueNew)) * Math.pow(10, afterDecimalPoint)));
      remainder.delete(0, remainder.indexOf("" + separator) + 1);
      if (afterDecimalPoint > 0) {
	while (remainder.length() < afterDecimalPoint)
	  remainder.insert(0, '0');
	result.append(separator);
	result.append(remainder.substring(0, afterDecimalPoint));
      }
      if (negative && (valueNew != 0.0))
	result.insert(0, "-");
    }

    return result.toString();
  }

  /**
   * Rounds a double and converts it into String. Uses the default locale.
   *
   * @param value 		the double value
   * @param afterDecimalPoint 	the (maximum) number of digits permitted
   * 				after the decimal point
   * @return 			the double as a formatted string
   */
  public static String doubleToString(double value, int afterDecimalPoint) {
    return doubleToString(value, afterDecimalPoint, LocaleHelper.getSingleton().getDefault());
  }

  /**
   * Rounds a double and converts it into String.
   *
   * @param value 		the double value
   * @param afterDecimalPoint 	the (maximum) number of digits permitted
   * 				after the decimal point
   * @param locale		the locale to use
   * @return 			the double as a formatted string
   */
  public static String doubleToString(double value, int afterDecimalPoint, Locale locale) {
    StringBuilder 	builder;
    double 		temp;
    int 		dotPosition;
    int 		currentPos;
    long 		precisionValue;
    char		separator;

    temp = value * Math.pow(10.0, afterDecimalPoint);
    if (Math.abs(temp) < Long.MAX_VALUE) {
      precisionValue = 	(temp > 0) ? (long)(temp + 0.5)
	  : -(long)(Math.abs(temp) + 0.5);
      if (precisionValue == 0)
	builder = new StringBuilder(String.valueOf(0));
      else
	builder = new StringBuilder(String.valueOf(precisionValue));

      if (afterDecimalPoint == 0)
	return builder.toString();

      separator   = LocaleHelper.getSingleton().getDecimalSeparator(locale);
      dotPosition = builder.length() - afterDecimalPoint;
      while (((precisionValue < 0) && (dotPosition < 1)) || (dotPosition < 0)) {
	if (precisionValue < 0)
	  builder.insert(1, '0');
	else
	  builder.insert(0, '0');
	dotPosition++;
      }

      builder.insert(dotPosition, separator);

      if ((precisionValue < 0) && (builder.charAt(1) == separator))
	builder.insert(1, '0');
      else if (builder.charAt(0) == separator)
	builder.insert(0, '0');

      currentPos = builder.length() - 1;
      while ((currentPos > dotPosition) && (builder.charAt(currentPos) == '0'))
	builder.setCharAt(currentPos--, ' ');

      if (builder.charAt(currentPos) == separator)
	builder.setCharAt(currentPos, ' ');

      return builder.toString().trim();
    }
    return new String("" + value);
  }

  /**
   * Rounds a double and converts it into a formatted decimal-justified String.
   * Trailing 0's are replaced with spaces. Uses the default locale.
   *
   * @param value 		the double value
   * @param width 		the width of the string
   * @param afterDecimalPoint 	the number of digits after the decimal point
   * @return 			the double as a formatted string
   */
  public static String doubleToString(double value, int width, int afterDecimalPoint) {
    return doubleToString(value, width, afterDecimalPoint, LocaleHelper.getSingleton().getDefault());
  }

  /**
   * Rounds a double and converts it into a formatted decimal-justified String.
   * Trailing 0's are replaced with spaces.
   *
   * @param value 		the double value
   * @param width 		the width of the string
   * @param afterDecimalPoint 	the number of digits after the decimal point
   * @param locale		the locale to use
   * @return 			the double as a formatted string
   */
  public static String doubleToString(double value, int width, int afterDecimalPoint, Locale locale) {
    String 	tempString;
    char[] 	result;
    int 	dotPosition;
    int		i;
    int 	offset;
    char	separator;

    tempString = doubleToString(value, afterDecimalPoint, locale);
    if ((afterDecimalPoint >= width)
	|| (tempString.indexOf('E') != -1)) { // Protects sci notation
      return tempString;
    }

    // Initialize result
    result = new char[width];
    for (i = 0; i < result.length; i++)
      result[i] = ' ';

    if (afterDecimalPoint > 0) {
      separator   = LocaleHelper.getSingleton().getDecimalSeparator(locale);
      // Get position of decimal point and insert decimal point
      dotPosition = tempString.indexOf(separator);
      if (dotPosition == -1)
	dotPosition = tempString.length();
      else
	result[width - afterDecimalPoint - 1] = separator;
    }
    else {
      dotPosition = tempString.length();
    }

    offset = width - afterDecimalPoint - dotPosition;
    if (afterDecimalPoint > 0)
      offset--;

    // Not enough room to decimal align within the supplied width
    if (offset < 0)
      return tempString;

    // Copy characters before decimal point
    for (i = 0; i < dotPosition; i++)
      result[offset + i] = tempString.charAt(i);

    // Copy characters after decimal point
    for (i = dotPosition + 1; i < tempString.length(); i++)
      result[offset + i] = tempString.charAt(i);

    return new String(result);
  }

  /**
   * Returns the basic class of an array class (handles multi-dimensional
   * arrays).
   * @param c        the array to inspect
   * @return         the class of the innermost elements
   */
  public static Class getArrayClass(Class c) {
    if (c.getComponentType().isArray())
      return getArrayClass(c.getComponentType());
    else
      return c.getComponentType();
  }

  /**
   * Returns the dimensions of the given array. Even though the
   * parameter is of type "Object" one can hand over primitve arrays, e.g.
   * int[3] or double[2][4].
   *
   * @param array       the array to determine the dimensions for
   * @return            the dimensions of the array
   */
  public static int getArrayDimensions(Class array) {
    if (array.getComponentType().isArray())
      return 1 + getArrayDimensions(array.getComponentType());
    else
      return 1;
  }

  /**
   * Returns the dimensions of the given array. Even though the
   * parameter is of type "Object" one can hand over primitve arrays, e.g.
   * int[3] or double[2][4].
   *
   * @param array       the array to determine the dimensions for
   * @return            the dimensions of the array
   */
  public static int getArrayDimensions(Object array) {
    return getArrayDimensions(array.getClass());
  }

  /**
   * Returns the given Array in a string representation. Even though the
   * parameter is of type "Object" one can hand over primitve arrays, e.g.
   * int[3] or double[2][4].
   *
   * @param array       the array to return in a string representation
   * @param outputClass	whether to output the class name instead of calling
   * 			the object's "toString()" method
   * @return            the array as string
   */
  public static String arrayToString(Object array, boolean outputClass) {
    StringBuilder	result;
    int			dimensions;
    int			i;
    Object		obj;

    result     = new StringBuilder();
    dimensions = getArrayDimensions(array);

    if (dimensions == 0) {
      result.append("null");
    }
    else if (dimensions == 1) {
      for (i = 0; i < Array.getLength(array); i++) {
	if (i > 0)
	  result.append(",");
	if (Array.get(array, i) == null) {
	  result.append("null");
	}
	else {
	  obj = Array.get(array, i);
	  if (outputClass) {
	    if (obj instanceof Class)
	      result.append(((Class) obj).getName());
	    else
	      result.append(obj.getClass().getName());
	  }
	  else {
	    result.append(obj.toString());
	  }
	}
      }
    }
    else {
      for (i = 0; i < Array.getLength(array); i++) {
	if (i > 0)
	  result.append(",");
	result.append("[" + arrayToString(Array.get(array, i)) + "]");
      }
    }

    return result.toString();
  }

  /**
   * Returns the given Array in a string representation. Even though the
   * parameter is of type "Object" one can hand over primitve arrays, e.g.
   * int[3] or double[2][4].
   *
   * @param array       the array to return in a string representation
   * @return            the array as string
   */
  public static String arrayToString(Object array) {
    return arrayToString(array, false);
  }

  /**
   * Converts specified characters into the string equivalents.
   *
   * @param string 	the string
   * @param find	the characters to replace
   * @param replace	the replacement strings for the characters
   * @return 		the converted string
   * @see		#unbackQuoteChars(String, String[], char[])
   */
  public static String backQuoteChars(String string, char[] find, String[] replace) {
    int 		index;
    StringBuilder 	newStr;
    int			i;

    if (string == null)
      return null;
    
    for (i = 0; i < find.length; i++) {
      if (string.indexOf(find[i]) > -1 ) {
	newStr = new StringBuilder();
	while ((index = string.indexOf(find[i])) > -1) {
	  if (index > 0)
	    newStr.append(string.substring(0, index));
	  newStr.append(replace[i]);
	  if ((index + 1) < string.length())
	    string = string.substring(index + 1);
	  else
	    string = "";
	}
	newStr.append(string);
	string = newStr.toString();
      }
    }

    return string;
  }

  /**
   * Converts carriage returns and new lines in a string into \r and \n.
   * Backquotes the following characters: ` " \ \t and %
   *
   * @param string 	the string
   * @return 		the converted string
   * @see		#unbackQuoteChars(String)
   */
  public static String backQuoteChars(String string) {
    return backQuoteChars(
	string,
	new char[]  {'\\',   '\'',  '\t',  '\n',  '\r',  '"'},
	new String[]{"\\\\", "\\'", "\\t", "\\n", "\\r", "\\\""});
  }

  /**
   * The inverse operation of backQuoteChars().
   * Converts the specified strings into their character representations.
   *
   * @param string 	the string
   * @param find	the string to find
   * @param replace	the character equivalents of the strings
   * @return 		the converted string
   * @see		#backQuoteChars(String, char[], String[])
   */
  public static String unbackQuoteChars(String string, String[] find, char[] replace) {
    int 		index;
    StringBuilder 	newStr;
    int[] 		pos;
    int			curPos;
    String 		str;
    int			i;

    if (string == null)
      return null;
    
    pos = new int[find.length];

    str = new String(string);
    newStr = new StringBuilder();
    while (str.length() > 0) {
      // get positions and closest character to replace
      curPos = str.length();
      index  = -1;
      for (i = 0; i < pos.length; i++) {
	pos[i] = str.indexOf(find[i]);
	if ( (pos[i] > -1) && (pos[i] < curPos) ) {
	  index  = i;
	  curPos = pos[i];
	}
      }

      // replace character if found, otherwise finished
      if (index == -1) {
	newStr.append(str);
	str = "";
      }
      else {
	newStr.append(str.substring(0, pos[index]));
	newStr.append(replace[index]);
	str = str.substring(pos[index] + find[index].length());
      }
    }

    return newStr.toString();
  }

  /**
   * The inverse operation of backQuoteChars().
   * Converts back-quoted carriage returns and new lines in a string
   * to the corresponding character ('\r' and '\n').
   * Also "un"-back-quotes the following characters: ` " \ \t and %
   *
   * @param string 	the string
   * @return 		the converted string
   * @see		#backQuoteChars(String)
   */
  public static String unbackQuoteChars(String string) {
    return unbackQuoteChars(
      string,
      new String[]{"\\\\", "\\'", "\\t", "\\n", "\\r", "\\\""},
      new char[]{'\\', '\'', '\t', '\n', '\r', '"'});
  }

  /**
   * Quotes a string if it contains special characters.
   *
   * The following rules are applied:
   *
   * A character is backquoted version of it is one
   * of <tt>" ' % \ \n \r \t</tt>.
   *
   * A string is enclosed within double quotes if a character has been
   * backquoted using the previous rule above or contains
   * <tt>{ }</tt> or is exactly equal to the strings
   * <tt>, ? space or ""</tt> (empty string).
   *
   * A quoted question mark distinguishes it from the missing value which
   * is represented as an unquoted question mark in arff files.
   *
   * @param string 	the string to be quoted
   * @return 		the string (possibly quoted)
   * @see		#unDoubleQuote(String)
   */
  public static String doubleQuote(String string) {
    return quote(string, "\"");
  }

  /**
   * Quotes a string if it contains special characters.
   *
   * The following rules are applied:
   *
   * A character is backquoted version of it is one
   * of <tt>" ' % \ \n \r \t</tt>.
   *
   * A string is enclosed within single quotes if a character has been
   * backquoted using the previous rule above or contains
   * <tt>{ }</tt> or is exactly equal to the strings
   * <tt>, ? space or ""</tt> (empty string).
   *
   * A quoted question mark distinguishes it from the missing value which
   * is represented as an unquoted question mark in arff files.
   *
   * @param string 	the string to be quoted
   * @return 		the string (possibly quoted)
   * @see		#unquote(String)
   */
  public static String quote(String string) {
    return quote(string, "'");
  }

  /**
   * Quotes a string if it contains special characters.
   *
   * The following rules are applied:
   *
   * A character is backquoted version of it is one
   * of <tt>" ' % \ \n \r \t</tt>.
   *
   * A string is enclosed within the quote character if a character has been
   * backquoted using the previous rule above or contains
   * <tt>{ }</tt> or is exactly equal to the strings
   * <tt>, ? space or ""</tt> (empty string).
   *
   * A quoted question mark distinguishes it from the missing value which
   * is represented as an unquoted question mark in arff files.
   *
   * @param string 	the string to be quoted
   * @param quoteChar	the quote character to use
   * @return 		the string (possibly quoted)
   * @see		#unquote(String,String)
   */
  public static String quote(String string, String quoteChar) {
      if (string == null)
	return string;
    
      boolean quote = false;

      // backquote the following characters
      if ((string.indexOf('\n') != -1) || (string.indexOf('\r') != -1) ||
	  (string.indexOf('\'') != -1) || (string.indexOf('"')  != -1) ||
	  (string.indexOf('\\') != -1) || (string.indexOf('\t') != -1)) {
	  string = backQuoteChars(string);
	  quote = true;
      }

      // Enclose the string in quotes if the string contains a recently added
      // backquote or contains one of the following characters.
      if((quote == true) ||
	 (string.indexOf('{') != -1) || (string.indexOf('}') != -1) ||
	 (string.indexOf(',') != -1) || (string.equals("?")) ||
	 (string.indexOf(' ') != -1) || (string.equals(""))) {
	  string = (new String(quoteChar).concat(string)).concat(new String(quoteChar));
      }

      return string;
  }

  /**
   * unquotes are previously quoted string (but only if necessary), i.e., it
   * removes the double quotes around it. Inverse to doubleQuote(String).
   *
   * @param string	the string to process
   * @return		the unquoted string
   * @see		#doubleQuote(String)
   */
  public static String unDoubleQuote(String string) {
    return unquote(string, "\"");
  }

  /**
   * unquotes are previously quoted string (but only if necessary), i.e., it
   * removes the single quotes around it. Inverse to quote(String).
   *
   * @param string	the string to process
   * @return		the unquoted string
   * @see		#quote(String)
   */
  public static String unquote(String string) {
    return unquote(string, "'");
  }

  /**
   * unquotes are previously quoted string (but only if necessary), i.e., it
   * removes the quote characters around it. Inverse to quote(String,String).
   *
   * @param string	the string to process
   * @param quoteChar	the quote character to use
   * @return		the unquoted string
   * @see		#quote(String,String)
   */
  public static String unquote(String string, String quoteChar) {
    if ((string == null) || (string.length() < 2))
      return string;
    
    if (string.startsWith(quoteChar) && string.endsWith(quoteChar)) {
      string = string.substring(1, string.length() - 1);

      if ((string.indexOf("\\n")  != -1) || (string.indexOf("\\r")  != -1) ||
	  (string.indexOf("\\'")  != -1) || (string.indexOf("\\\"") != -1) ||
	  (string.indexOf("\\\\") != -1) || (string.indexOf("\\t")  != -1)) {
	string = unbackQuoteChars(string);
      }
    }

    return string;
  }

  /**
   * Surrounds the strings with the specified quotes. If the quote character
   * is part of the string itself, it gets doubled up. Tab characters get
   * escaped as well.
   * 
   * @param s		the string to quote
   * @param quote	the quote character, e.g., ' or "
   * @return		the quoted character
   */
  public static String doubleUpQuotes(String s, char quote, char[] chars, String[] strings) {
    char[]	newChars;
    String[]	newStrings;
    
    if (chars.length != strings.length)
      throw new IllegalArgumentException(
	  "No the same number of chars and strings provided: " 
	      + chars.length + " != " + strings.length);
    
    newChars = new char[chars.length + 1];
    newChars[0] = quote;
    System.arraycopy(chars, 0, newChars, 1, chars.length);
    newStrings = new String[strings.length + 1];
    newStrings[0] = "" + quote + quote;
    System.arraycopy(strings, 0, newStrings, 1, strings.length);
    
    return quote + Utils.backQuoteChars(s, newChars, newStrings) + quote;
  }

  /**
   * Surrounds the strings with the specified quotes. If the quote character
   * is part of the string itself, it gets doubled up. Tab characters get
   * escaped as well.
   * 
   * @param s		the string to quote
   * @param quote	the quote character, e.g., ' or "
   * @return		the quoted character
   */
  public static String unDoubleUpQuotes(String s, char quote, String[] strings, char[] chars) {
    char[]	newChars;
    String[]	newStrings;

    if (chars.length != strings.length)
      throw new IllegalArgumentException(
	  "No the same number of strings and chars provided: " 
	      + strings.length + " != " + chars.length);
    
    if (s.startsWith("" + quote) && s.endsWith("" + quote)) {
      newChars = new char[chars.length + 1];
      newChars[0] = quote;
      System.arraycopy(chars, 0, newChars, 1, chars.length);
      newStrings = new String[strings.length + 1];
      newStrings[0] = "" + quote + quote;
      System.arraycopy(strings, 0, newStrings, 1, strings.length);

      return Utils.unbackQuoteChars(s.substring(1, s.length() - 1), newStrings, newChars);
    }
    else {
      return s;
    }
  }

  /**
   * Creates a deep copy of the given object (must be serializable!). Returns
   * null in case of an error.
   *
   * @param o		the object to copy
   * @return		the deep copy
   */
  public static Object deepCopy(Object o) {
    return deepCopy(o, false);
  }

  /**
   * Creates a deep copy of the given object (must be serializable!). Returns
   * null in case of an error.
   *
   * @param o		the object to copy
   * @param silent	whether to suppress error output
   * @return		the deep copy
   */
  public static Object deepCopy(Object o, boolean silent) {
    Object		result;
    SerializedObject	so;

    try {
      so     = new SerializedObject((Serializable) o);
      result = so.getObject();
    }
    catch (Exception e) {
      if (!silent) {
        System.err.println("Failed to serialize " + o.getClass().getName() + ":");
        e.printStackTrace();
      }
      result = null;
    }

    return result;
  }

  /**
   * Creates a new instance of the class represented by this object.
   *
   * @param o		the object to create a new instance for
   * @return		the new instance, or null in case of an error
   */
  public static Object newInstance(Object o) {
    Object	result;

    try {
      result = o.getClass().newInstance();
    }
    catch (Exception e) {
      System.err.println("Error creating new instance for " + o.getClass().getName() + ":");
      e.printStackTrace();
      result = null;
    }

    return result;
  }

  /**
   * Breaks up the string, if wider than "columns" characters.
   *
   * @param s		the string to process
   * @param columns	the width in columns
   * @return		the processed string
   */
  public static String[] breakUp(String s, int columns) {
    List<String>	result;
    String		line;
    BreakIterator	boundary;
    int			boundaryStart;
    int			boundaryEnd;
    String		word;
    String		punctuation;
    int			i;
    String[]		lines;

    result      = new ArrayList<String>();
    punctuation = " .,;:!?'\"";
    lines       = s.split("\n");

    for (i = 0; i < lines.length; i++) {
      boundary      = BreakIterator.getWordInstance();
      boundary.setText(lines[i]);
      boundaryStart = boundary.first();
      boundaryEnd   = boundary.next();
      line          = "";

      while (boundaryEnd != BreakIterator.DONE) {
	word = lines[i].substring(boundaryStart, boundaryEnd);
	if (line.length() >= columns) {
	  if (word.length() == 1) {
	    if (punctuation.indexOf(word.charAt(0)) > -1) {
	      line += word;
	      word = "";
	    }
	  }
	  result.add(line);
	  line = "";
	}
	line          += word;
	boundaryStart  = boundaryEnd;
	boundaryEnd    = boundary.next();
      }
      if (line.length() > 0)
	result.add(line);
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Inserts line breaks into the string, if wider than "columns" characters.
   *
   * @param s		the string to process
   * @param columns	the width in columns
   * @return		the processed string
   */
  public static String insertLineBreaks(String s, int columns) {
    StringBuilder	result;
    String[]		lines;
    int			i;

    result = new StringBuilder();

    lines = breakUp(s, columns);
    for (i = 0; i < lines.length; i++) {
      if (i > 0)
	result.append("\n");
      result.append(lines[i]);
    }

    return result.toString();
  }

  /**
   * Inserts comment characters at the start of each line.
   *
   * @param s		the string to process
   * @param comment	the comment string
   * @return		the processed string
   */
  public static String commentOut(String s, String comment) {
    return indent(s, comment);
  }

  /**
   * Inserts blanks at the start of each line.
   *
   * @param s		the string to process
   * @param numBlanks	the number of blanks to insert
   * @return		the processed string
   */
  public static String indent(String s, int numBlanks) {
    StringBuilder	indent;
    int			i;

    indent = new StringBuilder();
    for (i = 0; i < numBlanks; i++)
      indent.append(" ");

    return indent(s, indent.toString());
  }

  /**
   * Indents each line with the specified string.
   *
   * @param s		the string to process
   * @param indentStr	the string to use for indentation
   * @return		the processed string
   */
  protected static String indent(String s, String indentStr) {
    StringBuilder	result;
    String[]		lines;
    int			i;

    result = new StringBuilder();

    lines = s.split("\n");
    for (i = 0; i < lines.length; i++) {
      result.append(indentStr);
      result.append(lines[i]);
      result.append("\n");
    }

    return result.toString();
  }

  /**
   * Removes the comment characters from the start of each line.
   *
   * @param s		the string to process
   * @param comment	the comment string
   * @return		the processed string
   */
  public static String unComment(String s, String comment) {
    StringBuilder	result;
    String[]		lines;
    int			i;

    result = new StringBuilder();

    lines = s.split("\n");
    for (i = 0; i < lines.length; i++) {
      if (lines[i].startsWith(comment))
	result.append(lines[i].substring(comment.length()));
      else
	result.append(lines[i]);
      result.append("\n");
    }

    return result.toString();
  }

  /**
   * Flattens the list into a single, long string. The separator string gets
   * added between the objects, but not after the last one.
   *
   * @param lines	the lines to flatten
   * @param sep		the separator
   * @return		the generated string
   */
  public static String flatten(List lines, String sep) {
    return flatten(lines.toArray(new Object[lines.size()]), sep);
  }

  /**
   * Flattens the array into a single, long string. The separator string gets
   * added between the objects, but not after the last one. Uses the "toString()"
   * method of the objects to turn them into a string.
   *
   * @param lines	the lines to flatten
   * @param sep		the separator
   * @return		the generated string
   */
  public static String flatten(Object[] lines, String sep) {
    StringBuilder	result;
    int			i;

    result = new StringBuilder();

    for (i = 0; i < lines.length; i++) {
      if (i > 0)
	result.append(sep);
      result.append(lines[i].toString());
    }

    return result.toString();
  }

  /**
   * Removes empty lines from the vector. Performs trimming before ascertaining
   * whether the line is empty.
   *
   * @param lines	the list to clean up
   */
  public static void removeEmptyLines(List<String> lines) {
    removeEmptyLines(lines, true);
  }

  /**
   * Removes empty lines from the vector.
   *
   * @param lines	the list to clean up
   * @param trim	whether to trim the line first before checking whether
   * 			it is empty or not
   */
  public static void removeEmptyLines(List<String> lines, boolean trim) {
    int		i;
    String	line;

    i = 0;
    while (i < lines.size()) {
      if (trim)
	line = lines.get(i).trim();
      else
	line = lines.get(i);

      if (line.length() == 0)
	lines.remove(i);
      else
	i++;
    }
  }

  /**
   * Removes comment lines from the vector.
   *
   * @param lines	the list to clean up
   * @param comment	the start of a comment
   */
  public static void removeComments(List<String> lines, String comment) {
    int		i;

    i = 0;
    while (i < lines.size()) {
      if (lines.get(i).startsWith(comment))
	lines.remove(i);
      else
	i++;
    }
  }

  /**
   * Compares two comparable objects. Takes care of null objects.
   * Returns -1, 0 or +1, if o1 less than, equal to or greater than o2.
   * Returns 0 if both objects null, -1 if o1 null but not o2 and +1 if o1 not
   * null but o2.
   *
   * @param o1		the first object
   * @param o2		the second object
   * @return		the comparison result
   */
  public static int compare(Comparable o1, Comparable o2) {
    if ((o1 != null) && (o2 != null))
      return o1.compareTo(o2);
    else if ((o1 == null) && (o2 == null))
      return 0;
    else if (o1 == null)
      return -1;
    else
      return +1;
  }

  /**
   * Creates a new array of the specified length and fills it with the values
   * of the old array before returning it.
   *
   * @param array	the array to adjust
   * @param newLen	the new length
   * @param defValue	the default value to fill the new array with
   * @return		the fixed array
   */
  public static Object adjustArray(Object array, int newLen, Object defValue) {
    Object	result;
    int		i;
    boolean	serializable;

    serializable = (defValue instanceof Serializable);
    result       = Array.newInstance((array != null ? array.getClass().getComponentType() : defValue.getClass()), newLen);
    for (i = 0; i < Array.getLength(result); i++) {
      if (serializable)
	Array.set(result, i, deepCopy(defValue));
      else
	Array.set(result, i, defValue);
    }

    if (array != null)
	System.arraycopy(
	    array, 0, result, 0,
	    (Array.getLength(array) < Array.getLength(result)) ? Array.getLength(array) : Array.getLength(result));

    return result;
  }
  
  /**
   * Turns a class name into a Class instance. Arrays are indicated by "[]" at
   * the end of the name. Multiple array indicators can be used.
   * 
   * @param classname	the class name to return the Class instance for
   * @return		the generated class instance, null if failed to create
   */
  public static Class stringToClass(String classname) {
    Class	result;
    String	arrays;
    int		arrayDim;
    int		i;
    
    result = null;

    arrayDim = 0;
    if (classname.endsWith(ARRAY_INDICATOR)) {
      arrays    = classname.substring(classname.indexOf(ARRAY_INDICATOR));
      arrays    = arrays.replace("][", "],[");
      arrayDim  = arrays.split(",").length;
      classname = classname.substring(0, classname.indexOf(ARRAY_INDICATOR));
    }

    try {
      result = Class.forName(classname);
      for (i = 0; i < arrayDim; i++)
	result = Array.newInstance(result, 0).getClass();
    }
    catch (Exception e) {
      result = null;
    }
    
    return result;
  }
  
  /**
   * Creates a new array of the specified element type with the specified 
   * number of elements. Arrays are indicated by "[]" at
   * the end of the clas name. Multiple array indicators can be used.
   * E.g., newArray("java.lang.Double[]", 5) will generate "Double[5][]".
   * 
   * @param elementClass	the class type for the array elements
   * @return			the generated array instance, null if failed to create
   */
  public static Object newArray(String elementClass, int length) {
    Object	result;
    
    try {
      result = Array.newInstance(stringToClass(elementClass), length);
    }
    catch (Exception e) {
      result = null;
    }
    
    return result;
  }

  /**
   * Turns a class into a string.
   *
   * @param c		the class to turn into a string
   * @return		the string
   */
  public static String classToString(Class c) {
    String	result;
    int		dim;
    int		i;

    if (c.isArray()) {
      dim    = getArrayDimensions(c);
      result = getArrayClass(c).getName();
      for (i = 0; i < dim; i++)
	result += ARRAY_INDICATOR;
    }
    else {
      result = c.getName();
    }

    return result;
  }

  /**
   * Turns a class array into a string.
   *
   * @param c		the class array to turn into a string
   * @return		the string
   */
  public static String classesToString(Class[] c) {
    return classesToString(c, ", ");
  }

  /**
   * Turns a class array into a string.
   *
   * @param c		the class array to turn into a string
   * @param separator	the separator between the classes
   * @return		the string
   */
  public static String classesToString(Class[] c, String separator) {
    StringBuilder	result;

    result = new StringBuilder();
    for (Class cls: c) {
      if (result.length() > 0)
	result.append(separator);
      result.append(classToString(cls));
    }

    return result.toString();
  }

  /**
   * Turns the double array into a float array.
   *
   * @param array	the array to convert
   * @return		the converted array
   */
  public static float[] toFloat(double[] array) {
    float[]	result;
    int		i;

    result = new float[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = (float) array[i];

    return result;
  }

  /**
   * Turns the float array into a double array.
   *
   * @param array	the array to convert
   * @return		the converted array
   */
  public static double[] toDouble(float[] array) {
    double[]	result;
    int		i;

    result = new double[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i];

    return result;
  }

  /**
   * Pads the string with a padding character with to at most "width" width.
   * Does not truncate the string.
   *
   * @param s		the string to pad
   * @param padding	the padding character
   * @param width	the maximum width
   * @return		the padded string
   */
  public static String padLeft(String s, char padding, int width) {
    return padLeft(s, padding, width, false);
  }

  /**
   * Pads the string with a padding character with to at most "width" width.
   * Truncating, if string is longer than "width", is optional.
   *
   * @param s		the string to pad
   * @param padding	the padding character
   * @param width	the maximum width
   * @param truncate	if true then the string can be truncated (on the left)
   * 			to fit width
   * @return		the padded string
   */
  public static String padLeft(String s, char padding, int width, boolean truncate) {
    StringBuilder	result;

    result = new StringBuilder(s);

    // pad
    while (result.length() < width)
      result.insert(0, padding);

    // truncate
    if (truncate) {
      if (result.length() > width)
	result.delete(0, result.length() - width);
    }

    return result.toString();
  }

  /**
   * Returns the stacktrace of the throwable as string.
   *
   * @param t		the throwable to get the stacktrace for
   * @return		the stacktrace
   */
  public static String throwableToString(Throwable t) {
    return throwableToString(t, -1);
  }

  /**
   * Returns the stacktrace of the throwable as string.
   *
   * @param t		the throwable to get the stacktrace for
   * @param maxLines	the maximum number of lines to print, <= 0 for all
   * @return		the stacktrace
   */
  public static String throwableToString(Throwable t, int maxLines) {
    StringWriter	writer;
    StringBuilder	result;
    String[]		lines;
    int			i;

    writer = new StringWriter();
    t.printStackTrace(new PrintWriter(writer));

    if (maxLines > 0) {
      result = new StringBuilder();
      lines  = writer.toString().split("\n");
      for (i = 0; i < maxLines; i++) {
	if (i > 0)
	  result.append("\n");
	result.append(lines[i]);
      }
    }
    else {
      result = new StringBuilder(writer.toString());
    }

    return result.toString();
  }

  /**
   * Returns the current stack trace.
   *
   * @param maxDepth	the maximum depth of the stack trace, <= 0 for full trace
   * @return		the stack trace as string (multiple lines)
   */
  public static String getStackTrace(int maxDepth) {
    StringBuilder	result;
    Throwable		th;
    StackTraceElement[]	trace;
    int			i;

    result = new StringBuilder();
    th     = new Throwable();
    th.fillInStackTrace();
    trace  = th.getStackTrace();
    if (maxDepth <= 0)
      maxDepth = trace.length - 1;
    maxDepth++;  // we're starting at 1 not 0
    maxDepth = Math.min(maxDepth, trace.length);
    for (i = 1; i < maxDepth; i++) {
      if (i > 1)
	result.append("\n");
      result.append(trace[i]);
    }

    return result.toString();
  }

  /**
   * Converts the given decimal number into a different base.
   *
   * @param n		the decimal number to convert
   * @param base	the base
   * @return		the digits in the new base; index refers to power,
   * 			ie, 0 = base^0, 3 = base^3
   */
  public static List<Integer> toBase(int n, int base) {
    List<Integer>	result;
    int			current;
    int			times;
    int			remainder;

    result  = new ArrayList<Integer>();
    current = n;
    do {
      times     = current / base;
      remainder = current - times * base;
      result.add(remainder);
      current   = times;
    }
    while (times > 0);

    return result;
  }

  /**
   * Returns a hexadecimal representation of the byte value.
   * <br><br>
   * Taken from <a href="http://www.herongyang.com/Cryptography/SHA1-Message-Digest-in-Java.html" target="_blank">here</a>.
   *
   * @param value	the value to convert
   * @return		the hexadecimal representation
   */
  public static String toHex(byte value) {
    StringBuilder	result;

    result = new StringBuilder();
    result.append(HEX_DIGIT[(value >> 4) & 0x0f]);
    result.append(HEX_DIGIT[(value) & 0x0f]);

    return result.toString();
  }

  /**
   * Parses the hex string (00 - FF) and returns the byte.
   *
   * @param s		the string to parse
   * @return		the byte value
   */
  @MixedCopyright(
    copyright = "2008 Dave L.",
    license = License.CC_BY_SA_3,
    url = "http://stackoverflow.com/a/140861/4698227"
  )
  public static byte fromHex(String s) {
    s = s.toUpperCase();
    if (s.length() == 2) {
      return (byte) ((Character.digit(s.charAt(0), 16) << 4) + Character.digit(s.charAt(1), 16));
    }
    else {
      throw new IllegalArgumentException("Must be 2 characters long, provided: " + s);
    }
  }

  /**
   * Turns the binary array to a hexadecimal string.
   *
   * @param binary	the array to convert
   * @return		the hex string
   */
  public static String toHexArray(byte[] binary) {
    StringBuilder	result;

    result = new StringBuilder();
    for (byte b: binary)
      result.append(toHex(b));

    return result.toString();
  }

  /**
   * Turns the hex string (even number of chars) into an array of bytes.
   *
   * @param hex		the string to convert
   * @return		the extracted bytes
   */
  public static byte[] fromHexArray(String hex) {
    TByteArrayList	result;
    int			i;

    result = new TByteArrayList();

    for (i = 0; i < hex.length() / 2; i++)
      result.add(fromHex(hex.substring(i * 2, i * 2 + 2)));

    return result.toArray();
  }

  /**
   * Tries to parse the given string as boolean (true|false - any case).
   *
   * @param s		the string to check
   * @return		true if it represents a valid boolean
   */
  public static boolean isBoolean(String s) {
    s = s.toLowerCase();
    return s.equals("true") || s.equals("false");
  }

  /**
   * Tries to parse the given string as byte.
   *
   * @param s		the string to check
   * @return		true if it represents a valid byte
   */
  public static boolean isByte(String s) {
    try {
      Byte.parseByte(s);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Tries to parse the given string as short.
   *
   * @param s		the string to check
   * @return		true if it represents a valid short
   */
  public static boolean isShort(String s) {
    try {
      Short.parseShort(s);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Tries to parse the given string as integer.
   *
   * @param s		the string to check
   * @return		true if it represents a valid integer
   */
  public static boolean isInteger(String s) {
    try {
      Integer.parseInt(s);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Tries to parse the given string as long.
   *
   * @param s		the string to check
   * @return		true if it represents a valid long
   */
  public static boolean isLong(String s) {
    try {
      Long.parseLong(s);
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Tries to parse the given string as float.
   * Uses the default locale.
   *
   * @param s		the string to check
   * @return		true if it represents a valid float
   */
  public static boolean isFloat(String s) {
    return isFloat(s, LocaleHelper.getSingleton().getDefault());
  }

  /**
   * Tries to parse the given string as float.
   *
   * @param s		the string to check
   * @param locale	the locale to use
   * @return		true if it represents a valid float
   */
  public static boolean isFloat(String s, Locale locale) {
    return (toFloat(s, locale) != null);
  }

  /**
   * Tries to parse the given string as float.
   * Uses the default locale.
   *
   * @param s		the string to parse
   * @return		the float or null if failed to parse
   */
  public static Float toFloat(String s) {
    return toFloat(s, LocaleHelper.getSingleton().getDefault());
  }

  /**
   * Tries to parse the given string as float.
   *
   * @param s		the string to parse
   * @param locale	the locale to use
   * @return		the float or null if failed to parse
   */
  public static Float toFloat(String s, Locale locale) {
    char	grouping;
    char	decimal;
    
    grouping = LocaleHelper.getSingleton().getGroupingSeparator(locale);
    decimal  = LocaleHelper.getSingleton().getDecimalSeparator(locale);
    
    s = s.replace("" + grouping, "");
    s = s.replace(decimal, '.');
    
    try {
      return Float.parseFloat(s);
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Tries to parse the given string as double.
   * Uses the default locale.
   *
   * @param s		the string to check
   * @return		true if it represents a valid double
   */
  public static boolean isDouble(String s) {
    return isDouble(s, LocaleHelper.getSingleton().getDefault());
  }

  /**
   * Tries to parse the given string as double.
   *
   * @param s		the string to check
   * @param locale	the locale to use
   * @return		true if it represents a valid double
   */
  public static boolean isDouble(String s, Locale locale) {
    return (toDouble(s, locale) != null);
  }

  /**
   * Tries to parse the given string as double.
   * Uses the default locale.
   *
   * @param s		the string to parse
   * @return		the double or null if failed to parse
   */
  public static Double toDouble(String s) {
    return toDouble(s, LocaleHelper.getSingleton().getDefault());
  }

  /**
   * Tries to parse the given string as double.
   *
   * @param s		the string to parse
   * @param locale	the locale to use
   * @return		the double or null if failed to parse
   */
  public static Double toDouble(String s, Locale locale) {
    char	grouping;
    char	decimal;
    
    grouping = LocaleHelper.getSingleton().getGroupingSeparator(locale);
    decimal  = LocaleHelper.getSingleton().getDecimalSeparator(locale);
    
    s = s.replace("" + grouping, "");
    s = s.replace(decimal, '.');
    
    try {
      return Double.parseDouble(s);
    }
    catch (Exception e) {
      return null;
    }
  }

  /**
   * Splits the row into separate cells based on the delimiter character.
   * String.split(regexp) does not work with empty cells (eg ",,,").
   *
   * @param line	the row to split
   * @param delimiter	the delimiter to use
   * @return		the cells
   */
  public static String[] split(String line, char delimiter) {
    return split(line, "" + delimiter);
  }

  /**
   * Splits the row into separate cells based on the delimiter string.
   * String.split(regexp) does not work with empty cells (eg ",,,").
   *
   * @param line	the row to split
   * @param delimiter	the delimiter to use
   * @return		the cells
   */
  public static String[] split(String line, String delimiter) {
    ArrayList<String>	result;
    int			lastPos;
    int			currPos;

    result  = new ArrayList<String>();
    lastPos = -1;
    while ((currPos = line.indexOf(delimiter, lastPos + 1)) > -1) {
      result.add(line.substring(lastPos + 1, currPos));
      lastPos = currPos;
    }
    result.add(line.substring(lastPos + 1));

    return result.toArray(new String[result.size()]);
  }
  
  /**
   * Swaps the two integers in the array.
   * 
   * @param array	the array with two elements to swap
   */
  public static void swap(int[] array) {
    int		tmp;
    
    if (array.length == 2) {
      tmp      = array[0];
      array[0] = array[1];
      array[1] = tmp;
    }
  }
  
  /**
   * Swaps the two longs in the array.
   * 
   * @param array	the array with two elements to swap
   */
  public static void swap(long[] array) {
    long	tmp;
    
    if (array.length == 2) {
      tmp      = array[0];
      array[0] = array[1];
      array[1] = tmp;
    }
  }
  
  /**
   * Swaps the two floats in the array.
   * 
   * @param array	the array with two elements to swap
   */
  public static void swap(float[] array) {
    float	tmp;
    
    if (array.length == 2) {
      tmp      = array[0];
      array[0] = array[1];
      array[1] = tmp;
    }
  }
  
  /**
   * Swaps the two doubles in the array.
   * 
   * @param array	the array with two elements to swap
   */
  public static void swap(double[] array) {
    double		tmp;
    
    if (array.length == 2) {
      tmp      = array[0];
      array[0] = array[1];
      array[1] = tmp;
    }
  }
  
  /**
   * Swaps the two objects in the array.
   * 
   * @param array	the array with two elements to swap
   */
  public static void swap(Object[] array) {
    Object	tmp;
    
    if (array.length == 2) {
      tmp      = array[0];
      array[0] = array[1];
      array[1] = tmp;
    }
  }
  
  /**
   * Turns the byte array into a Byte list.
   * 
   * @param array	the array to convert
   * @return		the generated list
   */
  public static List<Byte> toList(byte[] array) {
    ArrayList<Byte>	result;
    
    result = new ArrayList<Byte>();
    for (byte element: array)
      result.add(element);
    
    return result;
  }
  
  /**
   * Turns the Byte list into a byte array.
   * 
   * @param list	the list to convert
   * @return		the generated array
   */
  public static byte[] toByteArray(List<Byte> list) {
    byte[]	result;
    int		i;
    
    result = new byte[list.size()];
    for (i = 0; i < list.size(); i++)
      result[i] = list.get(i);
    
    return result;
  }
  
  /**
   * Turns the int array into an Integer list.
   * 
   * @param array	the array to convert
   * @return		the generated list
   */
  public static List<Integer> toList(int[] array) {
    ArrayList<Integer>	result;
    
    result = new ArrayList<Integer>();
    for (int element: array)
      result.add(element);
    
    return result;
  }
  
  /**
   * Turns the Integer list into an int array.
   * 
   * @param list	the list to convert
   * @return		the generated array
   */
  public static int[] toIntArray(List<Integer> list) {
    int[]	result;
    int		i;
    
    result = new int[list.size()];
    for (i = 0; i < list.size(); i++)
      result[i] = list.get(i);
    
    return result;
  }

  /**
   * Turns the long array into a Long list.
   * 
   * @param array	the array to convert
   * @return		the generated list
   */
  public static List<Long> toList(long[] array) {
    ArrayList<Long>	result;
    
    result = new ArrayList<Long>();
    for (long element: array)
      result.add(element);
    
    return result;
  }
  
  /**
   * Turns the Long list into a long array.
   * 
   * @param list	the list to convert
   * @return		the generated array
   */
  public static long[] toLongArray(List<Long> list) {
    long[]	result;
    int		i;
    
    result = new long[list.size()];
    for (i = 0; i < list.size(); i++)
      result[i] = list.get(i);
    
    return result;
  }
  
  /**
   * Turns the double array into a Double list.
   * 
   * @param array	the array to convert
   * @return		the generated list
   */
  public static List<Double> toList(double[] array) {
    ArrayList<Double>	result;
    
    result = new ArrayList<Double>();
    for (double element: array)
      result.add(element);
    
    return result;
  }
  
  /**
   * Turns the Double list into a double array.
   * 
   * @param list	the list to convert
   * @return		the generated array
   */
  public static double[] toDoubleArray(List<Double> list) {
    double[]	result;
    int		i;
    
    result = new double[list.size()];
    for (i = 0; i < list.size(); i++)
      result[i] = list.get(i);
    
    return result;
  }

  /**
   * Turns the float array into a Float list.
   * 
   * @param array	the array to convert
   * @return		the generated list
   */
  public static List<Float> toList(float[] array) {
    ArrayList<Float>	result;
    
    result = new ArrayList<Float>();
    for (float element: array)
      result.add(element);
    
    return result;
  }
  
  /**
   * Turns the Float list into a float array.
   * 
   * @param list	the list to convert
   * @return		the generated array
   */
  public static float[] toFloatArray(List<Float> list) {
    float[]	result;
    int		i;
    
    result = new float[list.size()];
    for (i = 0; i < list.size(); i++)
      result[i] = list.get(i);
    
    return result;
  }

  /**
   * Outputs the stacktrace along with the message on stderr and returns a 
   * combination of both of them as string.
   * 
   * @param source	the object that generated the exception, can be null
   * @param msg		the message for the exception
   * @param t		the exception
   * @return		the full error message (message + stacktrace)
   */
  public static String handleException(LoggingSupporter source, String msg, Throwable t) {
    return handleException(source, msg, t, false);
  }

  /**
   * Generates a string from the stacktrace along with the message and returns
   * that. Depending on the silent flag, this string is also forwarded to the
   * source's logger.
   * 
   * @param source	the object that generated the exception, can be null
   * @param msg		the message for the exception
   * @param t		the exception
   * @param silent	if true then the generated message is not forwarded
   * 			to the source's logger
   * @return		the full error message (message + stacktrace)
   */
  public static String handleException(LoggingSupporter source, String msg, Throwable t, boolean silent) {
    String	result;

    result = msg.trim() + "\n" + Utils.throwableToString(t);
    if (!silent) {
      if (source != null)
        source.getLogger().log(Level.SEVERE, msg, t);
      else
        System.err.println(result);
    }
    
    return result;
  }

  /**
   * A simple waiting method.
   *
   * @param obj		the object to use for logging and synchronizing
   * @param msec	the maximum number of milli-seconds to wait, no waiting if 0
   * @param interval	the amount msecs to wait before checking state (interval < msec)
   */
  public static void wait(LoggingObject obj, int msec, int interval) {
    wait(obj, null, msec, interval);
  }

  /**
   * A simple waiting method.
   *
   * @param obj		the object to use for logging and synchronizing
   * @param stoppable	the object to use for checking stoppped state
   * @param msec	the maximum number of milli-seconds to wait, no waiting if 0
   * @param interval	the amount msecs to wait before checking state (interval < msec)
   */
  public static void wait(LoggingObject obj, StoppableWithFeedback stoppable, int msec, int interval) {
    int count;
    int current;

    if (msec == 0)
      return;

    if (obj.isLoggingEnabled())
      obj.getLogger().fine("wait: " + msec);

    count = 0;
    while (count < msec) {
      try {
	current = msec - interval;
	if (current <= 0)
	  current = msec;
	if (current > interval)
	  current = interval;
	synchronized (obj) {
	  obj.wait(current);
	}
	count += current;
      }
      catch (Throwable t) {
	// ignored
      }
      // stopped?
      if (stoppable != null) {
	if (stoppable.isStopped())
	  break;
      }
    }
  }

  /**
   * Checks whether the class is a wrapper for a primitive.
   *
   * @param cls		the class to test
   * @return		true if primitve
   */
  public static boolean isPrimitive(Class cls) {
    if (cls == Boolean.class)
      return true;
    else if (cls == Character.class)
      return true;
    else if (cls == String.class)
      return true;
    else if (cls == Byte.class)
      return true;
    else if (cls == Short.class)
      return true;
    else if (cls == Integer.class)
      return true;
    else if (cls == Long.class)
      return true;
    else if (cls == Float.class)
      return true;
    else if (cls == Double.class)
      return true;
    return false;
  }

  /**
   * Returns the corresponding BaseObject-derived wrapper class.
   *
   * @param cls		the primitive class to wrap
   * @return		the wrapper class, null if not available
   */
  public static Class getWrapperClass(Class cls) {
    if (cls == Boolean.class)
      return BaseBoolean.class;
    else if (cls == Character.class)
      return BaseCharacter.class;
    else if (cls == String.class)
      return BaseString.class;
    else if (cls == Byte.class)
      return BaseByte.class;
    else if (cls == Short.class)
      return BaseShort.class;
    else if (cls == Integer.class)
      return BaseInteger.class;
    else if (cls == Long.class)
      return BaseLong.class;
    else if (cls == Float.class)
      return BaseFloat.class;
    else if (cls == Double.class)
      return BaseDouble.class;
    return null;
  }

  /**
   * Returns the corresponding BaseObject-derived wrapper class.
   *
   * @param cls		the primitive class to wrap
   * @return		the wrapper class, null if not available
   */
  public static Class getPrimitiveClass(Class cls) {
    if (cls == BaseBoolean.class)
      return Boolean.class;
    else if (cls == BaseCharacter.class)
      return Character.class;
    else if (cls == BaseString.class)
      return String.class;
    else if (cls == BaseByte.class)
      return Byte.class;
    else if (cls == BaseShort.class)
      return Short.class;
    else if (cls == BaseInteger.class)
      return Integer.class;
    else if (cls == BaseLong.class)
      return Long.class;
    else if (cls == BaseFloat.class)
      return Float.class;
    else if (cls == BaseDouble.class)
      return Double.class;
    return null;
  }

  /**
   * Checks whether the object is a wrapper for a primitive.
   *
   * @param obj		the object to test
   * @return		true if primitve
   */
  public static boolean isPrimitive(Object obj) {
    if (obj instanceof Boolean)
      return true;
    else if (obj instanceof Character)
      return true;
    else if (obj instanceof String)
      return true;
    else if (obj instanceof Byte)
      return true;
    else if (obj instanceof Short)
      return true;
    else if (obj instanceof Integer)
      return true;
    else if (obj instanceof Long)
      return true;
    else if (obj instanceof Float)
      return true;
    else if (obj instanceof Double)
      return true;
    return false;
  }

  /**
   * Wraps the primitive in a BaseObject-derived object.
   *
   * @param obj		the primitive to wrap
   * @return		the wrapped object, null if failed to wrap
   */
  public static BaseObject wrapPrimitive(Object obj) {
    if (obj instanceof Boolean)
      return new BaseBoolean((Boolean) obj);
    else if (obj instanceof Character)
      return new BaseCharacter((Character) obj);
    else if (obj instanceof String)
      return new BaseString((String) obj);
    else if (obj instanceof Byte)
      return new BaseByte((Byte) obj);
    else if (obj instanceof Short)
      return new BaseShort((Short) obj);
    else if (obj instanceof Integer)
      return new BaseInteger((Integer) obj);
    else if (obj instanceof Long)
      return new BaseLong((Long) obj);
    else if (obj instanceof Float)
      return new BaseFloat((Float) obj);
    else if (obj instanceof Double)
      return new BaseDouble((Double) obj);
    return null;
  }

  /**
   * Unwraps the primitve from the BaseObject-derived object.
   *
   * @param obj		the BaseObject to unwrap
   * @return		the primitve, null if failed to unwrap
   */
  public static Object unwrapPrimitive(Object obj) {
    if (obj instanceof BaseBoolean)
      return ((BaseBoolean) obj).booleanValue();
    else if (obj instanceof BaseCharacter)
      return ((BaseCharacter) obj).charValue();
    else if (obj instanceof BaseString)
      return ((BaseString) obj).stringValue();
    else if (obj instanceof BaseByte)
      return ((BaseByte) obj).byteValue();
    else if (obj instanceof BaseShort)
      return ((BaseShort) obj).shortValue();
    else if (obj instanceof BaseInteger)
      return ((BaseInteger) obj).intValue();
    else if (obj instanceof BaseLong)
      return ((BaseLong) obj).longValue();
    else if (obj instanceof BaseFloat)
      return ((BaseFloat) obj).floatValue();
    else if (obj instanceof BaseDouble)
      return ((BaseDouble) obj).doubleValue();
    return null;
  }

  /**
   * Calculates the log2 of the specified integer.
   *
   * @param n		the number to calculate log2 for
   * @return		the log2
   */
  public static double log2(int n) {
    return (Math.log(n) / Math.log(2));
  }

  /**
   * Escapes any non-ASCII characters as unicode sequences.
   *
   * @param s		the string to process
   * @return		the string with the escaped sequences
   */
  public static String escapeUnicode(String s) {
    StringBuilder	result;

    result = new StringBuilder();
    for (char c: s.toCharArray()) {
      if ((c >> 7) > 0) {
	result.append("\\u");
	result.append(HEX_DIGIT[(c >> 12) & 0xF]);
	result.append(HEX_DIGIT[(c >>  8) & 0xF]);
	result.append(HEX_DIGIT[(c >>  4) & 0xF]);
	result.append(HEX_DIGIT[(c >>  0) & 0xF]);
      }
      else {
	result.append(c);
      }
    }

    return result.toString();
  }

  /**
   * Unescapes unicode sequences and stores them as unicode characters instead.
   *
   * @param s  		the string to process
   * @return		the unescaped string
   */
  public static String unescapeUnicode(String s) {
    StringBuilder	result;
    int			index;
    String		unicode;
    char[]		chars;

    if (!s.contains("\\u"))
      return s;

    result = new StringBuilder();
    while ((index = s.indexOf("\\u")) > -1) {
      result.append(s.substring(0, index));
      s = s.substring(index + 2);
      if (s.length() >= 4) {
	unicode = s.substring(0, 4).toUpperCase();
	s = s.substring(4);
	chars = Character.toChars(Integer.parseInt(unicode, 16));
	for (char c: chars)
	  result.append(c);
      }
      else {
	result.append("\\u");
      }
    }
    result.append(s);

    return result.toString();
  }
}
