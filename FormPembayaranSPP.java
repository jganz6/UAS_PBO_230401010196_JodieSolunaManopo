import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FormPembayaranSPP extends JFrame {
    private JTextField txtIdSiswa, txtNamaSiswa, txtKelas, txtJumlah;
    private JComboBox<String> cmbJurusan, cmbPembayaran;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnSimpan, btnUbah, btnHapus, btnCetak, btnLogout;
    private List<PembayaranSPP> dataPembayaran;
    private int selectedRow = -1;
    private User currentUser;
    
    public FormPembayaranSPP(User user) {
        this.currentUser = user;
        dataPembayaran = new ArrayList<>();
        initComponents();
        setupLayout();
        setupListeners();
        loadDataFromDatabase();
        setVisible(true);
    }
    
    private void initComponents() {
        setTitle("SISTEM PEMBAYARAN SPP  SMP JAKENAN");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        txtIdSiswa = new JTextField(25);
        txtNamaSiswa = new JTextField(25);
        txtKelas = new JTextField(25);
        txtJumlah = new JTextField(25);
        
        String[] jurusanOptions = {"Pilih", "IPA", "IPS", "Bahasa"};
        cmbJurusan = new JComboBox<>(jurusanOptions);
        
        String[] pembayaranOptions = {"Pilih", "BCA", "Mandiri", "BNI", "BRI", "Tunai"};
        cmbPembayaran = new JComboBox<>(pembayaranOptions);
        
        btnSimpan = new JButton("simpan");
        btnHapus = new JButton("Hapus");
        btnUbah = new JButton("Ubah");
        btnCetak = new JButton("Cetak");
        btnLogout = new JButton("Logout");
        
        String[] columnNames = {"ID", "ID Siswa", "Nama Siswa", "Kelas", "Jurusan", "Pembayaran", "Jumlah"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("ID Siswa :"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtIdSiswa, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(new JLabel("Nama Siswa :"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtNamaSiswa, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Kelas :"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtKelas, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(new JLabel("Jurusan :"), gbc);
        gbc.gridx = 3;
        formPanel.add(cmbJurusan, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Pembayaran :"), gbc);
        gbc.gridx = 1;
        formPanel.add(cmbPembayaran, gbc);
        
        gbc.gridx = 2; gbc.gridy = 2;
        formPanel.add(new JLabel("Jumlah :"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtJumlah, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnUbah);
        buttonPanel.add(btnCetak);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(950, 300));
        
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.add(new JLabel("User: " + currentUser.getNamaLengkap() + "  "));
        logoutPanel.add(btnLogout);
        
        mainPanel.add(logoutPanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void setupListeners() {
        btnSimpan.addActionListener(e -> simpanData());
        btnUbah.addActionListener(e -> ubahData());
        btnHapus.addActionListener(e -> hapusData());
        btnCetak.addActionListener(e -> cetakData());
        btnLogout.addActionListener(e -> logout());
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    loadDataToForm(selectedRow);
                }
            }
        });
    }
    
    private void simpanData() {
        if (validateInput()) {
            PembayaranSPP pembayaran = new PembayaranSPP(
                txtIdSiswa.getText().trim(),
                txtNamaSiswa.getText().trim(),
                txtKelas.getText().trim(),
                (String) cmbJurusan.getSelectedItem(),
                (String) cmbPembayaran.getSelectedItem(),
                Double.parseDouble(txtJumlah.getText().trim())
            );
            
            if (saveToDatabase(pembayaran)) {
                dataPembayaran.add(pembayaran);
                loadDataFromDatabase();
                clearForm();
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void ubahData() {
        if (selectedRow >= 0 && selectedRow < dataPembayaran.size()) {
            if (validateInput()) {
                PembayaranSPP pembayaran = dataPembayaran.get(selectedRow);
                pembayaran.setIdSiswa(txtIdSiswa.getText().trim());
                pembayaran.setNamaSiswa(txtNamaSiswa.getText().trim());
                pembayaran.setKelas(txtKelas.getText().trim());
                pembayaran.setJurusan((String) cmbJurusan.getSelectedItem());
                pembayaran.setPembayaran((String) cmbPembayaran.getSelectedItem());
                pembayaran.setJumlah(Double.parseDouble(txtJumlah.getText().trim()));
                
                if (updateToDatabase(pembayaran)) {
                    loadDataFromDatabase();
                    clearForm();
                    selectedRow = -1;
                    JOptionPane.showMessageDialog(this, "Data berhasil diubah!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void hapusData() {
        if (selectedRow >= 0 && selectedRow < dataPembayaran.size()) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Apakah Anda yakin ingin menghapus data ini?", 
                "Konfirmasi Hapus", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                PembayaranSPP pembayaran = dataPembayaran.get(selectedRow);
                if (deleteFromDatabase(pembayaran.getIdSiswa())) {
                    dataPembayaran.remove(selectedRow);
                    loadDataFromDatabase();
                    clearForm();
                    selectedRow = -1;
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void cetakData() {
        if (dataPembayaran.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk dicetak!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("LAPORAN PEMBAYARAN SPP\n");
        sb.append("=".repeat(80)).append("\n\n");
        
        for (int i = 0; i < dataPembayaran.size(); i++) {
            PembayaranSPP p = dataPembayaran.get(i);
            sb.append("Data ke-").append(i + 1).append("\n");
            sb.append("ID Siswa: ").append(p.getIdSiswa()).append("\n");
            sb.append("Nama Siswa: ").append(p.getNamaSiswa()).append("\n");
            sb.append("Kelas: ").append(p.getKelas()).append("\n");
            sb.append("Jurusan: ").append(p.getJurusan()).append("\n");
            sb.append("Pembayaran: ").append(p.getPembayaran()).append("\n");
            sb.append("Jumlah: Rp ").append(String.format("%.2f", p.getJumlah())).append("\n");
            sb.append("-".repeat(80)).append("\n");
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Cetak Data Pembayaran SPP", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin logout?", 
            "Konfirmasi Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginForm();
        }
    }
    
    private boolean validateInput() {
        if (txtIdSiswa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID Siswa harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtIdSiswa.requestFocus();
            return false;
        }
        if (txtNamaSiswa.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Siswa harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtNamaSiswa.requestFocus();
            return false;
        }
        if (cmbJurusan.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Pilih Jurusan!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (cmbPembayaran.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Pilih Pembayaran!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtJumlah.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Jumlah harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtJumlah.requestFocus();
            return false;
        }
        try {
            Double.parseDouble(txtJumlah.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Jumlah harus berupa angka!", "Validasi", JOptionPane.WARNING_MESSAGE);
            txtJumlah.requestFocus();
            return false;
        }
        return true;
    }
    
    private void loadDataToForm(int row) {
        PembayaranSPP pembayaran = dataPembayaran.get(row);
        txtIdSiswa.setText(pembayaran.getIdSiswa());
        txtNamaSiswa.setText(pembayaran.getNamaSiswa());
        txtKelas.setText(pembayaran.getKelas());
        cmbJurusan.setSelectedItem(pembayaran.getJurusan());
        cmbPembayaran.setSelectedItem(pembayaran.getPembayaran());
        txtJumlah.setText(String.format("%.0f", pembayaran.getJumlah()));
    }
    
    private void addRowToTable(PembayaranSPP p) {
        Object[] row = {
            p.getId(),
            p.getIdSiswa(),
            p.getNamaSiswa(),
            p.getKelas(),
            p.getJurusan(),
            p.getPembayaran(),
            String.format("%.2f", p.getJumlah())
        };
        tableModel.addRow(row);
    }
    
    private void clearForm() {
        txtIdSiswa.setText("");
        txtNamaSiswa.setText("");
        txtKelas.setText("");
        txtJumlah.setText("");
        cmbJurusan.setSelectedIndex(0);
        cmbPembayaran.setSelectedIndex(0);
        table.clearSelection();
        selectedRow = -1;
    }
    
    private void loadDataFromDatabase() {
        dataPembayaran.clear();
        tableModel.setRowCount(0);
        
        String sql = "SELECT * FROM pembayaran_spp ORDER BY id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                PembayaranSPP p = new PembayaranSPP(
                    rs.getString("id_siswa"),
                    rs.getString("nama_siswa"),
                    rs.getString("kelas"),
                    rs.getString("jurusan"),
                    rs.getString("pembayaran"),
                    rs.getDouble("jumlah")
                );
                p.setId(rs.getInt("id"));
                dataPembayaran.add(p);
                addRowToTable(p);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean saveToDatabase(PembayaranSPP p) {
        String sql = "INSERT INTO pembayaran_spp (id_siswa, nama_siswa, kelas, jurusan, pembayaran, jumlah) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, p.getIdSiswa());
            pstmt.setString(2, p.getNamaSiswa());
            pstmt.setString(3, p.getKelas());
            pstmt.setString(4, p.getJurusan());
            pstmt.setString(5, p.getPembayaran());
            pstmt.setDouble(6, p.getJumlah());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private boolean updateToDatabase(PembayaranSPP p) {
        String sql = "UPDATE pembayaran_spp SET nama_siswa=?, kelas=?, jurusan=?, pembayaran=?, jumlah=? " +
                     "WHERE id_siswa=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, p.getNamaSiswa());
            pstmt.setString(2, p.getKelas());
            pstmt.setString(3, p.getJurusan());
            pstmt.setString(4, p.getPembayaran());
            pstmt.setDouble(5, p.getJumlah());
            pstmt.setString(6, p.getIdSiswa());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error updating data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private boolean deleteFromDatabase(String idSiswa) {
        String sql = "DELETE FROM pembayaran_spp WHERE id_siswa=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, idSiswa);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error deleting data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
