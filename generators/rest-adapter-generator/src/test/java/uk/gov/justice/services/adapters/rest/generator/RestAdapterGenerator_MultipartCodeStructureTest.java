package uk.gov.justice.services.adapters.rest.generator;

import static java.util.Collections.emptyMap;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.raml.model.ActionType.POST;
import static uk.gov.justice.services.generators.test.utils.builder.HttpActionBuilder.httpAction;
import static uk.gov.justice.services.generators.test.utils.builder.MappingBuilder.mapping;
import static uk.gov.justice.services.generators.test.utils.builder.RamlBuilder.restRamlWithDefaults;
import static uk.gov.justice.services.generators.test.utils.builder.ResourceBuilder.resource;
import static uk.gov.justice.services.generators.test.utils.config.GeneratorConfigUtil.configurationWithBasePackage;
import static uk.gov.justice.services.generators.test.utils.reflection.ReflectionUtil.methodsOf;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.junit.Test;

public class RestAdapterGenerator_MultipartCodeStructureTest extends BaseRestAdapterGeneratorTest {

    @Test
    public void shouldGenerateResourceInterfaceWithOnePOSTMethod() throws Exception {
        generator.run(
                restRamlWithDefaults()
                        .with(resource("/some/path")
                                .with(httpAction(POST, MULTIPART_FORM_DATA)
                                        .with(mapping()
                                                .withName("upload")
                                                .withRequestType(MULTIPART_FORM_DATA)))
                        ).build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, emptyMap()));

        final Class<?> interfaceClass = compiler.compiledInterfaceOf(BASE_PACKAGE);

        final List<Method> methods = methodsOf(interfaceClass);
        assertThat(methods, hasSize(1));
        final Method method = methods.get(0);
        assertThat(method.getReturnType(), equalTo(Response.class));
        assertThat(method.getAnnotation(javax.ws.rs.POST.class), not(nullValue()));
        assertThat(method.getAnnotation(Consumes.class), not(nullValue()));
        assertThat(method.getAnnotation(Consumes.class).value(),
                is(new String[]{MULTIPART_FORM_DATA}));
        assertThat(method.getAnnotation(Produces.class), is(nullValue()));
    }

    @Test
    public void shouldGenerateInterfaceThatContainsMethodWithMultipartParameter() throws Exception {
        generator.run(
                restRamlWithDefaults()
                        .with(resource("/some/path")
                                .with(httpAction(POST, MULTIPART_FORM_DATA)
                                        .with(mapping()
                                                .withName("upload")
                                                .withRequestType(MULTIPART_FORM_DATA)))
                        ).build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, emptyMap()));

        final Class<?> interfaceClass = compiler.compiledInterfaceOf(BASE_PACKAGE);

        final List<Method> methods = methodsOf(interfaceClass);
        assertThat(methods, hasSize(1));
        final Method method = methods.get(0);
        assertThat(method.getParameterCount(), is(1));
        final Parameter parameter = method.getParameters()[0];
        assertThat(parameter.getType(), equalTo(MultipartInput.class));
        assertThat(parameter.getAnnotation(MultipartForm.class), not(nullValue()));
    }

    @Test
    public void shouldGenerateInterfaceThatContainsMethodWithTwoPathParamsAndMultipartParameter() throws Exception {
        generator.run(
                restRamlWithDefaults()
                        .with(resource("/some/path")
                                .with(httpAction(POST, MULTIPART_FORM_DATA)
                                        .with(mapping()
                                                .withName("upload")
                                                .withRequestType(MULTIPART_FORM_DATA)))
                                .withRelativeUri("/some/path/{paramA}/abc/{paramB}")
                                .withPathParam("paramA")
                                .withPathParam("paramB")
                        ).build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, emptyMap()));

        final Class<?> interfaceClass = compiler.compiledInterfaceOf(BASE_PACKAGE);

        final List<Method> methods = methodsOf(interfaceClass);
        assertThat(methods, hasSize(1));
        final Method method = methods.get(0);
        assertThat(method.getParameterCount(), is(3));

        final Parameter methodParam1 = method.getParameters()[0];
        assertThat(methodParam1.getType(), equalTo(String.class));
        assertThat(methodParam1.getAnnotations(), arrayWithSize(1));
        assertThat(methodParam1.getAnnotations()[0].annotationType(), equalTo(PathParam.class));
        assertThat(methodParam1.getAnnotation(PathParam.class).value(), is("paramA"));

        final Parameter methodParam2 = method.getParameters()[1];
        assertThat(methodParam2.getType(), equalTo(String.class));
        assertThat(methodParam2.getAnnotations(), arrayWithSize(1));
        assertThat(methodParam2.getAnnotations()[0].annotationType(), equalTo(PathParam.class));
        assertThat(methodParam2.getAnnotation(PathParam.class).value(), is("paramB"));

        final Parameter methodParam3 = method.getParameters()[2];
        assertThat(methodParam3.getType(), equalTo(MultipartInput.class));
        assertThat(methodParam3.getAnnotation(MultipartForm.class), not(nullValue()));
    }

    @Test
    public void shouldGenerateResourceClassImplementingMultipartInterface() throws Exception {
        generator.run(
                restRamlWithDefaults()
                        .with(resource("/some/path")
                                .with(httpAction(POST, MULTIPART_FORM_DATA)
                                        .with(mapping()
                                                .withName("upload")
                                                .withRequestType(MULTIPART_FORM_DATA)))
                        ).build(),
                configurationWithBasePackage(BASE_PACKAGE, outputFolder, emptyMap()));

        final Class<?> resourceInterface = compiler.compiledInterfaceOf(BASE_PACKAGE);
        final Class<?> resourceClass = compiler.compiledClassOf(BASE_PACKAGE, "resource", "DefaultSomePathResource");

        assertThat(resourceClass.isInterface(), is(false));
        assertThat(resourceClass.getGenericInterfaces(), arrayWithSize(1));
        assertThat(resourceClass.getGenericInterfaces()[0].getTypeName(), equalTo(resourceInterface.getTypeName()));
    }
}