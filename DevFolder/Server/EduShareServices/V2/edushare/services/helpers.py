import requests
from . import keys

### 
# Helper Functions

def build_api_search_url(search):
    params = {
                'key': keys.googleapi.get('key'),
                'cx': keys.googleapi.get('engine_id'),
                'q': search
            }
    return requests.get(keys.googleapi.get('url'), params)

def get_next_ten(query_data):
    if query_data['startIndex'] > int(query_data['totalResults']) - 10:
        return {'error': 'No More Results', 'items': '-'}
    else:
        startIndex =  query_data['startIndex'] + 10
        params = {
                'key': keys.googleapi.get('key'),
                'cx': keys.googleapi.get('engine_id'),
                'q': query_data['searchTerms'],
                'num': str(query_data['count']),
                'start':str(startIndex),
                'safe':query_data['safe']
            }
        search_results = requests.get(keys.googleapi.get('url'), params)     
        return search_results.json()

def get_previous_ten(query_data):
    if query_data['startIndex'] == 1:
        return {'error': 'Invalid Request', 'items': '-'}
    else :
        startIndex =  query_data['startIndex'] - 10
        params = {
                'key': keys.googleapi.get('key'),
                'cx': keys.googleapi.get('engine_id'),
                'q': query_data['searchTerms'],
                'num': str(query_data['count']),
                'start':str(startIndex),
                'safe':query_data['safe']
            }
        search_results = requests.get(keys.googleapi.get('url'), params)     
        return search_results.json()

def dictfetchall(cursor):
        "Return all rows from a cursor as a dict"
        columns = [col[0] for col in cursor.description]
        return [
            dict(zip(columns, row))
            for row in cursor.fetchall()
        ]

def process_error(error):
    try:
        if type(error) == tuple:
            return {'error':'an error occured','code':error[0],'details':error[1]}
        if type(error) == str:
            error = tuple(error)
            print(error)
            return {'error':'an error occured','code':error[0],'details':error[1]}
    except (TypeError,AttributeError):
        return {'error':error,'code':'unknown','details':'unknown'}
