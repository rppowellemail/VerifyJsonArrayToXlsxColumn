
@Grapes([
    // https://mvnrepository.com/artifact/org.apache.poi/poi
    @Grab(group='org.apache.poi', module='poi', version='4.1.0'),
    // https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml
    @Grab(group='org.apache.poi', module='poi-ooxml', version='4.1.0')
])

import static groovy.json.JsonOutput.*

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

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
}

if (args.size() < 1) {
    println "Need arguement"
    System.exit(0);
}
InputStream inp = new FileInputStream(args[0])
Workbook wb = WorkbookFactory.create(inp)
Sheet sheet = wb.getSheetAt(4)

MyXlsxParser myXlsxParser = new MyXlsxParser()

def d = myXlsxParser.parseRows(sheet)

println prettyPrint(toJson(d))
