CREATE DATABASE IF NOT EXISTS dbProjectSiswa;

USE dbProjectSiswa;

CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nama_lengkap VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pembayaran_spp (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_siswa VARCHAR(50) NOT NULL,
    nama_siswa VARCHAR(100) NOT NULL,
    kelas VARCHAR(20),
    jurusan VARCHAR(30),
    pembayaran VARCHAR(50),
    jumlah DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO user (username, password, nama_lengkap) VALUES 
('admin', 'admin123', 'Administrator'),
('user', 'user123', 'User Biasa');
