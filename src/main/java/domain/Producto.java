/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

/**
 *
 * @author barto
 */
public class Producto {

    private String codProducto;
    private double precio;
    private int puntosProducto, numUnidades;

    //constructores por defecto y sin dniCliente (cuando se crea el producto está en stock)
    public Producto() {
    }

    public Producto(String codProducto, double precio, int puntosProducto, int numeroUnidades) {
        this.codProducto = codProducto;
        this.precio = precio;
        this.puntosProducto = puntosProducto;
        this.numUnidades = numeroUnidades;
    }
    
    //getters y setters

    public String getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(String codProducto) {
        this.codProducto = codProducto;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getPuntosProducto() {
        return puntosProducto;
    }

    public void setPuntosProducto(int puntosProducto) {
        this.puntosProducto = puntosProducto;
    }

    public int getNumUnidades() {
        return numUnidades;
    }

    public void setNumUnidades(int numeroUnidades) {
        this.numUnidades = numeroUnidades;
    }
    
    //met toString para mostrar la info
    @Override
    public String toString() {
        return "\n-PRODUCTO-" + "\nCODIGO: " + codProducto +
                "\nPRECIO: " + precio + " €" + "\nPUNTOS: " + puntosProducto 
                + "\nNºUNIDADES: " + numUnidades +"\n";
    }
    
}
