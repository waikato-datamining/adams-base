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
 * DatasetHelper.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.evaluation;

import adams.gui.tools.wekainvestigator.data.DataContainer;

import javax.swing.ComboBoxModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class for dealing with datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DatasetHelper {

  /**
   * Checks whether the data has changed and the model needs updating.
   *
   * @param newDatasets		the new list of datasets
   * @param currentModel	the current model
   * @return			true if changed
   */
  public static boolean hasDataChanged(List<String> newDatasets, ComboBoxModel<String> currentModel) {
    boolean	result;
    int		i;
    Set<String> setDatasets;
    Set<String>	setModel;

    setDatasets = new HashSet<>(newDatasets);
    setModel    = new HashSet<>();
    for (i = 0; i < currentModel.getSize(); i++)
      setModel.add(currentModel.getElementAt(i));

    // different datasets?
    result = (setDatasets.size() != setModel.size())
      || !(setDatasets.containsAll(setModel) && setModel.containsAll(setDatasets));

    // different order?
    if (!result) {
      for (i = 0; i < newDatasets.size(); i++) {
	if (!newDatasets.get(i).equals(currentModel.getElementAt(i))) {
	  result = true;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Determines the index of the old dataset name in the current dataset model.
   *
   * @param conts	the list of data containers to use
   * @param oldDataset	the old dataset to look for
   * @return		the index, -1 if not found
   */
  public static int indexOfDataset(List<DataContainer> conts, String oldDataset) {
    int 		result;
    int			i;
    DataContainer	data;
    String		idStr;
    int			id;

    result = -1;

    if (oldDataset != null) {
      // get ID
      idStr = oldDataset.replaceAll(":.*", "");
      try {
	id = Integer.parseInt(idStr);
      }
      catch (Exception e) {
	id = -1;
      }

      // try ID
      for (i = 0; i < conts.size(); i++) {
	data = conts.get(i);
	if (data.getID() == id) {
	  result = i;
	  break;
	}
      }

      // try relationname
      if (result == -1) {
	oldDataset = oldDataset.replaceAll("^[0-9]+: ", "");
	for (i = 0; i < conts.size(); i++) {
	  data = conts.get(i);
	  if (data.getData().relationName().equals(oldDataset)) {
	    result = i;
	    break;
	  }
	}
      }
    }

    return result;
  }

  /**
   * Generates the list of datasets for a combobox.
   *
   * @param conts	the list of data containers to use
   * @return		the list
   */
  public static List<String> generateDatasetList(List<DataContainer> conts) {
    List<String> 	result;
    int			i;
    DataContainer 	data;

    result = new ArrayList<>();
    for (i = 0; i < conts.size(); i++) {
      data = conts.get(i);
      result.add(data.getID() + ": " + data.getData().relationName());
    }

    return result;
  }
}
