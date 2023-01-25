public class SemanticChecker {
    public static void main(String[] args) throws Exception
    {
        //  java.io.Reader r = new java.io.StringReader
        //  (""
        //  +"func main::()->int\n"
        //  +"begin\n"
        //  +"    return 0;\n"
        //  +"end\n"
        //  );

        //  if(args.length == 0)
        //  args = new String[]
        //  {
        //      "C:\\2019bFall-proj4-minc-SemanticChecker-startup\\sample\\minc\\"
        //      +"test_01_main_succ.minc",
        //  };

        if(args.length <= 0)
            return;
        String minicpath = args[0];
        java.io.Reader r = new java.io.FileReader(minicpath);

        Compiler compiler = new Compiler(r);

        compiler.Parse();
    }
}
