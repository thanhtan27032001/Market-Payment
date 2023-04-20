package com.example.marketpayment.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.marketpayment.view.activity.R;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class CalculatorFragment extends Fragment {
    TextView workingsTV;
    TextView resultsTV;

    String workings = "";
    String formula = "";
    String tempFormula = "";

    Button btnDoc, btn0, btnEqual;
    Button btn1, btn2, btn3, btnAdd;
    Button btn4, btn5, btn6, btnSubtract;
    Button btn7, btn8, btn9, btnMultiply;
    Button btnClear, btnBracket, btnDivide;
    ImageButton btnBackSpace;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_calculator, container, false);
        workingsTV = view.findViewById(R.id.workingsTextView);
        resultsTV = view.findViewById(R.id.resultTextView);

        btnDoc = view.findViewById(R.id.btnDoc);
        btn0 = view.findViewById(R.id.btn0);
        btnEqual = view.findViewById(R.id.btnEqual);
        btn1 = view.findViewById(R.id.btn1);
        btn2 = view.findViewById(R.id.btn2);
        btn3 = view.findViewById(R.id.btn3);
        btnAdd = view.findViewById(R.id.btnAdd);
        btn4 = view.findViewById(R.id.btn4);
        btn5 = view.findViewById(R.id.btn5);
        btn6 = view.findViewById(R.id.btn6);
        btnSubtract = view.findViewById(R.id.btnSubtract);
        btn7 = view.findViewById(R.id.btn7);
        btn8 = view.findViewById(R.id.btn8);
        btn9 = view.findViewById(R.id.btn9);
        btnMultiply = view.findViewById(R.id.btnMultiply);
        btnClear = view.findViewById(R.id.btnClear);
        btnBracket = view.findViewById(R.id.btnBracket);
        btnBackSpace = view.findViewById(R.id.btnBackSpace);
        btnDivide = view.findViewById(R.id.btnDivide);

        btnDoc.setOnClickListener(view1 -> setWorkings("."));
        btn0.setOnClickListener(view1 -> setWorkings("0"));
        btnEqual.setOnClickListener(view1 -> equalsOnClick());
        btn1.setOnClickListener(view1 -> setWorkings("1"));
        btn2.setOnClickListener(view1 -> setWorkings("2"));
        btn3.setOnClickListener(view1 -> setWorkings("3"));
        btnAdd.setOnClickListener(view1 -> setWorkings("+"));
        btn4.setOnClickListener(view1 -> setWorkings("4"));
        btn5.setOnClickListener(view1 -> setWorkings("5"));
        btn6.setOnClickListener(view1 -> setWorkings("6"));
        btnSubtract.setOnClickListener(view1 -> setWorkings("-"));
        btn7.setOnClickListener(view1 -> setWorkings("7"));
        btn8.setOnClickListener(view1 -> setWorkings("8"));
        btn9.setOnClickListener(view1 -> setWorkings("9"));
        btnMultiply.setOnClickListener(view1 -> setWorkings("*"));
        btnClear.setOnClickListener(view1 -> clearOnClick());
        btnBracket.setOnClickListener(view1 -> bracketsOnClick());
        btnBackSpace.setOnClickListener(view1 -> backSpaceOnClick());
        btnDivide.setOnClickListener(view1 -> setWorkings("/"));
        return view;
    }

    private void backSpaceOnClick() {
        workings = workings.length() > 0 ? workings.substring(0, workings.length() - 1) : "";
        workingsTV.setText(workings);
    }

    private void setWorkings(String givenValue)
    {
        workings = workings + givenValue;
        workingsTV.setText(workings);
    }

    public void equalsOnClick()
    {
        Double result = null;
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");
        checkForPowerOf();

        try {
            result = (double)engine.eval(formula);
        } catch (ScriptException e)
        {
            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
        }

        if(result != null){
            try {
                String resultText;
                if(Long.parseLong(String.format(Locale.US, "%.0f", result)) == (double)result){
                    resultText  = String.format(Locale.US, "%.0f", result);
                }
                else {
                    resultText  = String.format(Locale.US, "%.5f", result);
                }
                resultsTV.setText(resultText);
            }
            catch (Exception e){
                Toast.makeText(getContext(), "MATH ERROR: " + e.getCause(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkForPowerOf()
    {
        ArrayList<Integer> indexOfPowers = new ArrayList<>();
        for(int i = 0; i < workings.length(); i++)
        {
            if (workings.charAt(i) == '^')
                indexOfPowers.add(i);
        }

        formula = workings;
        tempFormula = workings;
        for(Integer index: indexOfPowers)
        {
            changeFormula(index);
        }
        formula = tempFormula;
    }

    private void changeFormula(Integer index)
    {
        String numberLeft = "";
        String numberRight = "";

        for(int i = index + 1; i< workings.length(); i++)
        {
            if(isNumeric(workings.charAt(i)))
                numberRight = numberRight + workings.charAt(i);
            else
                break;
        }

        for(int i = index - 1; i >= 0; i--)
        {
            if(isNumeric(workings.charAt(i)))
                numberLeft = numberLeft + workings.charAt(i);
            else
                break;
        }

        String original = numberLeft + "^" + numberRight;
        String changed = "Math.pow("+numberLeft+","+numberRight+")";
        tempFormula = tempFormula.replace(original,changed);
    }

    private boolean isNumeric(char c)
    {
        if((c <= '9' && c >= '0') || c == '.')
            return true;

        return false;
    }

    boolean leftBracket = true;

    public void clearOnClick() {
        workingsTV.setText("");
        workings = "";
        resultsTV.setText("");
        leftBracket = true;
    }

    public void bracketsOnClick()
    {
        if(leftBracket)
        {
            setWorkings("(");
            leftBracket = false;
        }
        else
        {
            setWorkings(")");
            leftBracket = true;
        }
    }
}
