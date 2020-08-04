from django.contrib import admin
from django.urls import include, path

urlpatterns = [
    path("", include('home.urls')),
    path('polls/', include('polls.urls')),
    path('v3.0/search/', include('search.urls')),
    path('v3.0/users/', include('users.urls')),
    path('v3.0/chat/', include('chat.urls')),
    path('v3.0/admin/', admin.site.urls),
]