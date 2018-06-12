package metadata.generator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import metadata.util.Config;

public class Excel2Csv {

	private Workbook wb;
	private PrintStream out;
	private DataFormatter formatter;
	private FormulaEvaluator fe;
	private Map<Integer, String> errors;
	private static Excel2Csv excel2csv;

	public static Excel2Csv newInstance() {
		if(excel2csv==null)
		 excel2csv= new Excel2Csv();
		return excel2csv;
	}

	/*
	 * Convierte la primera hoja - Encabezado en primera linea
	 */
	public Map<Integer, String> convertExcelToCsv(String xlsxFile) throws InvalidFormatException, IOException {

		errors = new HashMap<Integer, String>();
		wb = new XSSFWorkbook(new File(xlsxFile));
		fe = wb.getCreationHelper().createFormulaEvaluator();
		formatter = new DataFormatter();
		out = new PrintStream(new FileOutputStream(xlsxFile.replace(".xlsx", ".csv")), true, Config.CHARSET);

		// Byte-Order-Marker to indicate that the file is encoded in UTF-8
		byte[] bom = { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF };
		out.write(bom);

		Sheet sheet = wb.getSheetAt(0);
		if (sheet != null) {
			Row row;
			int cols = 0;
			for (int r = 0, rn = sheet.getLastRowNum(); r <= rn; r++) {
				row = sheet.getRow(r);
				if (row != null) {
					if (r == 0) {
						cols = row.getLastCellNum();
					}
					printRow(row, cols);
					out.println();
				}
			}
		}
		return errors;
	}

	private void printRow(Row row, int cols) {
		for (int c = 0; c < cols; c++) {
			Cell cell = row.getCell(c);

			out.print(factoryPreparate(cell, row.getRowNum()));
			if (c < cols - 1)
				out.print(',');
		}
	}

	private String factoryPreparate(Cell cell, int row) {
		String text = "", type = "";
		if (cell != null) {
			try {
				switch (cell.getCellTypeEnum()) {
				case FORMULA:
					type = "FORMULA";
					text = getFormula(cell, row);
					break;
				case BLANK:
					type = "BLANK";
					text = "";
					break;
				case BOOLEAN:
					type = "BOOLEAN";
					text = getBoolean(cell);
					break;
				case NUMERIC:
					type = "NUMERIC";
					text = getNumeric(cell);
					break;
				case STRING:
					type = "STRING";
					text = getString(cell);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				errors.put(row, type + " " + e.getMessage());
			}
		}
		return text;
	}

	private String getFormula(Cell cell, int row) throws Exception {
		if (fe != null) {
			cell = fe.evaluateInCell(cell);
			return factoryPreparate(cell, row);
		} else {
			throw new Exception("No se ha podido aplicar la formula");
		}
	}

	private String getBoolean(Cell cell) throws Exception {
		String text = formatter.formatCellValue(cell);
		return text;
	}
	
	private String getDate(String date) throws Exception {

System.out.println( "\t" +date);
		return date;
	}
	

	private String getNumeric(Cell cell) throws Exception {
		String text = formatter.formatCellValue(cell);
		text = text.trim();
		if(text.contains("-") || text.contains("/")) {
			return getDate(text);
		}
		text = text.replaceAll("[^\\dA-Za-z,.]", "");
		char[] letras = text.toCharArray();
		int nro_comas = count(letras, ',');
		int nro_ptos = count(letras, '.');
		if (nro_comas > 1)
			text = text.replaceAll(",", "");
		if (nro_ptos > 1)
			text = text.replaceAll(".", "");
		if (nro_comas == nro_ptos && nro_comas == 1) {
			int index_coma = text.indexOf(',');
			int index_pto = text.indexOf('.');
			if (index_pto > index_coma) {
				text = text.replaceAll(",", "");
			} else {
				text = text.replaceAll(".", "");
				text = text.replaceAll(",", ".");
			}
		}
		text = text.replaceAll(",", "");
		try {
			double d = Double.parseDouble(text);
			return text;
		} catch (Exception e) {
			throw new Exception("El valor (" + text + ") no se puede convertir a número.");
		}

	}

	private int count(char[] letras, char letra) {
		int contador = 0;

		for (int j = 0; j < letras.length; j++) {
			if (letras[j] == letra)
				contador++;
		}
		return contador;

	}

	private String getString(Cell cell) throws Exception {
		String text = formatter.formatCellValue(cell);
		return text;
	}

}
