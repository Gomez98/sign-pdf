package com.goan.signmicroservice.service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.signatures.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

@Component
public class SignDocumentService {

    public Mono<String> signPdf(FilePart filePart) {
        String filename = filePart.filename();

        if (filename.matches("[^a-zA-Z0-9.]+") || !filename.toLowerCase().endsWith(".pdf")) {
            return Mono.error(new IllegalArgumentException("El nombre del archivo no es válido"));
        }

        String projectDir = new File("").getAbsolutePath();
        String uploadDir = projectDir + File.separator + "documents";
        File destDir = new File(uploadDir);

        if (!destDir.exists() && !destDir.mkdirs()) {
            return Mono.error(new IOException("No se pudo crear los directorios necesarios para guardar el archivo"));
        }

        File destFile = new File(destDir, filename);

        return filePart.transferTo(destFile)
                .then(Mono.fromCallable(() -> {
                    String src = destFile.getPath();
                    String intermediate = "intermediate.pdf";

                    String keystorePath = "C240409101955.pfx";
                    String keystorePassword = "GHxN0nuHzYhY12";
                    String keyAlias;

                    try (FileInputStream fis = new FileInputStream(keystorePath)) {

                        KeyStore ks = KeyStore.getInstance("PKCS12");
                        ks.load(fis, keystorePassword.toCharArray());

                        Enumeration<String> aliases = ks.aliases();
                        keyAlias = aliases.nextElement();

                        while(aliases.hasMoreElements()) {
                            keyAlias = aliases.nextElement();
                        }

                        PrivateKey pk = (PrivateKey) ks.getKey(keyAlias, keystorePassword.toCharArray());
                        Certificate[] chain = ks.getCertificateChain(keyAlias);

                        Files.copy(Paths.get(src), Paths.get(intermediate), StandardCopyOption.REPLACE_EXISTING);

                        ImageData imageData = ImageDataFactory.create("german-firma.png");

                        try (PdfReader reader1 = new PdfReader(src);
                             PdfWriter writer1 = new PdfWriter(intermediate);
                             PdfDocument pdfDoc1 = new PdfDocument(reader1, writer1)) {

                            Document document = new Document(pdfDoc1);
                            String currentDate = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
                            String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());

                            for (int i = 1; i <= pdfDoc1.getNumberOfPages(); i++) {
                                Image image = new Image(imageData);
                                image.setFixedPosition(i, 5, 50);
                                image.setWidth(100);
                                image.setHeight(30);
                                document.add(image);

                                String dateParagraphText = String.format("Firmado digitalmente por %s \n Fecha: %s %s\n ", keyAlias, currentDate, currentTime);
                                Paragraph dateParagraph = new Paragraph(dateParagraphText);
                                dateParagraph.setFixedPosition(i, 5, 20, UnitValue.createPointValue(100));
                                dateParagraph.setFontSize(5);
                                dateParagraph.setHorizontalAlignment(HorizontalAlignment.CENTER);
                                document.add(dateParagraph);
                            }
                        }

                        String signedDirPath = projectDir + File.separator + "documents" + File.separator + "signed";
                        File signedDir = new File(signedDirPath);

                        if (!signedDir.exists() && !signedDir.mkdirs()){
                            throw new IOException("No se pudo crear los directorios necesarios para guardar el archivo");
                        }

                        String signedFileName = filename.replace(".pdf", "-signed.pdf");
                        File signedFile = new File(signedDir + File.separator + signedFileName);

                        try (PdfReader reader2 = new PdfReader(intermediate)) {
                            PdfSigner signer = new PdfSigner(reader2, new FileOutputStream(signedFile), new StampingProperties());
                            signer.setReason(keyAlias);

                            Security.addProvider(new BouncyCastleProvider());
                            IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, "BC");
                            IExternalDigest digest = new BouncyCastleDigest();
                            signer.signDetached(digest, pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

                        } catch (Exception ex) {
                            System.err.println("No se pudo borrar el archivo " + destFile.getAbsolutePath());
                            throw new RuntimeException("Error al firmar PDF: " + ex.getMessage());
                        }

                    } catch (Exception ex) {
                        throw new RuntimeException("Error loading keystore", ex);
                    }

                    return "Documento firmado exitosamente";

                }).subscribeOn(Schedulers.boundedElastic()));
    }
}
