package com.rppowell.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.skyscreamer.jsonassert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class BasicTest extends TestCase {

    public final String testWorkbook001Filename = "src/test/resources/TestWorkbook001.xlsx";
    public final String testWorkbook001_TestSheetOne_TestSheetOneColumnTwo_matching_JsonArrayFilename = "src/test/resources/TestWorkbook001_TestSheetOne_TestSheetOneColumnTwo_matching_JsonArray.json";

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public BasicTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( BasicTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testBasicXlsxRead() throws IOException, JSONException {
        List<String> xlsxStrings = XlsxReader.readXlsxColumn(testWorkbook001Filename, 0, 1, 1);

        JSONArray xlsxArray = new JSONArray(xlsxStrings);

        File jsonSourceFile = new File(testWorkbook001_TestSheetOne_TestSheetOneColumnTwo_matching_JsonArrayFilename);
        String jsonSourceString = FileUtils.readFileToString(jsonSourceFile, Charset.defaultCharset());

        JSONArray jsonArray = (JSONArray) JSONParser.parseJSON(jsonSourceString);
        JSONCompareResult result = JSONCompare.compareJSON(xlsxArray, jsonArray, JSONCompareMode.STRICT_ORDER);

        if (result.failed()) {
            System.out.println(result.getMessage());
            System.out.println("getFieldFailures(" + result.getFieldFailures() + ")");
            System.out.println("getFieldMissing(" + result.getFieldMissing() + ")");
            System.out.println("getFieldUnexpected(" + result.getFieldUnexpected() + ")");
        }
    }
}
