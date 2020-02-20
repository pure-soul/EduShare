from flask import Flask, jsonify, make_response, request, redirect, url_for, abort, render_template, Response
from flask_mysqldb import MySQL
import MySQLdb
from werkzeug.urls import url_parse
from werkzeug.security import generate_password_hash, check_password_hash
from urllib.parse import unquote
import pdb
from flask_bootstrap import Bootstrap
import boto3
from config import S3_BUCKET, S3_KEY, S3_SECRET

s3=boto3.client('s3',aws_access_key_id=S3_KEY, aws_secret_access_key=S3_SECRET)
app = Flask(__name__)

app.config['MYSQL_HOST'] = "bm8hz3h5flr23o71hqco-mysql.services.clever-cloud.com"
app.config['MYSQL_USER'] = "udrekmlrux4as6je"
app.config['MYSQL_PASSWORD'] = "bQDFy45cP2SlZItMHxqU"
app.config['MYSQL_DB'] = "bm8hz3h5flr23o71hqco"
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['MYSQL_CURSORCLASS'] = 'DictCursor'
app.config['DEBUG'] = True

Bootstrap(app)
mysql = MySQL(app)

@app.route('/')
def index():
    return render_template('index.html')

@app.route('/files')
def files():
    s3_resource=boto3.resource('s3')
    my_bucket=s3_resource.Bucket(S3_BUCKET)
    summaries = my_bucket.objects.all()
    return render_template('files.html',my_bucket=my_bucket,files=summaries) 

@app.route('/upload', methods=['POST'])
def upload():
    file = request.files['file']
    s3_resource=boto3.resource('s3')
    my_bucket=s3_resource.Bucket(S3_BUCKET)
    my_bucket.Object(file.filename).put(Body=file)
    return redirect(url_for("files"))

@app.route('/download', methods=['POST'])
def download():
    # if not request.json:
    #     abort(400)
    # try:
    key = request.form['key']
    # key = request.json['key']
    s3_resource = boto3.resource('s3')
    my_bucket=s3_resource.Bucket(S3_BUCKET)
    file_obj = my_bucket.Object(key).get()
    return Response(
        file_obj['Body'].read(),
        mimetype='text/plain',
        headers={"Content-Disposition":"attachment;filename={}".format(key)}
    )
    # except (MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
    #     return jsonify({'error':str(e),'type': type(e).__name__})

@app.route('/documentsearch',methods=['GET','POST'])
def document_search():
    if not request.json:
        abort(400)
    return True

@app.route('/search',methods=['GET','POST'])
def search():
    if not request.json:
        abort(400)
    try:
        item = request.json["search"]
        mycursor = mysql.connection.cursor()
        mycursor.execute("SELECT * FROM documents WHERE document_tags LIKE %s ", ("%" + item + "%",))
        #mycursor.execute(query)
        document_result = mycursor.fetchall()
        mycursor = mysql.connection.cursor()
        mycursor.execute("SELECT * FROM media WHERE media_tags LIKE %s ", ("%" + item + "%",))
        #mycursor.execute(query)
        media_result = mycursor.fetchall()
        return jsonify({'Documents':document_result, 'Media':media_result})
    except (MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
        return jsonify({'error':str(e),'type': type(e).__name__})

@app.route('/uploads', methods=['GET','POST'])
def uploads():
    if not request.files:
        abort(400)
    f = request.files['inputFile']
    file_name = f.filename
    file_file = f.read()
    author = "Kenny Rodgers"
    reviews = "Mostly Bad"
    mycursor = mysql.connection.cursor()
    query = "INSERT INTO documents (document_title, document_authors, document_reviews, document_file) VALUES (%s, %s, %s, %s)" 
    mycursor.execute(query, (file_name,author,reviews,file_file))
    mysql.connection.commit()
    return f.filename

@app.route('/uploadmedia', methods=['GET','POST'])
def uploadmedia():
    if not request.files:
        abort(400)
    f = request.files['secondFile']
    file_name = f.filename
    file_file = f.read()
    author = "Felleria Stephens"
    mycursor = mysql.connection.cursor()
    query = "INSERT INTO media (media_title, media_authors, media_file) VALUES (%s, %s, %s)" 
    mycursor.execute(query, (file_name,author,file_file))
    mysql.connection.commit()
    return f.filename

@app.route('/login', methods=['POST'])
def login():
    if not request.json:
        abort(400)
    try:
        username = request.json['username']
        password = request.json['password']
        if not is_valid_username(username):
            print('invalid username')
            return jsonify({'error':'user does not exist'})
            
        if not is_valid_password(password):
            print('invalid password')
            return jsonify({'error':'incorrect password'})
            
        mycursor = mysql.connection.cursor()
        query = "SELECT login_name, login_email FROM login WHERE login_name=%s AND login_password=sha1(%s)"
        mycursor.execute(query, (username,password))
        user = mycursor.fetchone()
        if user == None:
            return jsonify({'error':'please check login information'})
        else:
            return jsonify(user)
    except (MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
        print('aborting')
        return jsonify({'error':str(e),'type': type(e).__name__})

def error():
    return jsonify({'error':'something went wrong'})

@app.errorhandler(500)
def denied(error):
    return make_response(jsonify({"error":"The task could not be completed at this time",'code':500}),500)

@app.errorhandler(400)
def invalid_upload(error):
    return make_response(jsonify({"error":"Bad Request",'code':400}), 400)

@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({'error': 'Not Found','code':404}), 404)

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
    try:
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
    except (MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
        e=str(e)
        return jsonify({"error":e})

    return jsonify({'Registration':'Successful'})

def is_valid_username(username):
    try:
        mycursor = mysql.connection.cursor()
        query = "SELECT login_name, login_email FROM login  WHERE login_name=%s"
        mycursor.execute(query, username)
        user = mycursor.fetchone()
        print('Valid Username: ' + str(user))
        if user == None:
            return False
        else:
            return True
    except (MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
        return jsonify({'error':str(e),'type': type(e).__name__})

def is_valid_password(password):
    try:
        mycursor = mysql.connection.cursor()
        query = "SELECT login_name, login_email FROM login WHERE login_password=sha1(%s)"
        mycursor.execute(query, password)
        user = mycursor.fetchone()
        print('Valid Password: ' + str(user))
        if user == None:
            return False
        else:
            return True
    except (MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
        return jsonify({'error':str(e),'type': type(e).__name__})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000)