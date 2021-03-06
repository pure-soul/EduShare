# set async_mode to 'threading', 'eventlet', 'gevent' or 'gevent_uwsgi' to
# force a mode else, the best mode is selected automatically from what's
# installed
async_mode = None

import time
from flask import Flask, render_template
import socketio


user_sid = ''
my_ip = '45.56.155.177'

sio = socketio.Server(logger=True, async_mode=async_mode)
app = Flask(__name__)
app.wsgi_app = socketio.WSGIApp(sio, app.wsgi_app)
app.config['SECRET_KEY'] = 'neVEraSkeDaNIgGaFOsh!T,ThATiSSAfetOsAy!'
thread = None

async_mode = 'eventlet'

def background_thread():
    """Example of how to send server generated events to clients."""
    count = 0
    while True:
        sio.sleep(10)
        count += 1
        #sio.emit('edushare_response', {'data': 'Server generated event'})


@app.route('/')
def index():
    global thread
    if thread is None:
        thread = sio.start_background_task(background_thread)
    return render_template('index.html')

@app.route('/chat/henry')
def chat1():
    global thread
    if thread is None:
        thread = sio.start_background_task(background_thread)
    return render_template('chat.html')

@app.route('/chat/soul')
def chat2():
    global thread
    if thread is None:
        thread = sio.start_background_task(background_thread)
    return render_template('chat.html')

@sio.event
def my_event(sid, message):
    sio.emit('edushare_response', {'data': message['data']}, room=sid)


@sio.event
def my_broadcast_event(sid, message):
    sio.emit('edushare_response', {'data': message['data']})


@sio.event
def join(sid, message):
    sio.enter_room(sid, message['room'])
    sio.emit('edushare_response', {'data': 'Entered room: ' + message['room']},
             room=sid)


@sio.event
def leave(sid, message):
    sio.leave_room(sid, message['room'])
    sio.emit('edushare_response', {'data': 'Left room: ' + message['room']},
             room=sid)


@sio.event
def close_room(sid, message):
    sio.emit('edushare_response',
             {'data': 'Room ' + message['room'] + ' is closing.'},
             room=message['room'])
    sio.close_room(message['room'])


@sio.event
def my_room_event(sid, message):
    sio.emit('edushare_response', {'data': message['data']}, room=message['room'])


@sio.event
def disconnect_request(sid):
    sio.disconnect(sid)


@sio.event
def connect(sid, environ):
    sio.emit('edushare_response', {'data': 'Connected', 'count': 0, 'sender':'edushare_chat'}, room=sid)


@sio.event
def disconnect(sid):
    print('Client disconnected')

# Event handler for the "message" event.
# See: https://python-socketio.readthedocs.io/en/latest/server.html#defining-event-handlers
@sio.on("message")
def handle_message(sid, data):
    global user_sid
    if user_sid != sid:
        print("sid: ", sid)
        user_sid = sid
    print("message: ", data['data'])
    print("room: ", data['room'])
    # Broadcast the received message to room.
    # See: https://python-socketio.readthedocs.io/en/latest/server.html#emitting-events
    sio.emit("edushare_response", {'sender': sid, 'data': data['data']}, room=data['room'])

if __name__ == '__main__':
    if async_mode == 'threading':
        # deploy with Werkzeug
        app.run(threaded=True, host='0.0.0.0', port=50)
    elif async_mode == 'eventlet':
        # deploy with eventlet
        import eventlet
        import eventlet.wsgi
        eventlet.wsgi.server(eventlet.listen(('0.0.0.0', 50)), app)
    elif async_mode == 'gevent':
        # deploy with gevent
        from gevent import pywsgi
        try:
            from geventwebsocket.handler import WebSocketHandler
            websocket = True
        except ImportError:
            websocket = False
        if websocket:
            pywsgi.WSGIServer(('0.0.0.0', 50), app,
                              handler_class=WebSocketHandler).serve_forever()
        else:
            pywsgi.WSGIServer(('0.0.0.0', 50), app).serve_forever()
    elif async_mode == 'gevent_uwsgi':
        print('Start the application through the uwsgi server. Example:')
        print('uwsgi --http :5000 --gevent 1000 --http-websockets --master '
              '--wsgi-file app.py --callable app')
    else:
        print('Unknown async_mode: ' + sio.async_mode)