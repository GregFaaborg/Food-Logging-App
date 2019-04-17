package com.CSCE4901.Mint.Report;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;




class TemplatePDF {

    private final Context context;
    private File pdfFile;
    private Document document;
    private Paragraph paragraph;
    private final Font fTitle = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
    private final Font fsubTitle = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private final Font fText = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private final Font fHighText = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD);



    TemplatePDF(Context context) {
        this.context = context;
    }


    private void createFile() {
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "MintReports");



        if(!folder.exists()) {
            folder.mkdirs();
        }


        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("MM-d-yyyy h:mm:ss", Locale.US);
        String dateCreated = dateFormat.format(currentDate);

        pdfFile = new File(folder, dateCreated + ".pdf");

        //Toast.makeText(context, pdfFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();


        File[] files = folder.listFiles();

        if (files != null)
        {
            for (File file : files){
                Log.d("File: ", file.getPath());
            }
        }

    }


    void openDocument() {
        createFile();

        try {
            document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();
        } catch (Exception e) {
            Log.e("openDocument", e.toString());
        }
    }



    void closeDocument(){
        document.close();
    }

    void addMetaData(String title, String subject, String author) {
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);


    }

    void addTitles(String title, String subTitle, String date) {

        try {
            paragraph = new Paragraph();
            addChild(new Paragraph(title,fTitle));
            addChild(new Paragraph(subTitle,fsubTitle));
            addChild(new Paragraph(date,fHighText));
            paragraph.setSpacingAfter(30);
            document.add(paragraph);

            Chunk chunk = new Chunk("Entry 1",fTitle);
            Phrase phrase = new Phrase();
            phrase.add(chunk);
            Paragraph paragraph = new Paragraph();
            paragraph.add(phrase);
            paragraph.setAlignment(Element.ALIGN_CENTER);


        } catch (DocumentException e) {
            Log.e("addTitles", e.toString());
            e.printStackTrace();
        }

    }

    private void addChild(Paragraph childParagraph) {
        childParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childParagraph);
    }

    void addParagraph(String text) {

        try {
            paragraph = new Paragraph(text, fText);
            paragraph.setSpacingAfter(5);
            paragraph.setSpacingBefore(5);
            document.add(paragraph);

        } catch (Exception e) {
            Log.e("addParagraph", e.toString());
            e.printStackTrace();
        }
    }

    void viewPDF() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(pdfFile),"application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e){
            Toast.makeText(context, "No PDF READER", Toast.LENGTH_SHORT).show();
        }
    }

}
