package uk.gov.justice.services.adapter.rest.mutipart;

import java.util.ArrayList;
import java.util.List;

public class PartDefinitionsBuilder {

    private final List<String> partDefinitions = new ArrayList<>();

    public void add(final String fieldName) {
        partDefinitions.add(fieldName);
    }

    public List<String> toList() {
        return partDefinitions;
    }
}
