FROM python
WORKDIR /app
ADD . /app
RUN pip install -r requirements.txt
EXPOSE 80
ENV NAME edusharemain
CMD ["python", "app.py"]