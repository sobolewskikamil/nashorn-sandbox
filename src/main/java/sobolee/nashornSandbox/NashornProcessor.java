package sobolee.nashornSandbox;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NashornProcessor {
    private ScriptEngine engine;
    private String code;

    public static void main(String[] args){
        new NashornProcessor();
    }

    public NashornProcessor(){
        engine = new ScriptEngineManager().getEngineByName("nashorn");
        String js = getJavascript();
        execute(js);
    }
    public void execute(String js){
        try {
            engine.eval(js);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    public String getJavascript(){
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        String js = null;
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = input.readLine()) != null){
                sb.append(line);
            }
            js = sb.toString();
            input.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return js;
    }
}
