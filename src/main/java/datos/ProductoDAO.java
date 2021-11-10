
package datos;

import static datos.Conexion.getConnection;
import domain.Cliente;
import domain.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author barto
 */
public class ProductoDAO {

    //nuevo atributo para las transacciones
    private Connection conexionTransaccional;
    
    //constructor para crear la conexiones interna a la clase 
    public ProductoDAO() {
    }
    //contructor para crear la conexion externa a la clase
    public ProductoDAO(Connection conexionTransaccional) {
              this.conexionTransaccional = conexionTransaccional;
    }
    
    String codProductoSc;
    int puntosSc;
    Scanner tcl = new Scanner(System.in);
    
    //definimos las queries para select, insert, update y delete
    private static final String SQL_SELECT = "SELECT * FROM producto";
    private static final String SQL_UPDATE = "UPDATE producto SET puntosProducto = ?"
            + " WHERE codProducto = ?";
    //queries no pedidas por el ejercicio pero que sería oportuno implementar
    //private static final String SQL_INSERT = "INSERT INTO producto (codProducto, precio, "
    //        + "puntosProducto, numUnidades) VALUES(?, ?, ?, ?)";
    //private static final String SQL_DELETE = "DELETE FROM producto WHERE codProducto = ?";
     //sentencias para modificar saldo y/o puntos
    private static final String SQL_UPDATE_SALDOS_CLIENTE = "UPDATE cliente SET saldo = ?, puntos = ?"
            + " WHERE idWallet = ?";
    private static final String SQL_UPDATE_PUNTOS_CLIENTE = "UPDATE cliente SET puntos = ? WHERE idWallet = ?";
    private static final String SQL_UPDATE_UNIDADES = "UPDATE producto SET numUnidades = ? WHERE codProducto = ?";
    
    /**
     * Método para devolver las filas de la BD de la tabla producto guardandolos en un ArrayList
     * @return ArrayList. Devuelve la lista de productos de la BD
     * @throws java.sql.SQLException 
    **/
    public List<Producto> seleccionar() throws SQLException {
        Connection cnc = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        Producto producto;
        List<Producto> listaProductos = new ArrayList();
        
        try { //se instancian conexion, query y resultado
            cnc = getConnection();
            stm = cnc.prepareStatement(SQL_SELECT);
            rs = stm.executeQuery();

            while (rs.next()) {  //hasta que hay mas obj producto (filas) en la BD, seguimos en el while
                //se le asigna a los atributos del obj producto los valores de la BD
                String codProducto = rs.getString("codProducto");
                double precio = rs.getDouble("precio");
                int puntosProducto = rs.getInt("puntosProducto");
                int numUnidades = rs.getInt("numUnidades");
                
                producto = new Producto(codProducto, precio, puntosProducto, numUnidades);
                listaProductos.add(producto); //se agrega el producto a la lista
            }
        }catch (SQLException ex) {
                System.out.println(ex.getMessage());
        }
        finally {
            Conexion.close(rs);
            Conexion.close(stm);
            Conexion.close(cnc);
        }
        //muestro la lista seleccionada
        System.out.println("\n*********LISTA DE PRODUCTOS**********\n" + listaProductos);
        return listaProductos; //nos devuelve la lista de productos
    }
    
    /**
     * Método para buscar los productos por codProducto dentro de la BD
     * @return objeto Producto que es el producto buscado por teclado
     * @throws SQLException 
    **/
    public Producto buscarProducto() throws SQLException {
        Producto productoBuscado = null;
        boolean productoEncontrado = false;
                
        System.out.println("\nSeleccionar el producto para completar la operación.");
        //lista de ClientesDAO a recorrer
        List<Producto> listaProductos = seleccionar();
        System.out.println("\nIntroduce su CODIGO:");
        
        codProductoSc = tcl.nextLine(); //se coge el codigo
        //y se busca en la lista productoDAO
        for (Producto producto: listaProductos) {
            if(codProductoSc.equalsIgnoreCase(producto.getCodProducto())) {
                productoBuscado = producto; //se ha encontrado el producto y se le asignan sus atributos
                productoEncontrado = true; //se ha encontrado el producto
            }
        }
        if (!productoEncontrado) { //si no se ha encontrado el producto
            System.out.println("ERROR! Producto no encontrado.");
        }
        return productoBuscado;
    }
    
