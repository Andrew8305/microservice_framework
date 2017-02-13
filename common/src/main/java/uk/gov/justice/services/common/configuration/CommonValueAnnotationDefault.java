package uk.gov.justice.services.common.configuration;


import javax.enterprise.inject.spi.InjectionPoint;

class CommonValueAnnotationDefault {

    private String key;
    private String defaultValue;

    private CommonValueAnnotationDefault(final String key, final String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    static CommonValueAnnotationDefault localValueAnnotationOf(final InjectionPoint ip) {
        final Value annotation = ip.getAnnotated().getAnnotation(Value.class);
        return new CommonValueAnnotationDefault(annotation.key(), annotation.defaultValue());
    }

    static CommonValueAnnotationDefault globalValueAnnotationOf(final InjectionPoint ip) {
        final GlobalValue annotation = ip.getAnnotated().getAnnotation(GlobalValue.class);
        return new CommonValueAnnotationDefault(annotation.key(), annotation.defaultValue());
    }

    String key() {
        return key;
    }

    String defaultValue() {
        return defaultValue;
    }
}