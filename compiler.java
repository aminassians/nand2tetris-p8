package nandProject8;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class compiler {
    
    
    public static int arithm = 0;
    public static int push1 = 1;
    public static int pop1 = 2;
    public static int label1 = 3;
    public static int goto1 = 4;
    public static int if1 = 5;
    public static int function1 = 6;
    public static int return1 = 7;
    public static int call1 = 8;
    public static ArrayList<String> arithmscanner = new ArrayList<String>();
    
    private int argvalue;
    private String argument1;
    private int argument2;
    
    String stringOn;
    Scanner scanner;
    
    
static {
        arithmscanner.add("add");
        arithmscanner.add("sub");
        arithmscanner.add("neg");
        arithmscanner.add("eq");
        arithmscanner.add("gt");
        arithmscanner.add("lt");
        arithmscanner.add("and");
        arithmscanner.add("or");
        arithmscanner.add("not");
    
}

    public compiler(File fileIn) {

        argvalue = -1;
        argument1 = "";
        argument2 = -1;

        try {
            scanner = new Scanner(fileIn);

            String before = "";
            String line = "";

            while(scanner.hasNext()){

                line = noComments(scanner.nextLine()).trim();

                if (line.length() > 0) {
                    before += line + "\n";
                }
            }

            scanner = new Scanner(before.trim());

        } catch (FileNotFoundException e) {
            System.out.println("File not found Exception!");
        }

    }


    public boolean Commands(){

       return scanner.hasNextLine();
    }


    public void extra(){

        stringOn = scanner.nextLine();
        argument1 = "";
        argument2 = -1;

        String[] str = stringOn.split(" ");

        if (arithmscanner.contains(str[0]))
        {
            argvalue = arithm;
            argument1 = str[0];
        }
        
        else if (str[0].equals("return")) 
        {
            argvalue = return1;
            argument1 = str[0];
        }
        
        else 
        {
            argument1 = str[1];
            if(str[0].equals("push")){
                argvalue = push1;
            }
            
            else if(str[0].equals("pop")){
                argvalue = pop1;
            }
            else if(str[0].equals("label")){
                argvalue = label1;
            }
            else if(str[0].equals("if")){
                argvalue = if1;
            }
            else if (str[0].equals("goto")){
                argvalue = goto1;

            }
            else if (str[0].equals("function")){
                argvalue = function1;

            }
            else if (str[0].equals("call")){
                argvalue = call1;

            }
            else {
                throw new IllegalArgumentException("No such a command");

            }
        }
    }

    public int commandvalue(){
        if (argvalue != -1) 
        {
            return argvalue;
        }
        else {
            throw new IllegalStateException("No command!");
        }

    }

    public String arg1(){

        if (commandvalue() != return1){

            return argument1;

        }
        else {

            throw new IllegalStateException("Error on getting arg1");

        }

    }


    public int arg2(){

        if (commandvalue() == push1 || commandvalue() == pop1 || commandvalue() == function1 || commandvalue() == call1){

            return argument2;

        }else {

            throw new IllegalStateException("Error on getting arg2");

        }

    }


    public static String noComments(String strIn){

        int position = strIn.indexOf("//");

        if (position != -1){

            strIn = strIn.substring(0, position);

        }

        return strIn;
    }


    public static String noSpaces(String strIn){
        String result = "";

        if (strIn.length() != 0){

            String[] str = strIn.split(" ");

            for (String s: str){
                result += s;
            }
        }

        return result;
    }


    public static String getExt(String fileName){

        int index = fileName.lastIndexOf('.');

        if (index != -1){

            return fileName.substring(index);

        }else {

            return "";

        }
    }
}
