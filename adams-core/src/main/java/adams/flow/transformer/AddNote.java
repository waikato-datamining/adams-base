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
 * AddNote.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.Notes;
import adams.data.NotesHandler;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Adds a note to the notes handler passing through.<br/>
 * Pre-defined note types:<br/>
 * - ERROR: <br/>
 * - WARNING: <br/>
 * - PROCESS INFORMATION
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.NotesHandler<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.NotesHandler<br/>
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
 * &nbsp;&nbsp;&nbsp;default: AddNote
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-note-group &lt;java.lang.String&gt; (property: noteGroup)
 * &nbsp;&nbsp;&nbsp;The group to file the note under, eg a classname.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-note-type &lt;java.lang.String&gt; (property: noteType)
 * &nbsp;&nbsp;&nbsp;The type of note to add (prefixes the note value); predefined: ERROR: , 
 * &nbsp;&nbsp;&nbsp;WARNING: , PROCESS INFORMATION
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-note-value &lt;java.lang.String&gt; (property: noteValue)
 * &nbsp;&nbsp;&nbsp;The value of the note to add.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 6264 $
 */
public class AddNote
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 4013915680601748582L;

  /** the group of the note to add (eg classname). */
  protected String m_NoteGroup;

  /** the type of note to add. */
  protected String m_NoteType;
  
  /** the note value to add. */
  protected String m_NoteValue;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Adds a note to the notes handler passing through.\n"
	+ "Pre-defined note types:\n"
	+ "- " + Notes.ERROR + "\n"
	+ "- " + Notes.WARNING + "\n"
	+ "- " + Notes.PROCESS_INFORMATION;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "note-group", "noteGroup",
	    "");

    m_OptionManager.add(
	    "note-type", "noteType",
	    "");

    m_OptionManager.add(
	    "note-value", "noteValue",
	    "");
  }

  /**
   * Sets the note grpup.
   *
   * @param value	the grpup
   */
  public void setNoteGroup(String value) {
    m_NoteGroup = value;
    reset();
  }

  /**
   * Returns the note grpup.
   *
   * @return		the grpup
   */
  public String getNoteGroup() {
    return m_NoteGroup;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noteGroupTipText() {
    return "The group to file the note under, eg a classname.";
  }

  /**
   * Sets the note type.
   *
   * @param value	the type
   */
  public void setNoteType(String value) {
    m_NoteType = value;
    reset();
  }

  /**
   * Returns the note type.
   *
   * @return		the type
   */
  public String getNoteType() {
    return m_NoteType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noteTypeTipText() {
    return 
	"The type of note to add (prefixes the note value); predefined: " 
	+ Notes.ERROR + ", " + Notes.WARNING + ", " + Notes.PROCESS_INFORMATION;
  }

  /**
   * Sets the note value.
   *
   * @param value	the value
   */
  public void setNoteValue(String value) {
    m_NoteValue = value;
    reset();
  }

  /**
   * Returns the note value.
   *
   * @return		the value
   */
  public String getNoteValue() {
    return m_NoteValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String noteValueTipText() {
    return "The value of the note to add.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = QuickInfoHelper.toString(this, "noteGroup", (m_NoteGroup.isEmpty() ? "-no group-" : m_NoteGroup), "group: ");
    result += QuickInfoHelper.toString(this, "noteType", (m_NoteType.isEmpty() ? "-no type-" : m_NoteType), ", type: ");
    result += QuickInfoHelper.toString(this, "noteValue", (m_NoteValue.isEmpty() ? "-no value-" : m_NoteValue), ", value: ");
    
    return result;
  }
  
  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.NotesHandler.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{NotesHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.data.NotesHandler.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{NotesHandler.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    NotesHandler	handler;

    result = null;

    try {
      handler       = (NotesHandler) m_InputToken.getPayload();
      handler.getNotes().addNote(m_NoteGroup, m_NoteType + m_NoteValue);
      m_OutputToken = new Token(handler);
    }
    catch (Exception e) {
      m_OutputToken = null;
      result = handleException(
	  "Failed to add note (" + m_NoteGroup + "/" + m_NoteType + "/" + m_NoteValue + ") to: " 
	      + m_InputToken.getPayload(), e);
    }

    return result;
  }
}
