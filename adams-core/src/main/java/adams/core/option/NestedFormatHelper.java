/*
 * NestedFormatHelper.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class for the nested flow format.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NestedFormatHelper {
  /**
   * Determines the indentation level of the given string.
   *
   * @param s		the string to analyze
   * @return		the level
   */
  protected static int getIndentation(String s) {
    int		result;
    int		i;

    result = 0;

    for (i = 0; i < s.length(); i++) {
      if (s.charAt(i) != '\t')
        break;
      else
        result++;
    }

    return result;
  }

  /**
   * Turns the nested lines into a nested structure to be used by option
   * handlers.
   *
   * @param lines	the lines to turn into a nested structure
   * @param index	the index in the lines
   * @param levels	all the indentation levels in the lines
   * @return		the nested structure
   */
  protected static ArrayList linesToNested(List<String> lines, int[] index, int[] levels) {
    ArrayList	result;
    int		level;

    result = new ArrayList();
    level  = levels[index[0]];
    while (lines.size() > index[0]) {
      if (levels[index[0]] == level) {
        result.add(lines.get(index[0]).substring(level));
        index[0]++;
        continue;
      }

      if (levels[index[0]] > level) {
        result.add(linesToNested(lines, index, levels));
        continue;
      }

      if (levels[index[0]] < level) {
	break;
      }
    }

    return result;
  }

  /**
   * Turns the nested lines into a nested structure to be used by option
   * handlers.
   *
   * @param lines	the lines to turn into a nested structure
   * @return		the nested structure
   */
  public static ArrayList linesToNested(List<String> lines) {
    ArrayList	result;
    int[]	levels;
    int		i;


    levels = new int[lines.size()];
    for (i = 0; i < lines.size(); i++)
      levels[i] = getIndentation(lines.get(i));
    result = linesToNested(lines, new int[]{0}, levels);

    return result;
  }

  /**
   * Returns the indentation string for the given level.
   *
   * @param level	the level to generate the indentation string for
   * @return		the generated string
   */
  protected static String getIndentation(int level) {
    StringBuilder	result;
    int			i;

    result = new StringBuilder();
    for (i = 0; i < level; i++)
      result.append("\t");

    return result.toString();
  }

  /**
   * Turns the nested options from an option handler into indentated lines.
   *
   * @param nested	the nested structure to turn into indentated lines
   * @param lines	the lines so far
   * @param level	the current level of indentation
   */
  protected static void nestedToLines(List nested, List<String> lines, int level) {
    int		i;

    for (i = 0; i < nested.size(); i++) {
      if (nested.get(i).getClass() == String.class)
        lines.add(getIndentation(level) + nested.get(i));
      else
        nestedToLines((List) nested.get(i), lines, level + 1);
    }
  }

  /**
   * Turns the nested options from an option handler into indentated lines.
   *
   * @param nested	the nested structure to turn into indentated lines
   * @return		the indentated lines
   */
  public static List<String> nestedToLines(List nested) {
    ArrayList<String>	result;

    result = new ArrayList<String>();
    nestedToLines(nested, result, 0);

    return result;
  }
}
