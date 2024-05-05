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
--gpus=all \                                # подключение GPU
-p=8100:8000 -p=8101:8001 -p=8102:8002 \    # Проброс портов
--shm-size=1g \                             # Выделение памяти для работы Triton
-v=./model_repository:/models \             # Проброс томов
tritonserver_pintel:1                       # Название контейнера
tritonserver --model-repository=/models     # Командная строка с запуском сервера
```

