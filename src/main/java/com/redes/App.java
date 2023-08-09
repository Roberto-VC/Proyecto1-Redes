package com.redes;

import java.io.File;
import java.io.IOException;
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
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.EntityFullJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
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
                        System.out.println("New message from " + from + ": " + message.getBody());
                    }
                });

                Roster roster = Roster.getInstanceFor(connection);
                roster.addRosterListener(new RosterListener() {
                    @Override
                    public void entriesAdded(Collection<Jid> addresses) {

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
                        Presence.Type presenceType = presence.getType();
                        System.out.println("Presence Changed:");
                        System.out.println(" - Contact JID: " + contactJid);
                        System.out.println(" - Presence Type: " + presenceType);
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

                    String newContactJID = "echobot@alumchat.xyz"; // Replace with the JID of the new contact
                    String newContactName = "Echobot"; // Replace with the name of the new contact (optional)

                    EntityBareJid jid = JidCreate.entityBareFrom(newContactJID);
                    roster.createItemAndRequestSubscription(jid, newContactName, null);
                } else if (input1 == 2) {
                    System.out.println("Roster size: " + roster.getEntries().size());
                } else if (input1 == 3) {

                    System.out.println("Ingrese el usuario");
                    input.nextLine();
                    String users = input.nextLine();
                    user = users + "@alumchat.xyz";
                    EntityBareJid jid = JidCreate.entityBareFrom(users);
                    RosterEntry entry = roster.getEntry(jid);

                    System.out.println(entry);
                    if (entry != null) {
                        // Print the user's information
                        System.out.println("User Information for: " + user);
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
                    chat.send("Hello, this is a test message!");

                    // Keep the main thread waiting for user input

                } else if (input1 == 5) {
                    // Create a multi-user chat manager
                    MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(connection);

                    EntityBareJid roomJid = JidCreate.entityBareFrom("testing@conference.alumchat.xyz");
                    MultiUserChat muc = mucManager.getMultiUserChat(roomJid);

                    Resourcepart nickname = Resourcepart.from("Roberto");
                    muc.join(nickname);
                    // Set up message listener
                    muc.addMessageListener(new MessageListener() {
                        @Override
                        public void processMessage(Message message) {
                            System.out.println("Received message from " + message.getFrom() + ": " + message.getBody());
                        }
                    });

                    // Leave the room
                    while (true) {
                    }
                } else if (input1 == 6) {
                    System.out.println(":)");
                    EntityBareJid recipientBareJid = JidCreate.entityBareFrom("echobot@alumchat.xyz");
                    Collection<RosterEntry> rosterEntries = roster.getEntries();
                    for (RosterEntry entry : rosterEntries) {
                        System.out.print(entry.getJid());
                        System.out.print("\n");
                        System.out.println(entry.getJid().equals(recipientBareJid));
                        if (entry.getJid().equals(recipientBareJid)) {
                            Presence presence = roster.getPresence(entry.getJid());
                            EntityFullJid resource = presence.getFrom().asEntityFullJidIfPossible();
                            if (resource != null) {
                                System.out.println(resource);
                                // Create a FileTransferManager
                                FileTransferManager transferManager = FileTransferManager.getInstanceFor(connection);

                                // Create an OutgoingFileTransfer using the selected resource
                                OutgoingFileTransfer transfer = transferManager.createOutgoingFileTransfer(resource);

                                // Specify the file to send
                                File fileToSend = new File("C:\\Users\\rober\\Look.txt");

                                // Initiate the file transfer
                                transfer.sendFile(fileToSend, "File Description");

                                // Wait for the transfer to complete
                                while (!transfer.isDone()) {
                                    Thread.sleep(1000); // Add a delay to prevent high CPU usage
                                }

                                // Check if the transfer was successful
                                if (transfer.getStatus() == FileTransfer.Status.complete) {
                                    System.out.println("File transfer successful!");
                                } else {
                                    System.out.println("File transfer failed.");
                                }
                            }
                        }
                    }
                }
            }

            /*
             * String newContactJID = "echobot@alumchat.xyz"; // Replace with the JID of the
             * new contact
             * String newContactName = "Echobot"; // Replace with the name of the new
             * contact (optional)
             * 
             * EntityBareJid jid = JidCreate.entityBareFrom(newContactJID);
             * roster.createItemAndRequestSubscription(jid, newContactName, null);
             * 
             * 
             * System.out.println("Contacts (Roster Entries):");
             * System.out.println("Roster size: " + roster.getEntries().size());
             * 
             * for (RosterEntry entry : roster.getEntries()) {
             * System.out.println("JID: " + entry.getJid());
             * System.out.println("Name: " + entry.getName());
             * System.out.println("Groups: ");
             * for (RosterGroup group : entry.getGroups()) {
             * System.out.println("- " + group.getName());
             * }
             * System.out.println("-------------------------");
             * }
             * /*
             * String recipientJID = "";
             * 
             * // Create a chat session
             * EntityBareJid jid = JidCreate.entityBareFrom("echobot@alumchat.xyz");
             * Chat chat = chatManager.chatWith(jid);
             * 
             * // The message body
             * 
             * // Send the message
             * chat.send("Hello!");
             * 
             * System.out.println("Message sent successfully.");
             * 
             * chatManager.addIncomingListener(new IncomingChatMessageListener() {
             * 
             * @Override
             * public void newIncomingMessage(EntityBareJid from, Message message, Chat
             * chat) {
             * System.out.println("New message from " + from + ": " + message.getBody());
             * }
             * });
             */

            // User authenticated, perform your XMPP operations*/
        } catch (SmackException | XMPPException | IOException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("Connection failed. Check for any errors.");

            // Handle connection or authentication errors
        }

    }
}
