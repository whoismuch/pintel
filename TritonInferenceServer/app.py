
import flask
from flask import Flask, request

import numpy as np
import cv2
import tritonclient.grpc as tritongrpcclient

import io
import logging
import requests
from dataclasses import dataclass

from tags import choose_color_tags, choose_content_tags


@dataclass
class InferenceConfig():
    content_tagging_model_name='content_tagging'
    color_tagging_model_name='color_tagging'
    url='ml.dtp:8101'
    verbose=False
    flask_host='192.168.1.86'
    flask_port=8600
    flask_debug=True

class PinTelInference(Flask):
    def __init__(self) -> None:
        super().__init__('PinTel')
        self.logger = logging.getLogger('PinTelInference')
        
        logFormatter = logging.Formatter("%(asctime)s [%(name)-15.15s] [%(levelname)-5.5s] %(message)s")
        fileHandler = logging.FileHandler("{0}/{1}.log".format('./', 'flask_log'))
        consoleHandler = logging.StreamHandler()
        
        fileHandler.setFormatter(logFormatter)
        consoleHandler.setFormatter(logFormatter)
        logging.basicConfig(
            level=logging.INFO,
            handlers=[fileHandler, consoleHandler]
        )

        self.triton_config = InferenceConfig()
        self.connected = False
        self.add_url_rule('/content_tags', view_func=self.get_content_tags_by_img_link, methods=['GET'])
        self.add_url_rule('/color_tags', view_func=self.get_color_tags_by_img_link, methods=['GET'])
    
    def start(self):
        self.__connect_triton()
        self.run(
            host=self.triton_config.flask_host,
            port=self.triton_config.flask_port,
            debug=self.triton_config.flask_debug
        )
        self.logger.info('Stopped inference')
        
    def __connect_triton(self):
        try:
            self.triton_client = tritongrpcclient.InferenceServerClient(
            url=self.triton_config.url, verbose=self.triton_config.verbose
                )
            self.connected=True
            self.__health_check()
            self.logger.info('Successfully connected')
        except Exception as e:
            self.logger.fatal("Context creation failed: " + str(e))
            self.connected=False
    
    def __health_check(self):
        if not self.triton_client.is_server_live():
            self.logger.fatal("FAILED : is_server_live")

        if not self.triton_client.is_server_ready():
            self.logger.fatal("FAILED : is_server_ready")

        if not self.triton_client.is_model_ready(self.triton_config.content_tagging_model_name):
            self.logger.fatal("FAILED : is_model_ready for model:{0}".format(self.triton_config.content_tagging_model_name))
        if not self.triton_client.is_model_ready(self.triton_config.color_tagging_model_name):
            self.logger.fatal("FAILED : is_model_ready for model:{0}".format(self.triton_config.color_tagging_model_name))
        
    def infer_content_tagging_model(self, data: io.BytesIO):
        if self.connected:
            img_arr = np.frombuffer(data.getbuffer(), dtype=np.uint8)
            img_arr = np.expand_dims(img_arr.reshape(-1), 0)
            self.logger.info('Requested image with shape {}'.format(img_arr.shape))
            
            # Готовим входы и выходы
            inputs = tritongrpcclient.InferInput('IMAGE', img_arr.shape, "UINT8")
            outputs = tritongrpcclient.InferRequestedOutput('CONTENT')
            inputs.set_data_from_numpy(img_arr)
            results = self.triton_client.infer(
                model_name=self.triton_config.content_tagging_model_name, inputs=[inputs], outputs=[outputs]
            )
            tags = [tag.decode("utf-8") for tag in results.as_numpy("CONTENT")]
            tags = create_json_file(tags)
            return tags
        else:
            return create_json_file(choose_content_tags())
    
    def infer_color_tagging_model(self, data: io.BytesIO):
        if self.connected:
            img_arr = np.frombuffer(data.getbuffer(), dtype=np.uint8)
            img_arr = np.expand_dims(img_arr.reshape(-1), 0)
            self.logger.info('Requested image with shape {}'.format(img_arr.shape))
            
            # Готовим входы и выходы
            inputs = tritongrpcclient.InferInput('IMAGE', img_arr.shape, "UINT8")
            outputs = tritongrpcclient.InferRequestedOutput('COLORS')
            inputs.set_data_from_numpy(img_arr)
            results = self.triton_client.infer(
                model_name=self.triton_config.color_tagging_model_name, inputs=[inputs], outputs=[outputs]
            )
            tags = [tag.decode("utf-8") for tag in results.as_numpy("COLORS")]
            tags = create_json_file(tags)
            return tags
        else:
            return create_json_file(choose_color_tags())

    
    def get_content_tags_by_img_link(self):
        link = request.args.get('link')
        image_bytes = self.get_bytes_by_link(link)
        tags = self.get_content_tags_by_bytes(image_bytes)
        
        self.logger.info('Processed tags: {}'.format(tags))
        return flask.Response(tags, mimetype='application/json')
    
    def get_content_tags_by_img_link(self):
        link = request.args.get('link')
        image_bytes = self.get_bytes_by_link(link)
        tags = self.get_content_tags_by_bytes(image_bytes)
        
        self.logger.info('Processed tags: {}'.format(tags))
        return flask.Response(tags, mimetype='application/json')
    
    def get_color_tags_by_img_link(self):
        link = request.args.get('link')
        image_bytes = self.get_bytes_by_link(link)
        tags = self.get_color_tags_by_bytes(image_bytes)
        
        self.logger.info('Processed tags: {}'.format(tags))
        return flask.Response(tags, mimetype='application/json')

    def get_bytes_by_link(self, link) -> bytes:
        image_bytes = requests.get(link).content
        return image_bytes

    def get_content_tags_by_bytes(self, image_bytes) -> flask.json:
        return self.infer_content_tagging_model(io.BytesIO(image_bytes))
    def get_color_tags_by_bytes(self, image_bytes) -> flask.json:
        return self.infer_color_tagging_model(io.BytesIO(image_bytes))

def create_json_file(tags_str: list[str]):
    """
    :param tags_str: список тэгов после инференса
    :return: тэги в json формате
    """
    data = {'tags': tags_str}
    return flask.json.dumps(data)



if __name__ == '__main__':
    inferenceClient = PinTelInference()
    inferenceClient.start()