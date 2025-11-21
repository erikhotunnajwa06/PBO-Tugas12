/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tugas12;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author ASUS
 */
@Entity
@Table(name = "sembako")
@NamedQueries({
    @NamedQuery(name = "Sembako.findAll", query = "SELECT s FROM Sembako s"),
    @NamedQuery(name = "Sembako.findByKodeBarang", query = "SELECT s FROM Sembako s WHERE s.kodeBarang = :kodeBarang"),
    @NamedQuery(name = "Sembako.findByNamaBarang", query = "SELECT s FROM Sembako s WHERE s.namaBarang = :namaBarang"),
    @NamedQuery(name = "Sembako.findByQuantity", query = "SELECT s FROM Sembako s WHERE s.quantity = :quantity"),
    @NamedQuery(name = "Sembako.findByHargaBarang", query = "SELECT s FROM Sembako s WHERE s.hargaBarang = :hargaBarang")})
public class Sembako implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "kode_barang")
    private String kodeBarang;
    @Column(name = "nama_barang")
    private String namaBarang;
    @Column(name = "quantity")
    private String quantity;
    @Column(name = "harga_barang")
    private String hargaBarang;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "kodeBarang")
    private Collection<Terjual> transaksiCollection;

    public Sembako() {
    }

    public Sembako(String kodeBarang) {
        this.kodeBarang = kodeBarang;
    }

    public String getKodeBarang() {
        return kodeBarang;
    }

    public void setKodeBarang(String kodeBarang) {
        this.kodeBarang = kodeBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getHargaBarang() {
        return hargaBarang;
    }

    public void setHargaBarang(String hargaBarang) {
        this.hargaBarang = hargaBarang;
    }

    public Collection<Terjual> getTransaksiCollection() {
        return transaksiCollection;
    }

    public void setTransaksiCollection(Collection<Terjual> transaksiCollection) {
        this.transaksiCollection = transaksiCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (kodeBarang != null ? kodeBarang.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sembako)) {
            return false;
        }
        Sembako other = (Sembako) object;
        if ((this.kodeBarang == null && other.kodeBarang != null) || (this.kodeBarang != null && !this.kodeBarang.equals(other.kodeBarang))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Tugas10.Sembako[ kodeBarang=" + kodeBarang + " ]";
    }
    
}
