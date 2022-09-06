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
 * MostCommon.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.flow.tree.record.add;

import adams.core.NamedCounter;
import adams.core.Range;
import adams.core.io.FileUtils;
import adams.data.io.input.CsvSpreadSheetReader;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import adams.flow.core.Actor;
import adams.gui.core.DelayedActionRunnable;
import adams.gui.flow.tree.Node;
import adams.gui.flow.tree.TreeOperations;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Maintains a top X list of actors that were added.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class MostCommon
    extends AbstractRecordActorAdded {

  private static final long serialVersionUID = -546112832751723598L;

  /** the file to write the "add" history to .*/
  public final static String FILENAME = "FlowMostCommonActors.csv";

  /** for delaying the saving. */
  protected static DelayedActionRunnable m_SaveRunnable;

  /** the threading for saving. */
  protected static Thread m_SaveThread;

  /** the count per actor. */
  protected static NamedCounter m_Counter;

  /**
   * Returns whether the recording is active.
   *
   * @param added the node that got added (for context)
   * @return true if active
   */
  @Override
  public boolean isEnabled(Node added) {
    return added.getOwner().getRecordAdd();
  }

  /**
   * Returns the filename for storing the data.
   *
   * @return		the filename
   */
  protected static String getFilename() {
    return Environment.getInstance().getHome() + File.separator + FILENAME;
  }

  /**
   * Initializes the counter.
   */
  protected static void initCounter() {
    CsvSpreadSheetReader	reader;
    SpreadSheet			sheet;

    if (m_Counter != null)
      return;

    m_Counter = new NamedCounter();

    if (FileUtils.fileExists(getFilename())) {
      reader = new CsvSpreadSheetReader();
      reader.setParseFormulas(false);
      reader.setTextColumns(new Range("1"));
      reader.setNumRowsColumnTypeDiscovery(10);
      sheet = reader.read(getFilename());
      for (Row row : sheet.rows())
	m_Counter.set(row.getCell(0).getContent(), row.getCell(1).toLong().intValue());
    }
  }

  /**
   * Performs the actual save operation.
   */
  protected static String save() {
    SpreadSheet			sheet;
    Row				row;
    List<String> 		names;
    CsvSpreadSheetWriter	writer;

    sheet = new DefaultSpreadSheet();
    row   = sheet.getHeaderRow();
    row.addCell("actor").setContentAsString("Actor");
    row.addCell("count").setContentAsString("Count");

    names = new ArrayList<>(m_Counter.nameSet());
    Collections.sort(names);
    for (String name: names) {
      row = sheet.addRow();
      row.addCell(0).setContentAsString(name);
      row.addCell(1).setContent(m_Counter.current(name));
    }

    if (sheet.getRowCount() > 0) {
      writer = new CsvSpreadSheetWriter();
      if (!writer.write(sheet, getFilename()))
        return "Failed to update most common actors: " + getFilename();
    }

    return null;
  }

  /**
   * Saves the counter as CSV file.
   */
  protected static void saveCounter() {
    if (m_SaveRunnable == null) {
      m_SaveRunnable = new DelayedActionRunnable(10 * 1000, 250);
      m_SaveThread = new Thread(m_SaveRunnable);
      m_SaveThread.start();
    }
    m_SaveRunnable.queue(new DelayedActionRunnable.AbstractAction(m_SaveRunnable) {
      @Override
      public String execute() {
	m_SaveRunnable.stopExecution();
	m_SaveRunnable = null;
	return save();
      }
    });
  }

  /**
   * Records the actor that was added.
   *
   * @param added    the actor that was added
   * @param parent   the parent of the added actor
   * @param before   the immediate actor before the added actor, can be null
   * @param after    the immediate actor after the added actor, can be null
   * @param position how the actor was added
   */
  @Override
  protected void record(Actor added, Actor parent, Actor before, Actor after, TreeOperations.InsertPosition position) {
    initCounter();
    m_Counter.next(added.getClass().getName());
    saveCounter();
  }

  /**
   * Returns the X most common actors.
   *
   * @param max		the maximum number of actors, <= 0 for all
   * @return		the list of actor names
   */
  public static List<String> getMostCommon(int max) {
    List<String>	result;
    List<String>	minimum;
    TIntList		counts;
    int			minCount;

    result = new ArrayList<>();

    initCounter();

    if (m_Counter.size() == 0)
      return result;

    // retrieve current counts and sort them desc
    counts = new TIntArrayList();
    for (String name: m_Counter.nameSet())
      counts.add(m_Counter.current(name));
    counts.sort();
    counts.reverse();

    // minimum count for actor to have
    if ((counts.size() < max) || (max <= 0))
      minCount = 0;
    else
      minCount = counts.get(max - 1);

    // assemble list
    minimum = new ArrayList<>();
    for (String name: m_Counter.nameSet()) {
      if (m_Counter.current(name) > minCount)
        result.add(name);
      else if (m_Counter.current(name) == minCount)
        minimum.add(name);
    }

    Collections.sort(minimum);
    if (max > 0) {
      while (result.size() < max && minimum.size() > 0)
        result.add(minimum.remove(0));
    }
    else {
      result.addAll(minimum);
    }
    Collections.sort(result);

    return result;
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    if (m_SaveRunnable != null) {
      m_SaveRunnable.stopExecution();
      m_SaveRunnable = null;
      m_SaveThread   = null;
    }
    super.cleanUp();
  }
}
