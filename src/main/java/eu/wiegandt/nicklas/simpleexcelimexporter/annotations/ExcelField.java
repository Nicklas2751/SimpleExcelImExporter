package eu.wiegandt.nicklas.simpleexcelimexporter.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A annotation to mark a field as im-/exportable. It's also possible to mark a
 * importable field as required.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelField
{
    /**
     * @return true if the field is exportable.
     */
    boolean exportable() default false;

    /**
     * Optional parameter to set the name of the getter method.
     *
     * @return The getter method name.
     */
    String getterMethod() default "";

    /**
     * @return true if the field is importable.
     */
    boolean importable() default false;

    /**
     * Required only makes sense for an importable field.
     *
     * @return true if the field is required.
     */
    boolean requiredField() default false;

    /**
     * Optional parameter to set the name of the setter method.
     *
     * @return The setter method name.
     */
    String setterMethod() default "";

}
