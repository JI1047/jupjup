const crypto = require("crypto");
const http = require("http");
const { WebSocket, WebSocketServer } = require("ws");

const host = process.env.HOST || "0.0.0.0";
const port = Number(process.env.PORT || 3001);

const server = http.createServer((req, res) => {
  if (req.url === "/healthz") {
    res.writeHead(200, { "Content-Type": "application/json" });
    res.end(JSON.stringify({ status: "ok" }));
    return;
  }

  if (req.url === "/readyz") {
    res.writeHead(200, { "Content-Type": "application/json" });
    res.end(JSON.stringify({ status: "ready" }));
    return;
  }

  res.writeHead(404, { "Content-Type": "application/json" });
  res.end(JSON.stringify({ error: "not_found" }));
});

const wss = new WebSocketServer({ server, path: "/ws" });

function broadcast(payload, exclude) {
  const body = JSON.stringify(payload);

  for (const client of wss.clients) {
    if (client !== exclude && client.readyState === WebSocket.OPEN) {
      client.send(body);
    }
  }
}

wss.on("connection", (socket, request) => {
  const clientId = crypto.randomUUID();
  const forwardedFor = request.headers["x-forwarded-for"];
  const remoteAddress = forwardedFor || request.socket.remoteAddress || "unknown";

  socket.send(
    JSON.stringify({
      type: "welcome",
      clientId,
      message: "connected"
    })
  );

  broadcast(
    {
      type: "join",
      clientId,
      message: `${clientId} joined`,
      clients: wss.clients.size
    },
    socket
  );

  socket.on("message", (raw) => {
    const text = raw.toString("utf8").trim();
    if (!text) {
      return;
    }

    broadcast({
      type: "chat",
      clientId,
      message: text,
      remoteAddress,
      timestamp: new Date().toISOString()
    });
  });

  socket.on("close", () => {
    broadcast({
      type: "leave",
      clientId,
      message: `${clientId} left`,
      clients: wss.clients.size
    });
  });
});

server.listen(port, host, () => {
  console.log(`jupjup-chat listening on ${host}:${port}`);
});
