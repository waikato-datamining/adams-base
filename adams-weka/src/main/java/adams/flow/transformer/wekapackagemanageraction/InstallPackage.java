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
 * InstallPackage.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer.wekapackagemanageraction;

import adams.core.MessageCollection;
import weka.core.packageManagement.Package;

import java.util.logging.Level;

/**
 * Action that installs the incoming package.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class InstallPackage
  extends AbstractWekaPackageManagerAction{

  private static final long serialVersionUID = 551922326118868830L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Action that installs the incoming package.";
  }

  /**
   * The types of data the action accepts.
   *
   * @return the input types
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Package.class};
  }

  /**
   * The types of data the action generates.
   *
   * @return the output types
   */
  @Override
  public Class[] generates() {
    return new Class[]{Boolean.class};
  }

  /**
   * Executes the action.
   *
   * @param input  the input to process
   * @param errors for collecting errors
   * @return the generated output, null if failed to generated
   */
  @Override
  public Object doExecute(Object input, MessageCollection errors) {
    Package		pkg;

    pkg = (Package) input;
    try {
      getLogger().info("Installing: " + pkg.getName());
      pkg.install();
      return true;
    }
    catch (Exception e) {
      getLogger().log(Level.WARNING, "Failed to install: " + pkg.getName(), e);
      return false;
    }
  }
}
