package com.saurmo.metadata;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import metadata.generator.CsvValidator;
import metadata.generator.Excel2Csv;
import metadata.util.Config;
import metadata.util.Log;

/**
 * Hello world!
 *
 */
public class App {
	
	public static Logger log= Log.newInstance();
	
	public static void main(String[] args) {
		

		try {
			
			Excel2Csv();
			//CsvValidator();
			
			
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void Excel2Csv() throws Exception{
		log.info("Init Excel2Csv");			
		Excel2Csv excel2Csv = Excel2Csv.newInstance();
		Map<Integer, String> errors=excel2Csv.convertExcelToCsv(Config.pathConfig + "test_pesos.xlsx");			
		for(Map.Entry<Integer, String> entry : errors.entrySet()) {
			Integer key = entry.getKey();
			String value = entry.getValue();				
		    System.out.println(key + " - " + value);
		}			
		log.info("Convert Complete");
	}
	
	private static void CsvValidator() throws Exception{
		log.info("Init CsvValidator");			
		
		CsvValidator csvValidator = CsvValidator.newInstance();
		Map<String, String> columnsNameType = new HashMap<String, String>();
		columnsNameType.put("Saldo deuda Ideal", "NUMERIC");
		columnsNameType.put("Saldo deuda conversion", "NUMERIC");
		
		Map<Integer, String> errors=csvValidator.validate(Config.pathConfig + "test_pesos.csv", columnsNameType);
		
		for(Map.Entry<Integer, String> entry : errors.entrySet()) {
			Integer key = entry.getKey();
			String value = entry.getValue();				
		    System.out.println(key + " - " + value);
		}			
		log.info("Validation Complete");
	}
}
