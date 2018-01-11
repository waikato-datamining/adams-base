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
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import java.io.File;

import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.ConverterUtils.DataSource;
import adams.core.io.PlaceholderFile;
import adams.flow.core.Token;
import adams.flow.provenance.ActorType;
import adams.flow.provenance.Provenance;
import adams.flow.provenance.ProvenanceContainer;
import adams.flow.provenance.ProvenanceInformation;
import adams.flow.provenance.ProvenanceSupporter;

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
 * Valid options are: <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaInstancesAppend
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstancesAppend
  extends AbstractTransformer
  implements ProvenanceSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -268487303904639474L;

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
    if (result == null) {
      for (i = 0; i < inst.length - 1; i++) {
	for (n = i + 1; n < inst.length; n++) {
	  if ((msg = inst[i].equalHeadersMsg(inst[n])) != null) {
	    result = "Dataset #" + (i+1) + " and #" + (n+1) + " are not compatible:\n" + msg;
	    break;
	  }
	}
	if (result != null)
	  break;
      }
    }

    // append
    if (result == null) {
      full     = new Instances(inst[0]);
      relation = new StringBuilder(inst[0].relationName());
      for (i = 1; i < inst.length; i++) {
	relation.append("+" + inst[i].relationName());
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

  /**
   * Updates the provenance information in the provided container.
   *
   * @param cont	the provenance container to update
   */
  public void updateProvenance(ProvenanceContainer cont) {
    if (Provenance.getSingleton().isEnabled())
      cont.addProvenance(new ProvenanceInformation(ActorType.DATAGENERATOR, m_InputToken.getPayload().getClass(), this, m_OutputToken.getPayload().getClass()));
  }
}
