/**
 * Cliente para el ejercicio 2 de PSP03.
 * Este cliente se conecta a un servidor, envía el nombre de un archivo al servidor,
 * recibe la confirmación de la existencia del archivo y, si existe, recibe y muestra su contenido.
 */
package psp03_ejer2_cliente;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class PSP03_Ejer2_Cliente {
    
    private static final int PUERTO = 1500;

    /**
     * Método principal que inicia el cliente y se conecta al servidor.
     * @param args Los argumentos de la línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        try (
            Socket cliente = new Socket("localhost", PUERTO);
            DataInputStream dis = new DataInputStream(cliente.getInputStream());
            DataOutputStream dos = new DataOutputStream(cliente.getOutputStream());
            Scanner sca = new Scanner(System.in)
        ) {
            manejarConexion(dos, dis, sca);
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    /**
     * Maneja la conexión con el servidor.
     * @param dos Stream de salida de datos hacia el servidor.
     * @param dis Stream de entrada de datos desde el servidor.
     * @param sca Scanner para la entrada de datos del usuario.
     * @throws IOException Si hay un error de E/S durante la comunicación.
     */
    private static void manejarConexion(DataOutputStream dos, DataInputStream dis, Scanner sca) throws IOException {
        String nombreFichero;
        boolean existeFichero;

        do {
            System.out.println("Escribe el nombre del fichero");
            nombreFichero = sca.nextLine();
            dos.writeUTF(nombreFichero);

            existeFichero = dis.readBoolean();

            if (!existeFichero) {
                System.out.println("El fichero no existe, por favor, escribe de nuevo.");
            }
        } while (!existeFichero);

        recibirArchivo(dis);
    }

    /**
     * Recibe y muestra el contenido del archivo desde el servidor.
     * @param dis Stream de entrada de datos desde el servidor.
     * @throws IOException Si hay un error de E/S durante la recepción del archivo.
     */
    private static void recibirArchivo(DataInputStream dis) throws IOException {
        File archivo = new File("recibido.txt");
        archivo.createNewFile();

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(archivo))) {
            int lee;
            while ((lee = dis.read()) != -1) {
                bos.write(lee);
            }
        }

        System.out.println("Archivo recibido correctamente.");

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            System.out.println("Contenido del archivo:");
            while ((linea = br.readLine()) != null) {
                System.out.println(linea);
            }
        }
    }
}