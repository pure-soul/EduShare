from django.contrib import admin

# Register your models here.
from .models import User###, File

admin.site.register(User)
# admin.site.register(File)