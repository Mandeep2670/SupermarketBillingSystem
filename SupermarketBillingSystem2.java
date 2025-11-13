import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SupermarketBillingSystem2 {
    private static final Map<String, Double> itemPrices = new HashMap<>();
    private static final Map<String, Double> itemDiscounts = new HashMap<>();
    private static final double GST_RATE = 18.0;

    private static double totalAmount = 0.0;
    private static int billCount = 0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        initializeItems();

        System.out.println("======================================");
        System.out.println("     SUPERMARKET BILLING SYSTEM");
        System.out.println("======================================");

        System.out.print("Enter Customer Name: ");
        String customerName = sc.nextLine();

        String billNo = generateBillNumber();
        String date = new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date());

        List<String[]> itemsBought = new ArrayList<>();

        boolean running = true;
        while (running) {
            System.out.println("\nAvailable Items:");
            for (String item : itemPrices.keySet()) {
                System.out.println("- " + item + " (₹" + itemPrices.get(item) + ", Disc: " + itemDiscounts.get(item) + "%)");
            }

            System.out.print("\nEnter item name (or 'done' to finish): ");
            String itemName = sc.nextLine().trim();

            if (itemName.equalsIgnoreCase("done")) {
                running = false;
                break;
            }

            if (!itemPrices.containsKey(itemName)) {
                System.out.println("Item not found! Try again.");
                continue;
            }

            System.out.print("Enter quantity: ");
            int qty;
            try {
                qty = Integer.parseInt(sc.nextLine());
                if (qty <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.out.println("Invalid quantity!");
                continue;
            }

            double price = itemPrices.get(itemName);
            double discount = itemDiscounts.get(itemName);
            double discountedPrice = price - (price * discount / 100);
            double total = discountedPrice * qty;
            totalAmount += total;

            itemsBought.add(new String[]{
                    itemName,
                    String.format("%.2f", price),
                    String.format("%.1f", discount),
                    String.valueOf(qty),
                    String.format("%.2f", total)
            });

            System.out.println("Item added successfully!");
        }

        if (itemsBought.isEmpty()) {
            System.out.println("\nNo items added. Exiting...");
            return;
        }

        printBill(customerName, billNo, date, itemsBought);
        saveBillToFile(customerName, billNo, date, itemsBought);

        sc.close();
    }

    private static void initializeItems() {
        itemPrices.put("Milk", 50.0);
        itemPrices.put("Rice", 80.0);
        itemPrices.put("Bread", 30.0);
        itemPrices.put("Sugar", 45.0);
        itemPrices.put("Tea", 120.0);
        itemPrices.put("Oil", 160.0);
        itemPrices.put("Salt", 20.0);

        itemDiscounts.put("Milk", 5.0);
        itemDiscounts.put("Rice", 10.0);
        itemDiscounts.put("Bread", 7.0);
        itemDiscounts.put("Sugar", 8.0);
        itemDiscounts.put("Tea", 12.0);
        itemDiscounts.put("Oil", 15.0);
        itemDiscounts.put("Salt", 3.0);
    }

    private static String generateBillNumber() {
        return "BILL-" + (1000 + new Random().nextInt(9000));
    }

    private static void printBill(String customerName, String billNo, String date, List<String[]> items) {
        double gst = totalAmount * GST_RATE / 100;
        double grandTotal = totalAmount + gst;

        System.out.println("\n=====================================");
        System.out.println("           SUPERMARKET BILL");
        System.out.println("=====================================");
        System.out.println("Bill No: " + billNo);
        System.out.println("Customer: " + customerName);
        System.out.println("Date: " + date);
        System.out.println("-------------------------------------");
        System.out.printf("%-12s %-7s %-5s %-7s %-8s%n", "Item", "Price", "Disc", "Qty", "Total");
        System.out.println("-------------------------------------");

        for (String[] row : items) {
            System.out.printf("%-12s %-7s %-5s %-7s %-8s%n", row[0], row[1], row[2], row[3], row[4]);
        }

        System.out.println("-------------------------------------");
        System.out.printf("Subtotal: ₹ %.2f%n", totalAmount);
        System.out.printf("GST (%.0f%%): ₹ %.2f%n", GST_RATE, gst);
        System.out.printf("Grand Total: ₹ %.2f%n", grandTotal);
        System.out.println("=====================================");
    }

    private static void saveBillToFile(String customerName, String billNo, String date, List<String[]> items) {
        billCount++;
        String fileName = "Bill_" + customerName.replaceAll("\\s+", "") + "" + billCount + ".txt";

        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write("=====================================\n");
            fw.write("        SUPERMARKET BILL RECEIPT\n");
            fw.write("=====================================\n");
            fw.write("Bill No: " + billNo + "\n");
            fw.write("Date: " + date + "\n");
            fw.write("Customer: " + customerName + "\n");
            fw.write("-------------------------------------\n");
            fw.write(String.format("%-12s %-7s %-5s %-7s %-8s%n", "Item", "Price", "Disc", "Qty", "Total"));
            fw.write("-------------------------------------\n");

            for (String[] row : items) {
                fw.write(String.format("%-12s %-7s %-5s %-7s %-8s%n",
                        row[0], row[1], row[2], row[3], row[4]));
            }

            fw.write("-------------------------------------\n");
            fw.write(String.format("Subtotal: ₹ %.2f%n", totalAmount));
            fw.write(String.format("GST (%.0f%%): ₹ %.2f%n", GST_RATE, totalAmount * GST_RATE / 100));
            fw.write(String.format("Grand Total: ₹ %.2f%n", totalAmount + (totalAmount * GST_RATE / 100)));
            fw.write("=====================================\n");
            System.out.println("\nBill saved successfully to file: " + fileName);
        } catch (IOException e) {
            System.out.println("Error saving bill to file!");
        }
    }
}