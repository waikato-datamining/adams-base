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
 * NestedFormatHelper.java
 * Copyright (C) 2012-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.core.option;

import adams.core.Utils;
import adams.env.Modules;
import adams.env.Modules.Module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A helper class for the nested option format.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class NestedFormatHelper {
  
  /**
   * Container class for wrapping a line from the nested format.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Line
    implements Serializable {
    
    /** for serialization. */
    private static final long serialVersionUID = -4533183619132165184L;

    /** the line number. */
    protected int m_Number;
    
    /** the content. */
    protected String m_Content;
    
    /**
     * Wrapper for a line, line number is uninitialized at -1.
     * 
     * @param content	the actual content of the line
     */
    public Line(String content) {
      this(-1, content);
    }
    
    /**
     * Wrapper for a line.
     * 
     * @param number	the line number
     * @param content	the actual content of the line
     */
    public Line(int number, String content) {
      m_Number  = number;
      m_Content = content;
    }
    
    /**
     * Sets the associated line number.
     * 
     * @param value	the line number
     */
    public void setNumber(int value) {
      m_Number = value;
    }
    
    /**
     * Returns the associated line number.
     * 
     * @return		the line number
     */
    public int getNumber() {
      return m_Number;
    }
    
    /**
     * Returns the line content.
     * 
     * @return		the content
     */
    public String getContent() {
      return m_Content;
    }
    
    /**
     * Simply returns the line content.
     * 
     * @return		the content
     */
    @Override
    public String toString() {
      return m_Content;
    }
  }
  
  /**
   * Removes all comments from the start of the list of (raw) lines.
   * 
   * @param lines	the raw lines
   * @return		the number of comment lines
   */
  public static int removeComments(List<String> lines) {
    int		result;
    
    result = 0;
    while ((!lines.isEmpty()) && (lines.get(0).startsWith(NestedProducer.COMMENT))) {
      result++;
      lines.remove(0);
    }
    
    return result;
  }
  
  /**
   * Determines the indentation level of the given string.
   *
   * @param s		the string to analyze
   * @param indentChar	the character used for indentation
   * @return		the level
   */
  protected static int getIndentationLevel(String s, char indentChar) {
    int		result;
    int		i;

    result = 0;

    for (i = 0; i < s.length(); i++) {
      if (s.charAt(i) != indentChar)
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
   * @param offset	the offset in lines, i.e., the number of comments removed
   * @return		the nested structure
   */
  protected static ArrayList linesToNested(List<String> lines, int[] index, int[] levels, int offset) {
    ArrayList	result;
    int		level;

    result = new ArrayList();
    level  = levels[index[0]];
    while (lines.size() > index[0]) {
      if (levels[index[0]] == level) {
        result.add(new Line(offset + index[0], lines.get(index[0]).substring(level)));
        index[0]++;
        continue;
      }

      if (levels[index[0]] > level) {
        result.add(linesToNested(lines, index, levels, offset));
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
   * @param indentChar	the character used for indentation
   * @return		the nested structure
   */
  public static ArrayList linesToNested(List<String> lines, char indentChar) {
    return linesToNested(lines, 0, indentChar);
  }

  /**
   * Turns the nested lines into a nested structure to be used by option
   * handlers.
   *
   * @param lines	the lines to turn into a nested structure
   * @param offset	the offset in lines, i.e., the number of comments removed
   * @param indentChar	the character used for indentation
   * @return		the nested structure
   */
  public static ArrayList linesToNested(List<String> lines, int offset, char indentChar) {
    ArrayList	result;
    int[]	levels;
    int		i;


    levels = new int[lines.size()];
    for (i = 0; i < lines.size(); i++)
      levels[i] = getIndentationLevel(lines.get(i), indentChar);
    result = linesToNested(lines, new int[]{0}, levels, offset);

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
   * @param lineNo	whether to add the line numbers
   * @param lines	the lines so far
   * @param level	the current level of indentation
   */
  protected static void nestedToLines(List nested, boolean lineNo, List<String> lines, int level) {
    int		i;
    Line	line;

    for (i = 0; i < nested.size(); i++) {
      if (nested.get(i).getClass() == Line.class) {
	line = (Line) nested.get(i);
        lines.add((lineNo ? (line.getNumber()+1) + ":" : "") + getIndentation(level) + line.getContent());
      }
      else {
        nestedToLines((List) nested.get(i), lineNo, lines, level + 1);
      }
    }
  }

  /**
   * Turns the nested options from an option handler into indentated lines.
   *
   * @param nested	the nested structure to turn into indentated lines
   * @return		the indentated lines
   */
  public static List<String> nestedToLines(List nested) {
    return nestedToLines(nested, false);
  }

  /**
   * Turns the nested options from an option handler into indentated lines.
   *
   * @param nested	the nested structure to turn into indentated lines
   * @param lineNo	whether to add the line numbers
   * @return		the indentated lines
   */
  public static List<String> nestedToLines(List nested, boolean lineNo) {
    ArrayList<String>	result;

    result = new ArrayList<String>();
    nestedToLines(nested, lineNo, result, 0);

    return result;
  }

  /**
   * Updates the line numbering.
   *
   * @param nested	the nested lines to updated
   * @param offset	the current offset
   * @return		the last line number updated
   */
  protected static int renumber(List nested, int offset) {
    int		line;
    int		index;
    
    line  = offset;
    index = 0;
    while (index < nested.size()) {
      if (nested.get(index) instanceof ArrayList) {
	line = renumber((ArrayList) nested.get(index), line);
      }
      else {
	((Line) nested.get(index)).setNumber(line);
	line++;
      }
      index++;
    }
    
    return line;
  }

  /**
   * Updates the line numbering.
   *
   * @param nested	the nested lines to updated
   */
  public static void renumber(List nested) {
    renumber(nested, 0);
  }

  /**
   * Extracts the modules from the comments, if available.
   *
   * @param lines	the raw lines of the flow
   * @return		the modules, if any
   */
  public static List<String> getModules(List<String> lines) {
    List<String>	result;

    result = new ArrayList<>();
    for (String line: lines) {
      if (!line.startsWith(NestedProducer.COMMENT))
        break;
      line = line.substring(1).trim();
      if (line.startsWith(NestedProducer.MODULES)) {
        line = line.substring(line.indexOf(':') + 1).replace(" ", "");
        result.addAll(Arrays.asList(line.split(",")));
        break;
      }
    }

    return result;
  }

  /**
   * Checks whether there are any modules missing in the current environment
   * compared to the provided list of module names.
   * Only performs a check if {@link OptionUtils#getReportModuleDifferences()}
   * is set to true.
   *
   * @param modules	the list of modules to check
   * @return		null if everything OK, otherwise error message
   * @see		OptionUtils#getReportModuleDifferences()
   */
  public static String checkModules(List<String> modules) {
    if (!OptionUtils.getReportModuleDifferences())
      return null;

    if (!modules.isEmpty()) {
      for (Module module: Modules.getSingleton().getModules())
	modules.remove(module.getName());
      if (!modules.isEmpty())
	return "Flow was saved with additional modules: " + Utils.flatten(modules, ",");
    }
    return null;
  }
}
