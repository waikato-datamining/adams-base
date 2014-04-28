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
 * StringCut.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.Index;
import adams.core.QuickInfoHelper;
import adams.core.Utils;

/**
 <!-- globalinfo-start -->
 * Cuts out a part of a string. The cut can be either specified as a fixed character position or as a field from delimited columns.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br/>
 * - accepts:<br/>
 * <pre>   java.lang.String</pre>
 * <pre>   java.lang.String[]</pre>
 * - generates:<br/>
 * <pre>   java.lang.String</pre>
 * <pre>   java.lang.String[]</pre>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 *         The name of the actor.
 *         default: StringCut
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 *         The annotations to attach to this actor.
 *         default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 *         If set to true, transformation is skipped and the input token is just forwarded
 *          as it is.
 * </pre>
 *
 * <pre>-use-char-pos (property: useCharacterPos)
 *         If true then fixed character positions are used to extract the data instead
 *          of delimited fields.
 * </pre>
 *
 * <pre>-char-start-pos &lt;int&gt; (property: characterStartPos)
 *         The position of the first character to include in case fixed character positions
 *          are used.
 *         default: 1
 * </pre>
 *
 * <pre>-char-end-pos &lt;int&gt; (property: characterEndPos)
 *         The position of the last character to include in case fixed character positions
 *          are used.
 *         default: 10
 * </pre>
 *
 * <pre>-field-delimiter &lt;java.lang.String&gt; (property: fieldDelimiter)
 *         The field delimiter to use; \t gets automatically converted into its character
 *          counterpart.
 *         default: \\t
 * </pre>
 *
 * <pre>-field-index &lt;java.lang.String&gt; (property: fieldIndex)
 *         The 1-based index of the field to cut from the string(s).
 *         default: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringCut
  extends AbstractStringOperation {

  /** for serialization. */
  private static final long serialVersionUID = -3687113148170774846L;

  /** whether to use character positions or fields. */
  protected boolean m_UseCharacterPos;

  /** the character starting position. */
  protected int m_CharacterStartPos;

  /** the character end position. */
  protected int m_CharacterEndPos;

  /** the field delimiter. */
  protected String m_FieldDelimiter;

  /** the field number to extract. */
  protected Index m_FieldIndex;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Cuts out a part of a string. The cut can be either specified as a "
      + "fixed character position or as a field from delimited columns.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "use-char-pos", "useCharacterPos",
	    false);

    m_OptionManager.add(
	    "char-start-pos", "characterStartPos",
	    1);

    m_OptionManager.add(
	    "char-end-pos", "characterEndPos",
	    10);

    m_OptionManager.add(
	    "field-delimiter", "fieldDelimiter",
	    "\\t");

    m_OptionManager.add(
	    "field-index", "fieldIndex",
	    new Index("1"));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FieldIndex = new Index();
  }

  /**
   * Sets whether to use fixed character positions instead of delimited fields.
   *
   * @param value	if true then character positions will be used
   */
  public void setUseCharacterPos(boolean value) {
    m_UseCharacterPos = value;
    reset();
  }

  /**
   * Returns whether to use fixed character positions instead of delimited fields.
   *
   * @return		true if character possitions are used
   */
  public boolean getUseCharacterPos() {
    return m_UseCharacterPos;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useCharacterPosTipText() {
    return
        "If true then fixed character positions are used to extract the data "
      + "instead of delimited fields.";
  }

  /**
   * Sets the position of the first character to include.
   *
   * @param value	the starting position
   */
  public void setCharacterStartPos(int value) {
    m_CharacterStartPos = value;
    reset();
  }

  /**
   * Returns the position of the first character to include.
   *
   * @return		the starting position
   */
  public int getCharacterStartPos() {
    return m_CharacterStartPos;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String characterStartPosTipText() {
    return
        "The position of the first character to include in case fixed "
      + "character positions are used (1-based).";
  }

  /**
   * Sets the position of the last character to include.
   *
   * @param value	the end position
   */
  public void setCharacterEndPos(int value) {
    m_CharacterEndPos = value;
    reset();
  }

  /**
   * Returns the position of the last character to include.
   *
   * @return		the end position
   */
  public int getCharacterEndPos() {
    return m_CharacterEndPos;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String characterEndPosTipText() {
    return
        "The position of the last character to include in case fixed "
      + "character positions are used (1-based).";
  }

  /**
   * Sets the field delimiter to use.
   *
   * @param value	the delimiter
   */
  public void setFieldDelimiter(String value) {
    m_FieldDelimiter = Utils.unbackQuoteChars(value);
    reset();
  }

  /**
   * Returns the field delimiter in use.
   *
   * @return		the delimiter
   */
  public String getFieldDelimiter() {
    return Utils.backQuoteChars(m_FieldDelimiter);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldDelimiterTipText() {
    return
        "The field delimiter to use; \\t gets "
      + "automatically converted into its character counterpart.";
  }

  /**
   * Sets the index of the field to cut.
   *
   * @param value	the index
   */
  public void setFieldIndex(Index value) {
    m_FieldIndex = value;
    reset();
  }

  /**
   * Returns the index of the field to cut.
   *
   * @return		the index
   */
  public Index getFieldIndex() {
    return m_FieldIndex;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldIndexTipText() {
    return "The 1-based index of the field to cut from the string(s).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    if (QuickInfoHelper.hasVariable(this, "useCharacterPos") || m_UseCharacterPos) {
      result  = QuickInfoHelper.toString(this, "characterStartPos", m_CharacterStartPos);
      result += QuickInfoHelper.toString(this, "characterEndPos", m_CharacterEndPos, "-");
    }
    else {
      result  = QuickInfoHelper.toString(this, "fieldIndex", m_FieldIndex, "Index = ");
      result += QuickInfoHelper.toString(this, "fieldDelimiter", m_FieldDelimiter, ", Delimiter = ");
    }

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
    String	result;
    int		from;
    int		to;
    String[]	parts;

    if (m_UseCharacterPos) {
      from = m_CharacterStartPos - 1;
      to   = m_CharacterEndPos;
      if (to > s.length())
	to = s.length();
      if (from < s.length())
	result = s.substring(from, to);
      else
	result = "";
    }
    else {
      parts = s.split(m_FieldDelimiter);
      m_FieldIndex.setMax(parts.length);
      if ((m_FieldIndex.getIntIndex() != -1) && (m_FieldIndex.getIntIndex() < parts.length))
	result = parts[m_FieldIndex.getIntIndex()];
      else
	result = "";
    }

    return result;
  }
}
