package eu.wiegandt.nicklas.simpleexcelimexporter;

import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterError;
import eu.wiegandt.nicklas.simpleexcelimexporter.exceptions.ExcelImExporterWarning;

/**
 * A observer to receive the errors, warnings and progress updates during the
 * im-/export.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br>
 *         <b>Skype:</b> Nicklas2751<br>
 *
 */
public interface ExcelImExportObserver
{
    /**
     * Will be called if a error occurred during im-/export.
     *
     * @param aError
     *            The error which occurred.
     */
    public void newError(ExcelImExporterError aError);

    /**
     * Will be called if the im-/export has some warning for the user like a
     * column is unknown and will be skipped.
     *
     * @param aWarning
     *            The warning for the user.
     */
    public void newWarning(ExcelImExporterWarning aWarning);

    /**
     * Will be called if a new progress is reached.
     *
     * @param aPercentage
     *            The progress in percentage.
     */
    public void updateProgress(float aPercentage);
}
