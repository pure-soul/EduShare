from django.contrib import admin

# Register your models here.
from .models import Account, File

admin.site.register(Account)
admin.site.register(File)