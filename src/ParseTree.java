import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class ParseTree
{
    public static class RunEnv
    {
        // RunEnv maintains the program running environment by maintaining
        // * the function call stack (each func-call stack item contains the running environment including variable vlaues)
        // * the map for func_name -> func_body
        public Stack<HashMap<Integer, Object>> stack_funcrunenv ;   // function call stack
        public HashMap<String, FuncDefn>       funcname_funcdefn;   // name -> func body/node

        public RunEnv(ArrayList<ParseTree.FuncDefn> funcs)
        {
            // create calling stack environment
            stack_funcrunenv = new Stack<HashMap<Integer, Object>>();
            // create name->function map
            funcname_funcdefn = new HashMap<String, FuncDefn>();
            for(FuncDefn func : funcs)
            {
                funcname_funcdefn.put(func.ident, func);
            }
        }
        public Object GetValue(int reladdr)
        {
            // get value in the relative-address memory block
            //           of function-running environment
            //           at the top of calling function stack
            // 1. get top function-running environment
            HashMap<Integer, Object> idx_val = stack_funcrunenv.peek();
            // 2. get value at reladdr (relative address)
            if(idx_val.containsKey(reladdr) == false)
                idx_val.put(reladdr, null);
            Object val = idx_val.get(reladdr);
            return val;
        }
        public void SetValue(int reladdr, Object val)
        {
            // update value in the relative-address memory block
            //              of function-running environment
            //              at the top of calling function stack
            // 1. get top function-running environment
            HashMap<Integer, Object> idx_val = stack_funcrunenv.peek();
            // 2. update value at reladdr (relative address)
            idx_val.put(reladdr, val);
        }
        public void PushFuncCallEnv()
        {
            // push a new function-running environment
            // to prepare function call
            stack_funcrunenv.push(new HashMap<Integer, Object>());
        }
        public void PopFuncCallEnv()
        {
            // pop the used function-running environment
            // to cleaning the called-function environment
            stack_funcrunenv.pop();
        }
    }

    public static abstract class Node
    {
        abstract public Object   Exec(RunEnv runenv)   throws Exception; // This is used to evaluate the parse tree node
        abstract public String[] ToStringList(int opt) throws Exception; // This is used to print conde with indentation and comments
    }

    public static class TypeSpec extends Node
    {
        public ParseTreeInfo.TypeSpecInfo info = new ParseTreeInfo.TypeSpecInfo();
        public String typename;
        public TypeSpec(String typename)                       { this.typename = typename; }
        public Object Exec(RunEnv runenv)     throws Exception { return null; }
        public String ToString(int opt)       throws Exception { return typename; }
        public String[] ToStringList(int opt) throws Exception { return new String[] { ToString(opt) }; }
    }

    public static class Program extends Node
    {
        public ParseTreeInfo.ProgramInfo info = new ParseTreeInfo.ProgramInfo();
        public ArrayList<FuncDefn> funcs;
        public Program(ArrayList<FuncDefn> funcs)
        {
            this.funcs = funcs;
        }
        public Object Exec() throws Exception
        {
            ParseTree.RunEnv runenv = new ParseTree.RunEnv(funcs);
            Object ret = Exec(runenv);
            return ret;
        }
        public Object Exec(RunEnv runenv) throws Exception
        {
            FuncDefn main = runenv.funcname_funcdefn.get("main");
            ArrayList<Arg> args = new ArrayList<Arg>();
            Object ret = main.Exec(runenv, args);
            return ret;
        }
        public String[] ToStringList(int opt) throws Exception
        {
            ArrayList<String> strs = new ArrayList<String>();
            for(var func : funcs)
            {
                for(String str : func.ToStringList(opt))
                    strs.add(str);
            }
            return strs.toArray(String[]::new);
        }
    }

    public static class FuncDefn extends Node
    {
        public ParseTreeInfo.FuncDefnInfo info = new ParseTreeInfo.FuncDefnInfo();
        public String               ident     ;
        public TypeSpec             rettype   ;
        public ArrayList<Param    > params    ;
        public ArrayList<LocalDecl> localdecls;
        public ArrayList<Stmt     > stmtlist  ;
        public FuncDefn(String ident, TypeSpec rettype, ArrayList<Param> params, ArrayList<LocalDecl> localdecls, ArrayList<Stmt> stmtlist)
        {
            this.ident      = ident     ;
            this.rettype    = rettype   ;
            this.params     = params    ;
            this.localdecls = localdecls;
            this.stmtlist   = stmtlist  ;
        }
        public Object Exec(RunEnv runenv) throws Exception
        {
            Object ret = Exec(runenv, null);
            return ret;
        }
        public Object Exec(RunEnv runenv, ArrayList<Arg> args) throws Exception
        {
            assert(args.size() == params.size());
            // calculate argument values
            Object[] vals = new Object[args.size()];
            for(int i=0; i<vals.length; i++)
                vals[i] = args.get(i).Exec(runenv);

            // enter function environment
            runenv.PushFuncCallEnv();

            // pass argument values into the local variable stack of the function
            for(int i=0; i<vals.length; i++)
            {
                int reladdr = params.get(i).reladdr;
                runenv.SetValue(reladdr, vals[i]);
            }
            
            // call function's instructions
            for(Stmt stmt : stmtlist)
            {
                Object control = stmt.Exec(runenv);
                if(control != null && control.equals("return"))
                    break;
            }

            // get return value
            Object ret = runenv.GetValue(0);

            // leave function environment
            runenv.PopFuncCallEnv();

            return ret;
        }
        public String[] ToStringList(int opt) throws Exception
        {
            String head = "func " + ident + " :: ( ";
            for(int i=0; i<params.size(); i++)
                if(i == 0) head +=      params.get(i).ToString(opt);
                else       head += ", "+params.get(i).ToString(opt);
            head += " ) -> " + rettype.ToString(opt);

            ArrayList<String> strs = new ArrayList<String>();
            strs.add(head);
            if(opt == 1)
            {   // print comments for running environment
                for(var param : params)
                    strs.add("// relative address of parameter "+param.ident+" from func call base is "+param.reladdr+"");
            }
            strs.add("begin");
            for(var localdecl : localdecls)
                strs.add("    " + localdecl.ToString(opt));
            for(var stmt : stmtlist)
                for(String str : stmt.ToStringList(opt))
                    strs.add("    "+str);
            strs.add("end");

            return strs.toArray(String[]::new);
        }
    }

    public static class Param extends Node
    {
        public ParseTreeInfo.ParamInfo info = new ParseTreeInfo.ParamInfo();
        public String   ident   ;
        public TypeSpec typespec;
        public Integer  reladdr = null; // assign this value later for running the parse tree
        public Param(String ident, TypeSpec typespec)
        {
            this.ident    = ident   ;
            this.typespec = typespec;
        }
        public Object   Exec(RunEnv runenv)   throws Exception { return null; }
        public String   ToString(int opt)     throws Exception { return ident + "::" + typespec.ToString(opt); }
        public String[] ToStringList(int opt) throws Exception { return new String[] { ToString(opt) }; }
    }

    public static class LocalDecl extends Node
    {
        public ParseTreeInfo.LocalDeclInfo info = new ParseTreeInfo.LocalDeclInfo();
        public String   ident   ;
        public TypeSpec typespec;
        public Integer  reladdr = null; // assign this value later for running the parse tree
        public LocalDecl(String ident, TypeSpec typespec)
        {
            this.ident    = ident   ;
            this.typespec = typespec;
        }
        public Object Exec(RunEnv runenv)     throws Exception { return null; }
        public String[] ToStringList(int opt) throws Exception { return new String[] { ToString(opt) }; }
        public String ToString(int opt)       throws Exception
        {
            String str ="var " + ident + " :: " + typespec.ToString(opt) + ";";
            if(opt == 1)
            {   // print comments for running environment
                str += " // relative address of local variable "+ident+" from func call base is "+reladdr+"";
            }
            return str;
        }
    }

    public abstract static class Stmt extends Node
    {
        public ParseTreeInfo.StmtStmtInfo info = new ParseTreeInfo.StmtStmtInfo();
        abstract public Object Exec(RunEnv runenv)     throws Exception;
        abstract public String[] ToStringList(int opt) throws Exception;
    }
    public static class AssignStmt extends Stmt
    {
        public String  ident;
        public Integer ident_reladdr = null; // assign this value later for running the parse tree
        public Expr    expr ;
        public AssignStmt(String ident, Expr expr)
        {
            this.ident = ident;
            this.expr  = expr ;
        }
        public Object Exec(RunEnv runenv) throws Exception
        {
            Object exprval = expr.Exec(runenv);
            runenv.SetValue(ident_reladdr, exprval);
            return null;
        }
        public String[] ToStringList(int opt) throws Exception 
        {
            String str = ident;
            if(opt == 1)
            {   // print comments for running environment
                if(ident_reladdr == null)
                    throw new Exception("AssignStmt.ident_reladdr is not assigned yet.");
                str += "["+ident_reladdr+"]";
            }
            str  += " <- " + expr.ToString(opt) + ";";
            return new String[] { str };
        }
    }
    public static class PrintStmt extends Stmt
    {
        public Expr expr;
        public PrintStmt(Expr expr)
        {
            this.expr = expr;
        }
        public Object Exec(RunEnv runenv) throws Exception
        {
            Object exprval = expr.Exec(runenv);
            System.out.println(exprval);
            return null;
        }
        public String[] ToStringList(int opt) throws Exception
        {
            return new String[]
            {
                "print " + expr.ToString(opt) + ";"
            };
        }
    }
    public static class ReturnStmt extends Stmt
    {
        public Expr expr;
        public ReturnStmt(Expr expr)
        {
            this.expr = expr;
        }
        public Object Exec(RunEnv runenv) throws Exception
        {
            Object val = expr.Exec(runenv);
            runenv.SetValue(0, val);
            return "return";
        }
        public String[] ToStringList(int opt) throws Exception
        {
            return new String[]
            {
                "return " + expr.ToString(opt) + ";"
            };
        }
    }
    public static class IfStmt extends Stmt
    {
        public Expr            cond     ;
        public ArrayList<Stmt> thenstmts;
        public ArrayList<Stmt> elsestmts;
        public IfStmt(Expr cond, ArrayList<Stmt> thenstmts, ArrayList<Stmt> elsestmts)
        {
            this.cond      = cond;
            this.thenstmts = thenstmts;
            this.elsestmts = elsestmts;
        }
        public Object Exec(RunEnv runenv) throws Exception
        {
            boolean condval = (boolean)cond.Exec(runenv);
            if(condval)
            {
                for(Stmt stmt : thenstmts)
                {
                    Object control = stmt.Exec(runenv);
                    if(control != null && control.equals("return"))
                        return "return";
                }
            }
            else
            {
                for(Stmt stmt : elsestmts)
                {
                    Object control = stmt.Exec(runenv);
                    if(control != null && control.equals("return"))
                        return "return";
                }
            }

            return null;
        }
        public String[] ToStringList(int opt) throws Exception
        {
            ArrayList<String> strs = new ArrayList<String>();
            strs.add("if " + cond.ToString(opt) + " then");
            for(Stmt stmt : thenstmts)
                for(String str : stmt.ToStringList(opt))
                    strs.add("    "+str);
            strs.add("else");
            for(Stmt stmt : elsestmts)
                for(String str : stmt.ToStringList(opt))
                    strs.add("    "+str);
            strs.add("end");
            return strs.toArray(String[]::new);
        }
    }
    public static class WhileStmt extends Stmt
    {
        public Expr         cond        ;
        public CompoundStmt compoundstmt;
        public WhileStmt(Expr cond, CompoundStmt compoundstmt)
        {
            this.cond         = cond;
            this.compoundstmt = compoundstmt;
        }
        public Object Exec(RunEnv runenv) throws Exception
        {
            while(true)
            {
                boolean condval = (boolean)cond.Exec(runenv);
                if(condval == false)
                    break;

                Object control = compoundstmt.Exec(runenv);
                if(control != null && control.equals("return"))
                    return "return";
            }
            return null;
        }
        public String[] ToStringList(int opt) throws Exception
        {
            ArrayList<String> strs = new ArrayList<String>();
            strs.add("while " + cond.ToString(opt));
            for(String str : compoundstmt.ToStringList(opt))
                strs.add(str);
            return strs.toArray(String[]::new);
        }
    }
    public static class CompoundStmt extends Stmt
    {
        public ArrayList<LocalDecl> localdecls;
        public ArrayList<Stmt     > stmtlist  ;
        public CompoundStmt(ArrayList<LocalDecl> localdecls, ArrayList<Stmt> stmtlist)
        {
            this.localdecls = localdecls;
            this.stmtlist   = stmtlist  ;
        }
        public Object Exec(RunEnv runenv) throws Exception
        {
            for(Stmt stmt : stmtlist)
            {
                Object control = stmt.Exec(runenv);
                if(control != null && control.equals("return"))
                    return "return";
            }
            return null;
        }
        public String[] ToStringList(int opt) throws Exception
        {
            ArrayList<String> strs = new ArrayList<String>();
            strs.add("begin");
            for(LocalDecl localdecl : localdecls)
                strs.add("    " + localdecl.ToString(opt));
            for(Stmt stmt : stmtlist)
                for(String str : stmt.ToStringList(opt))
                    strs.add("    "+str);
            strs.add("end");
            return strs.toArray(String[]::new);
        }
    }

    public static class Arg extends Node
    {
        public ParseTreeInfo.ArgInfo info = new ParseTreeInfo.ArgInfo();
        public Expr expr;
        public Arg(Expr expr)                                  { this.expr = expr;       }
        public Object Exec(RunEnv runenv)     throws Exception { return expr.Exec(runenv); }
        public String ToString(int opt)       throws Exception { return expr.ToString(opt); }
        public String[] ToStringList(int opt) throws Exception { return new String[] { ToString(opt) }; }
    }

    public static abstract class Expr extends Node
    {
        public ParseTreeInfo.ExprInfo info = new ParseTreeInfo.ExprInfo();
        abstract public String ToString(int opt) throws Exception;
        public String[] ToStringList(int opt)    throws Exception { return new String[] { ToString(opt) }; }
    }
    public static class ExprBoolLit extends Expr
    {
        boolean val;
        public ExprBoolLit(boolean val)                    { this.val = val; }
        public Object Exec(RunEnv runenv) throws Exception { return val; }
        public String ToString(int opt)   throws Exception { return ""+val; }
    }
    public static class ExprIntLit extends Expr
    {
        Integer val;
        public ExprIntLit (Integer val)                    { this.val = val; }
        public Object Exec(RunEnv runenv) throws Exception { return val; }
        public String ToString(int opt)   throws Exception { return ""+val; } 
    }
    public static class ExprIdent extends Expr
    {
        public String   ident;
        public Integer  reladdr = null; // assign this value later for running the parse tree
        public ExprIdent  (String ident)  { this.ident = ident; }
        public Object Exec(RunEnv runenv) throws Exception
        {
            return runenv.GetValue(reladdr);
        }
        public String ToString(int opt) throws Exception
        {
            String str = ident;
            if(opt == 1)
            {   // print comments for running environment
                if(reladdr == null)
                    throw new Exception("ExprIdent.reladdr is not assigned yet.");
                str += "["+reladdr+"]";
            }
            return str;
        }
    }
    public static class ExprCall extends Expr
    {
        public String         ident;
        public ArrayList<Arg> args ;

        public ExprCall(String ident, ArrayList<Arg> args)
        {
            this.ident = ident;
            this.args  = args ;
        }
        public Object Exec(RunEnv runenv) throws Exception
        {
            FuncDefn func = runenv.funcname_funcdefn.get(ident);
            Object   ret  = func.Exec(runenv, args);
            return   ret;
        }
        public String ToString(int opt) throws Exception
        {
            String str = ident + "(";
            for(int i=0; i<args.size(); i++)
                if(i == 0) str +=      args.get(i).ToString(opt);
                else       str += ", "+args.get(i).ToString(opt);
            str += ")";
            return str;
        }
    }
    public static class ExprOper extends Expr
    {
        public Expr   op1 ;
        public Expr   op2 ;
        public String oper;
        public ExprOper(String oper, Expr op1, Expr op2)
        {
            this.op1  = op1 ;
            this.op2  = op2 ;
            this.oper = oper;
        }
        public Object Exec(RunEnv runenv) throws Exception
        {
            Object val1 = null; if(op1 != null) val1 = op1.Exec(runenv);
            Object val2 = null; if(op2 != null) val2 = op2.Exec(runenv);
            switch(oper)
            {
                case "+"  : return  ((Integer)val1 +  (Integer)val2 );
                case "-"  : return  ((Integer)val1 -  (Integer)val2 );
                case "*"  : return  ((Integer)val1 *  (Integer)val2 );
                case "/"  : return  ((Integer)val1 /  (Integer)val2 );
                case "%"  : return  ((Integer)val1 %  (Integer)val2 );
                case "and": return  ((Boolean)val1 && (Boolean)val2 );
                case "or" : return  ((Boolean)val1 || (Boolean)val2 );
                case "not": return !((Boolean)val1                  );
                case "="  : return  ((        val1).equals(    val2));
                case "!=" : return !((        val1).equals(    val2));
                case "<=" : return  ((Integer)val1 <= (Integer)val2 );
                case "<"  : return  ((Integer)val1 <  (Integer)val2 );
                case ">=" : return  ((Integer)val1 >= (Integer)val2 );
                case ">"  : return  ((Integer)val1 >  (Integer)val2 );
                case "()" : return  (         val1                  );
            }
            return null;
        }
        public String ToString(int opt) throws Exception
        {
            String str1 = null; if(op1 != null) str1 = op1.ToString(opt);
            String str2 = null; if(op2 != null) str2 = op2.ToString(opt);
            switch(oper)
            {
                case "+"  : return (       str1+" + "  +str2);
                case "-"  : return (       str1+" - "  +str2);
                case "*"  : return (       str1+" * "  +str2);
                case "/"  : return (       str1+" / "  +str2);
                case "%"  : return (       str1+" % "  +str2);
                case "and": return (       str1+" and "+str2);
                case "or" : return (       str1+" or " +str2);
                case "not": return ("not "+str1             );
                case "="  : return (       str1+" = "  +str2);
                case "!=" : return (       str1+" != " +str2);
                case "<=" : return (       str1+" <= " +str2);
                case "<"  : return (       str1+" < "  +str2);
                case ">=" : return (       str1+" >= " +str2);
                case ">"  : return (       str1+" > "  +str2);
                case "()" : return (  "( "+str1+" )"        );
            }
            return null;
        }
    }
    //public static abstract class ExprUnary  extends Expr { public Expr op1;                  public ExprUnary (Expr op1          ) { this.op1 = op1;                 } }
    //public static abstract class ExprBinary extends Expr { public Expr op1; public Expr op2; public ExprBinary(Expr op1, Expr op2) { this.op1 = op1; this.op2 = op2; } }
    //public static class ExprAdd extends ExprBinary { public ExprAdd(Expr op1, Expr op2) { super(op1, op2); } public Object Exec(RunEnv runenv) { return ( (Integer)op1.Exec(runenv) +  (Integer)op2.Exec(runenv) ); } public String ToString(int opt) { return op1.ToString(opt)+" + " +op2.ToString(opt); } }
    //public static class ExprSub extends ExprBinary { public ExprSub(Expr op1, Expr op2) { super(op1, op2); } public Object Exec(RunEnv runenv) { return ( (Integer)op1.Exec(runenv) -  (Integer)op2.Exec(runenv) ); } public String ToString(int opt) { return op1.ToString(opt)+" - " +op2.ToString(opt); } }
    //public static class ExprMul extends ExprBinary { public ExprMul(Expr op1, Expr op2) { super(op1, op2); } public Object Exec(RunEnv runenv) { return ( (Integer)op1.Exec(runenv) *  (Integer)op2.Exec(runenv) ); } public String ToString(int opt) { return op1.ToString(opt)+" * " +op2.ToString(opt); } }
    //public static class ExprDiv extends ExprBinary { public ExprDiv(Expr op1, Expr op2) { super(op1, op2); } public Object Exec(RunEnv runenv) { return ( (Integer)op1.Exec(runenv) /  (Integer)op2.Exec(runenv) ); } public String ToString(int opt) { return op1.ToString(opt)+" / " +op2.ToString(opt); } }
    //public static class ExprMod extends ExprBinary { public ExprMod(Expr op1, Expr op2) { super(op1, op2); } public Object Exec(RunEnv runenv) { return ( (Integer)op1.Exec(runenv) /  (Integer)op2.Exec(runenv) ); } public String ToString(int opt) { return op1.ToString(opt)+" % " +op2.ToString(opt); } }
    //public static class ExprAnd extends ExprBinary { public ExprAnd(Expr op1, Expr op2) { super(op1, op2); } public Object Exec(RunEnv runenv) { return ( (Boolean)op1.Exec(runenv) && (Boolean)op2.Exec(runenv) ); } public String ToString(int opt) { return op1.ToString(opt)+" && "+op2.ToString(opt); } }
    //public static class ExprOr  extends ExprBinary { public ExprOr (Expr op1, Expr op2) { super(op1, op2); } public Object Exec(RunEnv runenv) { return ( (Boolean)op1.Exec(runenv) || (Boolean)op2.Exec(runenv) ); } public String ToString(int opt) { return op1.ToString(opt)+" || "+op2.ToString(opt); } }
    //public static class ExprNot extends ExprUnary  { public ExprNot(Expr op1          ) { super(op1     ); } public Object Exec(RunEnv runenv) { return !((Boolean)op1.Exec(runenv)                              ); } public String ToString(int opt) { return "not "                  +op1.ToString(opt); } }
    //public static class ExprEq  extends ExprBinary { public ExprEq (Expr op1, Expr op2) { super(op1, op2); } public Object Exec(RunEnv runenv) { return ( (        op1.Exec(runenv)).equals(    op2.Exec(runenv))); } public String ToString(int opt) { return op1.ToString(opt)+" = " +op2.ToString(opt); } }
    //public static class ExprNe  extends ExprBinary { public ExprNe (Expr op1, Expr op2) { super(op1, op2); } public Object Exec(RunEnv runenv) { return (!(        op1.Exec(runenv)).equals(    op2.Exec(runenv))); } public String ToString(int opt) { return op1.ToString(opt)+" != "+op2.ToString(opt); } }
    //public static class ExprLe  extends ExprBinary { public ExprLe (Expr op1, Expr op2) { super(op1, op2); } public Object Exec(RunEnv runenv) { return ( (Integer)op1.Exec(runenv) <= (Integer)op2.Exec(runenv) ); } public String ToString(int opt) { return op1.ToString(opt)+" <= "+op2.ToString(opt); } }
    //public static class ExprLt  extends ExprBinary { public ExprLt (Expr op1, Expr op2) { super(op1, op2); } public Object Exec(RunEnv runenv) { return ( (Integer)op1.Exec(runenv) <  (Integer)op2.Exec(runenv) ); } public String ToString(int opt) { return op1.ToString(opt)+" < " +op2.ToString(opt); } }
    //public static class ExprGe  extends ExprBinary { public ExprGe (Expr op1, Expr op2) { super(op1, op2); } public Object Exec(RunEnv runenv) { return ( (Integer)op1.Exec(runenv) >= (Integer)op2.Exec(runenv) ); } public String ToString(int opt) { return op1.ToString(opt)+" >= "+op2.ToString(opt); } }
    //public static class ExprGt  extends ExprBinary { public ExprGt (Expr op1, Expr op2) { super(op1, op2); } public Object Exec(RunEnv runenv) { return ( (Integer)op1.Exec(runenv) >  (Integer)op2.Exec(runenv) ); } public String ToString(int opt) { return op1.ToString(opt)+" > " +op2.ToString(opt); } }
    //public static class ExprParen extends ExprUnary{ public ExprParen(Expr op1        ) { super(op1     ); } public Object Exec(RunEnv runenv) { return (          op1.Exec(runenv)                              ); } public String ToString(int opt) { return "( "+op1.ToString(opt)+" )"; } }
}
