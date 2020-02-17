docker build -t edusharemain .
docker login
docker tag edusharemain dmss/edushare:main_version2
docker push dmss/edushare:main_version2
