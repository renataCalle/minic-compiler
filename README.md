# MiniC-compiler

A compiler for the mini-C language  MiniC is a simple subset of the standard C language. It does NOT include arrays, structs, unions, files, sets, switch statements, do statements, or many of the low level operators.

### How to build
Run the following commands in this order:

    java -jar jflex-1.6.1.jar Lexer.flex
    ./yacc.exe -Jthrows="Exception" -Jextends=ParserImpl -Jnorun -J ./Parser.y
    javac *.java

### How to run
Run the command:

    java SemanticChecker test.minc
    
where test.minc is the MiniC program
