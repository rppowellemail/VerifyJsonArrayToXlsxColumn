#!/usr/bin/env groovy

"""
Usage:

groovy VerifyJsonArrayToXlsxColumn.groovy  -x src/test/resources/TestWorkbook001.xlsx -t TestSheetOne -c TestSheetOneColumnOne

"""

@Grab("org.apache.poi:poi-ooxml:4.1.0")
@Grab("org.skyscreamer:jsonassert:1.5.0")
@Grab("ch.qos.logback:logback-classic:1.2.3")

import static groovy.json.JsonOutput.*
import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import groovy.cli.OptionField
import groovy.cli.picocli.CliBuilder
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.comparator.JSONComparator
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode

Logger log = LoggerFactory.getLogger(this.class)

class XlsxOptions {
    String filename;
    String tab;
    String startrow;
}

class XlsxJsonComparator {
}

class MyXlsxParser {
    def tabIndex = 4;
    def startRow = 4;
    def tableNameColumnIndex = 2;
    def elementNameColumnIndex = 3;
    def mappedValueColumnIndex = 8;

    def parseRow(r, dataset) {
        def tableName = r.getCell(tableNameColumnIndex).getStringCellValue()
        def columnName = r.getCell(elementNameColumnIndex).getStringCellValue()
        def mappedValue = r.getCell(mappedValueColumnIndex).getStringCellValue()
        if (mappedValue == "Y") {
            dataset[tableName] = dataset.get(tableName, []) + [columnName]
        }
    }

    def parseRows(sheet) {
        def startRow = 4;
        def lastRow = sheet.getLastRowNum()
        Row r = null;
        def dataset = [:]
        for(int i = startRow; i < lastRow; i++) {
            r = sheet.getRow(i)
            parseRow(r, dataset)
        }
        return dataset
    }

    def parseXlsx(String filename, String tabname, String startingrow) {
        InputStream inp = new FileInputStream(filename)
        Workbook wb = WorkbookFactory.create(inp)
        Sheet sheet = wb.getSheetAt(4)
        def dataset = parseRows(sheet)
        //println prettyPrint(toJson(dataset))
        return dataset
    }
}

class MyJSONParser {
    def extractJSONKeys(jsonpath, jsonpathbits, jsonobject, parseresults) {
        if (jsonobject instanceof java.util.Map) {
            def keys = jsonobject.keySet()
            keys.each { it -> 
                if (jsonobject[it] instanceof java.util.Map) {
                    extractJSONKeys(jsonpath+"."+it, (jsonpathbits + [it]), jsonobject[it], parseresults)
                } else if (jsonobject[it] instanceof java.util.ArrayList) {
                    jsonobject[it].eachWithIndex { ait, i ->
                        extractJSONKeys(jsonpath+"."+it+"["+i+"]", jsonpathbits + [it], ait, parseresults)
                    }
                } else if (jsonobject[it] instanceof String) {
                    if(jsonobject[it].length() == 0) {
                        println(jsonpath + "." + it + " -- \"\"") 
                    } else {
                        if (parseresults.containsKey(jsonpath)) {
                            parseresults[jsonpath][2].add(it)
                        } else {
                            parseresults[jsonpath] = [jsonpathbits[-2], jsonpathbits[-1], [it]]
                        }
                        //parseresults[jsonpath] = parseresults.getOrDefault(jsonpath, []) + [it]
                        //println(jsonpath + "." + it + " " + ([jsonpathbits[-1], jsonpathbits[-2]].toString()))
                    }
                } else if (jsonobject[it] == null) {
                    println(jsonpath + "." + it + " -- null") 
                } else {
                    println(jsonpath + "." + it + " -- unexpected " + jsonobject[it].getClass()) 
                }
            }
        }
    }
    def extractJSONKeys(o, parseresults) { extractJSONKeys('$', ['$'], o, parseresults) }

    def extractJSONKeys(filename) { 
        File f = new File(filename)
        def jsonSlurper = new JsonSlurper()
        def j = jsonSlurper.parse(f)
        def parseresults = [:]
        extractJSONKeys(j, parseresults)
        //println prettyPrint(toJson(parseresults))
        return parseresults
    }
}

def compareJsonToXlsxDataset(jsonparseresults, xlsxdataset) {
    jsonparseresults.eachWithIndex { json, i ->
        //println(json)
        def v = json.getValue()
        //println(json.getKey() + ":" + v[0]+"."+v[1]+":"+v[2])
        if (xlsxdataset.containsKey(v[1])) {
            println("Found:" + v[1] + " -> " + xlsxdataset[v[1]])
        } else {
            println("Missing:" + v[1] + " :: " + json.getKey())
        }
    }
}

def readDataFromXlsx() {
    return JsonOutput.toJson(["valueOne","valueTwo","valueThree"])
}

def readDataFromJson() {
    //return JsonOutput.toJson(["valueOne","valueTwo","valueThree"])
    return JsonOutput.toJson(["valueOne","valueTwo"])
}

@OptionField(shortName = "h", description = "display usage")
boolean help

@OptionField(shortName = "j", description = "json file to read")
String jsonfilename

@OptionField(shortName = "x", description = "xlsx file to read")
String xlsxfilename

@OptionField(shortName = "t", description = "xlsx tab to read")
String xlsxtabname

@OptionField(shortName = "s", description = "xlsx starting row")
String xlsxstartrow

CliBuilder cli = new CliBuilder(name: "${this.class}")
cli.parseFromInstance(this, args)

if (help) {
    cli.usage()
    System.exit(0)
}

log.info("xlsx://" + xlsxfilename + ":" + xlsxtabname + ":" + xlsxstartrow)
MyXlsxParser myXlsxParser = new MyXlsxParser()
def xlsxData = myXlsxParser.parseXlsx(xlsxfilename, xlsxtabname, xlsxstartrow);

log.info("json://" + jsonfilename)

MyJSONParser myJSONParser = new MyJSONParser()
def jsondata = myJSONParser.extractJSONKeys(jsonfilename)

compareJsonToXlsxDataset(jsondata, xlsxData)

/*
println(JsonOutput.toJson(["valueOne","valueTwo","valueThree"]))
jsonCompareResult = JSONCompare.compareJSON(readDataFromJson(), readDataFromXlsx(), JSONCompareMode.STRICT)
println(jsonCompareResult.toString())
if(jsonCompareResult.failed()) {
    println(jsonCompareResult.getMessage())
    System.exit(1)
} else {
    System.exit(0)
}
*/
