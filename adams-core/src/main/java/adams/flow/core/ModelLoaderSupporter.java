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
 * ModelLoaderSupporter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.io.ModelFileHandler;
import adams.core.io.PlaceholderFile;
import adams.flow.control.StorageName;
import adams.flow.core.AbstractModelLoader.ModelLoadingType;

/**
 * Interface for classes that support model loading.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see AbstractModelLoader
 */
public interface ModelLoaderSupporter
  extends ModelFileHandler {

  /**
   * Sets the loading type. In case of {@link ModelLoadingType#AUTO}, first
   * file, then callable actor, then storage.
   *
   * @param value	the type
   */
  public void setModelLoadingType(ModelLoadingType value);

  /**
   * Returns the loading type. In case of {@link ModelLoadingType#AUTO}, first
   * file, then callable actor, then storage.
   *
   * @return		the type
   */
  public ModelLoadingType getModelLoadingType();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelLoadingTypeTipText();

  /**
   * Sets the file to load the model from.
   *
   * @param value	the model file
   */
  public void setModelFile(PlaceholderFile value);

  /**
   * Returns the file to load the model from.
   *
   * @return		the model file
   */
  public PlaceholderFile getModelFile();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelFileTipText();

  /**
   * Sets the callable actor to obtain the model from.
   *
   * @param value	the actor reference
   */
  public void setModelActor(CallableActorReference value);

  /**
   * Returns the callable actor to obtain the model from.
   *
   * @return		the actor reference
   */
  public CallableActorReference getModelActor();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelActorTipText();

  /**
   * Sets the storage item name to get the model from.
   *
   * @param value	the storage name
   */
  public void setModelStorage(StorageName value);

  /**
   * Returns the storage item name to get the model from.
   *
   * @return		the storage name
   */
  public StorageName getModelStorage();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelStorageTipText();
}
