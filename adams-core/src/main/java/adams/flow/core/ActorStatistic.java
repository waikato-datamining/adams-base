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
 * ActorStatistic.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.NamedCounter;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.statistics.InformativeStatistic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Generates some statistics for an actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorStatistic
  implements InformativeStatistic {

  /** the overall count. */
  public static String COUNT_ACTORS = "Actors";

  /** the control actor count. */
  public static String COUNT_CONTROLACTORS = "Control actors";

  /** the standalone count. */
  public static String COUNT_STANDALONES = "Standalones";

  /** the source count. */
  public static String COUNT_SOURCES = "Sources";

  /** the transformer count. */
  public static String COUNT_TRANSFORMERS = "Transformers";

  /** the sink count. */
  public static String COUNT_SINKS = "Sinks";

  /** the statistics per actor type. */
  protected NamedCounter m_TypeStatistics;

  /** the statistics per actor class. */
  protected NamedCounter m_ClassStatistics;

  /** the actor to create the statistics for. */
  protected Actor m_Actor;

  /**
   * Initializes the statistics.
   */
  public ActorStatistic() {
    super();

    m_TypeStatistics  = new NamedCounter();
    m_ClassStatistics = new NamedCounter();
    m_Actor           = null;
  }

  /**
   * Initializes the statistics with the specified actor.
   *
   * @param actor	the actor to generate the stats for
   */
  public ActorStatistic(Actor actor) {
    this();
    setActor(actor);
  }

  /**
   * Sets the actor to generate the statistics for.
   *
   * @param value	the actor to use
   */
  public void setActor(Actor value) {
    m_Actor = value;
    calculate();
  }

  /**
   * Returns the underlying actor.
   *
   * @return		the actor, null if none set
   */
  public Actor getActor() {
    return m_Actor;
  }

  /**
   * Updates the statistics with the specified actor.
   *
   * @param actor	the actor to use
   */
  protected void update(Actor actor) {
    m_TypeStatistics.next(COUNT_ACTORS);
    if (ActorUtils.isControlActor(actor))
      m_TypeStatistics.next(COUNT_CONTROLACTORS);
    if (ActorUtils.isStandalone(actor))
      m_TypeStatistics.next(COUNT_STANDALONES);
    if (ActorUtils.isSource(actor))
      m_TypeStatistics.next(COUNT_SOURCES);
    if (ActorUtils.isTransformer(actor))
      m_TypeStatistics.next(COUNT_TRANSFORMERS);
    if (ActorUtils.isSink(actor))
      m_TypeStatistics.next(COUNT_SINKS);
    m_ClassStatistics.next(actor.getClass().getName());
  }

  /**
   * Generates the statistics.
   */
  protected void calculate() {
    List<Actor>		actors;

    // init stats
    m_TypeStatistics.clear();
    m_TypeStatistics.clear(COUNT_ACTORS);
    m_TypeStatistics.clear(COUNT_CONTROLACTORS);
    m_TypeStatistics.clear(COUNT_STANDALONES);
    m_TypeStatistics.clear(COUNT_SOURCES);
    m_TypeStatistics.clear(COUNT_TRANSFORMERS);
    m_TypeStatistics.clear(COUNT_SINKS);
    m_ClassStatistics.clear();

    if (m_Actor == null)
      return;

    actors = ActorUtils.enumerate(getActor());
    update(m_Actor);
    for (Actor actor: actors)
      update(actor);
  }

  /**
   * Returns a description for this statistic.
   *
   * @return		the description
   */
  public String getStatisticDescription() {
    if (m_Actor == null)
      return "Statistics";
    else
      return m_Actor.getName();
  }

  /**
   * Returns all the names of the available statistical values.
   *
   * @return		the enumeration of names
   */
  public Iterator<String> statisticNames() {
    List<String>	result;
    List<String>	classes;

    result = new ArrayList<String>(m_TypeStatistics.nameSet());
    Collections.sort(result);
    classes = new ArrayList<String>(m_ClassStatistics.nameSet());
    Collections.sort(classes);
    result.addAll(classes);

    return result.iterator();
  }

  /**
   * Returns the statistical value for the given statistic name.
   *
   * @param name	the name of the statistical value
   * @return		the corresponding value
   */
  public double getStatistic(String name) {
    if (m_TypeStatistics.has(name))
      return m_TypeStatistics.current(name);
    else
      return m_ClassStatistics.current(name);
  }

  /**
   * Returns a string representation of the statistic.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    StringBuilder	result;
    Iterator<String>	names;
    String		name;

    result = new StringBuilder();
    result.append(getStatisticDescription());
    result.append("\n");

    names = statisticNames();
    while (names.hasNext()) {
      name = names.next();
      result.append(name + ": " + getStatistic(name));
      result.append("\n");
    }

    return result.toString();
  }

  /**
   * Returns the content as spreadsheet.
   *
   * @return		the content
   */
  public SpreadSheet toSpreadSheet() {
    SpreadSheet		result;
    Row row;
    Iterator<String>	names;
    String		name;

    result = new SpreadSheet();

    // header
    row = result.getHeaderRow();
    row.addCell("N").setContentAsString("Name");
    row.addCell("V").setContentAsString("Value");

    // data
    names = statisticNames();
    while (names.hasNext()) {
      name = names.next();
      row  = result.addRow();
      row.addCell("N").setContentAsString(name);
      row.addCell("V").setContent(getStatistic(name));
    }

    return result;
  }
}
