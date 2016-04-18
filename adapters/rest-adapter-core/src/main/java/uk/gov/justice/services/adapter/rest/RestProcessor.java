package uk.gov.justice.services.adapter.rest;

import org.slf4j.Logger;
import uk.gov.justice.services.adapter.rest.envelope.RestEnvelopeBuilderFactory;
import uk.gov.justice.services.messaging.JsonEnvelope;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.String.format;
import static javax.json.JsonValue.NULL;
import static javax.ws.rs.core.Response.Status.ACCEPTED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.status;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * In order to minimise the amount of generated code in the JAX-RS implementation classes, this service encapsulates
 * all the logic for building an envelope from the REST request, passing it to a consumer and building a suitable
 * response. This allows testing of this logic independently from the automated generation code.
 */
public class RestProcessor {

    private static final Logger LOGGER = getLogger(RestProcessor.class);

    @Inject
    RestEnvelopeBuilderFactory envelopeBuilderFactory;

    /**
     * Process an incoming REST request by combining the payload, headers and path parameters into an envelope and
     * passing the envelope to the given consumer.
     *
     * @param consumer       a consumer for the envelope
     * @param initialPayload the payload from the REST request
     * @param headers        the headers from the REST request
     * @param params         the parameters from the REST request
     * @return the HTTP response to return to the client
     */
    public Response processAsynchronously(final Consumer<JsonEnvelope> consumer,
                                          final JsonObject initialPayload,
                                          final HttpHeaders headers,
                                          final Map<String, String> params) {

        final JsonEnvelope envelope = envelopeBuilderFactory.builder()
                .withInitialPayload(initialPayload)
                .withHeaders(headers)
                .withParams(params)
                .build();

        consumer.accept(envelope);

        return status(ACCEPTED).build();
    }

    public Response processSynchronously(final Function<JsonEnvelope, JsonEnvelope> function,
                                         final HttpHeaders headers,
                                         final Map<String, String> params) {
        final JsonEnvelope envelope = envelopeBuilderFactory.builder()
                .withHeaders(headers)
                .withParams(params)
                .build();

        final JsonEnvelope result = function.apply(envelope);

        if (result == null) {
            LOGGER.error(format("Dispatcher returned a null envelope for %s", envelope.metadata().name()));
            return status(INTERNAL_SERVER_ERROR).build();
        } else if (result.payload() == NULL) {
            return status(NOT_FOUND).build();
        } else {
            return status(OK).entity(result.payload().toString()).build();
        }
    }
}
