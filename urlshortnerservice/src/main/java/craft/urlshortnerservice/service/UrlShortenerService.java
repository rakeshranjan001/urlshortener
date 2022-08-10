package craft.urlshortnerservice.service;

import craft.urlshortnerservice.model.CustomUrl;
import craft.urlshortnerservice.model.LongUrl;
import craft.urlshortnerservice.model.Url;
import craft.urlshortnerservice.repository.UrlRepository;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.net.URL;
import java.util.Date;
import java.util.Optional;

@Service
public class UrlShortenerService {

    private final UrlRepository urlRepository;
    private final EncoderDecoderService encoderDecoderService;


    @Autowired
    public UrlShortenerService(UrlRepository urlRepository, EncoderDecoderService encoderDecoderService) {
        this.urlRepository = urlRepository;
        this.encoderDecoderService = encoderDecoderService;
    }

    public static boolean isValid(String url)
    {
        /* Try creating a valid URL */
        try {
            new URL(url).toURI();
            return true;
        }
        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }

    public String convertToShortUrl(LongUrl longUrl) throws IllegalArgumentException{
        if(!isValid(longUrl.getUrl())){
           throw new IllegalArgumentException("Invalid Url "+longUrl.getUrl());
        }
        Url urlEntity = new Url();
        urlEntity.setLongUrl(longUrl.getUrl());
        urlEntity.setExpiresAt(longUrl.getExpiresAt());
        urlEntity.setCreatedAt(new Date());

        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
        long leftLimit = 0, rightLimit = (long)Math.pow(62,UrlConstants.maxLen);
        long index = randomDataGenerator.nextLong(leftLimit,rightLimit);
        while(urlRepository.existsById(index))  index = randomDataGenerator.nextLong(leftLimit,rightLimit);

        urlEntity.setId(index);
        Url entity = urlRepository.save(urlEntity);

        return encoderDecoderService.encode(entity.getId());
    }

    public String getLongUrl(String shortUrl){
        long index = encoderDecoderService.decode(shortUrl);
        Url entity = urlRepository.findById(index).orElseThrow(()->new EntityNotFoundException("No records found for "+shortUrl));

        if (entity.getExpiresAt() != null && entity.getExpiresAt().before(new Date())){
            urlRepository.delete(entity);
            throw new EntityNotFoundException("Link expired!");
        }

        return entity.getLongUrl();
    }

    public void removeShortUrlFromEntry(String shortUrl){
        long index = encoderDecoderService.decode(shortUrl);
        Url entity = urlRepository.findById(index).orElseThrow(()->new EntityNotFoundException("No records found for "+shortUrl));
        urlRepository.delete(entity);
    }

    public boolean createCustomUrl(CustomUrl customUrl) throws Exception {
        try {
            encoderDecoderService.validateEncodedString(customUrl.getShortUrl());
            long index = encoderDecoderService.decode(customUrl.getShortUrl());
            Optional<Url> entity = urlRepository.findById(index);
            if(entity.isPresent()){
                throw new Exception("Url is already in use");
            }

            Url entry = new Url();
            entry.setLongUrl(customUrl.getUrl());
            entry.setCreatedAt(new Date());
            entry.setId(index);

            urlRepository.save(entry);
            return true;
        }catch (Exception e){
            throw  e;
        }
    }
}
