package adams.ml;

import java.io.BufferedReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import adams.core.Utils;
import adams.core.io.FileUtils;

@Deprecated
public class CSVDataset extends Dataset {

  /** suid  */
  private static final long serialVersionUID = 2656050894378581346L;
  protected Hashtable<Integer, String> m_columnToName= new Hashtable<Integer, String>();
  protected Hashtable<String, Integer> m_nameToColumn= new Hashtable<String, Integer>();

  protected Hashtable<Integer, Vector<Integer>> m_index = new Hashtable<Integer, Vector<Integer>>();



  protected boolean hasColumn(String col){
    return(m_nameToColumn.get(col) != null);
  }
  
  protected void setIndex(String columnname){
    m_index.clear();
    for (int row=0;row<count();row++){
      DataRow dr=get(row);
      BaseData cell=dr.get(columnname);
      if (cell == null || !cell.isNumeric()){
	continue;
      }
      try {
	Integer i=(Integer)cell.getData();
	Vector<Integer> rows=m_index.get(i);
	if (rows==null){
	  rows=new Vector<Integer>();
	  m_index.put(i, rows);
	}
	rows.add(row);
      } catch (Exception e){
	e.printStackTrace();
      }
    }
  }

  public Object[] toArray(String columnname){
    Object[] oarr=new Object[count()];
    for (int row=0;row<count();row++){
      DataRow dr=get(row);
      BaseData cell=dr.get(columnname);
      if (cell != null){
	oarr[row]=cell.getData();
      } else {
	oarr[row]=null;
      }      
    }
    return(oarr);
  }

  protected Vector<Integer> getFromIndex(Integer i){
    Vector<Integer> ret=m_index.get(i);
    if (ret == null){
      ret=new Vector<Integer>();
    }
    return(ret);
  }

  protected void addError(int row, String col){
    Vector<String> err=m_errors.get(row);
    if (err == null){
      err=new Vector<String>();
    } 
    err.add(col);
    m_errors.put(row, err);
    //System.err.println("Error at:"+col+"->"+get(row).toString());
  }
  protected void setColumn(int cn, String name){
    m_columnToName.put(cn, name);
    m_nameToColumn.put(name, cn);
  }

  protected boolean columnPresent(String name){
    return(m_nameToColumn.get(name) != null);
  }

  protected Integer getColumn(String name){
    return(m_nameToColumn.get(name));
  }

  protected void renameColumn(String oldn, String newn){
    //curr does not work , need to change in row
    Integer col=getColumn(oldn);
    if (col != null){
      setColumn(col,newn);
      m_nameToColumn.remove(oldn);
    }
    for (DataRow dr:m_rows){
      dr.renameColumn(oldn, newn);
    }
  }

  /**
   * Unquotes the given string.
   *
   * @param s		the string to unquote, if necessary
   * @return		the processed string
   */
  protected String unquote(String s) {
    String	result;

    result = Utils.unquote(s);
    result = Utils.unDoubleQuote(result);

    return result;
  }

  /**
   * Breaks up a line from a CSV file into its cells.
   *
   * @param line	the row to break up
   * @return		the cells
   */
  protected String[] breakUp(String line) {
    Vector<String>	result;
    int			i;
    StringBuilder	current;
    boolean		escaped;
    char		escapeChr;
    char		chr;

    result    = new Vector<String>();
    current   = new StringBuilder();
    escaped   = false;
    escapeChr = '\0';

    for (i = 0; i < line.length(); i++) {
      chr = line.charAt(i);

      if ((chr == ',') || (chr == '\t')) {
	if (escaped) {
	  current.append(chr);
	}
	else {
	  result.add(unquote(current.toString()));
	  current = new StringBuilder();
	}
      }
      else if ((chr == '"')) {
	if ((i > 0) && (line.charAt(i - 1) == '\\')) {
	  current.append(chr);
	}
	else {
	  if (escaped && (escapeChr == chr)) {
	    escaped = false;
	    current.append(chr);
	  }
	  else {
	    escaped   = true;
	    escapeChr = chr;
	    current.append(chr);
	  }
	}
      }
      else {
	current.append(chr);
      }
    }

    // add last cell
    result.add(unquote(current.toString()));

    return result.toArray(new String[result.size()]);
  }

  /**
   * Read a line, ensuring that a newline isn't within quotes.
   * Look for single quote.
   * 
   * @param r	BufferedReader
   * @return	Line from file, including cr/lf if in quotes.
   */
  protected String readLine(BufferedReader r){
    StringBuffer result=new StringBuffer();
    boolean reading=true;

    try{
      int count=0;
      while(reading){
	String res=r.readLine();
	if (res == null){
	  return(null);
	}
	result.append(res);
	// count quotes, see if uneven
	reading=false;

	for (int i=res.length()-1;i>=0;i--){
	  if (res.charAt(i)=='\"'){	    
	    count++;	    
	  }
	}
	if ((count & 1) == 1){
	  reading=true;
	}
      }
    }
    catch (Exception e) {
      result = null;
      e.printStackTrace();
    }

    return result.toString();
  }

