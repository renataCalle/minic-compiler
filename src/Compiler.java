public class Compiler {
    Parser parser;
    public Compiler(java.io.Reader r)
    {
        parser = new Parser(r, false);
    }
    public void Parse()
    {
        ParserImpl._debug = false;

        try {
            if (parser.yyparse() == 0)
            {
                System.out.println("Success: no semantic error is found.");
                System.out.println();
            }
            else
            {
                System.out.println("Error: there is syntax error(s).");
                return;
            }
        }
        catch(Exception e)
        {
            System.out.println("Error: there is semantic error(s).");
            if(e != null && e.getMessage() != null)
                System.out.println(e.getMessage());
            return;
        }

        // print code with indentation
        try
        {
            int opt = 0; // no-print comments that are for running environment
            String[] lines = parser.parsetree_program.ToStringList(opt);
            System.out.println("================================================================================");
            System.out.println("Code with indentations:");
            for (String line : lines)
                System.out.println(line);
        }
        catch(Exception e)
        {
            System.out.println("================================================================================");
            System.out.print("Fail in printing parse tree.");
            if(e != null && e.getMessage() != null)
                System.out.println(e.getMessage());
            return;
        }

        // print code with indentation and comments
        try
        {
            int opt = 1; // print comments that are for running environment
            String[] lines = parser.parsetree_program.ToStringList(opt);
            System.out.println("================================================================================");
            System.out.println("Code with indentations and comments for running environment:");
            for (String line : lines)
                System.out.println(line);
        }
        catch(Exception e)
        {
            System.out.println("================================================================================");
            System.out.print("Fail in printing parse tree with comments.");
            if(e != null && e.getMessage() != null)
                System.out.println(e.getMessage());
            return;
        }

        // run code from parse tree
        try
        {
            System.out.println("================================================================================");
            System.out.println("Execute:");
            Object ret = parser.parsetree_program.Exec();
            System.out.println("Returned value by main: "+ret);
            System.out.println("================================================================================");
        }
        catch(Exception e)
        {
            System.out.println("================================================================================");
            System.out.print("Fail in executing parse tree.");
            if(e != null && e.getMessage() != null)
                System.out.println(e.getMessage());
            return;
        }
    }
}
