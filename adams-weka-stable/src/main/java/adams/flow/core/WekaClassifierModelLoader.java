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
 * WekaClassifierModelLoader.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.MessageCollection;
import adams.core.SerializationHelper;
import adams.core.Utils;
import adams.flow.container.AbstractContainer;
import adams.flow.container.WekaModelContainer;
import weka.classifiers.Classifier;

/**
 * Manages classifier models.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WekaClassifierModelLoader
  extends AbstractModelLoader<Classifier> {

  private static final long serialVersionUID = -8296159861720133340L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Manages Weka Classifier models.";
  }

  /**
   * Deserializes the model file.
   *
   * @param errors	for collecting errors
   * @return		the object read from the file, null if failed
   */
  @Override
  protected Object deserializeFile(MessageCollection errors) {
    Object	result;
    Object[]	objs;

    result = null;

    try {
      objs = SerializationHelper.readAll(m_ModelFile.getAbsolutePath());
      for (Object obj: objs) {
        if (obj instanceof Classifier) {
          result = obj;
          break;
	}
      }
      if (result == null)
        errors.add("Failed to locate a " + Utils.classToString(Classifier.class)
	  + " object in the objects loaded from: " + m_ModelFile);
    }
    catch (Exception e) {
      errors.add("Failed to deserialize '" + m_ModelFile + "': ", e);
    }

    return result;
  }

  /**
   * Retrieves the model from the container.
   *
   * @param cont	the container to get the model from
   * @param errors	for collecting errors
   * @return		the model, null if not in container
   */
  @Override
  protected Classifier getModelFromContainer(AbstractContainer cont, MessageCollection errors) {
    if (cont instanceof WekaModelContainer)
      return (Classifier) cont.getValue(WekaModelContainer.VALUE_MODEL);

    unhandledContainer(cont, errors);
    return null;
  }
}