  public void requireString(String columnname, String value){
    for (int row=0;row<count();row++){
      DataRow dr=get(row);
      BaseData cell=dr.get(columnname);
      if (cell == null){
	continue;
      }
      if (!value.equals(cell.toString())){
	addError(row,columnname);
      }
    }
  }

  public void requireString(String columnname, String[] values){
    for (int row=0;row<count();row++){
      DataRow dr=get(row);
      BaseData cell=dr.get(columnname);
      if (cell == null){
	continue;
      }
      boolean found=false;
      for (String s:values) {
	if (s.equals(cell.toString())){
	  found=true;
	  break;
	}
      }
      if (!found){
	addError(row,columnname);
      }
    }
  }


  public void requireInteger(String[] columnnames){
    for (String s:columnnames){
      requireInteger(s);
    }
  }



  public void requireNotEmpty(String[] columnnames){
    for (String s:columnnames){
      requireNotEmpty(s);
    }
  }

  public void requireInteger(String columnname, int value){
    for (int row=0;row<count();row++){
      DataRow dr=get(row);
      BaseData cell=dr.get(columnname);
      if (cell == null){
	continue;
      }
      try{
	Integer pi=Integer.parseInt(cell.toString());
	if (pi == value) {
	  dr.set(columnname, pi);
	} else {
	  addError(row,columnname);
	}
      } catch (Exception e){
	addError(row,columnname);
	//System.err.println("Error line:"+row+" "+cell.toString());
      }
    }
  }

  public void requireInteger(String columnname){
    for (int row=0;row<count();row++){
      DataRow dr=get(row);
      BaseData cell=dr.get(columnname);
      if (cell == null){
	continue;
      }
      try{
	Integer pi=Integer.parseInt(cell.toString());
	dr.set(columnname, pi);
      } catch (Exception e){
	addError(row,columnname);
	//System.err.println("Error line:"+row+" "+cell.toString());
      }
    }
  }

  public void requireDouble(String columnname){
    for (int row=0;row<count();row++){
      DataRow dr=get(row);
      BaseData cell=dr.get(columnname);
      if (cell == null){
	continue;
      }
      try{
	Double pi=Double.parseDouble(cell.toString());
	dr.set(columnname, pi);
      } catch (Exception e){
	addError(row,columnname);
	//System.err.println("Error line:"+row+" "+cell.toString());
      }
    }
  }

  public void requireNotEmpty(String columnname){
    for (int row=0;row<count();row++){
      DataRow dr=get(row);
      BaseData cell=dr.get(columnname);
      if (cell == null || cell.toString().equals("")){
	addError(row,columnname);
	//System.err.println("Empty:"+row+" "+cell.toString());
      }
    }
  }

  public void requireFloat(String columnname){
    for (int row=0;row<count();row++){
      DataRow dr=get(row);
      BaseData cell=dr.get(columnname);
      if (cell == null){
	continue;
      }
      try{
	Float pf=Float.parseFloat(cell.toString());
	dr.set(columnname, pf);
      }catch (Exception e){
	addError(row,columnname);
	//System.err.println("Error line:"+row+" "+cell.toString());
      }

    }
  }

  public CSVDataset dateMinMax(String columnname, BaseData.Date min, BaseData.Date max){
    CSVDataset ret=new CSVDataset();

    for (int row=0;row<count();row++){
      if (m_errors.get(row) != null){
	continue;
      }
      DataRow dr=get(row);
      BaseData cell=dr.get(columnname);
      if (cell == null || !cell.isDate()){
	continue;
      }
      if (((BaseData.Date)cell.getData()).between(min,max)){
	ret.add(dr.copy());
      }
    }
    return(ret);
  }

  public CSVDataset getWithIntValue(String columnname, Integer val){
    CSVDataset ret=new CSVDataset();

    for (int row=0;row<count();row++){
      if (m_errors.get(row) != null){
	continue;
      }
      DataRow dr=get(row);
      BaseData cell=dr.get(columnname);
      if (cell == null || !cell.isNumeric()){
	continue;
      }
      if (cell.isNumeric() && val.equals(cell.getData())){
	ret.add(dr.copy());
      }
    }
    return(ret);
  }


  @Override
  public CSVDataset getWithStringValue(String columnname, String val){
    CSVDataset ret=new CSVDataset();

    for (int row=0;row<count();row++){
      if (m_errors.get(row) != null){
	continue;
      }
      DataRow dr=get(row);
      BaseData cell=dr.get(columnname);
      if (cell == null){
	continue;
      }
      if (val.equals(cell.getData().toString())){
	ret.add(dr.copy());
      }
    }
    return(ret);
  }


