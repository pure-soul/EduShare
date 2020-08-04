from django.urls import path, re_path
from django.views.decorators.csrf import csrf_exempt

from . import views

app_name = 'users'
urlpatterns = [
    # path('upload/', csrf_exempt(views.upload), name='upload'),
    # path('submit_form/', csrf_exempt(views.submit_form), name='submit_form'),
    re_path(r'^tasks(?P<username>)(?P<password>)(?P<email>)(?P<role>)(?P<can_review>)(?P<name>)(?P<file_name>)(?P<key>)(?P<method>)(?P<action>)(?P<process>)$', csrf_exempt(views.task_manager), name='task_manager'),
    re_path(r'^sign-s3(?P<file_name>)(?P<file_type>)$', csrf_exempt(views.sign_s3), name='sign_s3'),
]