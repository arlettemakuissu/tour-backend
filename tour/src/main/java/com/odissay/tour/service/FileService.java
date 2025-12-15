package com.odissay.tour.service;

import com.odissay.tour.exception.Exception400;
import com.odissay.tour.exception.Exception404;
import com.odissay.tour.exception.Exception409;
import com.odissay.tour.exception.Exception500;
import com.odissay.tour.model.dto.reponse.TourDetailResponse;
import com.odissay.tour.model.entity.Tour;
import com.odissay.tour.model.entity.emurator.TourStatus;
import com.odissay.tour.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final TourRepository tourRepository;

    @Value("${tour.image.size}")
    long size;
    @Value("${tour.image.mimeTypes}")
    String[] mimeTypes;
    @Value("${tour.image.width}")
    int width;
    @Value("${tour.image.height}")
    int height;
    @Value("${tour.image.path}")
    String path;


    public TourDetailResponse uploadImage(int tourId, MultipartFile file){
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(()-> new Exception404("Tour non trovato con id "+tourId));
        if(!tour.getStatus().equals(TourStatus.WORK_IN_PROGRESS))
            throw new Exception409("Il tour è ormai pubblicato, non si può cambiare l'immagine.");

        if(!checkIsNotEmpty(file))
            throw new Exception400("Il file è vuoto.");

        if(!checkSize(file, size))
            throw new Exception400("Il file supera la dimesione di "+size);

        if(!checkDimension(file, width, height))
            throw new Exception400("Il file non è delle dimensioni di "+width+"px X "+height+"px");

        if(checkExtensions(file, mimeTypes))
            throw new Exception400("Il file non è del tipo consentito");
        uploadFile(file);
        return null;
    }


    private boolean checkIsNotEmpty(MultipartFile file){

        return !file.isEmpty();
    }

    private boolean checkSize(MultipartFile file, long size){

        return file.getSize() < size;
    }

    private BufferedImage fromMultipartFileToBufferedImage(MultipartFile file){
        try{
            return ImageIO.read(file.getInputStream());
        } catch (IOException e){
            throw new Exception400("File non valido");
        }
    }

    private boolean checkDimension(MultipartFile file, int width, int height){
        BufferedImage bf = fromMultipartFileToBufferedImage(file);
        return bf.getWidth() == width && bf.getHeight() == height;
    }


    private boolean checkExtensions(MultipartFile file, String[] mimeTypes){
        log.info(">>> getContentType: "+file.getContentType());
        String trueMimeType = getTrueMimeType(file);
        for(String s : mimeTypes){
            if(s.equals(trueMimeType))
                return true;
        }
        return false;
    }

    private String getTrueMimeType(MultipartFile file){
        Tika tika = new Tika();
        // uso il try-with-resource in modo da essere sicuro che l'inputStream venga chiuso
        try(InputStream inputStream = file.getInputStream()){
            return tika.detect(inputStream);
        } catch (IOException e){
            return null;
        }
    }


    private String uploadFile(MultipartFile file){

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + " ." + extension;

        try{
            Path destinationPath = Paths.get(path + newFileName);
            Files.write(destinationPath,file.getBytes());
        } catch (IOException e) {
            log.error(">>> "+e.getMessage());
            throw  new Exception500("Impossibile caricare il file");
        }
        return newFileName;

    }


}

