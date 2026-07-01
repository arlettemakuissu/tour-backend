package com.odissey.tour_service.service;

import com.odissey.tour_service.dto.request.ReorderImagesRequest;
import com.odissey.tour_service.entity.HeroImage;
import com.odissey.tour_service.entity.Tour;
import com.odissey.tour_service.entity.TourStatus;
import com.odissey.tour_service.exception.ErrMsg;
import com.odissey.tour_service.exception.TourException;
import com.odissey.tour_service.repository.HeroImageRepository;
import com.odissey.tour_service.repository.TourRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor @Slf4j
public class HeroImageService {

    private final HeroImageRepository heroImageRepository;
    private final TourRepository tourRepository;
    private final CheckSumService checkSumService;

    @Value("${app.heroImage.width}")
    private int width;
    @Value("${app.heroImage.height}")
    private int height;
    @Value("${app.heroImage.size}")
    private long size;
    @Value("${app.heroImage.extensions}")
    private String[] allowedExtensions;


    @Transactional
    public Map<String, String> createHeroImages(int id, int createdBy, MultipartFile[] files) throws IOException {
        Map<String, String> result = new HashMap<>();

        Tour tour = tourRepository.findById(id)
                .orElseThrow(()-> new TourException(ErrMsg.TOUR_NOT_FOUND));
        if(!TourStatus.WORK_IN_PROGRESS.equals(tour.getStatus()) && !TourStatus.OPEN.equals(tour.getStatus()))
            throw new TourException(ErrMsg.TOUR_IMAGE_NOT_UPDATABLE);

        int prg = heroImageRepository.maxPrg(tour.getId()).orElse(0);
        for(MultipartFile file : files){
            if(isFileEmpty(file))
                result.put(file.getOriginalFilename(), ErrMsg.EMPTY_IMAGE);
            long fileSize = file.getSize();
            if(!checkSize(fileSize, size))
                result.put(file.getOriginalFilename(), ErrMsg.FILE_TOO_LARGE);
            if(!checkDimension(file, width, height))
                result.put(file.getOriginalFilename(), ErrMsg.WRONG_DIMENSIONS_IN_PIXELS);
            if(!checkMimeType(file, allowedExtensions))
                result.put(file.getOriginalFilename(), ErrMsg.EXTENSION_NOT_ALLOWED);
            // Verifico che non esista già un'immagine con la stessa firma presente sul database
            CheckSumFile checkSumFile = checkSumService.generateChecksum(file);
            String checkSum = checkSumFile.getChecksum();
            String checkSumAlgorithm = checkSumFile.getCheckSumAlgorithm();
            if(heroImageRepository.existsByCheckSumAlgorithmAndChecksumAndTourId(checkSumAlgorithm, checkSum, tour.getId()))
                result.put(file.getOriginalFilename(), ErrMsg.IMAGE_ALREADY_PRESENT);
            // se il file ha superato tutti i controlli significa che non è presente nella Map
            // e quindi posso istanziare una HeroImage da associare al tour
            if(!result.containsKey(file.getOriginalFilename())) {
                HeroImage heroimage = new HeroImage(
                        UUID.randomUUID().toString(),
                        file.getOriginalFilename(), file.getContentType(), file.getBytes(),
                        tour, prg += 10, createdBy, null,
                        checkSumAlgorithm, checkSum, fileSize
                );
                heroImageRepository.save(heroimage);
                tour.addHeroImage(heroimage);
                result.put(file.getOriginalFilename(), "Immagine caricata correttamente");
            }

        }
        log.info(">>> Risultato salvataggio hero images: " + result);
        return result;
    }

    private boolean isFileEmpty(MultipartFile file){
        return file.isEmpty();
    }

    private boolean checkSize(long fileSize, long size){
        return fileSize < size;
    }

    private BufferedImage fromMultipartFileToBufferedImage(MultipartFile file){
        try{
            return ImageIO.read(file.getInputStream());
        } catch (IOException e){
            return null;
        }
    }

    private boolean checkDimension(MultipartFile file, int width, int height){
        BufferedImage bf = fromMultipartFileToBufferedImage(file);
        if(bf != null)
            return bf.getWidth() == width && bf.getHeight() == height;
        return false;
    }

    private boolean checkMimeType(MultipartFile file, String[] mimeTypes){
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

    public void deleteHeroImage(String id) {
        heroImageRepository.deleteById(id);
    }

    // Riordino dei progressivi delle immagini
    public void reorderImages(List<ReorderImagesRequest> newOrders, int updatedBy){
        for(ReorderImagesRequest r : newOrders){
            heroImageRepository.updatePrg(r.id(), r.prg(), LocalDateTime.now(), updatedBy);
        }

    }


}
