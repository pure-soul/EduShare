from flask import Flask, jsonify, make_response, request, redirect, url_for, abort
from flask_mysqldb import MySQL
from werkzeug.urls import url_parse
from werkzeug.security import generate_password_hash, check_password_hash
from urllib.parse import unquote

app = Flask(__name__)

app.config['MYSQL_HOST'] = "bm8hz3h5flr23o71hqco-mysql.services.clever-cloud.com"
app.config['MYSQL_USER'] = "udrekmlrux4as6je"
app.config['MYSQL_PASSWORD'] = "bQDFy45cP2SlZItMHxqU"
app.config['MYSQL_DB'] = "bm8hz3h5flr23o71hqco"
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['MYSQL_CURSORCLASS'] = 'DictCursor'
# app.config['DEBUG'] = True

mysql = MySQL(app)

@app.route('/login', methods=['POST'])
def login():
    if not request.json:
        abort(400)
    username = request.json['username']
    password = request.json['password']
    mycursor = mysql.connection.cursor()
    query = "SELECT login_name, login_email FROM login WHERE login_name=%s AND login_password=sha1(%s)"
    mycursor.execute(query, (username,password))
    user = mycursor.fetchone()
    if user == "":
        return error()
    else:
        return jsonify(user)

def error():
    return jsonify({'error':'something went wrong'})

@app.errorhandler(500)
def denied(error):
    return make_response(jsonify({"error":"The task could not be completed at this time"}))

@app.route('/register', methods=['POST'])
def register():
    if not request.json:
        abort(400)
    username = request.json['username']
    email = request.json['email']
    password = request.json['password']
    role = request.json['role']
    review = request.json['review']
    name = request.json['name']
    mycursor = mysql.connection.cursor()
    query = "INSERT INTO users (user_name, user_email) VALUES (%s, %s)"
    mycursor.execute(query,(name,email))
    mysql.connection.commit()
    query = "INSERT INTO login (login_name, login_email,login_password,user_id) VALUES (%s, %s, SHA1(%s),LAST_INSERT_ID())"
    mycursor.execute(query,(username,email,password))
    mysql.connection.commit()

    c = v = e = u = "Y"
    if review == "Yes":
        r = "Y"
    else:
        r = "N"


    query = "INSERT INTO users_roles (user_id, role_id, can_chat, can_review, can_view, can_edit, can_upload) VALUES (%s, %s, %s, %s, %s, %s, %s)"
    
    sub_query = "SELECT LAST_INSERT_ID() FROM users"
    
    sub_query2 = "SELECT role_id FROM roles WHERE role_name=%s"
    
    mycursor.execute(sub_query2,(role,))
    fetchedrole = mycursor.fetchone()
    fetchedrole = fetchedrole['role_id']

    mycursor.execute(sub_query)
    fetchedid = mycursor.fetchone()
    fetchedid = fetchedid['LAST_INSERT_ID()']

    mycursor.execute(query,(fetchedid,fetchedrole,c,r,v,e,u)) 
    
    mysql.connection.commit()
    return jsonify({'Registration':'Successful'})

if __name__ == '__main__':
    # app.run(debug=True, host='0.0.0.0', port=8008)
    app.run(host='0.0.0.0', port=8000)
