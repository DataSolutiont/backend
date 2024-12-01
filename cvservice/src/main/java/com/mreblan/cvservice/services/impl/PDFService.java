package com.mreblan.cvservice.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.apache.fontbox.encoding.BuiltInEncoding;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.encoding.Encoding;
import org.apache.pdfbox.pdmodel.font.encoding.MacExpertEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.MacOSRomanEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.StandardEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.SymbolEncoding;
import org.apache.pdfbox.pdmodel.font.encoding.Type1Encoding;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mreblan.cvservice.services.FileService;
import com.mreblan.cvservice.models.CvModel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PDFService implements FileService {

    private final PDFTextStripper pdfStripper;
    private final String PATH_TO_FONT = "/fonts/timesnrcyrmt.ttf";

    @Override
    public void uploadFile(MultipartFile file, String dirPath) throws IOException {
            File uploadDir = new File(dirPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            File pdfFile = new File(uploadDir, file.getOriginalFilename());

            try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
                fos.write(file.getBytes());
            }

            log.info("FILE WRITTEN: {}", pdfFile.getAbsolutePath());
    } 

    @Override
    public String extractText(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("uploaded", ".pdf");
        Files.copy(file.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        PDDocument document = Loader.loadPDF(tempFile);
        
        String text = pdfStripper.getText(document);
        document.close();

        return text;
    }

    @Override
    public byte[] generateFile(CvModel cv) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        File fontFile = new File(PATH_TO_FONT);
        Encoding encoding = StandardEncoding.INSTANCE;


        if (!fontFile.exists()) {
            throw new FileNotFoundException("Font file not found");
        }
        InputStream fontStream = new FileInputStream(PATH_TO_FONT);

        if (encoding == null) {
            throw new NullPointerException("Encoding is null");
        }

        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        PDTrueTypeFont font = PDTrueTypeFont.load(document, fontStream, encoding);

        // contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.COURIER), 12);
        contentStream.setFont(font, 12);
        contentStream.beginText();
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText(cv.getCvText());
        contentStream.endText();
        contentStream.close();

        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        document.save(pdfOutputStream);
        document.close();

        fontStream.close();

        return pdfOutputStream.toByteArray();
    }
}
