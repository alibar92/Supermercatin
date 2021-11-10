/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import datos.ClienteDAO;
import datos.Conexion;
import datos.ProductoDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author barto
 */
public class SupercomprinMain {

    public static void main(String[] args) throws SQLException {
        
        Connection conexion = Conexion.getConnection();

        //se crean los obj Cliente y Producto DAO para acceder a sus metodos
        ClienteDAO clienteDAO = new ClienteDAO(); 
        ProductoDAO productoDAO = new ProductoDAO();
        
        boolean salir = false; //para salir del bucle del menú do-while
        Scanner tcl = new Scanner(System.in);
        int opc; 

        do { //do-while con opciones menu
            try { //try-catch para control de error en la intro de datos por tcl
        
                System.out.println("\n*******SISTEMA GESTOR SUPERCOMPRIN**************");
                System.out.println("Elige la operació a realizar:\n1)Mostrar clientes\n2)Mostrar Productos"
                        + "\n3)Dar de alta a un cliente\n4)Dar de baja a un cliente\n5)Modificar cliente"
                        + "\n6)Comprar producto\n7)Devolver producto"
                        + "\n8)Modificar puntos producto\n9)Recargar saldo\n10)Salir");
                opc = tcl.nextInt();
                tcl.nextLine();
                
                switch (opc) { //switch case para llamar a met diferentes segun la opcion elegida
                    
                    case 1:
            //********SELECT para seleccionar la tabla cliente de la BD llamo al metodo que me devuelve la lista
                        clienteDAO.seleccionar();

                        break;

                    case 2:
            //*********SELECT PRODUCTO -> lo mismo para la tabla producto de la BD
                        productoDAO.seleccionar();
                        
                        break;

                    case 3:
            //*********INSERT CLIENTE (dar de alta al cliente: insertarlo)
                        try {
                            conexion = Conexion.getConnection();
                                if (conexion.getAutoCommit()) {  
                                    conexion.setAutoCommit(false); //AutoCommit se settea a false
                                }
                            clienteDAO = new ClienteDAO(conexion);
                            clienteDAO.insertar();
                            //se hace commit
                            conexion.commit();
                            //control del error si la conexion es null
                            }catch (SQLException ex) {
                                ex.printStackTrace(System.out);
                                System.out.println("Fallo en la operación de inserción. Se va a ejecutar rollback");
                                try {
                                    conexion.rollback();
                                } catch (SQLException ex1) {
                                    ex1.printStackTrace(System.out);
                            }
                        }
                        break;

                    case 4:
            //**********DELETE Cliente (dar de baja al cliente: borrarlo)    
                        try {
                            conexion = Conexion.getConnection();
                                if (conexion.getAutoCommit()) {  
                                    conexion.setAutoCommit(false); //AutoCommit se settea a false
                                }
                            clienteDAO = new ClienteDAO(conexion);
                            //como parametro del metodo de modifica se pasa el cliente obtenido por la busqueda
                            clienteDAO.eliminar(clienteDAO.buscarCliente());
                            //se hace commit
                            conexion.commit();
                         //control del error si la conexion es null
                            }catch (SQLException ex) {
                                ex.printStackTrace(System.out);
                                System.out.println("Fallo en la operación de borrado. Se va a ejecutar rollback.");
                                try {
                                    conexion.rollback();
                                } catch (SQLException ex1) {
                                    ex1.printStackTrace(System.out);
                            }
                        }
                        break;

                    case 5:
            //**********UPDATE Cliente (modificar datos)
                        try {
                            conexion = Conexion.getConnection();
                                if (conexion.getAutoCommit()) {  
                                    conexion.setAutoCommit(false); //AutoCommit se settea a false
                                }
                            clienteDAO = new ClienteDAO(conexion);
                            //como parametro del metodo de modifica se pasa el cliente obtenido por la busqueda
                            clienteDAO.modificar(clienteDAO.buscarCliente());
                            //se hace commit
                            conexion.commit();
                        
                            }catch (SQLException ex) {
                                ex.printStackTrace(System.out);
                                System.out.println("Fallo en la operación de modifica. Se va a ejecutar rollback.");
                                try {
                                    conexion.rollback();
                                } catch (SQLException ex1) {
                                    ex1.printStackTrace(System.out);
                            }
                        }
                        break;

                    case 6:
            //**********COMPRAR PRODUCTO
                        try {
                            conexion = Conexion.getConnection();
                                if (conexion.getAutoCommit()) {  
                                    conexion.setAutoCommit(false); //AutoCommit se settea a false
                                }
                            //se llama al metodo para comprar. Se necesita de un cliente y de un producto
                            productoDAO.comprarProducto();
                            //se hace commit
                            conexion.commit();
                         //control del error si la conexion es null
                            }catch (SQLException ex) {
                                System.out.println("Fallo en la operación de compra. Se va a ejecutar rollback.");
                                try {
                                    conexion.rollback();
                                } catch (SQLException ex1) {
                                    ex1.printStackTrace(System.out);
                            }
                        }        
                        break;

                    case 7:
            //**********DEVOLVER PRODUCTO 
                        try {
                            conexion = Conexion.getConnection();
                                if (conexion.getAutoCommit()) {  
                                    conexion.setAutoCommit(false); //AutoCommit se settea a false
                                }
                            productoDAO.devolverProducto();
                            //se hace commit
                            conexion.commit();
                            //control del error si la conexion es null
                            }catch (SQLException ex) {
                                System.out.println("Fallo en la operación de compra. Se va a ejecutar rollback.");
                                try {
                                    conexion.rollback();
                                } catch (SQLException ex1) {
                                    ex1.printStackTrace(System.out);
                            }
                        }   
                        break;

                    case 8:
            //*********UPDATE PUNTOS PRODUCTO (se modifican los puntos del producto)
                        try {
                            conexion = Conexion.getConnection();
                                if (conexion.getAutoCommit()) {  
                                    conexion.setAutoCommit(false); //AutoCommit se settea a false
                                }
                            //como parametro del metodo de modifica se pasa el producto obtenido por la busqueda
                            productoDAO.modificarPuntos(productoDAO.buscarProducto());
                            //se hace commit
                            conexion.commit();
                            //control del error si la conexion es null
                            }catch (SQLException ex) {
                                System.out.println("Fallo en la operación de compra. Se va a ejecutar rollback.");
                                try {
                                    conexion.rollback();
                                } catch (SQLException ex1) {
                                    ex1.printStackTrace(System.out);
                            }
                        }   
                        break;
                    
                    case 9:
            //*********UPDATE SALDO CLIENTE ingresando una cantidad de dinero para recargar el saldo
                        try {
                            conexion = Conexion.getConnection();
                                if (conexion.getAutoCommit()) {  
                                    conexion.setAutoCommit(false); //AutoCommit se settea a false
                                }
                            clienteDAO = new ClienteDAO(conexion);
                            //como parametro del metodo de modifica se pasa el cliente obtenido por la busqueda
                            clienteDAO.recargarSaldo();
                            //se hace commit
                            conexion.commit();
                            //System.out.println("Se ha hecho commit de la modifica del saldo.");
                        
                            }catch (SQLException ex) {
                                ex.printStackTrace(System.out);
                                System.out.println("Fallo en la operación de carga de saldo. Se va a ejecutar rollback.");
                                try {
                                    conexion.rollback();
                                } catch (SQLException ex1) {
                                    ex1.printStackTrace(System.out);
                            }
                        }
                        break;
                        
                    case 10:
            //******SALIR            
                        salir = true; //salir es true entonces se sale del do-while
                        break;
                        
                    default:
                        System.out.println("ERROR! Opción no valida.");
                }
            } catch(InputMismatchException e) { //Excepción de mismatch 
                System.out.println("\nERROR! TIPO DE DATO INSERTADO NO VALIDO: " + e.getMessage());
                tcl.next(); //limpiamos el buffer
            } catch (SQLException e) { //excepción SQL
                System.out.println("\n" + e.getMessage());
                tcl.next();
            }
            
        }while(!salir); //hasta que salir sea false
        
    }//fin main
        
}
