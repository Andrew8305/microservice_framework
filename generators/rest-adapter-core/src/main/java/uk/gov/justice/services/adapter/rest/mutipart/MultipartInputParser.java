package uk.gov.justice.services.adapter.rest.mutipart;

import static java.util.regex.Pattern.compile;
import static javax.json.Json.createReader;

import uk.gov.justice.services.adapter.rest.exception.BadRequestException;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;

@ApplicationScoped
public class MultipartInputParser {

    private static final String CONTENT_DISPOSITION_HEADER_NAME = "Content-Disposition";
    private static final Pattern FIND_FILENAME_PATTERN = compile("^.*filename=\"(.*)\".*$");

    private static final int FILENAME_MATCHER_GROUP = 1;
    private static final int FIRST_PART_IN_LIST = 0;

    public JsonObject getPartMetadata(final MultipartInput multipartInput) {

        final InputPart inputPart = getInputPart(multipartInput, FIRST_PART_IN_LIST);

        try {
            final String json = inputPart.getBodyAsString();
            try (final JsonReader jsonReader = createReader(new StringReader(json))) {
                return jsonReader.readObject();
            }
        } catch (final IOException | JsonParsingException e) {
            throw new BadRequestException("Failed to get first input part as json object");
        }
    }

    public InputPart getInputPart(final MultipartInput multipartInput, final int index) {

        final List<InputPart> inputParts = multipartInput.getParts();

        if (inputParts.isEmpty()) {
            throw new BadRequestException("No InputParts found in request");
        }

        return inputParts.get(index);
    }

    public String extractFileName(final InputPart filePart) {

        final String headerValue = filePart
                .getHeaders()
                .getFirst(CONTENT_DISPOSITION_HEADER_NAME);

        if (headerValue == null) {
            throw new BadRequestException("No header found named '" + CONTENT_DISPOSITION_HEADER_NAME + "'");
        }

        final Matcher matcher = FIND_FILENAME_PATTERN.matcher(headerValue);

        if (matcher.find()) {
            return matcher.group(FILENAME_MATCHER_GROUP);
        }

        throw new BadRequestException("Failed to find 'filename' in '" + CONTENT_DISPOSITION_HEADER_NAME + "' header");
    }
}