    /**
     * Método para modificar los puntos de un producto ya presente en la BD con el valor pasados por teclado
     * @param productoBuscado obtenido por el método buscarProducto()
     * @return int registros variable de llamada a la ejecución del update
     * @throws SQLException 
    **/
    public int modificarPuntos(Producto productoBuscado) throws SQLException {
        
        int registros = 0;
        
        if(productoBuscado != null) { //si el producto buscado se ha encontrado
            //se settean los puntos del producto
            System.out.println("NUEVOS PUNTOS:");
            puntosSc = tcl.nextInt();
            productoBuscado.setPuntosProducto(puntosSc);

            Connection cnc = null;
            PreparedStatement stm = null;
            try {
                cnc = this.conexionTransaccional !=null ? this.conexionTransaccional :Conexion.getConnection();
                stm = cnc.prepareStatement(SQL_UPDATE);
                //se settean los datos de la query con los del obj cliente elegido por tcl
                stm.setInt(1, productoBuscado.getPuntosProducto());
                stm.setString(2, productoBuscado.getCodProducto());

                registros = stm.executeUpdate(); //ejecucion del update

            } finally {
                try {
                    Conexion.close(stm);
                    if (this.conexionTransaccional==null){
                    Conexion.close(cnc);}
                } catch (SQLException ex) {
                    ex.printStackTrace(System.out);
                }
            }
            //se muestra el producto buscado actualizado 
            System.out.println("\n*****PUNTOS PRODUCTO ACTUALIZADOS*****\n" + productoBuscado);
        } else {
            System.out.println("PRODUCTO NO ACTUALIZADO!");
        }
        return registros;
    }
    
