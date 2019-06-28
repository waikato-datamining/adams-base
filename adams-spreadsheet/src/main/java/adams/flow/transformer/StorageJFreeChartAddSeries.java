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
 * StorageJFreeChartAddSeries.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.spreadsheet.SpreadSheet;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUpdater;
import adams.flow.core.Token;
import adams.flow.core.Unknown;
import adams.gui.visualization.jfreechart.dataset.AbstractDatasetGenerator;
import adams.gui.visualization.jfreechart.dataset.DefaultXY;
import org.jfree.data.general.Dataset;

/**
 <!-- globalinfo-start -->
 * Generates a series from the incoming spreadsheet and adds it to the specified JFreeChart dataset in storage.<br>
 * After inserting the object successfully, just forwards the object.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: StorageJFreeChartAddSeries
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
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-storageName &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the storage item that represents the collection to update.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 * <pre>-dataset &lt;adams.gui.visualization.jfreechart.dataset.AbstractDatasetGenerator&gt; (property: dataset)
 * &nbsp;&nbsp;&nbsp;The dataset generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.jfreechart.dataset.DefaultXY
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class StorageJFreeChartAddSeries
  extends AbstractTransformer
  implements StorageUpdater {

  private static final long serialVersionUID = -4381778255320714964L;

  /** the name of the dataset in storage. */
  protected StorageName m_StorageName;

  /** the dataset generator. */
  protected AbstractDatasetGenerator m_Dataset;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Generates a series from the incoming spreadsheet and adds it to the specified JFreeChart dataset in storage.\n"
	+ "After inserting the object successfully, just forwards the object.";
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  public Class[] getClassCrossReferences() {
    return new Class[]{CollectionInsert.class};
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storageName", "storageName",
      new StorageName());

    m_OptionManager.add(
      "dataset", "dataset",
      new DefaultXY());
  }

  /**
   * Sets the storage item name that represents the collection to update.
   *
   * @param value	the storage name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the storage item name that represents the collection to update.
   *
   * @return		the storage name
   */
  public StorageName getStorageName() {
    return m_StorageName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String storageNameTipText() {
    return "The name of the storage item that represents the collection to update.";
  }

  /**
   * Sets the dataset generator.
   *
   * @param value	the generator
   */
  public void setDataset(AbstractDatasetGenerator value) {
    m_Dataset = value;
    reset();
  }

  /**
   * Returns the dataset generator.
   *
   * @return		the generator
   */
  public AbstractDatasetGenerator getDataset() {
    return m_Dataset;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String datasetTipText() {
    return "The dataset generator to use.";
  }

  /**
   * Returns whether storage items are being updated.
   *
   * @return		true if storage items are updated
   */
  public boolean isUpdatingStorage() {
    return !getSkip();
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "storageName", m_StorageName, "storage: ");
    result += QuickInfoHelper.toString(this, "dataset", m_Dataset, ", dataset: ");

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
    SpreadSheet 	sheet;
    Dataset 		dataset;

    result = null;

    sheet   = m_InputToken.getPayload(SpreadSheet.class);
    dataset = null;
    if (!getStorageHandler().getStorage().has(m_StorageName))
      result = "Dataset not available from storage: " + m_StorageName;
    else
      dataset = (Dataset) getStorageHandler().getStorage().get(m_StorageName);

    if (result == null) {
      if (!dataset.getClass().equals(m_Dataset.generates())) {
	result = "Dataset in storage (" + m_StorageName + ") is of type "
	  + Utils.classToString(dataset) + ", but dataset generator produces "
	  + Utils.classToString(m_Dataset.generates()) + "!";
      }
      else {
        dataset = m_Dataset.addSeries(dataset, sheet);
        getStorageHandler().getStorage().put(m_StorageName, dataset);
	m_OutputToken = new Token(sheet);
      }
    }

    return result;
  }
}
