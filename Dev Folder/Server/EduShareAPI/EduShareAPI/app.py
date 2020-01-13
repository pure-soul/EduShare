"""
This script runs the application using a development server.
It contains the definition of routes and views for the application.
"""

from flask import Flask
import html
app = Flask(__name__)

# Make the WSGI interface available at the top level so wfastcgi can get it.
wsgi_app = app.wsgi_app


@app.route('/')
def hello():
    """Renders a sample page."""
    return "Hello World!"

@app.route('/search')
def search():
    """Renders a search page (as templated from https://codepen.io/adobewordpress/pen/gbewLV)"""
    return app.send_static_file('Forms/SearchPage.html')

if __name__ == '__main__':
    import os
    HOST = os.environ.get('SERVER_HOST', 'localhost')
    try:
        PORT = int(os.environ.get('SERVER_PORT', '5555'))
    except ValueError:
        PORT = 5555
    app.run(HOST, PORT)
