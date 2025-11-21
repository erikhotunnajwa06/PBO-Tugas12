/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Tugas12;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author ASUS
 */
public class DataSembako extends javax.swing.JFrame {
    
    Connection conn;
    Statement stmt;
    PreparedStatement pstmt = null;

    String DB_URL = "jdbc:postgresql://localhost:5432/PBO_M04";
    String USER = "postgres";
    String PASS = "Najwa040806";
    String driver = "org.postgresql.Driver"; // PostgreSQL driver

    private String selectedKodeBarang;
    private String selectedNamaBarang;
    private String selectedQuantity;
    private String selectedHargaBarang;
    /**
     * Creates new form DataSembako
     */
    public DataSembako() {
        initComponents();
        showTable();
        setLocationRelativeTo(null);
    }
    
    private void resetSelectedData() {
        selectedKodeBarang = null;
        selectedNamaBarang = null;
        selectedQuantity = null;
        selectedHargaBarang = null;
    }
    
    private void downloadToCSV(JTable table, String filename) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan sebagai CSV");
        fileChooser.setSelectedFile(new File(filename));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // Pastikan ekstensi .csv
            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (FileWriter writer = new FileWriter(fileToSave)) {
                // Tulis header
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int columnCount = model.getColumnCount();

                // Header
                for (int i = 0; i < columnCount; i++) {
                    writer.write(model.getColumnName(i));
                    if (i < columnCount - 1) {
                        writer.write(";");
                    }
                }
                writer.write("\n");

                // Data
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < columnCount; j++) {
                        Object value = model.getValueAt(i, j);
                        writer.write(value != null ? value.toString() : "");
                        if (j < columnCount - 1) {
                            writer.write(";");
                        }
                    }
                    writer.write("\n");
                }

                JOptionPane.showMessageDialog(this, "Data berhasil didownload ke: " + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saat mengdownload data: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearSembako() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus SEMUA data sembako dan data penjualan?\n"
                + "Data penjualan ikut terhapus karena berikatan dengan data sembako.\n"
                + "Tindakan ini tidak dapat dibatalkan!",
                "Konfirmasi Hapus Semua Data",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("PBOTugas12PU");
            EntityManager em = emf.createEntityManager();

            try {
                em.getTransaction().begin();

                // 1. Hapus semua data dari tabel Terjual terlebih dahulu
                Query deleteTerjual = em.createQuery("DELETE FROM Terjual");
                int deletedTerjualCount = deleteTerjual.executeUpdate();

                // 2. Hapus semua data dari tabel Sembako
                Query deleteSembako = em.createQuery("DELETE FROM Sembako");
                int deletedSembakoCount = deleteSembako.executeUpdate();

                em.getTransaction().commit();

                JOptionPane.showMessageDialog(this,
                        "Berhasil menghapus " + deletedSembakoCount + " data sembako dan "
                        + deletedTerjualCount + " data penjualan.",
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);

                showTable(); 

            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                JOptionPane.showMessageDialog(this,
                        "Error saat menghapus data: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                if (em != null && em.isOpen()) {
                    em.close();
                }
                if (emf != null && emf.isOpen()) {
                    emf.close();
                }
            }
        }
    }

    private void clearTerjual() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus SEMUA data penjualan?\nTindakan ini tidak dapat dibatalkan!",
                "Konfirmasi Hapus Semua Data",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("PBOTugas12PU");
            EntityManager em = emf.createEntityManager();

            try {
                em.getTransaction().begin();

                // Hapus semua data dari tabel Terjual
                Query deleteQuery = em.createQuery("DELETE FROM Terjual");
                int deletedCount = deleteQuery.executeUpdate();

                em.getTransaction().commit();

                JOptionPane.showMessageDialog(this,
                        "Berhasil menghapus " + deletedCount + " data penjualan.",
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);

                showTable(); 

            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                JOptionPane.showMessageDialog(this,
                        "Error saat menghapus data: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                if (em != null && em.isOpen()) {
                    em.close();
                }
                if (emf != null && emf.isOpen()) {
                    emf.close();
                }
            }
        }
    }

    public void showTable() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PBOTugas12PU");
        EntityManager em = emf.createEntityManager();

        try {
            Query qSembako = em.createNamedQuery("Sembako.findAll");
            List<Sembako> hasilSembako = qSembako.getResultList();
            DefaultTableModel tbsb = new DefaultTableModel(
                    new String[]{"Kode Barang", "Nama Barang", "Quantity", "Harga Barang"}, 0);
            for (Sembako data : hasilSembako) {
                Object[] baris = {
                    data.getKodeBarang(),
                    data.getNamaBarang(),
                    data.getQuantity(),
                    data.getHargaBarang()
                };
                tbsb.addRow(baris);
            }
            jTable1.setModel(tbsb);

            Query qTerjual = em.createNamedQuery("Terjual.findAll");
            List<Terjual> hasilTerjual = qTerjual.getResultList();
            DefaultTableModel tbtr = new DefaultTableModel(
                    new String[]{"ID Terjual", "Nama Barang", "Tanggal", "Jumlah"}, 0);
            for (Terjual data : hasilTerjual) {
                Object[] baris = {
                    data.getIdTerjual(),
                    data.getKodeBarang().getNamaBarang(), // Ambil kode barang dari relasi
                    data.getTanggal(),
                    data.getJumlah()
                };
                tbtr.addRow(baris);
            }
            jTable2.setModel(tbtr);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }
    
    private String selectedIdTerjual;
    private String selectedTanggal;
    private String selectedJumlah;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(224, 224, 248));

        jLabel1.setFont(new java.awt.Font("Algerian", 1, 36)); // NOI18N
        jLabel1.setText("DATA SEMBAKO");

        jPanel3.setBackground(new java.awt.Color(213, 211, 249));

        jTable1.setBackground(new java.awt.Color(166, 166, 231));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Kode Barang", "Nama Barang", "Quantity", "Harga Barang"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton1.setBackground(new java.awt.Color(153, 255, 153));
        jButton1.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton1.setText("Insert");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(204, 255, 153));
        jButton2.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton2.setText("Update");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(255, 153, 153));
        jButton3.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton3.setText("Delete");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(153, 255, 255));
        jButton6.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton6.setText("Upload");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(153, 153, 255));
        jButton4.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton4.setText("Cetak");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(255, 0, 51));
        jButton5.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton5.setText("EXIT");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton15.setBackground(new java.awt.Color(201, 31, 210));
        jButton15.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton15.setText("DOWNLOAD");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton16.setBackground(new java.awt.Color(255, 102, 102));
        jButton16.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton16.setText("CLEAR");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(31, 31, 31)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(34, 34, 34)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(33, 33, 33)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(jButton1)
                    .addComponent(jButton16))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton4)
                    .addComponent(jButton6)
                    .addComponent(jButton15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(22, 22, 22))
        );

        jTabbedPane2.addTab("List Harga", jPanel3);

        jPanel2.setBackground(new java.awt.Color(213, 211, 249));

        jTable2.setBackground(new java.awt.Color(166, 166, 231));
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id Penjualan", "Kode Barang", "Tanggal", "Jumlah"
            }
        ));
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        jButton7.setBackground(new java.awt.Color(153, 255, 153));
        jButton7.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton7.setText("Insert");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(204, 255, 153));
        jButton8.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton8.setText("Update");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setBackground(new java.awt.Color(255, 153, 153));
        jButton9.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton9.setText("Delete");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setBackground(new java.awt.Color(255, 0, 51));
        jButton10.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton10.setText("EXIT");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setBackground(new java.awt.Color(153, 153, 255));
        jButton11.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton11.setText("Cetak");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setBackground(new java.awt.Color(153, 255, 255));
        jButton12.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton12.setText("Upload");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton14.setBackground(new java.awt.Color(201, 31, 210));
        jButton14.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton14.setText("DOWNLOAD");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        jButton13.setBackground(new java.awt.Color(255, 102, 102));
        jButton13.setFont(new java.awt.Font("Stencil", 0, 12)); // NOI18N
        jButton13.setText("CLEAR");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8))
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(35, 35, 35)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                    .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8)
                    .addComponent(jButton9)
                    .addComponent(jButton7)
                    .addComponent(jButton13))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton10)
                    .addComponent(jButton11)
                    .addComponent(jButton12)
                    .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );

        jTabbedPane2.addTab("Penjualan", jPanel2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(96, 96, 96)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        InsertDlg dialog = new InsertDlg(new javax.swing.JFrame(), true);
        dialog.setVisible(true);
        showTable();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (selectedKodeBarang == null || selectedKodeBarang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diupdate "
                    + "dari tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        UpdateDlg dialog = new UpdateDlg(new javax.swing.JFrame(), true,
                selectedKodeBarang, selectedNamaBarang,
                selectedQuantity, selectedHargaBarang);
        dialog.setVisible(true);
        showTable();
        // Reset selected data setelah dialog ditutup
        resetSelectedData();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (selectedKodeBarang == null || selectedKodeBarang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus "
                + "dari tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DeleteDlg dialog = new DeleteDlg(new javax.swing.JFrame(), true,
            selectedKodeBarang, selectedNamaBarang, selectedQuantity, selectedHargaBarang);
        dialog.setVisible(true);
        showTable();
        // Reset selected data setelah dialog ditutup
        resetSelectedData();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File filePilihan = jfc.getSelectedFile();
            System.out.println("yang dipilih : " + filePilihan.getAbsolutePath());

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("PBOTugas12PU");
            EntityManager em = emf.createEntityManager();
            try (BufferedReader br = new BufferedReader(new FileReader(filePilihan))) {
                String baris;
                String pemisah = ";";
                boolean isFirstLine = true;

                while ((baris = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }                   
                    if (baris.trim().isEmpty()) {
                        continue;
                    }
                    String[] data = baris.split(pemisah);
                    if (data.length < 4) {
//                        System.out.println("Baris tidak lengkap, dilewati: " + baris);
                        continue;
                    }
                    String kode_barang = data[0].trim();
                    String nama_barang = data[1].trim();
                    String quantity = data[2].trim();
                    String harga_barang = data[3].trim();
                    if (!kode_barang.isEmpty() && !nama_barang.isEmpty() && !quantity.isEmpty()
                            && !harga_barang.isEmpty()) {
                        em.getTransaction().begin();

                        Sembako sb = new Sembako();
                        sb.setKodeBarang(kode_barang);
                        sb.setNamaBarang(nama_barang);
                        sb.setQuantity(quantity);
                        sb.setHargaBarang(harga_barang);

                        em.persist(sb);
                        em.getTransaction().commit();
//                        System.out.println("Berhasil input: " + kode_barang + " - " + nama_barang);
//                    } else {
//                        System.out.println("Data tidak valid (ada kolom kosong): " + baris);
                    }
                }
                JOptionPane.showMessageDialog(null, "Semua data dari file berhasil diinput!");
                showTable();
            } catch (Exception ex) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                Logger.getLogger(DataSembako.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + ex.getMessage());
            } finally {
                if (em.isOpen()) {
                    em.close();
                }
                if (emf.isOpen()) {
                    emf.close();
                }
            }
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        JasperReport reports;

        String path = ".\\\\src\\\\Tugas12\\\\laporan.jasper";
        String imagePath = "/Tugas12/coffee.jpg";
        try {
            try {
                Class.forName(driver);
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(DataSembako.class.getName()).log(Level.SEVERE, null, ex);
            }
            reports = (JasperReport) JRLoader.loadObjectFromFile(path);
            JasperPrint jprint = JasperFillManager.fillReport(path, null, conn);
            JasperViewer jviewer = new JasperViewer(jprint, false);
            jviewer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jviewer.setVisible(true);
        } catch (JRException ex) {
            Logger.getLogger(DataSembako.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int row = jTable1.getSelectedRow();
        if (row >= 0) { // Pastikan ada baris yang dipilih
            selectedKodeBarang = jTable1.getValueAt(row, 0).toString();
            selectedNamaBarang = jTable1.getValueAt(row, 1).toString();
            selectedQuantity = jTable1.getValueAt(row, 2).toString();
            selectedHargaBarang = jTable1.getValueAt(row, 3).toString();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File filePilihan = jfc.getSelectedFile();
            System.out.println("yang dipilih : " + filePilihan.getAbsolutePath());

            EntityManagerFactory emf = Persistence.createEntityManagerFactory("PBOTugas12PU");
            EntityManager em = emf.createEntityManager();

            int successCount = 0;
            int lineNumber = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(filePilihan))) {
                String baris;
                String pemisah = ";";
                boolean isFirstLine = true;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                while ((baris = br.readLine()) != null) {
                    lineNumber++;
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue; // Skip header
                    }
                    if (baris.trim().isEmpty()) {
                        continue; // Skip baris kosong
                    }

                    String[] data = baris.split(pemisah);
                    if (data.length < 4) {
                        JOptionPane.showMessageDialog(null,
                            "Baris " + lineNumber + ": Format tidak lengkap\n" + baris,
                            "Peringatan", JOptionPane.WARNING_MESSAGE);
                        continue;
                    }

                    String id_terjual = data[0].trim();
                    String kode_atau_nama = data[1].trim();
                    String tanggal_str = data[2].trim();
                    String jumlah_str = data[3].trim();
                    // Validasi data tidak kosong
                    if (id_terjual.isEmpty() || kode_atau_nama.isEmpty()
                        || tanggal_str.isEmpty() || jumlah_str.isEmpty()) {
                        JOptionPane.showMessageDialog(null,
                            "Baris " + lineNumber + ": Data tidak lengkap\n" + baris,
                            "Peringatan", JOptionPane.WARNING_MESSAGE);
                        continue;
                    }
                    try {
                        em.getTransaction().begin();
                        // Cek apakah id_terjual sudah ada
                        Terjual existing = em.find(Terjual.class, id_terjual);
                        if (existing != null) {
                            em.getTransaction().rollback();
                            JOptionPane.showMessageDialog(null,
                                "Baris " + lineNumber + ": ID Transaksi sudah ada - " + id_terjual,
                                "Peringatan", JOptionPane.WARNING_MESSAGE);
                            continue;
                        }
                        
                        // Cek apakah kode_barang ada di tabel Sembako
                        Sembako barang = em.find(Sembako.class, kode_atau_nama);
                        if (barang == null) {
                            TypedQuery<Sembako> query = em.createQuery(
                                    "SELECT s FROM Sembako s WHERE s.namaBarang = :namaBarang", Sembako.class);
                            query.setParameter("namaBarang", kode_atau_nama);
                            List<Sembako> results = query.getResultList();

                            if (!results.isEmpty()) {
                                barang = results.get(0);
                            } else {
                                em.getTransaction().rollback();
                                JOptionPane.showMessageDialog(null,
                                        "Baris " + lineNumber + ": Kode barang tidak ditemukan - " + kode_atau_nama,
                                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                                continue;
                            }
                        }
                        // Konversi tanggal ke java.sql.Date
                        java.sql.Date tanggal;
                        try {
                            java.util.Date utilDate = dateFormat.parse(tanggal_str);
                            tanggal = new java.sql.Date(utilDate.getTime());
                        } catch (ParseException e) {
                            em.getTransaction().rollback();
                            JOptionPane.showMessageDialog(null,
                                "Baris " + lineNumber + ": Format tanggal salah - " + tanggal_str
                                + "\nHarus format: YYYY-MM-DD",
                                "Peringatan", JOptionPane.WARNING_MESSAGE);
                            continue;
                        }
                        // Konversi jumlah ke int
                        int jumlah;
                        try {
                            jumlah = Integer.parseInt(jumlah_str);
                            if (jumlah <= 0) {
                                em.getTransaction().rollback();
                                JOptionPane.showMessageDialog(null,
                                    "Baris " + lineNumber + ": Jumlah harus > 0 - " + jumlah_str,
                                    "Peringatan", JOptionPane.WARNING_MESSAGE);
                                continue;
                            }
                        } catch (NumberFormatException e) {
                            em.getTransaction().rollback();
                            JOptionPane.showMessageDialog(null,
                                "Baris " + lineNumber + ": Jumlah harus angka - " + jumlah_str,
                                "Peringatan", JOptionPane.WARNING_MESSAGE);
                            continue;
                        }
                        // Buat objek Terjual dengan data yang benar
                        Terjual tr = new Terjual();
                        tr.setIdTerjual(id_terjual);
                        tr.setKodeBarang(barang);        // Set objek Sembako
                        tr.setTanggal(tanggal);          // Set java.sql.Date
                        tr.setJumlah(jumlah);            // Set int

                        em.persist(tr);
                        em.getTransaction().commit();
                        successCount++;

                    } catch (Exception ex) {
                        if (em.getTransaction().isActive()) {
                            em.getTransaction().rollback();
                        }
                        JOptionPane.showMessageDialog(null,
                            "Baris " + lineNumber + ": Error tidak terduga\n" + ex.getMessage(),
                            "Peringatan", JOptionPane.WARNING_MESSAGE);
                    }
                }
                // Tampilkan hasil upload
                if (successCount > 0) {
                    JOptionPane.showMessageDialog(null,
                        "Berhasil mengimpor " + successCount + " data transaksi!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                        "Tidak ada data yang berhasil diimpor.",
                        "Info", JOptionPane.WARNING_MESSAGE);
                }
                showTable();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                    "Terjadi kesalahan saat membaca file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(DataSembako.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (em != null && em.isOpen()) {
                    em.close();
                }
                if (emf != null && emf.isOpen()) {
                    emf.close();
                }
            }
        }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        JasperReport reports;

        String path = ".\\\\src\\\\Tugas12\\\\laporanPenjualan.jasper";
        String imagePath = "/Tugas12/coffee.jpg";
        try {
            try {
                Class.forName(driver);
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
            } catch (ClassNotFoundException | SQLException ex) {
                Logger.getLogger(DataSembako.class.getName()).log(Level.SEVERE, null, ex);
            }
            reports = (JasperReport) JRLoader.loadObjectFromFile(path);
            JasperPrint jprint = JasperFillManager.fillReport(path, null, conn);
            JasperViewer jviewer = new JasperViewer(jprint, false);
            jviewer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            jviewer.setVisible(true);
        } catch (JRException ex) {
            Logger.getLogger(DataSembako.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        if (selectedIdTerjual == null || selectedIdTerjual.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus "
                + "dari tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DeleteTrs dialog = new DeleteTrs(new javax.swing.JFrame(), true,
            selectedIdTerjual, selectedKodeBarang, selectedTanggal, selectedJumlah);
        dialog.setVisible(true);
        showTable();
        // Reset selected data setelah dialog ditutup
        resetSelectedData();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        if (selectedIdTerjual == null || selectedIdTerjual.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diupdate "
                + "dari tabel terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        UpdateTrs dialog = new UpdateTrs(new javax.swing.JFrame(), true,
            selectedIdTerjual, selectedKodeBarang, selectedTanggal, selectedJumlah);
        dialog.setVisible(true);
        showTable();
        // Reset selected data setelah dialog ditutup
        resetSelectedData();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        InsertTrs dialog = new InsertTrs(new javax.swing.JFrame(), true);
        dialog.setVisible(true);
        showTable();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        int row = jTable2.getSelectedRow();
        if (row >= 0) { // Pastikan ada baris yang dipilih
            selectedIdTerjual = jTable2.getValueAt(row, 0).toString();
            selectedTanggal = jTable2.getValueAt(row, 2).toString();
            selectedJumlah = jTable2.getValueAt(row, 3).toString();
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("PBOTugas12PU");
            EntityManager em = emf.createEntityManager();
            try {
                Terjual terjual = em.find(Terjual.class, selectedIdTerjual);
                if (terjual != null) {
                    selectedKodeBarang = terjual.getKodeBarang().getKodeBarang();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            } finally {
                if (em != null && em.isOpen()) {
                    em.close();
                }
                if (emf != null && emf.isOpen()) {
                    emf.close();
                }
            }
        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        downloadToCSV(jTable1, "data_sembako.csv");
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        downloadToCSV(jTable2, "data_penjualan.csv");
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        clearTerjual();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        clearSembako();
    }//GEN-LAST:event_jButton16ActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(DataSembako.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(DataSembako.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(DataSembako.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(DataSembako.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new DataSembako().setVisible(true);
//            }
//        });
//    }
    
    DefaultTableModel tbsb;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}