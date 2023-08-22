package com.redes;

//Importación de Librerías y Paquetes
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smack.packet.PresenceBuilder;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XmlEnvironment;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.SslContextFactory;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.jingle.Role;
import org.jivesoftware.smackx.muc.MUCRole;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.Occupant;

import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class App {
    // Create an SSLContext that trusts all certificates (not recommended for
    // production)

    // getSslContextFacotry para poder realizar una conexión segura
    // Esto permite crear cuenatas.
    private static SslContextFactory getSslContextFactory() {
        return () -> {
            try {
                final SSLContext result = SSLContext.getInstance("TLS");
                result.init(null, new TrustManager[] { new DummyTrustManager() }, new SecureRandom());
                return result;
            } catch (Exception e) {
                throw new RuntimeException("can't init SSL Context.", e);
            }
        };
    }

    private static class DummyTrustManager implements X509TrustManager {
        public boolean isClientTrusted(X509Certificate[] cert) {
            return true;
        }

        public boolean isServerTrusted(X509Certificate[] cert) {
            return true;
        }

        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
        // Función para los certificados.
    }

    public static void main(String[] args) throws XmppStringprepException, InterruptedException {
        Scanner input = new Scanner(System.in);
        String serverAddress = "alumchat.xyz";
        String xmppServiceName = "alumchat.xyz";
        // Direcciones del servicio para conectarse.
        int serverPort = 5222; // Default XMPP port

        System.setProperty("javax.net.ssl.trustStore", "keystore.jks");

        // COnfiguracipón para llamar al servicio. Se debe de conectar al host, al
        // doinio y el puero que es 5222.
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setHost(serverAddress)
                .setXmppDomain(xmppServiceName)
                .setPort(serverPort)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.required)
                .setSslContextFactory(getSslContextFactory())
                .setCustomX509TrustManager(new DummyTrustManager())
                // your server's security settings
                .setCompressionEnabled(false)
                .build();

        XMPPTCPConnection connection = new XMPPTCPConnection(config);
        PingManager pingManager = PingManager.getInstanceFor(connection);
        pingManager.setPingInterval(1000);
        // Se crea la conexión con la configuración.
        Logger logger = Logger.getLogger(XMPPTCPConnection.class.getName());
        logger.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);

        // Revise que se haya conectado correctamente.
        System.out.println("Connection: " + connection);
        int userName = 0;
        boolean log = false;
        try {
            log = true;
            connection.connect();
        } catch (Exception e) {
            System.out.println("No se pudo contectar");
            userName = 4;
        }
        // Creación de menu.
        while (userName != 4) {
            try {

                // Se conecta la sesión, y pregunta si quiere crear cuenta, ingresar, o eliminar
                // cuenta.
                System.out.println("¿Qué desea hacer?\n1. Crear cuenta.\n2. Ingresar\n3. Eliminar cuenta.\n4. Salir.");
                userName = input.nextInt();
                System.out.println(userName);
                if (userName == 1) {
                    AccountManager accountManager = AccountManager.getInstance(connection);
                    input.nextLine();
                    // Registro de Usuario
                    System.out.println("Ingrese el nombre del usuario");
                    String username = input.nextLine();
                    System.out.println("Ingrese de la contraseña");
                    String password = input.nextLine();
                    Localpart localpart = Localpart.from(username);

                    // Local part hace que se pueda converitr en XML y poder mandar.
                    try {
                        // Creación de cuenta. Si no puede, tira el error.
                        accountManager.createAccount(localpart, password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (userName == 2) {
                    if (log == false) {
                        connection.connect();
                        log = true;
                    }
                    // Ingreso de usuario y contrasela
                    System.out.println("Ingrese su usuario:");
                    input.nextLine();
                    String user = input.nextLine();
                    System.out.println("Ingrese su contraseña");
                    String password = input.nextLine();
                    Resourcepart resource = Resourcepart.from("Laptop");
                    // Conexión de login, y entra al servidor.
                    connection.login(user, password, resource);
                    // Chat Manager, para poder mandar y recibir mensajes.
                    ChatManager chatManager = ChatManager.getInstanceFor(connection);
                    // Creación de un manager de multiples personas, o de grupo
                    MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(connection);
                    // Array de Listeners para guardar la info, y después mandarla.
                    Map<String, MessageListener> groupListeners = new HashMap<>();
                    List<IncomingChatMessageListener> chatMessageListeners = new ArrayList<>();
                    IncomingChatMessageListener incomingListener = (from, message, chat) -> {
                        if (message.getBody().startsWith("file://")) {
                            // Contenido del archivo, y también formate del archivo ginal.también quien
                            // mando el archivo y su contenido
                            String base64File = message.getBody().substring(7);
                            String fileType = message.getBody().substring(7,
                                    7 + base64File.indexOf("://"));
                            base64File = base64File.substring(base64File.indexOf("://") + 3);

                            // Decodifica el archivo, en bytes.
                            byte[] file = java.util.Base64.getDecoder().decode(base64File);

                            System.out.println("\nArchivo recibido de: " + from);

                            File fileToSave = new File("recieved_file." + fileType);

                            try {
                                // Escribe el archivo y lo guarda ahí mismo.
                                java.io.FileWriter fileWriter = new java.io.FileWriter(fileToSave);
                                fileWriter.write(new String(file));
                                fileWriter.close();
                            } catch (Exception e) {
                                System.out.println("Error: " + e.getMessage());
                            }

                        } else {
                            // Si solo es un mensaje cualquiera, manda la info de este mensaje.
                            System.out.println("Nuevo mensaje de " + from + ": " + message.getBody());
                        }
                    };

                    // Listener para manejar mensajes
                    chatMessageListeners.add(incomingListener);
                    // También se guarda en el array de Listeners
                    chatManager.addIncomingListener(incomingListener);
                    // Para todos los contactos que se tiene, se tiene un manager para esto.
                    List<RosterListener> RosterListeners = new ArrayList<>();
                    Roster roster = Roster.getInstanceFor(connection);
                    RosterListener rosterListener = new RosterListener() {
                        @Override
                        public void entriesAdded(Collection<Jid> addresses) {
                            System.out.println("Se ingreso un nuevo usuario!");

                        }

                        @Override
                        public void entriesUpdated(Collection<Jid> addresses) {
                            System.out.println("Se actualizo un usuario!");
                        }

                        @Override
                        public void entriesDeleted(Collection<Jid> addresses) {
                            System.out.println("Se quito un usuario!");
                        }

                        @Override
                        public void presenceChanged(Presence presence) {
                            Jid contactJid = presence.getFrom();
                            Presence.Mode presenceType = presence.getMode();
                            String status = presence.getStatus();
                            System.out.println("Presencia Cambiada:");
                            System.out.println(" - Contact JID: " + contactJid);
                            System.out.println(" - Tipo de Presencia: " + presenceType);
                            System.out.println(" - Tipo de Estado: " + status);
                            System.out.println();
                        }
                    };
                    roster.addRosterListener(rosterListener);
                    RosterListeners.add(rosterListener);
                    // Menu cuando esta dentro del resrvidor.
                    int input1 = 0;
                    while (input1 != 9) {
                        System.out.println(
                                "Ingrese que hacer?\n1. Añadir Contacto\n2. Ver listado de contactos.\n3. Ver información de un usuario.\n4. Mandar mensaje.\n5. Unirse a un grupo.\n6. Mandar mensaje a un grupo. \n7. Mandar un archivo. \n8. Cambiar Presencia\n9. Desconectarse");
                        input1 = input.nextInt();
                        if (input1 == 1) {

                            System.out.println("Ingrese el Usuario del Contacto!");
                            input.nextLine();
                            // Pide al nuevo usuario. Se añade la dirección del servidor como el Jid
                            String newContactJID = input.nextLine() + "@alumchat.xyz";
                            System.out.println("Ingrese el nombre del contacto del Contacto!");
                            String newContactName = input.nextLine(); // Se puede cambiar el nombre del usuario, o si
                                                                      // no,
                                                                      // queda como el Jud.
                            EntityBareJid jid = JidCreate.entityBareFrom(newContactJID);
                            if (newContactName == "" || newContactName == null) {
                                newContactName = newContactJID;
                            }
                            // Crea el nuevo contacto respectivamente.
                            roster.createItemAndRequestSubscription(jid, newContactName, null);
                        } else if (input1 == 2) {
                            System.out.println("Lista de Contactos (Roster):");
                            for (RosterEntry entry : roster.getEntries()) {
                                if (entry.getName() == null) {
                                    System.out.println("Contacto: " + entry.getJid() + " (" + entry.getJid() + ")");
                                } else {
                                    System.out.println("Contacto: " + entry.getName() + " (" + entry.getJid() + ")");
                                }

                            }
                        } else if (input1 == 3) {

                            System.out.println("Ingrese el usuario");
                            input.nextLine();
                            String users = input.nextLine();
                            // Pide al usuario que buscar.
                            String use = users + "@alumchat.xyz";
                            EntityBareJid jid = JidCreate.entityBareFrom(use);
                            RosterEntry entry = roster.getEntry(jid);
                            // Busca en el rooster si esta el usuario.

                            if (entry != null) {
                                // Imprime la información del usuario, y muesrra si esta online u offline.
                                if (entry.getName() == null) {
                                    System.out.println("Información de Usuario: " + entry.getJid());
                                } else {
                                    System.out.println("Información de Usuario: " + entry.getName());
                                }
                                System.out.println("Email: " + entry.getJid());
                                System.out.println("JID: " + entry.getJid());
                                Presence presence = roster.getPresence(jid);
                                if (presence.isAvailable()) {
                                    System.out.println("Online");
                                } else {
                                    System.out.println("Offline");
                                }
                                ;
                            } else {
                                // Muestra la info del usuario. Si no, no muestra nada.
                                System.out.println("Usuario no se encuentra.");
                            }
                        } else if (input1 == 4) {
                            // Pid einformación al usuario
                            System.out.println("Ingrese el usuario");
                            input.nextLine();
                            String users = input.nextLine();
                            users = users + "@alumchat.xyz";
                            EntityBareJid recipient = JidCreate.entityBareFrom(users);
                            Chat chat = chatManager.chatWith(recipient);
                            System.out.println("Escriba el mensaje que mandar: ");
                            String message = input.nextLine();
                            chat.send(message);

                            // A ese usuario, le pide la información de que quiere mandar, y después, manda
                            // la información.

                        } else if (input1 == 5) {

                            // Pide el nombre del grupo a cual unirse.

                            input.nextLine();
                            System.out.println("Ingrese un apodo para el grupo: ");
                            String apodo = input.nextLine();
                            Resourcepart nickname = Resourcepart.from(apodo);
                            System.out.println("Ingrese nombre del grupo: ");
                            String room = input.nextLine();
                            // Crea el Jid del grupo. Se utiliza @conference para esto.
                            EntityBareJid roomJid = JidCreate.entityBareFrom(room + "@conference.alumchat.xyz");
                            ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(connection);
                            MultiUserChat muc = mucManager.getMultiUserChat(roomJid);
                            // Get MultiUser chat para crear un grupo, o para también tener el grupo
                            try {
                                discoManager.discoverInfo(roomJid);
                                System.out.println("Se encontro grupo: " + roomJid);
                            } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException
                                    | SmackException.NotConnectedException e) {
                                System.out.println("No se encontro grupo: " + roomJid);

                                Resourcepart name = Resourcepart.from(room);
                                muc.create(name);
                                System.out.println("Group chat room created: " + muc.getRoom());

                            }
                            Occupant ownOccupant = muc.getOccupant(connection.getUser()); // Get your own occupant
                            System.out.println(ownOccupant);
                            if (ownOccupant != null) {
                                System.out.println(ownOccupant.getRole());
                            } else {
                                System.out.println("You're not part of the group.");
                            }
                            // respecto.

                            muc.join(nickname);
                            // Recurso para poder mandar al grupo. Después de une.
                            MessageListener grouplistener = new MessageListener() {
                                @Override
                                public void processMessage(Message message) {
                                    try {
                                        EntityFullJid roomJid;
                                        roomJid = JidCreate
                                                .entityFullFrom(room + "@conference.alumchat.xyz/" + apodo);

                                        // Solo para que no reciba mensajes míos.
                                        // (!roomJid.equals(message.getFrom()))
                                        if (message.getBody() == null) {
                                            System.out.println(
                                                    "---------------------------------------------------------------------------");
                                        } else {
                                            System.out.println(
                                                    "Mensaje recibido de " + message.getFrom() + ": "
                                                            + message.getBody());
                                        }

                                    } catch (XmppStringprepException e) {
                                        // TODO Auto-generated catch block
                                        System.out.println("Error,  al crear el jid.");
                                        ;
                                    }

                                }
                            };
                            muc.addMessageListener(grouplistener);
                            groupListeners.put(room, grouplistener);
                            System.out.println("Se ha unido al group!");
                        } else if (input1 == 6) {
                            // Manager para recibir mensajes del grupo.
                            int group = 0;
                            // Menu de que hacer en el grupo.
                            while (group != 2) {
                                System.out.println("Que quiere hacer?\n1. Mandar un mensaje.\n2. Salir del grupo. ");
                                group = input.nextInt();
                                if (group == 1) {
                                    input.nextLine();

                                    System.out.print("Ingrese el nombre del grupo: ");
                                    String room = input.nextLine();
                                    EntityBareJid roomJid = JidCreate.entityBareFrom(room + "@conference.alumchat.xyz");
                                    ServiceDiscoveryManager discoManager = ServiceDiscoveryManager
                                            .getInstanceFor(connection);
                                    MultiUserChat muc = mucManager.getMultiUserChat(roomJid);
                                    // Get MultiUser chat para crear un grupo, o para también tener el grupo
                                    // Try catch si se encuentra o grupo, o so, no lo realiza.
                                    try {
                                        discoManager.discoverInfo(roomJid);
                                        System.out.println("Se encontro grupo: " + roomJid);
                                        System.out.println("Escriba el mensaje que queire mandar: ");
                                        String message = input.nextLine();
                                        muc.sendMessage(message);
                                        // En el caso que no sea parte del grupo, no manda los mensajes.
                                    } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException
                                            | SmackException.NotConnectedException e) {
                                        System.out.println("No se encontro grupo: " + roomJid);

                                    }

                                } else if (group == 2) {
                                    System.out.println("Volviendo al menu.");
                                }
                                // Opción de mandar mensaje el grupo, o de salir del grupo respectivamente.
                            }
                        } else if (input1 == 7) {
                            System.out.println("Ingrese el usuario.");
                            input.nextLine();
                            String username = input.nextLine();
                            System.out.println("Ingrese dirección del archivo: ");
                            String filePath = input.nextLine();
                            // Pide el usuario y la dirección del archivo a donde enviar.
                            File file = new File(filePath);

                            byte[] fileBytes = new byte[(int) file.length()];
                            // En un array de dates, se escribe que tan grande es el archivo.
                            try {
                                // Try catch en caso que no pueda leer el archivo.
                                java.io.FileInputStream fileInputStream = new java.io.FileInputStream(file);
                                fileInputStream.read(fileBytes);
                                fileInputStream.close();
                            } catch (Exception e) {
                                System.out.println("Error: " + e.getMessage());
                            }
                            // Enconding para leerlo en base64 File
                            String base64File = java.util.Base64.getEncoder().encodeToString(fileBytes);
                            String fileType = filePath.substring(filePath.lastIndexOf(".") + 1);
                            // Escribe el susbtring y al mandarlo, lo devuelve en un file.
                            String message = "file://" + fileType + "://" + base64File;

                            try {
                                EntityBareJid userID = JidCreate
                                        .entityBareFrom(username + "@" + config.getXMPPServiceDomain());
                                Chat chat = chatManager.chatWith(userID);
                                chat.send(message);
                            } catch (Exception e) {
                                System.out.println("Error" + e.getMessage());
                                return;
                            }
                            // Try catch para cuando manda el archivo.

                            System.out.println("File sent succesfully");
                        } else if (input1 == 8) {
                            input.nextLine();
                            // Opciones de presencia.
                            Presence.Mode[] presenceModes = Presence.Mode.values();
                            System.out.println("Select presence mode:\n");
                            for (int i = 1; i < presenceModes.length + 1; i++) {
                                System.out.println(i + ". " + presenceModes[i - 1]);

                            }
                            int option = input.nextInt() - 1;
                            // String para ingresar el estado del usuario.
                            System.out.println("Ingrese su estado: ");
                            input.nextLine();
                            String status = input.nextLine();
                            // Creación de Presencia basado en lo anterior.
                            try {
                                PresenceBuilder presenceBuilder = PresenceBuilder.buildPresence()
                                        .setMode(presenceModes[option])
                                        .setStatus(status);
                                connection.sendStanza(presenceBuilder.build());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            // Presencia actualizada
                            System.out.println("Presencia ha sido actualizada!");

                        } else if (input1 == 9) {
                            // Desconectar todos los listeners para que no haya problemas respectivos de
                            // múltiples instancias.
                            for (IncomingChatMessageListener listener : chatMessageListeners) {
                                chatManager.removeIncomingListener(listener);
                            }
                            chatMessageListeners.clear();
                            for (RosterListener listener : RosterListeners) {
                                roster.removeRosterListener(listener);
                            }
                            RosterListeners.clear();
                            for (String groupJid : groupListeners.keySet()) {
                                EntityBareJid groupToRemove = JidCreate
                                        .entityBareFrom(groupJid + "@conference.alumchat.xyz");
                                MultiUserChat chats = mucManager.getMultiUserChat(groupToRemove);
                                chats.removeMessageListener(groupListeners.get(groupJid));
                            }
                            // Desconectar y salir.
                            connection.disconnect();
                            log = false;
                        }
                    }

                } else if (userName == 3) {
                    // Se tiene el managaer para las cuentas.
                    if (log = true) {
                        connection.disconnect();
                        log = false;
                    } else {
                        connection.connect();
                    }
                    connection.connect();
                    AccountManager accountManager = AccountManager.getInstance(connection);
                    System.out.println("Ingrese su usuario:");
                    input.nextLine();
                    String user = input.nextLine();
                    System.out.println("Ingrese su contraseña");
                    String password = input.nextLine();
                    // Se conecta. Desde la conexioón de adentro, se elimina la cuenta y todo
                    // termina.
                    System.out.println("Pain");
                    connection.login(user, password);
                    accountManager.sensitiveOperationOverInsecureConnection(true);
                    accountManager.deleteAccount();
                    System.out.println("La cuenta ha sido borrada!");
                    connection.disconnect();
                }

                // User authenticated, perform your XMPP operations*/
            } catch (SmackException | XMPPException | IOException | NullPointerException e) {
                e.printStackTrace();
                System.out.println("Conexión fallida. Intente de nuevo.");

                // Si ocurren orresres, muestra que hubo en un problema en la conexión.
            }
        }
        input.close();
    }
}
