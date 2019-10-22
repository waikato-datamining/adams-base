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
 * MapToWekaInstance.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.flow.control.StorageHandler;
import adams.flow.control.StorageName;
import adams.flow.control.StorageUser;
import adams.flow.core.Actor;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Map;

/**
 <!-- globalinfo-start -->
 * Converts a map into a Weka Instance, using the provided storage object (Instances) as template.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-storage-name &lt;adams.flow.control.StorageName&gt; (property: storageName)
 * &nbsp;&nbsp;&nbsp;The name of the stored Instances object to use as template.
 * &nbsp;&nbsp;&nbsp;default: storage
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MapToWekaInstance
  extends AbstractConversion
  implements StorageUser {

  private static final long serialVersionUID = 5974871505196882936L;

  /** the name of the stored value. */
  protected StorageName m_StorageName;

  /** the template. */
  protected transient Instances m_Template;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a map into a Weka Instance, using the provided storage object (Instances) as template.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "storage-name", "storageName",
      new StorageName());
  }

  /**
   * Sets the name of the stored Instances object to use as template.
   *
   * @param value	the name
   */
  public void setStorageName(StorageName value) {
    m_StorageName = value;
    reset();
  }

  /**
   * Returns the name of the stored Instances object to use as template.
   *
   * @return		the name
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
    return "The name of the stored Instances object to use as template.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Map.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Instance.class;
  }

  /**
   * Returns whether storage items are being used.
   *
   * @return		true if storage items are used
   */
  @Override
  public boolean isUsingStorage() {
    return true;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "template", m_Template, "template: ");
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Instance 		result;
    double[]		values;
    Object		value;
    Map			map;
    String		key;
    Attribute		att;
    int			i;

    map = (Map) m_Input;

    // obtain template
    if (m_Template == null) {
      if (!(getOwner() instanceof Actor))
        throw new IllegalStateException("Owner does not have access to storage? Must implement " + Utils.classToString(StorageHandler.class) + "!");
      m_Template = (Instances) ((Actor) getOwner()).getStorageHandler().getStorage().get(m_StorageName);
      if (m_Template == null)
        throw new IllegalStateException("Failed to obtain Instances template from storage: " + m_StorageName);
    }

    values = new double[m_Template.numAttributes()];
    for (i = 0; i < values.length; i++)
      values[i] = weka.core.Utils.missingValue();
    for (Object keyObj: map.keySet()) {
      value = map.get(keyObj);
      if (value == null)
        continue;
      key = "" + keyObj;
      att = m_Template.attribute(key);
      if (att != null) {
        switch (att.type()) {
	  case Attribute.NUMERIC:
	    values[att.index()] = ((Number) value).doubleValue();
	    break;
	  case Attribute.NOMINAL:
	    i = att.indexOfValue("" + value);
	    if (i > -1)
	      values[att.index()] = i;
	    break;
	  case Attribute.STRING:
	    values[att.index()] = att.addStringValue("" + value);
	    break;
	  case Attribute.DATE:
	    values[att.index()] = att.parseDate("" + value);
	    break;
	  default:
	    throw new IllegalStateException("Unhandled attribute type: " + Attribute.typeToString(att.type()));
	}
      }
      else {
        throw new IllegalStateException("Key not an attribute in template: " + key + "\n" + m_Template);
      }
    }

    result = new DenseInstance(1.0, values);
    result.setDataset(m_Template);

    return result;
  }
}
