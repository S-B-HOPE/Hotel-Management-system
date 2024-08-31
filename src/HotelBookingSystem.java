import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class HotelBookingSystem extends JFrame {
    private JTextField roomNumberField;
    private JTextField guestNameField;
    private JComboBox<String> roomComboBox;
    private JSpinner checkInDateSpinner;
    private JSpinner checkOutDateSpinner;

    public HotelBookingSystem() {
        setTitle("Hotel Booking System");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 2));

        // Room Number Field
        add(new JLabel("Room Number:"));
        roomNumberField = new JTextField();
        add(roomNumberField);

        // Guest Name Field
        add(new JLabel("Guest Name:"));
        guestNameField = new JTextField();
        add(guestNameField);

        // Room ComboBox
        add(new JLabel("Select Room:"));
        roomComboBox = new JComboBox<>(loadAvailableRooms());
        add(roomComboBox);

        // Check-In Date Spinner
        add(new JLabel("Check-In Date:"));
        checkInDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor checkInDateEditor = new JSpinner.DateEditor(checkInDateSpinner, "yyyy-MM-dd");
        checkInDateSpinner.setEditor(checkInDateEditor);
        add(checkInDateSpinner);

        // Check-Out Date Spinner
        add(new JLabel("Check-Out Date:"));
        checkOutDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor checkOutDateEditor = new JSpinner.DateEditor(checkOutDateSpinner, "yyyy-MM-dd");
        checkOutDateSpinner.setEditor(checkOutDateEditor);
        add(checkOutDateSpinner);

        // Submit Button
        JButton submitButton = new JButton("Book Room");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookRoom();
            }
        });
        add(submitButton);

        setVisible(true);
    }

    private String[] loadAvailableRooms() {
        ArrayList<String> rooms = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT room_number FROM rooms WHERE is_available = true")) {

            while (rs.next()) {
                rooms.add(rs.getString("room_number"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms.toArray(new String[0]);
    }

    private void bookRoom() {
        String roomNumber = (String) roomComboBox.getSelectedItem();
        String guestName = guestNameField.getText();
        Date checkInDate = new Date(((java.util.Date) checkInDateSpinner.getValue()).getTime());
        Date checkOutDate = new Date(((java.util.Date) checkOutDateSpinner.getValue()).getTime());

        try (Connection connection = DBConnection.getConnection()) {
            // Get room ID
            PreparedStatement roomStmt = connection.prepareStatement("SELECT room_id FROM rooms WHERE room_number = ?");
            roomStmt.setString(1, roomNumber);
            ResultSet rs = roomStmt.executeQuery();
            if (rs.next()) {
                int roomId = rs.getInt("room_id");

                // Insert reservation
                PreparedStatement reservationStmt = connection.prepareStatement(
                        "INSERT INTO reservations (room_id, guest_name, check_in_date, check_out_date) VALUES (?, ?, ?, ?)");
                reservationStmt.setInt(1, roomId);
                reservationStmt.setString(2, guestName);
                reservationStmt.setDate(3, checkInDate);
                reservationStmt.setDate(4, checkOutDate);
                reservationStmt.executeUpdate();

                // Update room availability
                PreparedStatement updateRoomStmt = connection.prepareStatement("UPDATE rooms SET is_available = false WHERE room_id = ?");
                updateRoomStmt.setInt(1, roomId);
                updateRoomStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Room booked successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelBookingSystem());
    }
}
