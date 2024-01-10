/**
 * Servidor para el ejercicio 2 de PSP03.
 * Este servidor acepta una conexión de un cliente, recibe la ruta de un archivo desde el cliente,
 * verifica la existencia del archivo, y si existe, lo envía al cliente.
 */
package psp03_ejer2_servidor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PSP03_Ejer2_Servidor {

    private static final int PUERTO = 1500;

    /**
     * Método principal que inicia el servidor y espera conexiones de clientes.
     * @param args Los argumentos de la línea de comandos (no se utilizan).
     */
    public static void main(String[] args) {
        System.out.println("Arrancando el servidor en el puerto " + PUERTO);
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Esperando conexion desde el cliente...");
            Socket cliente = servidor.accept();
            System.out.println("Conexion aceptada desde " + cliente.getInetAddress());

            try (DataInputStream dis = new DataInputStream(cliente.getInputStream());
                 DataOutputStream dos = new DataOutputStream(cliente.getOutputStream())) {

                manejarConexion(dis, dos);

            } catch (IOException ex) {
                System.out.println("Error durante la comunicacion con el cliente: " + ex.getMessage());
            }

        } catch (IOException ex) {
            System.out.println("Error al iniciar el servidor: " + ex.getMessage());
        }
    }

    /**
     * Maneja la conexión con el cliente.
     * @param dis Stream de entrada de datos desde el cliente.
     * @param dos Stream de salida de datos hacia el cliente.
     * @throws IOException Si hay un error de entrada/salida durante la comunicación.
     */
    private static void manejarConexion(DataInputStream dis, DataOutputStream dos) throws IOException {
        System.out.println("Esperando la ruta del fichero desde el cliente...");
        String rutaFichero = dis.readUTF();

        if (verificarExistenciaArchivo(rutaFichero)) {
            System.out.println("Enviando archivo al cliente...");
            enviarArchivo(rutaFichero, dos);
        } else {
            System.out.println("El archivo no existe en la ruta especificada.");
        }
    }

    /**
     * Verifica la existencia del archivo en la ruta especificada.
     * @param rutaFichero Ruta del archivo proporcionada por el cliente.
     * @return True si el archivo existe, False en caso contrario.
     * @throws IOException Si hay un error de entrada/salida durante la verificación.
     */
    private static boolean verificarExistenciaArchivo(String rutaFichero) throws IOException {
        Path archivoPath = Paths.get(rutaFichero);
        return Files.exists(archivoPath);
    }

    /**
     * Envía el contenido de un archivo al cliente.
     * @param rutaFichero Ruta del archivo a enviar.
     * @param dos Stream de salida de datos hacia el cliente.
     * @throws IOException Si hay un error de entrada/salida durante el envío del archivo.
     */
    private static void enviarArchivo(String rutaFichero, DataOutputStream dos) throws IOException {
        Path archivoPath = Paths.get(rutaFichero);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(archivoPath.toFile()))) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesRead);
            }
        }
    }
}
