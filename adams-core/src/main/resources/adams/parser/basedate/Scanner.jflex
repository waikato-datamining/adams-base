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
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.parser.basedate;

import adams.core.DateFormat;
import adams.core.base.BaseDate;

import java_cup.runtime.SymbolFactory;
import java.io.*;
import java.util.*;

/**
 * A scanner for date expressions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
%%
%caseless
%cup
%public
%class Scanner
%{
  // Author: FracPete (fracpete at waikato dot ac dot nz)
  // Version: $Revision$
  protected SymbolFactory sf;

  protected static DateFormat m_Format;
  static {
    m_Format = new DateFormat(BaseDate.FORMAT);
  }

  public Scanner(InputStream r, SymbolFactory sf){
    this(r);
    this.sf = sf;
  }
%}
%eofval{
    return sf.newSymbol("EOF",sym.EOF);
%eofval}

%%
// operands
"-" { return sf.newSymbol("Minus", sym.MINUS); }
"+" { return sf.newSymbol("Plus", sym.PLUS); }
"*" { return sf.newSymbol("Times", sym.TIMES); }
"/" { return sf.newSymbol("Division", sym.DIVISION); }
"^" { return sf.newSymbol("Power", sym.EXPONENT); }
"%" { return sf.newSymbol("Modulo", sym.MODULO); }

// functions
"abs" { return sf.newSymbol("Abs", sym.ABS); }
"sqrt" { return sf.newSymbol("Sqrt", sym.SQRT); }
"log" { return sf.newSymbol("Log", sym.LOG); }
"exp" { return sf.newSymbol("Exp", sym.EXP); }
"rint" { return sf.newSymbol("Rint", sym.RINT); }
"floor" { return sf.newSymbol("Floor", sym.FLOOR); }
"power" { return sf.newSymbol("Pow", sym.POW); }
"pow" { return sf.newSymbol("Pow", sym.POW); }
"ceil" { return sf.newSymbol("Ceil", sym.CEIL); }

// types
"DAY"    { return sf.newSymbol("Day" ,   sym.TYPE, new Amount(Calendar.HOUR,     24)); }
"WEEK"   { return sf.newSymbol("Week",   sym.TYPE, new Amount(Calendar.HOUR,   24*7)); }
"MONTH"  { return sf.newSymbol("Month",  sym.TYPE, new Amount(Calendar.MONTH,     1)); }
"YEAR"   { return sf.newSymbol("Year",   sym.TYPE, new Amount(Calendar.YEAR,      1)); }

// numbers
[0-9]*\.?[0-9]+(E(-)?[1-9][0-9]*)? { return sf.newSymbol("Number", sym.NUMBER, new Double(yytext())); }

// date/time
[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9] { return sf.newSymbol("Date", sym.DATE_ACTUAL, m_Format.parse(yytext())); }
"-INF"  { return sf.newSymbol("-INF",  sym.DATE_ACTUAL, m_Format.parse(BaseDate.INF_PAST_DATE)); }
"+INF"  { return sf.newSymbol("+INF",  sym.DATE_ACTUAL, m_Format.parse(BaseDate.INF_FUTURE_DATE)); }
"NOW"   { return sf.newSymbol("Now",   sym.DATE_ACTUAL, new Date()); }
"START" { return sf.newSymbol("Start", sym.DATE_START,  m_Format.parse(BaseDate.INF_PAST_DATE)); }
"END"   { return sf.newSymbol("End",   sym.DATE_END,    m_Format.parse(BaseDate.INF_FUTURE_DATE)); }

// various
"(" { return sf.newSymbol("Left Bracket", sym.LPAREN); }
")" { return sf.newSymbol("Right Bracket", sym.RPAREN); }
"," { return sf.newSymbol("Comma", sym.COMMA); }

// whitespaces
[ \r\n\t\f] { /* ignore white space. */ }

// catch all
. { System.err.println("Illegal character: " + yytext()); }
