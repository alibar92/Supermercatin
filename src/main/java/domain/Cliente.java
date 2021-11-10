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
public class Cliente {
    
    private String dni, nombre, apellidos, email, idWallet; 
    private int edad, saldoPuntos;
    private double saldoEuro;

    //constructores por defecto, completo con los saldos en cero por defecto y completo
    public Cliente() {
        //al crear el objeto los saldos están en 0 por defecto
        this.saldoPuntos = 0;
        this.saldoEuro = 0;
    }

    public Cliente(String dni, String nombre, String apellido, int edad, String email, String idWallet) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellido;
        this.edad = edad;
        this.email = email;
        //una vez puestos los demás atributos, se asigna un id e-Wallet
        //pero sin este id el cliente no existe (es la PK)
        this.idWallet = idWallet;
        //al crear el objeto los saldos están en 0 por defecto
        this.saldoPuntos = 0;
        this.saldoEuro = 0;
    }

    public Cliente(String dni, String nombre, String apellido, int edad, String email, String idWallet, double saldo, int puntos) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellido;
        this.edad = edad;
        this.email = email;
        this.idWallet = idWallet;
        this.saldoEuro = saldo;
        this.saldoPuntos = puntos;
    }
    
    //getters y setters

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellidos;
    }

    public void setApellido(String apellido) {
        this.apellidos = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdWallet() {
        return idWallet;
    }

    public void setIdWallet(String idWallet) {
        this.idWallet = idWallet;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public int getSaldoPuntos() {
        return saldoPuntos;
    }

    public void setSaldoPuntos(int puntos) {
        this.saldoPuntos = puntos;
    }

    public double getSaldoEuro() {
        return saldoEuro;
    }

    public void setSaldoEuro(double saldo) {
        this.saldoEuro = saldo;
    }
    
    //met toString para mostrar la info
    @Override
    public String toString() {
        return "\n-CLIENTE-" + "\nID e-WALLET: " + idWallet + "\nDNI: " + dni + 
                "\nNOMBRE: " + nombre + "\nAPELLIDOS: " + apellidos +
                "\nEDAD: " + edad + "\nE-MAIL: " + email +
                "\nSALDO: " + saldoEuro + " €" + "\nPUNTOS: " + saldoPuntos +"\n";
    }
}
