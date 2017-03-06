package uk.gov.justice.services.adapter.rest.mutipart;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class PartDefinitionsBuilderTest {

    @Test
    public void shouldAddPartDefinitionAndReturnAsListOfPartDefintions() throws Exception {
        final String fieldName = "testField";

        final PartDefinitionsBuilder partDefinitionsBuilder = new PartDefinitionsBuilder();
        partDefinitionsBuilder.add(fieldName);
        final List<String> results = partDefinitionsBuilder.toList();

        assertThat(results.size(), is(1));

        final String result = results.get(0);
        assertThat(result, is(fieldName));
    }

    @Test
    public void shouldAddMultiplePartDefinitionsAndReturnAsListOfPartDefintions() throws Exception {
        final String fieldName1 = "testField 1";
        final String fieldName2 = "testField 2";
        final String fieldName3 = "testField 3";

        final PartDefinitionsBuilder partDefinitionsBuilder = new PartDefinitionsBuilder();

        partDefinitionsBuilder.add(fieldName1);
        partDefinitionsBuilder.add(fieldName2);
        partDefinitionsBuilder.add(fieldName3);

        final List<String> results = partDefinitionsBuilder.toList();

        assertThat(results.size(), is(3));
        assertThat(results.get(0), is(fieldName1));
        assertThat(results.get(1), is(fieldName2));
        assertThat(results.get(2), is(fieldName3));
    }

    @Test
    public void shouldReturnEmptyListIfNoPartDefinitionsAdded() throws Exception {
        assertThat(new PartDefinitionsBuilder().toList().isEmpty(), is(true));
    }
}