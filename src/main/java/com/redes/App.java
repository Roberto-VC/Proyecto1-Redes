package com.redes;

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

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) throws XmppStringprepException, InterruptedException {
        Scanner input = new Scanner(System.in);
        String serverAddress = "alumchat.xyz";
        String xmppServiceName = "alumchat.xyz";
        int serverPort = 5222; // Default XMPP port

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

        // Create the connection
        Logger logger = Logger.getLogger(XMPPTCPConnection.class.getName());
        logger.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);

        // Print connection object
        System.out.println("Connection: " + connection);
        try {
            connection.connect();
            System.out.println("¿Qué desea hacer?\n1. Crear cuenta.\n2. Ingresar\n3. Eliminar cuenta");
            int userName = input.nextInt();
            System.out.println(userName);
            if (userName == 1) {
                AccountManager accountManager = AccountManager.getInstance(connection);
                // User registration details
                String username = "newuser";
                String password = "password";
                Localpart localpart = Localpart.from(username);

                // Register the new user account
                try {
                    accountManager.createAccount(localpart, password);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (userName == 2) {
                System.out.println("Ingrese su usuario:");
                input.nextLine();
                String user = input.nextLine();
                System.out.println("Ingrese su contraseña");
                String password = input.nextLine();
                connection.login(user, password);
                ChatManager chatManager = ChatManager.getInstanceFor(connection);

                chatManager.addIncomingListener(new IncomingChatMessageListener() {
                    @Override
                    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {

                        if (message.getBody().startsWith("file://")) {
                            String base64File = message.getBody().substring(7);
                            String fileType = message.getBody().substring(7,
                                    7 + base64File.indexOf("://"));
                            base64File = base64File.substring(base64File.indexOf("://") + 3);

                            System.out.println(base64File);
                            System.out.println(fileType);

                            byte[] file = java.util.Base64.getDecoder().decode(base64File);

                            System.out.println("\nRecieved file from. " + fileType);

                            File fileToSave = new File("recieved_file." + fileType);

                            try {
                                java.io.FileWriter fileWriter = new java.io.FileWriter(fileToSave);
                                fileWriter.write(new String(file));
                                fileWriter.close();
                            } catch (Exception e) {
                                System.out.println("Error: " + e.getMessage());
                            }

                        } else {
                            System.out.println("New message from " + from + ": " + message.getBody());
                        }
                    }
                });

                Roster roster = Roster.getInstanceFor(connection);
                roster.addRosterListener(new RosterListener() {
                    @Override
                    public void entriesAdded(Collection<Jid> addresses) {
                        System.out.println("Se ingreso un nuevo usuario!");

                    }

                    @Override
                    public void entriesUpdated(Collection<Jid> addresses) {

                    }

                    @Override
                    public void entriesDeleted(Collection<Jid> addresses) {

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
                System.out.println(
                        "Ingrese que hacer?\n1.Añadir Contacto\n2. Ver listado de contactos.\n3Ver información de un usuario.\n4. Mandar mensaje.");
                int input1 = input.nextInt();
                if (input1 == 1) {

                    roster.addRosterListener(new RosterListener() {

                        @Override
                        public void entriesAdded(Collection<Jid> addresses) {
                            // Handle roster entries added
                            for (Jid address : addresses) {
                                System.out.println("New contact added: " + address);
                            }
                        }

                        @Override
                        public void entriesUpdated(Collection<Jid> addresses) {
                            // Handle roster entries updated
                            for (Jid address : addresses) {
                                System.out.println("Contact updated: " + address);
                            }
                        }

                        @Override
                        public void entriesDeleted(Collection<Jid> addresses) {
                            // Handle roster entries deleted
                            for (Jid address : addresses) {
                                System.out.println("Contact deleted: " + address);
                            }
                        }

                        @Override
                        public void presenceChanged(org.jivesoftware.smack.packet.Presence presence) {
                            // Handle presence changes
                            System.out.println("Presence changed: " + presence.getFrom() + " - " +
                                    presence.getType());
                        }
                    });

                    System.out.println("Ingrese el Usuario del Contacto!");
                    input.nextLine();
                    String newContactJID = input.nextLine() + "@alumchat.xyz";
                    System.out.println("Ingrese el nombre del contacto del Contacto!");
                    String newContactName = input.nextLine(); // Replace with the name of the new contact (optional)
                    EntityBareJid jid = JidCreate.entityBareFrom(newContactJID);
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
                    String use = users + "@alumchat.xyz";
                    EntityBareJid jid = JidCreate.entityBareFrom(use);
                    RosterEntry entry = roster.getEntry(jid);

                    System.out.println(entry);
                    if (entry != null) {
                        // Print the user's information
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

                    }
                } else if (input1 == 4) {
                    // Keep the main thread running to receive notifications
                    System.out.println("Ingrese el usuario");
                    input.nextLine();
                    String users = input.nextLine();
                    users = users + "@alumchat.xyz";
                    EntityBareJid recipient = JidCreate.entityBareFrom(users);
                    Chat chat = chatManager.chatWith(recipient);
                    System.out.println("Escriba el mensaje que mandar: ");
                    String message = input.nextLine();
                    chat.send(message);

                    // Keep the main thread waiting for user input

                } else if (input1 == 5) {
                    // Create a multi-user chat manager
                    MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(connection);

                    System.out.println("Ingrese nombre del grupo: ");
                    input.nextLine();
                    String room = input.nextLine();
                    EntityBareJid roomJid = JidCreate.entityBareFrom(room + "@conference.alumchat.xyz");
                    MultiUserChat muc = mucManager.getMultiUserChat(roomJid);

                    System.out.println("Ingrese un apodo para el grupo: ");
                    String apodo = input.nextLine();
                    Resourcepart nickname = Resourcepart.from(apodo);
                    muc.join(nickname);
                    // Set up message listener
                    muc.addMessageListener(new MessageListener() {
                        @Override
                        public void processMessage(Message message) {
                            System.out.println("Received message from " + message.getFrom() + ": " + message.getBody());
                        }
                    });

                    int group = 0;
                    // Leave the room
                    while (group != 2) {
                        if (group == 1) {
                            System.out.println("Que quiere hacer?\n 1. Mandar un mensaje.\n2. Salir del grupo. ");
                            muc.sendMessage("Hello, this is my message!");
                        } else if (group == 2) {
                            muc.leave();
                        }

                    }
                } else if (input1 == 6) {
                    System.out.println("Enter username");
                    input.nextLine();
                    String username = input.nextLine();
                    System.out.println("Enter file path: ");
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
                        EntityBareJid userID = JidCreate.entityBareFrom(username + "@" + config.getXMPPServiceDomain());
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
                AccountManager accountManager = AccountManager.getInstance(connection);
                System.out.println("Ingrese su usuario:");
                input.nextLine();
                String user = input.nextLine();
                System.out.println("Ingrese su contraseña");
                String password = input.nextLine();
                connection.login(user, password);
                accountManager.sensitiveOperationOverInsecureConnection(true);
                accountManager.deleteAccount();
                System.out.println("La cuenta ha sido borrada!");
            }

            // User authenticated, perform your XMPP operations*/
        } catch (SmackException | XMPPException | IOException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("Connection failed. Check for any errors.");

            // Handle connection or authentication errors
        }

    }
}
