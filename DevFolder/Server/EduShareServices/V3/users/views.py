from django.shortcuts import render, redirect
from django.http import JsonResponse, FileResponse, HttpResponseRedirect
from django.core.files.storage import default_storage
from django.urls import reverse
import requests
import json
from .models import User, File
from search import helpers
from . import scrypt
from .forms import UploadForm

def task_manager(request,username,password,email,role,can_review,name,file_name,key,method,action,process):
    try:
        if scrypt.user_valid(request.GET.get('key')):
            if request.GET.get('action') == 'login':
                return login_manager(request)
            if request.GET.get('action') == 'register':
                return register_manager(request)
            if request.GET.get('action') == 'download':
                if request.GET.get('method') == 'JSON':
                    return jsonget_file(request)
                return get_file(request.GET.get('file_name'))
            if request.GET.get('action') == 'upload':
                return post_file(request)
        else:
            return JsonResponse({'error':'Must submit valid \'key\' paramater'})
        return JsonResponse({'error':'Missing/Erroneous Parameter Values','message':'(\'action\' required with other supplementary values according to action)'})
    except (IndexError,AttributeError,ValueError) as e:
        print(str(e))
        return JsonResponse({'error':'Task Could Not Be Completed'})

def login_manager(request):
    try:
        if request.GET.get('method') == 'JSON':
            return jsonlogin(request)
        # print(scrypt.encrypt_with_AES(request.GET.get('password'),request.GET.get('key'),scrypt.salt))
        return login(request.GET.get('email'),request.GET.get('password'))
        # return login(request.GET.get('username'),scrypt.decrypt_with_AES(request.GET.get('password'),request.GET.get('key'),scrypt.salt))
    except (IndexError):
        return JsonResponse({'error':'Missing Parameter Values'})

def register_manager(request):
    try:
        user = User
        if request.GET.get('method') == 'JSON':
            return jsonregister(user,request)
        return register(user,request.GET.get('username'),request.GET.get('password'),request.GET.get('email'),request.GET.get('role'),request.GET.get('can_review'),request.GET.get('name'))
    except IndexError:
        return JsonResponse({'error':'Missing Parameter Values'})

def login(email,password):
    try:
        return JsonResponse(User.login_user({'email':email,'password':password}),safe=False)
    except TypeError:
        return JsonResponse({'error':'Could Not Process Request'})

def jsonlogin(request):
    if request.method == 'POST':
        try:
            return JsonResponse(User.login_user(json.loads(str(request.body,encoding='utf-8'))),safe=False)
        except KeyError:
            return JsonResponse({'error':'Invalid Submission'})
    return JsonResponse({'error':'Expected JSON Submission'})

def register(user,username, password, email, role, review, name):
    try:
        return JsonResponse(User.register_user(user,{'username':username,'password':password,'email':email,'role':role,'can_review':review,'name':name}),safe=False)
    except TypeError:
        return JsonResponse({'error':'Could Not Process Request'})

def jsonregister(user,request):
    if request.method == 'POST':
        try:
            return JsonResponse(User.register_user(user,json.loads(str(request.body,encoding='utf-8'))),safe=False)
        except KeyError:
            return JsonResponse({'error':'Could Not Process Request'})
    return JsonResponse({'error':'Expected JSON Submission'})

def get_file(file_name):
    try:
        File.get_file(file_name)
        return FileResponse(open('download/tmp/'+file_name, 'rb'))
    except (TypeError, AttributeError, ValueError):
        return JsonResponse({'error':'File Download Error'})

def jsonget_file(request):
    try:
        File.get_file(json.loads(str(request.body,encoding='utf-8'))['file_name'])
        return FileResponse(open('download/tmp/'+file_name, 'rb'))
    except (TypeError, AttributeError):
        return JsonResponse({'error':'File Download Error'})

def post_page(request,process):
    return render(request,'aws-upload.html',{"process": process})
    # File.get_post(request,process)
    # return None
    
def post_file(request):
    if request.method == 'POST':
        try:
            token = User.get_user_token(request.GET.get('email'),request.GET.get('password'))
            print(token)
            _file = File
            print([val for val in request.FILES])
            file_ = request.FILES['file']
            if File.post_file(_file,file_,request.GET.get('file_name'),request.GET.get('file_authors'),request.GET.get('publishers'),request.GET.get('file_date'),request.GET.get('file_tags'),token):
                return JsonResponse({"Success":"File \'" + request.GET.get('file_name') + "\' Uploaded"})
        except (TypeError, AttributeError):
            return JsonResponse({"Error":"File Upload Error"})

def sign_s3(request,file_name,file_type):
    try:
        print('Getting Signed')
        return HttpResponse(File.sign_s3(request.GET.get('file_name'),request.GET.get('file_type')),content_type=json)
    except (TypeError):
        return JsonResponse({"error":"Could Not sign request"})

def submit_form(request):
    if request.method == 'POST':
        form = UploadForm(request.POST)
        print([val for val in request.FILES])
        if form.is_valid():
            title = form.cleaned_data['title']
            authors = form.cleaned_data['authors']
            publishers = form.cleaned_data['publishers']
            datePublished = form.cleaned_data['datePublished']
            tags = form.cleaned_data['tags']
            _file = request.FILES['file']
            print(authors,publishers,datePublished, end=" ")
            file_=File
            if File.added_file_details(file_,title,_file.name,authors,publishers,datePublished,tags):
                # return redirect('/v2.1/users/tasks?key=neVEraSkeDaNIgGaFOsh!T,ThATiSSAfetOsAy!&action=upload&process=Uploaded \'' + file_.name + '\'')
                return HttpResponseRedirect('/v2.1/users/tasks?key=neVEraSkeDaNIgGaFOsh!TThATiSSAfetOsAy!&action=upload&process=Uploaded \'' + _file.name + '\'')
            else:
                return JsonResponse({"Error":"File Upload Error"})
        else: ###Unnecessary
            form = UploadForm()
            return HttpResponseRedirect('/v2.1/users/tasks?key=neVEraSkeDaNIgGaFOsh!TThATiSSAfetOsAy!&action=upload&process=Please Fill In All Fields (Enter None if applicable)')