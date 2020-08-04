from django.shortcuts import render, redirect
from django.http import JsonResponse, FileResponse, HttpResponseRedirect
from django.core.files.storage import default_storage
from django.urls import reverse
import requests
import json
from .models import Account, File
from . import helpers
from . import scrypt
import boto3

def task_manager(request,username,password,email,role,review,name,file_name,file_authors,file_publishers,file_date,file_tags,key,method,action,process,file_rating,file_reviewer,file_comment):
    try:
        if scrypt.user_valid(request.GET.get('key')):
            if request.GET.get('action') == 'login':
                return login_manager(request,request.GET.get('username'),request.GET.get('password'),request.GET.get('key'),request.GET.get('method'))
            if request.GET.get('action') == 'register':
                return register_manager(request,request.GET.get('username'),request.GET.get('password'),request.GET.get('email'),request.GET.get('role'),request.GET.get('review'),request.GET.get('name'),request.GET.get('key'),request.GET.get('method'))
            if request.GET.get('action') == 'download':
                if request.GET.get('method') == 'JSON':
                    return jsonget_file(request)
                return get_file(request.GET.get('file_name'))
            if request.GET.get('action') == 'upload':
                # if request.GET.get('process') == None:
                #     return post_page(request,'Choose A File, Enter Details then Press Upload')
                # return post_page(request,request.GET.get('process'))
                return post_file(request)
            if request.GET.get('action') == 'toBeReviewed':
                return JsonResponse({'reviewList': File.probationList()})
            if request.GET.get('action') == 'review':
                return review_file(request)
        else:
            return JsonResponse({'error':'Invalid Key Submitted'})
        return JsonResponse({'error':'Missing/Erroneous Parameter Values (\'action\' required with other supplementary values according to action)'})
    except (IndexError,AttributeError,ValueError) as e:
        return JsonResponse({'error':'Missing/Erroneous Parameter Values','message':str(e)})

def login_manager(request,username,password,key,method):
    try:
        if scrypt.user_valid(request.GET.get('key')):
            if request.GET.get('method') == 'JSON':
                return jsonlogin(request)
            # print(scrypt.encrypt_with_AES(request.GET.get('password'),request.GET.get('key'),scrypt.salt))
            return login(request,request.GET.get('username'),request.GET.get('password'))
        return JsonResponse({'error':'Must submit \'key\' paramater with valid key'})
    except (IndexError):
        return JsonResponse({'error':'Missing Parameter Values'})

def register_manager(request,username,password,email,role,review,name,key,method):
    try:
        if scrypt.user_valid(request.GET.get('key')):
            if request.GET.get('method') == 'JSON':
                return jsonregister(request)
            return register(request.GET.get('username'),request.GET.get('password'),request.GET.get('email'),request.GET.get('role'),request.GET.get('review'),request.GET.get('name'))
        return JsonResponse({'error':'Must submit \'key\' paramater with valid key'})
    except IndexError:
        return JsonResponse({'error':'Missing Parameter Values'})

def login(username,password):
    try:
        user = Account
        return JsonResponse(user.login_user({'username':username,'password':password}),safe=False)
    except TypeError:
        return JsonResponse({'error':'Could Not Process Request'})

def jsonlogin(request):
    if request.method == 'POST':
        try:
            user = Account
            return JsonResponse(user.login_user(json.loads(str(request.body,encoding='utf-8'))),safe=False)
        except KeyError:
            return JsonResponse({'error':'Invalid Submission'})
    return JsonResponse({'error':'Expected JSON Submission'})

def register(username, password, email, role, review, name):
    try:
        user = Account
        return JsonResponse(Account.register_user(user,{'username':username,'password':password,'email':email,'role':role,'review':review,'name':name}),safe=False)
    except TypeError:
        return JsonResponse({'error':'Could Not Process Request'})

def jsonregister(request):
    if request.method == 'POST':
        try:
            user = Account
            return JsonResponse(Account.register_user(user,json.loads(str(request.body,encoding='utf-8'))),safe=False)
        except KeyError:
            return JsonResponse({'error':'Could Not Process Request'})
    return JsonResponse({'error':'Expected JSON Submission'})

def get_file(file_name):
    try:
        _file = File
        _file.get_file(file_name)
        return FileResponse(open('/tmp/'+file_name, 'rb'))
    except (TypeError, AttributeError):
        JsonResponse({'error':'File Download Error'})

def jsonget_file(request):
    try:
        _file = File
        _file.get_file(json.loads(str(request.body,encoding='utf-8'))['file_name'])
        return FileResponse(open('/tmp/'+file_name, 'rb'))
    except TypeError:
        JsonResponse({'error':'File Download Error'})

def post_file(request):
    file_ = request.FILES['file']
    _file_ = File
    if File.post_file(_file_,file_,request.GET.get('file_name'),request.GET.get('file_authors'),request.GET.get('file_publishers'),request.GET.get('file_date'),request.GET.get('file_tags')):
        return JsonResponse({"Success":"Added \'" + file_.name + "\'"})
    return JsonResponse({"error":"Could Not sign request"})
    
def upload(request):
    if request.method == 'POST':
        form = UploadForm(request.POST)
        if form.is_valid():
            title = form.cleaned_data['title']
            authors = form.cleaned_data['authors']
            publishers = form.cleaned_data['publishers']
            datePublished = form.cleaned_data['datePublished']
            tags = form.cleaned_data['tags']
            print(authors,publishers,datePublished, end=" ")
            print([val for val in request.FILES])
            file_ = request.FILES['file']
            if File.post_file(file_,title,authors,publishers,datePublished,tags):
                return HttpResponseRedirect('https://edushare-services.herokuapp.com/v2.1/users/tasks?key=neVEraSkeDaNIgGaFOsh!TThATiSSAfetOsAy!&action=upload&process=Uploaded \'' + file_.name + '\'')
            else:
                return JsonResponse({"Error":"File Upload Error"})
        else:
            form = UploadForm()
            return HttpResponseRedirect('https://edushare-services.herokuapp.com/v2.1/users/tasks?key=neVEraSkeDaNIgGaFOsh!TThATiSSAfetOsAy!&action=upload&process=Please Fill In All Fields (Enter None if applicable)')

def review_file(request):
    try:
        _file = File
        return JsonResponse(File.review(_file,request.GET.get('file_name'),float(request.GET.get('file_rating')),request.GET.get('file_reviewer'),request.GET.get('file_comment')),content_type=json)
    except (TypeError) as e:
        print(str(e))
        return JsonResponse({"error":"Could Not Complete Review"})