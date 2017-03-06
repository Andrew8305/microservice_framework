package uk.gov.justice.services.adapter.rest.mutipart;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class PartDefinitionsBuilderTest {

    @Test
    public void shouldAddPartDefinitionAndReturnAsListOfPartDefintions() throws Exception {
        final int partIndex = 3;
        final String fieldName = "testField";

        final PartDefinitionsBuilder partDefinitionsBuilder = new PartDefinitionsBuilder();
        partDefinitionsBuilder.add(partIndex, fieldName);
        final List<PartDefinition> result = partDefinitionsBuilder.toList();

        assertThat(result.size(), is(1));

        final PartDefinition partDefinition = result.get(0);
        assertThat(partDefinition.getIndex(), is(partIndex));
        assertThat(partDefinition.getFieldName(), is(fieldName));
    }

    @Test
    public void shouldAddMultiplePartDefinitionsAndReturnAsListOfPartDefintions() throws Exception {
        final int partIndex1 = 1;
        final String fieldName1 = "testField 1";
        final int partIndex2 = 2;
        final String fieldName2 = "testField 2";
        final int partIndex3 = 3;
        final String fieldName3 = "testField 3";

        final PartDefinitionsBuilder partDefinitionsBuilder = new PartDefinitionsBuilder();

        partDefinitionsBuilder.add(partIndex1, fieldName1);
        partDefinitionsBuilder.add(partIndex2, fieldName2);
        partDefinitionsBuilder.add(partIndex3, fieldName3);

        final List<PartDefinition> result = partDefinitionsBuilder.toList();

        assertThat(result.size(), is(3));

        assertThat(result.get(0).getIndex(), is(partIndex1));
        assertThat(result.get(0).getFieldName(), is(fieldName1));

        assertThat(result.get(1).getIndex(), is(partIndex2));
        assertThat(result.get(1).getFieldName(), is(fieldName2));

        assertThat(result.get(2).getIndex(), is(partIndex3));
        assertThat(result.get(2).getFieldName(), is(fieldName3));
    }

    @Test
    public void shouldReturnEmptyListIfNoPartDefinitionsAdded() throws Exception {
        assertThat(new PartDefinitionsBuilder().toList().isEmpty(), is(true));
    }
}