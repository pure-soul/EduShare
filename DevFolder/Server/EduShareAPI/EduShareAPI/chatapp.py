import socketio
from bocadillo import App, configure, Templates, static
#replace bodacillo (Starlette, FastAPI)

app = App()
configure(app)
templates = Templates()

sio = socketio.AsyncServer(async_mode="asgi")
app.mount("/sio", socketio.ASGIApp(sio))

@app.route("/")
async def index(req, res):
    res.html = await templates.render("index.html")

@sio.on("message")
async def broadcast(sid, data: str):
    print("message:", data)
    await sio.emit("response", data)

app.mount("/socket.io", static("node_modules/socket.io-client/dist"))