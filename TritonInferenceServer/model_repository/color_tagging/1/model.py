import io
import json

import numpy as np
from sklearn.cluster import KMeans

# triton_python_backend_utils is available in every Triton Python model. You
# need to use this module to create inference requests and responses. It also
# contains some utility functions for extracting information from model_config
# and converting Triton input/output types to numpy types.
import triton_python_backend_utils as pb_utils

base_colors = {
    'red': [255,0,0], 'blue': [0,0,255],
    'green': [0,255,0], 'yellow': [255,255,0],
    'orange': [255,165,0],
    'purple': [255,0,255], 'brown': [165,42,42],
    'black': [0,0,0], 'white': [255,255,255]
}

def find_closest_color(color):
    colors_array = np.array(list(base_colors.values()))
    distances = np.linalg.norm(colors_array - color, axis=1)
    closest_color_index = np.argmin(distances)
    closest_color_name = list(base_colors.keys())[closest_color_index]
    return closest_color_name


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
        self.color_clustering_model = KMeans(n_clusters = 3)

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
            # Prepare model
            color_clustering_model = KMeans(n_clusters = 3, n_init=10)
            
            # Get input
            img = pb_utils.get_input_tensor_by_name(request, "IMAGE").as_numpy()
            img = img.reshape((-1,3))

            color_clustering_model.fit(img)
            colors = np.asarray(color_clustering_model.cluster_centers_, dtype='uint8')
            color_names = [find_closest_color(color) for color in colors]
            # Create InferenceResponse

            output_tensors = [
                pb_utils.Tensor("COLORS", np.array([color_names], dtype=np.object_)),
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