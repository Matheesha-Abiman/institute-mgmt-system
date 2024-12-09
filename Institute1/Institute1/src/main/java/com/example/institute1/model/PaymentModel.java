package com.example.institute1.model;

import com.example.institute1.crud.CrudUtil;
import com.example.institute1.dto.PaymentDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentModel {

    public static String getNextPaymentID() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = CrudUtil.execute("SELECT payment_id FROM Payment ORDER BY payment_id DESC LIMIT 1");
        if (resultSet.next()) {
            String lastId = resultSet.getString(1);
            int nextIndex = Integer.parseInt(lastId.substring(1)) + 1;
            return String.format("P%03d", nextIndex);
        }
        return "P001";
    }

    public static List<PaymentDto> getAllPayments() throws SQLException, ClassNotFoundException {
        List<PaymentDto> payments = new ArrayList<>();
        ResultSet resultSet = CrudUtil.execute("SELECT * FROM Payment");
        while (resultSet.next()) {
            payments.add(new PaymentDto(
                    resultSet.getString("payment_id"),
                    resultSet.getString("admin_id"),
                    resultSet.getString("student_id"),
                    resultSet.getDate("payment_date").toLocalDate(),
                    resultSet.getDouble("amount")
            ));
        }
        return payments;
    }

    public static boolean savePayment(PaymentDto paymentDto) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO Payment (payment_id, admin_id, student_id, payment_date, amount) VALUES (?, ?, ?, ?, ?)";
        return CrudUtil.execute(query,
                paymentDto.getPaymentId(), paymentDto.getAdminId(), paymentDto.getStudentId(), paymentDto.getPaymentDate(), paymentDto.getAmount());
    }

    public static boolean updatePayment(PaymentDto paymentDto) throws SQLException, ClassNotFoundException {
        String query = "UPDATE Payment SET admin_id = ?, student_id = ?, payment_date = ?, amount = ? WHERE payment_id = ?";
        return CrudUtil.execute(query,
                paymentDto.getAdminId(), paymentDto.getStudentId(), paymentDto.getPaymentDate(), paymentDto.getAmount(), paymentDto.getPaymentId());
    }

    public static boolean deletePayment(String paymentId) throws SQLException, ClassNotFoundException {
        String query = "DELETE FROM Payment WHERE payment_id = ?";
        return CrudUtil.execute(query, paymentId);
    }
}
