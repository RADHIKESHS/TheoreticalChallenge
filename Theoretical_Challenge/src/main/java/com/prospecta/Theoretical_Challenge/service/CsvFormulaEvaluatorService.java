package com.prospecta.Theoretical_Challenge.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;

import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

/**
 * Service for processing CSV files and evaluating formulas within the cells.
 */
@Service
public class CsvFormulaEvaluatorService {

    // Context for evaluating JavaScript formulas using GraalVM
    private final Context context;
    // Cache for storing evaluated cell values
    private final Map<String, String> evaluatedCells;
    // Set to track cells currently being evaluated to detect circular references
    private final Set<String> evaluatingCells;

    public CsvFormulaEvaluatorService() {
        this.context = Context.create();
        this.evaluatedCells = new HashMap<>();
        this.evaluatingCells = new HashSet<>();
    }
    
    public List<String[]> processCsv(InputStream inputStream, String outputFilePath) throws IOException, Exception {
        // Read CSV into a 2D array
        List<String[]> csvData = readCsv(inputStream);

        // Reset caches
        evaluatedCells.clear();
        evaluatingCells.clear();

        // Evaluate formulas within the CSV data
        evaluateFormulas(csvData);
        
        // Write the processed data to a new CSV file
        writeCsv(csvData, outputFilePath);

        return csvData;
    }


    /**
     * Read the CSV file into a list of string arrays.
     *
     * @param inputStream input stream of the CSV file
     * @return the CSV data as a list of string arrays
     */
    private List<String[]> readCsv(InputStream inputStream) throws IOException, CsvException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            return reader.readAll();
        }
    }

    /**
     * Write the processed CSV data to a file.
     *
     * @param csvData the processed CSV data
     * @param filePath the file path to save the data
     */
    private void writeCsv(List<String[]> csvData, String filePath) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeAll(csvData);
        }
    }
    /**
     * Evaluate formulas within the CSV data.
     * If a cell starts with "=", it is treated as a formula and evaluated.
     *
     * @param csvData the CSV data to process
     */

    private void evaluateFormulas(List<String[]> csvData) throws Exception {
        for (int row = 0; row < csvData.size(); row++) {
            for (int col = 0; col < csvData.get(row).length; col++) {
                String cell = csvData.get(row)[col];
                // Check if the cell contains a formula (starts with "=")
                if (cell.startsWith("=")) {
                    String result = evaluateCell(cell, row, col, csvData);
                    csvData.get(row)[col] = result;
                }
            }
        }
    }

    
    /**
     * Evaluate a single cell's formula.
     *
     * @param formula the formula to evaluate
     * @param row the row index of the cell
     * @param col the column index of the cell
     * @param csvData the entire CSV data
     * @return the evaluated result as a string
     * @throws Exception if any error occurs during evaluation or a circular reference is detected
     */    
    private String evaluateCell(String formula, int row, int col, List<String[]> csvData) throws Exception {
        String cellKey = getCellKey(row, col);

        // Check for circular reference
        if (evaluatingCells.contains(cellKey)) {
            throw new Exception("Circular reference detected at " + cellKey);
        }

        // Return cached value if already evaluated
        if (evaluatedCells.containsKey(cellKey)) {
            return evaluatedCells.get(cellKey);
        }

        evaluatingCells.add(cellKey); // Mark cell as being evaluated

        // Replace cell references (e.g., A1, B2) with actual values
        formula = replaceReferences(formula.substring(1), csvData); // Remove '=' before replacing references

        String resultString;
        try {
            // Evaluate the formula using GraalVM
            Value result = context.eval("js", formula);

            // Handle different types of results
            if (result.isString()) {
                resultString = result.asString();
            } else {
                resultString = result.toString(); // Convert other types to String
            }

        } catch (Exception e) {
            // Handle any exceptions that occur during evaluation
            throw new Exception("Error evaluating formula in cell " + cellKey + ":\n" + e.getMessage(), e);
        } finally {
            evaluatingCells.remove(cellKey); // Remove from evaluating set
        }

        // Cache the evaluated result
        evaluatedCells.put(cellKey, resultString);

        return resultString;
    }

    /**
     * Replace cell references in a formula (e.g., A1, B2) with their actual values from the CSV data.
     *
     * @param formula the formula containing cell references
     * @param csvData the entire CSV data
     * @return the formula with references replaced by actual values
     */
    private String replaceReferences(String formula, List<String[]> csvData) {
        // Regex pattern to match cell references (e.g., A1, B2)
        Pattern pattern = Pattern.compile("([A-Z])(\\d+)");
        Matcher matcher = pattern.matcher(formula);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String colLetter = matcher.group(1);
            String rowNumber = matcher.group(2);

            // Convert column letter to index (A -> 0, B -> 1, etc.)
            int col = colLetter.charAt(0) - 'A';
            int row = Integer.parseInt(rowNumber) - 1;

            // Replace the cell reference with the actual value
            String replacement;
            if (row >= 0 && row < csvData.size() && col >= 0 && col < csvData.get(row).length) {
                replacement = csvData.get(row)[col];
            } else {
                replacement = "0"; // Default value for out-of-bounds references
            }

            // Append the replacement string
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();
    }

    
    /**
     * Generate a unique key for a cell based on its row and column indices.
     *
     * @param row the row index
     * @param col the column index
     * @return the unique key for the cell (e.g., A1, B2)
     */
    private String getCellKey(int row, int col) {
        return (char) ('A' + col) + Integer.toString(row + 1);
    }
}
