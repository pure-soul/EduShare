from django.urls import path, re_path
from django.views.decorators.csrf import csrf_exempt

from . import views

app_name = 'search'
urlpatterns = [
    # re_path(r'^googletemplate(?P<key>)$', views.googlesearch, name='googlesearch'),
    # re_path(r'^with(?P<term>)(?P<key>)(?P<source>)(?P<method>)(?P<action>)$', csrf_exempt(views.search_manager), name='search_manager'),
]