#!/usr/bin/env groovy

"""
Usage:

groovy VerifyJsonArrayToXlsxColumn.groovy  -x src/test/resources/TestWorkbook001.xlsx -t TestSheetOne -c TestSheetOneColumnOne

"""

@Grab("com.codoid.products:fillo:1.18")
@Grab("org.skyscreamer:jsonassert:1.5.0")
@Grab("ch.qos.logback:logback-classic:1.2.3")

import groovy.json.JsonOutput
import groovy.cli.OptionField
import groovy.cli.picocli.CliBuilder
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.comparator.JSONComparator
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.codoid.products.exception.FilloException;
import com.codoid.products.fillo.Connection;
import com.codoid.products.fillo.Fillo;
import com.codoid.products.fillo.Recordset;

import org.skyscreamer.jsonassert.JSONCompare
import org.skyscreamer.jsonassert.JSONCompareMode

Logger log = LoggerFactory.getLogger(this.class)

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

@OptionField(shortName = "c", description = "xlsx column to read")
String xlsxcolumnname

CliBuilder cli = new CliBuilder(name: "${this.class}")
cli.parseFromInstance(this, args)

if (help) {
    cli.usage()
    System.exit(0)
}

log.info("xlsx://" + xlsxfilename + ":" + xlsxtabname + ":" + xlsxcolumnname)
println(JsonOutput.toJson(["valueOne","valueTwo","valueThree"]))


jsonCompareResult = JSONCompare.compareJSON(readDataFromJson(), readDataFromXlsx(), JSONCompareMode.STRICT)
println(jsonCompareResult.toString())
if(jsonCompareResult.failed()) {
    println(jsonCompareResult.getMessage())
    System.exit(1)
} else {
    System.exit(0)
}
