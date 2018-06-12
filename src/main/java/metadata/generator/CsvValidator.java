package metadata.generator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class CsvValidator {

	private Map<Integer, String> errors;
	private static CsvValidator csvValidator;

	public static CsvValidator newInstance() {
		if(csvValidator==null)
			csvValidator= new CsvValidator();
		return csvValidator;
	}

	public Map<Integer, String> validate(String csvFile, Map<String, String> columnsNameType)
			throws FileNotFoundException, IOException {
		
		errors = new HashMap<Integer, String>();
		Map<Integer, String> columnsNroTypes = null;
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try {
			br = new BufferedReader(new FileReader(csvFile));
			int row = 0;
			String[] headers=null;
			while ((line = br.readLine()) != null) {
				String[] lineVector = line.split(cvsSplitBy);
				if (row != 0) {
					if (columnsNroTypes != null)
						validateLine(lineVector, row, columnsNroTypes, headers);
					else
						errors.put(row, "Error al obtener el indice de la columna de los encabezados");
				} else {
					headers = new String[lineVector.length];
					headers= lineVector;
					columnsNroTypes = getNroColums(columnsNameType, lineVector);
				}
				row++;
			}
		} finally {
			if (br != null) {
				br.close();
			}
		}
		return errors;
	}

	private Map<Integer, String> getNroColums(Map<String, String> columnsNameType, String[] headers) {
		Map<Integer, String> columnsNroTypes = new HashMap<Integer, String>();
		for (Map.Entry<String, String> entry : columnsNameType.entrySet()) {
			String nameColumn = entry.getKey();
			String typeColumn = entry.getValue();

			for (int i = 0; i < headers.length; i++) {
				String header = headers[i];
				if (header.equals(nameColumn)) {
					columnsNroTypes.put(i, typeColumn);
				}
			}
		}

		return columnsNroTypes;
	}

	private void validateLine(String[] line, int row, Map<Integer, String> columnsType, String[] headers) {
		for (Map.Entry<Integer, String> entry : columnsType.entrySet()) {
			int nroColumn = entry.getKey();
			String typeColumn = entry.getValue();
			row++;
			
			try {
				String header = headers[nroColumn];
				
				if (nroColumn < line.length) {
					
					String data = line[nroColumn];
					if (typeColumn.equals("NUMERIC")) {
						try {
							if (data.isEmpty()) {
								errors.put(row, "El dato de la fila (" + row + ") y la columna (" + header
										+ ") esta vacio, por defecto será  0.");
								data = "0";
							}
							Double.parseDouble(data);
						} catch (Exception e) {
							errors.put(row, "El dato de la fila (" + row + ") y la columna (" + header
									+ ") debe ser númerico.");
						}
					}
				} else {
					errors.put(row, "La fila (" + row + ") no contiene la columna nro: " + header);
				}
			} catch (Exception e) {
				e.printStackTrace();
				errors.put(row, "Error al obtener el dato de la columna " + nroColumn);
			}
		}

	}

}
