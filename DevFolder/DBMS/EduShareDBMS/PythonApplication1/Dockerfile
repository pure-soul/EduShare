FROM python:3.7
WORKDIR /app
ADD . /app
RUN pip install -r requirements.txt
# export AWS_SECRET_ACCES_KEY = P4AlihrmDQ/POTs8EBGe5NOTRB9OCQMTMikjpdmx
# export AWS_ACCESS_KEY = AKIAQULRDITENFHW26OP
EXPOSE 8000
ENV NAME edusharedb
CMD ["python", "PythonApplication1.py"]