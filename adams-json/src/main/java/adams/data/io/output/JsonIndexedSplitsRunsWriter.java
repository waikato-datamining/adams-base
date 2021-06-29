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
 * JsonIndexedSplitsRunsWriter.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.MessageCollection;
import adams.core.io.FileUtils;
import adams.core.io.PrettyPrintingSupporter;
import adams.data.indexedsplits.IndexedSplit;
import adams.data.indexedsplits.IndexedSplitsRun;
import adams.data.indexedsplits.IndexedSplitsRuns;
import adams.data.indexedsplits.SplitIndices;
import adams.data.io.input.JsonIndexedSplitsRunsReader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;

import java.io.Writer;

/**
 * Writes runs of indexed splits as JSON.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class JsonIndexedSplitsRunsWriter
  extends AbstractIndexedSplitsRunsWriter
  implements PrettyPrintingSupporter {

  private static final long serialVersionUID = 5188430181037862982L;

  /** whether to use pretty-printing. */
  protected boolean m_PrettyPrinting;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes runs of indexed splits as JSON.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "pretty-printing", "prettyPrinting",
      false);
  }

  /**
   * Sets whether to use pretty-printing or not.
   *
   * @param value	true if to use pretty-printing
   */
  public void setPrettyPrinting(boolean value) {
    m_PrettyPrinting = value;
    reset();
  }

  /**
   * Returns whether pretty-printing is used or not.
   *
   * @return		true if to use pretty-printing
   */
  public boolean getPrettyPrinting() {
    return m_PrettyPrinting;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String prettyPrintingTipText() {
    return "If enabled, the output is printed in a 'pretty' format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return a description suitable for displaying in the
   * file chooser
   */
  @Override
  public String getFormatDescription() {
    return new JsonIndexedSplitsRunsReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new JsonIndexedSplitsRunsReader().getFormatExtensions();
  }

  /**
   * Returns the default extension of the format.
   *
   * @return the default extension (without the dot!)
   */
  @Override
  public String getDefaultFormatExtension() {
    return new JsonIndexedSplitsRunsReader().getDefaultFormatExtension();
  }

  /**
   * Writes the split definitions to the specified reader.
   * The caller must close the writer object.
   *
   * @param writer 	the writer to write to
   * @param errors 	for storing errors
   * @return		whether successfully written
   */
  @Override
  protected boolean doWrite(Writer writer, IndexedSplitsRuns runs, MessageCollection errors) {
    JsonObject 		json;
    JsonArray		rs;
    JsonObject		meta;
    JsonObject 		r;
    JsonArray 		spls;
    JsonObject		spl;
    SplitIndices	ind;
    JsonArray		jind;
    JsonWriter		jwriter;

    // generate json
    json = new JsonObject();
    // 1. runs
    rs   = new JsonArray();
    json.add("runs", rs);
    for (IndexedSplitsRun run: runs) {
      r = new JsonObject();
      rs.add(r);
      spls = new JsonArray();
      //r.addProperty("run", run.getRun());
      r.add("splits", spls);
      for (IndexedSplit split: run.getSplits()) {
        spl = new JsonObject();
        spls.add(spl);
	for (String splitName: split.getIndices().keySet()) {
	  ind  = split.getIndices().get(splitName);
	  jind = new JsonArray(ind.size());
	  for (int i: ind.getIndices())
	    jind.add(i);
	  spl.add(splitName, jind);
	}
      }
    }
    // 2. meta data
    meta = new JsonObject();
    for (String key: runs.getMetaData().keySet())
      meta.addProperty(key, runs.getMetaData().get(key));
    json.add("meta-data", meta);

    // write json out
    jwriter = null;
    try {
      jwriter = new JsonWriter(writer);
      if (m_PrettyPrinting)
	jwriter.setIndent("  ");
      jwriter.setLenient(true);
      Streams.write(json, jwriter);
      return true;
    }
    catch (Exception e) {
      errors.add("Failed to write JSON!", e);
      return false;
    }
    finally {
      FileUtils.closeQuietly(jwriter);
    }
  }
}
