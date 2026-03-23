import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Main GUI Application Class
public class shoppingApp extends JFrame {
    private List<Product> products;
    private List<CartItem> cart;
    private JTextArea cartArea;
    private JTextField quantityField, amountPaidField;
    private JLabel detailsLabel;
    private double total = 0.0;

    public shoppingApp() {
        setTitle("Group II Final Project - Shopping App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        products = new ArrayList<>();
        cart = new ArrayList<>();
        initializeProducts();
        initializeUI();
    }

    private void initializeProducts() {
        products.add(new Book("Angels & Demons", 163, "images/angels_and_demons.jpg", "Dan Brown", "A mystery-thriller novel."));
        products.add(new Book("Desert God", 392, "images/desert_god.jpg", "Wilbur Smith", "An adventure set in ancient Egypt."));
        products.add(new Book("Steve Jobs", 550, "images/steve_jobs.jpg", "Walter Isaacson", "The biography of Steve Jobs."));
        products.add(new Book("The Deathly Hallows", 700, "images/deathly_hallows.jpg", "J.K. Rowling", "The final book in the Harry Potter series."));
        products.add(new Book("The Immortals Of Meluha", 399, "images/immortals_of_meluha.jpg", "Amish Tripathi", "A story of Shiva."));
        products.add(new Cosmetics("Color Sensational Creamy Matte Lipstick", 299, "images/color_sensational_lipstick.jpg", "A comfortable matte formula."));
        products.add(new Cosmetics("Enrich Lip Crayon", 225, "images/enrich_lip_crayon.jpg", "A matte finish lip crayon with smooth texture."));
        products.add(new Cosmetics("Get Inked! Sketch Eyeliner", 450, "images/get_inked_eyeliner.jpg", "A waterproof eyeliner with a precision tip."));
        products.add(new Cosmetics("Eyeconic Kajal", 275, "images/eyeconic_kajal.jpg", "A long-lasting kajal that stays for up to 22 hours."));
        products.add(new Cosmetics("BB Cream", 124, "images/bb_cream.jpg", "A daily all-in-one moisturizer with SPF 24."));
        products.add(new Electronics("OnePlus 7T", 39999, "images/oneplus_7t.jpg", "90 Hz fluid display, triple camera setup."));
        products.add(new Electronics("Samsung Galaxy S10", 76900, "images/galaxy_s10.jpg", "Infinity-O display, triple rear cameras."));
        products.add(new Electronics("iPhone 11 Pro", 99900, "images/iphone_11_pro.jpg", "A13 Bionic chip, triple-camera system."));
        products.add(new Electronics("Pixel 3", 47990, "images/pixel_3.jpg", "Exceptional camera, pure Android experience."));
        products.add(new Electronics("Redmi Note 7 Pro", 14400, "images/redmi_note_7_pro.jpg", "48 MP camera, Snapdragon 675."));
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(new JLabel("Available Products:"), BorderLayout.NORTH);

        DefaultListModel<Product> listModel = new DefaultListModel<>();
        for (Product product : products) {
            listModel.addElement(product);
        }

        JList<Product> itemList = new JList<>(listModel);
        itemList.setCellRenderer(new ProductCellRenderer());
        JScrollPane itemScrollPane = new JScrollPane(itemList);
        leftPanel.add(itemScrollPane, BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsLabel = new JLabel();
        detailsPanel.add(detailsLabel, BorderLayout.CENTER);
        leftPanel.add(detailsPanel, BorderLayout.SOUTH);

        add(leftPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        cartArea = new JTextArea(20, 20);
        cartArea.setEditable(false);
        JScrollPane cartScrollPane = new JScrollPane(cartArea);
        centerPanel.add(cartScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField();
        inputPanel.add(quantityField);
        JButton addToCartButton = new JButton("Add to Cart");
        inputPanel.add(addToCartButton);
        JButton removeButton = new JButton("Remove Item");
        inputPanel.add(removeButton);
        centerPanel.add(inputPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new GridLayout(3, 1));
        JButton checkoutButton = new JButton("Checkout");
        rightPanel.add(checkoutButton);
        rightPanel.add(new JLabel("Amount Paid:"));
        amountPaidField = new JTextField();
        rightPanel.add(amountPaidField);
        JButton paymentButton = new JButton("Pay");
        rightPanel.add(paymentButton);

        add(rightPanel, BorderLayout.EAST);

        addToCartButton.addActionListener(new AddToCartListener(itemList));
        checkoutButton.addActionListener(new CheckoutListener());
        paymentButton.addActionListener(new PaymentListener());
        removeButton.addActionListener(new RemoveItemListener());

        itemList.addListSelectionListener(e -> showItemDetails(itemList.getSelectedValue()));
    }

    private void showItemDetails(Product product) {
        if (product != null) {
            detailsLabel.setText("<html>" + product.getDetails() + "</html>");
            detailsLabel.setIcon(new ImageIcon(product.getImagePath()));
        }
    }

    private class AddToCartListener implements ActionListener {
        private JList<Product> itemList;

        public AddToCartListener(JList<Product> itemList) {
            this.itemList = itemList;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Product selectedProduct = itemList.getSelectedValue();
            if (selectedProduct != null) {
                try {
                    double qty = Double.parseDouble(quantityField.getText());
                    cart.add(new CartItem(selectedProduct, qty));
                    cartArea.append(selectedProduct.getName() + " - Quantity: " + qty + "\n");
                    total += selectedProduct.getPrice() * qty;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(shoppingApp.this, "Please enter a valid quantity.");
                }
            }
        }
    }

    private class CheckoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (cart.isEmpty()) {
                JOptionPane.showMessageDialog(shoppingApp.this, "Your cart is empty.");
                return;
            }
            int option = JOptionPane.showOptionDialog(shoppingApp.this,
                    "Choose your payment method",
                    "Payment Method",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Cash", "Credit Card"},
                    "Cash");
            String paymentMethod = (option == 0) ? "Cash" : "Credit Card";
            JOptionPane.showMessageDialog(shoppingApp.this, "Total Amount: PHP " + total);
        }
    }

    private class PaymentListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                double amt = Double.parseDouble(amountPaidField.getText());
                if (amt < total) {
                    JOptionPane.showMessageDialog(shoppingApp.this, "Insufficient amount. Please enter again.");
                    return;
                }
                double change = amt - total;
                JOptionPane.showMessageDialog(shoppingApp.this, "Change: PHP " + change);
                JOptionPane.showMessageDialog(shoppingApp.this, "Thank you for your purchase!");
                deliveryDetails();
                resetCart();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(shoppingApp.this, "Please enter a valid amount.");
            }
        }
    }

    private class RemoveItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = JOptionPane.showInputDialog(shoppingApp.this, "Enter the name of the item to remove:");
            if (input != null) {
                boolean found = false;
                for (CartItem item : cart) {
                    if (item.getProduct().getName().equalsIgnoreCase(input)) {
                        total -= item.getProduct().getPrice() * item.getQuantity();
                        cart.remove(item);
                        updateCartArea();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    JOptionPane.showMessageDialog(shoppingApp.this, "Item not found.");
                }
            }
        }
    }

    private void updateCartArea() {
        cartArea.setText("");
        for (CartItem item : cart) {
            cartArea.append(item.getProduct().getName() + " - Quantity: " + item.getQuantity() + "\n");
        }
    }

    private void deliveryDetails() {
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();
        ((AbstractDocument) phoneField.getDocument()).setDocumentFilter(new NumericDocumentFilter());

        Object[] fields = {
                "Name:", nameField,
                "Address:", addressField,
                "Phone number:", phoneField
        };
        int option = JOptionPane.showConfirmDialog(shoppingApp.this, fields, "Delivery Details", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            String address = addressField.getText();
            String phone = phoneField.getText();
            if (!name.isEmpty() && !address.isEmpty() && !phone.isEmpty()) {
                saveOrderDetails(name, address, phone);
            } else {
                JOptionPane.showMessageDialog(shoppingApp.this, "Please fill in all details.");
            }
        }
    }

    private void saveOrderDetails(String name, String address, String phone) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("order_details.txt", true))) {
            writer.write("Customer Name: " + name);
            writer.newLine();
            writer.write("Address: " + address);
            writer.newLine();
            writer.write("Phone Number: " + phone);
            writer.newLine();
            writer.write("Total Amount: PHP " + total);
            writer.newLine();
            writer.newLine();
            JOptionPane.showMessageDialog(shoppingApp.this, "Order details saved.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(shoppingApp.this, "Error saving order details.");
        }
    }

    private void resetCart() {
        cart.clear();
        cartArea.setText("");
        total = 0.0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            shoppingApp gui = new shoppingApp();
            gui.setVisible(true);
        });
    }
}

