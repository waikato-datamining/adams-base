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
 * SpreadSheetRandomSystematicSample.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.Randomizable;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetView;
import adams.data.spreadsheet.SpreadSheetViewCreator;
import adams.flow.core.Token;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.Random;

/**
 <!-- globalinfo-start -->
 * Performs random systematic sampling on the rows of the incoming spreadsheet.<br>
 * Divides the rows into N blocks with N being the sample size. Then chooses a random starting position. Adds the row the output increments the position by the sample size. Position wraps around if output hasn't yet reached sample size.<br>
 * For more information see:<br>
 * https:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Systematic_sampling
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.spreadsheet.SpreadSheet<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SpreadSheetRandomSystematicSample
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-seed &lt;long&gt; (property: seed)
 * &nbsp;&nbsp;&nbsp;The seed for the randomization
 * &nbsp;&nbsp;&nbsp;default: 1
 * </pre>
 *
 * <pre>-sample-size &lt;int&gt; (property: sampleSize)
 * &nbsp;&nbsp;&nbsp;The size of the sample to use.
 * &nbsp;&nbsp;&nbsp;default: 10
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-create-view &lt;boolean&gt; (property: createView)
 * &nbsp;&nbsp;&nbsp;If enabled, then only a view of the subset is created.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetRandomSystematicSample
  extends AbstractSpreadSheetTransformer
  implements Randomizable, SpreadSheetViewCreator {

  private static final long serialVersionUID = -1287490418250599839L;

  /** the seed for the randomization. */
  protected long m_Seed;

  /** the sample size. */
  protected int m_SampleSize;

  /** whether to create a view only. */
  protected boolean m_CreateView;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs random systematic sampling on the rows of the incoming spreadsheet.\n"
      + "Divides the rows into N blocks with N being the sample size. Then chooses a random "
      + "starting position. Adds the row the output increments the position by the sample size. "
      + "Position wraps around if output hasn't yet reached sample size.\n"
      + "For more information see:\n"
      + "https://en.wikipedia.org/wiki/Systematic_sampling";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "seed", "seed",
      1L);

    m_OptionManager.add(
      "sample-size", "sampleSize",
      10, 1, null);

    m_OptionManager.add(
      "create-view", "createView",
      false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    String	value;

    result  = QuickInfoHelper.toString(this, "sampleSize", m_SampleSize, "size: ");
    value  = QuickInfoHelper.toString(this, "createView", m_CreateView, ", view only");
    if (value != null)
      result += value;

    return result;
  }

  /**
   * Sets the seed value.
   *
   * @param value	the seed
   */
  public void setSeed(long value) {
    m_Seed = value;
    reset();
  }

  /**
   * Returns the seed value.
   *
   * @return  		the seed
   */
  public long getSeed() {
    return m_Seed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String seedTipText() {
    return "The seed for the randomization";
  }

  /**
   * Sets the sample size.
   *
   * @param value	the size
   */
  public void setSampleSize(int value) {
    m_SampleSize = value;
    reset();
  }

  /**
   * Returns the sample size.
   *
   * @return  		the size
   */
  public int getSampleSize() {
    return m_SampleSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sampleSizeTipText() {
    return "The size of the sample to use.";
  }

  /**
   * Sets whether to create a view only.
   *
   * @param value	true if to create a view only
   */
  public void setCreateView(boolean value) {
    m_CreateView = value;
    reset();
  }

  /**
   * Returns whether to create only a view.
   *
   * @return		true if to create view only
   */
  public boolean getCreateView() {
    return m_CreateView;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String createViewTipText() {
    return "If enabled, then only a view of the subset is created.";
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String 		result;
    SpreadSheet 	sheet;
    TIntList 		rowsOut;
    int 		i;
    int 		pos;
    int 		inc;
    SpreadSheet		sheetOut;
    Random		rand;

    result = null;
    sheet  = m_InputToken.getPayload(SpreadSheet.class);

    if (isLoggingEnabled())
      getLogger().info("# rows: " + sheet.getRowCount());

    inc = sheet.getRowCount() / m_SampleSize;
    if (inc == 0)
      result = "Less rows than sample size: " + sheet.getRowCount() + " < " + m_SampleSize;
    if (isLoggingEnabled())
      getLogger().info("inc: " + inc);

    if (result == null) {
      // collect sample rows
      rowsOut = new TIntArrayList();
      rand    = new Random(m_Seed);
      pos     = rand.nextInt(inc) - inc;
      if (isLoggingEnabled())
	getLogger().info("starting pos: " + pos);
      for (i = 0; i < m_SampleSize; i++) {
        pos += inc;
        if (pos > sheet.getRowCount())
          pos -= sheet.getRowCount();
	if (isLoggingEnabled())
	  getLogger().info("pos: " + pos);
        rowsOut.add(pos);
      }

      // assemble output
      if (m_CreateView) {
        m_OutputToken = new Token(new SpreadSheetView(sheet, rowsOut.toArray(), null));
      }
      else {
        sheetOut = sheet.getHeader();
        for (i = 0; i < rowsOut.size(); i++)
          sheetOut.addRow().assign(sheet.getRow(rowsOut.get(i)));
        m_OutputToken = new Token(sheetOut);
      }
    }

    return result;
  }
}
