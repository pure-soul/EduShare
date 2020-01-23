FROM python
WORKDIR /app
ADD . /app
RUN pip install -r requirements.txt
EXPOSE 5000
ENV NAME edusharechat
CMD ["python", "chatapp.py"]