from flask import Flask
from flask_socketio import SocketIO

app = Flask(__name__, template_folder='Forms')
app.config['SECRET_KEY'] = 'neVEraSkeDaNEgUFOsh!T,ThATiSSAfetOsAy'
socketio = SocketIO(app)



if __name__ == '__main__':
    socketio.run(app)