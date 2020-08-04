# from django.shortcuts import render
# from django.http import JsonResponse
# import requests
# import json
# from . import helpers
# from users import scrypt
# from .models import Search

# # Create your views here.

# def googlesearch(request,key):
#     if scrypt.user_valid(request.GET.get('key')):
#         return render(request, 'googlesearch.html')
#     return JsonResponse({'error':'Must submit \'key\' paramater with valid key'})

# def search_manager(request,term,key,source,method,action):
#     try:
#         if scrypt.user_valid(request.GET.get('key')):
#             if request.GET.get('action') == 'getNEXT':
#                 return next_ten(request)
#             if request.GET.get('action') == 'getPREVIOUS':
#                 return previous_ten(request)
#             if request.GET.get('method') == 'JSON':
#                 if request.GET.get('source') == 'API':
#                     return jsonapisearch(request)
#                 return jsonsearch(request)
#             if request.GET.get('source') == 'API':
#                 return apisearch(request.GET.get('term'))
#             return search(request.GET.get('term'))
#         return JsonResponse({'error':'Must submit \'key\' paramater with valid key'})
#     except (IndexError):
#         return JsonResponse({'error':'Missing Parameter Values'})

# def search(search):
#     try:
#         _search = Search
#         return JsonResponse(_search.local_search(search),safe=False)
#     except TypeError:
#         return JsonResponse({'error':'No Results Matching Term'})

# def jsonsearch(request):
#     if request.method == 'POST':
#         try:
#             search = Search
#             return JsonResponse(search.local_search(json.loads(str(request.body,encoding='utf-8'))['search']),safe=False)
#         except KeyError:
#             return JsonResponse({'error':'Invalid Submission'})
#     return JsonResponse({'error':'Expected JSON Submission'})

# def apisearch(search):
#     try:
#         _search = Search
#         return JsonResponse(_search.api_search(search),safe=False)
#     except TypeError:
#         return JsonResponse({'error':'Could Not Process Search for "' + search + '"'})

# def jsonapisearch(request):
#     if request.method == 'POST':
#         try:
#             search = json.loads(str(request.body,encoding='utf-8'))['search']
#             results = helpers.build_api_search_url(search)
#             _results = results.json()
#             return JsonResponse({'items':_results['items'],'queries':_results['queries']['request'][0]})
#         except TypeError:
#             return JsonResponse({'error':'Could Not Process Search for ' + search + '"'})
#     return JsonResponse({'error':'Expected JSON Submission'})

# def next_ten(request):
#     if request.method == 'POST':
#         try:
#             results = helpers.get_next_ten(json.loads(str(request.body,encoding='utf-8'))['queries'])
#             return JsonResponse({'items':results['items'],'queries':results['queries']['request'][0]})
#         except KeyError:
#             return JsonResponse(results)
#     return JsonResponse({'error':'Expected JSON Submission'})

# def previous_ten(request):
#     if request.method == 'POST':
#         try:
#             results = helpers.get_previous_ten(json.loads(str(request.body,encoding='utf-8'))['queries'])
#             return JsonResponse({'items':results['items'],'queries':results['queries']['request'][0]})
#         except KeyError:
#             return JsonResponse(results)
#     return JsonResponse({'error':'Expected JSON Submission'})