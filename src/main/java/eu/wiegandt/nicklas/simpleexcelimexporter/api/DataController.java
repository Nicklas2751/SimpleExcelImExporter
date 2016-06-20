package eu.wiegandt.nicklas.simpleexcelimexporter.api;

import java.util.Collection;

/**
 * A interface which describes data controllers. A data controller is a class
 * which is the source for an export of a data class and the destination for
 * imports of a data class.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 * @param <D>
 *            The data class.
 */
public interface DataController<D extends DataClass>
{
    /**
     * @return The exact type for which this data controller is made.
     */
    Class<D> getDataClass();

    /**
     * @return A collections with data classes filled with the data for an excel
     *         export.
     */
    Collection<D> pullData();

    /**
     * @param aDataClass
     *            A imported data class.
     */
    void pushData(D aDataClass);

}
