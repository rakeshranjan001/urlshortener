package craft.urlshortnerservice.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static craft.urlshortnerservice.service.UrlConstants.maxLen;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class EncoderDecoderTest {

    private EncoderDecoderService encoderDecoderService = new EncoderDecoderService();

    @Test
    public void encode_lessThan62() {
        assertEquals("A", encoderDecoderService.encode(10));
    }

    @Test
    public void encode_moreThan62() {
        assertEquals("G8", encoderDecoderService.encode(1000));
    }

    @Test
    public void decode_singleCharacter() {
        assertEquals(47, encoderDecoderService.decode("l"));
    }

    @Test
    public void customShortUrlValidation(){
        assertTrue(encoderDecoderService.validateEncodedString("123456"));

        String lengthExceptionMessage = "Length should not be greater than "+maxLen;
        Exception exception1 = assertThrows(IllegalArgumentException.class,
                ()->encoderDecoderService.validateEncodedString("1234567"));

        assertTrue(exception1.getMessage().contains(lengthExceptionMessage));

        String invalidCharactersMessage = "Url should contain only valid characters";
        Exception exception2 = assertThrows(IllegalArgumentException.class,
                ()->encoderDecoderService.validateEncodedString("12+456"));

        assertTrue(exception2.getMessage().contains(invalidCharactersMessage));
    }
}