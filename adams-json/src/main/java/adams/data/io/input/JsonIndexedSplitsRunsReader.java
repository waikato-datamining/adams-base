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
 * JsonIndexedSplitsRunsReader.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.MessageCollection;
import adams.data.indexedsplits.IndexedSplit;
import adams.data.indexedsplits.IndexedSplits;
import adams.data.indexedsplits.IndexedSplitsRun;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.indexedsplits.SplitIndices;
import adams.env.Environment;
import com.google.gson.Gson;

import java.io.File;
import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * Reads runs of indexed splits from JSON.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class JsonIndexedSplitsRunsReader
  extends AbstractIndexedSplitsRunsReader {

  private static final long serialVersionUID = -1229985773501645279L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads runs of indexed splits from JSON.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return "JSON indexed splits";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"json"};
  }

  /**
   * Returns the default extension of the format.
   *
   * @return the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return "json";
  }

  /**
   * Reads the split definitions from the specified reader.
   * The caller must close the reader object.
   *
   * @param reader the reader to read from
   * @param errors for storing errors
   * @return the definitions or null in case of an error
   */
  @Override
  protected IndexedSplitsRuns doRead(Reader reader, MessageCollection errors) {
    IndexedSplitsRuns	result;
    Gson		gson;
    Map<?, ?> 		map;
    List 		jruns;
    Map			meta;
    int			ir;
    Map 		jrun;
    IndexedSplitsRun	run;
    List 		jsplits;
    IndexedSplits	splits;
    int			is;
    Map 		jsplit;
    IndexedSplit	split;
    SplitIndices 	splindices;
    List 		jindices;
    int[] 		intIndices;
    int			ii;

    try {
      gson = new Gson();
      map = gson.fromJson(reader, Map.class);
      if (!map.containsKey("runs")) {
        errors.add("Failed to locate top-level 'runs' property!");
	return null;
      }

      result = new IndexedSplitsRuns();

      // metadata
      if (map.containsKey("meta-data")) {
	meta = (Map) map.get("meta-data");
	for (Object key: meta.keySet())
	  result.getMetaData().put("" + key, "" + meta.get(key));
      }

      // runs
      jruns = (List) map.get("runs");
      for (ir = 0; ir < jruns.size(); ir++) {
        jrun = (Map) jruns.get(ir);
        if (!jrun.containsKey("splits")) {
	  errors.add("Failed to locate 'splits' property!");
	  return null;
	}
	jsplits = (List) jrun.get("splits");
	splits  = new IndexedSplits();
        run     = new IndexedSplitsRun(ir, splits);
        result.add(run);
        for (is = 0; is < jsplits.size(); is++) {
          jsplit = (Map) jsplits.get(is);
	  split  = new IndexedSplit(is);
          for (Object key: jsplit.keySet()) {
            jindices = (List) jsplit.get(key);
            intIndices = new int[jindices.size()];
            for (ii = 0; ii < jindices.size(); ii++)
              intIndices[ii] = ((Number) jindices.get(ii)).intValue();
	    splindices = new SplitIndices("" + key, intIndices);
	    split.add(splindices);
	  }
	  splits.add(split);
	}
      }

      return result;
    }
    catch (Exception e) {
      errors.add("Failed to read from JSON!", e);
      return null;
    }
  }

  public static void main(String[] args) throws Exception {
    Environment.setEnvironmentClass(Environment.class);

    MessageCollection errors = new MessageCollection();
    JsonIndexedSplitsRunsReader reader = new JsonIndexedSplitsRunsReader();
    IndexedSplitsRuns runs = reader.read(new File("/home/fracpete/temp/runs.json"), errors);
    if (!errors.isEmpty())
      System.out.println(errors);
    else
      System.out.println(runs);
  }
}
