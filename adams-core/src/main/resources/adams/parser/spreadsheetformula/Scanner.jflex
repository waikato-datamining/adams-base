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
 * Copyright (C) 2008-2024 University of Waikato, Hamilton, New Zealand
 */

package adams.parser.spreadsheetformula;

import java_cup.runtime.SymbolFactory;
import java.io.*;

/**
 * A scanner for spreadsheet formulas.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
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
  // operands
  "-" { return sf.newSymbol("Minus", sym.MINUS); }
  "+" { return sf.newSymbol("Plus", sym.PLUS); }
  "*" { return sf.newSymbol("Times", sym.TIMES); }
  "/" { return sf.newSymbol("Division", sym.DIVISION); }
  "^" { return sf.newSymbol("Power", sym.EXPONENT); }
  "%" { return sf.newSymbol("Modulo", sym.MODULO); }
  ":" { return sf.newSymbol("Modulo", sym.COLON); }

  // boolean stuff
  "<" { return sf.newSymbol("Less than", sym.LT); }
  "<=" { return sf.newSymbol("Less or equal than", sym.LE); }
  ">" { return sf.newSymbol("Greater than", sym.GT); }
  ">=" { return sf.newSymbol("Greater or equal than", sym.GE); }
  "=" { return sf.newSymbol("Equals", sym.EQ); }
  "!=" { return sf.newSymbol("Not qquals", sym.NOT_EQ); }
  "<>" { return sf.newSymbol("Not qquals", sym.NOT_EQ); }
  "!" { return sf.newSymbol("Not", sym.NOT); }
  "not" { return sf.newSymbol("Not", sym.NOT); }
  "&" { return sf.newSymbol("And", sym.AND); }
  "and" { return sf.newSymbol("And", sym.AND); }
  "|" { return sf.newSymbol("Or", sym.OR); }
  "or" { return sf.newSymbol("Or", sym.OR); }
  "true" { return sf.newSymbol("True", sym.TRUE); }
  "false" { return sf.newSymbol("False", sym.FALSE); }

  // functions
  "abs" { return sf.newSymbol("Abs", sym.ABS); }
  "sqrt" { return sf.newSymbol("Sqrt", sym.SQRT); }
  "log" { return sf.newSymbol("Log", sym.LOG); }
  "exp" { return sf.newSymbol("Exp", sym.EXP); }
  "sin" { return sf.newSymbol("Sin", sym.SIN); }
  "cos" { return sf.newSymbol("Cos", sym.COS); }
  "tan" { return sf.newSymbol("Tan", sym.TAN); }
  "rint" { return sf.newSymbol("Rint", sym.RINT); }
  "floor" { return sf.newSymbol("Floor", sym.FLOOR); }
  "pow" { return sf.newSymbol("Pow", sym.POW); }
  "power" { return sf.newSymbol("Pow", sym.POW); }
  "ceil" { return sf.newSymbol("Ceil", sym.CEIL); }
  "ifelse" { return sf.newSymbol("IfElse", sym.IFELSE); }
  "if" { return sf.newSymbol("IfElse", sym.IFELSE); }
  "length" { return sf.newSymbol("Length", sym.LENGTH); }
  "len" { return sf.newSymbol("Length", sym.LENGTH); }
  "trim" { return sf.newSymbol("Trim", sym.TRIM); }
  "substr" { return sf.newSymbol("Substr", sym.SUBSTR); }
  "left" { return sf.newSymbol("Left", sym.LEFT); }
  "right" { return sf.newSymbol("Right", sym.RIGHT); }
  "mid" { return sf.newSymbol("Mid", sym.MID); }
  "replace" { return sf.newSymbol("Replace", sym.REPLACE); }
  "substitute" { return sf.newSymbol("Substitute", sym.SUBSTITUTE); }
  "lowercase" { return sf.newSymbol("LowerCase", sym.LOWERCASE); }
  "lower" { return sf.newSymbol("LowerCase", sym.LOWERCASE); }
  "uppercase" { return sf.newSymbol("UpperCase", sym.UPPERCASE); }
  "upper" { return sf.newSymbol("UpperCase", sym.UPPERCASE); }
  "matches" { return sf.newSymbol("Matches", sym.MATCHES); }
  "find" { return sf.newSymbol("Find", sym.FIND); }
  "contains" { return sf.newSymbol("Contains", sym.CONTAINS); }
  "startswith" { return sf.newSymbol("StartsWith", sym.STARTSWITH); }
  "endswith" { return sf.newSymbol("EndsWith", sym.ENDSWITH); }
  "concatenate" { return sf.newSymbol("Concantenate", sym.CONCATENATE); }
  "rept" { return sf.newSymbol("repeat", sym.REPEAT); }
  "sum" { return sf.newSymbol("Sum", sym.SUM); }
  "min" { return sf.newSymbol("Min", sym.MIN); }
  "max" { return sf.newSymbol("Max", sym.MAX); }
  "average" { return sf.newSymbol("Average", sym.AVERAGE); }
  "median" { return sf.newSymbol("Median", sym.MEDIAN); }
  "stdev" { return sf.newSymbol("Stdev", sym.STDEV); }
  "stdevp" { return sf.newSymbol("StdevP", sym.STDEVP); }
  "countif" { return sf.newSymbol("CountIf", sym.COUNTIF); }
  "countblank" { return sf.newSymbol("CountBlank", sym.COUNTBLANK); }
  "sumif" { return sf.newSymbol("SumIf", sym.SUMIF); }
  "intercept" { return sf.newSymbol("Intercept", sym.INTERCEPT); }
  "slope" { return sf.newSymbol("Slope", sym.SLOPE); }
  "now" { return sf.newSymbol("Now", sym.NOW); }
  "today" { return sf.newSymbol("Today", sym.TODAY); }
  "year" { return sf.newSymbol("Year", sym.YEAR); }
  "month" { return sf.newSymbol("Month", sym.MONTH); }
  "day" { return sf.newSymbol("Day", sym.DAY); }
  "hour" { return sf.newSymbol("Hour", sym.HOUR); }
  "minute" { return sf.newSymbol("Minute", sym.MINUTE); }
  "second" { return sf.newSymbol("Second", sym.SECOND); }
  "weeknum" { return sf.newSymbol("WeekNum", sym.WEEKNUM); }
  "weekday" { return sf.newSymbol("WeekDay", sym.WEEKDAY); }
  "cellobj" { return sf.newSymbol("Cell object", sym.CELL_OBJECT); }
  "cellstr" { return sf.newSymbol("Cell string", sym.CELL_STRING); }
  "f_"[a-zA-Z0-9_]+ { return sf.newSymbol("Function", sym.FUNCTION, yytext().substring(2)); }
  "p_"[a-zA-Z0-9_]+ { return sf.newSymbol("Procedure", sym.PROCEDURE, yytext().substring(2)); }

  // constants
  "PI" { return sf.newSymbol("pi", sym.PI); }
  "E" { return sf.newSymbol("e", sym.E); }

  // numbers and variables
  [0-9]*\.?[0-9]+(E(-)?[1-9][0-9]*)? { return sf.newSymbol("Number", sym.NUMBER, Double.parseDouble(yytext())); }
  "-Infinity" { return sf.newSymbol("Number", sym.NUMBER, Double.NEGATIVE_INFINITY); }
  "Infinity" { return sf.newSymbol("Number", sym.NUMBER, Double.POSITIVE_INFINITY); }
  "NaN" { return sf.newSymbol("Number", sym.NUMBER, Double.NaN); }
  [A-Z]+[0-9]+ { return sf.newSymbol("Cell", sym.CELL, yytext()); }

  // comment
  "#".* { /* ignore line comments. */ }

  // whitespaces
  [ \r\n\t\f] { /* ignore white space. */ }

  // various
  "," { return sf.newSymbol("Comma", sym.COMMA); }
  ";" { return sf.newSymbol("Semicolon", sym.SEMICOLON); }
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
