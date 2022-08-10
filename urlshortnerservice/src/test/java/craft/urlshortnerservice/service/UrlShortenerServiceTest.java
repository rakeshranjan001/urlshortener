package craft.urlshortnerservice.service;

import craft.urlshortnerservice.model.CustomUrl;
import craft.urlshortnerservice.model.LongUrl;
import craft.urlshortnerservice.model.Url;
import craft.urlshortnerservice.repository.UrlRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.Optional;

import static craft.urlshortnerservice.service.UrlConstants.maxLen;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UrlShortenerServiceTest {

    @Mock
    EncoderDecoderService encoderDecoderService;

    @Mock
    UrlRepository urlRepository;

    @InjectMocks
    UrlShortenerService urlShortenerService;

    @Test
    public void convertLargeUrlToShortUrl(){
        Url url = new Url();
        url.setId(1001);
        url.setLongUrl("http://www.wikipedia.org/test/");
        url.setCreatedAt(new Date());

        LongUrl longUrl = new LongUrl();
        longUrl.setUrl(url.getLongUrl());

        when(urlRepository.save(any(Url.class))).thenReturn(url);
        when(encoderDecoderService.encode(url.getId())).thenReturn("G9");

        assertEquals("G9",urlShortenerService.convertToShortUrl(longUrl));
    }

    @Test
    public void getLongUrlFromShortUrl(){
        Url url = new Url();
        url.setId(1001);
        url.setLongUrl("http://www.wikipedia.org/test/");
        url.setCreatedAt(new Date());

        when(urlRepository.findById(((long)1001))).thenReturn(Optional.of(url));
        when(encoderDecoderService.decode("G9")).thenReturn((long)1001);

        assertEquals("http://www.wikipedia.org/test/",urlShortenerService.getLongUrl("G9"));
    }
}
