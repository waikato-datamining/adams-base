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
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.parser.actorsuggestion;

import java_cup.runtime.SymbolFactory;
import java.io.*;
import java.util.*;

/**
 * A scanner for parsing actor suggestion rules.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
%%
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
  // keywords
  "AND"         { return sf.newSymbol("And",         sym.AND); }
  "OR"          { return sf.newSymbol("Or",          sym.OR); }
  "BEFORE"      { return sf.newSymbol("Before",      sym.BEFORE); }
  "AFTER"       { return sf.newSymbol("After",       sym.AFTER); }
  "PARENT"      { return sf.newSymbol("Parent" ,     sym.PARENT); }
  "ANYPARENT"   { return sf.newSymbol("AnyParent" ,  sym.ANYPARENT); }
  "PRECEDING"   { return sf.newSymbol("Preceding" ,  sym.PRECEDING); }
  "FOLLOWING"   { return sf.newSymbol("Following" ,  sym.FOLLOWING); }
  "ISFIRST"     { return sf.newSymbol("First",       sym.ISFIRST); }
  "ISLAST"      { return sf.newSymbol("First",       sym.ISLAST); }
  "ALLOWS"      { return sf.newSymbol("Allows",      sym.ALLOWS); }
  "STANDALONE"  { return sf.newSymbol("Standalone",  sym.STANDALONE); }
  "SOURCE"      { return sf.newSymbol("Source",      sym.SOURCE); }
  "TRANSFORMER" { return sf.newSymbol("Transformer", sym.TRANSFORMER); }
  "SINK"        { return sf.newSymbol("Sink",        sym.SINK); }
  "IS"          { return sf.newSymbol("Is",          sym.IS); }
  "LIKE"        { return sf.newSymbol("Like",        sym.LIKE); }
  "GENERATES"   { return sf.newSymbol("Generates",   sym.GENERATES); }
  "ACCEPTS"     { return sf.newSymbol("Accepts",     sym.ACCEPTS); }
  "NOT"         { return sf.newSymbol("Not",         sym.NOT); }
  "IF"          { return sf.newSymbol("If",          sym.IF); }
  "THEN"        { return sf.newSymbol("Then",        sym.THEN); }
  "TRUE"        { return sf.newSymbol("True",        sym.TRUE); }
  "FALSE"       { return sf.newSymbol("False",       sym.FALSE); }

  // classname
  ([a-zA-Z_$][0-9a-zA-Z\d_$]*\.)*[a-zA-Z_$][0-9a-zA-Z\d_$]* { return sf.newSymbol("Classname", sym.CLASSNAME, new String(yytext())); }

  // parentheses
  "(" { return sf.newSymbol("Left Bracket",  sym.LPAREN); }
  ")" { return sf.newSymbol("Right Bracket", sym.RPAREN); }

  // whitespaces
  [ \r\n\t\f] { /* ignore white space. */ }

  // strings
  \"  { string.setLength(0); yybegin(STRING); }
}

<STRING> {
  \"            { yybegin(YYINITIAL);
                  return sf.newSymbol("Classname", sym.CMDLINE, string.toString()); }
  [^\n\r\"\\]+  { string.append(yytext()); }
  \\t           { string.append('\t'); }
  \\n           { string.append('\n'); }
  \\r           { string.append('\r'); }
  \\\"          { string.append('\"'); }
  \\            { string.append('\\'); }
}

// catch all
. { System.err.println("Illegal character: "+yytext()); }
