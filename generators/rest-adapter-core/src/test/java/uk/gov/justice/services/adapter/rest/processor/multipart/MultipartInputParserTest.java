package uk.gov.justice.services.adapter.rest.processor.multipart;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.adapter.rest.exception.BadRequestException;
import uk.gov.justice.services.adapter.rest.mutipart.MultipartInputParser;

import java.util.Map;

import javax.json.JsonObject;
import javax.ws.rs.core.MultivaluedHashMap;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MultipartInputParserTest {

    @InjectMocks
    private MultipartInputParser multipartInputParser;

    @Test
    public void shouldGetTheInputPartFromTheForm() throws Exception {

        final InputPart inputPart = mock(InputPart.class);
        final MultipartInput multipartInput = mock(MultipartInput.class);

        when(multipartInput.getParts()).thenReturn(singletonList(inputPart));

        assertThat(multipartInputParser.getInputPart(multipartInput, 0), is(inputPart));
    }

    @Test
    public void shouldThrowABadRequestExceptionIfNoInputPartFound() throws Exception {

        final MultipartInput multipartInput = mock(MultipartInput.class);

        when(multipartInput.getParts()).thenReturn(emptyList());

        try {
            multipartInputParser.getInputPart(multipartInput, 0);
            fail();
        } catch (final BadRequestException expected) {
            assertThat(expected.getMessage(), is("No InputParts found in request"));
        }
    }

    @Test
    public void shouldExtractTheFileNameFromTheContentDispositionHeader() throws Exception {

        final String headerName = "Content-Disposition";
        final String headerValue = "form-data; name=\"file\"; filename=\"your_file.zip\"";
        final Map<String, String> headers = of(headerName, headerValue);

        final InputPart inputPart = mock(InputPart.class);
        when(inputPart.getHeaders()).thenReturn(new MultivaluedHashMap<>(headers));

        assertThat(multipartInputParser.extractFileName(inputPart), is("your_file.zip"));
    }

    @Test
    public void shouldThrowABadRequestExceptionIfNoContentDispositionHeaderFound() throws Exception {

        final String headerName = "Some-Other-Header-Name";
        final String headerValue = "form-data; name=\"file\"; filename=\"your_file.zip\"";
        final Map<String, String> headers = of(headerName, headerValue);

        final InputPart inputPart = mock(InputPart.class);
        when(inputPart.getHeaders()).thenReturn(new MultivaluedHashMap<>(headers));

        try {
            multipartInputParser.extractFileName(inputPart);
        } catch (final BadRequestException expected) {
            assertThat(expected.getMessage(), is("No header found named 'Content-Disposition'"));
        }
    }

    @Test
    public void shouldThrowABadRequestExceptionIfNoHeadersFound() throws Exception {

        final InputPart inputPart = mock(InputPart.class);
        when(inputPart.getHeaders()).thenReturn(new MultivaluedHashMap<>());

        try {
            multipartInputParser.extractFileName(inputPart);
        } catch (final BadRequestException expected) {
            assertThat(expected.getMessage(), is("No header found named 'Content-Disposition'"));
        }
    }

    @Test
    public void shouldThrowABadRequestExceptionIfNoFilenameFoundInContentDispositionHeader() throws Exception {

        final String headerName = "Content-Disposition";
        final String headerValue = "form-data; name=\"file\"";
        final Map<String, String> headers = of(headerName, headerValue);

        final InputPart inputPart = mock(InputPart.class);
        when(inputPart.getHeaders()).thenReturn(new MultivaluedHashMap<>(headers));

        try {
            multipartInputParser.extractFileName(inputPart);
        } catch (final BadRequestException expected) {
            assertThat(expected.getMessage(), is("Failed to find 'filename' in 'Content-Disposition' header"));
        }
    }

    @Test
    public void shouldReturnJsonObjectFromFirstPartOfMultipart() throws Exception {

        final MultipartInput multipartInput = mock(MultipartInput.class);
        final InputPart inputPart = mock(InputPart.class);
        when(multipartInput.getParts()).thenReturn(singletonList(inputPart));
        when(inputPart.getBodyAsString()).thenReturn("{}");

        final JsonObject jsonObject = multipartInputParser.getPartMetadata(multipartInput);

        assertThat(jsonObject.toString(), is("{}"));
    }

    @Test
    public void shouldThrowABadRequestExceptionIfJsonObjectIsInvalid() throws Exception {

        final MultipartInput multipartInput = mock(MultipartInput.class);
        final InputPart inputPart = mock(InputPart.class);
        when(multipartInput.getParts()).thenReturn(singletonList(inputPart));
        when(inputPart.getBodyAsString()).thenReturn("");

        try {
            multipartInputParser.getPartMetadata(multipartInput);
        } catch (final BadRequestException expected) {
            assertThat(expected.getMessage(), is("Failed to get first input part as json object"));
        }
    }
}
