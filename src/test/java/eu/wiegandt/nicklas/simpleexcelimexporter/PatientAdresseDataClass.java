package eu.wiegandt.nicklas.simpleexcelimexporter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.FormatStyle;

import eu.wiegandt.nicklas.simpleexcelimexporter.annotations.ExcelField;
import eu.wiegandt.nicklas.simpleexcelimexporter.annotations.ExcelTable;
import eu.wiegandt.nicklas.simpleexcelimexporter.api.DataClass;

/**
 * Eine Hilfsklasse zum gemeinsamen importieren von Patient und dessen Adresse.
 *
 * @author Nicklas Wiegandt (Nicklas2751)<br/>
 *         <b>Mail:</b> nicklas@wiegandt.eu<br/>
 *         <b>Jabber:</b> nicklas2751@elaon.de<br/>
 *         <b>Skype:</b> Nicklas2751<br/>
 *
 */
@ExcelTable(tableName = "Patient", datacontroller = PatientAdresseDataController.class)
public class PatientAdresseDataClass implements DataClass
{
    private static final String ERROR_TEXT_GEBURTSDATUM_PARSE =
            "A field Geburtsdatum had an invalid date and can't parsed. ";

    private Integer adresseId;

    @ExcelField(exportable = true, importable = true)
    private String anrede;

    @ExcelField(exportable = true, importable = true, getterMethod = "getGeburtsdatumAsText",
            setterMethod = "setGeburtsdatumAsText")
    private LocalDate geburtsdatum;

    @ExcelField(exportable = true, importable = true)
    private String geschaeftNum;

    @ExcelField(exportable = true, importable = true)
    private String mobilNum;

    @ExcelField(exportable = true, importable = true, requiredField = true)
    private String nachname;

    @ExcelField(exportable = true, importable = true)
    private String ort;

    @ExcelField(exportable = true, importable = true)
    private String postleitzahl;

    @ExcelField(exportable = true, importable = true)
    private String strasse;

    @ExcelField(exportable = true, importable = true)
    private String telefonNum;

    @ExcelField(exportable = true, importable = true)
    private String titel;

    @ExcelField(exportable = true, importable = true, requiredField = true)
    private String vorname;

    private DateTimeFormatter dateFormat;

    /**
     * Standard Konstruktor der alle String Felder leer und alle anderen Felder
     * mit null vorbelegt.
     */
    public PatientAdresseDataClass()
    {
        dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        adresseId = null;
        anrede = "";
        geburtsdatum = null;
        geschaeftNum = "";
        mobilNum = "";
        nachname = "";
        ort = "";
        postleitzahl = "";
        strasse = "";
        telefonNum = "";
        titel = "";
        vorname = "";
    }

    public Integer getAdresseId()
    {
        return adresseId;
    }

    public String getAnrede()
    {
        return anrede;
    }

    public LocalDate getGeburtsdatum()
    {
        return geburtsdatum;
    }

    public String getGeburtsdatumAsText()
    {
        return dateFormat.format(getGeburtsdatum());
    }

    public String getGeschaeftNum()
    {
        return geschaeftNum;
    }

    public String getMobilNum()
    {
        return mobilNum;
    }

    public String getNachname()
    {
        return nachname;
    }

    public String getOrt()
    {
        return ort;
    }

    public String getPostleitzahl()
    {
        return postleitzahl;
    }

    public String getStrasse()
    {
        return strasse;
    }

    public String getTelefonNum()
    {
        return telefonNum;
    }

    public String getTitel()
    {
        return titel;
    }

    public String getVorname()
    {
        return vorname;
    }

    public void setAdresseId(final Integer aAdresseId)
    {
        adresseId = aAdresseId;
    }

    public void setAnrede(final String aAnrede)
    {
        anrede = aAnrede;
    }

    public void setGeburtsdatum(final LocalDate aGeburtsdatum)
    {
        geburtsdatum = aGeburtsdatum;
    }

    public void setGeburtsdatumAsText(final String aGeburtsdatumAsText)
    {
        if (aGeburtsdatumAsText == null || aGeburtsdatumAsText.trim().isEmpty())
        {
            setGeburtsdatum(null);
        }
        else
        {
            try
            {
                setGeburtsdatum(dateFormat.parse(aGeburtsdatumAsText, LocalDate::from));
            }
            catch (final DateTimeParseException parseException)
            {
                throw new IllegalArgumentException(ERROR_TEXT_GEBURTSDATUM_PARSE, parseException);
            }
        }
    }

    public void setGeschaeftNum(final String aGeschaeftNum)
    {
        geschaeftNum = aGeschaeftNum;
    }

    public void setMobilNum(final String aMobilNum)
    {
        mobilNum = aMobilNum;
    }

    public void setNachname(final String aNachname)
    {
        nachname = aNachname;
    }

    public void setOrt(final String aOrt)
    {
        ort = aOrt;
    }

    public void setPostleitzahl(final String aPostleitzahl)
    {
        postleitzahl = aPostleitzahl;
    }

    public void setStrasse(final String aStrasse)
    {
        strasse = aStrasse;
    }

    public void setTelefonNum(final String aTelefonNum)
    {
        telefonNum = aTelefonNum;
    }

    public void setTitel(final String aTitel)
    {
        titel = aTitel;
    }

    public void setVorname(final String aVorname)
    {
        vorname = aVorname;
    }

    @Override
    public String toString()
    {
        return "PatientAdresseDataClass [nachname=" + nachname + ", vorname=" + vorname + "]";
    }
}
