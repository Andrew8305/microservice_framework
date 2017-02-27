package uk.gov.justice.services.adapter.rest.mutipart;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;
import static javax.json.JsonValue.ValueType.OBJECT;
import static uk.gov.justice.services.messaging.JsonObjects.getJsonString;

import uk.gov.justice.services.adapter.rest.exception.BadRequestException;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.JsonString;

@ApplicationScoped
public class PartDefinitionParser {

    public List<PartDefinition> getPartDefinitions(final JsonObject partMetadata) {

        return partMetadata.getJsonArray("parts").stream().map(part -> {

            if(OBJECT.equals(part.getValueType())) {

                final JsonObject jsonObject = (JsonObject) part;
                final JsonString fieldName = getJsonString(jsonObject, "fieldName")
                        .orElseThrow(() -> new BadRequestException("Failed to find 'fieldName' property in part metadata json"));
                final JsonString filePartIndexJsonString = getJsonString(jsonObject, "filePartIndex")
                        .orElseThrow(() -> new BadRequestException("Failed to find 'filePartIndex' property in part metadata json"));

                final int filePartIndex = asInt(filePartIndexJsonString);

                return new PartDefinition(filePartIndex, fieldName.getString());
            }

            throw new BadRequestException("Parts array does not contain JsonObjects");

        }).collect(toList());
    }

    private int asInt(final JsonString filePartIndexJsonString) {
        try {
            return parseInt(filePartIndexJsonString.getString());
        } catch (final NumberFormatException e) {
            throw new BadRequestException("File part index is not an integer", e);
        }
    }
}
