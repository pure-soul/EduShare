from django.urls import path, re_path
from django.views.decorators.csrf import csrf_exempt

from . import views

app_name = 'services'
urlpatterns = [
    re_path(r'^tasks(?P<username>)(?P<password>)(?P<email>)(?P<role>)(?P<review>)(?P<name>)(?P<file_name>)(?P<file_authors>)(?P<file_publishers>)(?P<file_date>)(?P<file_tags>)(?P<key>)(?P<method>)(?P<action>)(?P<process>)(?P<file_rating>)(?P<file_reviewer>)(?P<file_comment>)$', csrf_exempt(views.task_manager), name='task_manager'),
]