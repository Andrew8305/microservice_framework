package uk.gov.justice.services.adapters.rest.generator;

import static java.util.Collections.emptyMap;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.raml.model.ActionType.POST;
import static uk.gov.justice.services.generators.test.utils.builder.HttpActionBuilder.httpAction;
import static uk.gov.justice.services.generators.test.utils.builder.MappingBuilder.mapping;
import static uk.gov.justice.services.generators.test.utils.builder.RamlBuilder.restRamlWithDefaults;
import static uk.gov.justice.services.generators.test.utils.builder.ResourceBuilder.resource;
import static uk.gov.justice.services.generators.test.utils.config.GeneratorConfigUtil.configurationWithBasePackage;
import static uk.gov.justice.services.generators.test.utils.reflection.ReflectionUtil.firstMethodOf;

import uk.gov.justice.services.adapter.rest.processor.response.ResponseStrategy;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Function;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.junit.Test;
import org.mockito.Mock;


public class RestAdapterGenerator_MultipartMethodBodyTest extends BaseRestAdapterGeneratorTest {

    @Mock
    private MultipartInput multipartInput;

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnResponseGeneratedByRestProcessor() throws Exception {
        generator.run(
                restRamlWithDefaults()
                        .with(resource("/some/path")
                                .with(httpAction(POST, MULTIPART_FORM_DATA)
                                        .with(mapping()
                                                .withName("upload")
                                                .withRequestType(MULTIPART_FORM_DATA)))
                        ).build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, emptyMap()));

        final Class<?> resourceClass = compiler.compiledClassOf(BASE_PACKAGE, "resource", "DefaultSomePathResource");
        final Object resourceObject = getInstanceOf(resourceClass);

        final Response processorResponse = Response.ok().build();
        when(restProcessor.process(any(ResponseStrategy.class), any(Function.class), anyString(), any(HttpHeaders.class),
                any(Collection.class), eq(multipartInput))).thenReturn(processorResponse);

        final Method method = firstMethodOf(resourceClass);

        final Object result = method.invoke(resourceObject, multipartInput);

        assertThat(result, is(processorResponse));
    }
}
