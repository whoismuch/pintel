
import flask
from flask import Flask, request

import numpy as np
import tritonclient.grpc as tritongrpcclient

import logging
from dataclasses import dataclass
from tags import choose_tags
import io
# app = Flask('PinTel')

@dataclass
class InferenceConfig():
    model_name='image_tagging'
    url='ml.dtp:8101'
    verbose=False
    flask_host='192.168.0.17'
    flask_port=8000
    flask_debug=True


class PinTelInference(Flask):
    def __init__(self) -> None:
        super().__init__('PinTel')

        self.logger = logging.getLogger('PinTelInference')
        self.triton_config = InferenceConfig()
        self.connected = False
        self.add_url_rule('/img', view_func=self.request, methods=['POST'])
    
    def start(self):
        self.__connect_triton()
        self.run(
            host=self.triton_config.flask_host,
            port=self.triton_config.flask_port,
            debug=self.triton_config.flask_debug
        )
        
    def __connect_triton(self):
        try:
            self.triton_client = tritongrpcclient.InferenceServerClient(
            url=self.triton_config.url, verbose=self.triton_config.verbose
                )
            self.connected=True
            self.__health_check()
        except Exception as e:
            self.logger.fatal("Context creation failed: " + str(e))
            self.connected=False
    
    def __health_check(self):
        if not self.triton_client.is_server_live():
            self.logger.fatal("FAILED : is_server_live")

        if not self.triton_client.is_server_ready():
            self.logger.fatal("FAILED : is_server_ready")

        if not self.triton_client.is_model_ready(self.triton_config.model_name):
            self.logger.fatal("FAILED : is_model_ready for model:{0}".format(self.config.model_name))
    
    def infer(self, data: io.BytesIO):
        if self.connected:
            img_arr = np.frombuffer(data.getbuffer(), dtype=np.uint8)
            img_arr = np.expand_dims(img_arr.reshape(-1), 0)
        
            # Готовим входы и выходы

            inputs = tritongrpcclient.InferInput('image', img_arr.shape, "UINT8")
            outputs = tritongrpcclient.InferRequestedOutput('tags')

            inputs.set_data_from_numpy(img_arr)
            results = self.triton_client.infer(
                model_name=self.triton_config.model_name, inputs=[inputs], outputs=[outputs]
            )
            tags = [tag.decode("utf-8") for tag in results.as_numpy("tags")]
            tags = create_json_file(tags)
            return tags
        else:
            return choose_tags()

    
    def __choose_random_tags(self):
        tags = ['white', 'dog']
        return create_json_file(tags)
    
    # @self.route('/img', methods=['POST'])
    def request(self):
        # Получаем файл из мультипарт запроса
        uploaded_file = request.files['file']
        # Отправляем на инференс
        tags = self.infer(uploaded_file)
        return tags


def create_json_file(tags_str: list[str]):
    """
    :param tags_str: список тэгов после инференса
    :return: тэги в json формате
    """
    data = {'tags': [tags_str]}
    return flask.json.dumps(data)



if __name__ == '__main__':
    inferenceClient = PinTelInference()
    inferenceClient.start()
    # app.run(host='192.168.0.17',port=8000, debug=True)