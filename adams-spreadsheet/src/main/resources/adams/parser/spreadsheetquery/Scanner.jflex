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
 * Scanner.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.parser.spreadsheetquery;

import java_cup.runtime.SymbolFactory;
import java.io.*;

/**
 * A scanner for spreadsheet queries.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
%%
%caseless
%unicode
%cup
%public
%class Scanner
%{
  // Author: FracPete (fracpete at waikato dot ac dot nz)
  // Version: $Revision$
  protected SymbolFactory sf;

  public Scanner(InputStream r, SymbolFactory sf){
    this(r);
    this.sf = sf;
  }
%}
%eofval{
    return sf.newSymbol("EOF",sym.EOF);
%eofval}

%{
  StringBuilder string = new StringBuilder();
%}

%state STRING

%%
<YYINITIAL> {
  // key words
  "select"  { return sf.newSymbol("Select",  sym.SELECT); }
  "as"      { return sf.newSymbol("As",      sym.AS); }
  "update"  { return sf.newSymbol("Update",  sym.UPDATE); }
  "delete"  { return sf.newSymbol("Delete",  sym.DELETE); }
  "where"   { return sf.newSymbol("Where",   sym.WHERE); }
  "order"   { return sf.newSymbol("Order",   sym.ORDER); }
  "by"      { return sf.newSymbol("By",      sym.BY); }
  "asc"     { return sf.newSymbol("Asc",     sym.ASC); }
  "desc"    { return sf.newSymbol("Desc",    sym.DESC); }
  "set"     { return sf.newSymbol("Set",     sym.SET); }
  "is"      { return sf.newSymbol("Is",      sym.IS); }
  "null"    { return sf.newSymbol("Null",    sym.NULL); }
  "regexp"  { return sf.newSymbol("RegExp",  sym.REGEXP); }
  "limit"   { return sf.newSymbol("Limit",   sym.LIMIT); }
  "group"   { return sf.newSymbol("Group",   sym.GROUP); }
  "having"  { return sf.newSymbol("Having",  sym.HAVING); }
  "count"   { return sf.newSymbol("Count",   sym.COUNT); }
  "min"     { return sf.newSymbol("Min",     sym.MIN); }
  "max"     { return sf.newSymbol("Max",     sym.MAX); }
  "mean"    { return sf.newSymbol("Mean",    sym.AVERAGE); }
  "average" { return sf.newSymbol("Average", sym.AVERAGE); }
  "median"  { return sf.newSymbol("Median",  sym.MEDIAN); }
  "stdev"   { return sf.newSymbol("StdDev",  sym.STDEV); }
  "stdevp"  { return sf.newSymbol("StDevP",  sym.STDEVP); }
  "sum"     { return sf.newSymbol("Sum",     sym.SUM); }
  "iqr"     { return sf.newSymbol("IQR",     sym.IQR); }
  "interquartile" { return sf.newSymbol("IQR", sym.IQR); }
  
  // operands
  "*" { return sf.newSymbol("All", sym.ALL); }

  // boolean stuff
  "<" { return sf.newSymbol("Less than", sym.LT); }
  "<=" { return sf.newSymbol("Less or equal than", sym.LE); }
  ">" { return sf.newSymbol("Greater than", sym.GT); }
  ">=" { return sf.newSymbol("Greater or equal than", sym.GE); }
  "=" { return sf.newSymbol("Equals", sym.EQ); }
  "<>" { return sf.newSymbol("Not qquals", sym.NOT_EQ); }
  "!=" { return sf.newSymbol("Not qquals", sym.NOT_EQ); }
  "not" { return sf.newSymbol("Not", sym.NOT); }
  "and" { return sf.newSymbol("And", sym.AND); }
  "or" { return sf.newSymbol("Or", sym.OR); }

  // numbers and variables
  [0-9]*\.?[0-9]+(E(-)?[1-9][0-9]*)? { return sf.newSymbol("Number", sym.NUMBER, new Double(yytext())); }
  [a-zA-Z0-9_\-]+ { return sf.newSymbol("Column", sym.COLUMN, new String(yytext())); }

  // comment
  "--".* { /* ignore line comments. */ }

  // whitespaces
  [ \r\n\t\f] { /* ignore white space. */ }

  // various
  "," { return sf.newSymbol("Comma", sym.COMMA); }
  "(" { return sf.newSymbol("Left Bracket", sym.LPAREN); }
  ")" { return sf.newSymbol("Right Bracket", sym.RPAREN); }

  // strings
  \"  { string.setLength(0); yybegin(STRING); }
}

<STRING> {
  \"            { yybegin(YYINITIAL);
                  return sf.newSymbol("String", sym.STRING, string.toString()); }
  [^\n\r\"\\]+  { string.append(yytext()); }
  \\t           { string.append('\t'); }
  \\n           { string.append('\n'); }
  \\r           { string.append('\r'); }
  \\\"          { string.append('\"'); }
  \\            { string.append('\\'); }
}

// catch all
. { System.err.println("Illegal character: "+yytext()); }
