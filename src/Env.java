import java.util.ArrayList;
import java.util.HashMap;

public class Env
{
    public Env prev;
    public Env(Env prev)
    {
    }
    public void Put(String name, Object value)
    {
    }
    public Object Get(String name)
    {
        // this is a fake implementation
        if(name.equals("a") == true) return "int";
        if(name.equals("b") == true) return "bool";
        if(name.equals("func") == true)
        {
            ArrayList<String> func_args = new ArrayList<>();
            HashMap<String, Object> func_attr = new HashMap<String, Object>();
            func_attr.put("return type", "int");
            func_attr.put("arguments", func_args);
            return func_attr;
        }
        return null;
    }
}
