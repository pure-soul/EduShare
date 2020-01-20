# from flask import Flask
# import socketio
# # from flask_socketio import SocketIO

# # app = Flask(__name__, template_folder='Forms')
# # app.config['SECRET_KEY'] = 'neVEraSkeDaNEgUFOsh!T,ThATiSSAfetOsAy'
# # socketio = SocketIO(app)

# sio = socketio.Server(async_mode='threading')
# app = Flask(__name__)
# app.wsgi_app = socketio.WSGIApp(sio, app.wsgi_app)

# @sio.event
# def connect(sid, environ):
#     print('connect ', sid)
#     raise ConnectionRefusedError('authentication failed')

# @sio.event
# def disconnect(sid):
#     print('disconnect ', sid)

# # ... Socket.IO and Flask handler functions ...

# @sio.event
# def send_message_to(sid, message, reciever):
#     pass

# if __name__ == '__main__':
#     app.run(threaded=True)

# # if __name__ == '__main__':
# #     socketio.run(app)

import socketio
from bocadillo import App, configure, Templates, static

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