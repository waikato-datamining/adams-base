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
 * StringRangeCut.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.util.ArrayList;
import java.util.List;

import adams.core.QuickInfoHelper;
import adams.core.Range;
import adams.core.Utils;

/**
 <!-- globalinfo-start -->
 * Cuts out a parts of a string. The cut can be either specified as fixed character positions or as fields from delimited columns.<br/>
 * When cutting multiple ranges, a 'glue' for the sub-strings can be provided.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br/>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br/>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: StringRangeCut
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-type &lt;CHARACTER_POSITIONS|DELIMITED_FIELDS&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of cut operation to perform.
 * &nbsp;&nbsp;&nbsp;default: CHARACTER_POSITIONS
 * </pre>
 *
 * <pre>-range &lt;java.lang.String&gt; (property: range)
 * &nbsp;&nbsp;&nbsp;The range of characters or fields to cut out; A range is a comma-separated
 * &nbsp;&nbsp;&nbsp;list of single 1-based indices or sub-ranges of indices ('start-end'); '
 * &nbsp;&nbsp;&nbsp;inv(...)' inverts the range '...'; the following placeholders can be used
 * &nbsp;&nbsp;&nbsp;as well: first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-delimiter &lt;java.lang.String&gt; (property: delimiter)
 * &nbsp;&nbsp;&nbsp;The delimiter to use in case of DELIMITED_FIELDS; \t gets automatically
 * &nbsp;&nbsp;&nbsp;converted into its character counterpart.
 * &nbsp;&nbsp;&nbsp;default: \\t
 * </pre>
 *
 * <pre>-glue &lt;java.lang.String&gt; (property: glue)
 * &nbsp;&nbsp;&nbsp;The 'glue' string to use for joining the sub-strings from the ranges; e.g.
 * &nbsp;&nbsp;&nbsp;, \t gets automatically converted into its character counterpart.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringRangeCut
  extends AbstractStringOperation {

  /** for serialization. */
  private static final long serialVersionUID = 882143928012626919L;

  /**
   * Enumeration for whether to use character positions of delimited fields.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Type {
    /** character position. */
    CHARACTER_POSITIONS,
    /** delimited fields. */
    DELIMITED_FIELDS
  }

  /** what type of cutting to perform. */
  protected Type m_Type;

  /** the range. */
  protected Range m_Range;

  /** the field delimiter. */
  protected String m_Delimiter;

  /** the glue for the sub-strings. */
  protected String m_Glue;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Cuts out a parts of a string. The cut can be either specified as "
      + "fixed character positions or as fields from delimited columns.\n"
      + "When cutting multiple ranges, a 'glue' for the sub-strings can "
      + "be provided.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "type",
	    Type.CHARACTER_POSITIONS);

    m_OptionManager.add(
	    "range", "range",
	    new Range("1"));

    m_OptionManager.add(
	    "delimiter", "delimiter",
	    "\\t");

    m_OptionManager.add(
	    "glue", "glue",
	    "");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Range = new Range();
  }

  /**
   * Sets the type of cut operation to perform.
   *
   * @param value	the type
   */
  public void setType(Type value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of cut operation to perform.
   *
   * @return		the type
   */
  public Type getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The type of cut operation to perform.";
  }

  /**
   * Sets the range of characters/fields to extract.
   *
   * @param value	the range
   */
  public void setRange(Range value) {
    m_Range = value;
    reset();
  }

  /**
   * Returns the range of characters/fields to extract.
   *
   * @return		the range
   */
  public Range getRange() {
    return m_Range;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rangeTipText() {
    return "The range of characters or fields to cut out.";
  }

  /**
   * Sets the delimiter to use.
   *
   * @param value	the delimiter
   */
  public void setDelimiter(String value) {
    m_Delimiter = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the delimiter in use.
   *
   * @return		the delimiter
   */
  public String getDelimiter() {
    return Utils.backQuoteChars(m_Delimiter);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String delimiterTipText() {
    return
        "The delimiter to use in case of " + Type.DELIMITED_FIELDS + "; \\t gets "
      + "automatically converted into its character counterpart.";
  }

  /**
   * Sets the "glue" to use for joining the substrings.
   *
   * @param value	the glue
   */
  public void setGlue(String value) {
    m_Glue = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the "glue" to use for joining the substrings.
   *
   * @return		the glue
   */
  public String getGlue() {
    return Utils.backQuoteChars(m_Glue);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String glueTipText() {
    return
        "The 'glue' string to use for joining the sub-strings from the ranges; "
      + "e.g., \\t gets automatically converted into its character counterpart.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    if (m_Type == Type.CHARACTER_POSITIONS) {
      result = QuickInfoHelper.toString(this, "range", m_Range, "Chars: ");
    }
    else if (m_Type == Type.DELIMITED_FIELDS) {
      result  = QuickInfoHelper.toString(this, "range", m_Range, "Fields: ");
      result += QuickInfoHelper.toString(this, "delimiter", m_Delimiter, ", Delimiter: ");
    }
    else {
      throw new IllegalStateException("Unhandled type: " + m_Type);
    }

    value = QuickInfoHelper.toString(this, "glue", m_Glue, ", Glue: ");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Processes the string. If null is returned, this output will be ignored.
   *
   * @param s		the string to process
   * @return		the processed string or null if nothing produced
   */
  @Override
  protected String process(String s) {
    String		result;
    String[]		parts;
    List<String>	substrings;
    int[]		indices;
    int[][]		segs;
    int			i;

    substrings = new ArrayList<String>();

    if (m_Type == Type.CHARACTER_POSITIONS) {
      m_Range.setMax(s.length());
      segs = m_Range.getIntSegments();
      for (i = 0; i < segs.length; i++)
	substrings.add(s.substring(segs[i][0], segs[i][1] + 1));
    }
    else if (m_Type == Type.DELIMITED_FIELDS) {
      parts = Utils.split(s, m_Delimiter);
      m_Range.setMax(parts.length);
      indices = m_Range.getIntIndices();
      for (i = 0; i < indices.length; i++)
	substrings.add(parts[indices[i]]);
    }
    else {
      throw new IllegalStateException("Unhandled cut type: " + m_Type);
    }

    result = Utils.flatten(substrings, m_Glue);

    return result;
  }
}
