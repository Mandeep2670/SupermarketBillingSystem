import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SupermarketBillingSystem extends JFrame {
    private JComboBox<String> comboItems;
    private JTextField txtPrice, txtQuantity, txtCustomerName;
    private JTable billTable;
    private DefaultTableModel tableModel;
    private JLabel lblTotal, lblGST, lblDiscount, lblGrandTotal, lblBillNo, lblDate;
    private double totalAmount = 0.0;
    private double gstRate = 18.0;
    private int userCount = 0;

    private final Map<String, Double> itemPrices = new HashMap<>();
    private final Map<String, Double> itemDiscounts = new HashMap<>();

    public SupermarketBillingSystem() {
        setTitle("Supermarket Billing System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initializeItems();

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setPreferredSize(new Dimension(950, 70));
        JLabel lblTitle = new JLabel("SUPERMARKET BILLING SYSTEM");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle);

        lblBillNo = new JLabel("  Bill No: " + generateBillNumber());
        lblBillNo.setForeground(Color.WHITE);
        headerPanel.add(lblBillNo);

        lblDate = new JLabel();
        lblDate.setForeground(Color.WHITE);
        updateDate();
        headerPanel.add(lblDate);
        add(headerPanel, BorderLayout.NORTH);

        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Items"));
        inputPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1;
        txtCustomerName = new JTextField(15);
        inputPanel.add(txtCustomerName, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Select Item:"), gbc);
        gbc.gridx = 1;
        comboItems = new JComboBox<>(itemPrices.keySet().toArray(new String[0]));
        inputPanel.add(comboItems, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Price (₹):"), gbc);
        gbc.gridx = 1;
        txtPrice = new JTextField(15);
        txtPrice.setEditable(false);
        inputPanel.add(txtPrice, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        txtQuantity = new JTextField(15);
        inputPanel.add(txtQuantity, gbc);

        JButton btnAdd = new JButton("Add Item");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        btnAdd.setBackground(new Color(34, 139, 34));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 14));
        btnAdd.addActionListener(e -> addItem());
        inputPanel.add(btnAdd, gbc);

        add(inputPanel, BorderLayout.WEST);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Bill Details"));
        String[] columns = {"S.No", "Item Name", "Price (₹)", "Discount (%)", "Quantity", "Total (₹)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        billTable = new JTable(tableModel);
        billTable.setFont(new Font("Arial", Font.PLAIN, 13));
        billTable.setRowHeight(25);
        billTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        billTable.getTableHeader().setBackground(new Color(70, 130, 180));
        billTable.getTableHeader().setForeground(Color.WHITE);
        tablePanel.add(new JScrollPane(billTable), BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // Totals + Buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel totalPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        totalPanel.setBackground(new Color(245, 245, 245));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        totalPanel.add(new JLabel("Subtotal: ")); lblTotal = new JLabel("₹ 0.00"); totalPanel.add(lblTotal);
        totalPanel.add(new JLabel("GST (18%): ")); lblGST = new JLabel("₹ 0.00"); totalPanel.add(lblGST);
        totalPanel.add(new JLabel("Discount: ")); lblDiscount = new JLabel("₹ 0.00"); totalPanel.add(lblDiscount);
        totalPanel.add(new JLabel("Grand Total: ")); lblGrandTotal = new JLabel("₹ 0.00");
        lblGrandTotal.setForeground(Color.RED); lblGrandTotal.setFont(new Font("Arial", Font.BOLD, 16));
        totalPanel.add(lblGrandTotal);

        bottomPanel.add(totalPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnRemove = new JButton("Remove Item");
        btnRemove.setBackground(Color.RED);
        btnRemove.setForeground(Color.WHITE);
        btnRemove.addActionListener(e -> removeItem());

        JButton btnClear = new JButton("Clear All");
        btnClear.setBackground(Color.ORANGE);
        btnClear.setForeground(Color.WHITE);
        btnClear.addActionListener(e -> clearAll());

        JButton btnPrint = new JButton("Print & Save Bill");
        btnPrint.setBackground(new Color(0, 102, 204));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.addActionListener(e -> printAndSaveBill());

        buttonPanel.add(btnRemove);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnPrint);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        comboItems.addActionListener(e -> updatePrice());
        updatePrice();
    }

    private void initializeItems() {
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

    private void updateDate() {
        lblDate.setText("Date: " + new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date()));
    }

    private String generateBillNumber() {
        return "BILL-" + (1000 + new Random().nextInt(9000));
    }

    private void updatePrice() {
        String selected = (String) comboItems.getSelectedItem();
        if (selected != null)
            txtPrice.setText(String.format("%.2f", itemPrices.get(selected)));
    }

    private void addItem() {
        try {
            String item = (String) comboItems.getSelectedItem();
            double price = itemPrices.get(item);
            double discount = itemDiscounts.get(item);
            int qty = Integer.parseInt(txtQuantity.getText().trim());
            if (qty <= 0) throw new NumberFormatException();

            double discountedPrice = price - (price * discount / 100);
            double total = discountedPrice * qty;
            totalAmount += total;

            tableModel.addRow(new Object[]{
                    tableModel.getRowCount() + 1, item, price, discount, qty, String.format("%.2f", total)
            });

            updateTotals();
            txtQuantity.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity! Please enter a positive number.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTotals() {
        double gst = totalAmount * gstRate / 100;
        double grand = totalAmount + gst;

        lblTotal.setText(String.format("₹ %.2f", totalAmount));
        lblGST.setText(String.format("₹ %.2f", gst));
        lblDiscount.setText("Already applied on each item");
        lblGrandTotal.setText(String.format("₹ %.2f", grand));
    }

    private void removeItem() {
        int row = billTable.getSelectedRow();
        if (row != -1) {
            double amt = Double.parseDouble(tableModel.getValueAt(row, 5).toString());
            totalAmount -= amt;
            tableModel.removeRow(row);
            for (int i = 0; i < tableModel.getRowCount(); i++)
                tableModel.setValueAt(i + 1, i, 0);
            updateTotals();
        } else {
            JOptionPane.showMessageDialog(this, "Select an item to remove!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearAll() {
        tableModel.setRowCount(0);
        totalAmount = 0;
        lblTotal.setText("₹ 0.00");
        lblGST.setText("₹ 0.00");
        lblGrandTotal.setText("₹ 0.00");
        txtQuantity.setText("");
    }

    private void printAndSaveBill() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No items to print!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userCount >= 5) {
            JOptionPane.showMessageDialog(this, "Maximum of 5 user records reached!", "Limit Reached", JOptionPane.WARNING_MESSAGE);
            return;
        }

        userCount++;
        String userFile = "Bill_User" + userCount + ".txt";

        StringBuilder bill = new StringBuilder();
        bill.append("=====================================\n");
        bill.append("        SUPERMARKET BILL RECEIPT\n");
        bill.append("=====================================\n");
        bill.append("Bill No: ").append(lblBillNo.getText()).append("\n");
        bill.append("Date: ").append(new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date())).append("\n");
        bill.append("Customer: ").append(txtCustomerName.getText().trim()).append("\n");
        bill.append("-------------------------------------\n");
        bill.append(String.format("%-12s %-7s %-5s %-7s %-8s\n", "Item", "Price", "Disc", "Qty", "Total"));
        bill.append("-------------------------------------\n");

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            bill.append(String.format("%-12s %-7s %-5s %-7s %-8s\n",
                    tableModel.getValueAt(i, 1), tableModel.getValueAt(i, 2),
                    tableModel.getValueAt(i, 3), tableModel.getValueAt(i, 4),
                    tableModel.getValueAt(i, 5)));
        }
        bill.append("-------------------------------------\n");
        bill.append(String.format("Subtotal: ₹ %.2f\n", totalAmount));
        bill.append(String.format("GST (18%%): ₹ %.2f\n", totalAmount * gstRate / 100));
        bill.append(String.format("Grand Total: ₹ %.2f\n", totalAmount + (totalAmount * gstRate / 100)));
        bill.append("=====================================\n\n");

        try (FileWriter fw = new FileWriter(userFile, true)) {
            fw.write(bill.toString());
            JOptionPane.showMessageDialog(this, "Bill saved to " + userFile, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving bill!", "File Error", JOptionPane.ERROR_MESSAGE);
        }

        JTextArea textArea = new JTextArea(bill.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Bill Receipt", JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SupermarketBillingSystem().setVisible(true));
    }
}