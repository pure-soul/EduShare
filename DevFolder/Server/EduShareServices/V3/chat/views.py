# from django.shortcuts import render
# async_mode = None
# import os
# from django.http import HttpResponse
# import socketio

# basedir = os.path.dirname(os.path.realpath(__file__))
# sio = socketio.Server(async_mode='eventlet')\

# @sio.on('connection-bind')
# def connection_bind(sid, data):
#                // code to capture the data
#    // sid is a unique id for each connection and data contains additional payload of the message.    
    
# @sio.on('disconnect')
# def test_disconnect(sid):
#     // code to capture the data
#    // sid is a unique id for each connection and data contains additional payload of the message.

# sio = socketio.Server(logger=True, async_mode=async_mode)
# thread = None

# # Create your views here.

# def index(request):
#     global thread
#     if thread is None:
#         thread = sio.start_background_task(background_thread)
#     return render(request, 'index.html')

# ###
# # Socket Handlers

# @sio.event
# def my_event(sid, message):
#     sio.emit('edushare_response', {'data': message['data']}, room=sid)


# @sio.event
# def my_broadcast_event(sid, message):
#     sio.emit('edushare_response', {'data': message['data']})


# @sio.event
# def join(sid, message):
#     sio.enter_room(sid, message['room'])
#     sio.emit('edushare_response', {'data': 'Entered room: ' + message['room']},
#              room=sid)


# @sio.event
# def leave(sid, message):
#     sio.leave_room(sid, message['room'])
#     sio.emit('edushare_response', {'data': 'Left room: ' + message['room']},
#              room=sid)


# @sio.event
# def close_room(sid, message):
#     sio.emit('edushare_response',
#              {'data': 'Room ' + message['room'] + ' is closing.'},
#              room=message['room'])
#     sio.close_room(message['room'])


# @sio.event
# def my_room_event(sid, message):
#     sio.emit('edushare_response', {'data': message['data']}, room=message['room'])


# @sio.event
# def disconnect_request(sid):
#     sio.disconnect(sid)


# @sio.event
# def connect(sid, environ):
#     sio.emit('edushare_response', {'data': 'Connected', 'count': 0, 'sender':'edushare_chat'}, room=sid)


# @sio.event
# def disconnect(sid):
#     print('Client disconnected')

# # Event handler for the "message" event.
# # See: https://python-socketio.readthedocs.io/en/latest/server.html#defining-event-handlers
# @sio.on("message")
# def handle_message(sid, data):
#     global user_sid
#     if user_sid != sid:
#         print("sid: ", sid)
#         user_sid = sid
#     print("message: ", data['data'])
#     print("room: ", data['room'])
#     # Broadcast the received message to room.
#     # See: https://python-socketio.readthedocs.io/en/latest/server.html#emitting-events
#     sio.emit("edushare_response", {'sender': sid, 'data': data['data']}, room=data['room'])