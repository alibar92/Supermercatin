
package datos;

import static datos.Conexion.getConnection;
import domain.Cliente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author barto
 */

public class ClienteDAO {
    
    //nuevo atributo para las transacciones
    private Connection conexionTransaccional;
    
    //constructor para crear la conexiones interna a la clase 
    public ClienteDAO() {
    }
    //contructor para crear la conexion externa a la clase
    public ClienteDAO(Connection conexionTransaccional) {
              this.conexionTransaccional = conexionTransaccional;
    }

    //datos pedidos por Scanner para crear un nuevo Cliente
    Scanner tcl = new Scanner(System.in);
    String dniSc, nombreSc, apellidoSc, emailSc, idWalletSc;
    int edadSc;
    double saldoESc;
    
    //definimos las queries para select, insert, update y delete
    private static final String SQL_SELECT = "SELECT * FROM cliente";
    private static final String SQL_INSERT = "INSERT INTO cliente (dni, nombre, apellido,"
            + "edad, email, idWallet, saldo, puntos) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_DELETE = "DELETE FROM cliente WHERE idWallet = ?";
    //no vamos a modificar ni el dni ni el id del Wallet
    private static final String SQL_UPDATE = "UPDATE cliente SET nombre = ?, "
        + "apellido = ?, edad = ?, email = ?  WHERE idWallet = ?";
    private static final String SQL_UPDATE_SALDOEURO = "UPDATE cliente SET saldo = ?"
            + "WHERE idWallet = ?";
   
    /**
     * Método para devolver las filas de la BD de la tabla cliente guardandolos en un ArrayList
     * @return ArrayList. Devuelve la lista de clientes de la BD
     * @throws java.sql.SQLException 
    **/
    public List<Cliente> seleccionar() throws SQLException {

        Connection cnc = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        Cliente cliente;
        List<Cliente> listaClientes = new ArrayList();

        try { //se instancian conexion, query y resultado
            cnc = getConnection();
            stm = cnc.prepareStatement(SQL_SELECT);
            rs = stm.executeQuery();

            while (rs.next()) {  //hasta que hay mas obj cliente (filas) en la BD, seguimos en el while
                //se le asigna a los atributos del obj cliente los valores de la BD
                String dni = rs.getString("dni");
                String nombre = rs.getString("nombre");
                String apellido = rs.getString("apellido");
                int edad = rs.getInt("edad");
                String email = rs.getString("email");
                String idWallet = rs.getString("idWallet");
                int saldo = rs.getInt("saldo");
                int puntos = rs.getInt("puntos");

                cliente = new Cliente(dni, nombre, apellido, edad, email, idWallet, saldo, puntos);
                listaClientes.add(cliente); //se agrega el cliente a la lista
            }
        }catch (SQLException ex) {
                System.out.println(ex.getMessage());
        }
        finally {
            Conexion.close(rs);
            Conexion.close(stm);
            Conexion.close(cnc);
        }
        //se muestra la lista selecionada
        System.out.println("\n*********LISTA DE CLIENTES ACTUALIZADA**********\n" + listaClientes);
        return listaClientes; //nos devuelve la lista de clientes
    }

