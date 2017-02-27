package uk.gov.justice.services.adapter.rest.mutipart;

import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createObjectBuilder;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import uk.gov.justice.services.adapter.rest.exception.BadRequestException;

import java.util.List;

import javax.json.JsonObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PartDefinitionParserTest {

    @InjectMocks
    private PartDefinitionParser partDefinitionParser;

    @Test
    public void shouldParseThePartMetadataJsonIntoPartDefinitionObjects() throws Exception {

        final JsonObject partMetadata = createObjectBuilder()
                .add("parts", createArrayBuilder()
                        .add(createObjectBuilder()
                                .add("fieldName", "fieldName_1")
                                .add("filePartIndex", "1"))
                        .add(createObjectBuilder()
                                .add("fieldName", "fieldName_2")
                                .add("filePartIndex", "2"))
                        .add(createObjectBuilder()
                                .add("fieldName", "fieldName_3")
                                .add("filePartIndex", "3")))
                .build();

        final List<PartDefinition> partDefinitions = partDefinitionParser.getPartDefinitions(partMetadata);

        assertThat(partDefinitions.size(), is(3));

        assertThat(partDefinitions.get(0).getFieldName(), is("fieldName_1"));
        assertThat(partDefinitions.get(0).getIndex(), is(1));
        assertThat(partDefinitions.get(1).getFieldName(), is("fieldName_2"));
        assertThat(partDefinitions.get(1).getIndex(), is(2));
        assertThat(partDefinitions.get(2).getFieldName(), is("fieldName_3"));
        assertThat(partDefinitions.get(2).getIndex(), is(3));
    }

    @Test
    public void shouldThrowABedRequestExceptionIfNoFieldNamePropertyFoundForPart() throws Exception {

        final JsonObject partMetadata = createObjectBuilder()
                .add("parts", createArrayBuilder()
                        .add(createObjectBuilder()
                                .add("filePartIndex", "2")))
                .build();

        try {
            partDefinitionParser.getPartDefinitions(partMetadata);
            fail();
        } catch (final BadRequestException expected) {
            assertThat(expected.getMessage(), is("Failed to find 'fieldName' property in part metadata json"));
        }
    }

    @Test
    public void shouldThrowABedRequestExceptionIfNoIndexPropertyFoundForPart() throws Exception {

        final JsonObject partMetadata = createObjectBuilder()
                .add("parts", createArrayBuilder()
                        .add(createObjectBuilder()
                                .add("fieldName", "fieldName")))
                .build();

        try {
            partDefinitionParser.getPartDefinitions(partMetadata);
            fail();
        } catch (final BadRequestException expected) {
            assertThat(expected.getMessage(), is("Failed to find 'filePartIndex' property in part metadata json"));
        }
    }

    @Test
    public void shouldThrowABedRequestExceptionIfTheIndexPropertyIsNotAnInteger() throws Exception {

        final JsonObject partMetadata = createObjectBuilder()
                .add("parts", createArrayBuilder()
                        .add(createObjectBuilder()
                                .add("fieldName", "fieldName_1")
                                .add("filePartIndex", "not-an-integer")))
                .build();

        try {
            partDefinitionParser.getPartDefinitions(partMetadata);
            fail();
        } catch (final BadRequestException expected) {
            assertThat(expected.getMessage(), is("File part index is not an integer"));
            assertThat(expected.getCause(), is(instanceOf(NumberFormatException.class)));
        }
    }

    @Test
    public void shouldThrowABedRequestExceptionIfNotAnArrayOfJsonObjects() throws Exception {

        final JsonObject partMetadata = createObjectBuilder()
                .add("parts", createArrayBuilder()
                        .add("not a JsonObject"))
                .build();

        try {
            partDefinitionParser.getPartDefinitions(partMetadata);
            fail();
        } catch (final BadRequestException expected) {
            assertThat(expected.getMessage(), is("Parts array does not contain JsonObjects"));
        }
    }
}
