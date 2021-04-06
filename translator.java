package nandProject8;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class translator {

    int num = 0;
    String name = "";

    int counter;
    PrintWriter printWriter;
    
    Pattern pattern = Pattern.compile("^[^0-9][0-9A-Za-z\\_\\:\\.\\$]+");
    
    public translator(File fileOut) 
    {
        try 
        {
            printWriter = new PrintWriter(fileOut);
            counter = 0;
        } 
        catch (FileNotFoundException exception) 
        {
            exception.printStackTrace();
        }
    }

    public void setArithms(String command){

        if (command.equals("add")){

            printWriter.print(arithmsType1() + "M=M+D\n");

        }else if (command.equals("sub")){

            printWriter.print(arithmsType1() + "M=M-D\n");

        }else if (command.equals("and")){

            printWriter.print(arithmsType1() + "M=M&D\n");

        }else if (command.equals("or")){

            printWriter.print(arithmsType1() + "M=M|D\n");

        }else if (command.equals("gt")){

            printWriter.print(arithmsType2("JLE"));//not <=
            counter++;

        }else if (command.equals("lt")){

            printWriter.print(arithmsType2("JGE"));//not >=
            counter++;

        }else if (command.equals("eq")){

            printWriter.print(arithmsType2("JNE"));//not <>
            counter++;

        }else if (command.equals("not")){

            printWriter.print("@SP\nA=M-1\nM=!M\n");

        }else if (command.equals("neg")){

            printWriter.print("D=0\n@SP\nA=M-1\nM=D-M\n");

        }else {

            throw new IllegalArgumentException("non-arithmetic command");

        }

    }

    public void setRest(int command, String segment, int index){

        if (command == compiler.push1){

            if (segment.equals("constant")){

                printWriter.print("@" + index + "\n" + "D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");

            }else if (segment.equals("local")){

                printWriter.print(arithmsRest1("LCL",index,false));

            }else if (segment.equals("argument")){

                printWriter.print(arithmsRest1("ARG",index,false));

            }else if (segment.equals("this")){

                printWriter.print(arithmsRest1("THIS",index,false));

            }else if (segment.equals("that")){

                printWriter.print(arithmsRest1("THAT",index,false));

            }else if (segment.equals("temp")){

                printWriter.print(arithmsRest1("R5", index + 5,false));

            }else if (segment.equals("pointer") && index == 0){

                printWriter.print(arithmsRest1("THIS",index,true));

            }else if (segment.equals("pointer") && index == 1){

                printWriter.print(arithmsRest1("THAT",index,true));

            }else if (segment.equals("static")){
       
                printWriter.print("@" + name + index + "\n" + "D=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");

            }

        }else if(command == compiler.pop1){

            if (segment.equals("local")){

                printWriter.print(arithmsRest2("LCL",index,false));

            }else if (segment.equals("argument")){

                printWriter.print(arithmsRest2("ARG",index,false));

            }else if (segment.equals("this")){

                printWriter.print(arithmsRest2("THIS",index,false));

            }else if (segment.equals("that")){

                printWriter.print(arithmsRest2("THAT",index,false));

            }else if (segment.equals("temp")){

                printWriter.print(arithmsRest2("R5", index + 5,false));

            }else if (segment.equals("pointer") && index == 0){

                printWriter.print(arithmsRest2("THIS",index,true));

            }else if (segment.equals("pointer") && index == 1){

                printWriter.print(arithmsRest2("THAT",index,true));

            }else if (segment.equals("static")){
              
                printWriter.print("@" + name + index + "\nD=A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");

            }

        }else {

            throw new IllegalArgumentException("different type of command");

        }

    }

    public void setLabel(String label){

        Matcher m = pattern.matcher(label);

        if (m.find()){

            printWriter.print("(" + label +")\n");

        }else {

            throw new IllegalArgumentException("label error");

        }

    }

    public void setGoto(String label){

        Matcher m = pattern.matcher(label);

        if (m.find()){

            printWriter.print("@" + label +"\n0;JMP\n");

        }else {

            throw new IllegalArgumentException("label error");

        }

    }


    public void setIf(String label){

        Matcher m = pattern.matcher(label);

        if (m.find()){

            printWriter.print(arithmsType1() + "@" + label +"\nD;JNE\n");

        }else {

            throw new IllegalArgumentException("label error");

        }

    }


    public void setInit(){

        printWriter.print("@256\n" +
                         "D=A\n" +
                         "@SP\n" +
                         "M=D\n");
        writeCall("Sys.init",0);

    }


    public void writeCall(String functionName, int numArgs){

        String str = "" + (num++);

        printWriter.print("@" + str + "\n" + "D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        printWriter.print(arithmsRest1("LCL",0,true));
        printWriter.print(arithmsRest1("ARG",0,true));
        printWriter.print(arithmsRest1("THIS",0,true));
        printWriter.print(arithmsRest1("THAT",0,true));

        printWriter.print("@SP\n" +
                        "D=M\n" +
                        "@5\n" +
                        "D=D-A\n" +
                        "@" + numArgs + "\n" +
                        "D=D-A\n" +
                        "@ARG\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "D=M\n" +
                        "@LCL\n" +
                        "M=D\n" +
                        "@" + functionName + "\n" +
                        "0;JMP\n" +
                        "(" + str + ")\n"
                        );

    }

    public void setReturn(){

        printWriter.print(returnType());

    }

    public void setFunction(String functionName, int numLocals){

        printWriter.print("(" + functionName +")\n");

        for (int i = 0; i < numLocals; i++){

            setRest(compiler.push1,"constant",0);

        }

    }

    public String frameType(String place){

        return "@R11\n" +
                "D=M-1\n" +
                "AM=D\n" +
                "D=M\n" +
                "@" + place + "\n" +
                "M=D\n";

    }


    public String returnType(){

        return "@LCL\n" +
                "D=M\n" +
                "@R11\n" +
                "M=D\n" +
                "@5\n" +
                "A=D-A\n" +
                "D=M\n" +
                "@R12\n" +
                "M=D\n" +
                arithmsRest2("ARG",0,false) +
                "@ARG\n" +
                "D=M\n" +
                "@SP\n" +
                "M=D+1\n" +
                frameType("THAT") +
                frameType("THIS") +
                frameType("ARG") +
                frameType("LCL") +
                "@R12\n" +
                "A=M\n" +
                "0;JMP\n";
    }

    public void close(){

        printWriter.close();

    }

    private String arithmsType1(){

        return "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n";

    }


    private String arithmsType2(String type){

        return "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n" +
                "D=M-D\n" +
                "@FALSE" + counter + "\n" +
                "D;" + type + "\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "@CONTINUE" + counter + "\n" +
                "0;JMP\n" +
                "(FALSE" + counter + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "(CONTINUE" + counter + ")\n";

    }


    private String arithmsRest1(String segment, int index, boolean isDirect){

        String noPointerCode = (isDirect)? "" : "@" + index + "\n" + "A=D+A\nD=M\n";

        return "@" + segment + "\n" +
                "D=M\n"+
                noPointerCode +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1\n";

    }

    private String arithmsRest2(String segment, int index, boolean isDirect){

        String noPointerCode = (isDirect)? "D=A\n" : "D=M\n@" + index + "\nD=D+A\n";

        return "@" + segment + "\n" +
                noPointerCode +
                "@R13\n" +
                "M=D\n" +
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@R13\n" +
                "A=M\n" +
                "M=D\n";

    }

}
