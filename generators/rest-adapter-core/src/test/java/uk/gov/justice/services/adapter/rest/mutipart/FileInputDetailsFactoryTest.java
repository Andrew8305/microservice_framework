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

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FileInputDetailsFactoryTest {

    @Mock
    private MultipartInputParser multipartInputParser;

    @InjectMocks
    private FileInputDetailsFactory fileInputDetailsFactory;

    @Test
    public void shouldCreateFileInputDetailsFromFilePart() throws Exception {

        final String fileName = "the-file-name.jpeg";
        final String fieldName = "myFieldName";
        final MediaType mediaType = TEXT_XML_TYPE;
        final int index = 23;
        final PartDefinition partDefinition = new PartDefinition(index, fieldName);

        final MultipartInput multipartInput = mock(MultipartInput.class);
        final InputPart inputPart = mock(InputPart.class);
        final InputStream inputStream = mock(InputStream.class);

        when(multipartInputParser.getInputPart(multipartInput, index)).thenReturn(inputPart);
        when(multipartInputParser.extractFileName(inputPart)).thenReturn(fileName);
        when(inputPart.getMediaType()).thenReturn(mediaType);
        when(inputPart.getBody(InputStream.class, null)).thenReturn(inputStream);

        final List<FileInputDetails> fileInputDetails = fileInputDetailsFactory.createFileInputDetailsFrom(
                multipartInput,
                singletonList(partDefinition));

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
        final MediaType mediaType = TEXT_XML_TYPE;
        final int index = 23;
        final PartDefinition partDefinition = new PartDefinition(index, fieldName);

        final MultipartInput multipartInput = mock(MultipartInput.class);
        final InputPart inputPart = mock(InputPart.class);

        when(multipartInputParser.getInputPart(multipartInput, index)).thenReturn(inputPart);
        when(multipartInputParser.extractFileName(inputPart)).thenReturn(fileName);
        when(inputPart.getMediaType()).thenReturn(mediaType);
        when(inputPart.getBody(InputStream.class, null)).thenThrow(ioException);

        try {
            fileInputDetailsFactory.createFileInputDetailsFrom(
                    multipartInput,
                    singletonList(partDefinition));
            fail();
        } catch (final FileStoreFailedException expected) {
            assertThat(expected.getCause(), is(ioException));
            assertThat(expected.getMessage(), is("Failed to store file 'the-file-name.jpeg'"));
        }
    }
}
