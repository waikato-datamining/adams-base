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
 * AbstractActorJavaCodeProducer.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.io.FileFormatHandler;
import adams.flow.core.Actor;

/**
 * Ancestor for producers that generate Java code from actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractActorJavaCodeProducer
  extends AbstractJavaCodeProducer 
  implements FileFormatHandler {

  /** for serialization. */
  private static final long serialVersionUID = 8418718518287490700L;

  /**
   * Returns the description of the file format.
   *
   * @return		the description
   */
  public String getFormatDescription() {
    return "Java Source Code";
  }

  /**
   * Returns the default file extension (without the dot).
   *
   * @return		the default extension
   */
  public String getDefaultFormatExtension() {
    return "java";
  }

  /**
   * Returns the file extensions (without the dot).
   *
   * @return		the extensions
   */
  public String[] getFormatExtensions() {
    return new String[]{getDefaultFormatExtension()};
  }

  /**
   * The outer most variable name.
   *
   * @return		the variable name
   */
  @Override
  protected String getOuterVariableName() {
    return "actor";
  }

  /**
   * Hook-method before starting visiting options.
   */
  @Override
  protected void preProduce() {
    if (!(m_Input instanceof Actor))
      throw new IllegalArgumentException(
	  "Only " + Actor.class.getName() + " objects (and derived ones) can be processed, provided: "
	  + m_Input.getClass().getName());

    super.preProduce();
  }
}
