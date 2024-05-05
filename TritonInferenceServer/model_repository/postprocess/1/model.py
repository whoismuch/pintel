import io
import json

import numpy as np
import torch

# triton_python_backend_utils is available in every Triton Python model. You
# need to use this module to create inference requests and responses. It also
# contains some utility functions for extracting information from model_config
# and converting Triton input/output types to numpy types.
import triton_python_backend_utils as pb_utils
from .coco_categ import COCOCategories

def post_process(out_logits, threshold=0.1):
    """
    Postprocessing of object detection logits
    :param out_logits: torch.Tensor((bs, 100, num_classes))
    :param threshold: threshold of probability of class
    :returns tag_list: list of tags
    """
    prob = torch.nn.functional.softmax(out_logits, -1)
    scores, labels = prob[..., :-1].max(-1)

    results = []
    
    for s, l in zip(scores, labels):
        score = s[s > threshold]
        label = l[s > threshold]
        results.append({"scores": score, "labels": label})
    tag_list = results[0]['labels'].tolist()
    if 'N/A' in tag_list: tag_list.remove('N/A')
    return list(set([tag_list]))

class TritonPythonModel:
    """Your Python model must use the same class name. Every Python model
    that is created must have "TritonPythonModel" as the class name.
    """

    def initialize(self, args):
        """`initialize` is called only once when the model is being loaded.
        Implementing `initialize` function is optional. This function allows
        the model to initialize any state associated with this model.

        Parameters
        ----------
        args : dict
          Both keys and values are strings. The dictionary keys and values are:
          * model_config: A JSON string containing the model configuration
          * model_instance_kind: A string containing model instance kind
          * model_instance_device_id: A string containing model instance device ID
          * model_repository: Model repository path
          * model_version: Model version
          * model_name: Model name
        """

        # You must parse model_config. JSON string is not parsed here
        self.model_config = model_config = json.loads(args["model_config"])
        self.threshold = float(self.model_config['parameters'].get('threshold').get('string_value'))




    def execute(self, requests):
        """`execute` MUST be implemented in every Python model. `execute`
        function receives a list of pb_utils.InferenceRequest as the only
        argument. This function is called when an inference request is made
        for this model. Depending on the batching configuration (e.g. Dynamic
        Batching) used, `requests` may contain multiple requests. Every
        Python model, must create one pb_utils.InferenceResponse for every
        pb_utils.InferenceRequest in `requests`. If there is an error, you can
        set the error argument when creating a pb_utils.InferenceResponse

        Parameters
        ----------
        requests : list
          A list of pb_utils.InferenceRequest

        Returns
        -------
        list
          A list of pb_utils.InferenceResponse. The length of this list must
          be the same as `requests`
        """

        responses = []

        # Every Python backend must iterate over everyone of the requests
        # and create a pb_utils.InferenceResponse for each of them.
        for request in requests:
            # Get detection results
            detection_logits = pb_utils.get_input_tensor_by_name(request, "DETECTION_LOGITS").as_numpy()

            # Postprocess object detection results 
            objects = post_process(torch.Tensor(detection_logits), self.threshold)
            content_tags = list(set([COCOCategories[obj] for obj in objects]))          

            # get color tags
            color_tags = pb_utils.get_input_tensor_by_name(request, "COLORS").as_numpy()[0]

            # Transform to Triton Tensor
            tags = [*color_tags, *content_tags]
            output_tensors = [
                pb_utils.Tensor("TAGS", np.array(tags, dtype=np.object_)),
            ]

            inference_response = pb_utils.InferenceResponse(
                output_tensors=output_tensors
            )
            responses.append(inference_response)

        # You should return a list of pb_utils.InferenceResponse. Length
        # of this list must match the length of `requests` list.
        return responses

    def finalize(self):
        """`finalize` is called only once when the model is being unloaded.
        Implementing `finalize` function is OPTIONAL. This function allows
        the model to perform any necessary clean ups before exit.
        """
        print("Cleaning up...")