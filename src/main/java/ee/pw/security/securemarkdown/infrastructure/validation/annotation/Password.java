package ee.pw.security.securemarkdown.infrastructure.validation.annotation;

public @interface Password {
	String value() default "";
}
