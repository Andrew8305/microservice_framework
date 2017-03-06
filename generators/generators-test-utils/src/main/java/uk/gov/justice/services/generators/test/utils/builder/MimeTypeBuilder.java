package uk.gov.justice.services.generators.test.utils.builder;

import static java.util.Collections.singletonList;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.raml.model.ParamType.FILE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raml.model.MimeType;
import org.raml.model.ParamType;
import org.raml.model.parameter.FormParameter;

public class MimeTypeBuilder {

    final Map<String, List<FormParameter>> formParameters = new HashMap<>();
    private final String type;

    public MimeTypeBuilder(final String type) {
        this.type = type;
    }

    public static MimeTypeBuilder multipartMimeType() {
        return new MimeTypeBuilder(MULTIPART_FORM_DATA);
    }

    public static MimeTypeBuilder multipartWithFileFormParameter(final int index, final String displayName) {
        final MimeTypeBuilder mimeTypeBuilder = multipartMimeType();
        mimeTypeBuilder.withRequiredFileTypeFormParameter(index, displayName);
        return mimeTypeBuilder;
    }

    public MimeTypeBuilder withRequiredFileTypeFormParameter(final int index, final String displayName) {
        return withFormParameter(index, displayName, FILE, true);
    }

    public MimeTypeBuilder withRequiredFormParameter(final int index, final String displayName, final ParamType paramType) {
        return withFormParameter(index, displayName, paramType, true);
    }

    public MimeTypeBuilder withOptionalFormParameter(final int index, final String displayName, final ParamType paramType) {
        return withFormParameter(index, displayName, paramType, false);
    }

    public MimeTypeBuilder withStringIndexFormParameter(final String index, final String displayName, final ParamType paramType) {
        final FormParameter formParameter = new FormParameter();
        formParameter.setType(paramType);
        formParameter.setDisplayName(displayName);
        formParameter.setRequired(true);

        formParameters.put(index, singletonList(formParameter));
        return this;
    }

    public MimeTypeBuilder withNoDisplayNameFormParameter(final int index, final ParamType paramType) {
        final FormParameter formParameter = new FormParameter();
        formParameter.setType(paramType);
        formParameter.setRequired(true);

        formParameters.put(String.valueOf(index), singletonList(formParameter));
        return this;
    }

    public MimeTypeBuilder withFormParameter(final int index, final String displayName, final ParamType paramType, final boolean required) {
        final FormParameter formParameter = new FormParameter();
        formParameter.setType(paramType);
        formParameter.setDisplayName(displayName);
        formParameter.setRequired(required);

        formParameters.put(String.valueOf(index), singletonList(formParameter));
        return this;
    }

    public MimeType build() {
        final MimeType mimeType = new MimeType(type);
        mimeType.setFormParameters(formParameters);
        return mimeType;
    }
}
