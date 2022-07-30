package com.example.jim;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import jim.src.Lexer;
import jim.src.Parser;
import jim.src.Primary;
import jim.src.StmtExpr;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CodeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private EditText edtTxtCode;
    private TextView textViewCode;
    private int mCurrLineCount;
    private String mExtension = ".jim";
    private MaterialButton btnSave;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CodeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CodeFragment newInstance(String param1, String param2) {
        CodeFragment fragment = new CodeFragment();
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
        return inflater.inflate(R.layout.fragment_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpCodeSpace(view);

        btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt = edtTxtCode.getText().toString();
                var lexer = new Lexer(txt);
                try {
                    var parser = new Parser(lexer);
                    var parseResult = parser.parse();
                    if (!(parseResult.get(0) instanceof StmtExpr.Function))
                        throw new Exception("Top level function expected, wrap your script in a function");
                    String fName = ((Primary.Identifier) ((StmtExpr.Function) parseResult.get(0)).getIdentifier()).getValue().toString();
                    writeFile(fName, txt);
                    Toast.makeText(requireContext(), "Saved function '" + fName + "'!", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void setUpCodeSpace(View view) {
        edtTxtCode = view.findViewById(R.id.edtTxtCode);
        textViewCode = view.findViewById(R.id.txtViewCode);

        edtTxtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCurrLineCount = edtTxtCode.getLineCount();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (edtTxtCode.getLineCount() > mCurrLineCount) {
                    String[] oldTextViewContent = textViewCode.getText().toString().split("\n");
                    int lastVal = Integer.valueOf(oldTextViewContent[oldTextViewContent.length - 1]);
                    lastVal++;
                    textViewCode.append("\n" + lastVal);
                } else {
                    ArrayList oldTextViewContent = new ArrayList<String>(Arrays.asList(textViewCode.getText().toString().split("\n")));
                    if (oldTextViewContent.size() > 1)
                        oldTextViewContent.remove(oldTextViewContent.size() - 1);
                    textViewCode.setText(String.join("\n", oldTextViewContent));
                }
                mCurrLineCount = edtTxtCode.getLineCount();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtTxtCode.setSelection(edtTxtCode.getText().length());
    }

    private void writeFile(String fname, String data) {
        Context context = getContext();
        File path = context.getFilesDir();
        File file = new File(path,fname + mExtension);

        try {
            FileOutputStream stream = new FileOutputStream(file);
            try {
                stream.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}