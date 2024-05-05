import json

import numpy as np
from torch.utils.dlpack import to_dlpack, from_dlpack
import triton_python_backend_utils as pb_utils


def to_numpy(pb_utils_tensor):
    tensor = from_dlpack(pb_utils_tensor[0].to_dlpack())
    return tensor.detach().cpu().numpy() if tensor.requires_grad else tensor.cpu().numpy()

class TritonPythonModel:
    def initialize(self, args):
        self.model_config = json.loads(args["model_config"])

    def execute(
        self, requests):

        responses = []

        for request in requests:
            # preprocess
            image =  pb_utils.get_input_tensor_by_name(request, "image").as_numpy()
            img_color_tagging, img_content_tagging = self.predict(
                model_name="preprocess",
                inputs=[pb_utils.Tensor("IMAGE", image)],
                output_names=["IMAGE_FOR_COLOR_TAGGING", "IMAGE_FOR_CONTENT_TAGGING"],
            )
            
            # color tagging
            img_color_tagging = img_color_tagging.as_numpy()
            (color_tags, ) = self.predict(
                model_name="color_tagging",
                inputs=[pb_utils.Tensor("IMAGE", img_color_tagging)],
                output_names=["COLORS"],
            )

            # object detection
            out_logits = self.predict(
                model_name="object_detection",
                inputs=[pb_utils.Tensor('im_content_tagging', img_content_tagging.as_numpy())],
                output_names=["out_logits"]
            )
            tags = self.predict(
                model_name="postprocess",
                inputs=[
                    color_tags,
                    pb_utils.Tensor("DETECTION_LOGITS", to_numpy(out_logits))
                ],
                output_names=["TAGS"]
            )

            # Prepare response
            tags = tags[0].as_numpy()
            final_tags = [*tags]

            output_tensors = [
                pb_utils.Tensor("tags", np.array(final_tags, dtype=np.object_)),
            ]
            inference_response = pb_utils.InferenceResponse(
                output_tensors=output_tensors
            )
            responses.append(inference_response)

        return responses

    @staticmethod
    def predict(
        model_name: str, inputs, output_names):
        infer_request = pb_utils.InferenceRequest(
            model_name=model_name,
            inputs=inputs,
            requested_output_names=output_names,
        )
        infer_response = infer_request.exec()

        if infer_response.has_error():
            raise pb_utils.TritonModelException(infer_response.error().message())

        return infer_response.output_tensors()