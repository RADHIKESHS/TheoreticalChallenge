package com.prospecta.Theoretical_Challenge.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.prospecta.Theoretical_Challenge.exceptions.CsvProcessingException;
import com.prospecta.Theoretical_Challenge.service.CsvFormulaEvaluatorService;

import javax.script.ScriptException;
import java.io.*;
import java.util.List;


/**
 * REST controller for handling CSV file processing.
 * Allows users to upload a CSV file and receive the processed CSV as a downloadable file.
 */
@RestController
public class CsvController {

    @Autowired
    private CsvFormulaEvaluatorService evaluatorService;

    
    /**
     * Endpoint to process a CSV file. Evaluates formulas and returns the processed CSV file.
     *
     * @param file the uploaded CSV file
     * @return the processed CSV file as a downloadable resource
     * @throws Exception if any error occurs during processing
     */
    @PostMapping("/process-csv")
    public ResponseEntity<InputStreamResource> processCsv(@RequestParam("file") MultipartFile file) throws Exception {
        try {
            // Process the uploaded CSV and save the output to a file
            String outputFilePath = "processed_output.csv";
            List<String[]> processedData = evaluatorService.processCsv(file.getInputStream(), outputFilePath);

            // Create an InputStreamResource to return the processed file as a response
            File processedFile = new File(outputFilePath);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(processedFile));

            // Set headers and return the file as a response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + processedFile.getName())
                    .contentType(MediaType.parseMediaType("application/csv"))
                    .contentLength(processedFile.length())
                    .body(resource);
        } catch (IOException | ScriptException e) {
            // Handle any errors during CSV processing
            throw new CsvProcessingException("Error processing CSV: " + e.getMessage());
        }
    }
}
