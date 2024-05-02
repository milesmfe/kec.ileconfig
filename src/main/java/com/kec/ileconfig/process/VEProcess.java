package com.kec.ileconfig.process;

public class VEProcess extends Process {

    private final int veILEEntryNoIdx = 10; // Item Ledger Entry Number
    private final int salesAmtExptIdx = 43; // Sales Amount (Expected)
    private final int salesAmtActualIdx = 15; // Sales Amount (Actual)
    private final int costAmtActualIdx = 24; // Cost Amount (Actual)
    private final int costAmtExptIdx = 44; // Cost Amount (Expected)

    /**
     * Create a new {@link #VEProcess} for processing VE csv files
     * 
     * @param workingDir the location of (path to) all relevant files.
     * 
     * @see Process
     * 
     */
    public VEProcess(Controller controller, String workingDir, String outputDir, String binDir, String regexDelimiter) {
        super(controller, ConfigMaps.getVEFields(), workingDir, outputDir, binDir, regexDelimiter);
    }

    @Override
    protected void addEntryNoToSet(String entryNo) {
        controller.addVENoToSet(entryNo);
    }

    @Override
    protected String[][] updateCSVEntryOnGetCSVMap(String[][] input) {
        String[] newHeaders = ConfigMaps.getVEOutputFields(); // Get new headers
        String[][] original = updateILEEntryNo(input.clone()); // Clone the original data and update if necessary
        String[][] output = new String[original.length][newHeaders.length];

        // Set the new headers as the first row
        output[0] = newHeaders;

        // Map data from original to output based on header names
        for (int i = 0; i < newHeaders.length; i++) {
            int columnIndex = getColumnIndex(newHeaders[i], input[0]); // Find the index of the header in the original data
            if (columnIndex != -1) { // If the header exists in the original data
                for (int j = 0; j < original.length; j++) {
                    output[j][i] = original[j][columnIndex]; // Map the data to the corresponding column in the output
                }
            }
            for (int rowIdx = 1; rowIdx < output.length; rowIdx++) {
                String salesAmountActual = output[rowIdx][salesAmtActualIdx];
                String salesAmountExpt = output[rowIdx][salesAmtExptIdx];
                String costAmountActual = output[rowIdx][costAmtActualIdx];
                String costAmountExpt = output[rowIdx][costAmtExptIdx];
                if (salesAmountActual != null && salesAmountExpt != null && costAmountActual != null && costAmountExpt != null) {
                    if (salesAmountActual.indexOf(".") != salesAmountActual.lastIndexOf(".")) {
                        salesAmountActual = salesAmountActual.substring(0, salesAmountActual.indexOf(".")) + salesAmountActual.substring(salesAmountActual.indexOf(".") + 1);
                    }
                    if (salesAmountExpt.indexOf(".") != salesAmountExpt.lastIndexOf(".")) {
                        salesAmountExpt = salesAmountExpt.substring(0, salesAmountExpt.indexOf(".")) + salesAmountExpt.substring(salesAmountExpt.indexOf(".") + 1);
                    }
                    if (costAmountActual.indexOf(".") != costAmountActual.lastIndexOf(".")) {
                        costAmountActual = costAmountActual.substring(0, costAmountActual.indexOf(".")) + costAmountActual.substring(costAmountActual.indexOf(".") + 1);
                    }
                    if (costAmountExpt.indexOf(".") != costAmountExpt.lastIndexOf(".")) {
                        costAmountExpt = costAmountExpt.substring(0, costAmountExpt.indexOf(".")) + costAmountExpt.substring(costAmountExpt.indexOf(".") + 1);
                    }
                    try {
                        // If the actual sales amount is 0 and the expected sales amount is not 0, update the actual sales amount to the expected sales amount and set the expected sales amount to 0
                        System.out.println("Updating sales and cost amount for row [" + rowIdx + "]");
                        if (Float.valueOf(salesAmountActual) == 0 && Float.valueOf(salesAmountExpt) != 0) {
                            output[rowIdx][salesAmtActualIdx] = salesAmountExpt;
                            output[rowIdx][salesAmtExptIdx] = "0";
                        }
                        if (Float.valueOf(costAmountActual) == 0 && Float.valueOf(costAmountExpt) != 0) {
                            output[rowIdx][costAmtActualIdx] = costAmountExpt;
                            output[rowIdx][costAmtExptIdx] = "0";
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("\t| Error converting string to float");
                        continue;
                    }
                }
            }
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

    private String[][] updateILEEntryNo(String[][] input) {
        String[][] output = input.clone();
        for (int rowIdx = 1; rowIdx < output.length; rowIdx++) {
            String ileEntryNoValue = output[rowIdx][veILEEntryNoIdx];
            output[rowIdx][veILEEntryNoIdx] = controller.getEntryNo(ileEntryNoValue);
        }
        return output;
    }
}
