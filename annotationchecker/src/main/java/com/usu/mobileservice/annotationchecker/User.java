package com.usu.mobileservice.annotationchecker;

import com.usu.servicemiddleware.annotations.MobileService;
import com.usu.servicemiddleware.annotations.ServiceMethod;

/**
 * Created by lee on 9/23/17.
 */
@MobileService
public class User {
    @ServiceMethod
    public String[] getFolderList(String root) {
        return new String[] { "hello", "there" };
    }
}