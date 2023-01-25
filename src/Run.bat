java -jar jflex-1.6.1.jar Lexer.flex
yacc.exe -Jthrows="Exception" -Jextends=ParserImpl -Jclass=Parser -Jnorun -J Parser.y

"c:\Program Files\Java\jdk-11.0.6\bin\javac.exe" *.java

java TestEnv

"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/syntaxerr_01_main.minc          > ../myoutput/myoutput_syntaxerr_01_main.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/syntaxerr_02_expr1.minc         > ../myoutput/myoutput_syntaxerr_02_expr1.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/syntaxerr_03_expr2.minc         > ../myoutput/myoutput_syntaxerr_03_expr2.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/syntaxerr_04_stmt.minc          > ../myoutput/myoutput_syntaxerr_04_stmt.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/syntaxerr_05_func1.minc         > ../myoutput/myoutput_syntaxerr_05_func1.txt

"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_01_main_fail1.minc         > ../myoutput/myoutput_test_01_main_fail1.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_01_main_fail2.minc         > ../myoutput/myoutput_test_01_main_fail2.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_01_main_succ.minc          > ../myoutput/myoutput_test_01_main_succ.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_02_expr1_fail1.minc        > ../myoutput/myoutput_test_02_expr1_fail1.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_02_expr1_fail2.minc        > ../myoutput/myoutput_test_02_expr1_fail2.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_02_expr1_fail3.minc        > ../myoutput/myoutput_test_02_expr1_fail3.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_02_expr1_fail4.minc        > ../myoutput/myoutput_test_02_expr1_fail4.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_02_expr1_fail5.minc        > ../myoutput/myoutput_test_02_expr1_fail5.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_02_expr1_fail6.minc        > ../myoutput/myoutput_test_02_expr1_fail6.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_02_expr1_succ.minc         > ../myoutput/myoutput_test_02_expr1_succ.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_03_expr2_fail1.minc        > ../myoutput/myoutput_test_03_expr2_fail1.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_03_expr2_fail2.minc        > ../myoutput/myoutput_test_03_expr2_fail2.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_03_expr2_fail3.minc        > ../myoutput/myoutput_test_03_expr2_fail3.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_03_expr2_fail4.minc        > ../myoutput/myoutput_test_03_expr2_fail4.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_03_expr2_succ.minc         > ../myoutput/myoutput_test_03_expr2_succ.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_04_stmt_fail1.minc         > ../myoutput/myoutput_test_04_stmt_fail1.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_04_stmt_fail2.minc         > ../myoutput/myoutput_test_04_stmt_fail2.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_04_stmt_fail3.minc         > ../myoutput/myoutput_test_04_stmt_fail3.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_04_stmt_fail4.minc         > ../myoutput/myoutput_test_04_stmt_fail4.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_04_stmt_succ.minc          > ../myoutput/myoutput_test_04_stmt_succ.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_05_func1_fail1.minc        > ../myoutput/myoutput_test_05_func1_fail1.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_05_func1_fail2.minc        > ../myoutput/myoutput_test_05_func1_fail2.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_05_func1_succ.minc         > ../myoutput/myoutput_test_05_func1_succ.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_06_func2_fail1.minc        > ../myoutput/myoutput_test_06_func2_fail1.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_06_func2_fail2.minc        > ../myoutput/myoutput_test_06_func2_fail2.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_06_func2_fail3.minc        > ../myoutput/myoutput_test_06_func2_fail3.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_06_func2_fail4.minc        > ../myoutput/myoutput_test_06_func2_fail4.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_06_func2_fail5.minc        > ../myoutput/myoutput_test_06_func2_fail5.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_06_func2_succ.minc         > ../myoutput/myoutput_test_06_func2_succ.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_07_func3_fail1.minc        > ../myoutput/myoutput_test_07_func3_fail1.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_07_func3_succ.minc         > ../myoutput/myoutput_test_07_func3_succ.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_08_advanced1_succ.minc     > ../myoutput/myoutput_test_08_advanced1_succ.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_08_advanced2_succ.minc     > ../myoutput/myoutput_test_08_advanced2_succ.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_09_advanced3_fail1.minc    > ../myoutput/myoutput_test_09_advanced3_fail1.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_09_advanced3_succ.minc     > ../myoutput/myoutput_test_09_advanced3_succ.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_10_scope1_fail1.minc       > ../myoutput/myoutput_test_10_scope1_fail1.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_10_scope1_fail2.minc       > ../myoutput/myoutput_test_10_scope1_fail2.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_10_scope1_fail3.minc       > ../myoutput/myoutput_test_10_scope1_fail3.txt
"c:\Program Files\Java\jdk-11.0.6\bin\java.exe"  SemanticChecker    ../minc/test_10_scope1_succ.minc        > ../myoutput/myoutput_test_10_scope1_succ.txt
