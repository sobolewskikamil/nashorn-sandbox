package sobolee.nashornSandbox;

import java.io.*;

public class Executor {

    public static void main(String[] args){
        new Executor().run();
    }

    public Object run(){
        String javaHome = System.getProperty("java.home");
        String javaBin = javaHome +
                File.separator + "bin" +
                File.separator + "java";
        String classpath = System.getProperty("java.class.path");
        String className = (NashornProcessor.class).getCanonicalName();

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classpath, className);

        Process process = null;
        String response = null;
        try {
            process = builder.start();
            sendJavascript(process, "print('Hello World!');");
            response = getResponse(process);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(response);
        return process.exitValue();
    }

    private void sendJavascript(Process process, String js){
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
        try {
            output.write(js, 0, js.length());
            output.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private String getResponse(Process process){
        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        //BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String response = null;
        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = input.readLine()) != null){
                sb.append(line);
            }
            response = sb.toString();
            input.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return response;
    }
}
