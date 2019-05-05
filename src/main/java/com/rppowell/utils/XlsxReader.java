package com.rppowell.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XlsxReader {
    public static List<String> readXlsxColumn(String filename, int tabIndex, int colIndex, int rowIndex) throws IOException {
        List<String> values = new ArrayList<String>();
        File f = new File(filename);
        FileInputStream ios = new FileInputStream(f);
        XSSFWorkbook workbook = new XSSFWorkbook(ios);
        XSSFSheet sheet = workbook.getSheetAt(tabIndex);

        Row row;
        Cell cell;
        int lastRow = sheet.getLastRowNum();
        for (int y = rowIndex; y <= sheet.getLastRowNum(); y++) {
            row = sheet.getRow(y);
            cell = row.getCell(colIndex);
            if (cell != null) {
                values.add(getStringFromCell(cell));
            } else {
                break;
            }
        }
        System.out.println(values);
        return values;
    }

    public static String getStringFromCell(Cell cell) {
        String cellString = "";
        switch(cell.getCellType()) {
            case STRING:
                cellString = cell.getStringCellValue();
                break;
            default:
                System.out.println("Cell.getCellType("+cell.getCellType()+")");
                System.out.println("Cell("+cell.toString()+")");
                break;
        }
        return cellString;
    }
}
