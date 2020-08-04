from django.shortcuts import render
from django.db import models, connection, utils
from django.core.files.storage import default_storage
import MySQLdb
import json
from . import helpers
from . import scrypt
import logging
import boto3
import botocore
import os

# Create your models here.
class Account(models.Model):

    username = models.CharField(max_length=300)
    name = models.CharField(max_length=300)
    email = models.CharField(max_length=200)
    role = models.CharField(max_length=10)
    review = models.CharField(max_length=1)
    
    def register_user(self,user_info):
        try:
            username = user_info['username']
            email = user_info['email']
            password = user_info['password']
            role = user_info['role']
            review = user_info['review']
            name = user_info['name']

            if self.email_exists(email):
                return json.loads(json.dumps({"error":"email already being used"}))

            if self.username_exists(username):
                return json.loads(json.dumps({"error":"username already being used"}))

            mycursor = connection.cursor()
            query = "INSERT INTO users (user_name, user_email) VALUES (%s, %s)"
            mycursor.execute(query,[name,email])
            query = "INSERT INTO login (login_name, login_email,login_password,user_id) VALUES (%s, %s, SHA1(%s),LAST_INSERT_ID())"
            mycursor.execute(query,[username,email,password])
            c = v = e = u = "Y"
            if review == "Yes":
                r = "Y"
            else:
                r = "N"
            query = "INSERT INTO users_roles (user_id, role_id, can_chat, can_review, can_view, can_edit, can_upload) VALUES (%s, %s, %s, %s, %s, %s, %s)"
            sub_query = "SELECT LAST_INSERT_ID() FROM users"
            sub_query2 = "SELECT role_id FROM roles WHERE role_name=%s"
            mycursor.execute(sub_query2,[role])
            fetchedrole = helpers.dictfetchall(mycursor)[0]
            fetchedrole = fetchedrole['role_id']
            mycursor.execute(sub_query)
            fetchedid =  helpers.dictfetchall(mycursor)[0]
            fetchedid = fetchedid['LAST_INSERT_ID()']         
            mycursor.execute(query,[fetchedid,fetchedrole,c,r,v,e,u])
            connection.commit()
        except (MySQLdb.Error, KeyError,utils.IntegrityError) as e:
            connection.rollback()
            print(str(e))
            return json.loads(json.dumps({"error":"Could Not Register user"}))

        self.username = username
        self.email = email
        self.role = role
        self.review = review
        self.name = name
        return json.loads(json.dumps({"Registration":"Successful"}))

    def login_user(user_info):
        try:
            username = user_info['username']
            password = user_info['password']
            
            if not Account.username_exists(username):
                return json.loads(json.dumps({"error":"user does not exist"}))
                
            if not Account.password_exists(password):
                return json.loads(json.dumps({"error":"incorrect password"}))
                
            mycursor = connection.cursor()
            query = "SELECT login_name, login_email FROM login WHERE login_name=%s AND login_password=sha1(%s)"
            mycursor.execute(query, [username,password])
            user = helpers.dictfetchall(mycursor)[0]
            
            if user == None:
                return json.loads(json.dumps({"error":"please check login information"}))
            else:
                return json.loads(str(user).replace("\'", "\""))

        except (MySQLdb.Error, MySQLdb.Warning, KeyError,utils.IntegrityError,IndexError) as e:
            print(str(e))
            return json.loads(json.dumps({"error":"Could Not Login user"}))

    def username_exists(username):
        try:
            mycursor = connection.cursor()
            query = "SELECT login_name, login_email FROM login  WHERE login_name=%s"
            mycursor.execute(query, [username])
            user = mycursor.fetchone()
            if user == None:
                return False
            else:
                return True
        except (MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
            print(str(e))
            return json.loads(json.dumps({"error":"Could Not Fetch Account"}))

    def password_exists(password):
        try:
            mycursor = connection.cursor()
            query = "SELECT login_name, login_email FROM login WHERE login_password=sha1(%s)"
            mycursor.execute(query, [password])
            user = mycursor.fetchone()
            if user == None:
                return False
            else:
                return True
        except (MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
            print(str(e))
            return json.loads(json.dumps({"error":"Could Not Access Account Credentials (P)"}))

    def name_exists(name):
        try:
            mycursor = connection.cursor()
            query = "SELECT login_name FROM login WHERE login_name=%s"
            mycursor.execute(query, [name])
            user = mycursor.fetchone()
            if user == None:
                return False
            else:
                return True
        except (MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
            print(str(e))
            return json.loads(json.dumps({"error":"Could Not Access Account Credentials (N)"}))

    def email_exists(email):
        try:
            mycursor = connection.cursor()
            query = "SELECT login_email FROM login WHERE login_email=%s"
            mycursor.execute(query, [email])
            user = mycursor.fetchone()
            if user == None:
                return False
            else:
                return True
        except (MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
            print(str(e))
            return json.loads(json.dumps({"error":"Could Not Access Account Credentials (E)"}))

class File(models.Model):

    title = models.CharField(max_length=300)
    file_name = models.CharField(max_length=300)
    authors = models.CharField(max_length=200)
    publishers = models.CharField(max_length=200)
    datePublished = models.DateField("Date Published")
    tags = models.CharField(max_length=200)

    def get_file(name):
        try:
            s3 = boto3.client('s3')
            resource = boto3.resource('s3')
            return resource.Bucket('edushare-filestorage').download_file(name, '/tmp/'+name)
        except botocore.exceptions.ClientError as e:
            if e.response['Error']['Code'] == "404":
                return json.loads(json.dumps({'error':'The object does not exist.'}))
            else:
                print(str(e))
                return json.loads(json.dumps({"error":"Could Not Get File"}))

    def get_post(request, process): #Obsolete
        try:
            s3_resource=boto3.resource('s3')
            my_bucket=s3_resource.Bucket('edushare-filestorage')
            summaries = my_bucket.objects.all()
            return render(request,'upload.html',{"process": process})
        except TypeError:
            return json.loads(json.dumps({'error':'Encountered a Problem...'}))
    
    def post_file(self,_file,title,authors,publishers,datePublished,tags):
        try:
            mycursor = connection.cursor()

            #check for duplicate document_file
            if self.file_exists(_file.name):
                return json.loads(json.dumps({"error":"file \'"+_file.name+"\' already exists"}))

            if self.name_exists(title):
                return json.loads(json.dumps({"error":"file name \'"+title+"\' already exists"}))

            query = "INSERT INTO documents (document_title, document_file, document_authors, document_publisher, document_date, document_tags, document_reviews, document_rating) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)"
            mycursor.execute(query, [title, _file.name, authors, publishers,datePublished,tags,0,0.0])
            connection.commit()
            s3_resource=boto3.resource('s3')
            my_bucket=s3_resource.Bucket('edushare-filestorage')
            my_bucket.Object(_file.name).put(Body=_file)
            self.title = title
            self.file_name = _file.name
            self.authors = authors
            self.publishers = publishers
            self.datePublished = datePublished
            self.tags = tags
            return True
        except (botocore.exceptions.ClientError, MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
            connection.rollback()
            logging.error(e)
            print(str(e))
            return False
    
    def probationList():
        try:
            string = "TO BE REVIEWED"
            cursor = connection.cursor()
            query = "SELECT * FROM documents WHERE document_reviews=0" #% string
            cursor.execute(query)
            lst = cursor.fetchall()
            print(lst)
            return lst
        except (MySQLdb.Error, MySQLdb.Warning, KeyError,utils.IntegrityError,IndexError) as e:
            connection.rollback()
            print(str(e))
            return json.loads(json.dumps({"error":"Could Not Fetch Files To Be Reviewed"}))

    def review(self,file_name,rating,reviewer,comment):
        
        print(rating)
        cursor = connection.cursor()
        try:

            if not self.file_exists(file_name):
                return json.loads(json.dumps({"error":"file \'"+file_name+"\' does not exist"}))

            ### Add Check for review priviliges

            if self.reviewer_file(reviewer,file_name):
                return json.loads(json.dumps({"error":"user \'"+reviewer+"\' already reviewed file \'"+file_name}))

            query = "SELECT COUNT(document_file) AS NumberOfReviews FROM reviews WHERE document_file=%s"
            cursor.execute(query,[file_name])
            reviews = helpers.dictfetchall(cursor)[0]['NumberOfReviews'] ###cursor.fetchone()
            print("reviews = "+str(reviews))
            query = "SELECT document_id FROM documents WHERE document_file=%s"
            cursor.execute(query,[file_name])
            # print(helpers.dictfetchall(cursor)[0]['document_id'])
            id_ = helpers.dictfetchall(cursor)[0]['document_id']
            if reviews < 3:
                query = "INSERT INTO reviews(document_id, document_file, review_author, review_rating, review_description) VALUES (%s, %s, %s, %s, %s)"
                cursor.execute(query, [id_,file_name,reviewer,rating,comment])
                ###alter reviews variable
                query = "SELECT COUNT(document_file) AS NumberOfReviews FROM reviews WHERE document_file=%s"
                cursor.execute(query,[file_name])
                reviews = helpers.dictfetchall(cursor)[0]['NumberOfReviews'] ###cursor.fetchone()
            if reviews == 3:
                query = "SELECT review_rating FROM reviews WHERE document_id=%s"
                cursor.execute(query,[id_])
                result = helpers.dictfetchall(cursor) ### cursor.fetchall()
                result = [entry['review_rating'] for entry in result]
                print("result = "+str(result))
                result = sum(result)/3
                print("result = "+str(result))
                query = "UPDATE documents SET document_reviews=%s, document_rating=%s WHERE document_id=%s"
                cursor.execute(query, [1,result,id_])
                ###maybe wanna adjust the table, or nah
        except (MySQLdb.Error, MySQLdb.Warning, IndexError) as e:
            connection.rollback()
            logging.error(e)
            print(str(e))
            return json.loads(json.dumps({"error":"Could Not Complete Review Proecess"}))
        connection.commit()        
        return json.loads(json.dumps({"success":"Added Review of \'"+file_name+"\' by \'"+reviewer}))

    def file_exists(file_name):
        try:
            mycursor = connection.cursor()
            query = "SELECT document_file FROM documents WHERE document_file=%s"
            mycursor.execute(query,[file_name])
            file_ = mycursor.fetchone()
            if file_ == None:
                return False
            else:
                return True
        except (MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
            print(str(e))
            return json.loads(json.dumps({"error":"Could Not Access File (F)"}))
    
    def reviewer_file(reviewer,file_name):
        try:
            mycursor = connection.cursor()
            query = "SELECT * FROM reviews WHERE review_author=%s AND document_file=%s"
            mycursor.execute(query,[reviewer,file_name])
            file_ = mycursor.fetchone()
            if file_ == None:
                return False
            else:
                return True
        except (MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
            print(str(e))
            return json.loads(json.dumps({"error":"Could Not Access File (F)"}))

    def name_exists(file_name):
        try:
            mycursor = connection.cursor()
            query = "SELECT document_title FROM documents WHERE document_title=%s"
            mycursor.execute(query,[file_name])
            file_ = mycursor.fetchone()
            if file_ == None:
                return False
            else:
                return True
        except (MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
            print(str(e))
            return json.loads(json.dumps({"error":"Could Not Access File (F2)"}))