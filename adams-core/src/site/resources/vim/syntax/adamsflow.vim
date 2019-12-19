" Vim syntax file
" Language:	ADAMS flow file
" Maintainer:	fracpete
" Last Change:	2019 Dec 19

" Syntax highlighting for ADAMS flow files

" For version 5.x: Clear all syntax items
" For version 6.x: Quit when a syntax file was already loaded
if version < 600
  syntax clear
elseif exists("b:current_syntax")
  finish
endif

syn case ignore

" keywords
syn match adamsClassname "^[\t ]*\([a-zA-Z_$][a-zA-Z0-9_$]*\.\)*[a-zA-Z_$][a-zA-Z0-9_$]*"
syn match adamsClassname2 " \([a-zA-Z_$][a-zA-Z0-9_$]*\.\)*[a-zA-Z_$][a-zA-Z0-9_$]*"
syn match adamsOption	" -[^ ][^ ]*"

" numbers
syn match  adamsNumber	"-\=\<\d\+\>"
syn match  adamsFloat	"-\=\<\d\+\.\d\+\>"
syn match  adamsFloat	"-\=\<\d\+\.\d\+[eE]-\=\d\+\>"

" date/time
syn match  adamsDate	"\d\d\d\d-\d\d-\d\d"
syn match  adamsTime	"\d\d:\d\d:\d\d"

" symbols
syn match  adamsSymbol	"="
syn match  adamsSymbol	"/"
syn match  adamsSymbol	"\\t"
syn match  adamsSymbol	"\\n"
syn match  adamsSymbol	"\\r"
syn match  adamsSymbol	"\^"
syn match  adamsSymbol	"\$"
syn match  adamsSymbol	"@"
syn match  adamsSymbol	"\.\*"
syn match  adamsSymbol   "{[^\}]*}"
syn region  adamsString        start=+"+  skip=+\\\\\|\\$"+  end=+"+

" comments
syn match  adamsComment	"#.*"

" Define the default highlighting.
" For version 5.7 and earlier: only when not done already
" For version 5.8 and later: only when an item doesn't have highlighting yet
if version >= 508 || !exists("did_flows_syn_inits")
  if version < 508
    let did_adams_syn_inits = 1
    command -nargs=+ HiLink hi link <args>
  else
    command -nargs=+ HiLink hi def link <args>
  endif

  HiLink adamsComment	Comment
  HiLink adamsClassname	Type
  HiLink adamsClassname2	Type
  HiLink adamsOption	Statement
  HiLink adamsSymbol	Special
  HiLink adamsFloat	Float
  HiLink adamsNumber	Number
  HiLink adamsDate	String
  HiLink adamsTime	String
  HiLink adamsString     String

  delcommand HiLink
endif

let b:current_syntax = "flows"

" vim: ts=8
