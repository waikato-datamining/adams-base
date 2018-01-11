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
 * ResultMatrixAdamsCSV.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package weka.experiment;

import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import weka.core.RevisionUtils;
import weka.core.Utils;

import java.io.StringWriter;

/**
 <!-- globalinfo-start -->
 * Generates the matrix in ADAMS CSV ('comma-separated values') format.
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
 *  (default: 0)</pre>
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
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ResultMatrixAdamsCSV
  extends ResultMatrix {

  /** for serialization. */
  private static final long serialVersionUID = -171838863135042743L;
  
  /**
   * initializes the matrix as 1x1 matrix.
   */
  public ResultMatrixAdamsCSV() {
    this(1, 1);
  }

  /**
   * initializes the matrix with the given dimensions.
   * 
   * @param cols	the number of columns
   * @param rows	the number of rows
   */
  public ResultMatrixAdamsCSV(int cols, int rows) {
    super(cols, rows);
  }

  /**
   * initializes the matrix with the values from the given matrix.
   * 
   * @param matrix      the matrix to get the values from
   */
  public ResultMatrixAdamsCSV(ResultMatrix matrix) {
    super(matrix);
  }
  
  /**
   * Returns a string describing the matrix.
   * 
   * @return 		a description suitable for
   * 			displaying in the experimenter gui
   */
  public String globalInfo() {
    return "Generates the matrix in ADAMS CSV ('comma-separated values') format.";
  }

  /**
   * returns the name of the output format.
   * 
   * @return		the display name
   */
  public String getDisplayName() {
    return "CSV (ADAMS)";
  }

  /**
   * removes the stored data but retains the dimensions of the matrix.
   */
  public void clear() {
    super.clear();
    LEFT_PARENTHESES = "[";
    RIGHT_PARENTHESES = "]";
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
   * returns a 2-dimensional array with the prepared data. includes the column
   * and row names. hidden cols/rows are already excluded. <br>
   * first row: column names<br>
   * last row: wins/ties/losses<br>
   * first col: row names<br>
   *
   * @return the generated array
   */
  protected String[][] toArray() {
    String[][]	result;
    int		inc;
    int		col;

    result = super.toArray();
    col    = 1;
    do {
      // inspect value
      if (col == 1) {
	if (getShowStdDev())
	  result[0][col + 1] = result[0][col] + " SD";
      }
      else {
	if (getShowStdDev()) {
	  result[0][col + 1] = result[0][col] + " SD";
	  result[0][col + 2] = result[0][col] + " W/L";
	}
	else {
	  result[0][col + 1] = result[0][col] + " W/L";
	}
      }
      // increment
      if (col == 1)
	inc = 1;
      else
	inc = 2;
      if (getShowStdDev())
	inc++;
      col += inc;
    }
    while (col < result[0].length - 1);

    return result;
  }

  /**
   * Returns the default writer to use.
   *
   * @return		the writer
   */
  protected CsvSpreadSheetWriter getDefaultWriter() {
    CsvSpreadSheetWriter	result;
    String			numFormat;
    int				i;

    result = new CsvSpreadSheetWriter();
    result.setMissingValue("");
    numFormat = "#.";
    for (i = 0; i < Math.max(getMeanPrec(), getStdDevPrec()); i++)
      numFormat += "#";
    result.setNumberFormat(numFormat);

    return result;
  }

  /**
   * Turns the spreadsheet into a string.
   *
   * @param sheet	the spreadsheet to convert
   * @return		the generated string
   */
  protected String toString(SpreadSheet sheet) {
    CsvSpreadSheetWriter	writer;
    StringWriter		swriter;

    swriter = new StringWriter();
    writer = getDefaultWriter();
    writer.write(sheet, swriter);

    return "---" + sheet.getName() + "\n" + swriter.toString();
  }

  /**
   * returns the header of the matrix as a string.
   * 
   * @return		the header
   * @see 		#m_HeaderKeys
   * @see 		#m_HeaderValues
   */
  public String toStringHeader() {
    return "---Header\n" + new ResultMatrixPlainText(this).toStringHeader();
  }

  /**
   * returns the matrix in CSV format.
   * 
   * @return		the matrix as string
   */
  public String toStringMatrix() {
    SpreadSheet		sheet;
    Row			row;
    String[][]          cells;
    int                 i;
    int                 n;

    sheet = new DefaultSpreadSheet();
    sheet.setName("Matrix");
    cells  = toArray();

    for (i = 0; i < cells.length; i++) {
      if (i == 0)
	row = sheet.getHeaderRow();
      else
        row = sheet.addRow();
      for (n = 0; n < cells[i].length; n++) {
	row.addCell("" + n).setContent(cells[i][n]);
      }
    }
    
    return toString(sheet);
  }

  /**
   * returns a key for all the col names, for better readability if
   * the names got cut off.
   * 
   * @return		the key
   */
  public String toStringKey() {
    SpreadSheet		sheet;
    Row			row;
    int             	i;

    sheet = new DefaultSpreadSheet();
    sheet.setName("Key");
    row   = sheet.getHeaderRow();
    row.addCell("I").setContent("Index");
    row.addCell("S").setContent("Scheme");
    for (i = 0; i < getColCount(); i++) {
      if (getColHidden(i))
        continue;
      row = sheet.addRow();
      row.addCell("I").setContent(LEFT_PARENTHESES + (i+1) + RIGHT_PARENTHESES);
      row.addCell("S").setContent(m_ColNames[i]);
    }

    return toString(sheet);
  }

  /**
   * returns the summary as string.
   * 
   * @return		the summary
   */
  public String toStringSummary() {
    SpreadSheet		sheet;
    Row			row;
    int         	i;
    int         	j;

    sheet = new DefaultSpreadSheet();
    sheet.setName("Summary");
    if (m_NonSigWins == null)
      return toString(sheet);

    row = sheet.getHeaderRow();
    for (i = 0; i < getColCount(); i++) {
      if (getColHidden(i))
        continue;
      row.addCell("" + sheet.getColumnCount()).setContent(getSummaryTitle(i));
    }
    row.addCell("" + sheet.getColumnCount()).setContent("(No. of datasets where [col] >> [row])");

    for (i = 0; i < getColCount(); i++) {
      if (getColHidden(i))
        continue;

      row  = sheet.addRow();
      for (j = 0; j < getColCount(); j++) {
        if (getColHidden(j))
          continue;

	if (j == i)
	  row.addCell("" + j).setContentAsString("-");
	else
	row.addCell("" + j).setContentAsString(m_NonSigWins[i][j] + " (" + m_Wins[i][j] + ")");
      }

      row.addCell("" + (sheet.getColumnCount() - 1)).setContentAsString(getSummaryTitle(i) + " = " + removeFilterName(m_ColNames[i]));
    }

    return toString(sheet);
  }

  /**
   * returns the ranking in a string representation.
   * 
   * @return		the ranking
   */
  public String toStringRanking() {
    SpreadSheet		sheet;
    Row			row;
    int[]         	ranking;
    int           	i;
    int           	curr;

    sheet = new DefaultSpreadSheet();
    sheet.setName("Ranking");
    if (m_RankingWins == null)
      return "---ranking\n" + toString(sheet);

    row = sheet.getHeaderRow();
    row.addCell("D").setContentAsString(">-<");
    row.addCell("W").setContentAsString(">");
    row.addCell("L").setContentAsString("<");
    row.addCell("R").setContentAsString("Resultset");

    ranking = Utils.sort(m_RankingDiff);

    for (i = getColCount() - 1; i >= 0; i--) {
      curr = ranking[i];

      if (getColHidden(curr))
        continue;

      row = sheet.addRow();
      row.addCell("D").setContent(m_RankingDiff[curr]);
      row.addCell("W").setContent(m_RankingWins[curr]);
      row.addCell("L").setContent(m_RankingLosses[curr]);
      row.addCell("R").setContentAsString(m_ColNames[curr]);
    }

    return toString(sheet);
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

    Environment.setEnvironmentClass(Environment.class);

    matrix = new ResultMatrixAdamsCSV(3, 3);

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