    /**
     * Método para pedir datos del nuevo cliente e insertarlo en la BD
     * si la persona es menor de edad salta el error y no se inserta
     * @return int registros variable de llamada a la ejecución del update
     * @throws SQLException 
    **/
    public int insertar() throws SQLException {

        Connection cnc = null; 
        PreparedStatement stm = null;
        int registros = 0;
        try {
            cnc = this.conexionTransaccional !=null ? this.conexionTransaccional :Conexion.getConnection();
            stm = cnc.prepareStatement(SQL_INSERT);

        //se piden los datos del nuevo cliente por tcl
            System.out.println("DNI:");
            dniSc = tcl.nextLine();
            System.out.println("NOMBRE:");
            nombreSc = tcl.nextLine();
            System.out.println("APELLIDO(S):");
            apellidoSc = tcl.nextLine();
            System.out.println("EDAD:");
            edadSc = tcl.nextInt();
            tcl.nextLine();
            //condición para el control de la edad
            if(edadSc >= 18) { //si es mayor de edad seguimos con le ejecución del metodo
                System.out.println("E-MAIL:");
                emailSc = tcl.nextLine();
                System.out.println("ID E-WALLET:");
                idWalletSc = tcl.nextLine();
                //saldo y puntos no se piden porque por defecto al dar de alta son = 0
                //nuevo obj Cliente 
                Cliente clienteNuevo = new Cliente(dniSc,nombreSc,apellidoSc,edadSc,emailSc,idWalletSc);    
                //y setteamos los atributos de la tabla con los del nuevo Cliente    
                stm.setString(1, clienteNuevo.getDni());
                stm.setString(2, clienteNuevo.getNombre());
                stm.setString(3, clienteNuevo.getApellido());
                stm.setInt(4, clienteNuevo.getEdad());
                stm.setString(5, clienteNuevo.getEmail());
                stm.setString(6, clienteNuevo.getIdWallet());
                stm.setDouble(7, clienteNuevo.getSaldoEuro());
                stm.setInt(8, clienteNuevo.getSaldoPuntos());

                registros = stm.executeUpdate(); //ejecucion del update
                //llamo al metodo seleccionar para mostrar la lista de clientes actualizada 
                seleccionar(); 

            } else { //si es menor de edad, salta el error
                System.out.println("\nIMPOSIBLE COMPLETAR EL PROCESO DE ALTA!\nPersona menor de edad.\n");
            }
        } finally {
            try {
                Conexion.close(stm);
                if (this.conexionTransaccional==null){
                Conexion.close(cnc);}
            } catch (SQLException ex) {
                ex.printStackTrace(System.out);
            }
        }
        return registros;
    }

    /**
     * Método para buscar los clientes por idWallet dentro de la BD
     * @return objeto Cliente que es el cliente buscado por teclado
     * @throws SQLException 
    **/
    public Cliente buscarCliente() throws SQLException {
        Cliente clienteBuscado = null;
        boolean clienteEncontrado = false;

        System.out.println("\nSeleccionar el cliente para completar la operación.");
        //lista de ClientesDAO a recorrer
        List<Cliente> listaClientes = seleccionar();
        System.out.println("Introduce su ID e-Wallet:");

        idWalletSc = tcl.nextLine(); //se coge el id y se busca en la lista clienteDAO

        for (Cliente cliente: listaClientes) {
            if(idWalletSc.equalsIgnoreCase(cliente.getIdWallet())) {
                clienteBuscado = cliente; //se ha encontrado el cliente y se le asignan sus atributos
                clienteEncontrado = true; //se ha encontrado el cliente
                break; //para no seguir buscando en el bucle
            } 
        }
        if (clienteEncontrado == false) { //si no se ha encontrado el cliente
            System.out.println("ERROR! Cliente no encontrado.\n");
        }
        return clienteBuscado;
    }

