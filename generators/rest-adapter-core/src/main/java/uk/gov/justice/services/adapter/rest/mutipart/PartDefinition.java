package uk.gov.justice.services.adapter.rest.mutipart;

public class PartDefinition {

    private final int index;
    private final String fieldName;

    public PartDefinition(final int index, final String fieldName) {
        this.index = index;
        this.fieldName = fieldName;
    }

    public int getIndex() {
        return index;
    }

    public String getFieldName() {
        return fieldName;
    }
}
