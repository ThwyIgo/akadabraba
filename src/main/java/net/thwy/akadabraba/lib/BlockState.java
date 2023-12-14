package net.thwy.akadabraba.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BlockState {
    /* Models for blocks:
       Y_ROTATE
     */
    String model();
}
