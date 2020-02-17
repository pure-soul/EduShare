docker build -t edusharedb .
docker login
docker tag edusharedb dmss/edushare:db_version2
docker push dmss/edushare:db_version2
