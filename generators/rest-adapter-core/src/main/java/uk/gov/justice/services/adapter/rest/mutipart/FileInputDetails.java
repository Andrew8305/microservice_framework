package uk.gov.justice.services.adapter.rest.mutipart;

import java.io.InputStream;

import javax.ws.rs.core.MediaType;

public class FileInputDetails {

    public static final String FILE_INPUT_DETAILS_LIST = "fileInputDetailsList";

    private final String fileName;
    private final String fieldName;
    private final MediaType mediaType;
    private final InputStream inputStream;

    public FileInputDetails(
            final String fileName,
            final String fieldName,
            final MediaType mediaType,
            final InputStream inputStream) {
        this.fileName = fileName;
        this.fieldName = fieldName;
        this.mediaType = mediaType;
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
