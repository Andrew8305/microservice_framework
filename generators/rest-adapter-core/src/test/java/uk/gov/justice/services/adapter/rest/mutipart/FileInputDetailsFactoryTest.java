package uk.gov.justice.services.adapter.rest.mutipart;

import static java.util.Collections.singletonList;
import static javax.ws.rs.core.MediaType.TEXT_XML_TYPE;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.adapter.rest.interceptor.FileStoreFailedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import com.google.common.collect.ImmutableMap;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FileInputDetailsFactoryTest {

    @Mock
    private InputPartFileNameExtractor inputPartFileNameExtractor;

    @InjectMocks
    private FileInputDetailsFactory fileInputDetailsFactory;

    @Test
    public void shouldCreateFileInputDetailsFromFilePart() throws Exception {

        final String fileName = "the-file-name.jpeg";
        final String fieldName = "myFieldName";
        final MediaType mediaType = TEXT_XML_TYPE;

        final MultipartFormDataInput multipartFormDataInput = mock(MultipartFormDataInput.class);
        final InputPart inputPart = mock(InputPart.class);
        final InputStream inputStream = mock(InputStream.class);

        final Map<String, List<InputPart>> formDataMap = ImmutableMap.of(fieldName, singletonList(inputPart));

        when(multipartFormDataInput.getFormDataMap()).thenReturn(formDataMap);
        when(inputPartFileNameExtractor.extractFileName(inputPart)).thenReturn(fileName);
        when(inputPart.getMediaType()).thenReturn(mediaType);
        when(inputPart.getBody(InputStream.class, null)).thenReturn(inputStream);

        final List<FileInputDetails> fileInputDetails = fileInputDetailsFactory.createFileInputDetailsFrom(
                multipartFormDataInput,
                singletonList(fieldName));

        final FileInputDetails inputDetails = fileInputDetails.get(0);
        assertThat(inputDetails.getFileName(), is(fileName));
        assertThat(inputDetails.getFieldName(), is(fieldName));
        assertThat(inputDetails.getInputStream(), is(inputStream));
        assertThat(inputDetails.getMediaType(), is(mediaType));
    }

    //TODO: Add multiple part definition test

    @Test
    public void shouldThrowFileStoreFailedExceptionIfGettingFileInputStreamFails() throws Exception {

        final IOException ioException = new IOException("bunnies");

        final String fileName = "the-file-name.jpeg";
        final String fieldName = "myFieldName";

        final MultipartFormDataInput multipartFormDataInput = mock(MultipartFormDataInput.class);
        final InputPart inputPart = mock(InputPart.class);

        final Map<String, List<InputPart>> formDataMap = ImmutableMap.of(fieldName, singletonList(inputPart));

        when(multipartFormDataInput.getFormDataMap()).thenReturn(formDataMap);
        when(inputPartFileNameExtractor.extractFileName(inputPart)).thenReturn(fileName);
        when(inputPart.getMediaType()).thenReturn(TEXT_XML_TYPE);
        when(inputPart.getBody(InputStream.class, null)).thenThrow(ioException);

        try {
            fileInputDetailsFactory.createFileInputDetailsFrom(
                    multipartFormDataInput,
                    singletonList(fieldName));
            fail();
        } catch (final FileStoreFailedException expected) {
            assertThat(expected.getCause(), is(ioException));
            assertThat(expected.getMessage(), is("Failed to store file 'the-file-name.jpeg'"));
        }
    }
}
