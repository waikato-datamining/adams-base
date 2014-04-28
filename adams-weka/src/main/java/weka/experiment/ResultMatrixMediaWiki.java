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
 * ResultMatrixMediaWiki.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.experiment;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.Utils;

/**
 <!-- globalinfo-start -->
 * Generates table output in MediaWiki format.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre> -mean-prec &lt;int&gt;
 *  The number of decimals after the decimal point for the mean.
 *  (default: 2)</pre>
 *
 * <pre> -stddev-prec &lt;int&gt;
 *  The number of decimals after the decimal point for the mean.
 *  (default: 2)</pre>
 *
 * <pre> -col-name-width &lt;int&gt;
 *  The maximum width for the column names (0 = optimal).
 *  (default: 0)</pre>
 *
 * <pre> -row-name-width &lt;int&gt;
 *  The maximum width for the row names (0 = optimal).
 *  (default: 25)</pre>
 *
 * <pre> -mean-width &lt;int&gt;
 *  The width of the mean (0 = optimal).
 *  (default: 0)</pre>
 *
 * <pre> -stddev-width &lt;int&gt;
 *  The width of the standard deviation (0 = optimal).
 *  (default: 0)</pre>
 *
 * <pre> -sig-width &lt;int&gt;
 *  The width of the significance indicator (0 = optimal).
 *  (default: 0)</pre>
 *
 * <pre> -count-width &lt;int&gt;
 *  The width of the counts (0 = optimal).
 *  (default: 0)</pre>
 *
 * <pre> -show-stddev
 *  Whether to display the standard deviation column.
 *  (default: no)</pre>
 *
 * <pre> -show-avg
 *  Whether to show the row with averages.
 *  (default: no)</pre>
 *
 * <pre> -remove-filter
 *  Whether to remove the classname package prefixes from the
 *  filter names in datasets.
 *  (default: no)</pre>
 *
 * <pre> -print-col-names
 *  Whether to output column names or just numbers representing them.
 *  (default: no)</pre>
 *
 * <pre> -print-row-names
 *  Whether to output row names or just numbers representing them.
 *  (default: no)</pre>
 *
 * <pre> -enum-col-names
 *  Whether to enumerate the column names (prefixing them with
 *  '(x)', with 'x' being the index).
 *  (default: no)</pre>
 *
 * <pre> -enum-row-names
 *  Whether to enumerate the row names (prefixing them with
 *  '(x)', with 'x' being the index).
 *  (default: no)</pre>
 *
 * <pre> -border &lt;int&gt;
 *  The thickness of the table border.
 *  (default: 1)</pre>
 *
 * <pre> -cell-spacing &lt;int&gt;
 *  The cell spacing of the table.
 *  (default: 0)</pre>
 *
 * <pre> -cell-padding &lt;int&gt;
 *  The cell padding of the table.
 *  (default: 5)</pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ResultMatrixMediaWiki
  extends ResultMatrix {

  /** for serialization. */
  private static final long serialVersionUID = 6501952611900558450L;

  /** the size of the border. */
  protected int m_Border = 1;

  /** the cell spacing. */
  protected int m_CellSpacing = 0;

  /** the cell padding. */
  protected int m_CellPadding = 5;

  /**
   * initializes the matrix as 1x1 matrix.
   */
  public ResultMatrixMediaWiki() {
    this(1, 1);
  }

  /**
   * initializes the matrix with the given dimensions.
   *
   * @param cols	the number of columns
   * @param rows	the number of rows
   */
  public ResultMatrixMediaWiki(int cols, int rows) {
    super(cols, rows);
  }

  /**
   * initializes the matrix with the values from the given matrix.
   *
   * @param matrix      the matrix to get the values from
   */
  public ResultMatrixMediaWiki(ResultMatrix matrix) {
    super(matrix);
  }

  /**
   * Returns a string describing the matrix.
   *
   * @return 		a description suitable for
   * 			displaying in the experimenter gui
   */
  public String globalInfo() {
    return "Generates table output in MediaWiki format.";
  }

  /**
   * Returns an enumeration of all the available options..
   *
   * @return 		an enumeration of all available options.
   */
  public Enumeration listOptions() {
    Vector<Option>	result;
    Enumeration		enm;

    result = new Vector<Option>();

    enm = super.listOptions();
    while (enm.hasMoreElements())
      result.add((Option) enm.nextElement());

    result.addElement(new Option(
        "\tThe thickness of the table border.\n"
        + "\t(default: 1)",
        "border", 1, "-border <int>"));

    result.addElement(new Option(
        "\tThe cell spacing of the table.\n"
        + "\t(default: 0)",
        "cell-spacing", 1, "-cell-spacing <int>"));

    result.addElement(new Option(
        "\tThe cell padding of the table.\n"
        + "\t(default: 5)",
        "cell-padding", 1, "-cell-padding <int>"));

    return result.elements();
  }

  /**
   * Sets the OptionHandler's options using the given list. All options
   * will be set (or reset) during this call (i.e. incremental setting
   * of options is not possible).
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    String	tmpStr;

    tmpStr = Utils.getOption("border", options);
    if (tmpStr.length() > 0)
      setBorder(Integer.parseInt(tmpStr));
    else
      setBorder(getDefaultBorder());

    tmpStr = Utils.getOption("cell-spacing", options);
    if (tmpStr.length() > 0)
      setCellSpacing(Integer.parseInt(tmpStr));
    else
      setCellSpacing(getDefaultCellSpacing());

    tmpStr = Utils.getOption("cell-padding", options);
    if (tmpStr.length() > 0)
      setCellPadding(Integer.parseInt(tmpStr));
    else
      setCellPadding(getDefaultCellPadding());

    super.setOptions(options);
  }

  /**
   * Gets the current option settings for the OptionHandler.
   *
   * @return the list of current option settings as an array of strings
   */
  public String[] getOptions() {
    Vector<String>	result;

    result = new Vector<String>(Arrays.asList(super.getOptions()));

    result.add("-border");
    result.add("" + getBorder());

    result.add("-cell-spacing");
    result.add("" + getCellSpacing());

    result.add("-cell-padding");
    result.add("" + getCellPadding());

    return result.toArray(new String[result.size()]);
  }

  /**
   * returns the name of the output format.
   *
   * @return		the display name
   */
  public String getDisplayName() {
    return "MediaWiki";
  }

  /**
   * returns the default width for the row names.
   *
   * @return		the width
   */
  public int getDefaultRowNameWidth() {
    return 25;
  }

  /**
   * returns the default of whether column names or numbers instead are printed.
   *
   * @return		true if names instead of numbers are printed
   */
  public boolean getDefaultPrintColNames() {
    return false;
  }

  /**
   * returns the default of whether column names are prefixed with the index.
   *
   * @return		true if the names are prefixed
   */
  public boolean getDefaultEnumerateColNames() {
    return true;
  }

  /**
   * Returns the default border thickness of the table.
   *
   * @return		the default thickness
   */
  protected int getDefaultBorder() {
    return 1;
  }

  /**
   * Sets the thickness of the border.
   *
   * @param value	the thickness
   */
  public void setBorder(int value) {
    if (value >= 0)
      m_Border = value;
  }

  /**
   * Returns the thickness of the border.
   *
   * @return		the thickness
   */
  public int getBorder() {
    return m_Border;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the experimenter gui
   */
  public String borderTipText() {
    return "The thickness of the table border.";
  }

  /**
   * Returns the default cell spacing for the table.
   *
   * @return		the default cell spacing
   */
  protected int getDefaultCellSpacing() {
    return 0;
  }

  /**
   * Sets the cell spacing for the table.
   *
   * @param value	the cell spacing
   */
  public void setCellSpacing(int value) {
    if (value >= 0)
      m_CellSpacing = value;
  }

  /**
   * Returns the cell spacing for the table.
   *
   * @return		the cell spacing
   */
  public int getCellSpacing() {
    return m_CellSpacing;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the experimenter gui
   */
  public String cellSpacingTipText() {
    return "The cell spacing of the table.";
  }

  /**
   * Returns the default cell padding for the table.
   *
   * @return		the default cell padding
   */
  protected int getDefaultCellPadding() {
    return 5;
  }

  /**
   * Sets the cell padding for the table.
   *
   * @param value	the cell padding
   */
  public void setCellPadding(int value) {
    if (value >= 0)
      m_CellPadding = value;
  }

  /**
   * Returns the cell padding for the table.
   *
   * @return		the cell padding
   */
  public int getCellPadding() {
    return m_CellPadding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the experimenter gui
   */
  public String cellPaddingTipText() {
    return "The cell padding of the table.";
  }

  /**
   * Returns the prolog of a table.
   *
   * @return		the prolog
   */
  protected String getTableProlog() {
    return "{| border=\"" + m_Border + "\" cellspacing=\"" + m_CellSpacing + "\" cellpadding=\"" + m_CellPadding + "\"\n";
  }

  /**
   * Returns the epilog for a table.
   *
   * @return		the epilog
   */
  protected String getTableEpilog() {
    return "|-\n" + "|}\n";
  }

  /**
   * returns the header of the matrix as a string.
   *
   * @return		the header
   * @see 		#m_HeaderKeys
   * @see 		#m_HeaderValues
   */
  public String toStringHeader() {
    StringBuilder	result;
    int			i;

    result = new StringBuilder();

    result.append(getTableProlog());
    for (i = 0; i < m_HeaderKeys.size(); i++) {
      result.append("|-\n");
      result.append("| " + m_HeaderKeys.get(i).toString() + "\n");
      result.append("| " + m_HeaderValues.get(i).toString() + "\n");
    }
    result.append(getTableEpilog());

    return result.toString();
  }

  /**
   * returns the matrix in CSV format.
   *
   * @return		the matrix as string
   */
  public String toStringMatrix() {
    StringBuilder        result;
    String[][]          cells;
    int                 i;
    int                 n;

    result = new StringBuilder();
    cells  = toArray();

    result.append(getTableProlog());
    for (i = 0; i < cells.length; i++) {
      result.append("|-\n");
      for (n = 0; n < cells[i].length; n++) {
	if (i == 0) {
	  if (n > 0)
	    result.append("! align=\"center\" | ");
	  else
	    result.append("! ");
	  result.append(cells[i][n] + "\n");
	}
	else {
	  if (n > 0)
	    result.append("| align=\"right\" ");
	  result.append("| " + cells[i][n] + "\n");
	}
      }
    }
    result.append(getTableEpilog());

    return result.toString();
  }

  /**
   * returns a key for all the col names, for better readability if
   * the names got cut off.
   *
   * @return		the key
   */
  public String toStringKey() {
    StringBuilder	result;
    int			i;

    result = new StringBuilder();

    result.append(getTableProlog());
    result.append("|-\n");
    result.append("! Key\n");
    result.append("! \n");
    for (i = 0; i < getColCount(); i++) {
      if (getColHidden(i))
        continue;
      result.append("|-\n");
      result.append("| " + LEFT_PARENTHESES + (i+1) + RIGHT_PARENTHESES + "\n");
      result.append("| " + removeFilterName(m_ColNames[i]) + "\n");
    }
    result.append(getTableEpilog());

    return result.toString();
  }

  /**
   * returns the summary as string.
   *
   * @return		the summary
   */
  public String toStringSummary() {
    StringBuilder	result;
    StringBuilder	titles;
    int			i;
    int			j;
    String		line;

    if (m_NonSigWins == null)
      return "-summary data not set-";

    result = new StringBuilder(getTableProlog());
    titles = new StringBuilder();

    for (i = 0; i < getColCount(); i++) {
      if (getColHidden(i))
        continue;
      titles.append("! " + getSummaryTitle(i) + "\n");
    }
    titles.append("! (No. of datasets where [col] >> [row])\n");
    result.append("|-\n");
    result.append(titles.toString());

    for (i = 0; i < getColCount(); i++) {
      if (getColHidden(i))
        continue;

      line = "";
      for (j = 0; j < getColCount(); j++) {
        if (getColHidden(j))
          continue;

	if (j == i)
	  line += "| -\n";
	else
	  line += "| " + m_NonSigWins[i][j] + " (" + m_Wins[i][j] + ")\n";
      }

      result.append("|-\n");
      result.append(line);
      result.append("| '''" + getSummaryTitle(i) + "''' = " + removeFilterName(m_ColNames[i]) + "\n");
    }

    result.append(getTableEpilog());

    return result.toString();
  }

  /**
   * returns the ranking in a string representation.
   *
   * @return		the ranking
   */
  public String toStringRanking() {
    StringBuilder	result;
    int[]		ranking;
    int 		i;
    int			curr;

    if (m_RankingWins == null)
      return "-ranking data not set-";

    result = new StringBuilder(getTableProlog());
    result.append("|-\n");
    result.append("! >-<\n");
    result.append("! >\n");
    result.append("! <\n");
    result.append("! Resultset\n");

    ranking = Utils.sort(m_RankingDiff);

    for (i = getColCount() - 1; i >= 0; i--) {
      curr = ranking[i];

      if (getColHidden(curr))
        continue;

      result.append("|-\n");
      result.append("| align=\"right\" | " + m_RankingDiff[curr] + "\n");
      result.append("| align=\"right\" | " + m_RankingWins[curr] + "\n");
      result.append("| align=\"right\" | " + m_RankingLosses[curr] + "\n");
      result.append("| " + removeFilterName(m_ColNames[curr]) + "\n");
    }

    result.append(getTableEpilog());

    return result.toString();
  }

  /**
   * Returns the revision string.
   *
   * @return		the revision
   */
  public String getRevision() {
    return RevisionUtils.extract("$Revision$");
  }

  /**
   * for testing only.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    ResultMatrix        matrix;
    int                 i;
    int                 n;

    matrix = new ResultMatrixMediaWiki(3, 3);

    // set header
    matrix.addHeader("header1", "value1");
    matrix.addHeader("header2", "value2");
    matrix.addHeader("header2", "value3");

    // set values
    for (i = 0; i < matrix.getRowCount(); i++) {
      for (n = 0; n < matrix.getColCount(); n++) {
        matrix.setMean(n, i, (i+1)*n);
        matrix.setStdDev(n, i, ((double) (i+1)*n) / 100);
        if (i == n) {
          if (i % 2 == 1)
            matrix.setSignificance(n, i, SIGNIFICANCE_WIN);
          else
            matrix.setSignificance(n, i, SIGNIFICANCE_LOSS);
        }
      }
    }

    System.out.println("\n\n--> " + matrix.getDisplayName());

    System.out.println("\n1. complete\n");
    System.out.println(matrix.toStringHeader() + "\n");
    System.out.println(matrix.toStringMatrix() + "\n");
    System.out.println(matrix.toStringKey());

    System.out.println("\n2. complete with std deviations\n");
    matrix.setShowStdDev(true);
    System.out.println(matrix.toStringMatrix());

    System.out.println("\n3. cols numbered\n");
    matrix.setPrintColNames(false);
    System.out.println(matrix.toStringMatrix());

    System.out.println("\n4. second col missing\n");
    matrix.setColHidden(1, true);
    System.out.println(matrix.toStringMatrix());

    System.out.println("\n5. last row missing, rows numbered too\n");
    matrix.setRowHidden(2, true);
    matrix.setPrintRowNames(false);
    System.out.println(matrix.toStringMatrix());

    System.out.println("\n6. mean prec to 3\n");
    matrix.setMeanPrec(3);
    matrix.setPrintRowNames(false);
    System.out.println(matrix.toStringMatrix());
  }
}
