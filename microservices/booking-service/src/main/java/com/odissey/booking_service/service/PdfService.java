package com.odissey.booking_service.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.odissey.booking_service.dto.response.TourInfoForBookingResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.EnumMap;
import java.util.Map;

@Service
@Slf4j
public class PdfService {

    public byte[] createPdf(TourInfoForBookingResponse tourInfoForBookingResponse, String code) throws Exception{

        // creazione di uno stream di array di byte
        try(PDDocument doc = new PDDocument();
            ByteArrayOutputStream pdfOut = new ByteArrayOutputStream()) {

            // setto dimensione pagina
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDRectangle mediaBox = page.getMediaBox();
            float margin = 50f;
            float pageHeigth = mediaBox.getHeight();

            // QR Code
            byte[] qrCode = generateQrPngBytes(code, 300, 300);
            PDImageXObject qrImage = PDImageXObject.createFromByteArray(doc, qrCode, "qr.png");

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
                // Punto di partenza in alto a sinistra
                cs.newLineAtOffset(margin, pageHeigth - margin - 160);
                // 👇 Imposta interlinea (spazio tra righe)
                cs.setLeading(18f);
                cs.showText(tourInfoForBookingResponse.getName().toUpperCase()
                        + " (" + tourInfoForBookingResponse.getCountry() + ")");
                cs.newLine(); // ← VA A CAPO
                cs.showText("Partenza: " + tourInfoForBookingResponse.getStartDate()
                        + " - Prezzo: " + tourInfoForBookingResponse.getPrice());
                cs.newLine(); // ← VA A CAPO
                cs.showText("Agenzia: "+ tourInfoForBookingResponse.getAgency());
                cs.newLine(); // ← VA A CAPO
                cs.showText("Codice prenotazione: " + code);
                cs.newLine(); // ← VA A CAPO
                cs.endText();
                cs.drawImage(qrImage, margin, pageHeigth - margin - 150, 150, 150 );
            }

            doc.save(pdfOut);
            return pdfOut.toByteArray();
        }
    }

    /** Genera un PNG (byte[]) con dentro il QR code che codifica la stringa "code". */
    public byte[] generateQrPngBytes(String code, int width, int height) throws Exception {
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix matrix = new MultiFormatWriter()
                .encode(code, BarcodeFormat.QR_CODE, width, height, hints);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
            }
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            return baos.toByteArray();
        }
    }
}
