package uk.gov.justice.services.adapter.rest.mutipart;

import static java.lang.String.format;

import uk.gov.justice.services.adapter.rest.interceptor.FileStoreFailedException;

import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;

@ApplicationScoped
public class FileInputDetailsFactory {

    @Inject
    MultipartInputParser multipartInputParser;

    public FileInputDetails createFileInputDetailsFrom(final MultipartInput multipartInput, final PartDefinition partDefinition) {

        final InputPart inputPart = multipartInputParser.getInputPart(multipartInput, partDefinition.getIndex());
        final String fileName = multipartInputParser.extractFileName(inputPart);
        final String fieldName = partDefinition.getFieldName();

        return new FileInputDetails(
                fileName,
                fieldName,
                inputPart.getMediaType(),
                inputStreamFrom(inputPart, fileName));
    }

    private InputStream inputStreamFrom(final InputPart inputPart, final String fileName) {
        try {
            return inputPart.getBody(InputStream.class, null);
        } catch (final IOException e) {
            throw new FileStoreFailedException(format("Failed to store file '%s'", fileName), e);
        }
    }
}