  public void requireDate(String columnname,String ps){
    for (int row=0;row<count();row++){
      DataRow dr=get(row);
      BaseData cell=dr.get(columnname);
      if (cell == null){
	continue;
      }
      SimpleDateFormat formatter = new SimpleDateFormat(ps);
      formatter.setLenient(true);
      Date dateStr;
      try {
	dateStr = formatter.parse(cell.toString());
	Calendar cal = Calendar.getInstance();
	cal.setTime(dateStr);
	int month = cal.get(Calendar.MONTH);
	BaseData.Date date=new BaseData.Date(cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.MONTH)+1,cal.get(Calendar.YEAR));

	dr.set(columnname, date);
      } catch (ParseException e) {
	// TODO Auto-generated catch block
	addError(row,columnname);
	System.err.println("Error line:"+row+" "+cell.toString());
	//dr.set(columnname,(String)null);

      }

    }

  }
  public void requireTime(String columnname, String ps){
    for (int row=0;row<count();row++){
      DataRow dr=get(row);
      BaseData cell=dr.get(columnname);
      if (cell == null){
	continue;
      }
      SimpleDateFormat formatter = new SimpleDateFormat(ps);
      Date dateStr;
      try {
	dateStr = formatter.parse(ps);
	BaseData.Time date=new BaseData.Time(dateStr.getHours(),dateStr.getMinutes(),dateStr.getSeconds(),0);
	dr.set(columnname, date);
      } catch (ParseException e) {
	// TODO Auto-generated catch block
	System.err.println("Error line:"+row+" ");
	dr.set(columnname,null);
      }

    }
  }

  public String showErrors(){
    StringBuffer sb=new StringBuffer();
    for (int row=0;row<count();row++){
      Vector<String> errs=m_errors.get(row);
      if (errs == null){
	continue;
      }
      DataRow dr=get(row);
      int i=0;
      sb.append(row+"  ");
      for (String s:m_nameToColumn.keySet()){
	sb.append(s);
	for (String c:errs){
	  if (c.equals(s)){
	    sb.append("*");
	  }
	}
	sb.append(":");
	BaseData bd=dr.get(s);
	if (bd != null){
	  if (bd.isString()){
	    sb.append("\"");
	    sb.append(dr.getObject(s));
	    sb.append("\"");
	  } else {
	    sb.append(dr.getObject(s));
	  }
	}
	if (i != m_nameToColumn.keySet().size()-1){
	  sb.append(",");
	}
	i++;
      }
      sb.append("\n");
    }
    return(sb.toString());
  }

  public String toCSV(String[] columns, boolean showErrors){

    StringBuffer sb=new StringBuffer();

    for (int i=0;i<columns.length;i++){
      sb.append(columns[i]);
      if (i != columns.length-1){
	sb.append(",");
      }
    }
    sb.append("\n");
    for (int row=0;row<count();row++){
      if (m_errors.get(row) != null){
	continue;
      }
      DataRow dr=get(row);
      for (int i=0;i<columns.length;i++){
	BaseData bd=dr.get(columns[i]);
	if (bd != null){
	  if (bd.isString()){
	    sb.append("\"");
	    sb.append(dr.getObject(columns[i]));
	    sb.append("\"");
	  } else {
	    sb.append(dr.getObject(columns[i]));
	  }
	}
	if (i != columns.length-1){
	  sb.append(",");
	}
      }
      sb.append("\n");
    }

    return(sb.toString());
  }

  public boolean loadCSV(Reader r){
    String		line;
    String[]		cells;
    String[]		headerCells;
    BufferedReader	reader;
    boolean result=true;

    Vector<Integer> ignore=new Vector<Integer>();

    m_columnToName= new Hashtable<Integer, String>();
    m_nameToColumn= new Hashtable<String, Integer>();

    m_errors = new Hashtable<Integer, Vector<String>>();

    if (r instanceof BufferedReader)
      reader = (BufferedReader) r;
    else
      reader = new BufferedReader(r);

    try {

      headerCells = null;
      while ((line = readLine(reader)) != null) {

	if (line.trim().length() == 0)
	  continue;
	cells = breakUp(line);
	if (headerCells == null) {
	  headerCells = cells;
	  for (int i=0;i<headerCells.length;i++){
	    if (columnPresent(headerCells[i])){
	      System.err.println("Warning ignoring duplicate column:"+i);
	      ignore.add(i);
	      continue;
	    }
	    if (headerCells[i].equals("")){
	      System.err.println("Warning empty column header:"+i);
	      continue;
	    }
	    this.setColumn(i, headerCells[i]);
	  }
	}
	else {
	  DataRow dr=new DataRow();
	  add(dr);
	  for (int i=0;i<cells.length;i++){
	    for (int ig:ignore){
	      if (i==ig){
		continue;
	      }
	    }
	    String nm=m_columnToName.get(i);
	    if (nm==null){
	      nm="Column"+i;
	    }
	    if (cells[i] != null && !cells[i].equals("")){
	      dr.set(nm, cells[i]);
	    }
	  }
	}	
      }
    }
    catch (Exception e) {
      result = false;
      e.printStackTrace();
    }

    return result;
  }

  public void save(String string, String[] summariseHeader) {
    FileUtils.writeToFile(string, toCSV(summariseHeader,false));
  }
}
