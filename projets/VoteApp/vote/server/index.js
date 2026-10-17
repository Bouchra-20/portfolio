import http from 'http';
import RequestController from './requestController.js';
import { Server as IOServer } from 'socket.io';
import IOController from './ioController.js';

// Create HTTP server instance to handle incoming requests
const server = http.createServer(
	(request, response) => new RequestController(request, response).handleRequest()
);

// Initialize Socket.IO server with HTTP server
const io = new IOServer(server);

// Create IO controller instance to manage socket connections
const ioController = new IOController(io);

// Handle new socket connections
io.on('connection', socket => {
  // Register new socket with controller
  ioController.registerSocket(socket);
});

// Start server listening on port 8080
server.listen(8080);

