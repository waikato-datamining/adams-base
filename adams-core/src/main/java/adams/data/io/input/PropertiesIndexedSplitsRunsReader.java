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
 * PropertiesIndexedSplitsRunsReader.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.MessageCollection;
import adams.core.Properties;
import adams.data.indexedsplits.IndexedSplit;
import adams.data.indexedsplits.IndexedSplits;
import adams.data.indexedsplits.IndexedSplitsRun;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.indexedsplits.SplitIndices;

import java.io.Reader;

/**
 * Reads runs of indexed splits from .props files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PropertiesIndexedSplitsRunsReader
  extends AbstractIndexedSplitsRunsReader {

  private static final long serialVersionUID = 7675147645377655254L;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads runs of indexed splits from .props files.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Properties indexed splits";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"props"};
  }

  /**
   * Returns the default extension of the format.
   *
   * @return the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return "props";
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
    IndexedSplitsRun 	indexedSplitsRun;
    IndexedSplits	indexedSplits;
    IndexedSplit  	indexedSplit;
    SplitIndices	splitIndices;
    Properties 		props;
    int			numRuns;
    int			run;
    int			numSplits;
    int			split;
    String[]		names;
    String[]		indicesStr;
    int[]		indices;
    int			i;
    int			id;

    result = new IndexedSplitsRuns();
    props  = new Properties();
    try {
      props.load(reader);
      numRuns = props.getInteger("runs");
      for (run = 0; run < numRuns; run++) {
        id               = props.getInteger("run." + run + ".id");
        numSplits        = props.getInteger("run." + run + ".splits");
        indexedSplits    = new IndexedSplits();
        indexedSplitsRun = new IndexedSplitsRun(id, indexedSplits);
        result.add(indexedSplitsRun);

        for (split = 0; split < numSplits; split++) {
          id    = props.getInteger("run." + run + ".split." + split + ".id");
          names = props.getProperty("run." + run + ".split." + split + ".names").split(",");
          indexedSplit = new IndexedSplit(id);
          indexedSplits.add(indexedSplit);
          for (String name: names) {
	    indicesStr = props.getProperty("run." + run + ".split." + split + "." + name).split(",");
	    indices = new int[indicesStr.length];
	    for (i = 0; i < indicesStr.length; i++)
	      indices[i] = Integer.parseInt(indicesStr[i]);
	    splitIndices = new SplitIndices(name, indices);
	    indexedSplit.add(splitIndices);
	  }
	}
      }
    }
    catch (Exception e) {
      errors.add("Failed to read splits!", e);
      result = null;
    }

    return result;
  }
}
