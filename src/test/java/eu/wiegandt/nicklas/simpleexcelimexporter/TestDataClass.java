package eu.wiegandt.nicklas.simpleexcelimexporter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.junit.Assert;

import eu.wiegandt.nicklas.simpleexcelimexporter.annotations.ExcelField;
import eu.wiegandt.nicklas.simpleexcelimexporter.annotations.ExcelTable;
import eu.wiegandt.nicklas.simpleexcelimexporter.api.DataClass;

@ExcelTable(mappingFile = "testMapping.json", datacontroller = TestDataController.class)
public class TestDataClass implements DataClass
{
    private final DateFormat dateFormat;

    @ExcelField(exportable = true)
    private String testFieldExport;

    @ExcelField(exportable = true, importable = true, requiredField = true)
    private String testFieldImExport;

    @ExcelField(importable = true)
    private String testFieldImport;

    @ExcelField(exportable = true, importable = true, getterMethod = "getTestOptionalGetterSetterAsText",
            setterMethod = "setTestOptionalGetterSetterAsText")
    private Date testOptionalGetterSetter;

    public TestDataClass()
    {
        dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY);
    }

    public String getTestFieldExport()
    {
        return testFieldExport;
    }

    public String getTestFieldImExport()
    {
        return testFieldImExport;
    }

    public String getTestFieldImport()
    {
        return testFieldImport;
    }

    public Date getTestOptionalGetterSetter()
    {
        return testOptionalGetterSetter;
    }

    public String getTestOptionalGetterSetterAsText()
    {

        return dateFormat.format(testOptionalGetterSetter);
    }

    public void setTestFieldExport(final String aTestFieldExport)
    {
        testFieldExport = aTestFieldExport;
    }

    public void setTestFieldImExport(final String aTestFieldImExport)
    {
        testFieldImExport = aTestFieldImExport;
    }

    public void setTestFieldImport(final String aTestFieldImport)
    {
        testFieldImport = aTestFieldImport;
    }

    public void setTestOptionalGetterSetter(final Date aTestOptionalGetterSetter)
    {
        testOptionalGetterSetter = aTestOptionalGetterSetter;
    }

    public void setTestOptionalGetterSetterAsText(final String aTestOptionalGetterSetter)
    {
        try
        {
            testOptionalGetterSetter = dateFormat.parse(aTestOptionalGetterSetter);
        }
        catch (final ParseException parseException)
        {
            Assert.fail(parseException.getLocalizedMessage());
        }
    }

    @Override
    public String toString()
    {
        return "TestDataClass [testFieldImport=" + testFieldImport + ", testFieldExport=" + testFieldExport
                + ", testFieldImExport=" + testFieldImExport + "]";
    }

}
