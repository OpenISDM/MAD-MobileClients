# Android Style Guide

## Whitespace

* Indention: 2 spaces (no tabs), 4 spaces for line continuations
* Line Length: 80 characters
* Braces: Same line starting, new line ending, space before opening brace
* Operators: Space before and after
* Parenthesis: No spacing inside

## Comments

* All functions must be prefaced with comments.
* All classes must be prefaced with javadocs.
* No @author tags in javadocs

## Autoformatters

Download the below xml file and save it to your local disk. Import this formatter in eclipse with Window --> Preferences --> Java --> Code Style --> Formatter and choose "Import..." and find this file on disk. Now, when you select code in eclipse, Ctrl + Shift + F will auto format it. Avoid auto-formatting entire classes if you're touching small parts, since it makes the code review unnecessarily large.

* Eclipse: http://omnidroid.googlecode.com/svn/tools/Eclipse_Formatter-ITP_Conventions.xml

## Other Reference
* http://developer.android.com/design/index.html
* http://source.android.com/source/code-style.html
* https://code.google.com/p/google-styleguide/