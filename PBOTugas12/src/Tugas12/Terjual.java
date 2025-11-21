/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tugas12;

import java.io.Serializable;
import java.sql.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author ASUS
 */
@Entity
@Table(name = "terjual")
@NamedQueries({
    @NamedQuery(name = "Terjual.findAll", query = "SELECT t FROM Terjual t"),
    @NamedQuery(name = "Terjual.findByIdTerjual", query = "SELECT t FROM Terjual t WHERE t.idTerjual = :idTerjual"),
    @NamedQuery(name = "Terjual.findByTanggal", query = "SELECT t FROM Terjual t WHERE t.tanggal = :tanggal"),
    @NamedQuery(name = "Terjual.findByJumlah", query = "SELECT t FROM Terjual t WHERE t.jumlah = :jumlah")})
public class Terjual implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_terjual")
    private String idTerjual;
    @Basic(optional = false)
    @Column(name = "tanggal")
    private Date tanggal;
    @Basic(optional = false)
    @Column(name = "jumlah")
    private int jumlah;
    @JoinColumn(name = "kode_barang", referencedColumnName = "kode_barang")
    @ManyToOne(optional = false)
    private Sembako kodeBarang;

    public Terjual() {
    }

    public Terjual(String idTerjual) {
        this.idTerjual = idTerjual;
    }

    public Terjual(String idTerjual, Date tanggal, int jumlah) {
        this.idTerjual = idTerjual;
        this.tanggal = tanggal;
        this.jumlah = jumlah;
    }

    public String getIdTerjual() {
        return idTerjual;
    }

    public void setIdTerjual(String idTerjual) {
        this.idTerjual = idTerjual;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public Sembako getKodeBarang() {
        return kodeBarang;
    }

    public void setKodeBarang(Sembako kodeBarang) {
        this.kodeBarang = kodeBarang;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTerjual != null ? idTerjual.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Terjual)) {
            return false;
        }
        Terjual other = (Terjual) object;
        if ((this.idTerjual == null && other.idTerjual != null) || (this.idTerjual != null && !this.idTerjual.equals(other.idTerjual))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Tugas10.Terjual[ idTerjual=" + idTerjual + " ]";
    }
    
}
