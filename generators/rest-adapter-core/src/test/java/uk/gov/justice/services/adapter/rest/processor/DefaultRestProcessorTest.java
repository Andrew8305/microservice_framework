package uk.gov.justice.services.adapter.rest.processor;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopeMatcher.jsonEnvelope;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopeMetadataMatcher.metadata;
import static uk.gov.justice.services.test.utils.core.matchers.JsonEnvelopePayloadMatcher.payloadIsJson;

import uk.gov.justice.services.adapter.rest.envelope.RestEnvelopeBuilderFactory;
import uk.gov.justice.services.adapter.rest.parameter.Parameter;
import uk.gov.justice.services.adapter.rest.processor.response.ResponseStrategy;
import uk.gov.justice.services.common.http.HeaderConstants;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.rest.ParameterType;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class DefaultRestProcessorTest {

    private static final String ACTION = "actionABC";
    private static final String USER_ID = "userId";
    private static final String PAYLOAD_ID = "payloadIdValue";
    private static final String PARAM_VALUE = "nameValue";

    @Mock
    private Function<JsonEnvelope, Optional<JsonEnvelope>> interceptorChain;

    @Mock
    private JsonEnvelope jsonEnvelope;

    @Mock
    private Response response;

    @Mock
    private ResponseStrategy responseStrategy;

    @Mock
    private Logger logger;

    @Spy
    private RestEnvelopeBuilderFactory restEnvelopeBuilderFactory;

    @InjectMocks
    private DefaultRestProcessor restProcessor;

    private ResteasyHttpHeaders headers;
    private List<Parameter> pathParams;

    @Before
    public void setup() {
        final MultivaluedMapImpl<String, String> requestHeaders = new MultivaluedMapImpl<>();
        requestHeaders.add(HeaderConstants.USER_ID, USER_ID);
        headers = new ResteasyHttpHeaders(requestHeaders);

        pathParams = singletonList(Parameter.valueOf("name", PARAM_VALUE, ParameterType.STRING));
    }

    @Test
    public void shouldPassEnvelopeWithPayloadToInterceptorChain() throws Exception {
        final JsonObject payload = Json.createObjectBuilder().add("payloadId", PAYLOAD_ID).build();

        restProcessor.process(responseStrategy, interceptorChain, ACTION, Optional.of(payload), headers, pathParams);

        final ArgumentCaptor<JsonEnvelope> envelopeCaptor = ArgumentCaptor.forClass(JsonEnvelope.class);
        verify(interceptorChain).apply(envelopeCaptor.capture());

        final JsonEnvelope envelope = envelopeCaptor.getValue();
        assertThat(envelope, jsonEnvelope()
                .withMetadataOf(metadata()
                        .withName(ACTION)
                        .withUserId(USER_ID))
                .withPayloadOf(payloadIsJson(allOf(
                        withJsonPath("$.payloadId", equalTo(PAYLOAD_ID)),
                        withJsonPath("$.name", equalTo(PARAM_VALUE))
                ))));
    }

    @Test
    public void shouldPassEnvelopeWithEmptyPayloadToInterceptorChain() throws Exception {
        restProcessor.process(responseStrategy, interceptorChain, ACTION, headers, pathParams);

        final ArgumentCaptor<JsonEnvelope> envelopeCaptor = ArgumentCaptor.forClass(JsonEnvelope.class);
        verify(interceptorChain).apply(envelopeCaptor.capture());

        final JsonEnvelope envelope = envelopeCaptor.getValue();

        assertThat(envelope, jsonEnvelope()
                .withMetadataOf(metadata()
                        .withName(ACTION)
                        .withUserId(USER_ID))
                .withPayloadOf(payloadIsJson(
                        withJsonPath("$.name", equalTo(PARAM_VALUE))
                )));
    }

    @Test
    public void shouldReturnResponseFromResponseStrategyForCallWithPayload() throws Exception {
        final JsonObject payload = mock(JsonObject.class);

        when(interceptorChain.apply(any(JsonEnvelope.class))).thenReturn(Optional.of(jsonEnvelope));
        when(responseStrategy.responseFor(ACTION, Optional.of(jsonEnvelope))).thenReturn(response);

        final Response result = restProcessor.process(responseStrategy, interceptorChain, ACTION, Optional.of(payload), headers, pathParams);

        verify(responseStrategy).responseFor(ACTION, Optional.of(jsonEnvelope));
        assertThat(result, equalTo(response));
    }

    @Test
    public void shouldReturnResponseFromResponseStrategyForCallWithoutPayload() throws Exception {
        when(interceptorChain.apply(any(JsonEnvelope.class))).thenReturn(Optional.of(jsonEnvelope));
        when(responseStrategy.responseFor(ACTION, Optional.of(jsonEnvelope))).thenReturn(response);

        final Response result = restProcessor.process(responseStrategy, interceptorChain, ACTION, headers, pathParams);

        verify(responseStrategy).responseFor(ACTION, Optional.of(jsonEnvelope));
        assertThat(result, equalTo(response));
    }
}