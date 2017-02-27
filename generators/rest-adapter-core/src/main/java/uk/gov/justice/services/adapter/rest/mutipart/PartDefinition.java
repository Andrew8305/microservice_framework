package uk.gov.justice.services.adapter.rest.mutipart;

import java.util.Objects;

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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PartDefinition that = (PartDefinition) o;
        return getIndex() == that.getIndex() &&
                Objects.equals(getFieldName(), that.getFieldName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIndex(), getFieldName());
    }
}
