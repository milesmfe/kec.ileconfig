package com.kec.ileconfig.process;

public class ILEProcess extends Process {
    private final int entryNoIdx = 0; // Entry No
    private final int countryRegCdIdx = 18; // Country/Region Code
    private final int globalDim1CdIdx = 13; // Global Dimention 1 Code
    private final int entryTypeIdx = 3; // Entry Type
    private final int quantityIdx = 8; // Quantity
    private final int invoicedQuantityIdx = 10; // Invoiced Quantity

    @Override
    protected void addEntryNoToSet(String entryNo) {
        controller.addILENoToSet(entryNo);
    }

    @Override
    protected String[][] generateBuddyArray(String[][] input) {
        // Create a new array with the same dimensions as the input array
        String[][] output = new String[input.length][];
        
        // Iterate over each row of the input array
        for (int i = 0; i < input.length; i++) {
            // Copy each row of the input array to the corresponding row of the output array
            output[i] = new String[input[i].length];
            System.arraycopy(input[i], 0, output[i], 0, input[i].length);
        }
        
        // Perform modifications on the cloned array
        for (int i = 1; i < input.length; i++) {
            String quantity = output[i][quantityIdx];
            String invoicedQuantity = output[i][invoicedQuantityIdx];
    
            Integer entryNo = 0;
            try {
                entryNo = Integer.parseInt(output[i][entryNoIdx]) * -1;
            } catch (NumberFormatException e) {
                addProblemEntry(entryNo.toString());
                controller.log("Error parsing ILE Entry No");
                continue;
            }
    
            String adjustmentType = "Positive";
            float quantityFloat = Float.parseFloat(quantity);
            float invoicedQuantityFloat = Float.parseFloat(invoicedQuantity);
            if (quantityFloat >= 0) {
                adjustmentType = "Negative";
            }        
            output[i][entryTypeIdx] = adjustmentType + "Adjustment";
            output[i][quantityIdx] = String.valueOf(quantityFloat * -1);
            output[i][invoicedQuantityIdx] = String.valueOf(invoicedQuantityFloat * -1);
            output[i][entryNoIdx] = String.valueOf(entryNo);
            addEntryNoToSet(entryNo.toString());
        }
        return output;
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