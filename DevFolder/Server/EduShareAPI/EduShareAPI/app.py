from flask import Flask, jsonify, abort, make_response, render_template, redirect
import html

import socket
#import fcntl
#import struct
#import requests

items = [
    {
        'id' : u'7459237505',
        'title': u'Lasco Milk',
        'content': u'Milk Powder by Lasco',
        'link': u'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRjFKz_2QaOroxoCC11ed4Jmti3gIAMM765Hsu-Hnlvf2VikD4A7g',
        'tags': u'#milk#lasco#lasco milk'
    },
    {
        'id' : u'9859237505',
        'title': u'Serge Milk',
        'content': u'Milk by Serger',
        'link': u'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRjFKz_2QaOroxoCC11ed4Jmti3gIAMM765Hsu-Hnlvf2VikD4A7g',
        'tags' : u'#milk#serge#serge milk'
    },
    {
        'id' : u'9369256538',
        'title': u'Trix Cereal',
        'content': u'Cereal by Trix',
        'link': u'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRjFKz_2QaOroxoCC11ed4Jmti3gIAMM765Hsu-Hnlvf2VikD4A7g',
        'tags': u'#cereal#trix#trix cereal'
    },
    {
        'id' : u'73946844246',
        'title': u'Grace Mackerel',
        'content': u'Tin Macerel by Grace',
        'link': u'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRjFKz_2QaOroxoCC11ed4Jmti3gIAMM765Hsu-Hnlvf2VikD4A7g',
        'tags': u'#mackerel#grace#grace mackerel'
    },
    {
        'id' : u'328754413',
        'title': u'National Bread',
        'content': u'Bread by NationPal Bakery',
        'link': u'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRjFKz_2QaOroxoCC11ed4Jmti3gIAMM765Hsu-Hnlvf2VikD4A7g',
        'tags': u'#bread#national#national bread#popular'
    }
]

users = [
    {
        'id': 111111111111,
        'name': 'puresoul',
        'password': 'gottapuresoul',
        'email': 'pure@soulmail.com'
    }
]

api = [
    {
        'key': 'AIzaSyBpvYniltDN972kSlE7tOrdmJsqrjLS3GU',
        'engine_id': '015449310451191663041:twljm0b8cev',
        'search': 'https://www.googleapis.com/customsearch/v1?key=AIzaSyBpvYniltDN972kSlE7tOrdmJsqrjLS3GU&cx=015449310451191663041:twljm0b8cev&q='
    }
]

app = Flask(__name__, template_folder='Forms')

# Make the WSGI interface available at the top level so wfastcgi can get it.
wsgi_app = app.wsgi_app

#Server Ports

@app.route('/')
def hello():
    """Renders a sample page."""
    return render_template('home.html')
    #return "Welcome to Edushare!"

@app.route('/getip')
def get_ip():
    #get_ip_address('eth0')
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    s.connect(('0.0.0.0', 80))
    return s.getsockname()[0]

@app.route('/edushare/api/v1.0/search', methods = ['GET','POST'])
def search():
    """Renders a search page (as templated from https://codepen.io/adobewordpress/pen/gbewLV)"""
    #return app.send_static_file('Forms/SearchPage.html')
    return render_template('googlesearch.html')

@app.route('/edushare/api/v1.0/search/<search>', methods = ['GET'])
def get_items(search):
    item = get_items_with(search)
    return jsonify(item)

@app.route('/edushare/api/v1.0/apisearch/<search>', methods = ['GET'])
def get_items_api(search):
    #search_url = api['search'] + search
    search_url = 'https://www.googleapis.com/customsearch/v1?key=AIzaSyBpvYniltDN972kSlE7tOrdmJsqrjLS3GU&cx=015449310451191663041:twljm0b8cev&q=' + search
    return redirect(search_url, code=302)

    #3 search = requests.get('https://www.googleapis.com/customsearch/v1?key=AIzaSyBpvYniltDN972kSlE7tOrdmJsqrjLS3GU&cx=015449310451191663041:twljm0b8cev&q=' + search)
    #3 return search['items']

    #4 response = urllib.request.urlretrieve(search_url)
    #4 data = json.loads(response)
    #4 return data['items']


@app.route('/edushare/api/v1.0/<username>/<password>', methods = ['GET','POST'])
def login(username, password):
    login_url = '0.0.0.0:8008/~/' + username + '/' + password
    return redirect(login_url, code=302)

    # if exists(username, password):
    #     return jsonify(getUser(username))

@app.route('/edushare/api/v1.0/register/<username>/<password>/<email>/<role>/<review>', methods=['GET', 'POST'])
def register(username, password, email, role, review):
    register_url = '0.0.0.0:8008/~/' + username + '/' + password + '/' + email + '/' + role+ '/' + review
    return redirect(register_url, code=302)

    # user_ = {
    #     'id': users[-1]['id'] + 1,
    #     'name': username,
    #     'email': email,
    #     'password': password
    # }
    # users.append(user_)
    # return jsonify(user_)

#Supporting Functions

def get_items_with(string):
    item = [item for item in items if string in item['tags']]
    if len(item) == 0:
        abort(404)
    return item

def exists(name, password):
    user = [user for user in users if name == user['name'] and password == user['password']]
    if len(user) == 0:
        abort(404)
    return True

def getUser(string):
    user = [user for user in users if string == user['name']]
    if len(user) == 0:
        abort(404)
    return user

def get_ip_address(ifname):
    s = socket.socket(socket.IPPORT_USERRESERVED, socket.SOCK_DGRAM)
    return socket.inet_ntoa(fcntl.ioctl(s.fileno(), 0x8915, struct.pack('256s', ifname[:15]))[20:24])

#Error Handlers

@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({'error': 'Not Found'}), 404)

@app.errorhandler(400)
def invalid_upload(error):
    return make_response(jsonify({'error': 'Invalid Submission'}), 400)

if __name__ == '__main__':
    import os
    HOST = os.environ.get('SERVER_HOST', 'localhost')
    try:
        PORT = int(os.environ.get('SERVER_PORT', '5555'))
    except ValueError:
        PORT = 5555
    #app.run(HOST, PORT)
    app.run(host='0.0.0.0', port=80)