import io
import json

import numpy as np
import skimage
from PIL import Image
from io import BytesIO
from sklearn.cluster import KMeans
# import torchvision.transforms as transforms
# from PIL import Image

# triton_python_backend_utils is available in every Triton Python model. You
# need to use this module to create inference requests and responses. It also
# contains some utility functions for extracting information from model_config
# and converting Triton input/output types to numpy types.
import triton_python_backend_utils as pb_utils


def normalize_img(image: np.ndarray, mean: tuple=(0.485, 0.456, 0.406), std: tuple=(0.229, 0.224, 0.225)):
    """https://discuss.pytorch.org/t/understanding-transform-normalize/21730/2"""
    for i, (m, s) in enumerate(zip(mean, std)):
        image[i] = (image[i] - m) / s
    return image



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

        # Get OUTPUTS configuration
        output0_config = pb_utils.get_output_config_by_name(model_config, "IMAGE_FOR_COLOR_TAGGING")
        output1_config = pb_utils.get_output_config_by_name(model_config, "IMAGE_FOR_CONTENT_TAGGING")

        # Convert Triton types to numpy types
        self.outputs_dtype = {
            "IMAGE_FOR_COLOR_TAGGING": pb_utils.triton_string_to_numpy(
                output0_config["data_type"]
            ),
            "IMAGE_FOR_CONTENT_TAGGING": pb_utils.triton_string_to_numpy(
            output1_config["data_type"]
            )
        }

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

        # output0_dtype = self.output0_dtype

        responses = []

        # Every Python backend must iterate over everyone of the requests
        # and create a pb_utils.InferenceResponse for each of them.
        for request in requests:
            # Get INPUT0
            
            image = pb_utils.get_input_tensor_by_name(request, "IMAGE").as_numpy()
            image = np.array(Image.open(BytesIO(image.tobytes())), dtype=np.uint8)           

            # Preprocess for color tagging 
            image_color_tagging = skimage.transform.resize(image, (200,200)) * 255
            image_color_tagging = image_color_tagging.reshape((-1,3)).astype(np.uint8)
            image_color_tagging = np.expand_dims(image_color_tagging, 0)

            # Preprocess for image content tagging
            image_content_tagging = skimage.transform.resize(image, (224, 224))
            image_content_tagging = normalize_img(image_content_tagging)
            image_content_tagging = image_content_tagging.transpose((2,0,1))
            image_content_tagging = np.expand_dims(image_content_tagging, 0)

            # Transform to Triton Tensor
            out_tensor_0 = pb_utils.Tensor(
                "IMAGE_FOR_COLOR_TAGGING", image_color_tagging.astype(self.outputs_dtype["IMAGE_FOR_COLOR_TAGGING"])
            )
            out_tensor_1 = pb_utils.Tensor(
                "IMAGE_FOR_CONTENT_TAGGING", image_content_tagging.astype(self.outputs_dtype["IMAGE_FOR_CONTENT_TAGGING"])
            )
            # Create Inference Response
            inference_response = pb_utils.InferenceResponse(
                output_tensors=[out_tensor_0, out_tensor_1]
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