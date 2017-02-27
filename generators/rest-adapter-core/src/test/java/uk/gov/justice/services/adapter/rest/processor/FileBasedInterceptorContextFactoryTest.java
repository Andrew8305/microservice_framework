package uk.gov.justice.services.adapter.rest.processor;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.adapter.rest.mutipart.FileInputDetails.FILE_INPUT_DETAILS_LIST;

import uk.gov.justice.services.adapter.rest.mutipart.FileInputDetails;
import uk.gov.justice.services.adapter.rest.mutipart.FileBasedInterceptorContextFactory;
import uk.gov.justice.services.adapter.rest.mutipart.FileInputDetailsFactory;
import uk.gov.justice.services.adapter.rest.mutipart.MultipartInputParser;
import uk.gov.justice.services.adapter.rest.mutipart.PartDefinition;
import uk.gov.justice.services.adapter.rest.mutipart.PartDefinitionParser;
import uk.gov.justice.services.core.interceptor.InterceptorContext;
import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.List;
import java.util.Optional;

import javax.json.JsonObject;

import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class FileBasedInterceptorContextFactoryTest {

    @Mock
    private MultipartInputParser multipartInputParser;

    @Mock
    private PartDefinitionParser partDefinitionParser;

    @Mock
    private FileInputDetailsFactory fileInputDetailsFactory;

    @InjectMocks
    private FileBasedInterceptorContextFactory fileBasedInterceptorContextFactory;

    @Test
    @SuppressWarnings("unchecked")
    public void shouldCreateAnInterceptorContextContainingTheFileDetailsList() throws Exception {

        final MultipartInput multipartInput = mock(MultipartInput.class);
        final JsonEnvelope inputEnvelope = mock(JsonEnvelope.class);

        final JsonObject partMetadata = mock(JsonObject.class);
        final PartDefinition partDefinition_1 = mock(PartDefinition.class);
        final PartDefinition partDefinition_2 = mock(PartDefinition.class);

        final FileInputDetails fileInputDetails_1 = mock(FileInputDetails.class);
        final FileInputDetails fileInputDetails_2 = mock(FileInputDetails.class);

        final List<PartDefinition> partDefinitions = asList(partDefinition_1, partDefinition_2);

        when(multipartInputParser.getPartMetadata(multipartInput)).thenReturn(partMetadata);
        when(partDefinitionParser.getPartDefinitions(partMetadata)).thenReturn(partDefinitions);
        when(fileInputDetailsFactory.createFileInputDetailsFrom(multipartInput, partDefinition_1)).thenReturn(fileInputDetails_1);
        when(fileInputDetailsFactory.createFileInputDetailsFrom(multipartInput, partDefinition_2)).thenReturn(fileInputDetails_2);

        final InterceptorContext interceptorContext = fileBasedInterceptorContextFactory.create(multipartInput, inputEnvelope);

        assertThat(interceptorContext.inputEnvelope(), is(inputEnvelope));
        final Optional<Object> inputParameter = interceptorContext.getInputParameter(FILE_INPUT_DETAILS_LIST);

        assertThat(inputParameter.isPresent(), is(true));

        final List<FileInputDetails> fileInputDetailsList = (List<FileInputDetails>) inputParameter.get();

        assertThat(fileInputDetailsList.size(), is(2));
        assertThat(fileInputDetailsList, hasItem(fileInputDetails_1));
        assertThat(fileInputDetailsList, hasItem(fileInputDetails_2));
    }


}
