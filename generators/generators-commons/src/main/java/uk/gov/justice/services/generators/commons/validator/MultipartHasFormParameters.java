package uk.gov.justice.services.generators.commons.validator;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;

import java.util.List;
import java.util.Map;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.ParamType;
import org.raml.model.Resource;
import org.raml.model.parameter.FormParameter;

public class MultipartHasFormParameters extends AbstractResourceRamlValidator {

    @Override
    protected void validate(final Resource resource) {
        final Map<ActionType, Action> actions = resource.getActions();

        if (!actions.isEmpty()) {
            actions.values().forEach(action ->
                    action.getBody().values().forEach(mimeType -> {
                        final Map<String, List<FormParameter>> formParameters = mimeType.getFormParameters();

                        if (null == formParameters || formParameters.isEmpty()) {
                            throw new RamlValidationException("Multipart form must contain form parameters");
                        }

                        formParameters.forEach((key, values) -> {

                            try {
                                parseInt(key);
                            } catch (final NumberFormatException e) {
                                throw new RamlValidationException("Multipart form parameter index should be a number identifying the part index of the multipart form");
                            }

                            if (values.isEmpty() || null == values.get(0).getDisplayName()) {
                                throw new RamlValidationException("Multipart form parameter requires a displayName to identify the field name of the file reference");
                            }

                            final FormParameter formParameter = values.get(0);

                            if (!ParamType.FILE.equals(formParameter.getType())) {
                                throw new RamlValidationException(format("Multipart form parameter is expected to be of type FILE, instead was %s", values.get(0).getType()));
                            }

                            if (!formParameter.isRequired()) {
                                throw new RamlValidationException("Multipart form parameter should be required not optional");
                            }

                        });
                    })
            );
        }
    }
}
