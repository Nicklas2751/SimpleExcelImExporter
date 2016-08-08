package eu.wiegandt.nicklas.simpleexcelimexporter;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.wiegandt.nicklas.simpleexcelimexporter.api.DataController;

/**
 * Der DataController für den {@link Patient} und dessen {@link Adresse}.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br/>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br/>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br/>
 *         <b>Skype:</b> Nicklas2751<br/>
 *
 */
public class PatientAdresseDataController implements DataController<PatientAdresseDataClass>
{
    private static final Logger LOG = LogManager.getLogger(PatientAdresseDataController.class);
    private static Collection<PatientAdresseDataClass> pushedData;

    static
    {
        pushedData = new ArrayList<>();
    }

    @Override
    public Class<PatientAdresseDataClass> getDataClass()
    {
        return PatientAdresseDataClass.class;
    }

    public static Collection<PatientAdresseDataClass> getPushedData()
    {
        return pushedData;
    }

    @Override
    public Collection<PatientAdresseDataClass> pullData()
    {
        return new ArrayList<>();
    }

    @Override
    public void pushData(final PatientAdresseDataClass aDataClass)
    {
        pushedData.add(aDataClass);
        LOG.info("Imported: " + aDataClass.toString());
    }
}
