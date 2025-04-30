package unieventhub.util;

import unieventhub.util.StudentCardValidator;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Pair;

import java.time.Year;
import java.util.Optional;

public class CardPaymentDialog {

    public static Optional<Pair<String, String>> show() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Payment Details");
        dialog.setHeaderText("Enter your card details:");

        // Create form fields
        TextField cardField = new TextField();
        cardField.setPromptText("Card Number (16 digits)");

        TextField cvvField = new TextField();
        cvvField.setPromptText("CVV (3 digits)");

        ComboBox<String> monthBox = new ComboBox<>();
        for (int i = 1; i <= 12; i++) monthBox.getItems().add(String.format("%02d", i));
        monthBox.setPromptText("MM");

        ComboBox<String> yearBox = new ComboBox<>();
        int currentYear = Year.now().getValue();
        for (int i = 0; i <= 10; i++) yearBox.getItems().add(String.valueOf(currentYear + i));
        yearBox.setPromptText("YYYY");

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Card Number:"), 0, 0);
        grid.add(cardField, 1, 0);
        grid.add(new Label("CVV:"), 0, 1);
        grid.add(cvvField, 1, 1);
        grid.add(new Label("Expiry Date:"), 0, 2);
        grid.add(monthBox, 1, 2);
        grid.add(yearBox, 2, 2);

        dialog.getDialogPane().setContent(grid);
        ButtonType payButtonType = new ButtonType("Pay", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(payButtonType, ButtonType.CANCEL);

        // Disable Pay button initially
        Node payButton = dialog.getDialogPane().lookupButton(payButtonType);
        payButton.setDisable(true);
        
     // Enable Pay button when fields are filled
        ChangeListener<String> validationListener = (obs, oldVal, newVal) -> {
            boolean filled = !cardField.getText().isEmpty()
                    && !cvvField.getText().isEmpty()
                    && monthBox.getValue() != null
                    && yearBox.getValue() != null;
            payButton.setDisable(!filled);
        };
        
        
        cardField.textProperty().addListener(validationListener);
        cvvField.textProperty().addListener(validationListener);
        monthBox.valueProperty().addListener((obs, oldVal, newVal) -> validationListener.changed(null, null, null));
        yearBox.valueProperty().addListener((obs, oldVal, newVal) -> validationListener.changed(null, null, null));

        
        dialog.setResultConverter(dialogButton -> {
        	if (dialogButton == payButtonType) {
                String card = cardField.getText();
                String cvv = cvvField.getText();
                String month = monthBox.getValue();
                String year = yearBox.getValue();

                // Validation
                if (!card.matches("\\d{16}")) {
                    showError("Invalid Card Number", "Card number must be 16 digits.");
                    return null;
                }

                if (!StudentCardValidator.isValid(Long.parseLong(card))) {
                    showError("Invalid Card", "Card number failed Luhn check.");
                    return null;
                }

                if (!cvv.matches("\\d{3}")) {
                    showError("Invalid CVV", "CVV must be 3 digits.");
                    return null;
                }

                if (month == null || year == null) {
                    showError("Missing Expiry Date", "Please select both month and year.");
                    return null;
                }

                int expYear = Integer.parseInt(year);
                int expMonth = Integer.parseInt(month);
                java.time.YearMonth current = java.time.YearMonth.now();
                java.time.YearMonth entered = java.time.YearMonth.of(expYear, expMonth);

                if (entered.isBefore(current)) {
                    showError("Card Expired", "Your card expiry date is in the past.");
                    return null;
                }

                return new Pair<>(card, cvv); // success
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        return Optional.ofNullable(result.orElse(null));
    }

    private static void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}