from django.shortcuts import render
from django.db import models
from django.core.files.storage import default_storage
import json
import datetime
from search import helpers
from users import scrypt
import os
from services.settings import firebaseConfig


'''-------------------------------------------'''

import firebase_admin
from firebase_admin import credentials, auth, db

cred = credentials.Certificate(firebaseConfig['serviceAccount'])
firebase = firebase_admin.initialize_app(cred, {
    'databaseURL': firebaseConfig['databaseURL']
})

'''-------------------------------------------'''

import pyrebase
import requests

# from urllib.parse import quote

# # Monkey patch pyrebase: replace quote function in pyrebase to workaround a bug.
# # See https://github.com/thisbejim/Pyrebase/issues/294.
# pyrebase.pyrebase.quote = lambda s, safe=None: s

# # Monkey patch pyrebase: the Storage.get_url method does need quoting :|
# def get_url(self, token=None):
#     path = self.path
#     self.path = None
#     if path.startswith('/'):
#         path = path[1:]
#     if token:
#         return "{0}/o/{1}?alt=media&token={2}".format(self.storage_bucket, quote(path, safe=''), token)
#     return "{0}/o/{1}?alt=media".format(self.storage_bucket, quote(path, safe=''))

# pyrebase.pyrebase.Storage.get_url = lambda self, token=None: \
#     get_url(self, token)

###
# Initialize and Configure pyrebase connections
def init_pyrebase():
    return pyrebase.initialize_app(firebaseConfig)
    # cred = credentials.Certificate(firebaseConfig['serviceAccount'])
    # return firebase_admin.initialize_app(cred, {'databaseURL': firebaseConfig['databaseURL']})

def init_auth():
    return init_pyrebase().auth()

def init_db():
    return init_pyrebase().database()

def init_storage():
    return init_pyrebase().storage()

def init_default():
    return init_auth().sign_in_with_email_and_password('shemarhenry24@yahoo.com','gottapuresoul')
    

'''-------------------------------------------'''

class User(models.Model):

    username = models.CharField(max_length=300)
    name = models.CharField(max_length=300)
    email = models.CharField(max_length=200)
    role = models.CharField(max_length=10)
    can_review = models.BooleanField(default=False)

    grab_user_token = False

    def register_user(self,user_info):
        try:
            self.username = user_info['username']
            self.email = user_info['email']
            password = user_info['password']
            self.role = user_info['role']
            
            auth = init_auth()
            db = init_db()

            if self.username_exists(self):
                return json.loads(json.dumps({"error":"Username \'" +self.username + "\' already being used (try another)"}))

            if user_info['can_review'] == 'Yes' or user_info['can_review'] == 'yes':
                user_info['can_review'] = True
                self.review = user_info['can_review']
            else:
                user_info['can_review'] = False
                self.review = user_info['can_review']
            self.name = user_info['name']

            auth.create_user_with_email_and_password(self.email,password)
            user = auth.sign_in_with_email_and_password(self.email,password)

            print(str(user))
            auth.send_email_verification(user['idToken'])

            user_info.pop("password", None)
            user_info.pop("username", None)
            user_info.update({'can_edit':True,'can_chat':True,'can_upload':True,'can_view':True})
            db.child("users").child(self.username).set(user_info)
            return json.loads(json.dumps({"Registration":"Successful"}))
        except (requests.exceptions.HTTPError) as e:
            print(json.loads(e.strerror)['error']['message'])
            try:
                if json.loads(e.strerror)['error']['message'] == "EMAIL_EXISTS":
                    return json.loads(json.dumps({"error":"Email \'" + self.email + "\' Already Being Used (Try login or reset password)"}))
            except (KeyError,IndexError):
                return json.loads(json.dumps({"error":"Unable to Register User"}))

    def login_user(user_info):
        try:

            email = user_info['email']
            password = user_info['password']

            auth = init_auth()

            user = auth.sign_in_with_email_and_password(email,password)
            print(str(user))

            if User.grab_user_token:
                User.grab_user_token=False
                return user['idToken']
            # auth.get_account_info(user['idToken'])
            
            if user == None or user == {}:\
                    
                return json.loads(json.dumps({"error":"please check login information"}))
            else:
                return json.loads(json.dumps({"Login":"Successful"}))###Try sending some data later

        except (requests.exceptions.HTTPError) as e:
            print(json.loads(e.strerror)['error']['message'])
            try:
                if json.loads(e.strerror)['error']['message'] == "INVALID_PASSWORD":
                    return json.loads(json.dumps({"error":"Wrong Password"}))               
                if json.loads(e.strerror)['error']['message'] == "EMAIL_NOT_FOUND":
                    return json.loads(json.dumps({"error":"Wrong Email"}))
            except (KeyError,IndexError):
                return json.loads(json.dumps({"error":"Unable to Login User"}))

    def reset_password(email):
        try:
            auth = init_auth()
            auth.send_password_reset_email("email")
            return json.loads(json.dumps({"Success":"Request Sent to \'" + email + "\'"}))
        except (requests.exceptions.HTTPError,KeyError,IndexError) as e:
            print(str(e))
            return json.loads(json.dumps({"error":"Unable to Send Reset Request to \'" + email + "\'"}))

    def username_exists(self):
        print(self.username)
        info = db.reference('users/'+self.username)
        print(info.get())
        if info.get() == None or info.get() == {}:
            print('User does not exist')
            return False
        else:
            print('User exists')
            return True

    def get_user_token(email,password):
        auth = init_auth()
        User.grab_user_token=True
        token = User.login_user({"email":email,"password":password})
        User.grab_user_token = False
        return token
        # if User.login_user({"email":email,"password":password}) == {"Login":"Successful"}:
        #     return auth.sign_in_with_email_and_password(email,password)
        # return json.loads(json.dumps({"error":"Invalid user information"}))

