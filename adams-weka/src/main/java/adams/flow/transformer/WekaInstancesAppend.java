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
 * WekaInstancesAppend.java
 * Copyright (C) 2012-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.LenientModeSupporter;
import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;
import adams.data.instances.Compatibility;
import adams.flow.core.Token;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;

/**
 <!-- globalinfo-start -->
 * Creates one large dataset by appending all one after the other.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String[]<br>
 * &nbsp;&nbsp;&nbsp;java.io.File[]<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances[]<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instances<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: WekaInstancesAppend
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-lenient &lt;boolean&gt; (property: lenient)
 * &nbsp;&nbsp;&nbsp;If enabled, then only the attribute types must match, not also the names.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class WekaInstancesAppend
  extends AbstractTransformer
  implements LenientModeSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -268487303904639474L;

  /** whether to be lenient. */
  protected boolean m_Lenient;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Creates one large dataset by appending all one after the other.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "lenient", "lenient",
      false);
  }

  /**
   * Sets whether lenient, ie only attribute types must match.
   *
   * @param value	true if lenient
   */
  public void setLenient(boolean value) {
    m_Lenient = value;
    reset();
  }

  /**
   * Returns whether lenient, ie only attribute types must match.
   *
   * @return		true if lenient
   */
  public boolean getLenient() {
    return m_Lenient;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lenientTipText() {
    return "If enabled, then only the attribute types must match, not also the names.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "lenient", m_Lenient, "lenient", "");
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->java.lang.String[].class, java.io.File[].class, weka.core.Instances[].class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{String[].class, File[].class, Instances[].class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->weka.core.Instances.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Instances.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    String[]		filesStr;
    File[]		files;
    int			i;
    int			n;
    Instances[]		inst;
    Instances		full;
    String		msg;
    StringBuilder	relation;
    double[]		values;

    result = null;

    // get filenames
    files = null;
    inst  = null;
    if (m_InputToken.getPayload() instanceof String[]) {
      filesStr = (String[]) m_InputToken.getPayload();
      files    = new File[filesStr.length];
      for (i = 0; i < filesStr.length; i++)
	files[i] = new PlaceholderFile(filesStr[i]);
    }
    else if (m_InputToken.getPayload() instanceof File[]) {
      files = (File[]) m_InputToken.getPayload();
    }
    else if (m_InputToken.getPayload() instanceof Instances[]) {
      inst = (Instances[]) m_InputToken.getPayload();
    }
    else {
      throw new IllegalStateException("Unhandled input type: " + m_InputToken.getPayload().getClass());
    }

    // load data?
    if (files != null) {
      inst = new Instances[files.length];
      for (i = 0; i < files.length; i++) {
	try {
	  inst[i] = DataSource.read(files[i].getAbsolutePath());
	}
	catch (Exception e) {
	  result = handleException("Failed to load dataset: " + files[i], e);
	  break;
	}
      }
    }

    // test compatibility
    if ((result == null) && (inst != null)) {
      for (i = 0; i < inst.length - 1; i++) {
	for (n = i + 1; n < inst.length; n++) {
	  if ((msg = Compatibility.isCompatible(inst[i], inst[n], !m_Lenient)) != null) {
	    result = "Dataset #" + (i+1) + " and #" + (n+1) + " are not compatible:\n" + msg;
	    break;
	  }
	}
	if (result != null)
	  break;
      }
    }

    // append
    if ((result == null) && (inst != null)) {
      full     = new Instances(inst[0]);
      relation = new StringBuilder(inst[0].relationName());
      for (i = 1; i < inst.length; i++) {
	relation.append("+").append(inst[i].relationName());
	for (Instance row: inst[i]) {
	  values = row.toDoubleArray();
	  for (n = 0; n < values.length; n++) {
	    if (row.attribute(n).isString())
	      values[n] = full.attribute(n).addStringValue(row.stringValue(n));
	    else if (row.attribute(n).isRelationValued())
	      values[n] = full.attribute(n).addRelation(row.relationalValue(n));
	  }
	  if (row instanceof SparseInstance)
	    row = new SparseInstance(row.weight(), values);
	  else
	    row = new DenseInstance(row.weight(), values);
	  full.add(row);
	}
      }
      full.setRelationName(relation.toString());
      m_OutputToken = new Token(full);
    }

    return result;
  }
}
