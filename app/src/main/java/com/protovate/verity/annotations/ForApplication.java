package com.protovate.verity.annotations;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Yan on 5/20/15.
 */
@Qualifier
@Retention(RUNTIME)
public @interface ForApplication {

}