    /**
     * Método para que el cliente compre un producto elegido de la tabla producto
     * @return int registros variable de llamada a la ejecución del update
     * @throws java.sql.SQLException
    **/
    public int comprarProducto() throws SQLException {
        int opc, registros = 0;
        Connection cnc = null;
        PreparedStatement stm = null;
        //se crea el ClienteDAO para acceder a sus metodos
        ClienteDAO clienteBuscado = new ClienteDAO();
        Cliente comprador = clienteBuscado.buscarCliente();
        Producto productoCompra = null;
        
        if (comprador != null ) { //si el comprador no es nulo
            //seguimos con el metodo y buscamos el producto a comprar
            productoCompra = buscarProducto();
            //si el producto se ha encontraro (no es nulo), seguimos con el metodo
            if(productoCompra != null) {
                System.out.println("\n1)Compra en euros\n2)Compra por puntos");

                do { //do-while hasta que no se eliga como opcion 1 o 2
                    opc = tcl.nextInt();
                    tcl.nextLine();
                    switch (opc) {
                        case 1:
                            //si el saldo en euro del comprador es suficiente, se ejecuta la compra
                            if(comprador.getSaldoEuro() >= productoCompra.getPrecio()) {
                                //se settea el saldo en euro del cliente, restandole el precio del producto
                                comprador.setSaldoEuro(comprador.getSaldoEuro()-productoCompra.getPrecio());
                                //se settea el saldo en puntos del cliente, sumandole los puntos del producto
                                comprador.setSaldoPuntos(comprador.getSaldoPuntos()+productoCompra.getPuntosProducto());
                                //se resta una unidad de producto al stock
                                productoCompra.setNumUnidades(productoCompra.getNumUnidades()-1);

                                //una vez seteados los atributos, ejecutamos las queries de la BD
                                try {
                                    cnc = this.conexionTransaccional !=null ? this.conexionTransaccional :Conexion.getConnection();
                                    
                                    stm = cnc.prepareStatement(SQL_UPDATE_SALDOS_CLIENTE);                               
                                    //se settean los datos de la query con los del obj cliente comprador
                                    stm.setDouble(1, comprador.getSaldoEuro());
                                    stm.setInt(2, comprador.getSaldoPuntos());
                                    stm.setString(3, comprador.getIdWallet());
                                    registros = stm.executeUpdate(); //ejecucion del update

                                    stm = cnc.prepareStatement(SQL_UPDATE_UNIDADES);
                                    //se settean los datos de la query con los del obj producto comprado
                                    stm.setInt(1, productoCompra.getNumUnidades());
                                    stm.setString(2, productoCompra.getCodProducto());
                                    registros = stm.executeUpdate(); //ejecucion del update
                                    System.out.println("COMPRA REALIZADA CON EXITO.");
                                    
                                } finally {                                   
                                    try {
                                        Conexion.close(stm);
                                        if (this.conexionTransaccional==null){
                                        Conexion.close(cnc);}
                                    } catch (SQLException ex) {
                                        ex.printStackTrace(System.out);
                                    }
                                }
                            } else { //sino salta el error
                                    System.out.println("ERROR! SALDO EURO INSUFICIENTE.");
                            }
                            break;

                        case 2:
                            //si el saldo en puntos del comprador es suficiente
                            //y si la compra supera los 5 euros, se ejecuta la compra
                            if(comprador.getSaldoPuntos() >= productoCompra.getPuntosProducto()
                                    && productoCompra.getPrecio() > 5) {
                                //se settea el saldo en puntos del cliente, restandole los puntos del producto
                                comprador.setSaldoPuntos(comprador.getSaldoPuntos()- productoCompra.getPuntosProducto());
                                productoCompra.setNumUnidades(productoCompra.getNumUnidades()-1);
                                try {
                                    cnc = this.conexionTransaccional !=null ? this.conexionTransaccional :Conexion.getConnection();
                                    stm = cnc.prepareStatement(SQL_UPDATE_PUNTOS_CLIENTE);
                                    //se settean los datos de la query con los del obj cliente comprador
                                    stm.setInt(1, comprador.getSaldoPuntos());
                                    stm.setString(2, comprador.getIdWallet());
                                    
                                    //segunda transaccion comun a las dos operaciones (compra con puntos o euro)
                                    stm = cnc.prepareStatement(SQL_UPDATE_UNIDADES);
                                    //se settean los datos de la query con los del obj producto comprado
                                    stm.setInt(1, productoCompra.getNumUnidades());
                                    stm.setString(2, productoCompra.getCodProducto());

                                    registros = stm.executeUpdate(); //ejecucion del update
                                    System.out.println("COMPRA REALIZADA CON EXITO.");
                                
                                } finally {
                                    try {
                                        Conexion.close(stm);
                                        if (this.conexionTransaccional==null){
                                            Conexion.close(cnc);
                                        }
                                    } catch (SQLException ex) {
                                        ex.printStackTrace(System.out);
                                    }
                                }
                            } else { //sino salta el error
                                System.out.println("ERROR! SALDO PUNTOS INSUFICIENTE.");
                            }
                            break;

                        default:
                            opc = 0;
                            System.out.println("ERROR! OPCIÓN NO VALIDA. DIGITE 1 O 2.");
                            break;
                    }
                } while(opc == 0);
            } 
        } 
        //si se ha efectuado el update, regisrro nos devuelve 1 
        if (registros == 1){
            
            System.out.println("\n*****DATOS CLIENTE ACTUALIZADOS DESPUES DE LA COMPRA******\n" + comprador);
            //se muestra el cliente buscado actualizado 
            System.out.println("\n*****DATOS PRODUCTO ACTUALIZADOS DESPUES DE LA COMPRA******\n" + productoCompra);
        } else {
            System.out.println("NO SE HA EFECTUADO LA COMPRA!");
        }
        return registros;
    }
    
