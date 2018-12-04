# CIFAR - 10

import pickle
import numpy as np
from keras.datasets import cifar10
from keras.utils import np_utils
from matplotlib import pyplot as plt
import pandas as pd
import requests
from tqdm import tqdm

def perturb_image(xs, img):
    # If this function is passed just one perturbation vector,
    # pack it in a list to keep the computation the same
    if xs.ndim < 2:
        xs = np.array([xs])
    
    # Copy the image n == len(xs) times so that we can 
    # create n new perturbed images
    tile = [len(xs)] + [1]*(xs.ndim+1)
    imgs = np.tile(img, tile)
    
    # Make sure to floor the members of xs as int types
    xs = xs.astype(int)
    
    for x,img in zip(xs, imgs):
        # Split x into an array of 5-tuples (perturbation pixels)
        # i.e., [[x,y,r,g,b], ...]
        pixels = np.split(x, len(x) // 5)
        for pixel in pixels:
            # At each pixel's x,y position, assign its rgb value
            x_pos, y_pos, *rgb = pixel
            img[x_pos, y_pos] = rgb
    
    return imgs

def plot_image(image, label_true=None, class_names=None, label_pred=None):
    if image.ndim == 4 and image.shape[0] == 1:
        image = image[0]

    plt.grid()
    plt.imshow(image.astype(np.uint8))

    # Show true and predicted classes
    if label_true is not None and class_names is not None:
        labels_true_name = class_names[label_true]
        if label_pred is None:
            xlabel = "True: "+labels_true_name
        else:
            # Name of the predicted class
            labels_pred_name = class_names[label_pred]

            xlabel = "True: "+labels_true_name+"\nPredicted: "+ labels_pred_name

        # Show the class on the x-axis
        plt.xlabel(xlabel)

    plt.xticks([]) # Remove ticks from the plot
    plt.yticks([])
    plt.show() # Show the plot

def evaluate_models(models, x_test, y_test):
    correct_imgs = []
    
    for model in models:
        print('Evaluating', model.name)
        
        predictions = model.predict(x_test)
        
        correct = [[model.name,i,label,np.max(pred),pred]
                for i,(label,pred)
                in enumerate(zip(y_test[:,0],predictions))
                if label == np.argmax(pred)]
        
        correct_imgs += correct
        
    return correct_imgs

def wrongimg(models, x_test, y_test):
	wrong_imgs = []
	for model in models:
		print('Evaluating', model.name)

		predictions = model.predict(x_test)

		wrong = [[model.name,i,label,np.max(pred),pred]
				for i,(label,pred)
				in enumerate(zip(y_test[:,0],predictions))
				if label != np.argmax(pred)]

		wrong_imgs += wrong
        
	return wrong_imgs

