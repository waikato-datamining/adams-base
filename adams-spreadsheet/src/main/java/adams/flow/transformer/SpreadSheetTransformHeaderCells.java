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
 * SpreadSheetTransformHeaderCells.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer;

import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;

import adams.core.DateTime;
import adams.core.QuickInfoHelper;
import adams.core.Time;
import adams.core.Utils;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorUser;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.Compatibility;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Transforms header cells with a callable transformer.<br/>
 * In case of transformers having Object or Unknown in their types of classes that they accept, no proper type can be inferred automatically. Therefore it is recommended to manually enforce the 'input type'.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br/>
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
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetTransformHeaderCells
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
 * <pre>-no-copy &lt;boolean&gt; (property: noCopy)
 * &nbsp;&nbsp;&nbsp;If enabled, no copy of the spreadsheet is created before processing it.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-force-input-type &lt;boolean&gt; (property: forceInputType)
 * &nbsp;&nbsp;&nbsp;If enabled, the input type is forced to a user-specified type, rather than
 * &nbsp;&nbsp;&nbsp;trying to determine type based on data types that the callable transformer
 * &nbsp;&nbsp;&nbsp;accepts.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-input-type &lt;MISSING|STRING|BOOLEAN|LONG|DOUBLE|DATE|DATETIME|TIME|OBJECT&gt; (property: inputType)
 * &nbsp;&nbsp;&nbsp;The input type to use in case the input type is enforced.
 * &nbsp;&nbsp;&nbsp;default: STRING
 * </pre>
 *
 * <pre>-skip-missing &lt;boolean&gt; (property: skipMissing)
 * &nbsp;&nbsp;&nbsp;If enabled, missing cells are skipped.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 *
 * <pre>-missing-replacement-value &lt;java.lang.String&gt; (property: missingReplacementValue)
 * &nbsp;&nbsp;&nbsp;The string representation of the value to use for replacing missing values.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-missing-replacement-type &lt;MISSING|STRING|BOOLEAN|LONG|DOUBLE|DATE|DATETIME|TIME|OBJECT&gt; (property: missingReplacementType)
 * &nbsp;&nbsp;&nbsp;The data type to use for the replacement value for missing values.
 * &nbsp;&nbsp;&nbsp;default: STRING
 * </pre>
 *
 * <pre>-transformer &lt;adams.flow.core.CallableActorReference&gt; (property: transformer)
 * &nbsp;&nbsp;&nbsp;The callable transformer to apply to the header cells.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetTransformHeaderCells
  extends AbstractInPlaceSpreadSheetTransformer
  implements CallableActorUser {

  /** for serialization. */
  private static final long serialVersionUID = -2533024333073383813L;

  /** the key for backing up the callable actor. */
  public final static String BACKUP_CALLABLEACTOR = "callable actor";

  /** whether to force the input type. */
  protected boolean m_ForceInputType;

  /** the input type. */
  protected ContentType m_InputType;

  /** whether to skip missing cells. */
  protected boolean m_SkipMissing;

  /** the value to use instead of missing. */
  protected String m_MissingReplacementValue;

  /** the data type of the replacement value. */
  protected ContentType m_MissingReplacementType;

  /** the callable transformer to apply to the cells. */
  protected CallableActorReference m_Transformer;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /** the callable actor. */
  protected AbstractActor m_CallableActor;

  /** used for parsing missing value replacement strings. */
  protected Cell m_Cell;

  /** for compatibility comparisons. */
  protected Compatibility m_Compatibility;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"Transforms header cells with a callable transformer.\n"
	+ "In case of transformers having " + Object.class.getSimpleName()
	+ " or " + Unknown.class.getSimpleName() + " in their types of "
	+ "classes that they accept, no proper type can be inferred automatically. "
	+ "Therefore it is recommended to manually enforce the 'input type'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "force-input-type", "forceInputType",
	    false);

    m_OptionManager.add(
	    "input-type", "inputType",
	    ContentType.STRING);

    m_OptionManager.add(
	    "skip-missing", "skipMissing",
	    true);

    m_OptionManager.add(
	    "missing-replacement-value", "missingReplacementValue",
	    "");

    m_OptionManager.add(
	    "missing-replacement-type", "missingReplacementType",
	    ContentType.STRING);

    m_OptionManager.add(
	    "transformer", "transformer",
	    new CallableActorReference("unknown"));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper        = new CallableActorHelper();
    m_Cell          = null;
    m_Compatibility = null;
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_CallableActor = null;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "transformer", m_Transformer, "transformer: ");
    result += QuickInfoHelper.toString(this, "skipMissing", m_SkipMissing, "skip missing", ", ");
    result += QuickInfoHelper.toString(this, "forceInputType", m_ForceInputType, "force input type", ", ");
    result += QuickInfoHelper.toString(this, "noCopy", m_NoCopy, "no copy", ", ");

    return result;
  }

  /**
   * Sets whether to force the input type.
   *
   * @param value	true if to force type
   */
  public void setForceInputType(boolean value) {
    m_ForceInputType = value;
    reset();
  }

  /**
   * Returns whether to force the input type.
   *
   * @return		true type forced
   */
  public boolean getForceInputType() {
    return m_ForceInputType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String forceInputTypeTipText() {
    return
	"If enabled, the input type is forced to a user-specified type, "
	+ "rather than trying to determine type based on data types that "
	+ "the callable transformer accepts.";
  }

  /**
   * Sets the input type to enforce.
   *
   * @param value	the input type
   */
  public void setInputType(ContentType value) {
    m_InputType = value;
    reset();
  }

  /**
   * Returns the input type to enforce.
   *
   * @return		the input type
   */
  public ContentType getInputType() {
    return m_InputType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String inputTypeTipText() {
    return "The input type to use in case the input type is enforced.";
  }

  /**
   * Sets whether to skip missing cells.
   *
   * @param value	true if to skip missing cells
   */
  public void setSkipMissing(boolean value) {
    m_SkipMissing = value;
    reset();
  }

  /**
   * Returns whether missing cells are skipped.
   *
   * @return		true if missing cells are skipped
   */
  public boolean getSkipMissing() {
    return m_SkipMissing;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipMissingTipText() {
    return "If enabled, missing cells are skipped.";
  }

  /**
   * Sets the replacement value for missing values.
   *
   * @param value	the replacement value
   */
  public void setMissingReplacementValue(String value) {
    m_MissingReplacementValue = value;
    reset();
  }

  /**
   * Returns the replacement value for missing values.
   *
   * @return		the replacement value
   */
  public String getMissingReplacementValue() {
    return m_MissingReplacementValue;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingReplacementValueTipText() {
    return "The string representation of the value to use for replacing missing values.";
  }

  /**
   * Sets the data type of the replacement value.
   *
   * @param value	the replacement type
   */
  public void setMissingReplacementType(ContentType value) {
    m_MissingReplacementType = value;
    reset();
  }

  /**
   * Returns the data type of the replacement value.
   *
   * @return		the replacement type
   */
  public ContentType getMissingReplacementType() {
    return m_MissingReplacementType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String missingReplacementTypeTipText() {
    return "The data type to use for the replacement value for missing values.";
  }

  /**
   * Sets the reference to the callable transformer.
   *
   * @param value	the reference
   */
  public void setTransformer(CallableActorReference value) {
    m_Transformer = value;
    reset();
  }

  /**
   * Returns the reference to the callable transformer.
   *
   * @return		the conversion
   */
  public CallableActorReference getTransformer() {
    return m_Transformer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String transformerTipText() {
    return "The callable transformer to apply to the header cells.";
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected AbstractActor findCallableActor() {
    return m_Helper.findCallableActorRecursive(this, getTransformer());
  }

  /**
   * Checks whether a reference to the callable actor is currently available.
   *
   * @return		true if a reference is available
   * @see		#getCallableActor()
   */
  public boolean hasCallableActor() {
    return (m_CallableActor != null);
  }

  /**
   * Returns the currently set callable actor.
   *
   * @return		the actor, can be null
   */
  @Override
  public AbstractActor getCallableActor() {
    return m_CallableActor;
  }

  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    pruneBackup(BACKUP_CALLABLEACTOR);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_CallableActor != null)
      result.put(BACKUP_CALLABLEACTOR, m_CallableActor);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    super.restoreState(state);

    if (state.containsKey(BACKUP_CALLABLEACTOR)) {
      m_CallableActor = (AbstractActor) state.get(BACKUP_CALLABLEACTOR);
      state.remove(BACKUP_CALLABLEACTOR);
    }
  }

  /**
   * Configures the callable actor.
   *
   * @return		null if successful, otherwise error message
   */
  protected String setUpCallableActor() {
    String		result;
    HashSet<String>	variables;

    result = null;

    m_CallableActor = findCallableActor();
    if (m_CallableActor == null) {
      result = "Couldn't find callable transformer '" + getTransformer() + "'!";
    }
    else {
      if (ActorUtils.isTransformer(m_CallableActor)) {
	variables = findVariables(m_CallableActor);
	m_DetectedVariables.addAll(variables);
	if (m_DetectedVariables.size() > 0)
	  getVariables().addVariableChangeListener(this);
      }
      else {
	result = "Callable actor '" + getTransformer() + "' is not a transformer!";
      }
    }

    return result;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    String	variable;

    result = super.setUp();

    if (result == null) {
      // do we have to wait till execution time because of attached variable?
      variable = getOptionManager().getVariableForProperty("transformer");
      if (variable == null)
	result = setUpCallableActor();
    }

    return result;
  }

  /**
   * Applies the transformer to the cell.
   *
   * @param cell	the cell to transform
   * @param sheet	the sheet to process
   * @return		null if successful, otherwise error message
   */
  protected String transformCell(Cell cell, SpreadSheet sheet) {
    String		result;
    Class[]		classIn;
    Class[]		classOut;
    Object		input;
    Object		output;

    result = null;
    input  = null;
    output = null;

    if (m_Cell == null)
      m_Cell = new Cell(null);
    if (m_Compatibility == null)
      m_Compatibility = new Compatibility();

    // skip missing cells
    if (cell.isMissing()) {
      if (m_SkipMissing)
	return null;
      else
	input = m_Cell.parseContent(m_MissingReplacementValue, m_MissingReplacementType);
    }


    if (m_ForceInputType) {
      switch (m_InputType) {
	case BOOLEAN:
	  classIn = new Class[]{Boolean.class};
	  break;
	case LONG:
	  classIn = new Class[]{Long.class};
	  break;
	case DOUBLE:
	  classIn = new Class[]{Double.class};
	  break;
	case TIME:
	  classIn = new Class[]{Time.class};
	  break;
	case DATE:
	  classIn = new Class[]{Date.class};
	  break;
	case DATETIME:
	  classIn = new Class[]{DateTime.class};
	  break;
	case OBJECT:
	  classIn = new Class[]{Object.class};
	  break;
	default:
	  classIn = new Class[]{String.class};
      }
    }
    else {
      classIn = ((InputConsumer) m_CallableActor).accepts();
    }

    if (input == null) {
      if (m_Compatibility.isCompatible(new Class[]{Double.class}, classIn))
	input = cell.toDouble();
      else if (m_Compatibility.isCompatible(new Class[]{Integer.class}, classIn))
	input = cell.toLong().intValue();
      else if (m_Compatibility.isCompatible(new Class[]{Long.class}, classIn))
	input = cell.toLong();
      else if (m_Compatibility.isCompatible(new Class[]{Date.class}, classIn))
	input = cell.toDate();
      else if (m_Compatibility.isCompatible(new Class[]{DateTime.class}, classIn))
	input = cell.toDateTime();
      else if (m_Compatibility.isCompatible(new Class[]{Time.class}, classIn))
	input = cell.toTime();
      else if (m_Compatibility.isCompatible(new Class[]{String.class}, classIn))
	input = cell.getContent();
      else
	result = "Don't know how to get cell value for transformation input type:\n"
	    + Utils.classesToString(classIn)
	    + "/"
	    + ((input != null) ? input.getClass().getName() : "null")
	    + "/" + cell.getContent();
    }

    if (result == null) {
      ((InputConsumer) m_CallableActor).input(new Token(input));
      result = m_CallableActor.execute();
    }

    if (result == null) {
      classOut = ((OutputProducer) m_CallableActor).generates();
      output   = ((OutputProducer) m_CallableActor).output();
      if (output != null)
	output = ((Token) output).getPayload();

      if (output instanceof Double)
	cell.setContent((Double) output);
      else if (output instanceof Integer)
	cell.setContent((Integer) output);
      else if (output instanceof Long)
	cell.setContent((Long) output);
      else if (output instanceof DateTime)
	cell.setContent((DateTime) output);
      else if (output instanceof Time)
	cell.setContent((Time) output);
      else if (output instanceof Date)
	cell.setContent((Date) output);
      else if (output instanceof String)
	cell.setContentAsString((String) output);
      else
	result = "Don't know how to set cell value for transformation output type:\n"
	    + Utils.classesToString(classOut)
	    + "/"
	    + ((output != null) ? output.getClass().getName() : "null") + "\n"
	    + "The input that resulted in this output:\n"
	    + Utils.classesToString(classIn)
	    + "/"
	    + ((input != null) ? input.getClass().getName() : "null")
	    + "/" + cell.getContent();
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    SpreadSheet		sheetOld;
    SpreadSheet		sheetNew;

    result = null;

    // is variable attached?
    if (m_CallableActor == null)
      result = setUpCallableActor();

    sheetOld = (SpreadSheet) m_InputToken.getPayload();
    if (m_NoCopy)
      sheetNew = sheetOld;
    else
      sheetNew = sheetOld.getClone();

    if (!m_CallableActor.getSkip() && !m_CallableActor.isStopped() && !m_Stopped) {
      synchronized(m_CallableActor) {
	for (Cell cell: sheetNew.getHeaderRow().cells()) {
	  result = transformCell(cell, sheetNew);
	  if (result != null)
	    break;
	  if (m_Stopped)
	    break;
	}
      }
    }

    if ((result == null) && !m_Stopped)
      m_OutputToken = new Token(sheetNew);

    return result;
  }
}
