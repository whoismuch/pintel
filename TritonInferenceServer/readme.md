# Инференс моделей
Flask приложение принимает POST запросы. Готовит данные и отправляет их на инференс на Titon Inference Server. Далее формирует ответ на полученный POST запрос в виде списка тэгов.
## Flask app
Подключение к Titon Inference Serever с помощью NVIDIA Triton Inference Server API.

```bash
# Установка зависимостей
mkdir pintel_venv
# Linux
source pintel_venv/bin/activate
# Windows
# pintel_venv\Scripts\activate.bat
pip install -r requirements.txt

# Запуск клиента
python app.py
```
## NVIDIA Triton Inference Server

Создание контейнера
```bash
sudo docker build -t tritonserver_pintel:1 ./triton_build/
```

Запуск контейнера
```bash
sudo docker run -d \
--gpus=all \
-p=8100:8000 -p=8101:8001 -p=8102:8002 \
--shm-size=1g \
-v=./model_repository:/models \
tritonserver_pintel:1 \
tritonserver --model-repository=/models
```