// Product Class
abstract class Product {
    private String name;
    private double price;
    private String imagePath;

    public Product(String name, double price, String imagePath) {
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getImagePath() {
        return imagePath;
    }

    public abstract String getDetails();

    @Override
    public String toString() {
        return name + " (PHP " + price + ")";
    }
}

// Book Class
class Book extends Product {
    private String author;
    private String description;

    public Book(String name, double price, String imagePath, String author, String description) {
        super(name, price, imagePath);
        this.author = author;
        this.description = description;
    }

    @Override
    public String getDetails() {
        return "<html>Title: " + getName() + "<br>Price: PHP " + getPrice() + "<br>Author: " + author + "<br>Description: " + description + "</html>";
    }
}

// Cosmetics Class
class Cosmetics extends Product {
    private String description;

    public Cosmetics(String name, double price, String imagePath, String description) {
        super(name, price, imagePath);
        this.description = description;
    }

    @Override
    public String getDetails() {
        return "<html>Product: " + getName() + "<br>Price: PHP " + getPrice() + "<br>Description: " + description + "</html>";
    }
}

// Electronics Class
class Electronics extends Product {
    private String specifications;

    public Electronics(String name, double price, String imagePath, String specifications) {
        super(name, price, imagePath);
        this.specifications = specifications;
    }

    @Override
    public String getDetails() {
        return "<html>Product: " + getName() + "<br>Price: PHP " + getPrice() + "<br>Specifications: " + specifications + "</html>";
    }
}

// CartItem Class
class CartItem {
    private Product product;
    private double quantity;

    public CartItem(Product product, double quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public double getQuantity() {
        return quantity;
    }
}

// Custom ListCellRenderer to display product names in JList
class ProductCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Product) {
            Product product = (Product) value;
            setText(product.getName());
        }
        return this;
    }
}

// Custom DocumentFilter to allow only numeric input
class NumericDocumentFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string != null && string.matches("\\d+")) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text != null && text.matches("\\d+")) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
