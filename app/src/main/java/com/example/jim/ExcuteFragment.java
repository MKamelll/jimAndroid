package com.example.jim;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import jim.src.Expression;
import jim.src.Interpreter;
import jim.src.Lexer;
import jim.src.Parser;
import jim.src.Primary;
import jim.src.StmtExpr;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExcuteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExcuteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MaterialAutoCompleteTextView dropdownComplete;
    private String mExtension = ".jim";
    private MaterialButton btnExcute;
    private String mSrc;
    private ArrayList<Expression> mParseResult;
    private LinearLayout llArgs;
    private StmtExpr.Function mCurrFun;

    public ExcuteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExcuteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExcuteFragment newInstance(String param1, String param2) {
        ExcuteFragment fragment = new ExcuteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_excute, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dropdownComplete = view.findViewById(R.id.dropdownComplete);
        llArgs = view.findViewById(R.id.llArgs);
        ArrayList<String> functions = new ArrayList<String>();
        File dirFiles = getContext().getFilesDir();
        for (String file : dirFiles.list()) {
            String functionName = file.substring(0, file.length() - mExtension.length());
            if (!functions.contains(functionName)) functions.add(functionName);
        }
        ArrayAdapter adapter = new ArrayAdapter(requireContext(), R.layout.autocomplete, functions);
        dropdownComplete.setAdapter(adapter);

        dropdownComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String fName = (String) adapterView.getItemAtPosition(i);
                mSrc = readFile(fName);

                if (mSrc == null) return;

                try {
                    Lexer lexer = new Lexer(mSrc);
                    Parser parser = new Parser(lexer);
                    mParseResult = parser.parse();
                    mCurrFun = ((StmtExpr.Function) mParseResult.get(0));
                    llArgs.removeAllViews();
                    for (var param : mCurrFun.getParams()) {
                        var edtTxt = new TextInputEditText(requireContext());
                        edtTxt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        edtTxt.setHint(((Primary.Identifier) param).getValue().toString());
                        llArgs.addView(edtTxt);
                    }
                } catch (Exception e) {
                    Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });

        btnExcute = view.findViewById(R.id.btnExcute);
        btnExcute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    var identifier = mCurrFun.getIdentifier();
                    var args = new ArrayList<Expression>();
                    int childCount = llArgs.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        var edtTxt = (TextInputEditText) llArgs.getChildAt(i);
                        double value = Double.valueOf(edtTxt.getText().toString());
                        args.add(new Primary.Number(value));
                    }
                    mParseResult.add(new StmtExpr.Call(identifier, args));
                    Interpreter interpreter = new Interpreter(mParseResult);
                    String interpreterResult = interpreter.interpret().toString();
                    var txtView = new MaterialTextView(requireContext());
                    txtView.setText(interpreterResult);
                    llArgs.addView(txtView);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private String readFile(String fname) {
        Context context = getContext();
        File path = context.getFilesDir();
        File file = new File(path,fname + mExtension);

        int length = (int) file.length();
        byte[] buff = new byte[length];
        try {
            FileInputStream in = new FileInputStream(file);
            try {
                in.read(buff);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return new String(buff);
    }
}