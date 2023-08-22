# Proyecto1-Redes

Proyecto 1 de Redes

## Table of Contents

- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Features](#features)
- [Instructions](#instructions)

## Getting Started

This is Project #1 for my Networking Class in UVG. In this proyect, we utilize a XMPP server to understand basics of a networks protocal in this case, eXtensible Messaging and Presence Protocol or XMPP. It uses that basics of account management, creating and deleting accounts, logging in and logging out. Likewise, you can send chat to one or multiple people in a groups and send files, add contacts to talk, also add as well as well seeing the info of that contact, and finally send a presence.

## Prerequisites

- Java Development Kit (JDK) 8 or later
- Git (for cloning the repository)
- Apache Maven (for building the project)

## Installation

```bash
git clone https://github.com/Roberto-VC/Proyecto1-Redes.git
cd Proyecto1-Redes
### Install dependencies
mvn clean package

mvn compile

mvn exec:java

mvn package

### Run .jar
java -jar target/App.jar
java -cp target/App.jar

```

## Features

### Account Management

- [x] Register a new account in the server
- [x] Log In with an account
- [x] Log Out with an account
- [x] Delete an Account in the Server

### Communication

- [x] Show all user/contacts and their state
- [x] Add an user to the contacts
- [x] Show details of contact of an user
- [x] Communication 1-to-1 with any user/contact
- [x] Participate in group conversation
- [x] Define Presence Message
- [x] Send/Recieve Notifiactions
- [x] Send/Recieve Fiesl

## Instructions

First you muy follow the isntructions to compile, build and execute the program. After that you will find the menu to create an account, delete and account and log in. You must have an active account in order to log in, and you must create an accoutn that doesn't exist as well. After that's done and you log in, you will get different menus for options. For the options of adding an user, you must know the username to do. Likewise for sending the messages to that user, where you must be to a contact, you can't simply send a random message. Likewise, for a group, you must join a group each time you connect. If you aren't part of the group, you can write messages, but they not be recieved. For the presence messages, you can modify them from another menu. Finally, when you send messages, you need to write the entire direction of the file, in order to work and for that to be sent properly as well. 
