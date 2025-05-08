# 🌐 Network Representation System

This Java project implements a system for representing and manipulating computer networks using IP addresses. It was developed as a programming final task at KIT

## 📋 Overview

The system models computer networks as undirected graphs where:
- Nodes represent computers with IP addresses
- Edges represent connections between computers
- Networks must maintain a valid tree topology

## ✨ Key Features

- Create and manipulate network structures
- Add subnets to existing networks
- Connect and disconnect nodes
- Find routes between nodes
- Calculate network heights and levels
- Parse and generate bracket notation for network representation

## 🧩 Core Components

### 🔢 IP Class
- Represents IP addresses in standard dot notation (e.g., "192.168.1.1")
- Validates IP address format
- Provides comparison and equality operations
- Stores adjacency information for network representation

### 🕸️ Network Class
- Represents a computer network as a graph
- Maintains the network's tree topology
- Provides operations for network manipulation:
  - Adding subnets
  - Connecting/disconnecting nodes
  - Finding routes between nodes
  - Calculating network height and levels

### 📝 Bracket Notation
The system uses a custom bracket notation to represent network structures:
```
(root child1 child2 (subroot subchild1 subchild2))
```

Example: `(141.255.1.133 0.146.197.108 122.117.67.158)` represents a network with:
- Root node: 141.255.1.133
- Two child nodes: 0.146.197.108 and 122.117.67.158

### 🔄 Parser and Printer
- `BracketNotationParser`: Converts bracket notation strings to network structures
- `BracketNotationPrinter`: Converts network structures to bracket notation strings

## 💻 Usage Example

```java
// Create a network with a root and two child nodes
IP root = new IP("141.255.1.133");
List<IP> children = List.of(new IP("0.146.197.108"), new IP("122.117.67.158"));
Network network = new Network(root, children);

// Display the network in bracket notation
System.out.println(network.toString(root));
// Output: (141.255.1.133 0.146.197.108 122.117.67.158)

// Add a subnet to the network
network.add(new Network("(85.193.148.81 34.49.145.239 231.189.0.127 141.255.1.133)"));

// Find a route between two nodes
List<IP> route = network.getRoute(new IP("141.255.1.133"), new IP("231.189.0.127"));
```

## 📂 Project Structure

- `src/ip/`: IP address representation
- `src/network/`: Network representation and operations
- `src/parsing/`: Bracket notation parsing
- `src/printing/`: Bracket notation generation
- `src/exceptions/`: Custom exceptions
- `src/cleanTests/` and `src/tests/`: Test cases