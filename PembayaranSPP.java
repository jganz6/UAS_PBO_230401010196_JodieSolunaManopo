public class PembayaranSPP {
    private int id;
    private String idSiswa;
    private String namaSiswa;
    private String kelas;
    private String jurusan;
    private String pembayaran;
    private double jumlah;
    
    public PembayaranSPP(String idSiswa, String namaSiswa, String kelas, String jurusan, 
                         String pembayaran, double jumlah) {
        this.idSiswa = idSiswa;
        this.namaSiswa = namaSiswa;
        this.kelas = kelas;
        this.jurusan = jurusan;
        this.pembayaran = pembayaran;
        this.jumlah = jumlah;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getIdSiswa() { return idSiswa; }
    public void setIdSiswa(String idSiswa) { this.idSiswa = idSiswa; }
    
    public String getNamaSiswa() { return namaSiswa; }
    public void setNamaSiswa(String namaSiswa) { this.namaSiswa = namaSiswa; }
    
    public String getKelas() { return kelas; }
    public void setKelas(String kelas) { this.kelas = kelas; }
    
    public String getJurusan() { return jurusan; }
    public void setJurusan(String jurusan) { this.jurusan = jurusan; }
    
    public String getPembayaran() { return pembayaran; }
    public void setPembayaran(String pembayaran) { this.pembayaran = pembayaran; }
    
    public double getJumlah() { return jumlah; }
    public void setJumlah(double jumlah) { this.jumlah = jumlah; }
}
