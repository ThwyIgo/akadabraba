package net.thwy.akadabraba.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Register {
    String name() default "";

    // Matches the name of a field in net.minecraft.data.client.Model
    // Some additional values are listed below
    String model() default "";
    /* Additional models for blocks:
       Y_ROTATE
     */
}
