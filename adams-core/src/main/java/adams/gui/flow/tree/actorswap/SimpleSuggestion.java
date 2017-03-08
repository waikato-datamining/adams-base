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
 * SimpleSuggestion.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tree.actorswap;

import adams.core.Properties;
import adams.flow.core.Actor;
import adams.flow.sink.Display;
import adams.flow.sink.DumpFile;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Suggests actors based on the {@link #PROPERTIES_FILENAME} rules.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleSuggestion
  extends AbstractActorSwapSuggestion {

  private static final long serialVersionUID = -2879844263173160775L;

  /** the properties file to load. */
  public final static String PROPERTIES_FILENAME = "adams/gui/flow/tree/actorswap/SimpleSuggestion.props";

  /** the properties. */
  protected static Properties m_Properties;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Suggests " + DumpFile.class + " as swap partner for " + Display.class.getName() + ".";
  }

  /**
   * Performs the actual search for candidates.
   *
   * @param current	the actor to find potential swaps for
   * @return		the list of potential swaps
   */
  @Override
  protected List<Actor> doSuggest(Actor current) {
    List<Actor>		result;
    Properties		props;
    String		name;

    result = new ArrayList<>();

    props = getProperties();
    name  = current.getClass().getName();
    if (props.hasKey(name)) {
      try {
	result.add((Actor) Class.forName(props.getProperty(name)).newInstance());
      }
      catch (Exception e) {
	getLogger().log(Level.SEVERE, "Failed to instantiate suggestion for '" + name + "': " + props.getProperty(name), e);
      }
    }

    return result;
  }

  /**
   * Returns the properties with the suggestions.
   *
   * @return		the suggestions
   */
  protected static synchronized Properties getProperties() {
    if (m_Properties == null) {
      try {
	m_Properties = Properties.read(PROPERTIES_FILENAME);
      }
      catch (Exception e) {
	System.err.println("Failed to read: " + PROPERTIES_FILENAME);
	e.printStackTrace();
	m_Properties = new Properties();
      }
    }
    return m_Properties;
  }
}
