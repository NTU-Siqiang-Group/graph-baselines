#!/bin/bash
pip install numpy

conda create -y -n py27 python=2.7
source "$(conda info --base)/etc/profile.d/conda.sh"
conda activate py27
python --version
pip install -r requirements.txt
pip3 install numpy