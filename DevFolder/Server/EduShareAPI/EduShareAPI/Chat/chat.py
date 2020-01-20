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