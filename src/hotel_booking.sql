CREATE DATABASE hotel_booking;

USE hotel_booking;

CREATE TABLE rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(10) NOT NULL,
    room_type VARCHAR(50),
    is_available BOOLEAN
);

CREATE TABLE reservations (
    reservation_id INT AUTO_INCREMENT PRIMARY KEY,
    room_id INT,
    guest_name VARCHAR(100),
    check_in_date DATE,
    check_out_date DATE,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);
