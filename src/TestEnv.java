public class TestEnv
{
    public static int total = 0;
    public static void Assert(boolean condition, String testname)
    {
        if(condition)
        {
            total += 3;
            System.out.println("Succeed (3 points) in " + testname);
        }
        else
            System.out.println("Fail (0 point) in " + testname);
    }

    public static void main(String[] args) throws Exception
    {
        Env env = new Env(null);
        total = 0;

        System.out.println("initial scope B0.");
        System.out.println("symbol table B0 test");
        Assert(((String)env.Get("w"))                   == null, "determining that the symbol 'w' is not in symbol table");

        env.Put("w", "int 1");
        Assert(((String)env.Get("w")).compareTo("int 1"  ) == 0, "finding the    int 1 symbol 'w' in symbol table");

        System.out.println();
        System.out.println("enter nested block B1.");
        System.out.println("symbol table B1 test");
        env = new Env(env);
        env.Put("x", "int 2");
        Assert(((String)env.Get("w")).compareTo("int 1"  ) == 0, "finding the   int 1 symbol 'w' in symbol table");
        Assert(((String)env.Get("x")).compareTo("int 2"  ) == 0, "finding the   int 2 symbol 'x' in symbol table");
        Assert(((String)env.Get("z"))                   == null, "determining that the symbol 'z' is not in symbol table");

        System.out.println();
        System.out.println("enter nested block B2.");
        System.out.println("symbol table B2 test");
        env = new Env(env);
        env.Put("w", "float 3");
        env.Put("y", "bool 4");
        env.Put("z", "int 5" );
        Assert(((String)env.Get("w")).compareTo("float 3") == 0, "finding the float 3 symbol 'w' in symbol table");
        Assert(((String)env.Get("x")).compareTo("int 2"  ) == 0, "finding the   int 2 symbol 'x' in symbol table");
        Assert(((String)env.Get("y")).compareTo("bool 4" ) == 0, "finding the  bool 4 symbol 'y' in symbol table");
        Assert(((String)env.Get("z")).compareTo("int 5"  ) == 0, "finding the   int 5 symbol 'z' in symbol table");

        System.out.println("leave nested block B2.");
        System.out.println("current block is B1.");
        env = env.prev;

        System.out.println("leave nested block B1.");
        System.out.println("current block is B0.");
        env = env.prev;
        Assert(((String)env.Get("z"))                   == null, "determining that the symbol 'z' is not in symbol table");

        System.out.println();
        System.out.println("You will get "+total+" points.");
    }
}
