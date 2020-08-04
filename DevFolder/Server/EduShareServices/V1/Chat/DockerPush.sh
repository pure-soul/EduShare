docker build -t edusharechat .   
docker login
docker tag edusharechat dmss/edushare:chat_version2
docker push dmss/edushare:chat_version2  