    /**
     * Método para eliminar los datos de un cliente presente en la BD
     * @param clienteBuscado obtenido por el método buscarCliente()
     * @return int registros variable de llamada a la ejecución del update
     * @throws SQLException 
    **/
    public int eliminar(Cliente clienteBuscado) throws SQLException {

        int registros = 0;
        if(clienteBuscado != null) { //si el cliente se ha encontrado se ejecuta el metodo
            Connection cnc = null;
            PreparedStatement stm = null;
            
            try {
                cnc = this.conexionTransaccional !=null ? this.conexionTransaccional :Conexion.getConnection();
                stm = cnc.prepareStatement(SQL_DELETE);
            
                //se settea en la query la PK  para que se borre la fila correspondiente
                stm.setString(1, clienteBuscado.getIdWallet()); 
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
            //metodo seleccionar para mostrar la lista de clientes actualizada sin el cliente borrado
            seleccionar();
        } else { //si no se muestra el error
            System.out.println("CLIENTE NO BORRADO!");
        }
        return registros;
    }

    /**
     * Método para modificar los datos de un cliente ya presente en la BD con los valores pasados por teclado
     * @param clienteBuscado Cliente obtenido por el método buscarCliente()
     * @return int registros variable de llamada a la ejecución del update
     * @throws SQLException 
    **/
    public int modificar(Cliente clienteBuscado) throws SQLException {

        int registros = 0;

        if(clienteBuscado != null) { //si el cliente se ha encontrado se ejecuta
            //se settean los atributos del Cliente buscado con los valores modificado por tcl
            System.out.println("NOMBRE:");
            clienteBuscado.setNombre(nombreSc = tcl.nextLine());
            System.out.println("APELLIDO:");
            clienteBuscado.setApellido(apellidoSc = tcl.nextLine());
            System.out.println("EDAD:");
            clienteBuscado.setEdad(edadSc = tcl.nextInt());
            tcl.nextLine();
            System.out.println("E-MAIL:");
            clienteBuscado.setEmail(emailSc = tcl.nextLine());
            //el saldo y los puntos no se pueden modificar porque dependen de las operaciones de compra y devolucion
            
            Connection cnc = null;
            PreparedStatement stm = null;
            try {
                cnc = this.conexionTransaccional !=null ? this.conexionTransaccional :Conexion.getConnection();
                stm = cnc.prepareStatement(SQL_UPDATE);
                //se settean los datos de la query con los del obj cliente elegido por tcl
                stm.setString(1, clienteBuscado.getNombre());
                stm.setString(2, clienteBuscado.getApellido());
                stm.setInt(3, clienteBuscado.getEdad());
                stm.setString(4, clienteBuscado.getEmail());
                stm.setString(5, clienteBuscado.getIdWallet());

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
            //se muestra el cliente buscado actualizado 
            System.out.println("\n*****DATOS CLIENTE ACTUALIZADOS DESPUES DE MODIFICAR******\n" + clienteBuscado);
        } else { //sino se muestra el error
            System.out.println("CLIENTE NO MODIFICADO!");
        }
        return registros;
    }
    
    /**
     * Método para recargar el saldo en euro de un cliente ya presente en la BD 
     * @return int registros variable de llamada a la ejecución del update
     * @throws SQLException 
    **/
    public int recargarSaldo() throws SQLException {
        
        int registros = 0;
        //el metodos se ejecuta solo si el dia del mes es entre 1 y 5
        if (LocalDateTime.now().getDayOfMonth() <= 5) {
            ClienteDAO clienteBuscado = new ClienteDAO(); //se crea el ClienteDAO para acceder a sus metodos
            Cliente clienteRecarga = clienteBuscado.buscarCliente();

            if(clienteRecarga != null ) {//si el cliente se ha encontrado
                //se settea el saldo en euro
                System.out.println("SALDO A RECARGAR:");
                saldoESc = tcl.nextDouble();
                tcl.nextLine();
                //se le suma al saldo del cliente buscado el saldo ingresado por teclado
                clienteRecarga.setSaldoEuro(clienteRecarga.getSaldoEuro()+ saldoESc);

                Connection cnc = null;
                PreparedStatement stm = null;
                try {
                    System.out.println("holaaa2");
                    cnc = this.conexionTransaccional !=null ? this.conexionTransaccional :Conexion.getConnection();
                    stm = cnc.prepareStatement(SQL_UPDATE_SALDOEURO);
                    //se settean los datos de la query con los del obj cliente elegido por tcl
                    stm.setDouble(1, clienteRecarga.getSaldoEuro());
                    stm.setString(2, clienteRecarga.getIdWallet());

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
                //se muestra el cliente buscado actualizado 
                System.out.println("\n*****SALDO CLIENTE ACTUALIZADO DESPUES DE INGRESAR DINERO******\n" + clienteRecarga);
            } else { //sino se muestra el error
                tcl.nextLine();
                System.out.println("SALDO NO INGRESADO!");
            }
        } else {
            System.out.println("NO SE PUEDE RECARGAR EL SALDO PASADO EL 5 DE CADA MES!");
        }
        return registros;
    }
    
}
