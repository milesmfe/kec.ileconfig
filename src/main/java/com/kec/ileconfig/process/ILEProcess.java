package com.kec.ileconfig.process;

public class ILEProcess extends Process {
    private final int countryRegCdIdx = 18; // Country/Region Code
    private final int globalDim1CdIdx = 13; // Global Dimention 1 Code

    @Override
    protected void addEntryNoToSet(String entryNo) {
        controller.addILENoToSet(entryNo);
    }

    @Override
    protected String[][] updateCSVEntryOnGetCSVMap(String[][] input) {
        String[] newHeaders = ConfigMaps.getILEOutputFields(); // Get new headers
        String[][] original = input.clone(); // Clone the original data
        String[][] output = new String[original.length][newHeaders.length];

        // Set the new headers as the first row
        output[0] = newHeaders;

        // Map data from original to output based on header names
        for (int i = 0; i < newHeaders.length; i++) {
            int columnIndex = getColumnIndex(newHeaders[i], original[0]); // Find the index of the header in the original data
            if (columnIndex != -1) { // If the header exists in the original data
                for (int j = 0; j < original.length; j++) {
                    output[j][i] = original[j][columnIndex]; // Map the data to the corresponding column in the output
                }
                
            } else if (i == globalDim1CdIdx) {
                output[0][i] = newHeaders[i];
                for (int j = 1; j < original.length; j++) {
                    String countryCode = original[j][countryRegCdIdx];
                    output[j][i] = "LJ-" + countryCode;
                }
            } // TODO: Add Global Dimention 2 Code change
        }
        return output;
    }

    // Method to get the index of a header in the input array
    private int getColumnIndex(String header, String[] inputHeaders) {
        for (int i = 0; i < inputHeaders.length; i++) {
            if (inputHeaders[i].equals(header)) {
                return i;
            }
        }
        return -1; // Return -1 if header not found
    }

    /**
     * Create a new {@link #ILEProcess} for processing ILE csv files
     * 
     * @param workingDir the location of (path to) all relevant files.
     * 
     * @see Process
     * 
     */
    public ILEProcess(Controller controller, String workingDir, String outputDir, String binDir, String regexDelimiter) {
        super(controller, ConfigMaps.getILEFields(), workingDir, outputDir, binDir, regexDelimiter);
    }
}