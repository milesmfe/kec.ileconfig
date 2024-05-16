package com.kec.ileconfig.process;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class ConfigMaps {

    private static final String[] ILEFields = {
            "Entry No.", // 0
            "Item No.",
            "Posting Date",
            "Entry Type",
            "Source No.",
            "Document No.",
            "Description",
            "Location Code",
            "Quantity",
            "Remaining Quantity",
            "Invoiced Quantity",
            "Applies-to Entry",
            "Open",
            "Global Dimension 1 Code",
            "Global Dimension 2 Code",
            "Positive",
            "Source Type",
            "Drop Shipment",
            "Transaction Nature Code",
            "Transport Method",
            "Country/Region Code",
            "Entry/Exit Point",
            "Document Date",
            "External Document No.",
            "Area",
            "Transaction Specification",
            "No. Series",
            "Reserved Quantity",
            "Document Type",
            "Document Line No.",
            "Job No.",
            "Job Task No.",
            "Job Purchase",
            "Prod. Order No.",
            "Variant Code",
            "Qty. per Unit of Measure",
            "Unit of Measure Code",
            "Derived from Blanket Order",
            "Cross-Reference No.",
            "Originally Ordered No.",
            "Originally Ordered Var. Code",
            "Out-of-Stock Substitution",
            "Item Category Code",
            "Nonstock",
            "Purchasing Code",
            "Product Group Code",
            "Transfer Order No.",
            "Completely Invoiced",
            "Last Invoice Date",
            "Applied Entry to Adjust",
            "Cost Amount (Expected)",
            "Cost Amount (Actual)",
            "Cost Amount (Non-Invtbl.)",
            "Cost Amount (Expected) (ACY)",
            "Cost Amount (Actual) (ACY)",
            "Cost Amount (Non-Invtbl.)(ACY)",
            "Purchase Amount (Expected)",
            "Purchase Amount (Actual)",
            "Sales Amount (Expected)",
            "Sales Amount (Actual)",
            "Correction",
            "Shipped Qty. Not Returned",
            "Prod. Order Line No.",
            "Prod. Order Comp. Line No.",
            "Service Order No.",
            "Serial No.",
            "Lot No.",
            "Warranty Date",
            "Expiration Date",
            "Item Tracking",
            "Return Reason Code",
            "Job Inventory",
            "Job Activity No.",
            "Job Change Order No."
    };

    private static final String[] VEFields = {
            "Entry No.", 
            "Item No.",
            "Posting Date",
            "Item Ledger Entry Type", // 3
            "Source No.",
            "Document No.",
            "Description",
            "Location Code",
            "Inventory Posting Group",
            "Source Posting Group",
            "Item Ledger Entry No.", // 10
            "Valued Quantity", // 11
            "Item Ledger Entry Quantity", // 12
            "Invoiced Quantity", // 13
            "Cost per Unit",
            "Sales Amount (Actual)",
            "Salespers./Purch. Code",
            "Discount Amount",
            "User ID",
            "Source Code",
            "Applies-to Entry",
            "Global Dimension 1 Code",
            "Global Dimension 2 Code",
            "Source Type",
            "Cost Amount (Actual)",
            "Cost Posted to G/L",
            "Reason Code",
            "Drop Shipment",
            "Journal Batch Name",
            "Gen. Bus. Posting Group",
            "Gen. Prod. Posting Group",
            "Document Date",
            "External Document No.",
            "Cost Amount (Actual) (ACY)",
            "Cost Posted to G/L (ACY)",
            "Cost per Unit (ACY)",
            "Document Type",
            "Document Line No.",
            "Expected Cost",
            "Item Charge No.",
            "Valued By Average Cost",
            "Partial Revaluation",
            "Inventoriable",
            "Valuation Date",
            "Entry Type",
            "Variance Type",
            "Purchase Amount (Actual)",
            "Purchase Amount (Expected)",
            "Sales Amount (Expected)",
            "Cost Amount (Expected)",
            "Cost Amount (Non-Invtbl.)",
            "Cost Amount (Expected) (ACY)",
            "Cost Amount (Non-Invtbl.)(ACY)",
            "Expected Cost Posted to G/L",
            "Exp. Cost Posted to G/L (ACY)",
            "Job No.",
            "Job Task No.",
            "Prod. Order No.",
            "Variant Code",
            "Adjustment",
            "Average Cost Exception",
            "Capacity Ledger Entry No.",
            "Type",
            "No.",
            "Prod. Order Line No.",
            "Return Reason Code",
            "Item Category Code",
            "Product Group Code",
            "Surcharge",
            "G/L Account No.",
            "fBlnTransMainEntry",
            "fBlnMainEntry",
            "IC-Unit Cost"
    };

    private static final String[] ILEOutputFields = {
        "Entry No.", // 0
        "Item No.",
        "Posting Date",
        "Entry Type", // 3
        "Source No.",
        "Document No.",
        "Description",
        "Location Code",
        "Quantity", // 8
        "Remaining Quantity", // 9
        "Invoiced Quantity", // 10
        "Applies-to Entry",
        "Open",
        "Global Dimension 1 Code",
        "Global Dimension 2 Code",
        "Positive",
        "Source Type",
        "Drop Shipment",
        // "Transaction Nature Code",
        // "Transport Method",
        "Country/Region Code",
        // "Entry/Exit Point",
        "Document Date",
        // "External Document No.",
        "Area",
        // "Transaction Specification",
        "No. Series",
        "Document Type",
        "Document Line No.",
        // "Order Type",
        // "Order No.",
        // "Order Line No.",
        "Job Purchase",
        // "Variant Code",
        "Qty. per Unit of Measure",
        "Unit of Measure Code",
        "Derived from Blanket Order",
        // "Cross-Reference No.",
        // "Originally Ordered No.",
        // "Originally Ordered Var. Code",
        "Out-of-Stock Substitution",
        "Item Category Code",
        "Nonstock",
        // "Purchasing Code",
        "Product Group Code",
        "Completely Invoiced",
        "Last Invoice Date",
        "Applied Entry to Adjust",
        "Correction",
        "Shipped Qty. Not Returned",
        "Prod. Order Comp. Line No.",
        // "Serial No.",
        // "Lot No.",
        // "Warranty Date",
        // "Expiration Date",
        "Item Tracking",
        // "Return Reason Code",
        // "Source Code",
        // "Country/Region of Origin Code",
        "Currency Code",
        // "Currency Factor",
        // "Sales Quantity"
    };
    
    private static final String[] VEOutputFields = {
        "Entry No.",
        "Item No.",
        "Posting Date", // 2
        "Item Ledger Entry Type", // 3
        "Source No.",
        "Document No.",
        "Description",
        "Location Code",
        "Inventory Posting Group",
        "Source Posting Group",
        "Item Ledger Entry No.", // 10
        "Valued Quantity", // 11
        "Item Ledger Entry Quantity", // 12
        "Invoiced Quantity", // 13
        "Cost per Unit",
        "Sales Amount (Actual)", // 15
        "Salespers./Purch. Code",
        "Discount Amount",
        "User ID",
        "Source Code",
        "Applies-to Entry",
        "Global Dimension 1 Code",
        "Global Dimension 2 Code",
        "Source Type",
        "Cost Amount (Actual)", // 24
        "Cost Posted to G/L",
        // "Reason Code",
        "Drop Shipment",
        // "Journal Batch Name",
        "Gen. Bus. Posting Group",
        "Gen. Prod. Posting Group",
        "Document Date",
        // "External Document No.",
        "Cost Amount (Actual) (ACY)",
        "Cost Posted to G/L (ACY)",
        "Cost per Unit (ACY)",
        "Document Type",
        "Document Line No.",
        "Expected Cost",
        // "Item Charge No.",
        "Valued By Average Cost",
        "Partial Revaluation",
        "Inventoriable",
        "Valuation Date",
        "Entry Type",
        // "Variance Type",
        "Purchase Amount (Actual)",
        "Purchase Amount (Expected)",
        "Sales Amount (Expected)", // 43
        "Cost Amount (Expected)", // 44
        "Cost Amount (Non-Invtbl.)",
        "Cost Amount (Expected) (ACY)",
        "Cost Amount (Non-Invtbl.)(ACY)",
        "Expected Cost Posted to G/L",
        "Exp. Cost Posted to G/L (ACY)",
        // "Job No.",
        // "Job Task No.",
        // "Job Ledger Entry No.",
        // "Variant Code",
        "Adjustment",
        "Average Cost Exception",
        "Capacity Ledger Entry No.",
        "Type",
        // "No.",
        // "Return Reason Code",
    };
    

    private static final Map<String, CustomerEntry> customerMap = Map.ofEntries(
            Map.entry("100238", new CustomerEntry("100238NL", "Archive 22")),
            Map.entry("101005", new CustomerEntry("101005NL", "Sasu Vertbois 30")),
            Map.entry("101045", new CustomerEntry("101045NL", "The Art Box Lijstenmakerij")),
            Map.entry("101540", new CustomerEntry("101540NL", "Any Frame")),
            Map.entry("101580", new CustomerEntry("101580NL", "Art Concept International")),
            Map.entry("101585", new CustomerEntry("101585NL", "De Lijstenmakerij Den Haag")),
            Map.entry("101602", new CustomerEntry("101602NL", "Artbroker International")),
            Map.entry("101633", new CustomerEntry("101633NL", "Frame 4 U")),
            Map.entry("101635", new CustomerEntry("101635NL", "Arts & Crafts Holland")),
            Map.entry("102050", new CustomerEntry("102050NL", "Rinus Bakker Photography")),
            Map.entry("102060", new CustomerEntry("102060NL", "Baspartout")),
            Map.entry("102062", new CustomerEntry("102062NL", "Bernard Weber s.p.r.l.")),
            Map.entry("102090", new CustomerEntry("102090NL", "Atelier Henk de Bouter")),
            Map.entry("102112", new CustomerEntry("102112NL", "Van Beek Eindhoven")),
            Map.entry("102240", new CustomerEntry("102240NL", "Lijstenmakerij Betzel")),
            Map.entry("102516", new CustomerEntry("102516NL", "Atelier Boog - Art")),
            Map.entry("102562", new CustomerEntry("102562NL", "Kunsthandel Brabant")),
            Map.entry("110060", new CustomerEntry("110060NL", "Jacqueline Wirtz")),
            Map.entry("111155", new CustomerEntry("111155NL", "Kees Tol verf & behang bv")),
            Map.entry("111260", new CustomerEntry("111260NL", "Kirsten Goemaere")),
            Map.entry("112461", new CustomerEntry("112461NL", "Lijstenmakerij De Lijst")),
            Map.entry("114542", new CustomerEntry("114542NL", "Lijstenmakerij van Norden v.o.f")),
            Map.entry("116106", new CustomerEntry("116106NL", "Passe-Partout B.V.")),
            Map.entry("116220", new CustomerEntry("116220NL", "Perplex Foto")),
            Map.entry("116580", new CustomerEntry("116580NL", "Lijstenmakerij Paragraaf V.O.F")),
            Map.entry("118290", new CustomerEntry("118290NL", "Quadro")),
            Map.entry("119350", new CustomerEntry("119350NL", "Singer Museum")),
            Map.entry("121380", new CustomerEntry("121380NL", "Unicorn Displays B.V.")),
            Map.entry("122360", new CustomerEntry("122360NL", "Romazo")));


    // private static final FloatDateMap forexMap = FloatDateMap.populateFromExcel(null);


    public static String[] getILEFields() {
        return ILEFields.clone();
    }

    public static String[] getVEFields() {
        return VEFields.clone();
    }

    public static String[] getILEOutputFields() {
        return ILEOutputFields.clone();
    }

    public static String[] getVEOutputFields() {
        return VEOutputFields.clone();
    }

    public static Set<String> getMappedCustomers() {
        return customerMap.keySet();
    }

    public static CustomerEntry getCustomerMapFor(String customerId) {
        return customerMap.get(customerId);
    }

    // public static FloatDateMap getForexMap() {
    //     return forexMap;
    // }

    // public static Float getForexRateFor(String date) {
    //     SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
    //     try {
    //         Date formattedDate = dateFormat.parse(date);
    //         return forexMap.findFloatFromDate(formattedDate);
    //     } catch (ParseException e) {
    //         e.printStackTrace();
    //         return null;
    //     }
    // }

    private ConfigMaps() {
        throw new AssertionError("Do not instantiate ConfigMaps");
    }

    public static class CustomerEntry {
        private String NAV17;
        private String name;

        public CustomerEntry(String NAV17, String name) {
            this.NAV17 = NAV17;
            this.name = name;
        }

        public String getNAV17() {
            return NAV17;
        }

        public String getName() {
            return name;
        }
    }
}
