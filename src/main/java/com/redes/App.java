package com.redes;

//Importación de Librerías y Paquetes
import java.io.File;
import java.io.IOException;
import org.jivesoftware.smack.packet.PresenceBuilder;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.ChatMessageListener;
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
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    public static void main(String[] args) throws XmppStringprepException, InterruptedException {
        Scanner input = new Scanner(System.in);
        String serverAddress = "alumchat.xyz";
        String xmppServiceName = "alumchat.xyz";
        // Direcciones del servicio para conectarse.
        int serverPort = 5222; // Default XMPP port

        // COnfiguracipón para llamar al servicio. Se debe de conectar al host, al
        // doinio y el puero que es 5222.
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setHost(serverAddress)
                .setXmppDomain(xmppServiceName)
                .setPort(serverPort)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled) // You may need to adjust this based on
                                                                                // your server's security settings
                .setCompressionEnabled(false)
                .setConnectTimeout(10000)
                .build();

        XMPPTCPConnection connection = new XMPPTCPConnection(config);
        // Se crea la conexión con la configuración.
        Logger logger = Logger.getLogger(XMPPTCPConnection.class.getName());
        logger.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);

        // Revise que se haya conectado correctamente.
        System.out.println("Connection: " + connection);
        int userName = 0;
        // Creación de menu.
        while (userName != 4) {
            try {
                connection.connect();
                // Se conecta la sesión, y pregunta si quiere crear cuenta, ingresar, o eliminar
                // cuenta.
                System.out.println("¿Qué desea hacer?\n1. Crear cuenta.\n2. Ingresar\n3. Eliminar cuenta.\n4Salir.");
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
                    // Ingreso de usuario y contrasela
                    System.out.println("Ingrese su usuario:");
                    input.nextLine();
                    String user = input.nextLine();
                    System.out.println("Ingrese su contraseña");
                    String password = input.nextLine();
                    // Conexión de login, y entra al servidor.
                    connection.login(user, password);
                    // Chat Manager, para poder mandar y recibir mensajes.
                    ChatManager chatManager = ChatManager.getInstanceFor(connection);

                    // Listener para manejar mensajes
                    chatManager.addIncomingListener(new IncomingChatMessageListener() {
                        @Override
                        public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {
                            // En el caso que se recibe un mensaje, recibe la inforamción de dicho mensaje.
                            if (message.getBody().startsWith("file://")) {
                                // Contenido del archivo, y también formate del archivo ginal.también quien
                                // mando el archivo y su contenido
                                String base64File = message.getBody().substring(7);
                                String fileType = message.getBody().substring(7,
                                        7 + base64File.indexOf("://"));
                                base64File = base64File.substring(base64File.indexOf("://") + 3);

                                // Decodifica el archivo, en bytes.
                                byte[] file = java.util.Base64.getDecoder().decode(base64File);

                                System.out.println("\nRecieved file from. " + fileType);

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
                                System.out.println("New message from " + from + ": " + message.getBody());
                            }
                        }
                    });
                    // Para todos los contactos que se tiene, se tiene un manager para esto.
                    Roster roster = Roster.getInstanceFor(connection);
                    // El rooster listener tiene en cuenta cuando se añade un nuevo usuario, update,
                    // borra, o cuando el contacto cambia su presencia.
                    roster.addRosterListener(new RosterListener() {
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
                            System.out.println("Presence Changed:");
                            System.out.println(" - Contact JID: " + contactJid);
                            System.out.println(" - Presence Type: " + presenceType);
                            System.out.println(" - Status Type: " + status);
                            System.out.println();
                        }
                    });
                    // Menu cuando esta dentro del resrvidor.
                    System.out.println(
                            "Ingrese que hacer?\n1.Añadir Contacto\n2. Ver listado de contactos.\n3. Ver información de un usuario.\n4. Mandar mensaje.\n5. Unirse a un grupo. \n6. Mandar un archivo. \n7. Cambiar Presencia\n8.Desconectarse");
                    int input1 = input.nextInt();
                    if (input1 == 1) {

                        System.out.println("Ingrese el Usuario del Contacto!");
                        input.nextLine();
                        // Pide al nuevo usuario. Se añade la dirección del servidor como el Jid
                        String newContactJID = input.nextLine() + "@alumchat.xyz";
                        System.out.println("Ingrese el nombre del contacto del Contacto!");
                        String newContactName = input.nextLine(); // Se puede cambiar el nombre del usuario, o si no,
                                                                  // queda como el Jud.
                        EntityBareJid jid = JidCreate.entityBareFrom(newContactJID);
                        if (newContactName == "" || newContactName == null) {
                            newContactName = newContactJID;
                        }
                        // Crea el nuevo contacto respectivamente.
                        roster.createItemAndRequestSubscription(jid, newContactName, null);
                    } else if (input1 == 2) {
                        roster.reloadAndWait();
                        System.out.println("Contact List (Roster):");
                        for (RosterEntry entry : roster.getEntries()) {
                            if (entry.getName() == null) {
                                System.out.println("Contact: " + entry.getJid() + " (" + entry.getJid() + ")");
                            } else {
                                System.out.println("Contact: " + entry.getName() + " (" + entry.getJid() + ")");
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

                        System.out.println(entry);
                        if (entry != null) {
                            // PImprime la información del usuario, y muesrra si esta online u offline.
                            System.out.println("User Information for: " + use);
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
                        // Creación de un manager de multiples personas, o de grupo
                        MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(connection);
                        // Pide el nombre del grupo a cual unirse.
                        System.out.println("Ingrese nombre del grupo: ");
                        input.nextLine();
                        String room = input.nextLine();
                        // Crea el Jid del grupo. Se utiliza @conference para esto.
                        EntityBareJid roomJid = JidCreate.entityBareFrom(room + "@conference.alumchat.xyz");
                        MultiUserChat muc = mucManager.getMultiUserChat(roomJid);
                        // Get MultiUser chat para crear un grupo, o para también tener el grupo
                        // respecto.
                        System.out.println("Ingrese un apodo para el grupo: ");
                        String apodo = input.nextLine();
                        Resourcepart nickname = Resourcepart.from(apodo);
                        muc.join(nickname);
                        // Recurso para poder mandar al grupo. Después de une.
                        muc.addMessageListener(new MessageListener() {
                            @Override
                            public void processMessage(Message message) {
                                System.out.println(
                                        "Received message from " + message.getFrom() + ": " + message.getBody());
                            }
                        });
                        // Manager para recibir mensajes del grupo.
                        int group = 0;
                        // Menu de que hacer en el grupo.
                        while (group != 2) {
                            System.out.println("Que quiere hacer?\n 1. Mandar un mensaje.\n2. Salir del grupo. ");
                            group = input.nextInt();
                            if (group == 1) {
                                input.nextLine();
                                System.out.println("Escriba el mensaje que euire mandar: ");
                                String message = input.nextLine();
                                muc.sendMessage(message);
                            } else if (group == 2) {
                                muc.leave();
                            }
                            // Opción de mandar mensaje el grupo, o de salir del grupo respectivamente.
                        }
                    } else if (input1 == 6) {
                        System.out.println("Ingrese el usuario.");
                        input.nextLine();
                        String username = input.nextLine();
                        System.out.println("Ingrese dirección del archivo: ");
                        String filePath = input.nextLine();

                        File file = new File(filePath);

                        byte[] fileBytes = new byte[(int) file.length()];

                        try {
                            java.io.FileInputStream fileInputStream = new java.io.FileInputStream(file);
                            fileInputStream.read(fileBytes);
                            fileInputStream.close();
                        } catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }

                        String base64File = java.util.Base64.getEncoder().encodeToString(fileBytes);
                        String fileType = filePath.substring(filePath.lastIndexOf(".") + 1);
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

                        System.out.println("File sent succesfully");
                    } else if (input1 == 7) {
                        input.nextLine();
                        Presence.Mode[] presenceModes = Presence.Mode.values();
                        System.out.println("Select presence mode:\n");
                        for (int i = 1; i < presenceModes.length + 1; i++) {
                            System.out.println(i + ". " + presenceModes[i - 1]);

                        }
                        int option = input.nextInt() - 1;

                        System.out.println("Ingrese su estado: ");
                        input.nextLine();
                        String status = input.nextLine();
                        try {
                            PresenceBuilder presenceBuilder = PresenceBuilder.buildPresence()
                                    .setMode(presenceModes[option])
                                    .setStatus(status);
                            connection.sendStanza(presenceBuilder.build());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println("Presencia ha sido actualizada!");

                    } else if (input1 == 8) {
                        connection.disconnect();
                    }

                } else if (userName == 3) {
                    // Se tiene el managaer para las cuentas.
                    AccountManager accountManager = AccountManager.getInstance(connection);
                    System.out.println("Ingrese su usuario:");
                    input.nextLine();
                    String user = input.nextLine();
                    System.out.println("Ingrese su contraseña");
                    String password = input.nextLine();
                    // Se conecta. Desde la conexioón de adentro, se elimina la cuenta y todo
                    // termina.
                    connection.login(user, password);
                    accountManager.sensitiveOperationOverInsecureConnection(true);
                    accountManager.deleteAccount();
                    System.out.println("La cuenta ha sido borrada!");
                }

                // User authenticated, perform your XMPP operations*/
            } catch (SmackException | XMPPException | IOException | NullPointerException e) {
                e.printStackTrace();
                System.out.println("Connection failed. Check for any errors.");

                // Si ocurren orresres, muestra que hubo en un problema en la conexión.
            }
        }

    }
}
