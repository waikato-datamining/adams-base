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
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.parser.lookupupdate;

import java_cup.runtime.SymbolFactory;
import java.io.*;

/**
 * A scanner for lookup update rules.
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
  "if"    { return sf.newSymbol("If",   sym.IF); }
  "then"  { return sf.newSymbol("Then", sym.THEN); }
  "else"  { return sf.newSymbol("Else", sym.ELSE); }
  "end"   { return sf.newSymbol("End",  sym.END); }

  // symbols
  ":=" { return sf.newSymbol("Assignment", sym.ASSIGNMENT); }
  ";" { return sf.newSymbol("Semi", sym.SEMI); }
  "," { return sf.newSymbol("Comma", sym.COMMA); }

  // operands
  "-" { return sf.newSymbol("Minus", sym.MINUS); }
  "+" { return sf.newSymbol("Plus", sym.PLUS); }
  "*" { return sf.newSymbol("Times", sym.TIMES); }
  "/" { return sf.newSymbol("Division", sym.DIVISION); }
  "^" { return sf.newSymbol("Power", sym.EXPONENT); }
  "%" { return sf.newSymbol("Modulo", sym.MODULO); }

  // functions
  "all" { return sf.newSymbol("All", sym.ALL); }
  "abs" { return sf.newSymbol("Abs", sym.ABS); }
  "sqrt" { return sf.newSymbol("Sqrt", sym.SQRT); }
  "cbrt" { return sf.newSymbol("Cbrt", sym.CBRT); }
  "log" { return sf.newSymbol("Log", sym.LOG); }
  "log10" { return sf.newSymbol("Log10", sym.LOG10); }
  "exp" { return sf.newSymbol("Exp", sym.EXP); }
  "sin" { return sf.newSymbol("Sin", sym.SIN); }
  "sinh" { return sf.newSymbol("SinH", sym.SINH); }
  "cos" { return sf.newSymbol("Cos", sym.COS); }
  "cosh" { return sf.newSymbol("CosH", sym.COSH); }
  "tan" { return sf.newSymbol("Tan", sym.TAN); }
  "tanh" { return sf.newSymbol("TanH", sym.TANH); }
  "atan" { return sf.newSymbol("Atan", sym.ATAN); }
  "atan2" { return sf.newSymbol("Atan2", sym.ATAN2); }
  "hypot" { return sf.newSymbol("Hypot", sym.HYPOT); }
  "signum" { return sf.newSymbol("Signum", sym.SIGNUM); }
  "rint" { return sf.newSymbol("Rint", sym.RINT); }
  "floor" { return sf.newSymbol("Floor", sym.FLOOR); }
  "power" { return sf.newSymbol("Pow", sym.POW); }
  "pow" { return sf.newSymbol("Pow", sym.POW); }
  "ceil" { return sf.newSymbol("Ceil", sym.CEIL); }
  "min" { return sf.newSymbol("Min", sym.MIN); }
  "max" { return sf.newSymbol("Max", sym.MAX); }

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
  "true" { return sf.newSymbol("True", sym.TRUE); }
  "false" { return sf.newSymbol("False", sym.FALSE); }

  // numbers and variables
  "-"?[0-9]*\.?[0-9]+(E(-)?[1-9][0-9]*)? { return sf.newSymbol("Number", sym.NUMBER, new Double(yytext())); }
  [a-zA-Z0-9_\-]+ { return sf.newSymbol("Variable", sym.VARIABLE, new String(yytext())); }
  "'"[^\']+"'" { return sf.newSymbol("Variable", sym.VARIABLE, new String(yytext().replace("[", "").replace("]", ""))); }

  // comment
  "//".* { /* ignore line comments. */ }

  // whitespaces
  [ \r\n\t\f] { /* ignore white space. */ }

  // various
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
