package eu.wiegandt.nicklas.simpleexcelimexporter;

import eu.wiegandt.nicklas.simpleexcelimexporter.annotations.ExcelField;
import eu.wiegandt.nicklas.simpleexcelimexporter.annotations.ExcelTable;
import eu.wiegandt.nicklas.simpleexcelimexporter.api.DataClass;

@ExcelTable(mappingFile = "testMapping.json", datacontroller = TestDataController.class)
public class TestDataClass implements DataClass
{

    @ExcelField(exportable = true)
    private String testFieldExport;

    @ExcelField(exportable = true, importable = true, requiredField = true)
    private String testFieldImExport;

    @ExcelField(importable = true)
    private String testFieldImport;

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

    @Override
    public String toString()
    {
        return "TestDataClass [testFieldImport=" + testFieldImport + ", testFieldExport=" + testFieldExport
                + ", testFieldImExport=" + testFieldImExport + "]";
    }

}
