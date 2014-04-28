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
 * InstanceCompare.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui;

import java.awt.BorderLayout;

import adams.core.Index;
import adams.core.Range;
import adams.core.io.PlaceholderFile;
import adams.env.Environment;
import adams.gui.core.AbstractFrameWithOptionHandling;
import adams.gui.visualization.instance.InstanceComparePanel;

/**
 * Stand-alone version of the Instance Compare utility.
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-file1 &lt;java.io.File&gt; (property: firstDataset)
 * &nbsp;&nbsp;&nbsp;The first dataset in the comparison.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-att-range1 &lt;java.lang.String&gt; (property: firstAttributeRange)
 * &nbsp;&nbsp;&nbsp;The range of attributes of the first dataset to use in the comparison.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 *
 * <pre>-row-index1 &lt;java.lang.String&gt; (property: firstRowIndex)
 * &nbsp;&nbsp;&nbsp;The index of the attribute in the first dataset to use for matching the
 * &nbsp;&nbsp;&nbsp;rows of the two datasets.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-file2 &lt;java.io.File&gt; (property: secondDataset)
 * &nbsp;&nbsp;&nbsp;The second dataset in the comparison.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-att-range2 &lt;java.lang.String&gt; (property: secondAttributeRange)
 * &nbsp;&nbsp;&nbsp;The range of attributes of the second dataset to use in the comparison.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 *
 * <pre>-row-index2 &lt;java.lang.String&gt; (property: secondRowIndex)
 * &nbsp;&nbsp;&nbsp;The index of the attribute in the second dataset to use for matching the
 * &nbsp;&nbsp;&nbsp;rows of the two datasets.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstanceCompare
  extends AbstractFrameWithOptionHandling {

  /** for serialization. */
  private static final long serialVersionUID = -8575580776969822903L;

  /** the panel for comparing the datasets. */
  protected InstanceComparePanel m_PanelCompare;

  /** the first file to compare. */
  protected PlaceholderFile m_FirstFile;

  /** the first attribute range to use. */
  protected Range m_FirstAttributeRange;

  /** the index of the first attribute to use for matching rows. */
  protected Index m_FirstRowIndex;

  /** the second file to compare. */
  protected PlaceholderFile m_SecondFile;

  /** the second attribute range to use. */
  protected Range m_SecondAttributeRange;

  /** the index of the second attribute to use for matching rows. */
  protected Index m_SecondRowIndex;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"file1", "firstDataset",
	new PlaceholderFile("."));

    m_OptionManager.add(
	"att-range1", "firstAttributeRange",
       "first-last");

    m_OptionManager.add(
	"row-index1", "firstRowIndex",
       "");

    m_OptionManager.add(
	"file2", "secondDataset",
	new PlaceholderFile("."));

    m_OptionManager.add(
	"att-range2", "secondAttributeRange",
        "first-last");

    m_OptionManager.add(
	"row-index2", "secondRowIndex",
        "");
  }

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FirstAttributeRange  = new Range();
    m_FirstRowIndex        = new Index();

    m_SecondAttributeRange = new Range();
    m_SecondRowIndex       = new Index();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setTitle("Instance Compare");

    getContentPane().setLayout(new BorderLayout());

    m_PanelCompare = new InstanceComparePanel();
    getContentPane().add(m_PanelCompare, BorderLayout.CENTER);
  }

  /**
   * Sets the first dataset.
   *
   * @param value	the first dataset
   */
  public void setFirstDataset(PlaceholderFile value) {
    m_FirstFile = value;
    reset();
  }

  /**
   * Returns the first dataset.
   *
   * @return		the first dataset
   */
  public PlaceholderFile getFirstDataset() {
    return m_FirstFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String firstDatasetTipText() {
    return "The first dataset in the comparison.";
  }

  /**
   * Sets the first attribute range ('first' and 'last' can be used as well).
   *
   * @param value	the attribute range
   */
  public void setFirstAttributeRange(String value) {
    m_FirstAttributeRange.setRange(value);
    reset();
  }

  /**
   * Returns the first attribute range.
   *
   * @return		the attribute range
   */
  public String getFirstAttributeRange() {
    return m_FirstAttributeRange.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String firstAttributeRangeTipText() {
    return "The range of attributes of the first dataset to use in the comparison.";
  }

  /**
   * Sets the first row index ('first' and 'last' can be used as well).
   *
   * @param value	the index of the row attribute
   */
  public void setFirstRowIndex(String value) {
    m_FirstRowIndex.setIndex(value);
    reset();
  }

  /**
   * Returns the first row index.
   *
   * @return		the index of the row attribute (1-based, 'first', 'last')
   */
  public String getFirstRowIndex() {
    return m_FirstRowIndex.getIndex();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String firstRowIndexTipText() {
    return
        "The index of the attribute in the first dataset to use for matching "
      + "the rows of the two datasets.";
  }

  /**
   * Sets the second dataset.
   *
   * @param value	the second dataset
   */
  public void setSecondDataset(PlaceholderFile value) {
    m_SecondFile = value;
    reset();
  }

  /**
   * Returns the second dataset.
   *
   * @return		the second dataset
   */
  public PlaceholderFile getSecondDataset() {
    return m_SecondFile;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String secondDatasetTipText() {
    return "The second dataset in the comparison.";
  }

  /**
   * Sets the second attribute range ('second' and 'last' can be used as well).
   *
   * @param value	the attribute range
   */
  public void setSecondAttributeRange(String value) {
    m_SecondAttributeRange.setRange(value);
    reset();
  }

  /**
   * Returns the second attribute range.
   *
   * @return		the attribute range
   */
  public String getSecondAttributeRange() {
    return m_SecondAttributeRange.getRange();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String secondAttributeRangeTipText() {
    return "The range of attributes of the second dataset to use in the comparison.";
  }

  /**
   * Sets the second row index ('second' and 'last' can be used as well).
   *
   * @param value	the index of the row attribute
   */
  public void setSecondRowIndex(String value) {
    m_SecondRowIndex.setIndex(value);
    reset();
  }

  /**
   * Returns the second row index.
   *
   * @return		the index of the row attribute (2-based, 'second', 'last')
   */
  public String getSecondRowIndex() {
    return m_SecondRowIndex.getIndex();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String secondRowIndexTipText() {
    return
        "The index of the attribute in the second dataset to use for matching "
      + "the rows of the two datasets.";
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  @Override
  protected void beforeShow() {
    super.beforeShow();

    // first dataset
    if (m_FirstFile.exists() && !m_FirstFile.isDirectory()) {
      m_PanelCompare.setFirstDataset(m_FirstFile);
      m_PanelCompare.setFirstAttributeRange(m_FirstAttributeRange.getRange());
      m_PanelCompare.setFirstRowIndex(m_FirstRowIndex.getIndex());
    }
    else {
      getLogger().severe("1st file does not exist: " + m_FirstFile);
    }

    // second dataset
    if (m_SecondFile.exists() && !m_SecondFile.isDirectory()) {
      m_PanelCompare.setSecondDataset(m_SecondFile);
      m_PanelCompare.setSecondAttributeRange(m_SecondAttributeRange.getRange());
      m_PanelCompare.setSecondRowIndex(m_SecondRowIndex.getIndex());
    }
    else {
      getLogger().severe("2nd file does not exist: " + m_SecondFile);
    }
  }

  /**
   * Starts the frame.
   *
   * @param args	the commandline arguments
   */
  public static void main(String[] args) {
    runFrame(Environment.class, InstanceCompare.class, args);
  }
}