'''-------------------------------------------'''

class File(models.Model):

    title = models.CharField(max_length=300)
    file_name = models.CharField(max_length=300)
    authors = models.CharField(max_length=200)
    publishers = models.CharField(max_length=200)
    datePublished = models.DateField("Date Published")
    tags = models.CharField(max_length=200)

    def get_file(name):
        try:
            storage = init_storage()
            # file_ = storage.child(name).download("/tmp/"+name)
            # print(file_.name)
            # print(default_storage.save(file_.name, file_))
            return storage.child(name).download("download/tmp/"+name)
        except (requests.exceptions.HTTPError,KeyError,IndexError) as e:
            print(str(e))
            return json.loads(json.dumps({"error":"Unable to Get File \'" + name + "\'"}))
    
    def post_file(self,file_,title,authors,publishers,datePublished,tags,token):
        try:
            self.title = title
            self.authors = authors
            self.file_name = file_.name
            self.publishers = publishers
            self.tags = tags
            self.datePublished = datetime.datetime.strptime(datePublished, '%Y-%m-%d')

            if self.file_exists(self):
                return json.loads(json.dumps({"error":"File named \'" +self.title + "\' already exists (try another)"}))

            storage = init_storage()
            database = init_db()

            print("11111")
            file_info = {"authors":self.authors,"publishers":self.publishers,"datePublished":datePublished,"tags":self.tags}
            database.child("temporaryFiles").child(self.title).set(file_info)
            print("22222")
            file_name = default_storage.save(file_.name, file_)
            print("33333")
            storage.child(default_storage.url(file_name)).put(self.file_name,token=token)
            print("44444")

            return True
        except (requests.exceptions.HTTPError,KeyError,IndexError) as e:
            print(str(e))
            return False

    def file_exists(self):
        print(self.title)
        temp_info = db.reference('temporaryFiles/'+self.title)
        info = db.reference('files/'+self.title)
        print(info.get())
        if info.get() == None or temp_info.get() == None:
            print('File does not exist')
            return False
        else:
            print('File exists')
            return True

    # def added_file_details(self,title,name,authors,publishers,datePublished,tags):
    #     try:
    #         mycursor = connection.cursor()
    #         query = "INSERT INTO documents (document_title, document_file, document_authors, document_publisher, document_date, document_tags, document_reviews) VALUES (%s, %s, %s, %s, %s, %s, %s)"
    #         mycursor.execute(query, [title, name, authors, publishers,datePublished,tags, "TO BE REVIEWED"])
    #         connection.commit()
    #         self.title = title
    #         self.file_name = name
    #         self.authors = authors
    #         self.publishers = publishers
    #         self.datePublished = datePublished
    #         self.tags =tags
    #         return True
    #     except (MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
    #         connection.rollback()
    #         logging.error(e)
    #         print(str(e))
    #         return False

    # def sign_s3(file_name,file_type):
    #     # Load necessary information into the application
    #     S3_BUCKET = os.environ.get('S3_BUCKET')

    #     # Initialise the S3 client
    #     s3 = boto3.client('s3')

    #     # Generate and return the presigned URL
    #     presigned_post = s3.generate_presigned_post(
    #         Bucket = S3_BUCKET,
    #         Key = file_name,
    #         Fields = {"acl": "public-read", "Content-Type": file_type},
    #         Conditions = [
    #         {"acl": "public-read"},
    #         {"Content-Type": file_type}
    #         ],
    #         ExpiresIn = 3600
    #     )

    #     # Return the data to the client
    #     return json.dumps({
    #         'data': presigned_post,
    #         'url': 'https://%s.s3.amazonaws.com/%s' % (S3_BUCKET, file_name)
    #     })
    
    # def probationList():
    #     string = "TO BE REVIEWED"
    #     cursor = connection.cursor()
    #     query = "SELECT * FROM documents WHERE document_reviews=%s" % string
    #     cursor.execute(query)
    #     lst = cursor.fetchall()
    #     print(lst)
