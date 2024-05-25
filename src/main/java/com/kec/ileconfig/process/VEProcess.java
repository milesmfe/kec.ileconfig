package com.kec.ileconfig.process;

public class VEProcess extends Process {

    private final int entryNoIdx = 0; // Entry Number
    private final int veILEEntryNoIdx = 10; // Item Ledger Entry Number

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
    protected String[][] onAfterProcessDataPortCSV(String[][] input) {
        int costAmtActualIdx = 24; // Cost Amount (Actual)
        int costAmtExptIdx = 49; // Cost Amount (Expected)
        int costAmtActACYIdx = 33; // Cost Amount (Actual) (ACY)
        int costAmtActExpACYIdx = 51; // Cost Amount (Expected) (ACY)  
        
        String[][] output = new String[input.length][];
        for (int i = 0; i < input.length; i++) {
            output[i] = new String[input[i].length];
            System.arraycopy(input[i], 0, output[i], 0, input[i].length);
        }

        for (int i = 1; i < input.length; i++) {
            output[i][costAmtActACYIdx] =  output[i][costAmtActualIdx];
            output[i][costAmtActExpACYIdx] = output[i][costAmtExptIdx];
        }
        return output;
    }

    @Override
    protected String[][] generateBuddyArray(String[][] input) {
        int salesAmtExptIdx = 48; // Sales Amount (Expected)
        int salesAmtActualIdx = 15; // Sales Amount (Actual)
        int costAmtActualIdx = 24; // Cost Amount (Actual)
        int costAmtExptIdx = 49; // Cost Amount (Expected)
        int ileTypeIDX = 3; // Entry Type
        int valuedQuantityIdx = 11; // Quantity
        int ileQuantityIdx = 12; // ILE Quantity
        int invoicedQuantityIdx = 13; // Invoiced Quantity
        int costAmtActACYIdx = 33; // Cost Amount (Actual) (ACY)
        int costAmtActExpACYIdx = 51; // Cost Amount (Expected) (ACY)
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
            String quantity = output[i][valuedQuantityIdx];
            String invoicedQuantity = output[i][invoicedQuantityIdx];
            String ileQuantity = output[i][ileQuantityIdx];

            Integer veILEEntryNo = 0;
            try {
                veILEEntryNo = Integer.parseInt(output[i][veILEEntryNoIdx]) * -1;
            } catch (NumberFormatException e) {
                controller.log("Error parsing ILE Entry No");
                addProblemEntry(output[i][entryNoIdx]);
                continue;
            }
            Integer entryNo = 0;
            try {
                entryNo = Integer.parseInt(output[i][entryNoIdx]) * -1;
            } catch (NumberFormatException e) {
                controller.log("Error parsing ILE Entry No");
                addProblemEntry(output[i][entryNoIdx]);
                continue;
            }
            String salesAmountActual = output[i][salesAmtActualIdx];
            String salesAmountExpt = output[i][salesAmtExptIdx];
            String costAmountActual = output[i][costAmtActualIdx];
            String costAmountExpt = output[i][costAmtExptIdx];

            if (salesAmountActual != null && salesAmountExpt != null && costAmountActual != null && costAmountExpt != null) {
                if (salesAmountActual.indexOf(".") != salesAmountActual.lastIndexOf(".")) {
                    salesAmountActual = salesAmountActual.substring(0, salesAmountActual.indexOf("."))
                            + salesAmountActual.substring(salesAmountActual.indexOf(".") + 1);
                }
                if (salesAmountExpt.indexOf(".") != salesAmountExpt.lastIndexOf(".")) {
                    salesAmountExpt = salesAmountExpt.substring(0, salesAmountExpt.indexOf("."))
                            + salesAmountExpt.substring(salesAmountExpt.indexOf(".") + 1);
                }
                if (costAmountActual.indexOf(".") != costAmountActual.lastIndexOf(".")) {
                    costAmountActual = costAmountActual.substring(0, costAmountActual.indexOf("."))
                            + costAmountActual.substring(costAmountActual.indexOf(".") + 1);
                }
                if (costAmountExpt.indexOf(".") != costAmountExpt.lastIndexOf(".")) {
                    costAmountExpt = costAmountExpt.substring(0, costAmountExpt.indexOf("."))
                            + costAmountExpt.substring(costAmountExpt.indexOf(".") + 1);
                }
            }
            try {
                String adjustmentType = "Positive";
                float quantityFloat = Float.parseFloat(quantity);
                float invoicedQuantityFloat = Float.parseFloat(invoicedQuantity);
                float ileQuantityFloat = Float.parseFloat(ileQuantity);
                float costAmtActualFloat = Float.parseFloat(output[i][costAmtActualIdx]);
                float costAmtExptFloat = Float.parseFloat(output[i][costAmtExptIdx]);
                float costAmtActACYFloat = Float.parseFloat(output[i][costAmtActACYIdx]);
                float costAmtActExpACYFloat = Float.parseFloat(output[i][costAmtActExpACYIdx]);
                if (quantityFloat >= 0) {
                    adjustmentType = "Negative";
                }
                output[i][costAmtActualIdx] = String.valueOf(costAmtActualFloat * -1);
                output[i][costAmtExptIdx] = String.valueOf(costAmtExptFloat * -1);
                output[i][costAmtActACYIdx] = String.valueOf(costAmtActACYFloat * -1);
                output[i][costAmtActExpACYIdx] = String.valueOf(costAmtActExpACYFloat * -1);
                output[i][ileTypeIDX] = adjustmentType + "Adjustment";
                output[i][valuedQuantityIdx] = String.valueOf(quantityFloat * -1);
                output[i][invoicedQuantityIdx] = String.valueOf(invoicedQuantityFloat * -1);
                output[i][ileQuantityIdx] = String.valueOf(ileQuantityFloat * -1);
                output[i][veILEEntryNoIdx] = String.valueOf(veILEEntryNo);
                output[i][entryNoIdx] = String.valueOf(entryNo);
                addEntryNoToSet(entryNo.toString());
                output[i][salesAmtActualIdx] = "0"; // Zero sales amount
                output[i][salesAmtExptIdx] = "0"; // Zero sales amount
            } catch (NumberFormatException e) {
                controller.log("Error parsing quantity");
                addProblemEntry(output[i][entryNoIdx]);
                continue;
            }
        }
        return output;
    }

    @Override
    protected String[][] updateCSVEntryOnGetCSVMap(String[][] input) {
        int salesAmtExptIdx = 43; // Sales Amount (Expected)
        int salesAmtActualIdx = 15; // Sales Amount (Actual)
        int costAmtActualIdx = 24; // Cost Amount (Actual)
        int costAmtExptIdx = 44; // Cost Amount (Expected)
        int postingDateIdx = 2; // Posting Date
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
        }
        for (int rowIdx = 1; rowIdx < output.length; rowIdx++) {
            String postingDate = output[rowIdx][postingDateIdx];
            Float exchangeFactor = ConfigMaps.getForexRateFor(controller, postingDate);

            if (exchangeFactor == null) {
                controller.log("Error: Exchange rate not found for date " + postingDate);
                addProblemEntry(output[rowIdx][entryNoIdx]);
                continue;
            }

            String salesAmountActual = output[rowIdx][salesAmtActualIdx];
            String salesAmountExpt = output[rowIdx][salesAmtExptIdx];
            String costAmountActual = output[rowIdx][costAmtActualIdx];
            String costAmountExpt = output[rowIdx][costAmtExptIdx];

            if (salesAmountActual != null && salesAmountExpt != null && costAmountActual != null
                    && costAmountExpt != null) {
                if (salesAmountActual.indexOf(".") != salesAmountActual.lastIndexOf(".")) {
                    salesAmountActual = salesAmountActual.substring(0, salesAmountActual.indexOf("."))
                            + salesAmountActual.substring(salesAmountActual.indexOf(".") + 1);
                }
                if (salesAmountExpt.indexOf(".") != salesAmountExpt.lastIndexOf(".")) {
                    salesAmountExpt = salesAmountExpt.substring(0, salesAmountExpt.indexOf("."))
                            + salesAmountExpt.substring(salesAmountExpt.indexOf(".") + 1);
                }
                if (costAmountActual.indexOf(".") != costAmountActual.lastIndexOf(".")) {
                    costAmountActual = costAmountActual.substring(0, costAmountActual.indexOf("."))
                            + costAmountActual.substring(costAmountActual.indexOf(".") + 1);
                }
                if (costAmountExpt.indexOf(".") != costAmountExpt.lastIndexOf(".")) {
                    costAmountExpt = costAmountExpt.substring(0, costAmountExpt.indexOf("."))
                            + costAmountExpt.substring(costAmountExpt.indexOf(".") + 1);
                }

                // Apply forex rate to the sales and cost amounts
                try {
                    salesAmountActual = String.valueOf(Float.valueOf(salesAmountActual) * exchangeFactor);
                    salesAmountExpt = String.valueOf(Float.valueOf(salesAmountExpt) * exchangeFactor);
                    costAmountActual = String.valueOf(Float.valueOf(costAmountActual) * exchangeFactor);
                    costAmountExpt = String.valueOf(Float.valueOf(costAmountExpt) * exchangeFactor);
                } catch (NumberFormatException e) {
                    controller.log("Error: " + e.getMessage());
                    addProblemEntry(output[rowIdx][entryNoIdx]);
                    continue;
                }

                try {
                    // If the actual sales amount is 0 and the expected sales amount is not 0,
                    // update the actual sales amount to the expected sales amount and set the
                    // expected sales amount to 0
                    // controller.log("Updating sales and cost amount for row [" + rowIdx + "]");
                    if (Float.valueOf(salesAmountActual) == 0 && Float.valueOf(salesAmountExpt) != 0) {
                        salesAmountActual = salesAmountExpt;
                        salesAmountExpt = "0";
                    }
                    if (Float.valueOf(costAmountActual) == 0 && Float.valueOf(costAmountExpt) != 0) {
                        costAmountActual = costAmountExpt;
                        costAmountExpt = "0";
                    }
                    // Save the updated values to the output array
                    output[rowIdx][salesAmtActualIdx] = salesAmountActual;
                    output[rowIdx][salesAmtExptIdx] = salesAmountExpt;
                    output[rowIdx][costAmtActualIdx] = costAmountActual;
                    output[rowIdx][costAmtExptIdx] = costAmountExpt;

                } catch (NumberFormatException | NullPointerException e) {
                    controller.log("Error: " + e.getMessage());
                    addProblemEntry(output[rowIdx][entryNoIdx]);
                    continue;
                }
            } else {
                controller.log("Error: Sales/Cost Values empty");
                addProblemEntry(output[rowIdx][entryNoIdx]);
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
