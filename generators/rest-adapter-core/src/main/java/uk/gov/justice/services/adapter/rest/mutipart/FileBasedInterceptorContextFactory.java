package uk.gov.justice.services.adapter.rest.mutipart;

import static java.util.stream.Collectors.toList;
import static uk.gov.justice.services.adapter.rest.mutipart.FileInputDetails.FILE_INPUT_DETAILS_LIST;
import static uk.gov.justice.services.core.interceptor.InterceptorContext.interceptorContextWithInput;

import uk.gov.justice.services.core.interceptor.InterceptorContext;
import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;

import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;

@ApplicationScoped
public class FileBasedInterceptorContextFactory {

    @Inject
    MultipartInputParser multipartInputParser;

    @Inject
    PartDefinitionParser partDefinitionParser;

    @Inject
    FileInputDetailsFactory fileInputDetailsFactory;

    public InterceptorContext create(final MultipartInput multipartInput, final JsonEnvelope envelope) {

        final JsonObject partMetadata = multipartInputParser.getPartMetadata(multipartInput);
        final List<PartDefinition> partDefinitions = partDefinitionParser.getPartDefinitions(partMetadata);

        final List<FileInputDetails> fileInputDetailsList = partDefinitions
                .stream()
                .map(partDefinition -> fileInputDetailsFactory.createFileInputDetailsFrom(multipartInput, partDefinition))
                .collect(toList());

        final InterceptorContext interceptorContext = interceptorContextWithInput(envelope);
        interceptorContext.setInputParameter(FILE_INPUT_DETAILS_LIST, fileInputDetailsList);

        return interceptorContext;
    }
}
