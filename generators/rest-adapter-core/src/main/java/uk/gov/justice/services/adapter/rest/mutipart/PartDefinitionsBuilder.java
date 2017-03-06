package uk.gov.justice.services.adapter.rest.mutipart;

import java.util.ArrayList;
import java.util.List;

public class PartDefinitionsBuilder {

    private final List<PartDefinition> partDefinitions = new ArrayList<>();

    public void add(final int partIndex, final String fieldName) {
        partDefinitions.add(new PartDefinition(partIndex, fieldName));
    }

    public List<PartDefinition> toList() {
        return partDefinitions;
    }
}
