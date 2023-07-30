package com.redes;

import java.io.IOException;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) throws XmppStringprepException, InterruptedException {
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
            connection.login("val20441", "gamesfan10");
            System.out.println("It now sorta of works!");
            ChatManager chatManager = ChatManager.getInstanceFor(connection);
            Roster roster = Roster.getInstanceFor(connection);
            // Optionally, add a RosterListener to receive updates on roster changes
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
                    System.out.println("Presence changed: " + presence.getFrom() + " - " + presence.getType());
                }
            });

            // Print all contacts (roster entries)

            /*
             * String newContactJID = "echobot@alumchat.xyz"; // Replace with the JID of the
             * new contact
             * String newContactName = "Echobot"; // Replace with the name of the new
             * contact (optional)
             * 
             * EntityBareJid jid = JidCreate.entityBareFrom(newContactJID);
             * roster.createItemAndRequestSubscription(jid, newContactName, null);
             */

            System.out.println("Contacts (Roster Entries):");
            System.out.println("Roster size: " + roster.getEntries().size());

            for (RosterEntry entry : roster.getEntries()) {
                System.out.println("JID: " + entry.getJid());
                System.out.println("Name: " + entry.getName());
                System.out.println("Groups: ");
                for (RosterGroup group : entry.getGroups()) {
                    System.out.println("- " + group.getName());
                }
                System.out.println("-------------------------");
            }
            /*
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

            // User authenticated, perform your XMPP operations
        } catch (SmackException | XMPPException | IOException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("Connection failed. Check for any errors.");

            // Handle connection or authentication errors
        }

    }
}
