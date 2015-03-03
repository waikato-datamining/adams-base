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
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
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
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: StringCut
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-type &lt;FIELD_DELIMITED|CHARACTER_POSITIONS&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;Determines what type of cut to perform.
 * &nbsp;&nbsp;&nbsp;default: FIELD_DELIMITED
 * </pre>
 * 
 * <pre>-field-delimiter &lt;java.lang.String&gt; (property: fieldDelimiter)
 * &nbsp;&nbsp;&nbsp;The field delimiter to use; \t gets automatically converted into its character 
 * &nbsp;&nbsp;&nbsp;counterpart.
 * &nbsp;&nbsp;&nbsp;default: \\t
 * </pre>
 * 
 * <pre>-field-index &lt;adams.core.Index&gt; (property: fieldIndex)
 * &nbsp;&nbsp;&nbsp;The 1-based index of the field to cut from the string(s).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-char-start-pos &lt;int&gt; (property: characterStartPos)
 * &nbsp;&nbsp;&nbsp;The position of the first character to include in case fixed character positions 
 * &nbsp;&nbsp;&nbsp;are used (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 * 
 * <pre>-char-end-pos &lt;int&gt; (property: characterEndPos)
 * &nbsp;&nbsp;&nbsp;The position of the last character to include in case fixed character positions 
 * &nbsp;&nbsp;&nbsp;are used (1-based).
 * &nbsp;&nbsp;&nbsp;default: 10
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

  /**
   * How to cut the string.
   */
  public enum CutType {
    FIELD_DELIMITED,
    CHARACTER_POSITIONS
  }

  /** the cut type. */
  protected CutType m_Type;
  
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
	    "type", "type",
	    CutType.FIELD_DELIMITED);

    m_OptionManager.add(
	    "field-delimiter", "fieldDelimiter",
	    "\\t");

    m_OptionManager.add(
	    "field-index", "fieldIndex",
	    new Index("1"));

    m_OptionManager.add(
	    "char-start-pos", "characterStartPos",
	    1);

    m_OptionManager.add(
	    "char-end-pos", "characterEndPos",
	    10);
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
   * Sets the type of cut to perform.
   *
   * @param value	the type of cut
   */
  public void setType(CutType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of cut to perform.
   *
   * @return		the type of cut
   */
  public CutType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "Determines what type of cut to perform.";
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

    if (QuickInfoHelper.hasVariable(this, "type") || (m_Type == CutType.CHARACTER_POSITIONS)) {
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

    if (m_Type == CutType.CHARACTER_POSITIONS) {
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
