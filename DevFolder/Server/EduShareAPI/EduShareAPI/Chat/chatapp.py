import socketio
from bocadillo import App, configure, Templates, static

app = App()
configure(app)
templates = Templates(app)

# Create a socket.io async server.
# NOTE: use the asgi driver, as described in:
# https://python-socketio.readthedocs.io/en/latest/server.html#uvicorn-daphne-and-other-asgi-servers
sio = socketio.AsyncServer(async_mode="asgi")

# Create an ASGI-compliant app out of the socket.io server,
# and mount it under the root app.
# NOTE: "socket.io" is the default for `socketio_path`. We only add it
# here for the sake of being explicit.
# As a result, the client can connect at `/sio/socket.io`.
app.mount("/sio", socketio.ASGIApp(sio, socketio_path="socket.io"))

# Server static files for the socket.io client.
# See: https://github.com/socketio/socket.io-client
# NOTE: alternatively, the socket.io client could be served from
# a CDN if you don't have npm/Node.js available in your runtime.
# If so, static files would be linked to in the HTML page, and we wouldn't
# need this line.
# See: https://socket.io/docs/#Javascript-Client
app.mount("/socket.io", static("node_modules/socket.io-client/dist"))


@app.route("/")
async def index(req, res):
    res.html = await templates.render("index.html")


# Event handler for the "message" event.
# See: https://python-socketio.readthedocs.io/en/latest/server.html#defining-event-handlers
@sio.on("message")
async def handle_message(sid, data: str):
    print("message:", data)
    # Broadcast the received message to all connected clients.
    # See: https://python-socketio.readthedocs.io/en/latest/server.html#emitting-events
    await sio.emit("response", data)

# Event Handler for "send_request" event
@sio.on("send_request")
async def send_request(sid, reciever_sid):
    sio.emit("new_request", {"sender": sid}, room=reciever_sid)
    return "OK", 123

# Event Handler for "request_response" event
@sio.on("request_response")
async def process_request(sid, response: bool, requestor_sid):
    if response == True:
        sio.emit("request_accepted", {"acceptor": sid}, room=requestor_sid)
        return "OK", 123

# Event Handler for "send_to" event
@sio.on("send_to")
async def send_message(sid, data, reciever_sid):
    sio.emit("new_message", {"data": data, "sender": sid}, room=reciever_sid)
    return "OK", 123

#Event functions
@sio.event
def begin_chat(sid):
    raise ConnectionRefusedError('authentication failed')
    sio.enter_room(sid, 'chat_users')

@sio.event
def exit_chat(sid):
    sio.leave_room(sid, 'chat_users')

if __name__ == "__main__":
    app.run()