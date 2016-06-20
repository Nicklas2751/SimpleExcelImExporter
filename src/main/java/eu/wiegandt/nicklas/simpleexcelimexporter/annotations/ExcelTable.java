package eu.wiegandt.nicklas.simpleexcelimexporter.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import eu.wiegandt.nicklas.simpleexcelimexporter.api.DataController;

/**
 * A annotation to mark a class as a data class for excel im- and export.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelTable
{
    /**
     * @return The {@link DataController} for this class.
     */
    Class<? extends DataController<?>> datacontroller();

    /**
     * An optional path to a mapping file for the class which contains this
     * field.
     *
     * @return The path to the mapping file for the class which contains this
     *         field.
     */
    String mappingFile() default "";

}