    /**
     * Método para que el cliente devuelva un producto
     * @return int registros variable de llamada a la ejecución del update
     * @throws java.sql.SQLException
    **/
    public int devolverProducto() throws SQLException {
        int registros = 0;
        Connection cnc = null;
        PreparedStatement stm = null;
        //se crea el ClienteDAO para acceder a sus metodos
        ClienteDAO clienteBuscado = new ClienteDAO();
        Cliente clienteDevolucion = clienteBuscado.buscarCliente();
        Producto productoDevolver = null;
        //se busca el cliente que devuelve y el producto a devolver
        if (clienteDevolucion != null ) { //si el comprador no es nulo
            //seguimos con el metodo y buscamos el producto a devolver
            productoDevolver = buscarProducto();
            //si el producto se ha encontraro (no es nulo), seguimos con el metodo
            if(productoDevolver != null) {
                //si el saldo en puntos queda mayor de 5, se puede devover el producto
               if((clienteDevolucion.getSaldoPuntos()- productoDevolver.getPuntosProducto()) > 5){
                   //se devuelve al cliente el precio del producto que devuelve
                   clienteDevolucion.setSaldoEuro(clienteDevolucion.getSaldoEuro()+productoDevolver.getPrecio());
                   //se le restan los puntos obtenidos por la compra del producto
                   clienteDevolucion.setSaldoPuntos(clienteDevolucion.getSaldoPuntos()- productoDevolver.getPuntosProducto());
                   //se suma la cantidad de un producto al stock
                   productoDevolver.setNumUnidades(productoDevolver.getNumUnidades()+1);
                   try {
                        cnc = this.conexionTransaccional !=null ? this.conexionTransaccional :Conexion.getConnection();
                        
                        //primera transaccion
                        stm = cnc.prepareStatement(SQL_UPDATE_SALDOS_CLIENTE);                               
                        stm.setDouble(1, clienteDevolucion.getSaldoEuro());
                        stm.setInt(2, clienteDevolucion.getSaldoPuntos());
                        stm.setString(3, clienteDevolucion.getIdWallet());
                        
                        //segunda transaccion
                        stm = cnc.prepareStatement(SQL_UPDATE_PUNTOS_CLIENTE);
                        stm.setInt(1, clienteDevolucion.getSaldoPuntos());
                        stm.setString(2, clienteDevolucion.getIdWallet());
                        
                        //tercera transaccion
                        stm = cnc.prepareStatement(SQL_UPDATE_UNIDADES);
                        stm.setInt(1, productoDevolver.getNumUnidades());
                        stm.setString(2, productoDevolver.getCodProducto());
                        
                        registros = stm.executeUpdate(); //ejecucion del update

                    } finally {
                        try {
                            Conexion.close(stm);
                            if (this.conexionTransaccional==null){
                                Conexion.close(cnc);
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace(System.out);
                        }
                    }
                } else { //sino, quedarían menos de 5 puntos, salta el error
                   System.out.println("ERROR! IMPOSIBLE DEVOLVER EL PRODUCTO: saldo puntos insuficiente.");
                }
            }
        }
        //si se ha efectuado el update, regisrro nos devuelve 1 
        if (registros == 1){
            System.out.println("\n*****DATOS CLIENTE ACTUALIZADOS DESPUES DE LA DEVOLUCIÓN******\n" + clienteDevolucion);
            //se muestra el cliente buscado actualizado 
            System.out.println("\n*****DATOS PRODUCTO ACTUALIZADOS DESPUES DE LA DEVOLUCIÓN******\n" + productoDevolver);
        } else {
            System.out.println("NO SE HA EFECTUADO LA DEVOLUCIÓN!");
        }
        //si los objetos Cliente o Producto pasados no son nulos se sigue con el metodo
        return registros;
    }
    
}
