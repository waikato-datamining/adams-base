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
 * WekaStoreInstance.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import weka.core.Instance;
import weka.core.Instances;
import adams.core.QuickInfoHelper;
import adams.flow.control.Storage;
import adams.flow.control.StorageName;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Appends the incoming weka.core.Instance to the dataset in storage. If no dataset currently stored under this name, it will get automatically added. The dataset after the update operation is then forwarded as token.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;weka.core.Instance<br>
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
 * &nbsp;&nbsp;&nbsp;default: WekaStoreInstance
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
 * <pre>-dataset &lt;adams.flow.control.StorageName&gt; (property: dataset)
 * &nbsp;&nbsp;&nbsp;The name of the dataset in internal storage to append the incoming data 
 * &nbsp;&nbsp;&nbsp;to.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaStoreInstance
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = 3077398394768688867L;
  
  /** the dataset to append to. */
  protected StorageName m_Dataset;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Appends the incoming " + Instance.class.getName() + " to the dataset "
        + "in storage. If no dataset currently stored under this name, it "
        + "will get automatically added. The dataset after the update "
        + "operation is then forwarded as token.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "dataset", "dataset",
	    new StorageName());
  }

  /**
   * Sets the name of the dataset in internal storage to append to.
   *
   * @param value	the name
   */
  public void setDataset(StorageName value) {
    m_Dataset = value;
    reset();
  }

  /**
   * Returns the name of the dataset in internal storage to append to.
   *
   * @return		the name
   */
  public StorageName getDataset() {
    return m_Dataset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String datasetTipText() {
    return "The name of the dataset in internal storage to append the incoming data to.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "dataset", m_Dataset);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->weka.core.Instance.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Instance.class};
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
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    
    result = super.setUp();
    
    if (result == null) {
      if (m_Dataset.isEmpty())
	result = "No storage name set for dataset!";
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
    String	result;
    Instance	inst;
    Instances	data;
    Storage	storage;

    result = null;

    inst    = (Instance) m_InputToken.getPayload();
    storage = getStorageHandler().getStorage();

    // dataset present?
    if (!storage.has(m_Dataset)) {
      data = new Instances(inst.dataset(), 0);
      storage.put(m_Dataset, data);
      if (isLoggingEnabled())
	getLogger().info("Adding dataset to storage: " + m_Dataset);
    }
    else {
      data = (Instances) storage.get(m_Dataset);
      if (isLoggingEnabled())
	getLogger().info("Dataset present in storage: " + m_Dataset);
    }

    data.add(inst);
    storage.put(m_Dataset, data);
    if (isLoggingEnabled())
      getLogger().info("Added instance to storage: " + m_Dataset);
    
    // broadcast data
    m_OutputToken = new Token(data);

    return result;
  }
}
