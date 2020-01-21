from flask import Flask, jsonify, abort, make_response, render_template, redirect, request
from content import items, users, api
import db_content
import html
#import requests

app = Flask(__name__, template_folder='Forms')

# Make the WSGI interface available at the top level so wfastcgi can get it.
wsgi_app = app.wsgi_app

#Server Ports

@app.route('/')
def hello():
    """Renders a sample page."""
    return render_template('home.html')
    #return "Welcome to Edushare!"

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
    if exists(username, password):
        return jsonify(getUser(username))

@app.route('/edushare/api/v1.0/register/<username>/<password>/<email>', methods=['GET', 'POST'])
def register(username, password, email):
    user_ = {
        'id': users[-1]['id'] + 1,
        'name': username,
        'email': email,
        'password': password
    }
    users.append(user_)
    return jsonify(user_)

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
    app.run(HOST, PORT)