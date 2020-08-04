# from django.db import models, connection, utils
# import MySQLdb
# import json
# from search import helpers

# class Search(models.Model):

#     def local_search(search):
#         try:
#             mycursor = connection.cursor()
#             mycursor.execute("SELECT * FROM documents WHERE document_tags LIKE %s ", ("%" + search + "%",))
#             document_result = helpers.dictfetchall(mycursor)
#             if document_result != []:
#                 print(document_result)
#                 document_result = document_result[0]
#             else:
#                 document_result =  {}
#             mycursor = connection.cursor()
#             mycursor.execute("SELECT * FROM media WHERE media_tags LIKE %s ", ("%" + search + "%",))
#             media_result = helpers.dictfetchall(mycursor)
#             if media_result != []:
#                 print(media_result)
#                 media_result = media_result[0]
#             else:
#                 media_result =  {}
#             if media_result == {} and document_result == {}:
#                 return json.loads(json.dumps({'error':'No Results Matching \'' + search + '\''}))
#             return json.loads(json.dumps({'Documents':document_result, 'Media':media_result}))
#         except(MySQLdb.Error, MySQLdb.Warning, KeyError) as e:
#             return json.loads(json.dumps({'error':str(e),'type': type(e).__name__}))

#     def api_search(search):
#         results = helpers.build_api_search_url(search)
#         _results = results.json()
#         return json.loads(json.dumps({'items':_results['items'],'queries':_results['queries']['request'][0]}))

#     def not_null(cursor):
#         if cursor.fetchone() == None:
#             return False
#         else:
#             return True