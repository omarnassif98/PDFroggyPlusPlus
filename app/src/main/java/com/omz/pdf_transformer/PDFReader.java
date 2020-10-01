//TEST
package com.omz.pdf_transformer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PDFReader extends AppCompatActivity {

    private InputStream LoadPickedPDF() throws FileNotFoundException {
        Intent intent = getIntent();
        Uri pdfURI = intent.getParcelableExtra("pdfURI");
        return getContentResolver().openInputStream(pdfURI);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_view);

        Intent intent = getIntent();
        Uri pdfURI = intent.getParcelableExtra("pdfURI");
        InputStream pfd = null;
        try {
            pfd = LoadPickedPDF();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        PDFBoxResourceLoader.init(getApplicationContext());
        final TextView documentTextView = findViewById(R.id.documentView);
        documentTextView.setMovementMethod(new ScrollingMovementMethod());
        TextView pdfNameView = findViewById(R.id.pdfNameView);
        final TextView pdfPageNumberView = findViewById(R.id.pdfPageNumberView);
        Button nextPageBtn = findViewById(R.id.nextPageBtn);
        Button previousPageBtn = findViewById(R.id.previousPageBtn);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch formatSwitch = findViewById(R.id.formatSwtich);
        final SharedPreferences pref = getSharedPreferences("MyPref", 0);
        final SharedPreferences.Editor editor = pref.edit();
        AssetManager assetManager = getAssets();
        try {
            final PDDocument document = PDDocument.load(pfd);
            //PDDocument.load(pfd)
            int pageNumber = 1;
            editor.putInt("pageNumber", 0);


            // Instantiate PDFTextStripper class
            PDFTextStripper pdfStripper = new PDFTextStripper();
            // Retrieving text from PDF document
            pdfStripper.setStartPage(pageNumber);
            pdfStripper.setEndPage(pageNumber);
            String pageText = pdfStripper.getText(document);
            documentTextView.setText(pageText);

            previousPageBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    PDFTextStripper pdfStripper = null;
                    try {
                        pdfStripper = new PDFTextStripper();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int pageNumber = pref.getInt("pageNumber", 1) - 1;
                    if (pageNumber != 0) {
                        editor.putInt("pageNumber", pageNumber);
                        editor.apply();
                        pdfStripper.setStartPage(pageNumber);
                        pdfStripper.setEndPage(pageNumber);
                        String pageText = null;
                        try {
                            pageText = pdfStripper.getText(document);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        documentTextView.setText(pageText);
                        pdfPageNumberView.setText(String.valueOf(pageNumber));
                    }
                }
            });

            nextPageBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    PDFTextStripper pdfStripper = null;
                    try {
                        pdfStripper = new PDFTextStripper();
                    } catch (IOException e) {
                        Log.d("Something", "somethignm");
                        e.printStackTrace();
                    }
                    int pageNumber = pref.getInt("pageNumber", 1) + 1;
                    if (pageNumber <= document.getNumberOfPages()) {
                        editor.putInt("pageNumber", pageNumber);
                        editor.apply();
                        pdfStripper.setStartPage(pageNumber);
                        pdfStripper.setEndPage(pageNumber);
                        String pageText = null;
                        try {
                            pageText = pdfStripper.getText(document);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        documentTextView.setText(pageText);
                        pdfPageNumberView.setText(String.valueOf(pageNumber));
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        formatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    documentTextView.setLineSpacing(2,2);
                }
                else {
                    documentTextView.setLineSpacing(1, 1);
                }
            }
        });
    }

}