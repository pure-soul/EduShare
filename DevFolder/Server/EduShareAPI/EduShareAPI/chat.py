from flask import Flask
import socketio
# from flask_socketio import SocketIO

# app = Flask(__name__, template_folder='Forms')
# app.config['SECRET_KEY'] = 'neVEraSkeDaNEgUFOsh!T,ThATiSSAfetOsAy'
# socketio = SocketIO(app)

sio = socketio.Server(async_mode='threading')
app = Flask(__name__)
app.wsgi_app = socketio.WSGIApp(sio, app.wsgi_app)

# ... Socket.IO and Flask handler functions ...

if __name__ == '__main__':
    app.run(threaded=True)

# if __name__ == '__main__':
#     socketio.run(app)