package net.thwy.akadabraba.lib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelGen {
    // If applied to an Item, Matches the name of a field in net.minecraft.data.client.Models
    /* If applied to a Block, it is one of:
       Y_ROTATE
     */
    String value();
}
