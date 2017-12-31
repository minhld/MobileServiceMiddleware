package com.usu.servicemiddleware.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * holds description of our service
 *  
 * @author minhld
 *
 */
@Target(ElementType.TYPE)				// applied in class definition
@Retention(RetentionPolicy.SOURCE)		// available during compilation
public @interface MobileService {
	
	// service version
	String version() default "1.1";
	
	// network model: could be either Pair-Pair, Client-Server, 
	// Publish-Subscribe or Push-Pull, default value is Client-Server
	CommModel commModel() default CommModel.ClientServer;
	
	// data transmission type: could be either Binary or JSON
	// default value is Binary
	TransmitType transmitType() default TransmitType.Binary;
	
	// host & port OR an unique name (TBD) 
	String host() default "*";
}
