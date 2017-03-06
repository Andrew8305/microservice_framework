package uk.gov.justice.services.generators.commons.validator;

import static org.raml.model.ActionType.POST;
import static org.raml.model.ParamType.FILE;
import static org.raml.model.ParamType.STRING;
import static uk.gov.justice.services.generators.test.utils.builder.HttpActionBuilder.httpAction;
import static uk.gov.justice.services.generators.test.utils.builder.MimeTypeBuilder.multipartMimeType;
import static uk.gov.justice.services.generators.test.utils.builder.MimeTypeBuilder.multipartWithFileFormParameter;
import static uk.gov.justice.services.generators.test.utils.builder.RamlBuilder.restRamlWithDefaults;
import static uk.gov.justice.services.generators.test.utils.builder.ResourceBuilder.resource;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.raml.model.Raml;

public class MultipartHasFormParametersTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private RamlValidator validator = new MultipartHasFormParameters();

    @Test
    public void shouldPassIfMultipartContainsCorrectFormParameter() throws Exception {
        final Raml raml = restRamlWithDefaults()
                .with(resource("/some/path")
                        .with(httpAction()
                                .withHttpActionType(POST)
                                .withMediaTypeWithoutSchema(multipartWithFileFormParameter(0, "photoId")))
                ).build();

        validator.validate(raml);
    }

    @Test
    public void shouldFailIfMultipartHasNoFormParameters() throws Exception {
        exception.expect(RamlValidationException.class);
        exception.expectMessage("Multipart form must contain form parameters");

        final Raml raml = restRamlWithDefaults()
                .with(resource("/some/path")
                        .with(httpAction()
                                .withHttpActionType(POST)
                                .withMediaTypeWithoutSchema(multipartMimeType()))
                ).build();

        validator.validate(raml);
    }

    @Test
    public void shouldFailIfMultipartHasFormParameterWithIndexThatIsNotPartNumber() throws Exception {
        exception.expect(RamlValidationException.class);
        exception.expectMessage("Multipart form parameter index should be a number identifying the part index of the multipart form");

        final Raml raml = restRamlWithDefaults()
                .with(resource("/some/path")
                        .with(httpAction()
                                .withHttpActionType(POST)
                                .withMediaTypeWithoutSchema(multipartMimeType()
                                        .withStringIndexFormParameter("notANumber", "photoId", FILE)))
                ).build();

        validator.validate(raml);
    }

    @Test
    public void shouldFailIfMultipartHasFormParameterWithNoFieldName() throws Exception {
        exception.expect(RamlValidationException.class);
        exception.expectMessage("Multipart form parameter requires a displayName to identify the field name of the file reference");

        final Raml raml = restRamlWithDefaults()
                .with(resource("/some/path")
                        .with(httpAction()
                                .withHttpActionType(POST)
                                .withMediaTypeWithoutSchema(multipartMimeType()
                                        .withNoDisplayNameFormParameter(0, FILE)))
                ).build();

        validator.validate(raml);
    }

    @Test
    public void shouldFailIfMultipartHasFormParameterWithIncorrectType() throws Exception {
        exception.expect(RamlValidationException.class);
        exception.expectMessage("Multipart form parameter is expected to be of type FILE, instead was STRING");

        final Raml raml = restRamlWithDefaults()
                .with(resource("/some/path")
                        .with(httpAction()
                                .withHttpActionType(POST)
                                .withMediaTypeWithoutSchema(multipartMimeType()
                                        .withFormParameter(0, "photoId", STRING, true)))
                ).build();

        validator.validate(raml);
    }

    @Test
    public void shouldFailIfMultipartHasFormParameterThatAreOptional() throws Exception {
        exception.expect(RamlValidationException.class);
        exception.expectMessage("Multipart form parameter should be required not optional");

        final Raml raml = restRamlWithDefaults()
                .with(resource("/some/path")
                        .with(httpAction()
                                .withHttpActionType(POST)
                                .withMediaTypeWithoutSchema(multipartMimeType()
                                        .withFormParameter(0, "photoId", FILE, false)))
                ).build();

        validator.validate(raml);
    }
}