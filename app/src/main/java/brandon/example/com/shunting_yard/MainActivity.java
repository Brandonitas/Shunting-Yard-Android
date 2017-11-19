package brandon.example.com.shunting_yard;

import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etinput;
    Button btnStart, btnEvaluate, btnStep, btnComplete;
    TextView tvoutput, tvevaluation;

    Queue<String> input = new LinkedList<>();
    Queue<String> output= new LinkedList<>();
    Stack<Character> operatorStack = new Stack<>();

    private String initial;

    TableLayout stk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etinput = (EditText) findViewById(R.id.input);
        btnStart = (Button) findViewById(R.id.buttonStart);
        btnEvaluate = (Button) findViewById(R.id.buttonEvaluate);
        btnStep = (Button) findViewById(R.id.buttonStep);
        btnComplete = (Button) findViewById(R.id.buttonComplete);
        tvoutput = (TextView) findViewById(R.id.textViewOutput);
        tvevaluation = (TextView) findViewById(R.id.textViewEvaluation);

        btnEvaluate.setEnabled(false);
        btnComplete.setEnabled(false);
        btnStep.setEnabled(false);

        stk = (TableLayout) findViewById(R.id.table);
        tabla();


        btnStart.setOnClickListener(this);
        btnEvaluate.setOnClickListener(this);
        btnStep.setOnClickListener(this);
        btnComplete.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.buttonStart:
                restartB();

                break;
            case R.id.buttonEvaluate:
                evaluatebutton();

                break;
            case R.id.buttonStep:
                    stepbutton();
                break;
            case R.id.buttonComplete:
                    completebutton();
                break;
        }

    }



    private void stepbutton() {
        if(etinput.getText().toString().equals("")){
            try {
                while (!operatorStack.isEmpty()) {//while there are tokens at the top of the stack
                    char runTokenStack = operatorStack.pop();
                    eliminarLinea();
                    if (runTokenStack != '(' && runTokenStack != ')')//if they are different than parenthesis
                    {
                        output.add(String.valueOf(runTokenStack));//pop the operator and enqueue it
                        tvoutput.setText("");
                        for (String car : output) {
                            tvoutput.setText(tvoutput.getText().toString()+ " " + car);
                        }
                    } else {
                        throw new ParenthesisNotPairedException("Missing parenthesis");
                    }
                }
                Toast.makeText(this, "Terminado", Toast.LENGTH_SHORT).show();
                btnEvaluate.setEnabled(true);
            }catch (ParenthesisNotPairedException ex){
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
            btnComplete.setEnabled(false);
            btnStep.setEnabled(false);
            etinput.setText(initial);
            etinput.setFocusableInTouchMode(true);

        }else {

                if (etinput.getText().toString().length() == input.peek().length()) {


                    etinput.setText(etinput.getText().toString().substring(input.peek().length()));
                }


            else {

                    etinput.setText(etinput.getText().toString().substring(input.peek().length() + 1));

            }
            try {
                Algorithm(input.remove(), output, operatorStack);
                System.out.print("");
            }catch (ParenthesisNotPairedException ex){
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void completebutton() {
        try {
            for (String it : input) {
                Algorithm(it, output, operatorStack);
            }
            while (!operatorStack.isEmpty()) {//while there are tokens at the top of the stack
                char runTokenStack = operatorStack.pop();
                eliminarLinea();
                if (runTokenStack != '(' && runTokenStack != ')')//if they are different than parenthesis
                {
                    output.add(String.valueOf(runTokenStack));//pop the operator and enqueue it
                    tvoutput.setText("");
                    for (String car : output) {
                        tvoutput.setText(tvoutput.getText().toString()+ " " + car);
                    }
                } else {
                    throw new ParenthesisNotPairedException("Missing parenthesis");
                }
            }
            Toast.makeText(this, "Terminado", Toast.LENGTH_SHORT).show();
            btnEvaluate.setEnabled(true);
        }catch (ParenthesisNotPairedException ex) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            clear(input, output,operatorStack);
        }
        btnComplete.setEnabled(false);
        btnStep.setEnabled(false);
        etinput.setFocusableInTouchMode(true);
        etinput.setText(initial);
    }

    private void evaluatebutton(){
        Queue <String> toeval=new LinkedList<>();
        Stack <Integer> numStack=new Stack<>();

        for (String it:tvoutput.getText().toString().split(" ")) {
            if (!it.equals(""))
                toeval.add(it);
        }
        while (!toeval.isEmpty()){
            String token=toeval.remove();
            if(token.charAt(0) == '+'){
                numStack.push(numStack.pop()+numStack.pop());
            }else if(token.charAt(0) == '-'){
                numStack.push(numStack.pop()-numStack.pop());
            }else if(token.charAt(0) == '*') {
                numStack.push(numStack.pop()*numStack.pop());
            }else if(token.charAt(0) == '/'){
                int a=numStack.pop();
                numStack.push(numStack.pop()/a);
            }else if(token.charAt(0) == '^'){
                int a=numStack.pop();
                numStack.push((int)Math.pow(numStack.pop(),a));
            }else {
                numStack.push(Integer.parseInt(String.valueOf(token)));
            }
        }
        tvevaluation.setText(String.valueOf(numStack.pop()));
    }


    private void clear(Queue<String> input, Queue<String> output, Stack<Character> operatorStack ){
        while(!input.isEmpty()){
            input.remove();
        }
        while(!output.isEmpty()){
            output.remove();
        }
        tvoutput.setText("");
        while(!operatorStack.isEmpty()){
            operatorStack.pop();
        }
        int rows = stk.getChildCount()-1;
        for (int i = 0; i < rows; i++) {
            eliminarLinea();
        }
    }


    private void Algorithm(String token, Queue<String> output, Stack<Character> operatorStack) throws ParenthesisNotPairedException {
        if(isNumeric(token)){//If token is a number
            output.add(token);//enqueue it
            tvoutput.setText("");
            for (String car:output) {
                tvoutput.setText(tvoutput.getText().toString()+" "+car);
            }
        }
        else if(token.equals("^") || token.equals("*") || token.equals("/") || token.equals("+") || token.equals("-")) {//If token is an operator o1
            if (!operatorStack.isEmpty() && operatorStack.peek() != '(') {//This excludes the open parenthesis
                while ((!operatorStack.isEmpty()) && ((!token.equals("^") && Operator.precedence(token.charAt(0), operatorStack.peek()) <= 0) || (!token.equals("^") && Operator.precedence(token.charAt(0), operatorStack.peek()) < 0))) {//while there is a operator in the top of the stack and o1 is left associative and its precedence is less o equal than o2 or o1 is right associative and its precedence is less than o2
                    output.add(String.valueOf(operatorStack.pop()));//pop o2 from the stack and enqueue it
                    eliminarLinea();
                    tvoutput.setText("");
                    for (String car:output) {
                        tvoutput.setText(tvoutput.getText().toString()+" "+car);
                    }
                }
            }
            operatorStack.push(token.toCharArray()[0]);
            crearLinea(token.charAt(0));

        }else if (token.equals("(")){//if token is an open parenthesis
            operatorStack.push(token.toCharArray()[0]);// push it in the top of the stack
            crearLinea(token.charAt(0));
        }else if (token.equals(")")){//if token is an close parenthesis
            char runTokenStack;
            //try catch if there is a parenthesis without its partner
            try {
                while ((runTokenStack = operatorStack.pop()) != '(') {//Until the top of the stack is an open parenthesis
                    eliminarLinea();
                    output.add(String.valueOf(runTokenStack));//pop operands from the stack and enqueue them
                    tvoutput.setText("");
                    for (String car : output) {
                        tvoutput.setText(tvoutput.getText().toString()+" " + car);
                    }
                }
            }catch (EmptyStackException ex){
                throw new ParenthesisNotPairedException("Missing parenthesis");
            }
            eliminarLinea();
        }
        //If there are no more tokens to read
    }


    public String spaces(String toEval){
        Queue <String> in=new LinkedList<>();
        for (char it:toEval.toCharArray()) {
            in.add(String.valueOf(it));
        }
        Stack <String> out=new Stack<>();
        while (!in.isEmpty()){
            String token=in.remove();
            if(token.equals("^") || token.equals("*") || token.equals("/") || token.equals("+") || token.equals("-") || token.equals("(") || token.equals(")")){
                out.push(token);
            }else{
                if(!out.isEmpty()){
                    if (out.peek().equals("^") || out.peek().equals("*") || out.peek().equals("/") || out.peek().equals("+") || out.peek().equals("-")|| token.equals("(") || token.equals(")")){
                        out.push(token);
                    }else{
                        if(out.peek().equals(("("))){
                            out.push(token);
                        }else {
                            out.push(out.pop() + token);
                        }
                    }
                }else {
                    out.push(token);
                }
            }
        }
        String res="";
        while (!out.isEmpty()){
            if(out.size()!=1){
                res=" "+out.pop()+res;
            }else {
                res=out.pop()+res;
            }
        }
        return res;
    }

    public boolean isNumeric(String str){
        try{
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe){
            return false;
        }
        return true;
    }



    public void tabla(){

        TableRow tbrow0 = new TableRow(this);

        TextView tv0 = new TextView(this);
        tv0.setText(" Procedence ");
        tv0.setTextSize(20);
        tv0.setTextColor(Color.BLACK);
        tv0.setGravity(Gravity.CENTER);
        tv0.setTypeface(null, Typeface.BOLD);
        tbrow0.addView(tv0);

        TextView tv1 = new TextView(this);
        tv1.setText(" Stack ");
        tv1.setTextSize(20);
        tv1.setTextColor(Color.BLACK);
        tv1.setTypeface(null, Typeface.BOLD);
        tv1.setGravity(Gravity.CENTER);
        tbrow0.addView(tv1);

        stk.addView(tbrow0);



    }

    public void crearLinea(char operator){

        TableRow tbrow = new TableRow(this);
        TextView t1v = new TextView(this);
        t1v.setText(Operator.myprecedence(operator)+"");
        t1v.setTextColor(Color.BLACK);
        t1v.setTextSize(18);
        t1v.setGravity(Gravity.CENTER);
        tbrow.addView(t1v);

        TextView t2v = new TextView(this);
        t2v.setText(operator+"");
        t2v.setTextSize(18);
        t2v.setTextColor(Color.BLACK);
        t2v.setGravity(Gravity.CENTER);
        tbrow.addView(t2v);

        stk.addView(tbrow,1);

    }

    public void eliminarLinea(){
        stk.removeViewAt(1);
    }

    public void restartB(){
        clear(input,output,operatorStack);
        etinput.setText(spaces(etinput.getText().toString()));
        for (String it:etinput.getText().toString().split(" ")) {
            input.add(it);
        }
        etinput.setFocusable(false);
        initial=etinput.getText().toString();
        btnComplete.setEnabled(true);
        btnStep.setEnabled(true);
        btnEvaluate.setEnabled(false);
    }



}
