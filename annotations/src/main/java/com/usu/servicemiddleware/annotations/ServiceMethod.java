package com.usu.servicemiddleware.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@SuppressWarnings("unused")
public @interface ServiceMethod {
	// synchronization mode
	// default is Asynchronous
	SyncMode syncMode() default SyncMode.Async;

}