from flask import Flask, jsonify, abort, make_response, render_template, redirect, request
import html
import requests
# Encrytion and Decrytion Code
from crypt import *
# Sample Items
from content import *


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
    try:
        item = get_items_with(search)
        return jsonify(item)
    except TypeError:
        abort(400)

@app.route('/edushare/api/v1.0/apisearch/<search>', methods = ['GET'])
def get_items_api(search):
    try:
        search_url = api.get('search_template').replace('{key}', api.get('key')).replace('{id}',api.get('engine_id')).replace('{search_term}',search)
        r = requests.get(search_url)
        _results = r.json()
        return jsonify({'items':_results['items'],'queries':_results['queries']['request'][0]}) #redirect(search_url, code=302)
    except TypeError:
        abort(400)

@app.route('/edushare/api/v1.0/apisearch/next', methods = ['POST'])
def get_next():
    if not request.json:
        abort(400)
    try:
        results = get_next_ten(request.json['queries'])
        return jsonify({'items':results['items'],'queries':results['queries']['request'][0]})
    except TypeError:
        abort(400)

@app.route('/edushare/api/v1.0/apisearch/previous', methods = ['POST'])
def get_previous():
    if not request.json:
        abort(400)
    try:
        results = get_previous_ten(request.json['queries'])
        return jsonify({'items':results['items'],'queries':results['queries']['request'][0]})
    except TypeError:
        abort(400)

@app.route('/edushare/api/v1.0/<username>/<password>', methods = ['GET','POST'])
def login(username, password):
    try:
        cipher = encrypt_with_AES(password, secret_key, salt)
        print("Cipher: " + cipher)

        decrypted = decrypt_with_AES(cipher, secret_key, salt)
        print("Decrypted: " + decrypted)

        login_url = 'http://0.0.0.0:8000/login' #+ username + '/' + password
        l = requests.post(login_url, json = {'username':username,'password':password})
        return l.json() #redirect(login_url, code=302)
    except TypeError:
        abort(400)

@app.route('/edushare/api/v1.0/login', methods = ['GET','POST'])
def login_():
    if not request.json:
        abort(400)
    try:
        cipher = encrypt_with_AES(request.json['password'], secret_key, salt)
        print("Cipher: " + cipher)

        decrypted = decrypt_with_AES(cipher, secret_key, salt)
        print("Decrypted " + decrypted)
        # request.json['password'] = decrypted

        login_url = 'http://0.0.0.0:8000/login'
        # login_url = 'http://localhost:8000/login'
        l = requests.post(login_url, json = request.json)
        return l.json() #redirect(login_url, code=302)
    except TypeError:
        abort(400)

@app.route('/edushare/api/v1.0/register/<username>/<password>/<email>/<role>/<review>/<name>', methods=['GET', 'POST'])
def register(username, password, email, role, review, name):
    try:
        register_url = 'http://0.0.0.0:8000/register' # + username + '/' + password + '/' + email + '/' + role + '/' + review
        s = requests.post(register_url, json = {'username':username,'password':password,'email':email,'role':role,'review':review,'name':name})
        print(s)
        return s.json()
    except TypeError:
        abort(400)

@app.route('/edushare/api/v1.0/register', methods=['GET', 'POST'])
def register_():
    if not request.json:
        abort(400)
    try:
        register_url = 'http://0.0.0.0:8000/register'
        s = requests.post(register_url, json = request.json)
        print(s)
        return s.json()
    except TypeError:
        abort(400)

def get_items_with(string):
    item = [item for item in items if string in item['tags']]
    if len(item) == 0:
        abort(404)
    return item

def get_next_ten(query_data):
    if query_data['startIndex'] == int(query_data['totalResults']):
        return(jsonify({'error': 'No More Results', 'items': '-'}))
    else:
        startIndex =  query_data['startIndex'] + 10
        _url_template = api.get('search_template_2').replace('{searchTerms}',query_data['searchTerms']).replace('{count?}',str(query_data['count'])).replace('{startIndex?}',str(startIndex)).replace('{cx?}',api.get('engine_id')).replace('{safe?}',query_data['safe']).replace('{key}',api.get('key'))
        search_results = requests.get(_url_template)     
        return search_results.json()

def get_previous_ten(query_data):
    if query_data['startIndex'] == 1:
        return(jsonify({'error': 'Invalid Request', 'items': '-'}))
    else :
        startIndex =  query_data['startIndex'] - 10
        _url_template = api.get('search_template_2').replace('{searchTerms}',query_data['searchTerms']).replace('{count?}',str(query_data['count'])).replace('{startIndex?}',str(startIndex)).replace('{cx?}',api.get('engine_id')).replace('{safe?}',query_data['safe']).replace('{key}',api.get('key'))
        search_results = requests.get(_url_template)     
        return search_results.json()

#Error Handlers

@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({'error': 'Not Found'}), 404)

@app.errorhandler(400)
def invalid_upload(error):
    return make_response(jsonify({'error': 'Invalid Submission'}), 400)

if __name__ == '__main__':
    import os
    HOST = os.environ.get('SERVER_HOST', '0.0.0.0')
    try:
        PORT = int(os.environ.get('SERVER_PORT', '80'))
    except ValueError:
        PORT = 80
    app.run(HOST, PORT)
    # app.run(host='0.0.0.0', port=80)