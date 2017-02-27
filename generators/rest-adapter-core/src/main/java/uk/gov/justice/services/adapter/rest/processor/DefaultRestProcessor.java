package uk.gov.justice.services.adapter.rest.processor;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static uk.gov.justice.services.core.interceptor.InterceptorContext.interceptorContextWithInput;
import static uk.gov.justice.services.messaging.logging.HttpMessageLoggerHelper.toHttpHeaderTrace;
import static uk.gov.justice.services.messaging.logging.LoggerUtils.trace;

import uk.gov.justice.services.adapter.rest.envelope.RestEnvelopeBuilderFactory;
import uk.gov.justice.services.adapter.rest.mutipart.FileBasedInterceptorContextFactory;
import uk.gov.justice.services.adapter.rest.parameter.Parameter;
import uk.gov.justice.services.core.interceptor.InterceptorContext;
import uk.gov.justice.services.adapter.rest.processor.response.ResponseStrategy;
import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.slf4j.Logger;

@ApplicationScoped
public class DefaultRestProcessor implements RestProcessor {

    @Inject
    Logger logger;

    @Inject
    RestEnvelopeBuilderFactory envelopeBuilderFactory;

    @Inject
    FileBasedInterceptorContextFactory fileBasedInterceptorContextFactory;

    @Override
    public Response process(final ResponseStrategy responseStrategy,
                            final Function<InterceptorContext, Optional<JsonEnvelope>> interceptorChain,
                            final String action,
                            final HttpHeaders headers,
                            final Collection<Parameter> params) {
        return process(responseStrategy, interceptorChain, action, empty(), headers, params, empty());
    }

    @Override
    public Response process(final ResponseStrategy responseStrategy,
                            final Function<InterceptorContext, Optional<JsonEnvelope>> interceptorChain,
                            final String action,
                            final Optional<JsonObject> initialPayload,
                            final HttpHeaders headers,
                            final Collection<Parameter> params) {

        return process(responseStrategy, interceptorChain, action, initialPayload, headers, params, empty());
    }

    @Override
    public Response process(final ResponseStrategy responseStrategy,
                            final Function<InterceptorContext, Optional<JsonEnvelope>> interceptorChain,
                            final String action,
                            final HttpHeaders headers,
                            final Collection<Parameter> params,
                            final MultipartInput multipartInput) {

        return process(responseStrategy, interceptorChain, action, empty(), headers, params, Optional.of(multipartInput));

    }

    private Response process(final ResponseStrategy responseStrategy,
                             final Function<InterceptorContext, Optional<JsonEnvelope>> interceptorChain,
                             final String action,
                             final Optional<JsonObject> initialPayload,
                             final HttpHeaders headers,
                             final Collection<Parameter> params,
                             final Optional<MultipartInput> multipartInput) {

        trace(logger, () -> format("Processing REST message: %s", toHttpHeaderTrace(headers)));

        final JsonEnvelope envelope = envelopeBuilderFactory.builder()
                .withInitialPayload(initialPayload)
                .withAction(action)
                .withHeaders(headers)
                .withParams(params)
                .build();

        trace(logger, () -> format("REST message converted to envelope: %s", envelope));

        final InterceptorContext interceptorContext = multipartInput
                .map(value -> fileBasedInterceptorContextFactory.create(value, envelope))
                .orElseGet(() -> interceptorContextWithInput(envelope));

        final Optional<JsonEnvelope> result = interceptorChain.apply(interceptorContext);

        trace(logger, () -> format("REST message processed: %s", envelope));

        return responseStrategy.responseFor(action, result);
    }
}
