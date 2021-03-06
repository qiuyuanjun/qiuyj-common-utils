package com.qiuyj.excel;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Objects;

/**
 * excel操作的辅助工具类
 * @author qiuyj
 * @since 2018/1/1
 */
public abstract class ExcelUtils {
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#");

  /**
   * 将当前Cell的内容按照字符串读取
   */
  public static String readExcelCellValueAsString(Cell cell) {
    String strVal;
    switch(cell.getCellTypeEnum()) {
      case STRING:
      case BLANK:
        strVal = cell.getStringCellValue();
        break;
      case NUMERIC:
        if (HSSFDateUtil.isCellDateFormatted(cell)) {
          // 表明是时间格式
          Date date = cell.getDateCellValue();
          strVal = String.valueOf(date.getTime());
        }
        else {
//          double val = cell.getNumericCellValue();
//          int intVal = (int) val;
//          if (val - intVal < Double.MIN_VALUE) {
//            strVal = Integer.toString(intVal);
//          }
//          else {
//            strVal = Double.toString(val);
//          }
          strVal = DECIMAL_FORMAT.format(cell.getNumericCellValue());
        }
        break;
      case BOOLEAN:
        strVal = cell.getBooleanCellValue() ? Boolean.TRUE.toString() : Boolean.FALSE.toString();
        break;
      case FORMULA:
        strVal = cell.getCellFormula();
        break;
      case ERROR:
      default:
          throw new IllegalStateException("Error getting excel cell value");
    }
    return strVal;
  }

  /**
   * 读取当前cell的实际类型的值
   * @param cell 要读取的cell
   * @return 对应的实际类型的值
   */
  public static Object getExcelCellValue(Cell cell) {
    Object value;
    switch (cell.getCellTypeEnum()) {
      case STRING:
        value = cell.getStringCellValue();
        break;
      case NUMERIC:
        if (HSSFDateUtil.isCellDateFormatted(cell)) {
          value = cell.getDateCellValue();
        }
        else {
          double val = cell.getNumericCellValue();
          int intVal = (int) val;
          value = val - intVal < Double.MIN_VALUE ? intVal : val;
        }
        break;
      case BOOLEAN:
        value = cell.getBooleanCellValue() ? Boolean.TRUE : Boolean.FALSE;
        break;
      case FORMULA:
        value = cell.getCellFormula();
        break;
      case ERROR:
      default:
        throw new IllegalStateException("Error getting excel cell value");
    }
    return value;
  }

  /**
   * 判断给定excel的一页sheet是否是空的
   */
  public static boolean isEmptySheet(Sheet sheet) {
    return sheet.getPhysicalNumberOfRows() == 0 && sheet.getLastRowNum() == 0;
  }

  public static void closeExcelWorkbookQuietly(Workbook workbook) {
    if (Objects.nonNull(workbook)) {
      try {
        workbook.close();
      }
      catch (IOException e) {
        // ignore
      }
    }
  }

}